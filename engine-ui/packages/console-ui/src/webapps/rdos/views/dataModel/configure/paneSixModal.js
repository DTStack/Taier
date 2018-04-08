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

class AtomIndexDefineModal extends Component {

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
        const title = isEdit ? '编辑增量规则': '创建增量规则'

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
                        label="原子指标名称"
                        hasFeedback
                    >
                        {getFieldDecorator('indexAlias', {
                            rules: [{
                                required: true, message: '原子指标名称不可为空！',
                            }],
                            initialValue: data ? data.indexAlias : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="原子指标命名"
                        hasFeedback
                    >
                        {getFieldDecorator('indexName', {
                            rules: [{
                                required: true, message: '原子指标命名不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9]+$/,
                                message: '原子指标命名只能由字母、数字组成!',
                            }],
                            initialValue: data ? data.indexName : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标口径"
                        hasFeedback
                    >
                        {getFieldDecorator('indexDesc', {
                            rules: [{
                                max: 200,
                                message: '指标口径请控制在200个字符以内！',
                            }],
                            initialValue: data ? data.indexDesc : '',
                        })(
                            <Input type="textarea" rows={4} />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标类型"
                        hasFeedback
                    >
                        {getFieldDecorator('indexType', {
                            rules: [],
                            initialValue: data ? data.indexType : '1',
                        })(
                            <Select>
                                <Option value="1">原子指标</Option>
                                <Option value="2">修饰词</Option>
                                <Option value="3">衍生指标</Option>
                            </Select>,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据类型"
                        hasFeedback
                    >
                        {getFieldDecorator('dataType', {
                            rules: [],
                            initialValue: data ? data.dataType : 'string',
                        })(
                            <Select>
                                <Option value="string">string</Option>
                                <Option value="bigint">bigint</Option>
                            </Select>,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(AtomIndexDefineModal);
export default wrappedForm
