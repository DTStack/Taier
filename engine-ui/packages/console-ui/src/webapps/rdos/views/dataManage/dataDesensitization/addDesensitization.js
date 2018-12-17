import React, { Component } from 'react';
import { Modal, Form, Input, Select } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
// mock
const children = [];
for (let i = 10; i < 36; i++) {
    children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
}
class AddDesensitization extends Component {
    cancel = () => {
        const { onCancel } = this.props;
        onCancel();
    }
    submit = () => {
        const { onOk } = this.props;
        const desensitizationData = this.props.form.getFieldsValue();
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields()
                onOk(desensitizationData)
            }
        });
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Modal
                visible={this.props.visible}
                title='添加脱敏'
                onCancel={this.cancel}
                onOk={this.submit}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏名称"
                    >
                        {getFieldDecorator('desensitizationName', {
                            rules: [{
                                required: true,
                                message: '脱敏名称不可为空！'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目"
                    >
                        {getFieldDecorator('projects', {
                            rules: [{
                                required: true,
                                message: '项目不可为空！'
                            }]
                        })(
                            <Select
                                mode="multiple"
                                allowClear
                            >
                                {children}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('tables', {
                            rules: [{
                                required: true,
                                message: '表不可为空！'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                        {getFieldDecorator('columns', {
                            rules: [{
                                required: true,
                                message: '字段不可为空！'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('sampleData', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }]
                            // initialValue: sourceData.dataDesc || ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏规则"
                    >
                        {getFieldDecorator('rules', {
                            rules: [{
                                required: true,
                                message: '脱敏规则不可为空！'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddDesensitization);
