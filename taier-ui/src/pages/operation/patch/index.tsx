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

import { useMemo } from 'react';
import type { FormInstance } from 'antd';
import { Checkbox, message, Modal } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import moment from 'moment';
import { history } from 'umi';

import Api from '@/api';
import Sketch from '@/components/sketch';
import { DRAWER_MENU_ENUM } from '@/constant';
import { getCookie } from '@/utils';

const { confirm } = Modal;

const disabledDate = (current: moment.Moment) => {
    return current && current.valueOf() > Date.now();
};

// 补数据实例类型
interface ITasksProps {
    allJobSum: number | null;
    gmtCreate: string;
    doneJobSum: number | null;
    operatorId: number;
    operatorName: string;
    fillDataName: string;
    finishedJobSum: number | null;
    fromDay: string;
    id: number;
    toDay: string;
    runDay: string;
}

// 筛选表单类型
interface IFormFieldProps {
    operatorId?: number;
    name?: string;
    runDay?: moment.Moment | null | undefined;
    checkList?: string[];
}

// 请求类型
interface IRequestParams {
    currentPage: number;
    pageSize: number;
    jobName: string;
    /**
     * YYYY-MM-dd 类型的日期
     */
    runDay: string;
    operatorId: number;
}

export default () => {
    const handleKillAllJobs = (job: ITasksProps) => {
        confirm({
            title: '确认提示',
            content: '确定要杀死所有实例？',
            onOk() {
                Api.stopFillDataJobs({
                    fillId: job.id,
                }).then((res) => {
                    if (res.code === 1) {
                        message.success('已成功杀死所有实例！');
                    }
                });
            },
        });
    };

    const columns = useMemo<ColumnsType<ITasksProps>>(() => {
        return [
            {
                title: '补数据名称',
                dataIndex: 'fillDataName',
                key: 'fillDataName',
                width: 300,
                render: (text, record) => {
                    return (
                        <a
                            onClick={() => {
                                sessionStorage.setItem(
                                    'task-patch-data',
                                    JSON.stringify({
                                        name: record.fillDataName,
                                        id: record.id,
                                    })
                                );
                                history.push({
                                    query: {
                                        drawer: DRAWER_MENU_ENUM.PATCH_DETAIL,
                                    },
                                });
                            }}
                        >
                            {text}
                        </a>
                    );
                },
            },
            {
                width: 120,
                title: '成功/已完成/总实例',
                dataIndex: 'doneJobSum',
                key: 'doneJobSum',
                render: (_, record) => {
                    const isComplete =
                        record.finishedJobSum === record.doneJobSum && record.doneJobSum === record.allJobSum;
                    const style = isComplete
                        ? { color: 'var(--editor-foreground)' }
                        : { color: 'var(--editorError-foreground)' };
                    return (
                        <span style={style}>
                            {record.finishedJobSum || 0}/{record.doneJobSum || 0}/{record.allJobSum || 0}
                        </span>
                    );
                },
            },
            {
                width: 140,
                title: '计划日期',
                dataIndex: 'fromDay',
                key: 'fromDay',
                render: (_, record) => {
                    return (
                        <>
                            {record.fromDay} ~ {record.toDay}
                        </>
                    );
                },
            },
            {
                width: 120,
                title: '运行日期',
                dataIndex: 'runDay',
                key: 'runDay',
            },
            {
                width: 120,
                title: '实例生成时间',
                dataIndex: 'gmtCreate',
                key: 'gmtCreate',
            },
            {
                width: 120,
                title: '操作人',
                dataIndex: 'operatorName',
                key: 'operatorName',
            },
            {
                width: 120,
                title: '操作',
                dataIndex: 'id',
                fixed: 'right',
                key: 'id',
                render: (_, record) => {
                    return <a onClick={() => handleKillAllJobs(record)}>杀死所有实例</a>;
                },
            },
        ];
    }, []);

    const convertFormFieldToParams = (values: IFormFieldProps): Partial<IRequestParams> => {
        const { name, operatorId, runDay } = values;
        return {
            jobName: name,
            runDay: runDay ? moment(runDay).format('YYYY-MM-DD') : undefined,
            operatorId,
        };
    };

    const handleRequestSearch = (
        values: IFormFieldProps,
        { current, pageSize }: { current: number; pageSize: number }
    ) => {
        const params: Partial<IRequestParams> = {
            currentPage: current,
            pageSize,
            ...convertFormFieldToParams(values),
        };
        return Api.getFillData(params).then((res) => {
            if (res.code === 1) {
                return {
                    total: res.data.totalCount,
                    data: res.data.data || [],
                };
            }
        });
    };

    const handleFormFieldChange = (
        field: keyof IFormFieldProps,
        value: any,
        values: IFormFieldProps,
        form: FormInstance<IFormFieldProps>
    ) => {
        const currentUser = Number(getCookie('userId'));
        if (field === 'checkList') {
            const checkListValue = value as string[];
            // 勾选「我今天补的」则修改运行日期为今日，操作人为当前用户
            if (checkListValue.includes('todayUpdate')) {
                form.setFieldsValue({
                    operatorId: currentUser,
                    runDay: moment(),
                });
            } else if (checkListValue.includes('person')) {
                // 勾选「我的任务」则修改操作人为当前用户
                form.setFieldsValue({
                    operatorId: currentUser,
                });
            } else {
                // 如果都不勾选
                // 运行日期为今日，则修改运行日期为空
                if (moment().isSame(values.runDay, 'day')) {
                    form.setFieldsValue({
                        runDay: null,
                    });
                }

                // 操作人为当前用户，则修改操作人为空
                if (values.operatorId === currentUser) {
                    form.setFieldsValue({
                        operatorId: undefined,
                    });
                }
            }
        }

        if (field === 'operatorId') {
            const ownerValue = value as number | undefined;

            // 如果操作人选择当前用户，则勾选我的任务
            if (ownerValue === currentUser) {
                form.setFieldsValue({
                    checkList: ['person'],
                });
            } else if (values.checkList?.includes('person')) {
                // 如果操作人选择非当前用户，但是「我的任务」处于勾选状态，则取消勾选
                form.setFieldsValue({
                    checkList: [],
                });
            }
        }

        if (field === 'runDay') {
            const runDayValue = value as moment.Moment | undefined;
            if (moment().isSame(runDayValue, 'day') && values.operatorId === currentUser) {
                const nextCheckList = values.checkList?.concat() || [];
                if (!nextCheckList.includes('todayUpdate')) {
                    nextCheckList.push('todayUpdate');
                }
                form.setFieldsValue({
                    checkList: nextCheckList,
                });
            }
        }
    };

    return (
        <>
            <Sketch<ITasksProps, IFormFieldProps>
                request={handleRequestSearch}
                header={[
                    'input',
                    {
                        name: 'operatorId',
                        props: {
                            formItemProps: { label: '操作人' },
                            slotProps: {
                                placeholder: '请选择操作人',
                            },
                        },
                    },
                    {
                        name: 'datePicker',
                        props: {
                            formItemProps: {
                                name: 'runDay',
                                label: '运行日期',
                            },
                            slotProps: {
                                placeholder: '运行日期',
                                disabledDate,
                            },
                        },
                    },
                    {
                        name: 'checkList',
                        renderFormItem: (
                            <Checkbox.Group>
                                <Checkbox value="person">我的任务</Checkbox>
                                <Checkbox value="todayUpdate">我今天补的</Checkbox>
                            </Checkbox.Group>
                        ),
                    },
                ]}
                onFormFieldChange={handleFormFieldChange}
                columns={columns}
                tableProps={{ rowSelection: undefined }}
            />
        </>
    );
};
