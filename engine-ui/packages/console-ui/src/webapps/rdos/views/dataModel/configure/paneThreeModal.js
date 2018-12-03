import React, { Component } from 'react'
import { isEmpty } from 'lodash';
import {
    Form, Input,
    Modal
} from 'antd'

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item

class FreshFrequencyModal extends Component {
    state = {}

    submit = (e) => {
        e.preventDefault()
        const { handOk, form, data } = this.props

        const formData = this.props.form.getFieldsValue()
        formData.type = 3; // 刷新频率
        formData.isEdit = data && !isEmpty(data) ? true : undefined;
        formData.id = formData.isEdit ? data.id : undefined;

        this.props.form.validateFields((err) => {
            if (!err) {
                setTimeout(() => {
                    form.resetFields()
                }, 200)
                handOk(formData)
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
            form, visible, data
        } = this.props

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑刷新频率' : '创建刷新频率'

        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
                maskClosable={false}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="刷新频率"
                        hasFeedback
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true, message: '刷新频率不可为空！'
                            }, {
                                max: 64,
                                message: '刷新频率不得超过64个字符！'
                            }],
                            initialValue: data ? data.name : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="刷新方式标识"
                        hasFeedback
                    >
                        {getFieldDecorator('prefix', {
                            rules: [{
                                required: true, message: '刷新方式标识不可为空！'
                            }, {
                                pattern: /^[A-Za-z0-9]+$/,
                                message: '刷新方式标识只能由字母、数字组成!'
                            }, {
                                max: 128,
                                message: '刷新方式标识不得超过128个字符！'
                            }],
                            initialValue: data ? data.prefix : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="刷新频率说明"
                        hasFeedback
                    >
                        {getFieldDecorator('modelDesc', {
                            rules: [{
                                max: 200,
                                message: '增量说明请控制在200个字符以内！'
                            }],
                            initialValue: data ? data.modelDesc : ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(FreshFrequencyModal);
export default wrappedForm
