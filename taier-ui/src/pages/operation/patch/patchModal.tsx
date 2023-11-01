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

import { useContext,useEffect, useRef, useState } from 'react';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { Checkbox, Col, DatePicker, Form, Input, message, Modal, Row, Space, Table,TimePicker, Tooltip } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import type { TableRowSelection } from 'antd/lib/table/interface';
import { range } from 'lodash';
import moment from 'moment';
import { history } from 'umi';

import Api from '@/api';
import type { SCHEDULE_STATUS, TASK_TYPE_ENUM } from '@/constant';
import { DRAWER_MENU_ENUM, formItemLayout } from '@/constant';
import context from '@/context';
import type { ITaskProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';

const FormItem = Form.Item;
const { RangePicker } = DatePicker;
const { confirm } = Modal;

export type ITaskBasicProps = Pick<ITaskProps, 'taskId' | 'name'>;

interface IPatchDataProps {
    task: ITaskBasicProps | null;
    visible: boolean;
    handCancel: () => void;
}

const format = 'HH:mm';

interface ITaskNodeProps {
    taskId: number;
    taskName: string;
    taskType: TASK_TYPE_ENUM;
    scheduleStatus: SCHEDULE_STATUS;
    gmtCreate: number;
    isFlowTask: boolean;
    tenantId: number;
    tenantName: string;
    parentNode: ITaskNodeProps[];
    childNode: ITaskNodeProps[];
}

interface IRequestParams {
    /**
     * yyyy-MM-dd
     */
    startDay: string;
    endDay: string;
    /**
     * HH:mm
     */
    beginTime?: string;
    endTime?: string;
    fillName: string;
    fillDataInfo: IFillDataInfoProps;
}

interface IFillDataInfoProps {
    /**
     * 批量补数据 fillDataType = 0
     */
    fillDataType: 0;
    rootTaskId: {
        taskId: number;
    };
    taskIds: { taskId: number }[];
}

interface IFormFieldProps {
    fillName: string;
    rangeDate: moment.Moment[];
    concreteStartTime?: moment.Moment;
    concreteEndTime?: moment.Moment;
}

const disabledDate = (current: moment.Moment) => {
    return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
};

/**
 * Update the specific tree node's childNode
 * This function will modify the raw data
 */
const updateTreeNode = (treeNode: ITaskNodeProps[], needUpdateNode: ITaskNodeProps) => {
    const stack = [...treeNode];
    while (stack.length) {
        const firstNode = stack.shift()!;
        if (firstNode.taskId === needUpdateNode.taskId) {
            firstNode.childNode = needUpdateNode.childNode;
            break;
        }
        if (firstNode.childNode.length) {
            stack.push(...firstNode.childNode);
        }
    }
};

export default ({ visible, task, handCancel }: IPatchDataProps) => {
    const { supportJobTypes } = useContext(context);
    const [form] = Form.useForm<IFormFieldProps>();
    const [loading, setLoading] = useState(false);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [selectedAll, setSelectedAll] = useState(false);
    const [selectedRowKeys, setSelectedKeys] = useState<number[]>([]);
    const [expandedRowKeys, setExpanedKeys] = useState<number[]>([]);
    const [treeData, setTreeData] = useState<ITaskNodeProps[]>([]);
    const requestedRow = useRef(new Set());

    const getChildTask = ({ taskId }: { taskId: number }) => {
        setLoading(true);
        getTreeDataSync({ taskId })
            .then((rootNode) => {
                const arr = rootNode ? [rootNode] : [];
                setTreeData(arr);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const getTreeDataSync = async ({ taskId }: { taskId: number }): Promise<ITaskNodeProps | null> => {
        const res = await Api.getTaskChildren({
            taskId,
            directType: DIRECT_TYPE_ENUM.CHILD,
            level: 2,
        });
        if (res.code === 1 && res.data?.rootTaskNode) {
            return res.data?.rootTaskNode;
        }
        return null;
    };

    const showAddResult = (params: { fillId: number; fillName: string }) => {
        handCancel();
        confirm({
            okText: '查看',
            title: '查看补数据结果',
            content: '补数据任务已在执行中，点击下方按钮查看结果',
            onOk() {
                sessionStorage.setItem(
                    'task-patch-data',
                    JSON.stringify({
                        name: params.fillName,
                        id: params.fillId,
                    })
                );
                history.push({
                    query: { drawer: DRAWER_MENU_ENUM.PATCH_DETAIL },
                });
            },
        });
    };

    const getFillDataInfo = () => {
        const info: Partial<IFillDataInfoProps> = {
            fillDataType: 0,
        };
        if (selectedAll) {
            info.rootTaskId = { taskId: task!.taskId };
        } else {
            info.taskIds = selectedRowKeys.map((item) => ({
                taskId: item,
            }));
        }
        return info;
    };

    const addData = () => {
        if (loading) {
            message.warn('加载中，请稍后再试');
            return;
        }
        if (!task) {
            message.error('任务不存在，请稍后再试');
            return;
        }
        if (!selectedRowKeys.length) {
            message.warn('未选择任务');
            return;
        }
        const info = getFillDataInfo();
        form.validateFields().then((values) => {
            setConfirmLoading(true);
            Api.patchTaskData({
                fillName: values.fillName,
                startDay: values.rangeDate[0].format('YYYY-MM-DD'),
                endDay: values.rangeDate[1].format('YYYY-MM-DD'),
                beginTime: values.concreteStartTime?.format('HH:mm'),
                endTime: values.concreteEndTime?.format('HH:mm'),
                fillDataInfo: info,
            } as IRequestParams)
                .then((res) => {
                    if (res.code === 1) {
                        showAddResult({
                            fillId: res.data,
                            fillName: values.fillName,
                        });
                        setTimeout(() => {
                            form.resetFields();
                            setSelectedAll(false);
                        }, 500);
                    }
                })
                .finally(() => {
                    setConfirmLoading(false);
                });
        });
    };

    const resetStatus = () => {
        setSelectedAll(false);
        setSelectedKeys([]);
        setExpanedKeys([]);
        setTreeData([]);
    };

    const cancleModal = () => {
        resetStatus();
        form.resetFields();
        handCancel();
    };

    // get disabled hours
    const disabledHours = (timeType: 'start' | 'end') => {
        const startTime: IFormFieldProps['concreteStartTime'] = form.getFieldValue('concreteStartTime');
        const endTime: IFormFieldProps['concreteEndTime'] = form.getFieldValue('concreteEndTime');
        if (!startTime || !endTime) {
            return [];
        }

        // 开始时间
        const startTimeHour = Number(startTime.format('HH'));
        // 结束时间
        const endTimeHour = Number(endTime.format('HH'));
        const hours = range(0, 60);

        if (timeType === 'start') {
            hours.splice(0, endTimeHour + 1); // 不禁用的小时
            return hours;
        }
        if (timeType === 'end') {
            hours.splice(startTimeHour, 24); // 不禁用的小时
            return hours;
        }
        return [];
    };

    // get disabled minutes
    const disabledMinutes = (timeType: 'start' | 'end') => {
        const startTime: IFormFieldProps['concreteStartTime'] = form.getFieldValue('concreteStartTime');
        const endTime: IFormFieldProps['concreteEndTime'] = form.getFieldValue('concreteEndTime');
        if (!startTime || !endTime) {
            return [];
        }

        // 开始时间
        const startTimeHour = Number(startTime.format('HH'));
        const startTimeMinute = Number(startTime.format('mm'));
        // 结束时间
        const endTimeHour = Number(endTime.format('HH'));
        const endTimeMinute = Number(endTime.format('mm'));

        if (timeType === 'start' && startTimeHour === endTimeHour) {
            return range(endTimeMinute + 1, 60);
        }
        if (timeType === 'end' && startTimeHour === endTimeHour) {
            return range(0, startTimeMinute);
        }
        return [];
    };

    const handleTableSelectAll = (selected: boolean, tableSelectedRows: ITaskNodeProps[]) => {
        setSelectedAll(selected);
        setSelectedKeys(tableSelectedRows.map((row) => row.taskId));
    };

    const handleSelected: TableRowSelection<ITaskNodeProps>['onSelect'] = (_, __, rows) => {
        setSelectedKeys(rows.map((row) => row.taskId));
    };

    const handleExpand = (expanded: boolean, record: ITaskNodeProps) => {
        if (expanded && !requestedRow.current.has(record.taskId)) {
            requestedRow.current.add(record.taskId);
            getTreeDataSync({ taskId: record.taskId }).then((rootNode) => {
                if (rootNode) {
                    const nextTreeData = treeData.concat();
                    updateTreeNode(nextTreeData, rootNode);
                    setTreeData(nextTreeData);
                    if (selectedAll) {
                        const nextSelectedKeys = selectedRowKeys.concat();
                        const { childNode = [] } = rootNode;
                        nextSelectedKeys.push(...childNode.map((n) => n.taskId));
                        setSelectedKeys(nextSelectedKeys);
                    }
                }
            });
        }
    };

    const handleExpandRowsChange = (expandedKeys: readonly React.Key[]) => {
        setExpanedKeys(expandedKeys as number[]);
    };

    useEffect(() => {
        if (visible && task) {
            setSelectedKeys([task.taskId]);
            setExpanedKeys([]);
            setTreeData([]);
            getChildTask({ taskId: task.taskId });
            form.setFieldsValue({
                fillName: `P_${task.name}_${moment().format('YYYY_MM_DD_mm_ss')}`,
                rangeDate: [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            });
        }
    }, [visible, task]);

    const columns: ColumnsType<any> = [
        {
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
            render: (text) => {
                let nameVal = text;
                if (nameVal && nameVal.length > 10) {
                    nameVal = text.substring(0, 10);
                }
                return (
                    <Tooltip placement="topLeft" title={text}>
                        {nameVal === text ? nameVal : `${nameVal}...`}
                    </Tooltip>
                );
            },
        },
        {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text) => supportJobTypes.find((t) => t.key === text)?.value || '未知',
        },
        {
            title: '所属租户',
            dataIndex: 'tenantName',
            key: 'tenantName',
        },
    ];

    const rowSelection: TableRowSelection<ITaskNodeProps> = {
        selectedRowKeys,
        onSelect: handleSelected,
        onSelectAll: handleTableSelectAll,
    };

    return (
        <Modal
            title="补数据"
            okText="运行选中任务"
            visible={visible}
            width={650}
            onOk={addData}
            onCancel={cancleModal}
            confirmLoading={confirmLoading}
            destroyOnClose
        >
            <Form {...formItemLayout} preserve={false} form={form}>
                <FormItem
                    name="fillName"
                    label="补数据名"
                    rules={[
                        {
                            required: true,
                            message: '请输入补数据名!',
                        },
                        {
                            pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
                            message: '补数据名称只能由字母、数字、中文、下划线组成!',
                        },
                        {
                            max: 128,
                            message: '补数据名称不得超过128个字符！',
                        },
                    ]}
                >
                    <Input placeholder="请输入补数据名" />
                </FormItem>
                <FormItem
                    name="rangeDate"
                    label="计划日期"
                    rules={[
                        {
                            type: 'array',
                            required: true,
                            message: '请选择计划时间!',
                        },
                    ]}
                >
                    <RangePicker disabledDate={disabledDate} format="YYYY-MM-DD" style={{ width: '100%' }} />
                </FormItem>
                <FormItem
                    wrapperCol={{
                        offset: formItemLayout.labelCol.sm.span,
                        span: 16,
                    }}
                >
                    <Space align="center">
                        <FormItem name="hasMinute" noStyle valuePropName="checked">
                            <Checkbox>选择分钟粒度</Checkbox>
                        </FormItem>
                        <Tooltip
                            placement="top"
                            title="产生指定的业务日期内，指定的时间范围内计划开始运行的实例，例如：
                            业务日期：2019-01-01~2019-01-03
                            具体时间：01:30~03:00
                            表示：2019-01-01~2019-01-03期间内，每天的01:30~03:00开始运行的实例，时间范围为闭区间，时间范围选择了23:59后，计划23:59开始运行的实例也会产生 支持将数值类型、Timestamp类型作为增量标识字段
                            选择分钟粒度后，补数据时，跨周期依赖配置无效"
                        >
                            <QuestionCircleOutlined />
                        </Tooltip>
                    </Space>
                </FormItem>
                <FormItem
                    noStyle
                    shouldUpdate={(prevValues, currentValues) => prevValues.hasMinute !== currentValues.hasMinute}
                >
                    {({ getFieldValue }) =>
                        getFieldValue('hasMinute') ? (
                            <FormItem label="具体时间" required>
                                <Row>
                                    <Col span={11}>
                                        <FormItem
                                            name="concreteStartTime"
                                            initialValue={moment('00:00', format)}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: '请选择具体时间!',
                                                },
                                            ]}
                                        >
                                            <TimePicker
                                                format={format}
                                                style={{ width: '100%' }}
                                                allowClear={false}
                                                disabledHours={() => disabledHours('start')}
                                                disabledMinutes={() => disabledMinutes('start')}
                                            />
                                        </FormItem>
                                    </Col>
                                    <Col span={2}>
                                        <span
                                            style={{
                                                display: 'inline-block',
                                                width: '100%',
                                                textAlign: 'center',
                                            }}
                                        >
                                            -
                                        </span>
                                    </Col>
                                    <Col span={11}>
                                        <FormItem
                                            name="concreteEndTime"
                                            initialValue={moment('23:59', format)}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: '请选择具体时间!',
                                                },
                                            ]}
                                        >
                                            <TimePicker
                                                format="HH:mm"
                                                style={{ width: '100%' }}
                                                allowClear={false}
                                                disabledHours={() => disabledHours('end')}
                                                disabledMinutes={() => disabledMinutes('end')}
                                            />
                                        </FormItem>
                                    </Col>
                                </Row>
                            </FormItem>
                        ) : null
                    }
                </FormItem>
            </Form>
            <Table<ITaskNodeProps>
                loading={loading}
                columns={columns}
                rowKey="taskId"
                expandable={{
                    expandedRowKeys,
                    onExpand: handleExpand,
                    onExpandedRowsChange: handleExpandRowsChange,
                    childrenColumnName: 'childNode',
                }}
                rowSelection={rowSelection}
                dataSource={treeData}
                pagination={false}
                footer={() =>
                    selectedAll ? (
                        <span>
                            已选择<a>全部</a>
                        </span>
                    ) : (
                        <span>
                            共<a>{selectedRowKeys.length}</a>个任务
                        </span>
                    )
                }
            />
        </Modal>
    );
};
