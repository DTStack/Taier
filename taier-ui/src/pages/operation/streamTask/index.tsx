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

import { useContext,useMemo, useRef, useState } from 'react';
import { SyncOutlined } from '@ant-design/icons';
import { DateTime } from '@dtinsight/dt-utils';
import { Button, Divider,message, Modal, Popconfirm, Space, Tooltip } from 'antd';
import type { ColumnsType, FilterValue } from 'antd/lib/table/interface';
import { debounce } from 'lodash';
import { history } from 'umi';

import stream from '@/api';
import type { IActionRef } from '@/components/sketch';
import Sketch from '@/components/sketch';
import { DATA_SOURCE_ENUM,FLINK_SQL_TYPE, TASK_STATUS, TASK_STATUS_FILTERS, TASK_TYPE_ENUM } from '@/constant';
import context from '@/context';
import type { IStreamJobProps } from '@/interface';
import { IComputeType } from '@/interface';
import { goToTaskDev } from '@/utils';
import { TaskStatus } from '@/utils/enums';
import DetailPane from './components/detailPane';
import GoOnTask from './components/goOnTask';

const { confirm } = Modal;

interface IFormFieldProps {
    name?: string;
}

enum OPERATOR_MODE {
    goOn,
    stop,
    submit,
}

// 是否为 flinksql 向导模式
function isFlinkSqlGuideMode(taskData: IStreamJobProps) {
    const { taskType, createModel } = taskData;
    return taskType === TASK_TYPE_ENUM.SQL && createModel === FLINK_SQL_TYPE.GUIDE;
}

