import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { isEmpty } from 'lodash';
import moment from 'moment';
import { Button, Form, Select, DatePicker, Checkbox, message } from 'antd';

import { ruleConfigActions } from '../../../actions/ruleConfig';
import { halfFormItemLayout } from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;

const initialSchedule = {
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

const mapStateToProps = state => {
    const { common } = state;
    return { common }
}

const mapDispatchToProps = dispatch => ({
    addMonitor(params) {
        dispatch(ruleConfigActions.addMonitor(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepThree extends Component {
    constructor(props) {
        super(props);
        this.state = {
            scheduleConfObj: initialSchedule
        }
    }

    componentDidMount() {
        this.initState();
    }

    initState = () => {
        const { scheduleConf } = this.props.editParams;

        if (scheduleConf) {
            this.setState({ scheduleConfObj: JSON.parse(scheduleConf) });
        }
    }

    resetScheduleConf = (type) => {
        let scheduleConfObj = { ...initialSchedule, periodType: type };

        this.setState({ scheduleConfObj });
        this.props.changeParams({
            scheduleConf: JSON.stringify(scheduleConfObj)
        });
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.value} 
                value={item.value.toString()}>
                {item.name}
            </Option>
        });
    }

    // 调度周期回调
    onPeriodTypeChange = (type) => {
        this.resetScheduleConf(type);
    }

    renderSendTypeList = (data) => {
        return data && data.map((item) => {
            return <Checkbox 
                key={item.value} 
                value={item.value.toString()}>
                {item.name}
            </Checkbox>
        })
    }

    // 联系人变化回调
    onSendTypeChange = (value) => {
        const { form } = this.props;
        let notifyUser = form.getFieldValue('notifyUser');

        if (value.length === 0 && notifyUser.length === 0) {
            form.setFieldsValue({ notifyUser: [] });
        }

        this.props.changeParams({ sendTypes: value });
    }

    // 通知人下拉框
    renderUserList = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.id} 
                value={item.id.toString()}>
                {item.userName}
            </Option>
        });
    }

    // 通知人回调
    onNotifyUserChange = (value) => {
        const { form } = this.props;
        let sendTypes = form.getFieldValue('sendTypes');
        
        if (value.length === 0 && sendTypes.length === 0) {
            form.setFieldsValue({ sendTypes: [] });
        }

        this.props.changeParams({ notifyUser: value });
    }

    // 调度日期回调
    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj } = this.state;

        let newParams = {};
        newParams[type] = value;
        let newConfObj = { ...scheduleConfObj, ...newParams };

        this.setState({
            scheduleConfObj: newConfObj
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify(newConfObj)
        });
    }

    onBeginDateChange = (date, dateString) => {
        this.changeScheduleConfTime('beginDate', dateString);
    }

    onEndDateChange = (date, dateString) => {
        this.changeScheduleConfTime('endDate', dateString);
    }

    // 根据调度类型的不同返回不同的调度配置
    renderDynamic() {
        const { form, common } = this.props;
        const { allDict } = common;
        const { getFieldDecorator } = form;
        const { scheduleConfObj } = this.state;

        let periodType = allDict.periodType ? allDict.periodType : [];

        // 小时选择框
        const generateHours = (type) => {
            let options = [];

            for (let i = 0; i <= 23; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i < 10 ? `0${i}`: i}
                    </Option>
                );
            }

            return <Select 
                style={{ width: 200 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                { options }
            </Select>
        };

        // 分钟选择框
        const generateMins = (type) => {
            let options = [];

            for (let i = 0, l = 59; i <= l; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i < 10 ? `0${i}`: i}
                    </Option>
                );
            }

            return <Select 
                style={{ width: 200 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                { options }
            </Select>
        };

        // 间隔时间选择框
        const generateGapHour = () => {
            let options = [];

            for(let i = 1, l = 23; i <= l; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i}小时
                    </Option>
                );
            }
            
            return <Select
                style={{ width: 200 }}
                onChange={this.changeScheduleConfTime.bind(this, 'gapHour') }>
                { options }
            </Select>
        };

        // 月内天数选择框
        const generateDate = () => {
            let options = [];

            for (let i = 1; i <= 31; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {`每月${i}号`}
                    </Option>
                );
            }

            return <Select
                mode="multiple"
                style={{ width: 428 }}
                onChange={this.changeScheduleConfTime.bind(this, 'day')}>
                { options }
            </Select>
        };

        // 周内天数选择框
        const generateDays = () => {
            return <Select
                mode="multiple"
                style={{ width: 428 }}
                onChange={this.changeScheduleConfTime.bind(this, 'weekDay')}>
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
                    <FormItem {...halfFormItemLayout} label="开始时间">
                        {
                            getFieldDecorator('beginHour', {
                                rules: [{
                                    required: true, 
                                    message: '开始时间不能为空'
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
                                    required: true, 
                                    message: '开始时间不能为空'
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

                    <FormItem {...halfFormItemLayout} label="间隔时间">
                        {
                            getFieldDecorator('gapHour', {
                                rules: [{
                                    required: true, 
                                    message: '间隔时间不能为空'
                                }],
                                initialValue: scheduleConfObj.gapHour ? scheduleConfObj.gapHour : ''
                            })(
                                generateGapHour()
                            )
                        }
                    </FormItem>

                    <FormItem {...halfFormItemLayout} label="结束时间">
                        {
                            getFieldDecorator('endHour', {
                                rules: [{
                                    required: true, 
                                    message: '结束时间不能为空'
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
                                    required: true, 
                                    message: '结束时间不能为空'
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
                return <FormItem {...halfFormItemLayout} label="起调时间">
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
                    <FormItem {...halfFormItemLayout} label="选择时间">
                        {
                            getFieldDecorator('weekDay', {
                                rules: [{
                                    required: true, 
                                    message: '周内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.weekDay ? scheduleConfObj.weekDay : []
                            })(
                                generateDays()
                            )
                        }
                    </FormItem>

                    <FormItem {...halfFormItemLayout} label="起调时间">
                        {
                            getFieldDecorator('hour1', {
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
                            getFieldDecorator('min1', {
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
                    <FormItem {...halfFormItemLayout} label="选择时间">
                        {
                            getFieldDecorator('day', {
                                rules: [{
                                    required: true, 
                                    message: '月内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.day ? scheduleConfObj.day : []
                            })(
                                generateDate()
                            )
                        }
                    </FormItem>

                    <FormItem {...halfFormItemLayout} label="起调时间">
                        {
                            getFieldDecorator('hour2', {
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
                            getFieldDecorator('min2', {
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

    // 检查调度开始时间
    checkTime = (rule, value, callback) => {
        const { form } = this.props;
        let beginHour = form.getFieldValue('beginHour'),
            beginMin  = form.getFieldValue('beginMin'),
            endHour   = form.getFieldValue('endHour'),
            endMin    = form.getFieldValue('endMin'),
            beginTime = parseInt(beginHour) * 60 + parseInt(beginMin),
            endTime   = parseInt(endHour) * 60 + parseInt(endMin);

        if (beginTime >= endTime) {
            callback('开始时间不能晚于结束时间');
        } else {
            form.setFieldsValue({
                beginHour,
                beginMin,
                endHour,
                endMin
            });
        }

        callback();
    }

    // 检查生效日期
    checkDate = (rule, value, callback) => {
        const { form } = this.props;
        let beginDate = form.getFieldValue('beginDate'),
            endDate = form.getFieldValue('endDate');

        if (!beginDate || !endDate) {
            callback();
        } else {
            if (beginDate.valueOf() > endDate.valueOf()) {
                callback('生效日期的开始时间不能晚于结束时间');
            } else {
                form.setFieldsValue({
                    beginDate,
                    endDate
                });
            }
        }

        callback();
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    save = () => {
        const { form, editParams } = this.props;
        form.validateFields((err, values) => {
            console.log(err,values)
            if (err && err.endDate) {
                message.error(err.endDate.errors[0].message)
            }

            if(!err) {
                editParams.rules.forEach((rule) => {
                    delete rule.id;
                    delete rule.isTable;
                    delete rule.percentType;
                    delete rule.functionName;
                    delete rule.verifyTypeValue;
                });

                this.props.addMonitor({...editParams});
                setTimeout(
                    ()=>{
                        hashHistory.push('/dq/rule');
                    },
                    1000
                )
                
            }
        });
    }

    render() {
        const { form, common, editParams } = this.props;
        const { getFieldDecorator } = form;
        const { allDict, userList } = common;
        const { notifyUser, sendTypes } = editParams;
        const { scheduleConfObj } = this.state;

        let periodType = allDict.periodType ? allDict.periodType : [],
            notifyType = allDict.notifyType ? allDict.notifyType : [];

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...halfFormItemLayout} label="调度周期">
                            {
                                getFieldDecorator('periodType', {
                                    rules: [{ 
                                        required: true, 
                                        message: '执行周期不能为空' 
                                    }], 
                                    initialValue: `${scheduleConfObj.periodType}`
                                })(
                                    <Select onChange={this.onPeriodTypeChange}>
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
                            <FormItem {...halfFormItemLayout} label="生效日期">
                                {
                                    getFieldDecorator('beginDate', {
                                        rules: [{
                                            required: true, 
                                            message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.beginDate)
                                    })(
                                        <DatePicker
                                            style={{ width: 200 }}
                                            format="YYYY-MM-DD"
                                            placeholder="开始日期"
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
                                            required: true, 
                                            message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.endDate)
                                    })(
                                        <DatePicker
                                            style={{ width: 200 }}
                                            format="YYYY-MM-DD"
                                            placeholder="结束日期"
                                            onChange={this.onEndDateChange}
                                        />
                                    )
                                }
                            </FormItem>
                        }

                        {
                            this.renderDynamic()
                        }
                        
                        <FormItem {...halfFormItemLayout} label="告警方式">
                            {
                                getFieldDecorator('sendTypes', {
                                    rules: [{
                                        required: notifyUser.length,
                                        message: '选择告警方式',
                                    }],
                                    initialValue: sendTypes.map(item => item.toString())
                                })(
                                    <Checkbox.Group onChange={this.onSendTypeChange}>
                                        {
                                            this.renderSendTypeList(notifyType)
                                        }
                                    </Checkbox.Group>
                                )
                            }
                        </FormItem>
                        
                        <FormItem {...halfFormItemLayout} label="告警接收人">
                            {
                                getFieldDecorator('notifyUser', {
                                    rules: [{
                                        required: sendTypes.length,
                                        message: '选择告警接收人',
                                    }],
                                    initialValue: notifyUser.map(item => item.toString())
                                })(
                                    <Select 
                                        allowClear 
                                        mode="multiple" 
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