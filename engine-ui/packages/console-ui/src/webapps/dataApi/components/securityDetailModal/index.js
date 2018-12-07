import React from 'react';
import { Modal, Button, Form, Input, Select } from 'antd';

import { formItemLayout, SECURITY_TYPE } from '../../consts'

const FormItem = Form.Item;
const Option = Select.Option;

class SecurityDetailModal extends React.Component {
    render () {
        const { visible, closeModal, data = {} } = this.props;
        return (
            <Modal
                visible={visible}
                onCancel={closeModal}
                title="查看详情"
                footer={<Button type="primary" onClick={closeModal}>关闭</Button>}
            >
                <FormItem
                    label="名称"
                    {...formItemLayout}
                >
                    <Input disabled value={data.name} />
                </FormItem>
                <FormItem
                    label="类型"
                    {...formItemLayout}
                >
                    <Select value={data.type} disabled style={{ width: '100%' }}>
                        <Option value={SECURITY_TYPE.WHITE} key={SECURITY_TYPE.WHITE}>白名单</Option>
                        <Option value={SECURITY_TYPE.BLACK} key={SECURITY_TYPE.BLACK}>黑名单</Option>
                    </Select>
                </FormItem>
                <FormItem
                    label="IP地址"
                    {...formItemLayout}
                >
                    <Input disabled type="textarea" value={data.ip} />
                </FormItem>
            </Modal>
        )
    }
}
export default SecurityDetailModal;
