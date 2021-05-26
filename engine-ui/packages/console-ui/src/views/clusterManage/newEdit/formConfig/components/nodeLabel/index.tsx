import React, { useState } from 'react'
import { Modal } from 'antd'
import { FormComponentProps } from 'antd/lib/form/Form'
import Api from '../../../../../../api/console'
import { COMPONENT_TYPE_VALUE } from '../../../const'

import './index.scss'

interface IProps extends FormComponentProps {}

const NodeLabel: React.FC<IProps> = (props) => {
    const [visible, setVisible] = useState<boolean>(false)
    const [nodes, setNodes] = useState<any[]>([])

    const getNodes = async () => {
        const { getFieldValue } = props.form
        const field = COMPONENT_TYPE_VALUE.DTSCRIPT_AGENT + '.componentConfig.agentAddress'
        const agentAddress = getFieldValue(field) || ''

        const res = await Api.getDtScriptAgentLabel({ agentAddress })
        if (res.code == 1) {
            setNodes(res.data || [])
            setVisible(true)
        }
    }

    return (
        <>
            <a className='c-nodeLable__content' onClick={() => getNodes()}>查看节点标签和ip对应关系</a>
            <Modal
                title="查看节点标签和ip对应关系"
                visible={visible}
                onCancel={() => setVisible(false)}
                footer={null}
                className='c-nodeLable__modal'
            >
                {nodes.length > 0 ? <div className='c-nodeLable__modal__nodes'>
                    {nodes.map((node) => {
                        return <p key={node.id}>{node?.label || ''} : {node?.localIp || ''}</p>
                    })}
                </div> : <div className='c-nodeLable__modal__empty'>
                    <img src="public/img/emptyLogo.svg" />
                    <span>无内容，请检查配置！</span>
                </div>}
            </Modal>
        </>
    )
}

export default NodeLabel
