import * as React from 'react';
import { Modal } from 'antd';
import { COMPONENT_CONFIG_NAME, COMPONENT_TYPE_VALUE } from '../../const';

interface IProps {
    deleteComps: any[];
    addComps: any[];
    visible: boolean;
    onOk: Function;
    onCancel: Function;
}

export default class ModifyCompsModal extends React.Component<IProps, any> {
    render () {
        const { onOk, onCancel, deleteComps, addComps, visible } = this.props;
        const compsName = deleteComps.map(code => COMPONENT_CONFIG_NAME[code])
        const isRadio = [COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(addComps[0]) > -1

        return (
            <Modal
                title="修改组件配置"
                onOk={() => onOk()}
                onCancel={() => onCancel()}
                visible={visible}
                className="c-clusterManage__modal"
            >
                { isRadio
                    ? <span>切换到 {COMPONENT_CONFIG_NAME[addComps[0]]} 后 {compsName[0]} 的配置信息将丢失，确认切换到 {COMPONENT_CONFIG_NAME[addComps[0]]}？</span>
                    : <span>删除 {compsName.join('、')} 组件后相应配置信息将丢失，确定删除 { compsName.join('、') } 组件？</span>}
            </Modal>
        )
    }
}
