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

import { useContext,useMemo, useState } from 'react';
import { SyncOutlined } from '@ant-design/icons';
import { Button, message, Modal, Tooltip } from 'antd';
import type { ColumnsType, FilterValue, SorterResult } from 'antd/lib/table/interface';
import moment from 'moment';

import Api from '@/api';
import Sketch, { useSketchRef } from '@/components/sketch';
import SlidePane from '@/components/slidePane';
import {
    RESTART_STATUS_ENUM,
    STATISTICS_TYPE_ENUM,
    TASK_STATUS,
    TASK_STATUS_FILTERS,
    TASK_TYPE_ENUM,
} from '@/constant';
import context from '@/context';
import { getTodayTime, removePopUpMenu } from '@/utils';
import { TaskStatus } from '@/utils/enums';
import type { IScheduleTaskProps } from '../schedule';
import TaskJobFlowView from '../taskJobFlowView';
import './detail.scss';

const { confirm, warning } = Modal;
const yesterDay = moment().subtract(1, 'days');

// 任务类型
type ITableDataProps = IScheduleTaskProps;

// 请求参数类型
interface IRequestParams {
    currentPage: number;
    pageSize: number;
    taskName: string;
    operatorId: number;
    cycStartDay: number;
    cycEndDay: number;
    jobStatusList: number[];
    taskTypeList: string[];
    execTimeSort: string | undefined;
    execStartSort: string | undefined;
    cycSort: string | undefined;
    businessDateSort: string | undefined;
    retryNumSort: string | undefined;
    fillId: number;
}

// 条件筛选表单类型
interface IFormFieldProps {
    name?: string;
    owner?: number;
    rangeDate?: [moment.Moment, moment.Moment];
}

const disabledDate = (current: moment.Moment) => {
    return current && current.valueOf() > new Date().getTime();
};

