import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import moment from 'moment';
import { Button, Form, Select, Input, Row, Col, Radio, TimePicker, DatePicker, Checkbox } from 'antd';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { commonActions } from '../../../actions/common';
import { formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { common } = state;
    return { common }
}

const mapDispatchToProps = dispatch => ({
    getUserList(params) {
        dispatch(commonActions.getUserList(params));
    },
    addRule(params) {
        dispatch(ruleConfigActions.addRule(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepFour extends Component {
    constructor(props) {
        super(props);
        this.state = {
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                endDate: moment().add(3, 'months').format('YYYY-MM-DD'),
                periodType: '1',
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

    resetScheduleConf = () => {
        this.setState({
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                endDate: moment().add(3, 'months').format('YYYY-MM-DD'),
                periodType: '1',
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
        });
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.value} value={item.value.toString()}>{item.name}</Option>
            )
        })
    }

    // 调度周期回调
    onPeriodTypeChange = (type) => {
        const { scheduleConfObj } = this.state;
        
        this.setState({
            scheduleConfObj: {...scheduleConfObj, periodType: type}
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({...scheduleConfObj, periodType: type})
        });
    }

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
                <Option key={item.id} value={item.id.toString()}>{item.userName}</Option>
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
            console.log(err,values)
            if(!err) {
                editParams.rules.forEach((rule) => {
                    delete rule.id
                    delete rule.isCustomizeSql
                })
                this.props.addRule({...editParams});
                // location.href = "/dataQuality.html#/dq/rule";
            }
        })

    }

    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj } = this.state;
        console.log(type,value)
        let newParams = {};
        newParams[type] = value;

        this.setState({
            scheduleConfObj: {...scheduleConfObj, ...newParams}
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({...scheduleConfObj, ...newParams})
        });
    }

    render() {
        const { form, common, editParams, editStatus } = this.props;
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
            return <Select style={{ width: 150, marginRight: 8 }} onChange={this.changeScheduleConfTime.bind(this, type)}>{ options }</Select>;
        };

        const generateMins = (type) => {
            let options = [];

            for (let i = 0, l = 59; i <= l; i++) {
                options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}`: i}</Option>);
            }
            return <Select style={{ width: 150, margin: '0 8px' }} onChange={this.changeScheduleConfTime.bind(this, type)}>{ options }</Select>;
        };

        const generateDate = () => {
            let options = [];

            for (let i = 1; i <= 31; i++) {
                options.push(<Option key={i} value={`${i}`}>{`每月${i}号`}</Option>);
            }
            return <Select
                mode="multiple"
                style={{ width: 325 }}
                onChange={this.changeScheduleConfTime.bind(this, 'day')}
            >{ options }</Select>;
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
                                    initialValue: moment(scheduleConfObj.beginDate)
                                    })(
                                        <DatePicker
                                            size="large"
                                            format="YYYY-MM-DD"
                                            placeholder="开始日期"
                                            style={{ width: 150, marginRight: 8 }}
                                            onChange={this.onBeginDateChange}
                                        />
                                    )
                                }
                                到
                                {
                                    getFieldDecorator('endDate', {
                                        initialValue: moment(scheduleConfObj.endDate)
                                    })(
                                        <DatePicker
                                            size="large"
                                            format="YYYY-MM-DD"
                                            placeholder="结束日期"
                                            style={{ width: 150, marginLeft: 8 }}
                                            onChange={this.onEndDateChange}
                                        />
                                    )
                                }
                            </FormItem>
                        }

                        {
                            ((type) => {
                                switch (type) {
                                    case '1': {
                                        return <div>
                                            <FormItem {...formItemLayout} label="开始时间">
                                                {
                                                    getFieldDecorator('beginHour', {
                                                        rules: [{
                                                                required: true
                                                            },{
                                                                // validator: ctx.checkTimeS.bind(ctx)
                                                            }
                                                        ],
                                                        initialValue: `${scheduleConfObj.beginHour}`
                                                    })(
                                                        generateHours('beginHour')
                                                    )
                                                }
                                                时
                                                {
                                                    getFieldDecorator('beginMin', {
                                                        rules: [{
                                                            required: true
                                                        }],
                                                        initialValue: `${scheduleConfObj.beginMin}`
                                                    })(
                                                        generateMins('beginMin')
                                                    )
                                                }
                                                分
                                            </FormItem>

                                            <FormItem {...formItemLayout} label="间隔时间">
                                                {
                                                    getFieldDecorator('gapHour', {
                                                        rules: [{
                                                            required: true
                                                        }],
                                                        initialValue: scheduleConfObj.gapHour ? scheduleConfObj.gapHour : ''
                                                    })(
                                                        <Select
                                                            style={{ width: 150 }}
                                                            onChange={this.changeScheduleConfTime.bind(this, 'gapHour') }
                                                        >
                                                            {
                                                                (function() {
                                                                    let options = [];

                                                                    for(let i = 1, l = 23; i <= l; i++) {
                                                                        options.push(<Option key={i} value={`${i}`}>{i}小时</Option>)
                                                                    }
                                                                    return options;
                                                                })()
                                                            }
                                                        </Select>
                                                    )
                                                }
                                            </FormItem>

                                            <FormItem {...formItemLayout} label="结束时间">
                                                {
                                                    getFieldDecorator('endHour', {
                                                        rules: [{
                                                            required: true
                                                        }, {
                                                            // validator: ctx.checkTimeE1.bind(ctx)
                                                        }],
                                                        initialValue: `${scheduleConfObj.endHour}`
                                                    })(
                                                        generateHours('endHour')
                                                    )
                                                }
                                                时
                                                {
                                                    getFieldDecorator('endMin', {
                                                        rules: [{
                                                            required: true
                                                        }],
                                                        initialValue: `${scheduleConfObj.endMin}`
                                                    })(
                                                        generateMins('endMin')
                                                    )
                                                }
                                                分
                                            </FormItem>
                                        </div>
                                    }

                                    case '3': {
                                        return <FormItem {...formItemLayout} label="选择时间">
                                            <Col span="13">
                                                {
                                                    getFieldDecorator('weekDay', {
                                                        rules: [{
                                                            required: true
                                                        }],
                                                        initialValue: scheduleConfObj.weekDay ? `${scheduleConfObj.weekDay}`.split(',') : []
                                                    })(
                                                        generateDays()
                                                    )
                                                }
                                            </Col>
                                        </FormItem>
                                    }

                                    case '4': {
                                        return <FormItem {...formItemLayout} label="选择时间">
                                            <Col span="13">

                                                {
                                                    getFieldDecorator('day', {
                                                        rules: [{
                                                            required: true
                                                        }],
                                                        initialValue: scheduleConfObj.day ? `${scheduleConfObj.day}`.split(',') : []
                                                    })(
                                                        generateDate()
                                                    )
                                                }
                                            </Col>
                                        </FormItem>
                                    }

                                    case '5': {
                                        break;
                                    }

                                    default: {
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
                                            时
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
                                            分
                                        </FormItem>
                                    }
                                }
                            })(scheduleConfObj.periodType)
                        }
                        
                        <FormItem {...formItemLayout} label="通知方式" key="sendTypes">
                            {
                                getFieldDecorator('sendTypes', {
                                    rules: [{ required: true, message: '选择一种通知方式' }], 
                                    initialValue: sendTypes.map(item => item.toString())
                                })(
                                    <Checkbox.Group onChange={this.onSendTypeChange}>
                                        <Checkbox value="0">邮件</Checkbox>
                                        <Checkbox value="1">短信</Checkbox>
                                    </Checkbox.Group>
                                )
                            }
                        </FormItem>
                        
                        <FormItem {...formItemLayout} label="通知接收人" key='notifyUser'>
                            {
                                getFieldDecorator('notifyUser', {
                                    rules: [{ required: true, message: '接收人不能为空' }],
                                    initialValue: notifyUser.map(item => item.toString())
                                })(
                                    <Select style={{ width: 325 }} mode="multiple" allowClear onChange={this.onNotifyUserChange}>
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
                    <Button className="m-l-8" type="primary" onClick={this.save}>{editStatus === 'edit' ? '保存' : '新建'}</Button>
                </div>
            </div>
        )
    }
}
StepFour = Form.create()(StepFour);