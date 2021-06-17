import React, { useState } from 'react'
import { Modal, Alert, Col, Row, Table, Input, message } from 'antd'
import { FormComponentProps } from 'antd/lib/form/Form'
import Api from '../../../../../../api/console'
import { COMPONENT_TYPE_VALUE } from '../../../const'

import './index.scss'

interface TableData {
    userName: string;
    password: string;
}

interface IProps extends FormComponentProps {
    clusterInfo: any;
    view: boolean;
}

const NodeLabel: React.FC<IProps> = (props) => {
    const [visible, setVisible] = useState<boolean>(false)
    const [nodes, setNodes] = useState<any[]>([])
    const [loading, setLoading] = useState<boolean>(false)

    const getNodes = async () => {
        const { clusterId } = props.clusterInfo
        const { getFieldValue } = props.form
        const field = COMPONENT_TYPE_VALUE.DTSCRIPT_AGENT + '.componentConfig.agentAddress'
        const params = {
            clusterId,
            needRefresh: true,
            componentTypeCode: COMPONENT_TYPE_VALUE.DTSCRIPT_AGENT,
            agentAddress: getFieldValue(field) || ''
        }

        const res = await Api.getClusterComponentUser({ ...params })
        if (res.code == 1) {
            // setNodes([
            //     {
            //         label: 'default',
            //         labelIp: 'aaaaaaaaaaaaaaa',
            //         clusterId: -1,
            //         componentTypeCode: 18,
            //         componentUserInfoList: [
            //             {
            //                 userName: 'kano',
            //                 password: 'kano'
            //             },
            //             {
            //                 userName: 'kano1',
            //                 password: 'kano2'
            //             }
            //         ]
            //     }
            // ])
            setNodes(res?.data?.data || [])
            setVisible(true)
        }
    }

    const getClassName = (suffix?: string) => {
        return 'c-nodeLable__modal' + (suffix ? '__' + suffix : '')
    }

    const addTableData = (currentNode) => {
        setNodes(nodes.map((node) => {
            const { componentUserInfoList, label } = node
            if (label !== currentNode.label) return node
            return { ...node, componentUserInfoList: [...componentUserInfoList, {}] }
        }))
    }

    const deleteTableData = (currentNode, index: number) => {
        setNodes(nodes.map((node) => {
            if (node.label !== currentNode.label) return node
            node.componentUserInfoList.splice(index, 1)
            return node
        }))
    }

    const setTableDataValue = (currentNode, location: number, key: string, value: string) => {
        setNodes(nodes.map((node) => {
            const { componentUserInfoList, label } = node
            if (label !== currentNode.label) return node
            const data = componentUserInfoList.map((data, index) => {
                if (location === index) data[key] = value
                return data
            })
            return { ...node, componentUserInfoList: data }
        }))
    }

    const onOk = async () => {
        if (!nodes.length) {
            setVisible(false)
            return
        }
        setLoading(true)
        const res = await Api.addOrUpdateComponentUser({ componentUserList: nodes })
        if (res.code === 1) {
            message.success('保存成功')
            setVisible(false)
            setLoading(false)
        }
    }

    const getColumn = (node) => {
        return [
            {
                title: '服务器用户名',
                dataIndex: 'userName',
                render: (text: string, record: TableData, index: number) => {
                    return <Input
                        onChange={(e) => setTableDataValue(node, index, 'userName', e.target.value)}
                        placeholder="请输入"
                        style={{ width: 180 }}
                        size="small"
                        value={text}
                    />
                }
            },
            {
                title: '密码',
                dataIndex: 'password',
                render: (text: string, record: TableData, index: number) => {
                    return <Input.Password
                        onChange={(e) => setTableDataValue(node, index, 'password', e.target.value)}
                        placeholder="请输入"
                        style={{ width: 180, height: 28 }}
                        size="small"
                        value={text}
                        visibilityToggle={false}
                    />
                }
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: '102px',
                render: (text: string, record: TableData, index: number) => {
                    return <a onClick={() => { deleteTableData(node, index) }}>删除</a>
                }
            }
        ]
    }

    return (
        <>
            {!props.view && <a className='c-nodeLable__content' onClick={() => getNodes()}>查看节点标签和ip对应关系</a>}
            <Modal
                title="查看详情"
                visible={visible}
                onCancel={() => setVisible(false)}
                onOk={onOk}
                className={getClassName()}
                width={680}
                confirmLoading={loading}
            >
                <Alert
                    className={getClassName('alter')}
                    message="配置服务器用户后Shell on Agent任务可选择指定服务器用户运行。此处注意同一节点标签下所有IP的服 务器用户名与密码需要保持一致。"
                    type="info"
                    showIcon
                />
                {nodes.length > 0 ? <div>
                    {nodes.map((node) => {
                        return (
                            <div key={node.label} className='c-nodeLable__modal__nodes'>
                                <Row>
                                    <Col span={4}>节点标签：</Col>
                                    <Col span={20}>{node?.label || ''}</Col>
                                </Row>
                                <Row>
                                    <Col span={4}>IP：</Col>
                                    <Col span={20}>{node?.labelIp || ''}</Col>
                                </Row>
                                <Row>
                                    <Col span={4}>服务器用户配置：</Col>
                                    <Col span={20}>
                                        <Table
                                            className="dt-table-border dt-table-last-row-noborder"
                                            style={{ marginBottom: 12, boxShadow: 'unset' }}
                                            columns={getColumn(node)}
                                            dataSource={node?.componentUserInfoList || []}
                                            footer={null}
                                            pagination={false}
                                            size={'middle'}
                                            locale={{
                                                emptyText: <div className='preview-empty' style={{ marginTop: -5, marginBottom: -5 }}>
                                                    暂无数据
                                                </div>
                                            }}
                                        />
                                        <a onClick={() => addTableData(node)}>+ 添加自定义参数</a>
                                    </Col>
                                </Row>
                            </div>
                        )
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
