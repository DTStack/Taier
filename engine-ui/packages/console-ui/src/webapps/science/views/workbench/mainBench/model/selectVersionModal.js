import React from 'react';

import { Modal, Form, Select, Alert } from 'antd';
import { formItemLayout } from '../../../../consts';

const Option = Select.Option;

class SelectVersionsModal extends React.Component {
    state = {
        versionsList: []
    }
    render () {
        const { versionsList } = this.state;
        const { visible, onCancel, data } = this.props;
        return (
            <Modal
                visible={visible}
                onCancel={onCancel}
                title='切换版本'
            >
                <Alert style={{ marginBottom: '20px' }} message="注：切换版本时，由于某些模型较大，切换可能需要较长时间，请耐心等待。" type="info" />
                <Form.Item
                    {...formItemLayout}
                    label='选择版本'
                >
                    <Select style={{ width: '100%' }} defaultValue={data && data.versionId} >
                        {versionsList.map((version) => {
                            return <Option key={version.value}>{version.name}</Option>
                        })}
                    </Select>
                </Form.Item>
            </Modal>
        )
    }
}
export default SelectVersionsModal;
