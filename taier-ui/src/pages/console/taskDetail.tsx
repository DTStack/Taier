/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useRef, useState } from 'react';
import CopyToClipboard from 'react-copy-to-clipboard';
import { CloseCircleOutlined,DownOutlined, SyncOutlined } from '@ant-design/icons';
import { Utils } from '@dtinsight/dt-utils';
import { Button, Dropdown, Menu, message, Modal,Space, Tooltip } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import { history } from 'umi';

import type { IActionRef } from '@/components/sketch';
import Sketch from '@/components/sketch';
import type { TASK_STATUS } from '@/constant';
import { JOB_STAGE_ENUM } from '@/constant';
import { formatDateTime } from '@/utils';
import { TaskStatus } from '@/utils/enums';
import Api from '../../api';
import ViewDetail from '../../components/viewDetail';

const JOB_STAGE_OPTIONS = [
    {
        label: '已存储',
        value: JOB_STAGE_ENUM.Saved,
    },
    {
        label: '队列中',
        value: JOB_STAGE_ENUM.Queueing,
    },
    {
        label: '等待重试',
        value: JOB_STAGE_ENUM.WaitTry,
    },
    {
        label: '等待资源',
        value: JOB_STAGE_ENUM.WaitResource,
    },
    {
        label: '运行中',
        value: JOB_STAGE_ENUM.Running,
    },
];

interface IQueueTaskProps {
    computeType: 0;
    engineType: string;
    execStartTime: number;
    generateTime: number;
    gmtCreate: number;
    gmtModified: number;
    id: number;
    isFailover: number;
    jobId: string;
    jobInfo: any;
    jobName: string;
    jobPriority: number;
    jobResource: string;
    nodeAddress: string;
    stage: JOB_STAGE_ENUM;
    status: TASK_STATUS;
    tenantName: string;
    waitReason: string | null;
    waitTime: string;
}

interface IFormFieldProps {
    radioValue: number;
}

