import React, { Component } from 'react'
import {
    Form, Input, InputNumber,
    Select, Modal, Checkbox
} from 'antd'

import { formItemLayout, TASK_TYPE, alarmTriggerType } from '../../../../../../comm/const'
import HelpDoc from '../../../../../helpDoc';

const FormItem = Form.Item
const Option = Select.Option
const CheckboxGroup = Checkbox.Group

class AlarmForm extends Component {
    state = {
        senderTypes: [],
        myTrigger: alarmTriggerType.TASK_FAIL
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { alarmInfo = {}, visible } = nextProps;
        if (visible && this.props.visible != visible) {
            this.props.form.resetFields();
            this.setState({
                senderTypes: alarmInfo.senderTypes || [],
                myTrigger: alarmInfo.myTrigger || alarmTriggerType.TASK_FAIL
            })
        }
    }

    submit = (e) => {
        e.preventDefault()
        const ctx = this
        const { addAlarm, updateAlarm, alarmInfo } = this.props
        const alarm = this.props.form.getFieldsValue()
        alarm.receiveUsers = alarm.receiveUsers.join(',')
        /**
         * 服务端统一参数，但是本地存的时候需要区分，否则会导致formItem的id重复而混淆。
         */
        alarm.threshold = alarm.delayNum || alarm.delayNumP;
        alarm.delayNum = undefined;
        alarm.delayNumP = undefined;
        this.props.form.validateFields((err) => {
            if (!err) {
                if (alarmInfo) {
                    updateAlarm(alarm).then((isSuccess) => {
                        isSuccess && ctx.props.form.resetFields()
                    })
                } else {
                    addAlarm(alarm).then((isSuccess) => {
                        isSuccess && ctx.props.form.resetFields()
                    })
                }
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
    changeMyTrigger (value) {
        this.setState({
            myTrigger: value
        })
    }
    render () {
        let showDD = false;
        let {
            form, title, projectUsers,
            visible, alarmInfo, user, taskName, data
        } = this.props
        const { getFieldDecorator } = form
        const { senderTypes, myTrigger } = this.state;
        let isFlinkSQL = data.taskType == TASK_TYPE.SQL;
        const isDelayTrigger = myTrigger == alarmTriggerType.DELAY_COST || myTrigger == alarmTriggerType.DELAY_COST_P;
        alarmInfo = alarmInfo || {};

        const receivers = alarmInfo.receiveUsers
            ? alarmInfo.receiveUsers.map(item => item.userId) : []
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (<Option key={item.userId} value={item.userId} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []

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
                maskClosable={false}
                width={550}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="告警规则名称"
                        hasFeedback
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
                        hasFeedback
                    >
                        {getFieldDecorator('taskId', {
                            rules: [{
                                required: true, message: '请您选择所要告警的任务！'
                            }],
                            initialValue: alarmInfo.taskName || taskName || ''
                        })(
                            <Input disabled />
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
                            initialValue: myTrigger
                        })(
                            <Select onChange={this.changeMyTrigger.bind(this)}>
                                <Option key={alarmTriggerType.TASK_FAIL} value={alarmTriggerType.TASK_FAIL} >任务失败</Option>
                                <Option key={alarmTriggerType.TASK_STOP} value={alarmTriggerType.TASK_STOP} >任务停止</Option>
                                {isFlinkSQL && [
                                    <Option key={alarmTriggerType.DELAY_COST} value={alarmTriggerType.DELAY_COST} >延迟消费数</Option>,
                                    <Option key={alarmTriggerType.DELAY_COST_P} value={alarmTriggerType.DELAY_COST_P} >延迟消费比例</Option>
                                ]}
                            </Select>
                        )}
                        {isFlinkSQL && <HelpDoc doc="alarmWarning" />}
                    </FormItem>
                    {myTrigger == alarmTriggerType.DELAY_COST ? (
                        <FormItem
                            {...formItemLayout}
                            label="延迟消费数量"
                        >
                            {getFieldDecorator('delayNum', {
                                rules: [{
                                    required: true, message: '请填写延迟消费数量！'
                                }],
                                initialValue: alarmInfo.myTrigger == alarmTriggerType.DELAY_COST ? alarmInfo.threshold : undefined
                            })(
                                <InputNumber precision={0} style={{ width: 'calc(100% - 30px )' }} placeholder="请输入延迟消费数量" />
                            )}
                            <span style={{ paddingLeft: '8px' }}>条</span>
                        </FormItem>
                    ) : null}
                    {myTrigger == alarmTriggerType.DELAY_COST_P ? (
                        <FormItem
                            {...formItemLayout}
                            label="延迟消费/总消息数"
                        >
                            {getFieldDecorator('delayNumP', {
                                rules: [{
                                    required: true, message: '请填写延迟消费比例！'
                                }],
                                initialValue: alarmInfo.myTrigger == alarmTriggerType.DELAY_COST_P ? alarmInfo.threshold : undefined
                            })(
                                <InputNumber min={0} max={100} style={{ width: 'calc(100% - 30px )' }} placeholder="请输入延迟消费比例" />
                            )}
                            <span style={{ paddingLeft: '8px' }}>%</span>
                        </FormItem>
                    ) : null}
                    {isFlinkSQL && isDelayTrigger ? <FormItem
                        {...formItemLayout}
                        label="告警抑制"
                    >
                            30分钟内，触发超过
                        {getFieldDecorator('alarmTimes', {
                            initialValue: alarmInfo.alarmTimes
                        })(
                            <InputNumber precision={0} min={1} max={999} style={{ width: '48px', margin: '0px 5px' }} />
                        )}
                            次延迟消费告警后，1小时内不再发送
                    </FormItem> : null
                    }
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
                                style={{ width: 200 }}
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
