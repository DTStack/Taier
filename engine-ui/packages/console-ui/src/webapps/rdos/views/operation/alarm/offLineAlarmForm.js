import React, { Component } from 'react'
import moment from 'moment'
import { isEmpty } from 'lodash'
import {
    Form, Input, Checkbox, InputNumber,
    Select, Modal, TimePicker
} from 'antd'

import pureRender from 'utils/pureRender';

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group

@pureRender
class AlarmForm extends Component {
    state = {
        myTrigger: 0,
        triggerTimeType: 0, // 触发时间类型
        runHour: '',
        runMin: '',
        senderTypes: []
    }

    componentWillReceiveProps (nextProps) {
        const { alarmInfo, visible } = nextProps;
        if (visible && this.props.visible != visible) {
            this.setState({
                myTrigger: alarmInfo.myTrigger || 0,
                senderTypes: alarmInfo.senderTypes || []
            })
        }
    }

    submit = (e) => {
        e.preventDefault()
        const ctx = this
        const { onOk, form } = this.props
        const alarm = form.getFieldsValue()
        const fields = ['name', 'taskId', 'senderTypes', 'myTrigger', 'receiveUsers']
        if (alarm.myTrigger === 4) { // 定时未完成
            fields.push('uncompleteTime')
            alarm.uncompleteTime = moment(alarm.uncompleteTime).format('HH:mm')
        } else if (alarm.myTrigger === 5) { // 超时未完成,
            fields.push('runTime')
            // 转换小时和分钟成秒，然后赋值给未完成字段
            const { runHour, runMin } = this.state
            const hour = runHour ? runHour * 60 * 60 : 0
            const min = runMin ? runMin * 60 : 0
            alarm.uncompleteTime = hour + min
        }

        if (alarm.senderTypes.indexOf(4) > -1) {
            fields.push('webhook')
        }

        alarm.receiveUsers = alarm.receiveUsers.join(',')
        form.validateFields(fields, (err) => {
            if (!err) {
                ctx.setState({ myTrigger: 0, senderTypes: [] })
                setTimeout(() => form.resetFields(), 300)
                onOk(alarm)
            }
        });
    }

    cancle = () => {
        const { form, onCancel } = this.props
        form.resetFields()
        this.setState({ myTrigger: 0 })
        onCancel()
    }

    receiveChange = (e) => {
        this.setState({ selectedReceive: e.target.value })
    }

    onChangeTrigger = (value) => {
        this.setState({ myTrigger: value })
    }

    onChangeRunHour = (value) => {
        console.log('onChangeRunHour:', value);
        this.props.form.setFieldsValue({ 'runTime': value })
        this.setState({ runHour: value })
    }

    onChangeRunMin = (value) => {
        console.log('onChangeRunMin:', value);
        this.props.form.setFieldsValue({ 'runTime': value })
        this.setState({ runMin: value })
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

        const { myTrigger, triggerTimeType, senderTypes } = this.state;
        const display = myTrigger === 2 ? 'block' : 'none'

        let showDD = false;
        if (senderTypes.indexOf(4) > -1) {
            showDD = true;
        }
        console.log('myTrigger:', myTrigger)

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
                                required: true, message: '请您选择告警通知的方式！'
                            }],
                            initialValue: alarmInfo.senderTypes || [1] // 1：邮件、2：短信、3：钉钉
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
                            <Select onChange={this.onChangeTrigger}>
                                <Option value={0}>任务失败</Option>
                                <Option value={3}>任务停止</Option>
                                <Option value={4}>定时未完成</Option>
                                <Option value={5}>超时未完成</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="定时时长"
                        style={{ display: myTrigger === 4 ? 'block' : 'none' }}
                    >
                        {getFieldDecorator('uncompleteTime', {
                            rules: [{
                                required: true, message: '请您设置任务的定时时长！'
                            }]
                        })(
                            <TimePicker format={'HH:mm'} style={{ width: '100%' }} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        style={{ display: myTrigger === 5 ? 'block' : 'none' }}
                        label="运行时长"
                    >
                        {getFieldDecorator('runTime', { // 换算成秒
                            rules: [{
                                required: true, message: '请您设置运行时长触发条件！'
                            }]
                        })(
                            <div>
                                <InputNumber min={0} onChange={this.onChangeRunHour} />小时&nbsp;
                                <InputNumber min={0} max={59} onChange={this.onChangeRunHour} />分钟
                            </div>
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
                            initialValue: receivers
                        })(
                            <Select
                                showSearch
                                mode="multiple"
                                size='Default'
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
