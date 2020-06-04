import * as React from 'react';
import { Modal, Form, Input } from 'antd';

import { formItemLayout } from '../../consts';

const FormItem = Form.Item;
// 新增集群、增加组件、增加引擎共用组件
class AddEngineModal extends React.Component<any, any> {
    /**
     * 获取新增集群参数
     */
    onSubmit = () => {
        const { onOk, form } = this.props;
        const { validateFields } = form;
        validateFields((err: any, value: any) => {
            if (!err) {
                onOk({ clusterName: value.clusterName });
            }
        })
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { title, visible, onCancel } = this.props;

        return (
            <Modal
                title={title}
                visible={visible}
                onCancel={onCancel}
                onOk={this.onSubmit}
                className="c-clusterManage__modal"
            >
                <Form>
                    <FormItem
                        label="集群名称"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('clusterName', {
                            rules: [{
                                required: true,
                                message: '集群标识不可为空！'
                            }, {
                                pattern: /^[a-z0-9_]{1,64}$/i,
                                message: '集群标识不能超过64字符，支持英文、数字、下划线'
                            }]
                        })(
                            <Input placeholder="请输入集群标识" />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(AddEngineModal);
