import * as React from 'react'
import { isEmpty } from 'lodash';
import {
    Form, Input, Select,
    Modal
} from 'antd'

import Api from '../../../api/dataModel'
import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item
const Option = Select.Option;

class AtomIndexDefineModal extends React.Component<any, any> {
    state: any = {
        columnTypes: [],
        types: []
    }

    componentDidMount () {
        this.getColumnType();
        this.getType();
    }

    getColumnType () {
        Api.getColumnType().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    columnTypes: res.data || []
                })
            }
        })
    }

    getType () {
        Api.getType().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    types: res.data || []
                })
            }
        })
    }

    submit = (e: any) => {
        e.preventDefault()
        const { handOk, form, data } = this.props

        const formData = this.props.form.getFieldsValue()
        formData.type = 1; // 1 - 原子指标, 2 - 衍生指标,
        formData.isEdit = data && !isEmpty(data) ? true : undefined;
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
        this.setState({}, () => {
            handCancel()
            form.resetFields()
        })
    }

    render () {
        const { form, visible, data } = this.props;
        const { columnTypes, types } = this.state;
        const { getFieldDecorator } = form;

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑原子指标' : '创建原子指标'

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
                        label="原子指标名称"
                        hasFeedback
                    >
                        {getFieldDecorator('columnNameZh', {
                            rules: [{
                                required: true, message: '原子指标名称不可为空！'
                            }, {
                                max: 64,
                                message: '原子指标名称不得超过64个字符！'
                            }],
                            initialValue: data ? data.columnNameZh : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="原子指标命名"
                        hasFeedback
                    >
                        {getFieldDecorator('columnName', {
                            rules: [{
                                required: true, message: '原子指标命名不可为空！'
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '原子指标命名只能由字母、数字、下划线组成!'
                            }, {
                                max: 64,
                                message: '原子指标命名不得超过64个字符！'
                            }],
                            initialValue: data ? data.columnName : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标口径(描述)"
                        hasFeedback
                    >
                        {getFieldDecorator('modelDesc', {
                            rules: [{
                                max: 200,
                                message: '指标口径请控制在200个字符以内！'
                            }],
                            initialValue: data ? data.modelDesc : ''
                        })(
                            <Input.TextArea rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标类型"
                    >
                        {getFieldDecorator('columnType', {
                            rules: [],
                            initialValue: data ? data.columnType : 1
                        })(
                            <Select>
                                {
                                    types.map((v: any, index: any) => <Option key={index + 1} value={index + 1}>{v}</Option>)
                                }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据类型"
                    >
                        {getFieldDecorator('dataType', {
                            rules: [],
                            initialValue: data ? data.dataType : 'STRING'
                        })(
                            <Select>
                                {
                                    columnTypes.map((v: any) => <Option key={v} value={v}>{v}</Option>)
                                }
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create<any>()(AtomIndexDefineModal);
export default wrappedForm
