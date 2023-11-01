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

import { forwardRef, useImperativeHandle } from 'react';
import type { CheckboxProps,SelectProps } from 'antd';
import { Checkbox, DatePicker, Form, Input,Select } from 'antd';
import get from 'lodash/get';
import moment from 'moment';

import { SCHEDULE_STATUS, scheduleConfigLayout, TASK_PERIOD_ENUM } from '@/constant';
import type { IScheduleConfProps } from '@/interface';

const { Option } = Select;
const FormItem = Form.Item;

interface IFormWrapProps {
    scheduleConf: Partial<IScheduleConfProps>;
    status: SCHEDULE_STATUS;
    /**
     * 是否为工作流任务
     */
    isWorkflowRoot?: boolean;
    /**
     * 是否为工作流任务的子任务
     */
    isWorkflowNode?: boolean;
    /**
     * 调度配置发生修改的回调函数
     */
    handleScheduleConf: () => void;
    /**
     * 调度状态项发生修改的回调函数
     */
    handleScheduleStatus: CheckboxProps['onChange'];
    /**
     * 调度周期发生修改的回调函数
     */
    handleScheduleType: SelectProps<string>['onChange'];
}

/**
 * 小时默认下拉选项，0 -> 23
 */
const HOURS_OPTIONS = new Array(24)
    .fill(1)
    .map((_, i) => ({ label: i < 10 ? `0${i}` : i.toString(), value: i.toString() }));
/**
 * 分钟默认下拉选择, 00 -> 59
 */
const MINS_OPTIONS = new Array(60)
    .fill(1)
    .map((_, i) => ({ label: i < 10 ? `0${i}` : i.toString(), value: i.toString() }));
/**
 * 分钟间隔默认下拉。5分钟，10分钟..., 55分钟
 */
const GAP_OPTIONS = new Array(11).fill(1).map((_, i) => ({
    label: `${(i + 1) * 5}分钟`,
    value: ((i + 1) * 5).toString(),
}));
/**
 * 小时间隔默认下拉，1小时，2小时...,23小时
 */
const GAP_HOUR_OPTIONS = new Array(24).fill(1).map((_, i) => ({
    label: `${i + 1}小时`,
    value: (i + 1).toString(),
}));
/**
 * 星期默认下拉选项
 */
const WEEKS_OPTIONS = ['天', '一', '二', '三', '四', '五', '六'].map((day, index) => ({
    label: `星期${day}`,
    value: (index + 1).toString(),
}));
/**
 * 日默认下拉选项，1 -> 30
 */
const DAYS_OPTIONS = new Array(30).fill(1).map((_, i) => ({
    label: `每月${i + 1}号`,
    value: (i + 1).toString(),
}));
/**
 * 重试次数下拉选项，1 -> 5
 */
const RETRY_OPTIONS = new Array(5).fill(1).map((_, i) => ({
    label: i + 1,
    value: (i + 1).toString(),
}));

