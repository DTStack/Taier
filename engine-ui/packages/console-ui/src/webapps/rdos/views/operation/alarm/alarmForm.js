import React, { Component } from 'react'
import {
    Form, Input,
    Select, Modal, Checkbox
} from 'antd'

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group

class AlarmForm extends Component {
    state = {
        senderTypes: []
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const { alarmInfo, visible } = nextProps;
        if (visible && this.props.visible != visible) {
            this.setState({
                senderTypes: alarmInfo.senderTypes || []
            })
        }
    }

    submit = (e) => {
        e.preventDefault()
        const ctx = this
        const { onOk } = this.props
        const alarm = this.props.form.getFieldsValue()
        alarm.receiveUsers = alarm.receiveUsers.join(',')
        this.props.form.validateFields((err) => {
            if (!err) {
                ctx.props.form.resetFields()
                onOk(alarm)
            }
        });
    }

    receiveChange = (e) => {
        this.setState({ selectedReceive: e.target.value })
    }

    cancle = () => {
        const { form, onCancel } = this.props
        form.resetFields()
        onCancel()
    }

    senderTypesChange (values) {
        this.setState({
            senderTypes: values
        });
    }

    render () {
        const {
            form, title, projectUsers,
            visible, alarmInfo, taskList, user
        } = this.props
        const { getFieldDecorator } = form

        const { senderTypes } = this.state;

        const taskItems = taskList && taskList.length > 0
            ? taskList.map((item) => {
                return (<Option key={item.id} value={item.id} name={item.name}>
                    {item.name}
                </Option>)
            }) : []

        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (<Option key={item.id} value={item.userId} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []

        const receivers = alarmInfo.receiveUsers
            ? alarmInfo.receiveUsers.map(item => item.userId) : []

        let showDD = false;
        if (senderTypes.indexOf(4) > -1) {
            showDD = true;
        }
        return (
            <Modal
                title={title}
                wrapClassName="vertical-center-modal"
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="告警规则名称"
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true, message: '告警规则名称不可为空！'
                            }, {
                                max: 30,
                                message: '告警规则名称不得超过30个字符！'
                            }],
                            initialValue: alarmInfo.alarmName || ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="任务名称"
                    >
                        {getFieldDecorator('taskId', {
                            rules: [{
                                required: true, message: '请您选择所要告警的任务！'
                            }],
                            initialValue: alarmInfo.taskId || ''
                        })(
                            <Select
                                showSearch
                                size='Default'
                                style={{ width: '100%' }}
                                placeholder="任务任务"
                                optionFilterProp="name"
                            >
                                { taskItems }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="告警方式"
                    >
                        {getFieldDecorator('senderTypes', {
                            rules: [{
                                required: true, message: '请您选择所要告警的任务！'
                            }],
                            initialValue: alarmInfo.senderTypes || [1] // 1-邮件， 2-短信
                        })(
                            <CheckboxGroup onChange={this.senderTypesChange.bind(this)}>
                                <Checkbox value={1}>邮件</Checkbox>
                                <Checkbox value={2}>短信</Checkbox>
                                <Checkbox value={4}>钉钉</Checkbox>
                            </CheckboxGroup>
                        )}
                    </FormItem>
                    {showDD && <FormItem
                        {...formItemLayout}
                        label="webhook"
                    >
                        {getFieldDecorator('webhook', {
                            rules: [{
                                required: true, message: 'webhook不能为空'
                            }],
                            initialValue: alarmInfo.webhook || ''
                        })(
                            <Input />
                        )}
                    </FormItem>}
                    <FormItem
                        {...formItemLayout}
                        label="触发方式"
                    >
                        {getFieldDecorator('myTrigger', {
                            rules: [{
                                required: true, message: '请您选择任务触发方式！'
                            }],
                            initialValue: alarmInfo.myTrigger || 0 // 任务失败
                        })(
                            <Select>
                                <Option value={0}>任务失败</Option>
                                <Option value={3}>任务停止</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="接收人"
                    >
                        {getFieldDecorator('receiveUsers', {
                            rules: [{
                                required: true, message: '请您选择接收人!'
                            }],
                            initialValue: receivers || user.id
                        })(
                            <Select
                                showSearch
                                mode="multiple"
                                style={{ width: '100%' }}
                                placeholder="请选择接收人"
                                optionFilterProp="name"
                            >
                                {userItems}
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(AlarmForm);
export default wrappedForm
