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

import { useEffect, useState } from 'react';
import { Alert, Checkbox, Col, DatePicker, Form, message,Modal, Radio, Row, Select } from 'antd';
import type { CheckboxChangeEvent } from 'antd/lib/checkbox';
import type { CheckboxValueType } from 'antd/lib/checkbox/Group';
import moment from 'moment';

import Api from '@/api';
import { formItemLayout } from '@/constant';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { RangePicker } = DatePicker;
const CheckboxGroup = Checkbox.Group;
const { Option } = Select;

interface IKillJobFormProps {
    visible: boolean;
    onCancel: () => void;
    autoFresh: () => void;
}

interface ITaskProps {
    name: string;
    taskId: number;
}

const YESTERDAY_DATE = moment().subtract(1, 'days');
const PARAMS = {
    currentPage: 1,
    pageSize: 200,
    searchType: 'front',
};

const disabledDate = (current: moment.Moment) => {
    return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
};

const SCHEDULING_OPTIONS = [
    {
        label: '天',
        value: 2,
    },
    {
        label: '周',
        value: 3,
    },
    {
        label: '月',
        value: 4,
    },
];

export default ({ visible, onCancel, autoFresh }: IKillJobFormProps) => {
    const [form] = Form.useForm();
    const [submitLoading, setLoading] = useState(false);
    const [taskList, setTaskList] = useState<ITaskProps[]>([]);
    const [checkAll, setCheckAll] = useState(false);
    const [indeterminate, setIndeterminate] = useState(false);

    const searchTask = async (value?: string) => {
        if (!value) return;
        const reg = new RegExp(/^[\u4E00-\u9FA5A-Za-z0-9_]+$/);
        const reqParam = { ...PARAMS };
        if (reg.exec(value) === null) return;
        const params = value ? { name: value, ...reqParam } : reqParam;
        const res = await Api.queryOfflineTasks(params);
        if (!res) {
            setTaskList([]);
        }
        setTaskList(res?.data?.data || []);
    };

    // 调度周期全选触发函数
    const handleSelectAll = (e: CheckboxChangeEvent) => {
        const { checked } = e.target;
        form.setFieldsValue({
            schedulingCycle: checked ? SCHEDULING_OPTIONS.map((o) => o.value) : [],
        });
        setCheckAll(checked);
        setIndeterminate(false);
    };

    // 调度周期
    const handleSchedulingChange = (checkedList: CheckboxValueType[]) => {
        const nextIndeterminate = !!checkedList.length && checkedList.length < SCHEDULING_OPTIONS.length;
        const nextCheckAll = checkedList.length === SCHEDULING_OPTIONS.length;
        setIndeterminate(nextIndeterminate);
        setCheckAll(nextCheckAll);
    };

    const handleSubmit = () => {
        form.validateFields().then((values) => {
            const type = values.select;
            const { taskIds } = values;
            if (type === 2 && (!taskIds || (Array.isArray(taskIds) && taskIds.length === 0))) {
                return message.error('请选择任务');
            }
            setLoading(true);
            Api.batchStopJobByDate({
                type: 0,
                taskIds: values.taskIds || undefined,
                taskPeriodId: values.schedulingCycle ? values.schedulingCycle.join(',') : undefined,
                bizStartDay: values.businessDate ? values.businessDate[0].unix() : undefined,
                bizEndDay: values.businessDate ? values.businessDate[1].unix() : undefined,
            })
                .then((res) => {
                    if (res.code === 1) {
                        message.success(`取消了${res.data}个任务`);
                        autoFresh();
                        handleCancel();
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        });
    };

    const handleCancel = () => {
        setIndeterminate(false);
        setCheckAll(false);
        form.resetFields();
        onCancel();
    };

    useEffect(() => {
        searchTask();
    }, []);

    return (
        <Modal
            title="按业务日期杀实例"
            confirmLoading={submitLoading}
            visible={visible}
            width={650}
            onOk={handleSubmit}
            onCancel={handleCancel}
            okText="杀任务"
        >
            <Alert
                description={
                    <span style={{ fontSize: 12 }}>
                        根据业务日期和调度周期来快速筛选大量实例，例如选择业务日期在2018-01-01~2018-01-20的分钟任务实例
                    </span>
                }
                message=""
                type="info"
                showIcon
                style={{
                    marginBottom: 20,
                }}
            />
            <Form
                form={form}
                initialValues={{
                    select: 1,
                }}
            >
                <FormItem
                    {...formItemLayout}
                    name="select"
                    label="选择任务"
                    rules={[
                        {
                            required: true,
                            message: '请选择任务',
                        },
                    ]}
                >
                    <RadioGroup>
                        <Radio value={1}>全部任务</Radio>
                        <Radio value={2}>指定任务</Radio>
                    </RadioGroup>
                </FormItem>
                <FormItem
                    noStyle
                    shouldUpdate={(prevValues, currentValues) => prevValues.select !== currentValues.select}
                >
                    {({ getFieldValue }) =>
                        getFieldValue('select') === 1 ? (
                            <>
                                <FormItem
                                    {...formItemLayout}
                                    name="businessDate"
                                    label="业务日期"
                                    rules={[
                                        {
                                            required: true,
                                            message: '请输入业务日期',
                                        },
                                    ]}
                                >
                                    <RangePicker
                                        size="middle"
                                        format="YYYY-MM-DD"
                                        allowClear={false}
                                        disabledDate={disabledDate}
                                        ranges={{
                                            昨天: [moment().subtract(2, 'days'), YESTERDAY_DATE],
                                            最近7天: [moment().subtract(8, 'days'), YESTERDAY_DATE],
                                            最近30天: [moment().subtract(31, 'days'), YESTERDAY_DATE],
                                        }}
                                    />
                                </FormItem>
                                <FormItem {...formItemLayout} label="调度周期" required>
                                    <Row>
                                        <Col span={4}>
                                            <Checkbox
                                                checked={checkAll}
                                                indeterminate={indeterminate}
                                                onChange={handleSelectAll}
                                            >
                                                全选
                                            </Checkbox>
                                        </Col>
                                        <Col span={20}>
                                            <FormItem
                                                noStyle
                                                name="schedulingCycle"
                                                rules={[
                                                    {
                                                        required: true,
                                                        message: '请选择调度周期',
                                                    },
                                                ]}
                                            >
                                                <CheckboxGroup
                                                    options={SCHEDULING_OPTIONS}
                                                    onChange={handleSchedulingChange}
                                                />
                                            </FormItem>
                                        </Col>
                                    </Row>
                                </FormItem>
                            </>
                        ) : (
                            <>
                                <FormItem {...formItemLayout} label={<></>} colon={false} name="taskIds">
                                    <Select
                                        filterOption={false}
                                        onSearch={searchTask}
                                        style={{ width: 286 }}
                                        mode="multiple"
                                        placeholder="输入任务名称搜索，可添加多个任务"
                                    >
                                        {taskList.map((item) => {
                                            return (
                                                <Option key={item.taskId} value={item.taskId}>
                                                    {item.name}
                                                </Option>
                                            );
                                        })}
                                    </Select>
                                </FormItem>
                                <FormItem
                                    {...formItemLayout}
                                    label="业务日期"
                                    name="businessDate"
                                    rules={[
                                        {
                                            required: true,
                                            message: '请输入业务日期',
                                        },
                                    ]}
                                >
                                    <RangePicker
                                        size="middle"
                                        format="YYYY-MM-DD"
                                        allowClear={false}
                                        disabledDate={disabledDate}
                                        ranges={{
                                            昨天: [moment().subtract(2, 'days'), YESTERDAY_DATE],
                                            最近7天: [moment().subtract(8, 'days'), YESTERDAY_DATE],
                                            最近30天: [moment().subtract(31, 'days'), YESTERDAY_DATE],
                                        }}
                                    />
                                </FormItem>
                            </>
                        )
                    }
                </FormItem>
            </Form>
        </Modal>
    );
};
