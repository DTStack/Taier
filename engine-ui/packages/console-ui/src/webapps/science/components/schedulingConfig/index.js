import React from 'react';
import moment from 'moment';

import { Form, Checkbox, Select, Col, DatePicker } from 'antd';

// import { formItemLayout } from '../../consts'

const FormItem = Form.Item;
const Option = Select.Option;

class SchedulingConfig extends React.Component {
    generateHours (disabledInvokeTime) {
        let options = [];
        for (let i = 0; i <= 23; i++) {
            options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}` : i}</Option>)
        }
        return <Select
            disabled={disabledInvokeTime}
        >
            {options}
        </Select>;
    }
    generateMins (disabledInvokeTime) {
        let options = [];
        for (let i = 0, l = 59; i <= l; i++) {
            options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}` : i}</Option>)
        }
        return <Select
            disabled={disabledInvokeTime}
        >
            {options}
        </Select>;
    }
    generateDays () {
        return <Select
            mode="multiple"
            style={{ width: '100%' }}
        >
            <Option key={1} value="1">星期一</Option>
            <Option key={2} value="2">星期二</Option>
            <Option key={3} value="3">星期三</Option>
            <Option key={4} value="4">星期四</Option>
            <Option key={5} value="5">星期五</Option>
            <Option key={6} value="6">星期六</Option>
            <Option key={7} value="7">星期天</Option>
        </Select>
    }
    generateDate () {
        let options = [];
        for (let i = 1; i <= 31; i++) {
            options.push(<Option key={i} value={`${i}`}>{`每月${i}号`}</Option>)
        }
        return <Select
            mode="multiple"
            style={{ width: '100%' }}
        >{options}</Select>;
    }
    generateInterval (isHour) {
        let options = (function () {
            let options = [];
            for (let i = 5; i <= 55; i += 5) {
                options.push(<Option key={i} value={`${i}`}>{i}{isHour ? '小时' : '分钟'}</Option>)
            }
            return options;
        })();
        return options;
    }
    checkTimeS1 (rule, value, callback) {
        callback();
    }
    renderPeriod () {
        const { formData = {}, form } = this.props;
        const { getFieldDecorator } = form;
        const { periodType } = formData;
        const hourView = (
            <FormItem
                label="具体时间"
            >
                <Col span="6">

                    {getFieldDecorator('hour', {
                        rules: [{
                            required: true
                        }, {
                            validator: this.checkTimeS1
                        }
                        ]
                    })(
                        this.generateHours()
                    )}
                </Col>

                <span className="split-text">时</span>
                <Col span="6">

                    {getFieldDecorator('min', {
                        rules: [{
                            required: true
                        }, {
                            validator: this.checkTimeS1
                        }]
                    })(
                        this.generateMins()
                    )}
                </Col>

                <span className="split-text">分</span>
            </FormItem>
        );
        switch (periodType) {
            case PERIOD_TYPE.MIN:
            case PERIOD_TYPE.HOUR: {
                const isHour = periodType == PERIOD_TYPE.HOUR;
                return <React.Fragment>
                    <FormItem
                        label="开始时间"
                    >
                        <Col span="6">

                            {getFieldDecorator('beginHour', {
                                rules: [{
                                    required: true
                                }, {
                                    validator: this.checkTimeS1
                                }
                                ]
                            })(
                                this.generateHours()
                            )}
                        </Col>

                        <span className="split-text">时</span>
                        <Col span="6">

                            {getFieldDecorator('beginMin', {
                                rules: [{
                                    required: true
                                }, {
                                    validator: this.checkTimeS1
                                }]
                            })(
                                this.generateMins()
                            )}
                        </Col>

                        <span className="split-text">分</span>
                    </FormItem>
                    <FormItem
                        label="间隔时间"
                    >
                        <Col span="6">
                            {getFieldDecorator(isHour ? 'gapHour' : 'gapMin', {
                                rules: [{
                                    required: true
                                }]
                            })(
                                <Select>
                                    {this.generateInterval(isHour)}
                                </Select>
                            )}
                        </Col>
                    </FormItem>
                    <FormItem
                        label="结束时间"
                    >
                        <Col span="6">

                            {getFieldDecorator('endHour', {
                                rules: [{
                                    required: true
                                }, {
                                    validator: this.checkTimeS1
                                }
                                ]
                            })(
                                this.generateHours()
                            )}
                        </Col>

                        <span className="split-text">时</span>
                        <Col span="6">

                            {getFieldDecorator('endMin', {
                                rules: [{
                                    required: true
                                }, {
                                    validator: this.checkTimeS1
                                }]
                            })(
                                this.generateMins(isHour)
                            )}
                        </Col>

                        <span className="split-text">分</span>
                    </FormItem>
                </React.Fragment>
            }
            case PERIOD_TYPE.DAY: {
                return hourView;
            }
            case PERIOD_TYPE.WEEK: {
                return <React.Fragment>
                    <FormItem
                        label="选择时间"
                    >
                        <Col span="13">
                            {getFieldDecorator('weekDay', {
                                rules: [{
                                    required: true
                                }]
                            })(
                                this.generateDays()
                            )}
                        </Col>
                    </FormItem>
                    {hourView}
                </React.Fragment>
            }
            case PERIOD_TYPE.MONTH: {
                return <React.Fragment>
                    <FormItem
                        label="选择时间"
                    >
                        <Col span="13">
                            {getFieldDecorator('day', {
                                rules: [{
                                    required: true
                                }]
                            })(
                                this.generateDate()
                            )}
                        </Col>
                    </FormItem>
                    {hourView}
                </React.Fragment>
            }
        }
    }
    render () {
        const { form, formData = {} } = this.props;
        const { getFieldDecorator } = form;
        return (
            <div className='c-schedulingConfig'>
                <header className='c-panel__siderbar__header'>
                    调度属性
                </header>
                <Form className='c-panel__siderbar__form'>
                    <FormItem
                        label='调度状态'
                    >
                        {getFieldDecorator('scheduleStatus', {
                            valuePropName: 'checked'
                        })(
                            <Checkbox>冻结</Checkbox>
                        )}
                    </FormItem>
                    <FormItem
                        label='出错重试'
                    >
                        {getFieldDecorator('isFailRetry', {
                            valuePropName: 'checked'
                        })(
                            <Checkbox>是</Checkbox>
                        )}
                    </FormItem>
                    {!!formData.isFailRetry && (
                        <FormItem
                            label='重试次数'
                        >
                            <Col span="6">
                                {getFieldDecorator('maxRetryNum', {
                                    rules: [{
                                        required: true, message: '请选择重试次数'
                                    }]
                                })(
                                    <Select>
                                        <Option key='1' value='1'>1</Option>
                                        <Option key='2' value='2'>2</Option>
                                        <Option key='3' value='3'>3</Option>
                                        <Option key='4' value='4'>4</Option>
                                        <Option key='5' value='5'>5</Option>
                                    </Select>
                                )}
                            </Col>
                            <span className="split-text">次，每次间隔2分钟</span>
                        </FormItem>
                    )}
                    <FormItem
                        label='生效日期'
                    >
                        {getFieldDecorator('beginDate')(
                            <DatePicker
                                style={{ width: '120px' }}
                            />
                        )}
                        <span className="split-text" style={{ float: 'none' }} >-</span>
                        {getFieldDecorator('endDate')(
                            <DatePicker
                                style={{ width: '120px' }}
                            />
                        )}
                    </FormItem>
                    <FormItem
                        label='调度周期'
                    >
                        {getFieldDecorator('periodType', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <Select>
                                <Option key={0} value={PERIOD_TYPE.MIN}>分钟</Option>
                                <Option key={1} value={PERIOD_TYPE.HOUR}>小时</Option>
                                <Option key={2} value={PERIOD_TYPE.DAY}>天</Option>
                                <Option key={3} value={PERIOD_TYPE.WEEK}>周</Option>
                                <Option key={4} value={PERIOD_TYPE.MONTH}>月</Option>
                            </Select>
                        )}
                    </FormItem>
                    {this.renderPeriod()}
                </Form>
            </div>
        );
    }
}
export const PERIOD_TYPE = {
    MIN: '0',
    HOUR: '1',
    DAY: '2',
    WEEK: '3',
    MONTH: '4'
}
const dateType = ['beginDate', 'endDate']
export default Form.create({
    mapPropsToFields (props) {
        const { formData = {} } = props;
        const keyAndValues = Object.entries(formData);
        return (() => {
            let result = {};
            keyAndValues.forEach(([key, value]) => {
                if (dateType.indexOf(key) > -1) {
                    result[key] = {
                        value: moment(value)
                    }
                } else {
                    result[key] = {
                        value: value
                    }
                }
            });
            return result;
        })()
    },
    onValuesChange (props, values) {
        const { formData = {} } = props;
        const keys = Object.keys(values);
        keys.forEach((key) => {
            if (dateType.indexOf(key) > -1 && values[key] instanceof moment) {
                values[key] = values[keys].format('YYYY-MM-DD');
            }
        })
        let newFormData = { ...formData, ...values };
        if (values.hasOwnProperty('periodType')) {
            newFormData.beginHour = undefined;
            newFormData.beginMin = undefined;
            newFormData.endHour = undefined;
            newFormData.endMin = undefined;
            newFormData.gapHour = undefined;
            newFormData.gapMin = undefined;
            newFormData.hour = undefined;
            newFormData.min = undefined;
            newFormData.day = undefined;
            newFormData.weekDay = undefined;
            switch (values.periodType) {
                case PERIOD_TYPE.MIN:
                case PERIOD_TYPE.HOUR: {
                    newFormData.beginHour = '0';
                    newFormData.beginMin = '0';
                    newFormData.endHour = '23';
                    newFormData.endMin = '59';
                    if (values.periodType == PERIOD_TYPE.HOUR) {
                        newFormData.gapHour = '5';
                    } else {
                        newFormData.gapMin = '5';
                    }
                    break;
                }
                case PERIOD_TYPE.DAY: {
                    newFormData.hour = '0';
                    newFormData.min = '0';
                    break;
                }
                case PERIOD_TYPE.WEEK:
                case PERIOD_TYPE.MONTH: {
                    newFormData.hour = '0';
                    newFormData.min = '0';
                    if (values.periodType == PERIOD_TYPE.WEEK) {
                        newFormData.gapHour = '5';
                        newFormData.weekDay = ['3'];
                    } else {
                        newFormData.gapMin = '5';
                        newFormData.day = ['5'];
                    }
                }
            }
        }
        if (values.hasOwnProperty('isFailRetry')) {
            newFormData.maxRetryNum = 3;
        }
        console.log(values);
        return props.onChange(newFormData, values);
    }
})(SchedulingConfig);
