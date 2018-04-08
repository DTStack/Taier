import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import moment from 'moment';
import { Button, Form, Select, Input, Row, Col, Radio, TimePicker, DatePicker, Checkbox } from 'antd';

import { dataCheckActions } from '../../../actions/dataCheck';
import { formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { common } = state;
    return { common }
}

const mapDispatchToProps = dispatch => ({
    addCheck(params) {
        dispatch(dataCheckActions.addCheck(params));
    },
    updateCheck(params) {
        dispatch(dataCheckActions.updateCheck(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepFour extends Component {
    constructor(props) {
        super(props);
        this.state = {
            executeType: 0,
            isInform: false,
            scheduleConfObj: {
                beginDate: moment().format('YYYY-MM-DD'),
                hour: 0,
                min: 0,
                endDate: '2121-01-01',
                periodType: '2'
            }
        }
    }

    componentDidMount() {
        this.initState();
    }

    initState = () => {
        const { scheduleConf, notifyVO } = this.props.editParams;
        const { scheduleConfObj } = this.state;

        if (scheduleConf) {
            this.setState({ scheduleConfObj: JSON.parse(scheduleConf) });
        } else {
            this.props.changeParams({
                scheduleConf: JSON.stringify(scheduleConfObj)
            });
        }

        if (notifyVO) {
            this.setState({ isInform: true });
        } 
    }

    onExecuteTypeChange = (e) => {
        this.props.changeParams({
            executeType: e.target.value,
        });
    }

    onInformChange = (e) => {
        this.setState({ isInform: e.target.checked });

        if (!e.target.checked) {
            this.props.changeParams({
                notifyVO: undefined
            });
        }
    }

    onInformTypeChange = (value) => {
        const { notifyVO } = this.props.editParams;
        this.props.changeParams({
            notifyVO: { ...notifyVO, sendTypes: value }
        });
    }

    changeScheduleParams = (type, date, dateString) => {
        const { scheduleConfObj } = this.state;

        let newParams = {};
        newParams[type] = dateString;

        this.setState({
            scheduleConfObj: {...scheduleConfObj, ...newParams}
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({...scheduleConfObj, ...newParams})
        });
    }

    onInformUserChange = (value) => {
        const { notifyVO } = this.props.editParams;
        this.props.changeParams({
            notifyVO: { ...notifyVO, receivers: value }
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
                    this.props.updateCheck({...editParams});
                } else {
                    this.props.addCheck({...editParams});
                }
                location.href = "/dataQuality.html#/dq/dataCheck";
            }
        })

    }

    render() {
        const { form, common, editParams, editStatus } = this.props;
        const { isInform, scheduleConfObj } = this.state;
        const { executeType, notifyVO, scheduleConf } = editParams;
        const { getFieldDecorator } = form;

        let receivers, sendTypes;
        if (notifyVO) {
            receivers = notifyVO.receivers;
            sendTypes = notifyVO.sendTypes;
        }

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="执行时间" key="executeType">
                            {
                                getFieldDecorator('executeType', {
                                    rules: [{ required: true, message: '不能为空' }], 
                                    initialValue: executeType
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
                            <Row style={{ marginBottom: 24 }}>
                                <Col span={12} offset={6}>
                                    {
                                        getFieldDecorator('beginDate', {
                                            rules: [{ required: true }],
                                            initialValue: moment(scheduleConfObj.beginDate)
                                        })(
                                            <DatePicker
                                                format="YYYY-MM-DD"
                                                placeholder="开始时间"
                                                onChange={this.changeScheduleParams.bind(this, 'beginDate')}
                                                style={{ width: 150, marginRight: 15 }}
                                            />
                                        )
                                    }
                                    {
                                        getFieldDecorator('hour', {
                                            rules: [{ required: true }],
                                            initialValue: moment(scheduleConfObj.hour, 'HH')
                                        })(
                                            <TimePicker
                                                format="HH"
                                                placeholder='小时'
                                                onChange={this.changeScheduleParams.bind(this, 'hour')}
                                                style={{ marginRight: 15 }}
                                            />
                                        )
                                    }
                                    {
                                        getFieldDecorator('min', {
                                            rules: [{ required: true }],
                                            initialValue: moment(scheduleConfObj.min, 'mm')
                                        })(
                                            <TimePicker
                                                format="mm"
                                                placeholder='分钟'
                                                onChange={this.changeScheduleParams.bind(this, 'min')}
                                            />
                                        )
                                    }
                                </Col>
                            </Row>
                        }

                        <FormItem {...formItemLayout} label="通知设置">
                            {
                                getFieldDecorator('informSetting', {
                                    rules: [],
                                    valuePropName: 'checked',
                                    initialValue: isInform
                                })(
                                    <Checkbox onChange={this.onInformChange}>执行完成后发送通知</Checkbox>
                                )
                            }
                        </FormItem>

                        {
                            isInform === true
                            &&
                            <Row>
                                <FormItem {...formItemLayout} label="通知方式" key="sendTypes">
                                    {
                                        getFieldDecorator('sendTypes', {
                                            rules: [{ required: true, message: '选择通知方式' }], 
                                            initialValue: sendTypes ? sendTypes.map(item => item.toString()) : []
                                        })(
                                            <Checkbox.Group onChange={this.onInformTypeChange}>
                                                <Checkbox value="0">邮件</Checkbox>
                                                <Checkbox value="1">短信</Checkbox>
                                            </Checkbox.Group>
                                        )
                                    }
                                </FormItem>
                                
                                <FormItem {...formItemLayout} label="通知接收人" key='receivers'>
                                    {
                                        getFieldDecorator('receivers', {
                                            rules: [{ required: true, message: '选择接收人' }],
                                            initialValue: receivers ? receivers.map(item => item.toString()) : []
                                        })(
                                            <Select mode="multiple" allowClear onChange={this.onInformUserChange}>
                                                {
                                                    this.renderUserList(common.userList)
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
                    <Button className="m-l-8" type="primary" onClick={this.save}>{editStatus === 'edit' ? '保存' : '新建'}</Button>
                </div>
            </div>
        )
    }
}
StepFour = Form.create()(StepFour);