export default function StreamTask() {
    const { supportJobTypes } = useContext(context);
    const [overview, setOverview] = useState<{
        ALL: number;
        FAILED: number;
        RUNNING: number;
        CANCELED: number;
        UNRUNNING: number;
    }>({ ALL: 0, FAILED: 0, RUNNING: 0, CANCELED: 0, UNRUNNING: 0 });
    const [polling, setPolling] = useState<boolean>(false);
    const [goOnTask, setGoOnTask] = useState<Pick<IStreamJobProps, 'jobId' | 'id'> | undefined>(undefined);
    // 任务详情信息
    const [slidePane, setSlidePane] = useState<{
        visible: boolean;
        selectTask?: IStreamJobProps;
    }>({ visible: false, selectTask: undefined });
    const actionRef = useRef<IActionRef>(null);

    const loadCount = (params: any) => {
        stream.getStatusCount(params).then((res) => {
            if (res.code === 1 && res?.data) {
                setOverview(res.data);
            }
        });
    };

    const getStatusList = () => {
        const { ALL, FAILED, RUNNING, CANCELED, UNRUNNING } = overview;

        return [
            {
                className: 'status_overview_count_font',
                children: [{ title: '总数', dataSource: ALL }],
            },
            {
                className: 'status_overview_running_font',
                children: [{ title: '运行中', dataSource: RUNNING }],
            },
            {
                className: 'status_overview_yellow_font',
                children: [{ title: '未运行', dataSource: UNRUNNING }],
            },
            {
                className: 'status_overview_fail_font',
                children: [{ title: '失败', dataSource: FAILED }],
            },
            {
                className: 'status_overview_grey_font',
                children: [{ title: '取消', dataSource: CANCELED }],
            },
        ];
    };

    const handleRefresh = () => {
        actionRef.current?.submit();
    };

    const chooseTask = (record: IStreamJobProps) => {
        setSlidePane({ visible: true, selectTask: record });
    };

    const closeSlidePane = () => {
        setSlidePane({ visible: false, selectTask: undefined });
    };

    const handleUpdateTaskStatus = (mode: OPERATOR_MODE, task: IStreamJobProps, params?: Record<string, any>) => {
        switch (mode) {
            case OPERATOR_MODE.goOn:
                setGoOnTask({ id: task.id, jobId: task.jobId });
                break;
            case OPERATOR_MODE.stop:
                stream
                    .stopTask({
                        taskId: task.id,
                        isForce: params?.isForce,
                    })
                    .then(() => {
                        message.success('任务正在停止！');
                        actionRef.current?.submit();
                    });
                break;
            case OPERATOR_MODE.submit: {
                stream
                    .startTask({
                        taskId: task.id,
                        isRestoration: params?.isRestore,
                    })
                    .then((res) => {
                        if (res.code === 1) {
                            if (res.data.status === TASK_STATUS.SUBMITTING) {
                                message.success('任务操作成功！');
                                actionRef.current?.submit();
                            } else if (res.data.msg) {
                                message.error(res.data.msg);
                            }
                        }
                    });
                break;
            }
            default:
                break;
        }
    };

    const debounceRecoverTask = debounce(
        (task: IStreamJobProps) => {
            stream
                .startTask({
                    taskId: task.id,
                    isRestoration: 0,
                })
                .then((res) => {
                    if (res.code === 1) {
                        message.success('任务操作成功！');
                        actionRef.current?.submit();
                    }
                });
        },
        1000,
        { maxWait: 5000 }
    );

    /**
     * 这里判断是否需要自动刷新，
     * 当有等待提交之类的状态，则自动刷新
     */
    const shouldRequstPolling = (data?: IStreamJobProps[]) => {
        if (!data) {
            return false;
        }
        const SHOULD_POLLING_STATUS = [
            TASK_STATUS.RUNNING,
            TASK_STATUS.STOPING,
            TASK_STATUS.SUBMITTING,
            TASK_STATUS.RESTARTING,
            TASK_STATUS.WAIT_RUN,
            TASK_STATUS.WAIT_COMPUTE,
        ];
        const doPolling = data.some(({ status }) => SHOULD_POLLING_STATUS.includes(status));

        return doPolling;
    };

    const goOnTaskSuccess = () => {
        hideGoOnTask();
        actionRef.current?.submit();
    };

    const hideGoOnTask = () => {
        setGoOnTask(undefined);
    };

    const loadTaskList = async (
        values: IFormFieldProps,
        { current, pageSize }: { current: number; pageSize: number },
        filters: Record<string, FilterValue | null>,
        sorter: any
    ) => {
        const { status, taskType, strategyName, componentVersion } = filters || {};
        const { field, order } = sorter || {};

        const orderMapping: Record<string, string> = {
            gmtModified: 'gmt_modified',
            execStartTime: 'exec_start_time',
        };

        const sortMapping: Record<string, 'desc' | 'asc'> = {
            descend: 'desc',
            ascend: 'asc',
        };

        const reqParams = {
            currentPage: current,
            pageSize,
            taskName: values?.name,
            isTimeSortDesc: true,
            statusList: status,
            type: taskType,
            strategyId: strategyName,
            orderBy: orderMapping[field],
            sort: sortMapping[order],
            componentVersion,
        };
        loadCount({
            taskName: values.name || '',
            statusList: status,
            type: taskType,
            strategyId: strategyName,
            componentVersion,
        });
        return stream.getTaskList(reqParams).then((res) => {
            if (res.code === 1) {
                const isPolling = shouldRequstPolling(res.data?.data);
                setPolling(isPolling);
                return {
                    polling: isPolling
                        ? {
                              delay: 5000,
                          }
                        : false,
                    total: res.data?.totalCount,
                    data: res.data?.data,
                };
            }
        });
    };

    const renderStatus = () => {
        const statusList = getStatusList();
        return statusList.map(({ className, children }) => (
            <span key={className} className={className}>
                {children.map(({ title, dataSource }) => (
                    <span key={title}>
                        {title}: {dataSource || 0}
                    </span>
                ))}
            </span>
        ));
    };

    const getDealButton = (record?: IStreamJobProps) => {
        if (!record) return null;
        const notGoOn = [DATA_SOURCE_ENUM.WEBSOCKET, DATA_SOURCE_ENUM.SOCKET, DATA_SOURCE_ENUM.POSTGRESQL].includes(
            record.originSourceType
        );

        const openModal = () => {
            const Confirm = confirm({
                title: '是否停止任务',
                content: (
                    <div>
                        当任务状态过大时，保存SavePoint可能会导致任务停止失败。请确认是否保存SavePoint并停止任务
                        <Button
                            style={{
                                height: 32,
                                position: 'absolute',
                                right: 167,
                                top: 124,
                                padding: '0 15px',
                            }}
                            onClick={() => {
                                Confirm.destroy();
                                handleUpdateTaskStatus(OPERATOR_MODE.stop, record, { isForce: 1 });
                            }}
                        >
                            不保存
                        </Button>
                    </div>
                ),
                okText: '确认',
                cancelText: '取消',
                onOk: () => handleUpdateTaskStatus(OPERATOR_MODE.stop, record, { isForce: 0 }),
            });
        };

        switch (record.status) {
            case TASK_STATUS.WAIT_SUBMIT:
            case TASK_STATUS.SUBMIT_FAILED:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        <a
                            href="#"
                            onClick={() => {
                                handleUpdateTaskStatus(OPERATOR_MODE.submit, record, {
                                    isRestore: 1,
                                });
                            }}
                        >
                            提交
                        </a>
                    </Space>
                );
            case TASK_STATUS.FINISHED:
            case TASK_STATUS.STOPED:
            case TASK_STATUS.KILLED:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        {!notGoOn && (
                            <a href="#" onClick={() => handleUpdateTaskStatus(OPERATOR_MODE.goOn, record)}>
                                续跑
                            </a>
                        )}
                        {isFlinkSqlGuideMode(record) ? (
                            <a
                                href="#"
                                onClick={() =>
                                    handleUpdateTaskStatus(OPERATOR_MODE.submit, record, {
                                        isRestore: 0,
                                    })
                                }
                            >
                                重跑
                            </a>
                        ) : (
                            <Popconfirm
                                okText="确定"
                                cancelText="取消"
                                onConfirm={() => {
                                    debounceRecoverTask(record);
                                }}
                                title="重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停"
                            >
                                <a href="#">重跑</a>
                            </Popconfirm>
                        )}
                    </Space>
                );
            case TASK_STATUS.RUN_FAILED:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        {!notGoOn && (
                            <a href="#" onClick={() => handleUpdateTaskStatus(OPERATOR_MODE.goOn, record)}>
                                续跑
                            </a>
                        )}
                        <a
                            href="#"
                            onClick={() =>
                                handleUpdateTaskStatus(OPERATOR_MODE.submit, record, {
                                    isRestore: 1,
                                })
                            }
                        >
                            重试
                        </a>
                    </Space>
                );
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.TASK_STATUS_NOT_FOUND:
            case TASK_STATUS.WAIT_RUN:
            case TASK_STATUS.WAIT_COMPUTE:
            case TASK_STATUS.SUBMITTING:
            case TASK_STATUS.COMPUTING:
            case TASK_STATUS.RESTARTING:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        <a href="#" onClick={openModal}>
                            停止
                        </a>
                    </Space>
                );
            case TASK_STATUS.STOPING:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        <a href="#">正在停止</a>
                    </Space>
                );
            case TASK_STATUS.AUTO_CANCEL:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        {!notGoOn && (
                            <a
                                href="#"
                                onClick={() => {
                                    handleUpdateTaskStatus(OPERATOR_MODE.goOn, record);
                                }}
                            >
                                续跑
                            </a>
                        )}
                        <a
                            href="#"
                            onClick={() =>
                                handleUpdateTaskStatus(OPERATOR_MODE.submit, record, {
                                    isRestore: 1,
                                })
                            }
                        >
                            重试
                        </a>
                    </Space>
                );
            case TASK_STATUS.LACKING:
                return (
                    <Space size={5} split={<Divider type="vertical" />}>
                        <a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
                            修改
                        </a>
                        <a
                            href="#"
                            onClick={() =>
                                handleUpdateTaskStatus(OPERATOR_MODE.submit, record, {
                                    isRestore: 1,
                                })
                            }
                        >
                            重试
                        </a>
                    </Space>
                );
            default:
                return null;
        }
    };

    const tableColumns = useMemo<ColumnsType<IStreamJobProps>>(
        () => [
            {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                width: 230,
                ellipsis: true,
                fixed: 'left',
                render: (text, record) => {
                    return <a onClick={() => chooseTask(record)}>{text}</a>;
                },
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 120,
                fixed: 'left',
                render: (text) => {
                    return <TaskStatus value={text} />;
                },
                filters: TASK_STATUS_FILTERS,
                filterMultiple: true,
            },
            {
                title: '版本',
                dataIndex: 'componentVersion',
                width: 80,
                key: 'componentVersion',
            },
            {
                title: '任务类型',
                dataIndex: 'taskType',
                key: 'taskType',
                width: 120,
                render: (text) => supportJobTypes.find((t) => t.key === text)?.value || '未知',
                filters: supportJobTypes
                    .filter((t) => t.computeType === IComputeType.STREAM)
                    .map((t) => ({
                        text: t.value,
                        value: t.key,
                    })),
                filterMultiple: true,
            },
            {
                title: '运行开始时间',
                dataIndex: 'execStartTime',
                width: 200,
                key: 'execStartTime',
                render: (text) => (text ? DateTime.formatDateTime(text) : '-'),
            },
            {
                title: '最近操作时间',
                dataIndex: 'gmtModified',
                width: 180,
                key: 'gmtModified',
                render: (text) => (text ? DateTime.formatDateTime(text) : '-'),
                sorter: true,
            },
            {
                title: '最近操作人',
                dataIndex: 'modifyUserName',
                width: 150,
                key: 'modifyUserName',
            },
            {
                title: '操作',
                width: 160,
                key: 'operation',
                fixed: 'right',
                render: (_, record) => {
                    return getDealButton(record);
                },
            },
        ],
        [supportJobTypes]
    );

    return (
        <div className="h-full">
            <Sketch<IStreamJobProps, IFormFieldProps>
                actionRef={actionRef}
                header={[
                    {
                        name: 'input',
                        props: {
                            formItemProps: {
                                initialValue: history.location.query?.tname,
                            },
                        },
                    },
                ]}
                extra={
                    <Tooltip title="刷新数据">
                        <Button className="dt-refresh">
                            <SyncOutlined onClick={handleRefresh} spin={!!polling} />
                        </Button>
                    </Tooltip>
                }
                tableProps={{ rowSelection: undefined }}
                headerTitle={renderStatus()}
                headerTitleClassName="ope-statistics"
                request={loadTaskList}
                columns={tableColumns}
            />
            <DetailPane
                key={slidePane.selectTask?.id}
                data={slidePane.selectTask}
                visibleSlidePane={slidePane.visible}
                closeSlidePane={closeSlidePane}
                extButton={getDealButton(slidePane.selectTask)}
            />
            <GoOnTask visible={!!goOnTask} data={goOnTask} onOk={goOnTaskSuccess} onCancel={hideGoOnTask} />
        </div>
    );
}
