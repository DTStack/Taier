import React, { useState } from 'react'
import { Modal, Alert, Col, Row, Table, Input,
    message, Select } from 'antd'
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
    const [loading, setLoading] = useState<boolean>(false)
    const [nodes, setNodes] = useState<any[]>([])
    const [defaultLabel, setDefaultLabel] = useState<string | null>(null)

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
        setVisible(true)
        if (res.code == 1) {
            if (!res?.data?.length) setNodes([])
            setDefaultLabel(res.data[0]?.label || null)
            setNodeDefaultValue(res.data, res?.data[0]?.label)
            setVisible(true)
        }
    }

    const getClassName = (suffix?: string) => {
        return 'c-nodeLable__modal' + (suffix ? '__' + suffix : '')
    }

    const addTableData = (currentNode) => {
        setNodes(nodes.map((node) => {
            const componentUserInfoList = node.componentUserInfoList || []
            if (node.label !== currentNode.label) return node
            return { ...node, componentUserInfoList: [...componentUserInfoList, { userName: '', password: '' }] }
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

    const isRepeatUserName = () => {
        for (const node of nodes) {
            const mark = {}
            for (const user of (node?.componentUserInfoList || [])) {
                if (mark[user.userName]) return true
                mark[user.userName] = true
            }
        }
        return false
    }

    const isIllegalValue = () => {
        const pattern = /^[^\s]*$/
        for (const node of nodes) {
            for (const user of (node?.componentUserInfoList || [])) {
                const { userName, password } = user
                if (!pattern.test(userName) || !pattern.test(password)) return true
                if (userName?.length > 64 || password?.length > 64) return true
            }
        }
        return false
    }

    const setNodeDefaultValue = (nodes: any[], value: string) => {
        setNodes(nodes.map(node => {
            if (node.label !== value) return { ...node, isDefault: false }
            return { ...node, isDefault: true }
        }))
    }

    const setDefaultLabelValue = (value: string) => {
        setDefaultLabel(value)
        setNodeDefaultValue(nodes, value)
    }

    const onOk = async () => {
        if (!nodes.length) {
            setVisible(false)
            return
        }
        if (isRepeatUserName()) {
            message.error('服务器用户名重复，请检查服务器用户名！')
            return
        }
        if (isIllegalValue()) {
            message.error('请检查服务器用户名和密码配置！')
            return
        }
        setLoading(true)
        const res = await Api.addOrUpdateComponentUser({ componentUserList: nodes })
        if (res.code === 1) {
            message.success('保存成功')
            setVisible(false)
        }
        setLoading(false)
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
                maskClosable={false}
            >
                <Alert
                    className={getClassName('alter')}
                    message="配置服务器用户后Shell on Agent任务可选择指定服务器用户运行。此处注意同一节点标签下所有IP的服 务器用户名与密码需要保持一致。"
                    type="info"
                    showIcon
                />
                {nodes.length > 0 ? <div>
                    <Row>
                        <Col span={4}>默认节点标签：</Col>
                        <Col span={20}>
                            <Select
                                showSearch
                                value={defaultLabel}
                                optionFilterProp="label"
                                style={{ width: 340 }}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                onChange={(value: string) => setDefaultLabelValue(value)}
                            >
                                {nodes.map((node) => {
                                    return <Select.Option key={node.label} value={node.label}>{node.label} </Select.Option>
                                })}
                            </Select>
                        </Col>
                    </Row>
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
                                            footer={null}
                                            pagination={false}
                                            size={'middle'}
                                            columns={getColumn(node)}
                                            dataSource={node?.componentUserInfoList || []}
                                            className="dt-table-border dt-table-last-row-noborder"
                                            style={{ marginBottom: 12, boxShadow: 'unset' }}
                                            locale={{
                                                emptyText: <div className="table-emptys">暂无数据</div>
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
