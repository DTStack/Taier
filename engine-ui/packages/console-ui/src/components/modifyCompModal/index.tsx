import * as React from 'react';
import { Modal } from 'antd';
import { COMPONEMT_CONFIG_NAME_ENUM } from '../../consts';

export default class ModifyComponentModal extends React.Component<any, any> {
    render () {
        const { modifyComponent, modify, handleCancleModify, modifyComps, selectValue, modifySource } = this.props;
        let modifyCompsNames: any = [];
        modifyComps.map((comps: any) => {
            modifyCompsNames.push(comps.componentName)
        })
        return (
            <Modal
                title="修改组件配置"
                onOk={modifyComponent}
                onCancel={handleCancleModify}
                visible={modify}
                className="c-modifyComponents__modal"
            >
                {
                    modifySource
                        ? <span>切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]} 后 {COMPONEMT_CONFIG_NAME_ENUM[modifySource]} 的配置信息将丢失，确认切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]}？</span>
                        : <span>删除 {modifyCompsNames.join('、')} 组件后相应配置信息将丢失，确定删除 { modifyCompsNames.join('、') } 组件？</span>
                }
            </Modal>
        )
    }
}