export default () => {
    const sketchRef = useRef<IActionRef>(null);
    const [taskDetailVisible, setVisible] = useState(false);
    const [details, setDetails] = useState<string>('');

    const handleRequestSearch = (
        values: IFormFieldProps,
        {
            current,
            pageSize,
        }: {
            current: number;
            pageSize: number;
        }
    ) => {
        const { node, jobResource } = history.location.query || {};
        return Api.getViewDetail({
            nodeAddress: node as string,
            pageSize,
            currentPage: current,
            stage: values.radioValue,
            jobResource: jobResource as string,
        }).then((res) => {
            if (res.code === 1) {
                return {
                    total: res.data.totalCount,
                    data: res.data.data,
                };
            }
        });
    };

    const requestKillJob = (jobIdList: string[]) => {
        const { node = '', jobResource } = history.location.query || {};
        return new Promise<void>((resolve) => {
            Api.killTasks({
                stage: sketchRef.current?.form.getFieldValue('radioValue'),
                jobIdList,
                jobResource: jobResource as string,
                nodeAddress: node as string,
            }).then((res) => {
                if (res.code === 1) {
                    resolve();
                }
            });
        });
    };

    const handleRefresh = () => {
        sketchRef.current?.submit();
    };

    // 杀死选中的任务
    const handleKillSelect = () => {
        const selected = (sketchRef.current?.selectedRowKeys || []) as string[];

        if (!selected || selected.length <= 0) {
            message.error('您没有选择任何任务！');
            return false;
        }

        Modal.confirm({
            title: '杀死选中任务',
            okText: '杀死选中任务',
            okButtonProps: {
                danger: true,
            },
            cancelText: '取消',
            width: '460px',
            icon: <CloseCircleOutlined />,
            content: <span style={{ color: '#ff5f5c' }}>本操作将杀死列表（非跨分页）中的选中任务</span>,
            async onOk() {
                return new Promise<void>((resolve) => {
                    requestKillJob(selected).then(() => {
                        message.success('操作成功');
                        sketchRef.current?.submit();
                        resolve();
                    });
                });
            },
        });
    };

    const handleKillAll = () => {
        Modal.confirm({
            title: '杀死全部任务',
            okText: '杀死全部任务',
            okButtonProps: {
                danger: true,
            },
            cancelText: '取消',
            width: '460px',
            icon: <CloseCircleOutlined />,
            content: (
                <span style={{ color: '#ff5f5c' }}>
                    本操作将杀死列表（跨分页）中的全部任务，不仅是当前页
                    <br />
                    杀死运行中的任务需要较长时间
                </span>
            ),
            async onOk() {
                return new Promise<void>((resolve) => {
                    requestKillJob([]).then(() => {
                        // 杀死全部任务为异步有延迟，需要延迟执行刷新数据操作
                        setTimeout(() => {
                            message.success('操作成功');
                            sketchRef.current?.submit();
                            resolve();
                        }, 1000);
                    });
                });
            },
        });
    };

    // 查看详情
    const viewDetails = (record: IQueueTaskProps) => {
        setDetails(JSON.stringify(record, null, 2));
        setVisible(true);
    };

    const showTaskParams = (record: IQueueTaskProps) => {
        setDetails(record?.jobInfo?.taskParams ?? '');
        setVisible(true);
    };

    // 置顶
    const stickTask = (record: IQueueTaskProps, msg: string) => {
        const { jobResource } = history.location.query || {};
        Api.stickJob({
            jobId: record.jobId,
            jobResource: jobResource as string,
        }).then((res) => {
            if (res.code === 1) {
                message.success(`${msg}成功`);
                sketchRef.current?.submit();
            }
        });
    };

    const killTask = (record: IQueueTaskProps) => {
        Modal.confirm({
            title: '杀任务',
            cancelText: '取消',
            icon: <CloseCircleOutlined />,
            content: '是否要杀死此任务?',
            async onOk() {
                return new Promise<void>((resolve) => {
                    requestKillJob([record.jobId]).then(() => {
                        message.success('操作成功');
                        sketchRef.current?.submit();
                        resolve();
                    });
                });
            },
        });
    };

    const handleCloseViewModal = () => {
        setVisible(false);
    };

    const columns: ColumnsType<IQueueTaskProps> = [
        {
            title: '任务名称',
            dataIndex: 'jobName',
            fixed: 'left',
            ellipsis: true,
            width: 150,
            render: (text) => <Tooltip title={text}>{text}</Tooltip>,
        },
        {
            title: '任务ID',
            dataIndex: 'jobId',
            width: 150,
            render(_, record) {
                return (
                    <CopyToClipboard text={record.jobId} onCopy={() => message.success('复制成功！')}>
                        <Tooltip title="点击复制">
                            <code className="cursor-pointer">{record.jobId}</code>
                        </Tooltip>
                    </CopyToClipboard>
                );
            },
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 100,
            render(text) {
                return <TaskStatus value={text} />;
            },
        },
        {
            title: '节点',
            width: 180,
            dataIndex: 'nodeAddress',
        },
        {
            title: '已等待',
            width: 180,
            dataIndex: 'waitTime',
        },
        {
            title: '等待原因',
            width: 300,
            dataIndex: 'waitReason',
            render(_, record) {
                return (
                    <Tooltip title={record.waitReason} placement="top">
                        {Utils.textOverflowExchange(record.waitReason ?? '-', 20)}
                    </Tooltip>
                );
            },
        },
        {
            title: '提交时间',
            dataIndex: 'generateTime',
            width: 180,
            render(text) {
                return formatDateTime(text);
            },
        },
        {
            title: '租户',
            width: 100,
            ellipsis: true,
            dataIndex: 'tenantName',
            render: (text) => <Tooltip title={text}>{text}</Tooltip>,
        },
        {
            title: '操作',
            dataIndex: 'deal',
            fixed: 'right',
            width: 250,
            render: (_, record) => {
                const isSaved = record.stage === JOB_STAGE_ENUM.Saved;
                const isQueueing = record.stage === JOB_STAGE_ENUM.Queueing;
                const insert = isSaved ? '插入队列头' : null;
                const stickTxt = isQueueing ? '置顶' : insert;
                return (
                    <Space split="|">
                        <a onClick={() => viewDetails(record)}>查看详情</a>
                        <a onClick={() => killTask(record)}>杀任务</a>
                        {stickTxt && <a onClick={() => stickTask(record, stickTxt)}>{stickTxt}</a>}
                        <a onClick={() => showTaskParams(record)}>任务参数</a>
                    </Space>
                );
            },
        },
    ];

    return (
        <>
            <Sketch<IQueueTaskProps, IFormFieldProps>
                actionRef={sketchRef}
                className="dt-queue-detail"
                header={[
                    {
                        name: 'radioGroup',
                        props: {
                            formItemProps: {
                                name: 'radioValue',
                                initialValue: Number(history.location.query!.jobStage),
                            },
                            slotProps: {
                                options: JOB_STAGE_OPTIONS,
                            },
                        },
                    },
                ]}
                extra={
                    <Tooltip title="刷新数据">
                        <Button className="dt-refresh">
                            <SyncOutlined onClick={() => handleRefresh()} />
                        </Button>
                    </Tooltip>
                }
                tableProps={{
                    rowKey: 'jobId',
                }}
                tableFooter={
                    <Dropdown.Button
                        key="kill"
                        type="primary"
                        onClick={handleKillSelect}
                        overlay={
                            <Menu onClick={() => handleKillAll()}>
                                <Menu.Item key="1">杀死全部任务</Menu.Item>
                            </Menu>
                        }
                        trigger={['click']}
                        icon={<DownOutlined />}
                    >
                        杀死选中任务
                    </Dropdown.Button>
                }
                request={handleRequestSearch}
                columns={columns}
            />
            <ViewDetail visible={taskDetailVisible} onCancel={handleCloseViewModal} resource={details} />
        </>
    );
};
