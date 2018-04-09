import React, { Component } from 'react'
import { isArray, isNumber, isEmpty } from 'lodash';
import {
    Form, Input, Icon, Select,
    Radio, Modal,
} from 'antd'

import Api from '../../../api'
import { formItemLayout } from '../../../comm/const'
import LifeCycle from '../../dataManage/lifeCycle'

const FormItem = Form.Item
const RadioGroup = Radio.Group
const Option = Select.Option;

class SubjectDomainModal extends Component {

    state = { }

    submit = (e) => {
        e.preventDefault()
        const { handOk, form } = this.props

        const formData = this.props.form.getFieldsValue()
  
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
        this.setState({ }, () => {
            handCancel()
            form.resetFields()
        })
    }

    render() {

        const {
            form, visible, data
        } = this.props

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑主题域': '创建主题域'

        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="主题域名称"
                        hasFeedback
                    >
                        {getFieldDecorator('domainName', {
                            rules: [{
                                required: true, message: '主题域名称不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '主题域名称只能由字母、数字、下划线组成!',
                            }, {
                                max: 64,
                                message: '主题域名称不得超过64个字符！',
                            }],
                            initialValue: data ? data.domainName : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="主题域前缀"
                        hasFeedback
                    >
                        {getFieldDecorator('domainPrefix', {
                            rules: [{
                                required: true, message: '主题域前缀不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '主题域前缀只能由字母、数字、下划线组成!',
                            }, {
                                max: 64,
                                message: '主题域前缀不得超过64个字符！',
                            }],
                            initialValue: data ? data.domainPrefix : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="主题域说明"
                        hasFeedback
                    >
                        {getFieldDecorator('domainDesc', {
                            rules: [{
                                max: 200,
                                message: '主题域说明请控制在200个字符以内！',
                            }],
                            initialValue: data ? data.levelDesc : '',
                        })(
                            <Input type="textarea" rows={4} />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(SubjectDomainModal);
export default wrappedForm
