import React, { Component } from 'react';
import { Modal } from 'antd';

import EngineForm from '../../../components/engineForm';

class CreateEngineModal extends Component {
    onOk = () => {
        const { onOk } = this.props;
        const form = this._refInstance.props.form;
        const formData = form.getFieldsValue();

        form.validateFields((err) => {
            if (!err) {
                if (onOk) {
                    onOk(formData, form);
                }
            }
        });
    }
    render () {
        const { visible, onCancel } = this.props;
        return (
            <Modal
                title="添加计算引擎"
                visible={visible}
                onCancel={onCancel}
                onOk={this.onOk}
                maskClosable={false}
            >
                <EngineForm wrappedComponentRef={(ins) => this._refInstance = ins }/>
            </Modal>
        )
    }
}

export default CreateEngineModal;
