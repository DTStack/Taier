import React from 'react';
import { connect } from 'react-redux';

import { Modal, Form, Input } from 'antd';

import { formItemLayout } from '../../consts';

const FormItem = Form.Item;

@connect(state => {
    return {
        user: state.user
    }
})
class SubmitModal extends React.Component {
    state = {
        key: null
    }
    onCancel = () => {
        this.props.onClose();
        this.setState({
            key: Math.random()
        })
    }
    onOk = () => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.onOk(values).then((success) => {
                    if (success) {
                        this.onCancel();
                    }
                })
            }
        });
    }
    render () {
        const { key } = this.state;
        const { visible, form, user, name } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                key={key}
                visible={visible}
                onCancel={this.onCancel}
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
export default Form.create()(SubmitModal);
