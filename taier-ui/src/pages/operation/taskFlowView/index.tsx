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
import { PlusSquareOutlined } from '@ant-design/icons';
import { Modal } from 'antd';
import type { mxCell } from 'mxgraph';
import { history } from 'umi';

import Api from '@/api';
import type { IContextMenuConfig } from '@/components/mxGraph/container';
import MxGraphContainer from '@/components/mxGraph/container';
import { DRAWER_MENU_ENUM, SCHEDULE_STATUS, TASK_TYPE_ENUM } from '@/constant';
import context from '@/context';
import type { ITaskProps,IUpstreamJobProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';
import { formatDateTime, goToTaskDev } from '@/utils';

interface ITaskFlowViewProps {
    tabData: ITaskProps | null;
    onPatchData?: (task: IUpstreamJobProps) => void;
    onForzenTasks?: (taskId: number, status: SCHEDULE_STATUS) => void;
}

interface IGetTaskChildrenParams {
    taskId: number;
    directType: DIRECT_TYPE_ENUM;
    /**
     * 不传的话默认是 2 层
     */
    level?: number;
}

const TaskFlowView = ({ tabData, onPatchData, onForzenTasks }: ITaskFlowViewProps) => {
    const { supportJobTypes } = useContext(context);
    const [graphData, setGraphData] = useState<IUpstreamJobProps[] | null>(null);
    const [loading, setLoading] = useState(false);
    const [visible, setVisible] = useState(false);
    const [currentWorkflowTask, setWorkflowTask] = useState<IUpstreamJobProps[] | null>(null);

    /**
     * 获取任务上下游关系
     */
    const loadTaskChildren = (
        taskId: number,
        directType: DIRECT_TYPE_ENUM = DIRECT_TYPE_ENUM.CHILD,
        level?: number,
        // 是否是工作流的子任务, 如果是则改变 currentWorkflowTask 的值，否则改变 graphData 的值
        workflow?: boolean
    ) => {
        setLoading(true);
        Api.getTaskChildren<{ rootTaskNode: IUpstreamJobProps; directType: DIRECT_TYPE_ENUM }>({
            taskId,
            directType,
            level,
        } as IGetTaskChildrenParams)
            .then((res) => {
                if (res.code === 1) {
                    const data = res.data?.rootTaskNode || {};

                    // 不同的 directType 取不同的字段
                    const property = directType === DIRECT_TYPE_ENUM.CHILD ? 'childNode' : 'parentNode';

                    const performDataHandler = workflow ? setWorkflowTask : setGraphData;

                    performDataHandler((graph) => {
                        if (graph) {
                            const stack = [graph[0]];
                            while (stack.length) {
                                const item = stack.pop()!;
                                if (item.taskId === data?.taskId) {
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

    const refresh = () => {
        if (tabData) {
            setGraphData(null);
            loadTaskChildren(tabData.taskId);
        }
    };

    const handleContextMenu = (data: IUpstreamJobProps): IContextMenuConfig[] => {
        return [
            {
                title: '展开上游（6层）',
                callback: () => loadTaskChildren(data.taskId, DIRECT_TYPE_ENUM.FATHER, 6, visible),
            },
            {
                title: '展开下游（6层）',
                callback: () => loadTaskChildren(data.taskId, DIRECT_TYPE_ENUM.CHILD, 6, visible),
            },
            {
                title: '补数据',
                callback: () => onPatchData?.(data),
            },
            {
                title: '冻结',
                callback: () => onForzenTasks?.(data.taskId, SCHEDULE_STATUS.FORZON),
                disabled: data.scheduleStatus !== SCHEDULE_STATUS.NORMAL,
            },
            {
                title: '解冻',
                callback: () => onForzenTasks?.(data.taskId, SCHEDULE_STATUS.NORMAL),
                disabled:
                    data.scheduleStatus !== SCHEDULE_STATUS.STOPPED && data.scheduleStatus !== SCHEDULE_STATUS.FORZON,
            },
            {
                title: '查看实例',
                callback: () =>
                    history.push({
                        query: {
                            drawer: DRAWER_MENU_ENUM.SCHEDULE,
                            tName: data.taskName,
                        },
                    }),
            },
        ];
    };

    const handleRenderCell = (cell: mxCell) => {
        const task: IUpstreamJobProps = cell.value;
        if (task) {
            const taskType = supportJobTypes.find((t) => t.key === task.taskType)?.value || '未知';
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

        return '';
    };

    const handleClickCell = (cell: mxCell, _: any, event: React.MouseEvent<HTMLElement, MouseEvent>) => {
        if ((event.target as HTMLElement).closest('.vertex-extra')) {
            const data: IUpstreamJobProps = cell.value;

            setLoading(true);
            Api.getRootWorkflowTask<number[]>({ taskId: data.taskId })
                .then((res) => {
                    if (res.code === 1) {
                        return res.data;
                    }
                    return [];
                })
                .then((taskIdList) => {
                    return Promise.all(
                        taskIdList.map((taskId) =>
                            Api.getTaskChildren<{
                                rootTaskNode: IUpstreamJobProps;
                                directType: DIRECT_TYPE_ENUM;
                            }>({
                                taskId,
                                directType: DIRECT_TYPE_ENUM.CHILD,
                                level: 6,
                            })
                        )
                    );
                })
                .then((results) => {
                    if (results.every((res) => res.code === 1)) {
                        setWorkflowTask(results.map((res) => res.data.rootTaskNode));
                        setVisible(true);
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    };

    useEffect(() => {
        if (tabData) {
            refresh();
        }
    }, [tabData?.taskId]);

    return (
        <>
            <MxGraphContainer<IUpstreamJobProps>
                graphData={graphData}
                loading={loading}
                onRefresh={refresh}
                onRenderCell={handleRenderCell}
                onClick={handleClickCell}
                onContextMenu={handleContextMenu}
                onDrawVertex={(data) => {
                    if (data.scheduleStatus === SCHEDULE_STATUS.FORZON) {
                        return 'whiteSpace=wrap;fillColor=var(--badge-pending-background);strokeColor=var(--badge-pending-border);';
                    }
                    return 'whiteSpace=wrap;fillColor=var(--badge-running-background);strokeColor=var(--badge-running-border);';
                }}
            >
                {(data) => (
                    <>
                        <div className="graph-info">
                            <span>{data?.taskName || '-'}</span>
                            <span className="mx-2">{data?.operatorName || '-'}</span>
                            发布于
                            {data && (
                                <>
                                    <span>{formatDateTime(data.gmtCreate)}</span>
                                    <a
                                        className="mx-2"
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
                    graphData={currentWorkflowTask}
                    loading={loading}
                    onRenderCell={handleRenderCell}
                    onContextMenu={handleContextMenu}
                    onDrawVertex={(data) => {
                        if (data.scheduleStatus === SCHEDULE_STATUS.FORZON) {
                            return 'whiteSpace=wrap;fillColor=var(--badge-pending-background);strokeColor=var(--badge-pending-border);';
                        }
                        return 'whiteSpace=wrap;fillColor=var(--badge-running-background);strokeColor=var(--badge-running-border);';
                    }}
                />
            </Modal>
        </>
    );
};

export default TaskFlowView;
