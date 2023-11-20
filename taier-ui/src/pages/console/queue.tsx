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

import { useLayoutEffect, useMemo, useRef, useState } from 'react';
import { CloseCircleOutlined,SyncOutlined } from '@ant-design/icons';
import { Button, message, Modal, Tooltip } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import { history } from 'umi';

import Api from '@/api';
import type { IActionRef } from '@/components/sketch';
import Sketch from '@/components/sketch';
import { DRAWER_MENU_ENUM, JOB_STAGE_ENUM } from '@/constant';

interface IQueueListProps {
    db: number;
    dbJobSize: number;
    dbWaitTime: string;
    jobResource: string;
    lacking: number;
    lackingJobSize: number;
    lackingWaitTime: string;
    priority: number;
    priorityJobSize: number;
    priorityWaitTime: string;
    restart: number;
    restartJobSize: number;
    restartWaitTime: string;
    submitted: number;
    submittedJobSize: number;
    submittedWaitTime: string;
}

interface IClusterProps {
    clusterId: number;
    clusterName: string;
    id: number;
}

interface IFormFieldProps {
    clusterId: string;
    clusterNode: string;
}

export default () => {
    const [clusterList, setClusterList] = useState<IClusterProps[]>([]);
    const [clusterNodeList, setNodeList] = useState<string[]>([]);
    const sketchRef = useRef<IActionRef>(null);

    const getNodeAddressSelect = () => {
        return Api.getNodeAddressSelect().then((res) => {
            if (res.code === 1) {
                setNodeList(res.data || []);
            }
        });
    };

    // 获取集群下拉数据
    const getClusterSelect = () => {
        return Api.getAllCluster().then((res) => {
            if (res.code === 1) {
                setClusterList(res.data);
                sketchRef.current?.form.setFieldsValue({
                    // 首次取第一项集群名称展示
                    clusterId: res.data[0].clusterName,
                });
            }
        });
    };

    const handleRequestSearch = ({ clusterId, clusterNode }: IFormFieldProps) => {
        if (clusterId) {
            return Api.getClusterDetail({
                clusterName: clusterId,
                nodeAddress: clusterNode,
            }).then((res) => {
                if (res.code === 1) {
                    return {
                        total: res.data.length,
                        data: res.data,
                    };
                }
            });
        }
        return Promise.resolve();
    };

    // 查看明细(需要传入参数 集群,引擎,group) detailInfo
    const handleViewDetails = (record: IQueueListProps, jobStage: JOB_STAGE_ENUM) => {
        history.push({
            query: {
                drawer: DRAWER_MENU_ENUM.QUEUE_DETAIL,
                node: sketchRef.current?.form.getFieldValue('clusterNode'),
                jobStage: jobStage.toString(),
                clusterName: sketchRef.current?.form.getFieldValue('clusterId'),
                jobResource: record.jobResource,
            },
        });
    };

    const handleKillAllTask = (record: IQueueListProps) => {
        Modal.confirm({
            title: '杀死全部',
            okText: '杀死全部',
            okButtonProps: {
                danger: true,
            },
            cancelText: '取消',
            width: '460px',
            icon: <CloseCircleOutlined />,
            content: (
                <div>
                    <p>
                        杀死所有
                        <span style={{ color: '#ff5f5c' }}>队列中、已存储、等待重试、等待资源</span>
                        的任务
                    </p>
                    <p>
                        <span style={{ color: '#ff5f5c' }}>运行中的任务不会被杀死</span>
                        ，可点击运行中的任务，并执行批量杀死操作
                    </p>
                </div>
            ),
            async onOk() {
                const { form, submit } = sketchRef.current!;
                const nodeAddress = form.getFieldValue('clusterNode');

                const res = await Api.killAllTask({
                    jobResource: record.jobResource,
                    nodeAddress,
                });
                if (res.code === 1) {
                    message.success('杀死全部成功！');
                    submit();
                }
            },
        });
    };

    const handleRefresh = () => {
        sketchRef.current?.submit();
    };

    const renderTableText = (text: string, record: IQueueListProps, jobStage: JOB_STAGE_ENUM) => (
        <a onClick={() => handleViewDetails(record, jobStage)}>{text || 0}</a>
    );

    useLayoutEffect(() => {
        Promise.all([getClusterSelect(), getNodeAddressSelect()]).then(() => {
            sketchRef.current?.submit();
        });
    }, []);

    const columns: ColumnsType<IQueueListProps> = [
        {
            title: '计算类型',
            dataIndex: 'jobResource',
            width: 180,
            ellipsis: true,
            render(text, record) {
                return (
                    <Tooltip title={text} placement="topLeft">
                        {renderTableText(text, record, JOB_STAGE_ENUM.Queueing)}
                    </Tooltip>
                );
            },
        },
        {
            title: '队列中(等待时长)',
            dataIndex: 'priorityJobSize',
            render(text, record) {
                const txt = text + (record.priorityWaitTime ? ` (${record.priorityWaitTime})` : '');
                return renderTableText(txt, record, JOB_STAGE_ENUM.Queueing);
            },
        },
        {
            title: '已存储',
            dataIndex: 'dbJobSize',
            render(text, record) {
                return renderTableText(text, record, JOB_STAGE_ENUM.Saved);
            },
        },
        {
            title: '等待重试',
            dataIndex: 'restartJobSize',
            render(text, record) {
                return renderTableText(text, record, JOB_STAGE_ENUM.WaitTry);
            },
        },
        {
            title: '等待资源',
            dataIndex: 'lackingJobSize',
            render(text, record) {
                return renderTableText(text, record, JOB_STAGE_ENUM.WaitResource);
            },
        },
        {
            title: '运行中',
            dataIndex: 'submittedJobSize',
            render(text, record) {
                return renderTableText(text, record, JOB_STAGE_ENUM.Running);
            },
        },
        {
            title: '操作',
            dataIndex: 'deal',
            render: (_, record) => {
                return <a onClick={() => handleKillAllTask(record)}>杀死全部</a>;
            },
        },
    ];

    const clusterOptions = useMemo(() => {
        return clusterList.map((item) => {
            return {
                label: item.clusterName,
                value: item.clusterName,
            };
        });
    }, [clusterList]);

    const clusterNodeOptions = useMemo(() => {
        return clusterNodeList.map((item) => {
            return {
                label: item,
                value: item,
            };
        });
    }, [clusterNodeList]);

    return (
        <Sketch<IQueueListProps, IFormFieldProps>
            actionRef={sketchRef}
            className="dt-queue"
            header={[
                {
                    name: 'select',
                    props: {
                        formItemProps: {
                            label: '集群',
                            name: 'clusterId',
                        },
                        slotProps: {
                            placeholder: '选择集群',
                            options: clusterOptions,
                        },
                    },
                },
                {
                    name: 'select',
                    props: {
                        formItemProps: {
                            label: '节点',
                            name: 'clusterNode',
                        },
                        slotProps: {
                            placeholder: '选择节点',
                            allowClear: true,
                            options: clusterNodeOptions,
                        },
                    },
                },
            ]}
            columns={columns}
            extra={
                <Tooltip title="刷新数据">
                    <Button className="dt-refresh">
                        <SyncOutlined onClick={() => handleRefresh()} />
                    </Button>
                </Tooltip>
            }
            request={handleRequestSearch}
            tableProps={{
                rowSelection: undefined,
                footer: undefined,
                rowKey: 'jobResource',
            }}
        />
    );
};
