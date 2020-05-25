import * as React from 'react';
import { Modal, Button } from 'antd';
import { COMPONEMT_CONFIG_NAME_ENUM } from '../../consts';

export default class SaveCompModal extends React.Component<any, any> {
    render () {
        const { notSave, handleCancleSaveModal, modifyCompsArr } = this.props;
        let modifyCompsNames: any = [];
        modifyCompsArr.map((comp: number) => {
            modifyCompsNames.push(COMPONEMT_CONFIG_NAME_ENUM[comp])
        })
        return (
            <Modal
                title="保存组件配置"
                onCancel={handleCancleSaveModal}
                visible={notSave}
                className="c-modifyComponents__modal"
                footer={
                    <Button type="primary" onClick={handleCancleSaveModal}>确定</Button>
                }
            >
                组件 {modifyCompsNames.join('、')} 参数变更未保存，请先保存再测试组件连通性
            </Modal>
        )
    }
}
