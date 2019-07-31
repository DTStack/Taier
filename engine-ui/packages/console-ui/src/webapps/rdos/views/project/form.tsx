import * as React from 'react'
import { Form, Input, Modal, Switch } from 'antd'

import { formItemLayout } from '../../comm/const'

const FormItem = Form.Item

class ProjectForm extends React.Component<any, any> {
    state: any = {
        loading: false
    }
    submit = (e: any) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const project = form.getFieldsValue()
        form.validateFields(async (err: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                project.scheduleStatus = project.scheduleStatus ? 0 : 1;
                project.isAllowDownload = project.isAllowDownload ? 1 : 0;
                let isSuccess = await onOk(project);
                this.setState({
                    loading: false
                })
                if (isSuccess) {
                    form.resetFields()
                }
            }
        });
    }

    cancle = () => {
        const { onCancel, form } = this.props
        onCancel()
        form.resetFields()
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { title, visible } = this.props
        const { loading } = this.state;
        return (
            <Modal
                title={title}
                wrapClassName="vertical-center-modal"
                visible={visible}
                confirmLoading={loading}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="项目标识"
                        hasFeedback
                    >
                        {getFieldDecorator('projectName', {
                            rules: [{
                                required: true, message: '项目标识不可为空！'
                            }, {
                                pattern: /^([A-Za-z])[A-Za-z0-9_]*$/,
                                message: '项目标识须由字母开头，且由字母、数字、下划线组成!'
                            }, {
                                max: 20,
                                message: '项目标识不得超过20个字符！'
                            }]
                        })(
                            <Input placeholder="请输入项目标识" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目显示名称"
                        hasFeedback
                    >
                        {getFieldDecorator('projectAlias', {
                            rules: [{
                                max: 20,
                                message: '项目别名不得超过20个字符！'
                            }]
                        })(
                            <Input placeholder="请输入项目别名" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目描述"
                        hasFeedback
                    >
                        {getFieldDecorator('projectDesc', {
                            rules: [{
                                max: 200,
                                message: '项目描述请控制在200个字符以内！'
                            }]
                        })(
                            <Input type="textarea" rows={4} placeholder="项目描述请控制在200个字符以内" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="启动周期调度"
                    >
                        {getFieldDecorator('scheduleStatus', {
                            rules: [{
                                required: true
                            }],
                            valuePropName: 'checked',
                            initialValue: true
                        })(
                            <Switch checkedChildren="开" unCheckedChildren="关" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="下载SELECT结果"
                    >
                        {getFieldDecorator('isAllowDownload', {
                            rules: [{
                                required: true
                            }],
                            valuePropName: 'checked',
                            initialValue: true
                        })(
                            <Switch checkedChildren="开" unCheckedChildren="关" />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedProjectForm = Form.create<any>()(ProjectForm);
export default wrappedProjectForm
