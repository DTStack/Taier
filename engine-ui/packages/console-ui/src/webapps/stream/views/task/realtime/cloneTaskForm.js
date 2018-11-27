import React, { Component } from 'react'
import { isArray, isNumber } from 'lodash';
import {
    Form, Input, Select,
    Radio, Modal
} from 'antd'

import { getContainer } from 'funcs';
import FolderPicker from './folderTree'
import HelpDoc from '../../helpDoc';
import { formItemLayout, TASK_TYPE, DATA_SYNC_TYPE } from '../../../comm/const'

const FormItem = Form.Item

class CloneTaskForm extends Component {
    submit = (e) => {
        e.preventDefault()

        const { handOk, form, taskInfo } = this.props
        const task = this.props.form.getFieldsValue()
        console.log(task)
        const fileds = ['taskName', 'nodePid']
        this.props.form.validateFields(fileds, (err) => {
            if (!err) {
                setTimeout(() => {
                    form.resetFields()
                }, 200)
                handOk(task)
            }
        });
    }

    cancle = () => {
        const { handCancel, form } = this.props
        this.setState({}, () => {
            handCancel()
            form.resetFields()
        })
    }

    render () {
        const {
            form, ayncTree, visible,
            taskInfo, taskRoot
        } = this.props

        const { getFieldDecorator } = form
        const cloneName = taskInfo.name;
        console.log(taskInfo)
        return (
            <div id="JS_task_modal_realtime">
                <Modal
                    title="克隆任务"
                    visible={visible}
                    onOk={this.submit}
                    onCancel={this.cancle}
                    getContainer={() => getContainer('JS_task_modal_realtime')}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="任务名称"
                            hasFeedback
                        >
                            {getFieldDecorator('taskName', {
                                rules: [{
                                    required: true, message: '任务名称不可为空！'
                                }, {
                                    pattern: /^[A-Za-z0-9_-]+$/,
                                    message: '任务名称只能由字母、数字、下划线组成!'
                                }, {
                                    max: 64,
                                    message: '任务名称不得超过64个字符！'
                                }],
                                initialValue: `${cloneName}_copy`
                            })(
                                <Input />
                            )}
                        </FormItem>

                        <FormItem
                            {...formItemLayout}
                            label="选择存储位置"
                            hasFeedback
                        >
                            {getFieldDecorator('nodePid', {
                                rules: [{
                                    required: true, message: '必须选择存储位置！'
                                }],
                                initialValue: taskInfo.nodePid
                            })(
                                <FolderPicker
                                    id="nodePid"
                                    placeholder="请选择存储位置"
                                    isPicker
                                    isFolderPicker
                                    treeData={taskRoot}
                                    loadData={ayncTree}
                                />
                            )}
                        </FormItem>

                        <FormItem
                            {...formItemLayout}
                            label="描述"
                            hasFeedback
                        >
                            {getFieldDecorator('taskDesc', {
                                rules: [{
                                    max: 200,
                                    message: '描述请控制在200个字符以内！'
                                }],
                                initialValue: taskInfo.taskDesc
                            })(
                                <Input type="textarea" rows={4} />
                            )}
                        </FormItem>

                        <FormItem style={{ display: 'none' }}>
                            {getFieldDecorator('taskId', {
                                initialValue: taskInfo.id
                            })(
                                <Input type="hidden"></Input>
                            )}
                        </FormItem>

                    </Form>
                </Modal>
            </div>
        )
    }
}
const wrappedForm = Form.create()(CloneTaskForm);
export default wrappedForm
