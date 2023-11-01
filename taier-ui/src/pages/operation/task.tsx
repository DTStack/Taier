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
import molecule from '@dtinsight/molecule';
import type { FormInstance } from 'antd';
import { Button, Checkbox, Divider, message,Tabs } from 'antd';
import type { ColumnsType } from 'antd/lib/table/interface';
import moment from 'moment';
import { history } from 'umi';

import api from '@/api';
import type { IActionRef } from '@/components/sketch';
import Sketch from '@/components/sketch';
import SlidePane from '@/components/slidePane';
import type { TASK_PERIOD_ENUM } from '@/constant';
import { offlineTaskPeriodFilter, SCHEDULE_STATUS,TASK_TYPE_ENUM } from '@/constant';
import context from '@/context';
import type { ITaskProps } from '@/interface';
import { IComputeType } from '@/interface';
import { formatDateTime, getCookie, goToTaskDev, removePopUpMenu } from '@/utils';
import { TaskTimeType } from '@/utils/enums';
import type { ITaskBasicProps } from './patch/patchModal';
import PatchModal from './patch/patchModal';
import TaskFlowView from './taskFlowView';
import './task.scss';

const { TabPane } = Tabs;

interface IFormFieldProps {
    name?: string;
    owner?: number;
    checkList?: string[];
}

interface IRequestParams {
    currentPage: number;
    pageSize: number;
    name?: string;
    operatorId?: number;
    startModifiedTime?: number;
    endModifiedTime?: number;
    scheduleStatus?: SCHEDULE_STATUS;
    taskTypeList?: number[];
    periodTypeList?: TASK_PERIOD_ENUM[];
}

