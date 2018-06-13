import React, { Component } from 'react'
import { Form, Input, Modal, InputNumber } from 'antd'

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item

class TableApply extends Component {

    submit = (e) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const formData = form.getFieldsValue()
        form.validateFields((err) => {
            if (!err) {
                setTimeout(() => { form.resetFields() }, 200)
                onOk(formData)
            }
        });
    }

    cancle = () => {
        const { onCancel, form } = this.props
        onCancel()
        form.resetFields()
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, table } = this.props;
        return (
            <Modal
                title="申请授权"
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="正在申请的表为"
                        hasFeedback
                    >
                        <span>{table.tableName}</span>
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="权限有效期"
                        hasFeedback
                    >
                        {getFieldDecorator('day', {
                            rules: [],
                        })(
                            <InputNumber min={1} placeholder="请输入申请时长（天）" style={{width: "100%"}}/>,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="申请理由"
                        hasFeedback
                    >
                        {getFieldDecorator('applyReason', {
                            rules: [{
                                max: 200,
                                message: '申请理由请控制在200个字符以内！',
                            }],
                        })(
                            <Input type="textarea" rows={4} placeholder="请输入申请理由" />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create()(TableApply);
export default FormWrapper
