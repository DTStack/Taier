import React, { Component } from 'react'
import { isArray, isNumber, isEmpty } from 'lodash';
import {
    Form, Input, Icon, Select,
    Radio, Modal,
} from 'antd'

import { formItemLayout } from '../../../comm/const'
import LifeCycle from '../../dataManage/lifeCycle'

const FormItem = Form.Item
const RadioGroup = Radio.Group
const Option = Select.Option;

class IncrementDefineModal extends Component {

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
                        label="增量名称"
                        hasFeedback
                    >
                        {getFieldDecorator('incrementName', {
                            rules: [{
                                required: true, message: '刷新频率不可为空！',
                            }],
                            initialValue: data ? data.incrementName : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="增量方式标识"
                        hasFeedback
                    >
                        {getFieldDecorator('sign', {
                            rules: [{
                                required: true, message: '增量方式标识不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9]+$/,
                                message: '增量方式标识只能由字母、数字组成!',
                            }],
                            initialValue: data ? data.sign : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(IncrementDefineModal);
export default wrappedForm