export default () => {
    const { supportJobTypes } = useContext(context);
    const [selectedTask, setSelectedTasks] = useState<ITaskProps | null>(null);
    const [visibleSlidePane, setVisible] = useState(false);
    const [patchDataVisible, setPatchVisible] = useState(false);
    const [patchTargetTask, setPatchTask] = useState<ITaskBasicProps | null>(null);
    const actionRef = useRef<IActionRef>(null);

    const handleExpandWorkflow = async (expanded: boolean, record: ITaskProps): Promise<ITaskProps[]> => {
        if (expanded) {
            const res = await api.getOfflineSubTaskById<ITaskProps[]>({ taskId: record.taskId });
            if (res.code === 1) {
                return res.data.map((data) => ({
                    taskId: data.taskId,
                    name: data.name,
                    taskType: data.taskType,
                    scheduleStatus: data.scheduleStatus,
                    periodType: data.periodType,
                    ownerUserName: data.ownerUserName,
                    ownerUserId: data.ownerUserId,
                    gmtModified: data.gmtModified,
                }));
            }
        }

        return [];
    };

    const convertToParams = (formField: IFormFieldProps) => {
        const params: Partial<IRequestParams> = {
            name: formField.name,
            operatorId: formField.owner,
        };
        if (formField.checkList?.length) {
            formField.checkList.forEach((check) => {
                if (check === 'todayUpdate') {
                    params.startModifiedTime =
                        moment()
                            .set({
                                hour: 0,
                                minute: 0,
                                second: 0,
                            })
                            .unix() * 1000;
                    params.endModifiedTime =
                        moment()
                            .set({
                                hour: 23,
                                minute: 59,
                                second: 59,
                            })
                            .unix() * 1000;
                }

                if (check === 'stopped') {
                    params.scheduleStatus = SCHEDULE_STATUS.FORZON;
                }
            });
        }

        return params;
    };

    const getTableResult = async (
        params: IFormFieldProps,
        { current, pageSize }: { current: number; pageSize: number },
        filters: Record<string, any> = {}
    ) => {
        const requestParams = convertToParams(params);
        const res = await api.queryOfflineTasks({
            currentPage: current,
            pageSize,
            taskTypeList: filters.taskType || [],
            periodTypeList: filters.periodType || [],
            ...requestParams,
        });
        if (res.code === 1) {
            return {
                total: res.data.totalCount,
                data: res.data.data.map((d: ITaskProps) => ({
                    ...d,
                    children: d.taskType === TASK_TYPE_ENUM.WORK_FLOW ? [] : undefined,
                })),
            };
        }
    };

    const handleCloseSlidePane = () => {
        setVisible(false);
        setSelectedTasks(null);
        removePopUpMenu();
    };

    const handleTaskFlowPatchClick = ({ taskId, name }: ITaskBasicProps) => {
        setPatchTask({ taskId, name });
        setPatchVisible(true);
    };

    const handlePatchModalCancel = () => {
        setPatchTask(null);
        setPatchVisible(false);
    };

    // 冻结或解冻
    const forzenTasks = (mode: SCHEDULE_STATUS) => {
        const { selectedRowKeys, setSelectedKeys, submit } = actionRef.current!;
        if (!selectedRowKeys.length) {
            message.error('您没有选择任何任务！');
            return false;
        }
        api.forzenTask({
            taskIds: selectedRowKeys,
            scheduleStatus: mode,
        }).then((res) => {
            if (res.code === 1) {
                // 如果当前冻结或解冻的任务在 editor 中打开，则还需要去改变 editor 中的数据
                selectedRowKeys.forEach((key) => {
                    if (molecule.editor.isOpened(key.toString())) {
                        const groupId = molecule.editor.getGroupIdByTab(key.toString());
                        const tab = molecule.editor.getTabById<any>(key.toString(), groupId!);
                        molecule.editor.updateTab({
                            id: key.toString(),
                            data: { ...tab!.data!, scheduleStatus: mode },
                        });
                    }
                });

                setSelectedKeys([]);
                submit();
            }
        });
    };

    // 冻结或解冻指定任务
    const handleForzonTask = (taskId: number, mode: SCHEDULE_STATUS) => {
        const { submit } = actionRef.current!;
        api.forzenTask({
            taskIds: [taskId],
            scheduleStatus: mode,
        }).then((res) => {
            if (res.code === 1) {
                submit();

                // update the job view task
                if (selectedTask) {
                    const nextSelectedTask = { ...selectedTask, scheduleStatus: mode };
                    setSelectedTasks(nextSelectedTask);
                }
            }
        });
    };

    const showTask = (task: ITaskProps) => {
        setVisible(true);
        setSelectedTasks(task);
    };

    const handleFormValuesChange = (
        field: keyof IFormFieldProps,
        value: any,
        values: IFormFieldProps,
        form: FormInstance
    ) => {
        // 修改操作人需要连带勾选我的任务
        if (field === 'owner') {
            const nextCheckList = (values.checkList || []).concat();
            if (value?.toString() === getCookie('useId') && !nextCheckList.includes('person')) {
                nextCheckList.push('person');
                form.setFieldsValue({
                    checkList: nextCheckList,
                });
            }

            if (!value && nextCheckList.includes('person')) {
                const index = nextCheckList.indexOf('person');
                nextCheckList.splice(index, 1);
                form.setFieldsValue({
                    checkList: nextCheckList,
                });
            }
        }

        // 勾选我的任务，需要连带修改操作人
        if (field === 'checkList') {
            const { owner } = values;
            if (value.includes('person') && owner?.toString() !== getCookie('userId')) {
                form.setFieldsValue({
                    owner: Number(getCookie('userId')),
                });
            }

            if (!value.includes('person') && owner?.toString() === getCookie('userId')) {
                form.setFieldsValue({
                    owner: undefined,
                });
            }
        }
    };

    const columns = useMemo<ColumnsType<ITaskProps>>(() => {
        return [
            {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                render: (text, record) => {
                    const content = (
                        <a onClick={() => showTask(record)}>
                            {record.name + (record.scheduleStatus === SCHEDULE_STATUS.FORZON ? ' (已冻结)' : '')}
                        </a>
                    );
                    return content;
                },
            },
            {
                title: '提交时间',
                dataIndex: 'gmtModified',
                key: 'gmtModified',
                render: (text: string) => {
                    return <span>{formatDateTime(text)}</span>;
                },
            },
            {
                title: '任务类型',
                dataIndex: 'taskType',
                key: 'taskType',
                render: (text: TASK_TYPE_ENUM) => {
                    return supportJobTypes.find((t) => t.key === text)?.value || '未知';
                },
                filters: supportJobTypes
                    .filter((t) => t.computeType === IComputeType.BATCH)
                    .map((t) => ({ text: t.value, value: t.key })),
            },
            {
                title: '调度周期',
                dataIndex: 'periodType',
                key: 'periodType',
                render: (text) => {
                    return <TaskTimeType value={text} />;
                },
                filters: offlineTaskPeriodFilter,
            },
            {
                title: '操作人',
                dataIndex: 'operatorName',
                key: 'operatorName',
            },
            {
                title: '操作',
                key: 'operation',
                width: 120,
                render: (_, record) => {
                    return (
                        <span>
                            <a onClick={() => handleTaskFlowPatchClick(record)}>补数据</a>
                            <Divider type="vertical" />
                            <a
                                onClick={() => {
                                    goToTaskDev({ id: record.taskId });
                                }}
                            >
                                修改
                            </a>
                        </span>
                    );
                },
            },
        ];
    }, [supportJobTypes]);

    return (
        <div className="c-taskMana__wrap">
            <Sketch<ITaskProps, IFormFieldProps>
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
                    'owner',
                    {
                        name: 'checkList',
                        renderFormItem: (
                            <Checkbox.Group>
                                <Checkbox value="person" className="select-task">
                                    我的任务
                                </Checkbox>
                                <Checkbox value="todayUpdate" className="select-task">
                                    今日修改的任务
                                </Checkbox>
                                <Checkbox value="stopped" className="select-task">
                                    冻结的任务
                                </Checkbox>
                            </Checkbox.Group>
                        ),
                    },
                ]}
                request={getTableResult}
                onFormFieldChange={handleFormValuesChange}
                tableFooter={[
                    <Button key="freeze" type="primary" onClick={() => forzenTasks(SCHEDULE_STATUS.FORZON)}>
                        冻结
                    </Button>,
                    <Button
                        key="unfreeze"
                        style={{ marginLeft: 15 }}
                        onClick={() => forzenTasks(SCHEDULE_STATUS.NORMAL)}
                    >
                        解冻
                    </Button>,
                ]}
                columns={columns}
                onExpand={handleExpandWorkflow}
                tableProps={{
                    rowKey: 'taskId',
                    rowClassName: (record) => {
                        if (selectedTask && selectedTask.taskId === record.taskId) {
                            return 'row-select';
                        }
                        return '';
                    },
                }}
            />
            <SlidePane
                onClose={handleCloseSlidePane}
                visible={visibleSlidePane}
                className="dt-slide-pane"
                style={{ top: '33px' }}
            >
                <Tabs className="c-taskMana__slidePane__tabs" animated={false} tabBarStyle={{ zIndex: 3 }}>
                    <TabPane tab="依赖视图" key="taskFlow">
                        <TaskFlowView
                            onForzenTasks={handleForzonTask}
                            onPatchData={({ taskId, taskName }) =>
                                handleTaskFlowPatchClick({
                                    taskId,
                                    name: taskName,
                                })
                            }
                            tabData={selectedTask}
                        />
                    </TabPane>
                </Tabs>
            </SlidePane>
            <PatchModal visible={patchDataVisible} task={patchTargetTask} handCancel={handlePatchModalCancel} />
        </div>
    );
};
