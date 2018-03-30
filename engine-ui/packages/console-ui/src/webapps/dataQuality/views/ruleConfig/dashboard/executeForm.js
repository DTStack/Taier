import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Form, Select, DatePicker, Checkbox, Modal, message } from 'antd';

import { commonActions } from '../../../actions/common';
import { formItemLayout } from '../../../consts';
import RCApi from '../../../api/ruleConfig';

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
    getAllDict(params) {
        dispatch(commonActions.getAllDict(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class ExecuteForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                endDate: moment().add(3, 'months').format('YYYY-MM-DD'),
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
            },
            // params: {}
            params: {
                monitorId: undefined,
                scheduleConf: '',
                notifyUser: [],
                sendTypes: [],
                periodType: ''
            }
        }
    }

    componentDidMount() {
        this.props.getUserList();
        this.props.getAllDict();
        this.initState(this.props.data);
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;

        if (oldData.monitorId != newData.monitorId) {
            this.initState(newData);
        }
    }

    initState = (data) => {
        if (data.scheduleConf) {
            console.log(data)
            this.setState({ 
                scheduleConfObj: JSON.parse(data.scheduleConf),
                params: {
                    monitorId: data.monitorId,
                    sendTypes: data.sendTypes,
                    notifyUser: data.notifyUser.map(item => item.id),
                    periodType: data.periodType,
                    scheduleConf: data.scheduleConf
                }
            });
        }
    }

    resetScheduleConf = (type) => {
        const { params } = this.state;
        let scheduleConfObj = {
            beginDate: moment().format('YYYY-MM-DD'),
            endDate: moment().add(3, 'months').format('YYYY-MM-DD'),
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

        this.setState({
            scheduleConfObj,
            params: {
                ...params, 
                periodType: type,
                scheduleConf: JSON.stringify(scheduleConfObj)
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
        this.resetScheduleConf(type);
    }

    onSendTypeChange = (value) => {
        const { params } = this.state;
        this.setState({
            params: {...params, sendTypes: value}
        });
    }

    onBeginDateChange = (date, dateString) => {
        this.changeScheduleConfTime('beginDate', dateString,);
    }

    onEndDateChange = (date, dateString) => {
        this.changeScheduleConfTime('endDate', dateString,);
    }

    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.id} value={item.id.toString()}>{item.userName}</Option>
            )
        })
    }

    onNotifyUserChange = (value) => {
        const { params } = this.state;
        this.setState({
            params: {...params, notifyUser: value}
        });
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    save = () => {
        const { form } = this.props;
        const { params } = this.state;
        form.validateFields({ force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                RCApi.updateMonitor(params).then((res) => {
                    if (res.code === 1) {
                        message.success('更新成功！');
                        this.props.closeModal(true);
                    }
                });
            }
        });

    }

    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj, params } = this.state;
        let newParams = {};

        newParams[type] = value;
        this.setState({
            scheduleConfObj: {...scheduleConfObj, ...newParams},
            params: {...params, scheduleConf: JSON.stringify({...scheduleConfObj, ...newParams})}
        });
    }

    renderDynamic() {
        const { form, common, data, editStatus } = this.props;
        const { scheduleConfObj } = this.state;
        const { allDict, userList } = common;
        const { notifyUser, sendTypes } = data;
        const { getFieldDecorator } = form;

        let periodType = allDict.periodType ? allDict.periodType : [];
        let notifyType = allDict.notifyType ? allDict.notifyType : [];

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
                                    },{
                                        // validator: ctx.checkTimeS.bind(ctx)
                                    }
                                ],
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
                                    // validator: ctx.checkTimeE1.bind(ctx)
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

    closeModal = () => {
        const { form, data, closeModal } = this.props;

        this.initState(data);
        form.resetFields();
        closeModal(false);
    }

    render() {
        const { form, common, data, visible, closeModal } = this.props;
        const { scheduleConfObj, params } = this.state;
        const { getFieldDecorator } = form;
        const { allDict, userList } = common;
        const { notifyUser, sendTypes } = params;

        let periodType = allDict.periodType ? allDict.periodType : [],
            notifyType = allDict.notifyType ? allDict.notifyType : [];

        return (
            <Modal
                title="编辑执行信息"
                wrapClassName="editExecuteModal"
                maskClosable={false}
                visible={visible}
                width={'50%'}
                okText="保存"
                cancelText="取消"
                onOk={this.save}
                onCancel={this.closeModal}>  
                <Form>
                    <FormItem {...formItemLayout} label="调度周期">
                        {
                            getFieldDecorator('periodType', {
                                rules: [{ required: true, message: '执行周期不能为空' }], 
                                initialValue: scheduleConfObj.periodType.toString()
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
                        <FormItem {...formItemLayout} label="生效日期">
                            {
                                getFieldDecorator('beginDate', {
                                    initialValue: moment(scheduleConfObj.beginDate)
                                })(
                                    <DatePicker
                                        size="large"
                                        style={{ width: 150 }}
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
                                    initialValue: moment(scheduleConfObj.endDate)
                                })(
                                    <DatePicker
                                        size="large"
                                        style={{ width: 150 }}
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
                    
                    <FormItem {...formItemLayout} label="通知方式">
                        {
                            getFieldDecorator('sendTypes', {
                                rules: [{ required: true, message: '选择一种通知方式' }], 
                                initialValue: sendTypes.map(item => item.toString())
                            })(
                                <Checkbox.Group onChange={this.onSendTypeChange}>
                                    {
                                        notifyType.map((item) => {
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
                    
                    <FormItem {...formItemLayout} label="通知接收人">
                        {
                            getFieldDecorator('notifyUser', {
                                rules: [{ required: true, message: '接收人不能为空' }],
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
            </Modal>
        )
    }
}
ExecuteForm = Form.create()(ExecuteForm);