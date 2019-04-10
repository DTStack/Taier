import React, { Component } from 'react';
import { Modal, Form, Input } from 'antd';
const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};
// TODO
const FORM_ENUM = {
    '表名称': 'projectName',
    '表生命周期': 'projectAliaName',
    '表描述': 'projectDesc'
}
class Edit extends Component {
    handleOk = () => {
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                // TODO
                console.log('Received values of form: ', values);
                this.handleCancel();
            }
        });
    }
    handleCancel = () => {
        this.props.form.resetFields();
        this.props.onCancel();
    }
    render () {
        const { visible } = this.props;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Modal
                    maskClosable={false}
                    title="数据源编辑"
                    visible={visible}
                    onOk={this.handleOk}
                    wrapClassName='datasource-edit-modal'
                    okText="创建"
                    onCancel={this.handleCancel}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="表名称"
                        >
                            {getFieldDecorator(FORM_ENUM['表名称'], {
                                rules: [{
                                    required: true, message: '请填写表名称'
                                }, {
                                    max: 32, message: '不超过32个字符，只支持字母、数字、下划线'
                                }, {
                                    pattern: /^[A-Za-z0-9_]+$/, message: '不超过32个字符，只支持字母、数字、下划线'
                                }]
                            })(
                                <Input placeholder="不超过32个字符，只支持字母、数字、下划线" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="表生命周期"
                        >
                            {getFieldDecorator(FORM_ENUM['表生命周期'], {
                                rules: [{
                                    required: true, message: '请填写表生命周期'
                                }]
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="表描述"
                        >
                            {getFieldDecorator(FORM_ENUM['表描述'], {
                                rules: [{
                                    max: 64,
                                    message: '不超过64个字符'
                                }]
                            })(
                                <Input type="textarea" rows={4} placeholder="不超过64个字符" />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        );
    }
}

export default Form.create({
    mapPropsToFields (props) {
        return {
            // TODO
            [FORM_ENUM['表名称']]: {
                value: props.record.projectName || ''
            },
            [FORM_ENUM['表生命周期']]: {
                value: props.record.projectName || ''
            },
            [FORM_ENUM['表描述']]: {
                value: props.record.projectName || ''
            }
        };
    }
})(Edit);
