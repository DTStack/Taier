import * as React from 'react';
import { Modal, Form, Input, message } from 'antd';

import { formItemLayout } from '../../consts'
import api from '../../api/apiManage';

const FormItem = Form.Item;

class LimitModal extends React.Component<any, any> {
    state: any = {
        loading: false
    }
    onOk = () => {
        const { form, apiId, onOk, closeModal } = this.props;
        const { validateFields } = form;
        validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                api.updateLimiter({
                    apiId,
                    ...values
                }).then((res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        message.success('修改成功');
                        onOk();
                        closeModal();
                    }
                })
            }
        })
    }
    render () {
        const { visible, closeModal, data, form } = this.props;
        const { loading } = this.state;
        const { getFieldDecorator } = form;
        return <Modal
            visible={visible}
            onCancel={closeModal}
            onOk={this.onOk}
            confirmLoading={loading}
            title="调用次数限制"
        >
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="调用限制"
                >
                    {getFieldDecorator('reqLimit', {
                        rules: [
                            { required: true, message: '请输入调用次数限制' },
                            {
                                validator: function (rule: any, value: any, callback: any) {
                                    if (value && (value > 2000 || value < 1)) {
                                        const error = '请输入不大于2000的正整数'
                                        callback(error)
                                        return;
                                    }
                                    callback();
                                }
                            }
                        ],
                        initialValue: data
                    })(
                        <Input type="number" placeholder="单用户每秒最大调用次数不超过2000次" />
                    )}
                </FormItem>
            </Form>
        </Modal>
    }
}
export default Form.create<any>()(LimitModal);
