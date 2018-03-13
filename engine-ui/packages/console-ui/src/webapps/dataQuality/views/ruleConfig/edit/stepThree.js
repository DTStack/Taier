import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import moment from 'moment';
import { Button, Form, Select, Input, Row, Col, Radio, TimePicker, DatePicker, Checkbox } from 'antd';
import * as UserAction from '../../../actions/user';
import { formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { user } = state;
    return { user }
}

const mapDispatchToProps = dispatch => ({
    getUserList(params) {
        dispatch(UserAction.getUserList(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepFour extends Component {
    constructor(props) {
        super(props);
        this.state = {
            executeType: 0,
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                hour: 0,
                min: 0,
                endDate: '2121-01-01',
                periodType: '0'
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

    onDateChange = (date, dateString) => {
        this.changeScheduleParams(dateString, 'beginDate');
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

    onNotifyUserChange = (value) => {
        const { notifyUser } = this.props.editParams;
        this.props.changeParams({
            notifyUser: value
        })
    }

    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.id} value={item.id.toString()}>{item.userName}</Option>
            )
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
                if (editParams.id) {
                    this.props.updateRule({...editParams});
                } else {
                    this.props.addRule({...editParams});
                }
                location.href = "/dataQuality.html#/dq/rule";
            }
        })

    }

    render() {
        const { form, user, editParams, editStatus } = this.props;
        const { scheduleConfObj } = this.state;
        const { executeType, notifyUser, sendTypes } = editParams;
        const { getFieldDecorator } = form;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="执行周期" key="periodType">
                            {
                                getFieldDecorator('periodType', {
                                    rules: [{ required: true, message: '执行周期不能为空' }], 
                                    initialValue: scheduleConfObj.periodType
                                })(
                                    <Select onChange={this.onPeriodTypeChange}>
                                        <Option value="0">天</Option>
                                        <Option value="1">周</Option>
                                        <Option value="2">月</Option>
                                        <Option value="3">小时</Option>
                                    </Select>
                                )
                            }
                        </FormItem>

                        <FormItem {...formItemLayout} label="具体时间" key="executeTime">
                            {
                                getFieldDecorator('executeTime', {
                                    rules: [{ required: true, message: '执行时间不能为空' }], 
                                    initialValue: moment(scheduleConfObj.beginDate)
                                })(
                                    <DatePicker
                                        format="YYYY-MM-DD HH:mm"
                                        showTime
                                        placeholder="执行时间"
                                        onChange={this.onDateChange}
                                        style={{ width: 250 }}
                                    />
                                )
                            }
                        </FormItem>

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
                                    <Select mode="multiple" allowClear onChange={this.onNotifyUserChange}>
                                        {
                                            this.renderUserList(user.userList)
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