import * as React from 'react'
import moment from 'moment'
import {
    Form, Input, Checkbox, InputNumber,
    Select as mSelect, Modal, TimePicker,
    Tooltip, Icon
} from 'antd'

import pureRender from 'utils/pureRender';

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item
const Select: any = mSelect;
const Option: any = Select.Option
const CheckboxGroup = Checkbox.Group

@pureRender
class AlarmForm extends React.Component<any, any> {
    state: any = {
        myTrigger: 0,
        triggerTimeType: 0, // 触发时间类型
        runHour: '',
        runMin: '',
        senderTypes: []
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const { alarmInfo, visible } = nextProps;
        if (visible && this.props.visible != visible) {
            this.setState({
                myTrigger: alarmInfo.myTrigger || 0,
                senderTypes: alarmInfo.senderTypes || []
            })
        }
    }

    submit = (e: any) => {
        e.preventDefault()
        const ctx = this
        const { onOk, form } = this.props
        const alarm = form.getFieldsValue()
        const fields: any = ['name', 'taskId', 'senderTypes', 'myTrigger', 'receiveUsers']
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
        alarm.isTaskHolder = alarm.isTaskHolder ? 1 : 0;
        alarm.receiveUsers = alarm.receiveUsers.join(',')
        form.validateFields(fields, async (err: any) => {
            if (!err) {
                let res = await onOk(alarm);
                if (res) {
                    ctx.setState({ myTrigger: 0, senderTypes: [] })
                    setTimeout(() => form.resetFields(), 300)
                }
            }
        });
    }

    cancle = () => {
        const { form, onCancel } = this.props
        form.resetFields()
        this.setState({ myTrigger: 0 })
        onCancel()
    }

    receiveChange = (e: any) => {
        this.setState({ selectedReceive: e.target.value })
    }

    onChangeTrigger = (value: any) => {
        this.setState({ myTrigger: value })
    }

    onChangeRunHour = (value: any) => {
        console.log('onChangeRunHour:', value);
        this.props.form.setFieldsValue({ 'runTime': value })
        this.setState({ runHour: value })
    }

    onChangeRunMin = (value: any) => {
        console.log('onChangeRunMin:', value);
        this.props.form.setFieldsValue({ 'runTime': value })
        this.setState({ runMin: value })
    }

    senderTypesChange (values: any) {
        this.setState({
            senderTypes: values
        });
    }

    render () {
        const {
            form, title, projectUsers,
            visible, alarmInfo, taskList
        } = this.props

        const { getFieldDecorator } = form

        const taskItems = taskList && taskList.length > 0
            ? taskList.map((item: any) => {
                return (<Option key={item.id} value={item.id} name={item.name}>
                    {item.name}
                </Option>)
            }) : []

        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item: any) => {
                return (<Option key={item.id} value={item.userId} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []

        const receivers = alarmInfo.receiveUsers
            ? alarmInfo.receiveUsers.map((item: any) => item.userId) : []

        const { myTrigger, senderTypes } = this.state;

        let showDD = false;
        if (senderTypes.indexOf(4) > -1) {
            showDD = true;
        }
        console.log('myTrigger:', myTrigger)

        const text = (
            <>
                <p>定时未完成：从计划时间开始计算，超出定时时间，若处于成功/失败之外的状态，会触发告警。</p>
                <p>超时未完成：从任务开始运行计算，超出指定时间，若处于未完成的状态，会触发告警。</p>
                <p>任务重跑、补数据时，不会触发告警。</p>
            </>
        )
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
                                max: 64,
                                message: '告警规则名称不得超过64个字符！'
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
                        &nbsp;
                        <Tooltip placement="top" title={text} arrowPointAtCenter overlayClassName="extremely-big-tooltip">
                            <Icon type="question-circle-o" />
                        </Tooltip>
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="触发时刻"
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
                        label="开始运行起"
                    >
                        {getFieldDecorator('runTime', { // 换算成秒
                            rules: [{
                                required: true, message: '请您设置运行时长触发条件！'
                            }]
                        })(
                            <div>
                                <InputNumber min={0} onChange={this.onChangeRunHour} />小时&nbsp;
                                <InputNumber min={0} max={59} onChange={this.onChangeRunMin} />分钟
                            </div>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="任务责任人"
                    >
                        {getFieldDecorator('isTaskHolder', {
                            rules: [{
                                required: true
                            }],
                            initialValue: alarmInfo.isTaskHolder,
                            valuePropName: 'checked'
                        })(
                            <Checkbox>接收告警</Checkbox>
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
const wrappedForm = Form.create<any>()(AlarmForm);
export default wrappedForm
