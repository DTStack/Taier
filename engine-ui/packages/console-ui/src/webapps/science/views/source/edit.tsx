import * as React from 'react';
import { Modal, Form, Input, message } from 'antd';

import Api from '../../api';

const FormItem = Form.Item;
const formItemLayout: any = {
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
const FORM_ENUM: any = {
    '表名称': 'name',
    '表生命周期': 'lifeDay',
    '表描述': 'dataSourceDesc'
}
class Edit extends React.Component<any, any> {
    handleOk = () => {
        this.props.form.validateFieldsAndScroll(async (err: any, values: any) => {
            if (!err) {
                const { record } = this.props;
                let res = await Api.comm.updateDataSource({
                    ...record,
                    ...values
                });
                if (res && res.code == 1) {
                    message.success('修改成功');
                    this.props.onOk(values);
                    this.handleCancel();
                }
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
                    okText="确定"
                    onCancel={this.handleCancel}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="表名称"
                        >
                            {getFieldDecorator(FORM_ENUM['表名称'], {
                                /* rules: [{
                                    required: true, message: '请填写表名称'
                                }, {
                                    max: 32, message: '不超过32个字符，只支持字母、数字、下划线'
                                }, {
                                    pattern: /^[A-Za-z0-9_]+$/, message: '不超过32个字符，只支持字母、数字、下划线'
                                }] */
                            })(
                                <Input placeholder="不超过32个字符，只支持字母、数字、下划线" disabled />
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
    mapPropsToFields (props: any) {
        return {
            // TODO
            [FORM_ENUM['表名称']]: {
                value: props.record[FORM_ENUM['表名称']] || ''
            },
            [FORM_ENUM['表生命周期']]: {
                value: props.record[FORM_ENUM['表生命周期']] || ''
            },
            [FORM_ENUM['表描述']]: {
                value: props.record[FORM_ENUM['表描述']] || ''
            }
        };
    }
})(Edit);
