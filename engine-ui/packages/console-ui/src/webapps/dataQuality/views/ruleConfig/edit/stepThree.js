import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { isEmpty } from 'lodash';
import moment from 'moment';
import { Button, Form, Select, DatePicker, Checkbox } from 'antd';

import { ruleConfigActions } from '../../../actions/ruleConfig';
import { commonActions } from '../../../actions/common';
import { formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;

const mapStateToProps = state => {
    const { common } = state;
    return { common }
}

const mapDispatchToProps = dispatch => ({
    getUserList(params) {
        dispatch(commonActions.getUserList(params));
    },
    addMonitor(params) {
        dispatch(ruleConfigActions.addMonitor(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepThree extends Component {
    constructor(props) {
        super(props);
        this.state = {
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                endDate: moment().add(100, 'years').format('YYYY-MM-DD'),
                periodType: '2',
                day: undefined,
                weekDay: undefined,
                hour: 0,
                min: 0,
                beginHour: 0,
                beginMin: 0,
                gapHour: undefined,
                endHour: 0,
                endMin: 0
            }
        }
    }

    componentDidMount() {
        this.props.getUserList();
        this.initState();
    }

    initState = () => {
        const { scheduleConf } = this.props.editParams;

        if (scheduleConf) {
            this.setState({ scheduleConfObj: JSON.parse(scheduleConf) });
        }
    }

    resetScheduleConf = (type) => {
        let scheduleConfObj = {
            beginDate: moment().format('YYYY-MM-DD'),
            endDate: moment().add(100, 'years').format('YYYY-MM-DD'),
            periodType: type,
            day: undefined,
            weekDay: undefined,
            hour: 0,
            min: 0,
            beginHour: 0,
            beginMin: 0,
            gapHour: undefined,
            endHour: 0,
            endMin: 0
        };

        this.setState({ scheduleConfObj });
        this.props.changeParams({
            scheduleConf: JSON.stringify(scheduleConfObj)
        });
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.value} 
                    value={item.value.toString()}>
                    {item.name}
                </Option>
            )
        })
    }

    // 调度周期回调
    onPeriodTypeChange = (type) => {
        this.resetScheduleConf(type);
    }

    // 联系人变化回调
    onSendTypeChange = (value) => {
        const { sendTypes } = this.props.editParams;
        this.props.changeParams({
            sendTypes: value
        });
    }

    onBeginDateChange = (date, dateString) => {
        this.changeScheduleParams(dateString, 'beginDate');
    }

    onEndDateChange = (date, dateString) => {
        this.changeScheduleParams(dateString, 'endDate');
    }

    changeScheduleParams = (date, type) => {
        const { scheduleConfObj } = this.state;

        let newParams = {};
        newParams[type] = date;

        this.setState({
            scheduleConfObj: {...scheduleConfObj, ...newParams}
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({...scheduleConfObj, ...newParams})
        });
    }

    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.id} 
                    value={item.id.toString()}>
                    {item.userName}
                </Option>
            )
        })
    }

    onNotifyUserChange = (value) => {
        const { notifyUser } = this.props.editParams;
        this.props.changeParams({
            notifyUser: value
        })
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    save = () => {
        const { form, editParams } = this.props;
        form.validateFields({ force: true }, (err, values) => {
            if(!err) {
                editParams.rules.forEach((rule) => {
                    delete rule.id;
                    delete rule.isEnum;
                    delete rule.isTable;
                    delete rule.isPercent;
                    delete rule.editStatus;
                    delete rule.functionName;
                    delete rule.verifyTypeValue;
                });

                this.props.addMonitor({...editParams});
                hashHistory.push('/dq/rule');
            }
        });
    }

    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj } = this.state;
        let newParams = {};
        newParams[type] = value;

        this.setState({
            scheduleConfObj: {...scheduleConfObj, ...newParams}
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({...scheduleConfObj, ...newParams})
        });
    }

    checkTime = (rule, value, callback) => {
        const { form } = this.props;
        let beginTime = parseInt(form.getFieldValue('beginHour')) * 60 + parseInt(form.getFieldValue('beginMin')),
            endTime   = parseInt(form.getFieldValue('endHour')) * 60 + parseInt(form.getFieldValue('endMin'));

        if (beginTime >= endTime) {
            callback('开始时间不能晚于结束时间');
        }

        callback();
    }

    checkDate = (rule, value, callback) => {
        const { form } = this.props;
        let beginDate = form.getFieldValue('beginDate'),
            endDate = form.getFieldValue('endDate');

        if (!beginDate || !endDate) {
            callback();
        } else {
            if (beginDate.valueOf() > endDate.valueOf()) {
                callback('开始时间不能晚于结束时间');
            }
        }

        callback();
    }

    renderDynamic() {
        const { form, common, editParams } = this.props;
        const { scheduleConfObj } = this.state;
        const { allDict, userList } = common;
        const { notifyUser, sendTypes } = editParams;
        const { getFieldDecorator } = form;

        let periodType = allDict.periodType ? allDict.periodType : [];

        const generateHours = (type) => {
            let options = [];

            for (let i = 0; i <= 23; i++) {
                options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}`: i}</Option>);
            }

            return <Select 
                style={{ width: 150 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                { options }
            </Select>;
        };

        const generateMins = (type) => {
            let options = [];

            for (let i = 0, l = 59; i <= l; i++) {
                options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}`: i}</Option>);
            }

            return <Select 
                style={{ width: 150 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                { options }
            </Select>;
        };

        const generateGapHour = () => {
            let options = [];

            for(let i = 1, l = 23; i <= l; i++) {
                options.push(<Option key={i} value={`${i}`}>{i}小时</Option>)
            }
            
            return <Select
                style={{ width: 150 }}
                onChange={this.changeScheduleConfTime.bind(this, 'gapHour') }>
                { options }
            </Select>
        };

        const generateDate = () => {
            let options = [];

            for (let i = 1; i <= 31; i++) {
                options.push(<Option key={i} value={`${i}`}>{`每月${i}号`}</Option>);
            }

            return <Select
                mode="multiple"
                style={{ width: 325 }}
                onChange={this.changeScheduleConfTime.bind(this, 'day')}>
                { options }
            </Select>;
        };

        const generateDays = () => {
            return <Select
                mode="multiple"
                style={{ width: 325 }}
                onChange={this.changeScheduleConfTime.bind(this, 'weekDay')}
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

        switch (scheduleConfObj.periodType) {
            case '1': {
                return <div>
                    <FormItem {...formItemLayout} label="开始时间">
                        {
                            getFieldDecorator('beginHour', {
                                rules: [{
                                    required: true, message: '开始时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.beginHour}`
                            })(
                                generateHours('beginHour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('beginMin', {
                                rules: [{
                                    required: true, message: '开始时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.beginMin}`
                            })(
                                generateMins('beginMin')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                    <FormItem {...formItemLayout} label="间隔时间">
                        {
                            getFieldDecorator('gapHour', {
                                rules: [{
                                    required: true, message: '间隔时间不能为空'
                                }],
                                initialValue: scheduleConfObj.gapHour ? scheduleConfObj.gapHour : ''
                            })(
                                generateGapHour()
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="结束时间">
                        {
                            getFieldDecorator('endHour', {
                                rules: [{
                                    required: true, message: '结束时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.endHour}`
                            })(
                                generateHours('endHour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('endMin', {
                                rules: [{
                                    required: true, message: '结束时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.endMin}`
                            })(
                                generateMins('endMin')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '2': {
                return <FormItem {...formItemLayout} label="起调周期">
                    {
                        getFieldDecorator('hour', {
                            rules: [{
                                required: true
                            }],
                            initialValue: `${scheduleConfObj.hour}`
                        })(
                            generateHours('hour')
                        )
                    }
                    <span className="m-8">
                        时
                    </span>
                    {
                        getFieldDecorator('min', {
                            rules: [{
                                required: true
                            }],
                            initialValue: `${scheduleConfObj.min}`

                        })(
                            generateMins('min')
                        )
                    }
                    <span className="m-8">
                        分
                    </span>
                </FormItem>
            }

            case '3': {
                return <div>
                    <FormItem {...formItemLayout} label="选择时间">
                        {
                            getFieldDecorator('weekDay', {
                                rules: [{
                                    required: true, message: '周内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.weekDay ? `${scheduleConfObj.weekDay}`.split(',') : []
                            })(
                                generateDays()
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="起调周期">
                        {
                            getFieldDecorator('hour', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.hour}`
                            })(
                                generateHours('hour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('min', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.min}`

                            })(
                                generateMins('min')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '4': {
                return <div>
                    <FormItem {...formItemLayout} label="选择时间">
                        {
                            getFieldDecorator('day', {
                                rules: [{
                                    required: true, message: '月内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.day ? `${scheduleConfObj.day}`.split(',') : []
                            })(
                                generateDate()
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="起调周期">
                        {
                            getFieldDecorator('hour', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.hour}`
                            })(
                                generateHours('hour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('min', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.min}`

                            })(
                                generateMins('min')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '5': 
            default: {
                break;
            }
        }
        
    }

    render() {
        const { form, common, editParams } = this.props;
        const { scheduleConfObj } = this.state;
        const { allDict, userList } = common;
        const { notifyUser, sendTypes } = editParams;
        const { getFieldDecorator } = form;

        let periodType = allDict.periodType ? allDict.periodType : [];

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="调度周期" key="periodType">
                            {
                                getFieldDecorator('periodType', {
                                    rules: [{ required: true, message: '执行周期不能为空' }], 
                                    initialValue: scheduleConfObj.periodType
                                })(
                                    <Select style={{ width: 325 }} onChange={this.onPeriodTypeChange}>
                                        {
                                            this.renderPeriodType(periodType)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            scheduleConfObj.periodType != 5
                            &&
                            <FormItem {...formItemLayout} label="生效日期">
                                {
                                    getFieldDecorator('beginDate', {
                                        rules: [{
                                            required: true, message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.beginDate)
                                    })(
                                        <DatePicker
                                            // size="large"
                                            format="YYYY-MM-DD"
                                            placeholder="开始日期"
                                            style={{ width: 150 }}
                                            onChange={this.onBeginDateChange}
                                        />
                                    )
                                }
                                <span className="m-8">
                                    到
                                </span>
                                {
                                    getFieldDecorator('endDate', {
                                        rules: [{
                                            required: true, message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.endDate)
                                    })(
                                        <DatePicker
                                            // size="large"
                                            format="YYYY-MM-DD"
                                            placeholder="结束日期"
                                            style={{ width: 150 }}
                                            onChange={this.onEndDateChange}
                                        />
                                    )
                                }
                            </FormItem>
                        }

                        {
                            this.renderDynamic()
                        }
                        
                        <FormItem {...formItemLayout} label="通知方式" key="sendTypes">
                            {
                                getFieldDecorator('sendTypes', {
                                    rules: [],
                                    initialValue: sendTypes.map(item => item.toString())
                                })(
                                    <Checkbox.Group onChange={this.onSendTypeChange}>
                                        {
                                            allDict.notifyType.map((item) => {
                                                return <Checkbox 
                                                    key={item.value} 
                                                    value={item.value.toString()}>
                                                    {item.name}
                                                </Checkbox>
                                            })
                                        }
                                    </Checkbox.Group>
                                )
                            }
                        </FormItem>
                        
                        <FormItem {...formItemLayout} label="通知接收人" key='notifyUser'>
                            {
                                getFieldDecorator('notifyUser', {
                                    rules: [],
                                    initialValue: notifyUser.map(item => item.toString())
                                })(
                                    <Select 
                                        allowClear 
                                        mode="multiple" 
                                        style={{ width: 325 }} 
                                        onChange={this.onNotifyUserChange}>
                                        {
                                            this.renderUserList(userList)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </Form>
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button className="m-l-8" type="primary" onClick={this.save}>新建</Button>
                </div>
            </div>
        )
    }
}
StepThree = Form.create()(StepThree);