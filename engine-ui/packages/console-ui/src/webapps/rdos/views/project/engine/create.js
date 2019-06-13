import React, { Component } from 'react';
import { Modal } from 'antd';

import EngineForm from '../../../components/engineForm';

class CreateEngineModal extends Component {
    onOk = () => {
        const { onOk } = this.props;
        const form = this._refInstance.props.form;
        const formData = form.getFieldsValue();
        console.log('formdata', formData, form)
        form.validateFields((err) => {
            if (!err) {
                if (onOk) {
                    onOk(formData, form);
                }
            }
        });
    }
    render () {
        const { visible, onCancel, key } = this.props;
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
                    engineList={[{
                        name: 'hadoop', // 新增计算引擎modal 调用获取支持的引擎列表和 数据库列表
                        value: 1
                    }, {
                        name: 'libra',
                        value: 2
                    }]}
                    targetDb={{
                        1: [
                            'db1',
                            'db2'
                        ]
                    }}
                />
            </Modal>
        )
    }
}

export default CreateEngineModal;
