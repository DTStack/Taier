import * as React from 'react';
import { Modal } from 'antd';
import { COMPONEMT_CONFIG_NAME_ENUM, COMPONENT_TYPE_VALUE } from '../../consts';

export default class ModifyComponentModal extends React.Component<any, any> {
    render () {
        const { handleDeleteComps, modify, handleCancleModify, deleteComps, selectValue } = this.props;
        let modifyCompsNames: any = [];
        deleteComps.map((comps: any) => {
            modifyCompsNames.push(comps.componentName)
        })
        const isSource = selectValue[0] === COMPONENT_TYPE_VALUE.YARN || selectValue[0] === COMPONENT_TYPE_VALUE.KUBERNETES;
        return (
            <Modal
                title="修改组件配置"
                onOk={handleDeleteComps}
                onCancel={handleCancleModify}
                visible={modify}
                className="c-modifyComponents__modal"
            >
                {
                    isSource
                        ? <span>切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]} 后 {modifyCompsNames[0]} 的配置信息将丢失，确认切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]}？</span>
                        : <span>删除 {modifyCompsNames.join('、')} 组件后相应配置信息将丢失，确定删除 { modifyCompsNames.join('、') } 组件？</span>
                }
            </Modal>
        )
    }
}
