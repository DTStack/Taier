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

import { useContext, useEffect, useState } from 'react';
import ReactDOMServer from 'react-dom/server';
import { PlusSquareOutlined, ReloadOutlined } from '@ant-design/icons';
import { Col,message, Modal, Row, Tooltip } from 'antd';
import type { mxCell } from 'mxgraph';

import Api from '@/api';
import { DetailInfoModal } from '@/components/detailInfo';
import type { IContextMenuConfig } from '@/components/mxGraph/container';
import MxGraphContainer from '@/components/mxGraph/container';
import {
    FAILED_STATUS,
    PARENTFAILED_STATUS,
    RESTART_STATUS_ENUM,
    RUN_FAILED_STATUS,
    TASK_STATUS,
    TASK_TYPE_ENUM,
} from '@/constant';
import context from '@/context';
import type { IUpstreamJobProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';
import { formatDateTime, getVertexStyle, goToTaskDev } from '@/utils';
import { taskStatusText } from '@/utils/enums';
import type { IScheduleTaskProps } from '../schedule';
import LogInfo from './taskLog';
import './index.scss';

interface ITaskJobFlowViewProps {
    taskJob?: IScheduleTaskProps;
    reload?: () => void;
}

export default function TaskJobFlowView({ taskJob, reload }: ITaskJobFlowViewProps) {
    const { supportJobTypes } = useContext(context);
    const [graphData, setGraphData] = useState<IUpstreamJobProps[] | null>(null);
    const [loading, setLoading] = useState(false);
    // 任务属性
    const [taskAttribute, setAttribute] = useState<{
        visible: boolean;
        job: undefined | IUpstreamJobProps;
    }>({
        visible: false,
        job: undefined,
    });
    // 任务日志
    const [taskLogInfo, setTaskLog] = useState<{
        jobId: string | null;
        current: number;
        total: number;
        visible: boolean;
        log: null | Record<string, any>;
    }>({
        jobId: null,
        current: 0,
        total: 0,
        visible: false,
        log: null,
    });
    // 工作流 modal
    const [visible, setVisible] = useState(false);
    const [workflowJob, setWorkflowJob] = useState<IUpstreamJobProps[] | null>(null);

    // 获取任务日志详情
    const handleGetTaskLog = (jobId: string, current?: number) => {
        Api.getOfflineTaskLog({
            jobId,
            pageInfo: current ?? taskLogInfo.current,
        }).then((res) => {
            if (res.code === 1) {
                setTaskLog({
                    jobId,
                    current: res.data.pageIndex,
                    total: res.data.pageSize,
                    visible: true,
                    log: res.data,
                });
            }
        });
    };

    // 停止任务
    const handleStopJob = (params: { jobIds: string[] }) => {
        Api.batchStopJob(params).then((res) => {
            if (res.code === 1) {
                message.success('任务终止运行命令已提交！');
            }

            refresh();
        });
    };

    // 重跑并恢复任务
    const restartAndResume = (params: { jobIds: string[]; restartType: RESTART_STATUS_ENUM }) => {
        Api.batchRestartAndResume(params).then((res) => {
            if (res.code === 1) {
                message.success(`置成功并恢复调度命令已提交!`);
                if (reload) reload();
            }

            refresh();
        });
    };

    const loadTaskChildren = (
        jobId: string,
        directType = DIRECT_TYPE_ENUM.CHILD,
        level?: number,
        isWorkflow?: boolean
    ) => {
        setLoading(true);
        Api.getJobChildren({
            jobId,
            directType,
            level,
        })
            .then((res) => {
                if (res.code === 1) {
                    const data = res.data.rootNode as IUpstreamJobProps;

                    // 不同的 directType 取不同的字段
                    const property = directType === DIRECT_TYPE_ENUM.CHILD ? 'childNode' : 'parentNode';

                    const performDataHandler = isWorkflow ? setWorkflowJob : setGraphData;

                    performDataHandler((graph) => {
                        if (graph) {
                            const stack = [graph[0]];
                            while (stack.length) {
                                const item = stack.pop()!;
                                if (item.jobId === data?.jobId) {
                                    item[property] = data[property];
                                    break;
                                }

                                stack.push(...(item?.[property] || []));
                            }

                            return [...graph];
                        }
                        return [data];
                    });
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const loadPeriodsData = async (params: {
        isAfter: boolean;
        jobId: string;
        limit: number;
    }): Promise<
        {
            cycTime: string;
            jobId: string;
            status: TASK_STATUS;
        }[]
    > => {
        const res = await Api.getOfflineTaskPeriods(params);
        if (res.code === 1) {
            return res.data;
        }
        return [];
    };

    /**
     * 刷新当前数据
     * @param jobId 若传 jobId，则表示跳转到该任务
     */
    const refresh = (jobId?: string) => {
        setGraphData(null);
        loadTaskChildren(jobId || taskJob!.jobId);
    };

    const loadByJobId = (jobId: string) => {
        refresh(jobId);
    };

    const handleDoubleClick = (data: IUpstreamJobProps) => {
        handleGetTaskLog(data.jobId, 0);
    };

    const handleClickCell = (cell: mxCell, _: any, event: React.MouseEvent<HTMLElement, MouseEvent>) => {
        if ((event.target as HTMLElement).closest('.vertex-extra')) {
            const data: IUpstreamJobProps = cell.value;

            setLoading(true);

            Api.getRootWorkflowJob<string[]>({ jobId: data.jobId })
                .then((res) => {
                    if (res.code === 1) {
                        return res.data;
                    }
                    return [];
                })
                .then((rootJobIds) => {
                    return Promise.all(
                        rootJobIds.map((rootJobId) =>
                            // 根据 jobId 在获取对应的工作流图数据
                            Api.getJobChildren({
                                jobId: rootJobId,
                                directType: DIRECT_TYPE_ENUM.CHILD,
                                level: 6,
                            })
                        )
                    );
                })
                .then((results) => {
                    if (results.every((res) => res.code === 1)) {
                        setWorkflowJob(results.map((res) => res.data.rootNode));
                        setVisible(true);
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    };

    const handleRenderCell = (cell: mxCell) => {
        if (cell.vertex && cell.value) {
            const task: IUpstreamJobProps = cell.value;
            const taskType = supportJobTypes.find((t) => t.key === task.taskType)?.value || '未知';
            if (task) {
                return ReactDOMServer.renderToString(
                    <div className="vertex">
                        <span className="vertex-title">
                            {task.taskName}
                            <span className="vertex-extra">
                                {task.taskType === TASK_TYPE_ENUM.WORK_FLOW && <PlusSquareOutlined />}
                            </span>
                        </span>
                        <br />
                        <span className="vertex-desc">{taskType}</span>
                    </div>
                );
            }
        }
        return '';
    };

    const handleContextMenu = async (data: IUpstreamJobProps): Promise<IContextMenuConfig[]> => {
        return [
            {
                title: '展开上游（6层）',
                callback: () => loadTaskChildren(data.jobId, DIRECT_TYPE_ENUM.FATHER, 6),
            },
            {
                title: '展开下游（6层）',
                callback: () => loadTaskChildren(data.jobId, DIRECT_TYPE_ENUM.CHILD, 6),
            },
            {
                title: '查看任务日志',
                callback: () => handleGetTaskLog(data.jobId, 0),
            },
            {
                title: '查看任务属性',
                callback: () => setAttribute({ visible: true, job: data }),
            },
            {
                title: '转到前一周期实例',
                children: (await loadPeriodsData({ jobId: data.jobId, isAfter: false, limit: 6 })).map((period) => ({
                    title: `${period.cycTime} (${taskStatusText(period.status)})`,
                    callback: () => loadByJobId(period.jobId),
                })),
            },
            {
                title: '转到下一周期实例',
                children: (await loadPeriodsData({ jobId: data.jobId, isAfter: true, limit: 6 })).map((period) => ({
                    title: `${period.cycTime} (${taskStatusText(period.status)})`,
                    callback: () => loadByJobId(period.jobId),
                })),
            },
            {
                title: '修改任务',
                callback: () => goToTaskDev({ id: data.taskId }),
            },
            {
                title: '终止',
                callback: () => handleStopJob({ jobIds: [data.jobId] }),
                disabled:
                    data.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                    data.status === TASK_STATUS.SUBMITTING || // 提交中
                    data.status === TASK_STATUS.WAIT_RUN || // 等待运行
                    data.status === TASK_STATUS.RUNNING, // 运行中
            },
            {
                title: '刷新任务实例',
                callback: () => refresh(),
            },
            {
                title: '置成功并恢复调度',
                callback: () =>
                    restartAndResume({
                        jobIds: [data.jobId],
                        restartType: RESTART_STATUS_ENUM.SUCCESSFULLY_AND_RESUME,
                    }),
                // 所有「失败」任务重跑并恢复调度
                disabled: [FAILED_STATUS, PARENTFAILED_STATUS, RUN_FAILED_STATUS].some((collection) =>
                    collection.includes(data.status)
                ),
            },
        ];
    };

    useEffect(() => {
        if (taskJob?.jobId) {
            refresh();
        }
    }, [taskJob?.jobId]);

    return (
        <>
            <MxGraphContainer<IUpstreamJobProps>
                graphData={graphData}
                loading={loading}
                vertexKey="jobId"
                onRefresh={() => refresh()}
                onRenderCell={handleRenderCell}
                onClick={handleClickCell}
                onContextMenu={handleContextMenu}
                onDrawVertex={(data) => getVertexStyle(data.status)}
                onDoubleClick={handleDoubleClick}
            >
                {(data) => (
                    <>
                        <div className="graph-status">
                            <Row justify="start" wrap={false}>
                                <Col span={4} style={{ minWidth: 200 }}>
                                    <div className="mxYellow" />
                                    等待提交/提交中/等待运行
                                </Col>
                                <Col span={3}>
                                    <div className="mxBlue" />
                                    运行中
                                </Col>
                                <Col span={3}>
                                    <div className="mxGreen" />
                                    成功
                                </Col>
                                <Col span={3}>
                                    <div className="mxRed" />
                                    失败
                                </Col>
                                <Col span={4}>
                                    <div className="mxGray" />
                                    冻结/取消
                                </Col>
                            </Row>
                        </div>
                        <div className="graph-info">
                            <span>{data?.taskName || '-'}</span>
                            <span className="mx-2">{data?.operatorName || '-'}</span>
                            提交于
                            {data && (
                                <>
                                    <span>{formatDateTime(data.taskGmtCreate)}</span>
                                    &nbsp;
                                    <a
                                        title="双击任务可快速查看日志"
                                        onClick={() => handleGetTaskLog(data.jobId, 0)}
                                        style={{ marginRight: '8' }}
                                    >
                                        查看日志
                                    </a>
                                    &nbsp;
                                    <a
                                        onClick={() => {
                                            goToTaskDev({ id: data.taskId });
                                        }}
                                    >
                                        查看代码
                                    </a>
                                </>
                            )}
                        </div>
                    </>
                )}
            </MxGraphContainer>
            <Modal
                title="工作流"
                visible={visible}
                width={800}
                footer={null}
                bodyStyle={{ height: 400 }}
                destroyOnClose
                onCancel={() => setVisible(false)}
            >
                <MxGraphContainer<IUpstreamJobProps>
                    graphData={workflowJob}
                    loading={loading}
                    vertexKey="jobId"
                    onRenderCell={handleRenderCell}
                    onContextMenu={handleContextMenu}
                    onDrawVertex={(data) => getVertexStyle(data.status)}
                    onDoubleClick={handleDoubleClick}
                />
            </Modal>
            <DetailInfoModal
                title="查看属性"
                visible={taskAttribute.visible}
                onCancel={() => setAttribute({ visible: false, job: undefined })}
                loading={false}
                type="taskJob"
                data={taskAttribute.job}
            />
            <Modal
                key={taskJob && taskJob.jobId}
                width={800}
                title={
                    <span>
                        任务日志
                        <Tooltip placement="right" title="刷新">
                            <ReloadOutlined
                                style={{
                                    cursor: 'pointer',
                                    marginLeft: '5px',
                                }}
                                onClick={() => handleGetTaskLog(taskLogInfo.jobId!)}
                            />
                        </Tooltip>
                    </span>
                }
                wrapClassName="no-padding-modal"
                visible={taskLogInfo.visible}
                onCancel={() => setTaskLog({ visible: false, current: 0, total: 0, log: null, jobId: null })}
                footer={null}
                maskClosable
            >
                <LogInfo
                    log={taskLogInfo.log?.logInfo || taskLogInfo.log?.engineLog}
                    sqlText={taskLogInfo.log?.sqlText}
                    syncLog={taskLogInfo.log?.syncLog}
                    downLoadUrl={taskLogInfo.log?.downLoadUrl}
                    syncJobInfo={taskLogInfo.log?.syncJobInfo}
                    downloadLog={taskLogInfo.log?.downloadLog}
                    subNodeDownloadLog={taskLogInfo.log?.subNodeDownloadLog}
                    page={{
                        current: taskLogInfo.current,
                        total: taskLogInfo.total,
                    }}
                    onChangePage={(page: number) => handleGetTaskLog(taskLogInfo.jobId!, page)}
                    height="520px"
                />
            </Modal>
        </>
    );
}
