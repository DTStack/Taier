import React, { Component } from 'react';
import { Modal } from 'antd';

import EngineForm from '../../../components/engineForm';

class CreateEngineModal extends Component {
    onOk = () => {
        const { onOk } = this.props;
        const form = this._refInstance.props.form;
        let formData = form.getFieldsValue();
        const reqParams = formData[''];
        form.validateFields((err) => {
            if (!err) {
                if (onOk) {
                    onOk(reqParams, form);
                }
            }
        });
    }
    render () {
        const { visible, onCancel, key, unUseEngineList, dBList } = this.props;
        return (
            <Modal
                title="添加计算引擎"
                key={key}
                visible={visible}
                onCancel={onCancel}
                onOk={this.onOk}
                maskClosable={false}
            >
                <EngineForm wrappedComponentRef={(ins) => this._refInstance = ins }
                    // isCreateEngine={true}
                    engineList={unUseEngineList}
                    targetDb={dBList}
                />
            </Modal>
        )
    }
}

export default CreateEngineModal;
