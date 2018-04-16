import React, { Component } from 'react'
import { isArray, isNumber, isEmpty } from 'lodash';
import {
    Form, Input, Icon, Select,
    Radio, Modal, Checkbox,
} from 'antd'

import Api from '../../../api'
import { formItemLayout } from '../../../comm/const'
import LifeCycle from '../../dataManage/lifeCycle'

const FormItem = Form.Item
const RadioGroup = Radio.Group
const Option = Select.Option;

class ModelLevelModal extends Component {

    state = { }


    submit = (e) => {
        e.preventDefault()
        const { handOk, form, data } = this.props

        const formData = this.props.form.getFieldsValue()
        formData.type = 1; // 模型层级
        formData.isEdit = data && !isEmpty(data) ? true : undefined;
        formData.depend = !formData.depend ? 0 : 1;
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

    lifeCycleChange = (value) => {
        this.props.form.setFieldsValue({'lifeDay': value})
    }

    render() {
        const {
            form, visible, data
        } = this.props

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑模型层级': '创建模型层级'

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
                        label="层级名称"
                        hasFeedback
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true, message: '层级名称不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '层级名称只能由字母、数字、下划线组成!',
                            }, {
                                max: 64,
                                message: '层级名称不得超过64个字符！',
                            }],
                            initialValue: data ? data.name : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="层级前缀"
                        hasFeedback
                    >
                        {getFieldDecorator('prefix', {
                            rules: [{
                                required: true, message: '层级前缀不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '层级前缀只能由字母、数字、下划线组成!',
                            }, {
                                max: 64,
                                message: '层级前缀不得超过64个字符！',
                            }],
                            initialValue: data ? data.prefix : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    
                    <FormItem
                        {...formItemLayout}
                        label="生命周期"
                    >
                        {getFieldDecorator('lifeDay', {
                            rules: [{
                                required: true,
                                message: '生命周期不可为空！'
                            }],
                            initialValue: data ? data.lifeDay : 90
                        })(
                            <LifeCycle
                                width={120}
                                onChange={this.lifeCycleChange} 
                            />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="是否记入层级依赖"
                    >
                        {getFieldDecorator('depend', {
                            rules: [],
                            initialValue: data && data.depend === 1 ? true : false,
                        })(
                            <Checkbox defaultChecked> </Checkbox>,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="层级说明"
                        hasFeedback
                    >
                        {getFieldDecorator('modelDesc', {
                            rules: [{
                                max: 200,
                                message: '层级说明请控制在200个字符以内！',
                            }],
                            initialValue: data ? data.modelDesc : '',
                        })(
                            <Input type="textarea" rows={4} />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(ModelLevelModal);
export default wrappedForm
