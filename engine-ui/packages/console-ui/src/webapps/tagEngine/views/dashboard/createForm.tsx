import * as React from 'react'
import { Form, Input, Modal } from 'antd'

import { formItemLayout } from '../../comm/const'

const FormItem = Form.Item

class ProjectForm extends React.Component<any, any> {
    submit = (e: any) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const project = form.getFieldsValue()
        form.validateFields((err: any) => {
            if (!err) {
                setTimeout(() => { form.resetFields() }, 1000)
                onOk(project)
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
        const { title, visible } = this.props;
        const rowFix = {
            rows: 4
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
                        label="项目名称"
                        hasFeedback
                    >
                        {getFieldDecorator('projectName', {
                            rules: [{
                                required: true, message: '项目名称不可为空！'
                            }, {
                                // pattern: /^[A-Za-z0-9_]*([A-Za-z])[A-Za-z0-9_]*$/,
                                // message: '项目名称由字母、数字、下划线组成!'
                                pattern: /^[A-Za-z0-9_\u4e00-\u9fa5]+$/,
                                message: '项目名称只能由中文、字母、数字和下划线组成'
                            }, {
                                max: 80,
                                message: '项目名称不得超过80个字符！'
                            }]
                        })(
                            <Input placeholder="请输入项目名称" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目显示名称"
                        hasFeedback
                    >
                        {getFieldDecorator('projectAlias', {
                            rules: [{
                                max: 80,
                                message: '项目别名不得超过80个字符！'
                            }, {
                                pattern: /^[A-Za-z0-9_\u4e00-\u9fa5]+$/,
                                message: '项目别名只能由中文、字母、数字和下划线组成'
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
                                max: 500,
                                message: '项目描述不得超过500个字符！'
                            }]
                        })(
                            <Input type="textarea" placeholder="请输入描述信息，限制在500个字符以内" {...rowFix}/>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedProjectForm = Form.create<any>()(ProjectForm);
export default wrappedProjectForm
