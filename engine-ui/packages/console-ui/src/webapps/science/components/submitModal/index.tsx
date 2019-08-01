import * as React from 'react';
import { connect } from 'react-redux';

import { Modal, Form, Input } from 'antd';

import { formItemLayout } from '../../consts';

const FormItem = Form.Item;

@(connect((state: any) => {
    return {
        user: state.user
    }
}) as any)
class SubmitModal extends React.Component<any, any> {
    state: any = {
        key: null,
        confirmLoading: false
    }
    onCancel = () => {
        this.props.onClose();
        this.setState({
            key: Math.random()
        })
    }
    onOk = () => {
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    confirmLoading: true
                });
                this.props.onOk(values).then((success: any) => {
                    if (success) {
                        this.onCancel();
                    }
                    this.setState({
                        confirmLoading: false
                    });
                })
            }
        });
    }
    render () {
        const { key, confirmLoading } = this.state;
        const { visible, form, user, name } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                key={key}
                visible={visible}
                onCancel={this.onCancel}
                confirmLoading={confirmLoading}
                onOk={this.onOk}
                title={name + '提交'}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label='提交人'
                    >
                        <Input disabled value={user.userName} />
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label='备注'
                    >
                        {getFieldDecorator('publishDesc', {
                            rules: [{
                                max: 64,
                                message: '最大字符不能超过64'
                            }]
                        })(
                            <Input.TextArea />
                        )}
                    </FormItem>
                    <p style={{ textAlign: 'center' }}>注：{name}提交后才能进行任务离线调度及运维</p>
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(SubmitModal);
