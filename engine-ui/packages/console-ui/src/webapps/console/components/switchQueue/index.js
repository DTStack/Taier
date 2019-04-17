import React from 'react';
import { Modal, Form, Select, message } from 'antd';
import { formItemLayout } from '../../consts'
import Api from '../../api/console';

const Option = Select.Option;
class SwitchQueue extends React.Component {
    closeAllModal () {
        this.props.onCancel()
        this.props.closeResourceModal() // 关闭并刷新列表
    }
    queueOption = () => {
        const { queueList, resource } = this.props;
        return queueList && queueList.map(item => {
            if (resource.queueId === item.id) return null;
            const id = `${item.id}`;
            return (
                <Option key={id}>
                    {item.queueName}
                </Option>
            )
        })
    }
    confirmSwitchQueue = () => {
        const { getFieldValue, validateFields } = this.props.form;
        validateFields(err => {
            if (!err) {
                Api.confirmSwitchQueue({
                    tenantId: getFieldValue('tenantId'),
                    queueId: getFieldValue('queueId')
                }).then(res => {
                    if (res.code === 1) {
                        message.success('切换队列成功！')
                        this.closeAllModal();
                    }
                })
            }
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, tenantInfo } = this.props;
        return (
            <Modal
                title='切换队列'
                visible={visible}
                onOk={this.confirmSwitchQueue}
                onCancel={this.props.onCancel}
            >
                <Form>
                    <Form.Item
                        style={{ display: 'none' }}
                    >
                        {getFieldDecorator('tenantId', {
                            initialValue: tenantInfo.id
                        })}
                    </Form.Item>
                    <Form.Item
                        label="租户名称"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('tenantName', {
                            initialValue: tenantInfo.name
                        })(
                            <span>{tenantInfo.name}</span>
                        )}
                    </Form.Item>
                    <Form.Item
                        label="队列切换至"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('queueId', {
                            rules: [{
                                required: true,
                                message: '请选择要切换的队列'
                            }]
                        })(
                            <Select
                                allowClear
                                placeholder='请选择切换的队列名称'
                            >
                                {this.queueOption()}
                            </Select>
                        )}
                    </Form.Item>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(SwitchQueue);
