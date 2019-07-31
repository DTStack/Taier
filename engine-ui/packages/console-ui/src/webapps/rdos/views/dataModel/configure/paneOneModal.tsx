import * as React from 'react'
import { isEmpty } from 'lodash';
import {
    Form, Input,
    Modal, Checkbox
} from 'antd'

import { formItemLayout } from '../../../comm/const'
import LifeCycle from '../../dataManage/lifeCycle'

const FormItem = Form.Item

class ModelLevelModal extends React.Component<any, any> {
    state: any = { }

    submit = (e: any) => {
        e.preventDefault()
        const { handOk, form, data } = this.props

        const formData = this.props.form.getFieldsValue()
        formData.type = 1; // 模型层级
        formData.isEdit = data && !isEmpty(data) ? true : undefined;
        formData.depend = !formData.depend ? 0 : 1;
        formData.id = formData.isEdit ? data.id : undefined;

        this.props.form.validateFields((err: any) => {
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
            form.resetFields()
            handCancel()
        })
    }

    lifeCycleChange = (value: any) => {
        this.props.form.setFieldsValue({ 'lifeDay': value })
    }

    render () {
        const {
            form, visible, data
        } = this.props

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑模型层级' : '创建模型层级'

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
                        label="层级名称"
                        hasFeedback
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true, message: '层级名称不可为空！'
                            }, {
                                max: 64,
                                message: '层级名称不得超过64个字符！'
                            }],
                            initialValue: data ? data.name : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="层级前缀"
                        hasFeedback
                    >
                        {getFieldDecorator('prefix', {
                            rules: [{
                                required: true, message: '层级前缀不可为空！'
                            }, {
                                pattern: /^[A-Za-z0-9]+$/,
                                message: '层级前缀只能由字母、数字组成!'
                            }, {
                                max: 64,
                                message: '层级前缀不得超过64个字符！'
                            }],
                            initialValue: data ? data.prefix : ''
                        })(
                            <Input />
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
                            valuePropName: 'checked',
                            initialValue: !((isEdit && data.depend === 0))
                        })(
                            <Checkbox>&nbsp;</Checkbox>
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
                                message: '层级说明请控制在200个字符以内！'
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
const wrappedForm = Form.create<any>()(ModelLevelModal);
export default wrappedForm
