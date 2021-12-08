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

import * as React from 'react';
import moment from 'moment';
import get from 'lodash/get';

import { Col, Checkbox, Form, DatePicker, Select, Input, Radio } from 'antd';
import HelpDoc, { relativeStyle } from '../../../components/helpDoc';

const Group = Radio.Group;
const Option = Select.Option;
const FormItem = Form.Item;

const formItemLayout: any = {
    // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 17 },
    },
};

class ScheduleForm extends React.Component<any, any> {

    changeEndDisabledDate = (currentDate: any) => {
        const { form } = this.props;
        const date = form.getFieldValue('beginDate');
        return date && currentDate.valueOf() < date;
    };

    changeStartDisabledDate = (currentDate: any) => {
        const { form } = this.props;
        const date = form.getFieldValue('endDate');
        return date && currentDate.valueOf() > date;
    };

    render() {
        const {
            status,
            scheduleConf,
            isWorkflowNode,
            isWorkflowRoot,
            isScienceTask,
            form: { getFieldDecorator },
            handleScheduleStatus,
            handleScheduleConf,
            handleScheduleType
        } = this.props;
        const { beginDate, endDate } = scheduleConf;
        const { periodType, isFailRetry } = scheduleConf;
        // 调度周期为小时或者分钟
        const isHourOrMinTask =
            scheduleConf &&
            (scheduleConf.periodType === '0' ||
                scheduleConf.periodType === '1');
        const generateHours = () => {
            const options: any = [];
            for (let i = 0; i <= 23; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {i < 10 ? `0${i}` : i}
                    </Option>
                );
            }
            return (
                <Select
                    onChange={handleScheduleConf}
                >
                    {options}
                </Select>
            );
        };
        const generateMins = () => {
            const options: any = [];
            for (let i = 0, l = 59; i <= l; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {i < 10 ? `0${i}` : i}
                    </Option>
                );
            }
            return (
                <Select
                    onChange={handleScheduleConf}
                >
                    {options}
                </Select>
            );
        };
        const generateDate = () => {
            const options: any = [];
            for (let i = 1; i <= 31; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>{`每月${i}号`}</Option>
                );
            }
            return (
                <Select
                    mode="multiple"
                    style={{ width: '100%' }}
                    disabled={isScienceTask}
                    onChange={handleScheduleConf}
                >
                    {options}
                </Select>
            );
        };
        const generateDays = () => {
            return (
                <Select
                    mode="multiple"
                    style={{ width: '100%' }}
                    disabled={isScienceTask}
                    onChange={handleScheduleConf}
                >
                    <Option key={1} value="1">
                        星期一
                    </Option>
                    <Option key={2} value="2">
                        星期二
                    </Option>
                    <Option key={3} value="3">
                        星期三
                    </Option>
                    <Option key={4} value="4">
                        星期四
                    </Option>
                    <Option key={5} value="5">
                        星期五
                    </Option>
                    <Option key={6} value="6">
                        星期六
                    </Option>
                    <Option key={7} value="7">
                        星期天
                    </Option>
                </Select>
            );
        };
        return (
            <Form key={periodType} className="schedule-form">
                <FormItem {...formItemLayout} label="调度状态">
                    {getFieldDecorator('scheduleStatus', {
                        valuePropName: 'checked',
                        initialValue: status === 0 || status === 2,
                    })(
                        <Checkbox
                            disabled={isScienceTask}
                            onChange={handleScheduleStatus}
                        >
                            冻结
                        </Checkbox>
                    )}
                </FormItem>
                {!isWorkflowRoot && (
                    <React.Fragment>
                        <FormItem {...formItemLayout} label="出错重试">
                            {getFieldDecorator('isFailRetry', {
                                valuePropName: 'checked',
                                initialValue: get(scheduleConf, 'isFailRetry'),
                            })(
                                <Checkbox
                                    disabled={isScienceTask}
                                    onChange={handleScheduleConf}
                                >
                                    是
                                </Checkbox>
                            )}
                        </FormItem>
                        {isFailRetry && (
                            <FormItem {...formItemLayout} label="重试次数">
                                <Col span={6}>
                                    {getFieldDecorator('maxRetryNum', {
                                        rules: [
                                            {
                                                required: true,
                                                message: '请选择重试次数',
                                            },
                                        ],
                                        initialValue: get(
                                            scheduleConf,
                                            'maxRetryNum',
                                            3
                                        ),
                                    })(
                                        <Select
                                            disabled={isScienceTask}
                                            onChange={handleScheduleConf}
                                        >
                                            <Option key="1" value="1">
                                                1
                                            </Option>
                                            <Option key="2" value="2">
                                                2
                                            </Option>
                                            <Option key="3" value="3">
                                                3
                                            </Option>
                                            <Option key="4" value="4">
                                                4
                                            </Option>
                                            <Option key="5" value="5">
                                                5
                                            </Option>
                                        </Select>
                                    )}
                                </Col>
                                <span className="split-text">
                                    次，每次间隔2分钟
                                </span>
                            </FormItem>
                        )}
                    </React.Fragment>
                )}
                {!isWorkflowNode && (
                    <div>
                        <FormItem {...formItemLayout} label="生效日期">
                            {getFieldDecorator('beginDate', {
                                initialValue: moment(beginDate),
                                rules: [
                                    {
                                        required: true,
                                        message: '请选择生效日期开始时间',
                                    },
                                ],
                            })(
                                <DatePicker
                                    allowClear={false}
                                    disabledDate={this.changeStartDisabledDate}
                                    disabled={isScienceTask}
                                    style={{ width: '140px' }}
                                    onChange={handleScheduleConf}
                                />
                            )}
                            <span
                                className="split-text"
                                style={{ float: 'none' }}
                            >
                                -
                            </span>
                            {getFieldDecorator('endDate', {
                                initialValue: moment(endDate),
                                rules: [
                                    {
                                        required: true,
                                        message: '请选择生效日期结束时间',
                                    },
                                ],
                            })(
                                <DatePicker
                                    allowClear={false}
                                    disabled={isScienceTask}
                                    disabledDate={this.changeEndDisabledDate}
                                    style={{ width: '140px' }}
                                    onChange={handleScheduleConf}
                                />
                            )}
                        </FormItem>
                        <FormItem {...formItemLayout} label="调度周期">
                            <Col span={6}>
                                {getFieldDecorator('periodType', {
                                    initialValue: `${scheduleConf?.periodType}`,
                                    rules: [
                                        {
                                            required: true,
                                        },
                                    ],
                                })(
                                    <Select
                                        disabled={isScienceTask}
                                        onChange={handleScheduleType}
                                    >
                                        <Option key={0} value="0">
                                            分钟
                                        </Option>
                                        <Option key={1} value="1">
                                            小时
                                        </Option>
                                        <Option key={2} value="2">
                                            天
                                        </Option>
                                        <Option key={3} value="3">
                                            周
                                        </Option>
                                        <Option key={4} value="4">
                                            月
                                        </Option>
                                    </Select>
                                )}
                            </Col>
                        </FormItem>
                    </div>
                )}
                <FormItem style={{ display: 'none' }}>
                    {getFieldDecorator('selfReliance', {
                        initialValue: scheduleConf.selfReliance,
                    })(<Input disabled={isScienceTask} type="hidden"></Input>)}
                </FormItem>
                {(function (type: any, ctx: any) {
                    let dom: any;
                    switch (type) {
                        case 0: {
                            // 分钟
                            dom = (
                                <span key={type}>
                                    <FormItem
                                        {...formItemLayout}
                                        label="开始时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('beginHour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeS1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.beginHour}`,
                                            })(generateHours())}
                                        </Col>

                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('beginMin', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeS1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${
                                                    scheduleConf.beginMin || '0'
                                                }`,
                                            })(generateMins())}
                                        </Col>

                                        <span className="split-text">分</span>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="间隔时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('gapMin', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.gapMin}`,
                                            })(
                                                <Select
                                                    disabled={isScienceTask}
                                                    onChange={handleScheduleConf}
                                                >
                                                    {(function () {
                                                        const options: any = [];
                                                        for (
                                                            let i = 5;
                                                            i <= 55;
                                                            i += 5
                                                        ) {
                                                            options.push(
                                                                <Option
                                                                    key={i}
                                                                    value={`${i}`}
                                                                >
                                                                    {i}分钟
                                                                </Option>
                                                            );
                                                        }
                                                        return options;
                                                    })()}
                                                </Select>
                                            )}
                                        </Col>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="结束时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('endHour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeE1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.endHour}`,
                                            })(generateHours())}
                                        </Col>
                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('endMin', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeE1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${
                                                    scheduleConf.endMin || '59'
                                                }`,
                                            })(generateMins())}
                                        </Col>
                                        <span className="split-text">分</span>
                                    </FormItem>
                                </span>
                            );
                            break;
                        }
                        case 1: {
                            // 小时
                            dom = (
                                <span key={type}>
                                    <FormItem
                                        {...formItemLayout}
                                        label="开始时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('beginHour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeS1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.beginHour}`,
                                            })(generateHours())}
                                        </Col>

                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('beginMin', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeS1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.beginMin}`,
                                            })(generateMins())}
                                        </Col>

                                        <span className="split-text">分</span>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="间隔时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('gapHour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.gapHour}`,
                                            })(
                                                <Select
                                                    onChange={handleScheduleConf}
                                                    disabled={isScienceTask}
                                                >
                                                    {(function () {
                                                        const options: any = [];
                                                        for (
                                                            let i = 1, l = 23;
                                                            i <= l;
                                                            i++
                                                        ) {
                                                            options.push(
                                                                <Option
                                                                    key={i}
                                                                    value={`${i}`}
                                                                >
                                                                    {i}小时
                                                                </Option>
                                                            );
                                                        }
                                                        return options;
                                                    })()}
                                                </Select>
                                            )}
                                        </Col>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="结束时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('endHour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                    {
                                                        validator:
                                                            ctx.checkTimeE1.bind(
                                                                ctx
                                                            ),
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.endHour}`,
                                            })(generateHours())}
                                        </Col>
                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('endMin', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: '59',
                                            })(
                                                <Select disabled>
                                                    <Option value="59">
                                                        59
                                                    </Option>
                                                </Select>
                                            )}
                                        </Col>
                                        <span className="split-text">分</span>
                                    </FormItem>
                                </span>
                            );
                            break;
                        }
                        case 2: {
                            // 天
                            const prefix = isWorkflowNode ? '起调' : '具体';
                            dom = (
                                <span key={type}>
                                    <FormItem
                                        {...formItemLayout}
                                        label={`${prefix}时间`}
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('hour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.hour}`,
                                            })(generateHours())}
                                        </Col>
                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('min', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.min}`,
                                            })(generateMins())}
                                        </Col>
                                        <span className="split-text">分</span>
                                    </FormItem>
                                </span>
                            );
                            break;
                        }
                        case 3: {
                            // 周
                            dom = (
                                <span key={type}>
                                    <FormItem
                                        {...formItemLayout}
                                        label="选择时间"
                                    >
                                        <Col span={13}>
                                            {getFieldDecorator('weekDay', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue:
                                                    `${scheduleConf.weekDay}`.split(
                                                        ','
                                                    ),
                                            })(generateDays())}
                                        </Col>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="具体时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('hour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.hour}`,
                                            })(generateHours())}
                                        </Col>

                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('min', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.min}`,
                                            })(generateMins())}
                                        </Col>

                                        <span className="split-text">分</span>
                                    </FormItem>
                                </span>
                            );
                            break;
                        }
                        case 4: {
                            // 月
                            dom = (
                                <span key={type}>
                                    <FormItem
                                        {...formItemLayout}
                                        label="选择时间"
                                    >
                                        <Col span={13}>
                                            {getFieldDecorator('day', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue:
                                                    `${scheduleConf.day}`.split(
                                                        ','
                                                    ),
                                            })(generateDate())}
                                        </Col>
                                    </FormItem>
                                    <FormItem
                                        {...formItemLayout}
                                        label="具体时间"
                                    >
                                        <Col span={6}>
                                            {getFieldDecorator('hour', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.hour}`,
                                            })(generateHours())}
                                        </Col>

                                        <span className="split-text">时</span>
                                        <Col span={6}>
                                            {getFieldDecorator('min', {
                                                rules: [
                                                    {
                                                        required: true,
                                                    },
                                                ],
                                                initialValue: `${scheduleConf.min}`,
                                            })(generateMins())}
                                        </Col>
                                        <span className="split-text">分</span>
                                    </FormItem>
                                </span>
                            );
                            break;
                        }

                        default:
                            dom = <span>something wrong</span>;
                    }
                    return dom;
                })(+periodType, this)}
                {isHourOrMinTask && ( // 仅小时或者
                    <FormItem {...formItemLayout} label="延迟实例">
                        {getFieldDecorator('isExpire', {
                            valuePropName: 'checked',
                            initialValue: get(scheduleConf, 'isExpire'),
                        })(
                            <Checkbox
                                onChange={handleScheduleConf}
                            >
                                自动取消
                            </Checkbox>
                        )}
                        <HelpDoc doc="autoSkipJobHelp" style={relativeStyle} />
                    </FormItem>
                )}
                {scheduleConf.isExpire && (
                    <FormItem {...formItemLayout} label="当天最后一个实例">
                        {getFieldDecorator('isLastInstance', {
                            initialValue: scheduleConf?.isLastInstance ?? true,
                        })(
                            <Group
                                onChange={handleScheduleConf}
                            >
                                <Radio value={true}>始终保留</Radio>
                                <Radio value={false}>
                                    延迟至第二天后自动取消
                                </Radio>
                            </Group>
                        )}
                        <HelpDoc doc="theLastExample" style={relativeStyle} />
                    </FormItem>
                )}
            </Form>
        );
    }

    checkTimeS(rule: any, value: any, callback: any) {
        const { form } = this.props;
        const endHour = form.getFieldValue('endHour');
        if (+value > +endHour) {
            /* eslint-disable-next-line */
            callback('开始时间不能晚于结束时间');
        }
        callback();
    }

    checkTimeE(rule: any, value: any, callback: any) {
        const { form } = this.props;
        const beginHour = form.getFieldValue('beginHour');

        if (+value < +beginHour) {
            /* eslint-disable-next-line */
            callback('结束时间不能早于开始时间');
        }
        callback();
    }

    checkTimeS1(rule: any, value: any, callback: any) {
        const { form } = this.props;
        const beginHour = +form.getFieldValue('beginHour');
        const beginMin = +form.getFieldValue('beginMin');
        const endHour = +form.getFieldValue('endHour') * 60 + 59;

        if (beginHour * 60 + beginMin > endHour) {
            /* eslint-disable-next-line */
            callback('开始时间不能晚于结束时间');
            return;
        }
        callback();
    }

    checkTimeE1(rule: any, value: any, callback: any) {
        const { form } = this.props;
        const beginHour = +form.getFieldValue('beginHour');
        const beginMin = +form.getFieldValue('beginMin');
        const endHour = +form.getFieldValue('endHour') * 60 + 59;

        if (beginHour * 60 + beginMin > endHour) {
            /* eslint-disable-next-line */
            callback('结束时间不能早于开始时间');
            return;
        }
        callback();
    }
}

const FormWrap = Form.create<any>()(ScheduleForm);
export default FormWrap;