export default () => {
    const { supportJobTypes } = useContext(context);
    const [fillId] = useState(() => Number(JSON.parse(sessionStorage.getItem('task-patch-data') || '{}').id));
    const [statistics, setStatistics] = useState<Record<string, number>>({});

    const [selectedTask, setSelectedTask] = useState<ITableDataProps | undefined>(undefined);
    const [visibleSlidePane, setSlideVisible] = useState(false);
    const [btnStatus, setBtnStatus] = useState([false, false]);
    const actionRef = useSketchRef();

    const loadJobStatics = (params: any) => {
        Api.queryJobStatics({
            ...params,
            type: STATISTICS_TYPE_ENUM.FILL_DATA,
            fillTaskName: params.fillJobName,
        }).then((res) => {
            if (res.code === 1) {
                const data: { count: TASK_STATUS; statusKey: string }[] = res.data || [];
                const nextStat = data.reduce((pre, cur) => {
                    const next = pre;
                    next[cur.statusKey] = cur.count;
                    return next;
                }, {} as Record<string, TASK_STATUS>);
                setStatistics(nextStat);
            }
        });
    };

    const showTask = (task: ITableDataProps) => {
        setSlideVisible(true);
        setSelectedTask(task);
    };

    // 是否可以进行kill
    const canKill = (ids: React.Key[]) => {
        const tasks: ITableDataProps[] = actionRef.current?.getTableData() || [];
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i += 1) {
                const id = ids[i];
                const res = tasks.find((task) => task.jobId.toString() === id.toString());
                if (
                    res &&
                    (res.status === TASK_STATUS.SUBMIT_FAILED ||
                        res.status === TASK_STATUS.RUN_FAILED ||
                        res.status === TASK_STATUS.PARENT_FAILD ||
                        res.status === TASK_STATUS.STOPED ||
                        res.status === TASK_STATUS.FINISHED)
                )
                    return false;
            }
            return true;
        }
    };

    // 批量杀任务
    const batchKillJobs = () => {
        const selected = actionRef.current?.selectedRowKeys;

        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要杀死的任务！',
            });
            return;
        }
        if (canKill(selected)) {
            confirm({
                title: '确认提示',
                content: '确定要杀死选择的任务？',
                onOk() {
                    Api.batchStopJob({ jobIds: selected }).then((res) => {
                        if (res.code === 1) {
                            actionRef.current?.setSelectedKeys([]);
                            actionRef.current?.submit();
                            message.success('已经成功杀死所选任务！');
                        }
                    });
                },
            });
        } else {
            warning({
                title: '提示',
                content: `
                    “失败”、“取消”、“成功”状态和“已删除”的任务，不能被杀死 !
                `,
            });
        }
    };

    const canReload = (ids: React.Key[]) => {
        // 未运行、成功、失败的任务可以reload
        const tasks: ITableDataProps[] = actionRef.current?.getTableData() || [];
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i += 1) {
                const id = ids[i];
                const task = tasks.find((item) => item.jobId.toString() === id.toString());
                if (
                    task &&
                    task.status !== TASK_STATUS.WAIT_SUBMIT &&
                    task.status !== TASK_STATUS.FINISHED &&
                    task.status !== TASK_STATUS.RUN_FAILED &&
                    task.status !== TASK_STATUS.SUBMIT_FAILED &&
                    task.status !== TASK_STATUS.STOPED &&
                    task.status !== TASK_STATUS.KILLED &&
                    task.status !== TASK_STATUS.PARENT_FAILD
                )
                    return false;
            }
            return true;
        }
    };

    const batchReloadJobs = () => {
        // 批量重跑
        const selected = actionRef.current?.selectedRowKeys;
        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要重跑的任务！',
            });
            return;
        }
        if (canReload(selected)) {
            confirm({
                title: '确认提示',
                content: '确认需要重跑选择的任务？',
                onOk() {
                    Api.batchRestartAndResume({
                        jobIds: selected,
                        restartType: RESTART_STATUS_ENUM.DOWNSTREAM,
                    }).then((res: any) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑所选任务！');
                            actionRef.current?.setSelectedKeys([]);
                            actionRef.current?.submit();
                        }
                    });
                },
            });
        } else {
            warning({
                title: '提示',
                content: `
                    只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                    请您重新选择!
                `,
            });
        }
    };

    // 杀死所有实例
    const killAllJobs = () => {
        Api.stopFillDataJobs({
            fillId,
        }).then((res) => {
            if (res.code === 1) {
                actionRef.current?.submit();
                message.success('已成功杀死所有实例！');
            }
        });
    };

    const closeSlidePane = () => {
        setSlideVisible(false);
        setSelectedTask(undefined);
        removePopUpMenu();
    };

    const renderStatus = (list: typeof statusList) => {
        return list.map((item, index) => {
            const { className, children } = item;
            return (
                <span key={index} className={className}>
                    {children.map((childItem) => {
                        return (
                            <span key={childItem.title}>
                                {childItem.title}: {childItem.dataSource || 0}
                            </span>
                        );
                    })}
                </span>
            );
        });
    };

    const statusList = useMemo(() => {
        const {
            ALL,
            RUNNING,
            UNSUBMIT,
            SUBMITTING,
            WAITENGINE,
            FINISHED,
            CANCELED,
            FROZEN,
            SUBMITFAILD,
            FAILED,
            PARENTFAILED,
        } = statistics;
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
                children: [
                    { title: '等待提交', dataSource: UNSUBMIT },
                    { title: '提交中', dataSource: SUBMITTING },
                    { title: '等待运行', dataSource: WAITENGINE },
                ],
            },
            {
                className: 'status_overview_finished_font',
                children: [{ title: '成功', dataSource: FINISHED }],
            },
            {
                className: 'status_overview_grey_font',
                children: [
                    { title: '取消', dataSource: CANCELED },
                    { title: '冻结', dataSource: FROZEN },
                ],
            },
            {
                className: 'status_overview_fail_font',
                children: [
                    { title: '提交失败', dataSource: SUBMITFAILD },
                    { title: '运行失败', dataSource: FAILED },
                    { title: '上游失败', dataSource: PARENTFAILED },
                ],
            },
        ];
    }, [statistics]);

    const columns = useMemo<ColumnsType<ITableDataProps>>(() => {
        return [
            {
                title: '任务名称',
                dataIndex: 'jobName',
                key: 'jobName',
                width: 200,
                fixed: 'left',
                render: (_, record) => {
                    const name = record.taskName;
                    const originText = name;
                    let showName: React.ReactNode;
                    if (record.retryNum && [TASK_STATUS.WAIT_RUN, TASK_STATUS.RUNNING].indexOf(record.status) > -1) {
                        showName = (
                            <a
                                onClick={() => {
                                    showTask(record);
                                }}
                            >
                                {name}(重试)
                            </a>
                        );
                    } else {
                        showName = (
                            <a
                                onClick={() => {
                                    showTask(record);
                                }}
                            >
                                {name}
                            </a>
                        );
                    }
                    return <span title={originText}>{showName}</span>;
                },
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                fixed: 'left',
                render: (text) => {
                    return <TaskStatus value={text} />;
                },
                width: '110px',
                filters: TASK_STATUS_FILTERS,
                filterMultiple: true,
            },
            {
                title: '任务类型',
                dataIndex: 'taskType',
                key: 'taskType',
                width: '100px',
                render: (text) => {
                    return supportJobTypes.find((t) => t.key === text)?.value || '未知';
                },
                filters: supportJobTypes.map((t) => ({ text: t.value, value: t.key })),
            },
            {
                title: '计划时间',
                dataIndex: 'cycTime',
                key: 'cycTime',
                width: '180px',
                sorter: true,
            },
            {
                title: '开始时间',
                dataIndex: 'startExecTime',
                key: 'startExecTime',
                width: 160,
                sorter: true,
            },
            {
                title: '结束时间',
                dataIndex: 'endExecTime',
                key: 'endExecTime',
                width: 160,
                sorter: true,
            },
            {
                title: '运行时长',
                dataIndex: 'execTime',
                key: 'execTime',
                width: '150px',
                sorter: true,
            },
            {
                title: '重试次数',
                dataIndex: 'retryNum',
                key: 'retryNum',
                width: '120px',
                sorter: true,
            },
            {
                title: '操作人',
                dataIndex: 'operatorName',
                key: 'operatorName',
                width: '180px',
            },
        ];
    }, [supportJobTypes]);

    const handleExpandJob = async (expanded: boolean, record: Pick<ITableDataProps, 'jobId'>) => {
        if (expanded) {
            const res = await Api.getSubJobs<ITableDataProps[]>({
                jobId: record.jobId,
            });
            if (res.code === 1) {
                return res.data;
            }
        }

        return [];
    };

    const convertToParams = (values: IFormFieldProps): Partial<IRequestParams> => {
        return {
            fillId: fillId || undefined,
            taskName: values.name,
            operatorId: values.owner,
            cycStartDay: values.rangeDate && getTodayTime(values.rangeDate[0])[0].unix(),
            cycEndDay: values.rangeDate && getTodayTime(values.rangeDate[1])[1].unix(),
        };
    };

    const handleSketchRequest = async (
        values: IFormFieldProps,
        { current, pageSize }: { current: number; pageSize: number },
        filters: Record<string, FilterValue | null>,
        sorter?: SorterResult<any>
    ): Promise<{ polling: true; total: number; data: ITableDataProps[] }> => {
        const params = convertToParams(values);
        const { status = [], taskType = [] } = filters;

        const sortMapping: Record<string, string> = {
            execTime: 'execTimeSort',
            cycTime: 'cycSort',
            startExecTime: 'execStartSort',
            endExecTime: 'execTimeSort',
            retryNum: 'retryNumSort',
        };

        const orderMapping = {
            descend: 'desc',
            ascend: 'asc',
        };

        const { field = 'cycTime', order } = sorter || {};

        const sortKey = sortMapping[field as string];
        const sortValue = orderMapping[order || 'descend'];

        const queryParams: Partial<IRequestParams> = {
            ...params,
            currentPage: current,
            pageSize,
            jobStatusList: (status || []) as number[],
            taskTypeList: (taskType || []) as string[],
            [sortKey]: sortValue,
        };
        loadJobStatics(queryParams);

        return new Promise((resolve) => {
            const currentTableData: ITableDataProps[] = actionRef.current?.getTableData() || [];

            const pendingGetChildrenList = currentTableData.reduce<string[]>((pre, cur) => {
                if (cur.children?.length) {
                    return [...pre, cur.jobId];
                }
                return pre;
            }, []);

            Promise.all(pendingGetChildrenList.map((jobId) => handleExpandJob(true, { jobId })))
                .then((results) =>
                    results.reduce<Record<string, IScheduleTaskProps[]>>(
                        (pre, cur, idx) => ({ ...pre, [pendingGetChildrenList[idx]]: cur }),
                        {}
                    )
                )
                .then((jobCollection) => {
                    Api.getFillDataDetail<{
                        totalCount: number;
                        data: {
                            fillDataJobVOLists?: ITableDataProps[];
                        };
                    }>(queryParams).then((res) => {
                        if (res.code === 1) {
                            actionRef.current?.setSelectedKeys([]);
                            resolve({
                                polling: true,
                                total: res.data.totalCount,
                                data: (res.data.data.fillDataJobVOLists || []).map((vo) => {
                                    const children = jobCollection[vo.jobId] || [];
                                    return {
                                        ...vo,
                                        children: vo.taskType === TASK_TYPE_ENUM.WORK_FLOW ? children : undefined,
                                    };
                                }),
                            });
                        }
                    });
                });
        });
    };

    const handleTableSelect = (_: any, rows: ITableDataProps[]) => {
        let haveFail = false;
        let haveNotRun = false;
        let haveSuccess = false;
        let haveRunning = false;
        const selectedRows = rows || [];
        for (let i = 0; i < selectedRows.length; i += 1) {
            const row = selectedRows[i];
            switch (row.status) {
                case TASK_STATUS.RUN_FAILED:
                case TASK_STATUS.PARENT_FAILD:
                case TASK_STATUS.SUBMIT_FAILED: {
                    haveFail = true;
                    break;
                }
                case TASK_STATUS.RUNNING:
                case TASK_STATUS.SUBMITTING:
                case TASK_STATUS.WAIT_SUBMIT:
                case TASK_STATUS.WAIT_RUN: {
                    haveRunning = true;
                    break;
                }
                case TASK_STATUS.FINISHED: {
                    haveSuccess = true;
                    break;
                }
                default: {
                    haveNotRun = true;
                    break;
                }
            }
        }

        const couldKill = haveRunning && !haveFail && !haveNotRun && !haveFail;
        const couldReRun = !haveRunning && (haveSuccess || haveFail || haveNotRun || haveFail);
        setBtnStatus([couldKill, couldReRun]);
    };

    const handleRefresh = () => {
        actionRef.current?.submit();
    };

    return (
        <div className="dt-patch-data-detail">
            <Sketch<ITableDataProps, IFormFieldProps>
                actionRef={actionRef}
                request={handleSketchRequest}
                onTableSelect={handleTableSelect}
                header={[
                    'input',
                    'owner',
                    {
                        name: 'rangeDate',
                        props: {
                            formItemProps: {
                                label: '计划时间',
                            },
                            slotProps: {
                                ranges: {
                                    昨天: [moment().subtract(2, 'days'), yesterDay],
                                    最近7天: [moment().subtract(8, 'days'), yesterDay],
                                    最近30天: [moment().subtract(31, 'days'), yesterDay],
                                },
                                disabledDate,
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
                headerTitle={renderStatus(statusList)}
                headerTitleClassName="ope-statistics"
                columns={columns}
                onExpand={handleExpandJob}
                tableProps={{
                    rowKey: 'jobId',
                    rowClassName: (record) => {
                        if (selectedTask && selectedTask.taskId === record.taskId) {
                            return 'row-select';
                        }
                        return '';
                    },
                }}
                tableFooter={[
                    <Button
                        disabled={!btnStatus[0]}
                        key="kill"
                        style={{ marginRight: 12 }}
                        type="primary"
                        onClick={batchKillJobs}
                    >
                        批量杀任务
                    </Button>,
                    <Button
                        disabled={!btnStatus[1]}
                        style={{ marginRight: 12 }}
                        key="reload"
                        type="primary"
                        onClick={batchReloadJobs}
                    >
                        重跑当前及下游任务
                    </Button>,
                    <Button key="killAll" type="primary" onClick={killAllJobs}>
                        杀死所有实例
                    </Button>,
                ]}
            />
            <SlidePane
                onClose={closeSlidePane}
                visible={visibleSlidePane}
                style={{
                    right: '0px',
                    width: '60%',
                    bottom: 0,
                    minHeight: '400px',
                    position: 'fixed',
                    paddingTop: '64px',
                    paddingBottom: 22,
                }}
            >
                <TaskJobFlowView taskJob={selectedTask} reload={() => actionRef.current?.submit()} />
            </SlidePane>
        </div>
    );
};