export default forwardRef(
    (
        {
            scheduleConf,
            status,
            isWorkflowRoot,
            isWorkflowNode,
            handleScheduleStatus,
            handleScheduleConf,
            handleScheduleType,
        }: IFormWrapProps,
        ref
    ) => {
        const [form] = Form.useForm();
        const {
            periodType,
            beginDate,
            beginHour,
            beginMin,
            endHour,
            endMin,
            endDate,
            // isLastInstance,
            gapMin,
            weekDay,
            hour,
            min,
            day,
            gapHour,
        } = scheduleConf;

        useImperativeHandle(ref, () => ({
            ...form,
        }));

        const changeStartDisabledDate = (currentDate: moment.Moment) => {
            const date = form.getFieldValue('endDate');
            return date && currentDate.valueOf() > date;
        };

        const changeEndDisabledDate = (currentDate: moment.Moment) => {
            const date = form.getFieldValue('beginDate');
            return date && currentDate.valueOf() < date;
        };

        const checkTimeS1 = () => {
            const currentBeginHour = +form.getFieldValue('beginHour');
            const currentBeginMin = +form.getFieldValue('beginMin');
            const currentEndHour = +form.getFieldValue('endHour') * 60 + 59;

            if (currentBeginHour * 60 + currentBeginMin > currentEndHour) {
                return Promise.reject(new Error('开始时间不能晚于结束时间'));
            }
            return Promise.resolve();
        };

        const checkTimeE1 = () => {
            const currentBeginHour = +form.getFieldValue('beginHour');
            const currentBeginMin = +form.getFieldValue('beginMin');
            const currentEndHour = +form.getFieldValue('endHour') * 60 + 59;

            if (currentBeginHour * 60 + currentBeginMin > currentEndHour) {
                return Promise.reject(new Error('结束时间不能早于开始时间'));
            }
            return Promise.resolve();
        };

        const renderTimeConfig = (type: number) => {
            switch (type) {
                case TASK_PERIOD_ENUM.MINUTE: {
                    return (
                        <span key={type}>
                            <FormItem {...scheduleConfigLayout} label="开始时间" required>
                                <FormItem
                                    noStyle
                                    name="beginHour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeS1,
                                        },
                                    ]}
                                    initialValue={`${beginHour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="beginMin"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeS1,
                                        },
                                    ]}
                                    initialValue={`${beginMin || '0'}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                            <FormItem
                                {...scheduleConfigLayout}
                                label="间隔时间"
                                name="gapMin"
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                                initialValue={`${gapMin}`}
                            >
                                <Select disabled={isWorkflowNode} onChange={handleScheduleConf} options={GAP_OPTIONS} />
                            </FormItem>
                            <FormItem {...scheduleConfigLayout} label="结束时间" required>
                                <FormItem
                                    noStyle
                                    name="endHour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeE1,
                                        },
                                    ]}
                                    initialValue={`${endHour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="endMin"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeE1,
                                        },
                                    ]}
                                    initialValue={`${endMin || '59'}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                        </span>
                    );
                }
                case TASK_PERIOD_ENUM.HOUR: {
                    return (
                        <span key={type}>
                            <FormItem {...scheduleConfigLayout} label="开始时间" required>
                                <FormItem
                                    noStyle
                                    name="beginHour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeS1,
                                        },
                                    ]}
                                    initialValue={`${beginHour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="beginMin"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeS1,
                                        },
                                    ]}
                                    initialValue={`${beginMin || '0'}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                            <FormItem
                                {...scheduleConfigLayout}
                                label="间隔时间"
                                name="gapHour"
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                                initialValue={`${gapHour}`}
                            >
                                <Select
                                    disabled={isWorkflowNode}
                                    onChange={handleScheduleConf}
                                    options={GAP_HOUR_OPTIONS}
                                />
                            </FormItem>
                            <FormItem {...scheduleConfigLayout} label="结束时间" required>
                                <FormItem
                                    noStyle
                                    name="endHour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeE1,
                                        },
                                    ]}
                                    initialValue={`${endHour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="endMin"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                        {
                                            validator: checkTimeE1,
                                        },
                                    ]}
                                    initialValue={`${endMin || '59'}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                        </span>
                    );
                }
                case TASK_PERIOD_ENUM.DAY: {
                    const prefix = isWorkflowNode ? '起调' : '具体';
                    return (
                        <span key={type}>
                            <FormItem
                                {...scheduleConfigLayout}
                                label={`${prefix}时间`}
                                required
                                tooltip={isWorkflowNode ? `工作流子任务无法修改${prefix}时间` : ''}
                            >
                                <FormItem
                                    noStyle
                                    name="hour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${hour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="min"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${min}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                        </span>
                    );
                }
                case TASK_PERIOD_ENUM.WEEK: {
                    return (
                        <span key={type}>
                            <FormItem
                                {...scheduleConfigLayout}
                                label="选择时间"
                                name="weekDay"
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                                initialValue={`${weekDay}`.split(',')}
                            >
                                <Select
                                    mode="multiple"
                                    style={{ width: '100%' }}
                                    disabled={isWorkflowNode}
                                    onChange={handleScheduleConf}
                                    options={WEEKS_OPTIONS}
                                />
                            </FormItem>
                            <FormItem {...scheduleConfigLayout} label="具体时间" required>
                                <FormItem
                                    noStyle
                                    name="hour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${hour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="min"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${min}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                        </span>
                    );
                }
                case TASK_PERIOD_ENUM.MONTH: {
                    return (
                        <span key={type}>
                            <FormItem
                                {...scheduleConfigLayout}
                                label="选择时间"
                                name="day"
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                                initialValue={`${day}`.split(',')}
                            >
                                <Select
                                    mode="multiple"
                                    style={{ width: '100%' }}
                                    disabled={isWorkflowNode}
                                    onChange={handleScheduleConf}
                                    options={DAYS_OPTIONS}
                                />
                            </FormItem>
                            <FormItem {...scheduleConfigLayout} label="具体时间" required>
                                <FormItem
                                    noStyle
                                    name="hour"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${hour}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={HOURS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="mx-5px">时</span>
                                <FormItem
                                    noStyle
                                    name="min"
                                    rules={[
                                        {
                                            required: true,
                                        },
                                    ]}
                                    initialValue={`${min}`}
                                >
                                    <Select
                                        disabled={isWorkflowNode}
                                        style={{ width: '40%' }}
                                        onChange={handleScheduleConf}
                                        options={MINS_OPTIONS}
                                    />
                                </FormItem>
                                <span className="ml-5px">分</span>
                            </FormItem>
                        </span>
                    );
                }

                default:
                    return <span>something wrong</span>;
            }
        };

        return (
            <Form
                form={form}
                className="schedule-form"
                initialValues={{
                    scheduleStatus: status === SCHEDULE_STATUS.FORZON || status === SCHEDULE_STATUS.STOPPED,
                    selfReliance: scheduleConf.selfReliance,
                }}
            >
                <FormItem {...scheduleConfigLayout} label="调度状态" name="scheduleStatus" valuePropName="checked">
                    <Checkbox onChange={handleScheduleStatus}>冻结</Checkbox>
                </FormItem>
                {!isWorkflowRoot && (
                    <>
                        <FormItem
                            {...scheduleConfigLayout}
                            label="出错重试"
                            name="isFailRetry"
                            initialValue={get(scheduleConf, 'isFailRetry')}
                            valuePropName="checked"
                        >
                            <Checkbox onChange={handleScheduleConf}>是</Checkbox>
                        </FormItem>
                        <FormItem noStyle dependencies={['isFailRetry']}>
                            {({ getFieldValue }) =>
                                getFieldValue('isFailRetry') && (
                                    <FormItem {...scheduleConfigLayout} label="重试次数" required>
                                        <FormItem
                                            noStyle
                                            name="maxRetryNum"
                                            rules={[
                                                {
                                                    required: true,
                                                    message: '请选择重试次数',
                                                },
                                            ]}
                                            initialValue={get(scheduleConf, 'maxRetryNum', 3)}
                                        >
                                            <Select
                                                style={{
                                                    display: 'inline-block',
                                                    width: 70,
                                                }}
                                                onChange={handleScheduleConf}
                                                options={RETRY_OPTIONS}
                                            />
                                        </FormItem>
                                        <span className="ml-5px">次，每次间隔2分钟</span>
                                    </FormItem>
                                )
                            }
                        </FormItem>
                    </>
                )}
                <FormItem {...scheduleConfigLayout} label="生效日期" required>
                    <FormItem
                        name="beginDate"
                        noStyle
                        initialValue={moment(beginDate)}
                        rules={[
                            {
                                required: true,
                                message: '请选择生效日期开始时间',
                            },
                        ]}
                    >
                        <DatePicker
                            allowClear={false}
                            disabledDate={changeStartDisabledDate}
                            disabled={isWorkflowNode}
                            style={{ width: 115 }}
                            onChange={handleScheduleConf}
                        />
                    </FormItem>
                    <span className="mx-5px">-</span>
                    <FormItem
                        noStyle
                        name="endDate"
                        initialValue={moment(endDate)}
                        rules={[
                            {
                                required: true,
                                message: '请选择生效日期结束时间',
                            },
                        ]}
                    >
                        <DatePicker
                            allowClear={false}
                            disabled={isWorkflowNode}
                            disabledDate={changeEndDisabledDate}
                            style={{ width: 115 }}
                            onChange={handleScheduleConf}
                        />
                    </FormItem>
                </FormItem>
                <FormItem
                    {...scheduleConfigLayout}
                    label="调度周期"
                    name="periodType"
                    initialValue={`${periodType}`}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Select disabled={isWorkflowNode} onChange={handleScheduleType}>
                        <Option key={0} value={TASK_PERIOD_ENUM.MINUTE.toString()}>
                            分钟
                        </Option>
                        <Option key={1} value={TASK_PERIOD_ENUM.HOUR.toString()}>
                            小时
                        </Option>
                        <Option key={2} value={TASK_PERIOD_ENUM.DAY.toString()}>
                            天
                        </Option>
                        <Option key={3} value={TASK_PERIOD_ENUM.WEEK.toString()}>
                            周
                        </Option>
                        <Option key={4} value={TASK_PERIOD_ENUM.MONTH.toString()}>
                            月
                        </Option>
                    </Select>
                </FormItem>
                <FormItem noStyle name="selfReliance">
                    <Input type="hidden" />
                </FormItem>
                <FormItem dependencies={['periodType']} noStyle>
                    {({ getFieldValue }) => renderTimeConfig(Number(getFieldValue('periodType')))}
                </FormItem>
            </Form>
        );
    }
);
