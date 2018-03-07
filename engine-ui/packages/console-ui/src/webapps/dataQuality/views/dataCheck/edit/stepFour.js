import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
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
            isInform: false
        }
    }

    componentDidMount() {
        
    }

    onExecuteTypeChange = (e) => {
        console.log(e)
        this.setState({ executeType: e.target.value });
        this.props.changeParams({
            executeType: e.target.value,
            scheduleConf: `{"min":0,"hour":0,"periodType":"2","beginDate":"2018-03-06","endDate":"2121-01-01"}`
        });
    }

    onInformChange = (e) => {
        console.log(e.target.checked);
        this.setState({ isInform: e.target.checked });
        if (!this.props.user.userList.length) {
            this.props.getUserList();
        }
    }

    onInformTypeChange = (value) => {
        console.log(value)
        const { notifyVO } = this.props.dataCheck.params;
        this.props.changeParams({
            notifyVO: { ...notifyVO, sendTypes: value }
        })
    }

    onDateChange = (date, dateString) => {
        console.log(date, dateString)
    }

    onTimeChange = (date, dateString) => {
        console.log(date, dateString)
    }

    onMinChange = (date, dateString) => {
        console.log(date, dateString)
    }

    onInformUserChange = (value) => {
        console.log(value)
        const { notifyVO } = this.props.dataCheck.params;
        this.props.changeParams({
            notifyVO: { ...notifyVO, receivers: value }
        })
    }

    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option value={item.id.toString()}>{item.userName}</Option>
            )
        })
    }

    prev = () => {
        this.props.navToStep(2);
    }

    add = () => {
        this.props.editCheck(this.props.dataCheck.params)
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { userList } = this.props.user;
        const { params } = this.props.dataCheck;
        const { executeType, isInform } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="执行时间" style={{ marginTop: 24, marginBottom: 5 }}>
                            {
                                getFieldDecorator('executeType', {
                                    rules: [{ required: true, message: '不能为空' }], initialValue: params.executeType || 0
                                })(
                                    <RadioGroup onChange={this.onExecuteTypeChange} size="default">
                                        <Radio value={0}>立即执行</Radio>
                                        <Radio value={1}>定时执行</Radio>
                                    </RadioGroup>
                                )
                            }
                        </FormItem>

                        {
                            executeType === 1
                            &&
                            <Row className="table-view">
                                <Col span={12} offset={6}>
                                    {
                                        getFieldDecorator('beginDate', {
                                            rules: [{ required: true }]
                                        })(
                                            <DatePicker
                                                format="YYYY-MM-DD"
                                                placeholder="开始时间"
                                                onChange={this.onDateChange}
                                                style={{ width: 150, marginRight: 15 }}
                                            />
                                        )
                                    }
                                    {
                                        getFieldDecorator('hour', {
                                            rules: [{ required: true }]
                                        })(
                                            <TimePicker
                                                format="HH"
                                                placeholder='小时'
                                                onChange={this.onTimeChange}
                                                style={{ marginRight: 15 }}
                                            />
                                        )
                                    }
                                    {
                                        getFieldDecorator('min', {
                                            rules: [{ required: true }]
                                        })(
                                            <TimePicker
                                                format="mm"
                                                placeholder='分钟'
                                                onChange={this.onMinChange}
                                            />
                                        )
                                    }
                                </Col>
                            </Row>
                        }

                        <FormItem {...formItemLayout} label="通知设置" style={{ marginTop: 10 }}>
                            {
                                getFieldDecorator('informSetting', {
                                    rules: [],
                                })(
                                    <Checkbox onChange={this.onInformChange}>执行完成后发送通知</Checkbox>
                                )
                            }
                        </FormItem>

                        {
                            isInform === true
                            &&
                            <Row>
                                <FormItem {...formItemLayout} label="通知方式">
                                    {
                                        getFieldDecorator('sendTypes', {
                                            rules: [{ required: true, message: '选择一种通知方式' }], initialValue: ['0']
                                        })(
                                            <Checkbox.Group onChange={this.onInformTypeChange}>
                                                <Checkbox value="0">邮件</Checkbox>
                                                <Checkbox value="1">短信</Checkbox>
                                            </Checkbox.Group>
                                        )
                                    }
                                </FormItem>
                                <FormItem {...formItemLayout} label="通知接收人">
                                    {
                                        getFieldDecorator('receivers', {
                                            rules: [{ required: true, message: '接收人不能为空' }],
                                        })(
                                            <Select mode="multiple" allowClear onChange={this.onInformUserChange}>
                                                {
                                                    this.renderUserList(userList)
                                                }
                                            </Select>
                                        )
                                    }
                                </FormItem>
                            </Row>
                        }
                    </Form>
                </div>
                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button className="m-l-8" type="primary" onClick={this.add}>新建</Button>
                    <Button className="m-l-8" type="primary"><Link to="/dq/dataCheck">校验列表</Link></Button>
                </div>
            </div>
        )
    }
}
StepFour = Form.create()(StepFour);