import * as React from 'react';
import { Modal } from 'antd';

import EngineForm from '../../../components/engineForm';

class CreateEngineModal extends React.Component<any, any> {
    onOk = () => {
        const { onOk } = this.props;
        const form = this._refInstance.props.form;
        let formData = form.getFieldsValue();
        const reqParams = Object.assign(formData[''], {
            engineType: formData.engineType
        })
        form.validateFields((err: any) => {
            if (!err) {
                if (onOk) {
                    onOk(reqParams, form);
                }
            }
        });
    }
    render () {
        const { visible, onCancel, key, unUseEngineList, dBList, confirmLoading } = this.props;
        return (
            <Modal
                title="添加计算引擎"
                key={key}
                visible={visible}
                onCancel={onCancel}
                onOk={this.onOk}
                confirmLoading={confirmLoading}
                maskClosable={false}
            >
                <EngineForm wrappedComponentRef={(ins: any) => this._refInstance = ins }
                    // isCreateEngine={true}
                    engineList={unUseEngineList}
                    targetDb={dBList}
                />
            </Modal>
        )
    }
}

export default CreateEngineModal;
