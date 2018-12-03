import moment from 'moment'
import React, { Component } from 'react'

import {
    Modal, Row, message, Col, Tree
} from 'antd'

import Api from '../../../../api'
import { TaskType, TaskStatus } from '../../../../components/status'

const TreeNode = Tree.TreeNode

class RestartModal extends Component {
    state = {
        treeData: [],
        selected: [],
        expandedKeys: [],
        currentNode: '',
        checkedKeys: []
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const node = nextProps.restartNode
        const visible = nextProps.visible

        if (visible && node) {
            this.setState({
                currentNode: node,
                treeData: [node]
            }, () => {
                this.loadTaskTree({
                    taskId: node.batchTask.id,
                    jobKey: node.jobKey,
                    isOnlyNextChild: false
                })
            })
        }
    }

    restartChildNodes = () => {
        const { onCancel, restartNode } = this.props
        const checked = this.state.checkedKeys

        if (checked.length === 0) {
            message.error('请选择要重跑的任务!')
            return;
        }

        const reqParams = {
            jobId: restartNode.id,
            subJobIds: checked,
            justRunChild: true,
            setSuccess: true
        }

        Api.restartAndResume(reqParams).then((res) => {
            if (res.code === 1) {
                message.success('重跑成功!')
                onCancel();
            }
        })
    }

    loadTaskTree = (params) => {
        const ctx = this
        const parent = this.state.currentNode
        Api.getRestartJobs(params).then(res => {
            if (res.code === 1) {
                const children = res.data || []
                if (children.length > 0) {
                    ctx.insertChildren(parent, children)
                }
            }
        })
    }

    insertChildren = (node, children) => {
        const treeData = Object.assign(this.state.treeData)

        const loop = (data) => {
            for (let i = 0; i < data.length; i++) {
                if (data[i].id === node.id) {
                    data[i].children = children;
                    break;
                }
                if (data[i].children) {
                    loop(data[i].children)
                }
            }
        }
        loop(treeData)

        this.setState({ treeData })
    }

    cancleModal = () => {
        this.setState({
            selected: []
        })
        this.props.onCancel()
    }

    onCheck = (checkedKeys, info) => {
        this.setState({ checkedKeys: checkedKeys.checked })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    asyncTree = (treeNode) => {
        const ctx = this
        const node = treeNode.props.data
        return new Promise((resolve) => {
            if (!node.children || node.children.length === 0) {
                ctx.loadTaskTree({
                    taskId: node.batchTask.id,
                    jobKey: node.jobKey,
                    isOnlyNextChild: true
                })
            }
            resolve();
        })
    }

    getTreeNodes = (data, currentNode) => {
        if (data && data.length > 0) {
            const nodes = data.map((item) => {
                const id = `${item.batchTask ? item.id : item.jobId}`;

                const name = item.taskName || (item.batchTask && item.batchTask.name);
                const status = item.jobStatus || item.status;
                const taskType = item.taskType || (item.batchTask && item.batchTask.taskType);

                const content = <Row>
                    <Col span="6" className="ellipsis" title={name}>{name}</Col>
                    <Col span="8">{item.cycTime}</Col>
                    <Col span="4"><TaskStatus value={status} /></Col>
                    <Col span="6"><TaskType value={taskType} /></Col>
                </Row>

                if (item.children) {
                    return (<TreeNode
                        data={item}
                        value={id}
                        title={content}
                        key={id}>
                        {this.getTreeNodes(item.children, currentNode)}
                    </TreeNode>);
                }
                return (<TreeNode
                    data={item}
                    name={name}
                    value={id}
                    title={content}
                    key={id}
                />)
            });
            return nodes;
        }
        return []
    }

    render () {
        const { visible, onCancel, restartNode } = this.props
        const { treeData } = this.state
        const treeNodes = this.getTreeNodes(treeData, restartNode)

        return (
            <Modal
                title="重跑下游并恢复调度"
                okText="确认执行"
                visible={visible}
                onOk={this.restartChildNodes}
                onCancel={onCancel}
            >
                <Row>
                    <Col span="12">请选择要重跑的任务:</Col>
                    <Col span="12" className="txt-right">业务日期：{restartNode ? restartNode.businessDate : ''}</Col>
                </Row>
                <Row className="section patch-data">
                    <Row className="patch-header">
                        <Col span="8">任务名称</Col>
                        <Col span="6">执行时间</Col>
                        <Col span="4">任务状态</Col>
                        <Col span="6">任务类型</Col>
                    </Row>
                    <Tree
                        checkable
                        checkStrictly
                        onCheck={this.onCheck}
                        checkedKeys={this.state.checkedKeys}
                        loadData={this.asyncTree}
                    >
                        {treeNodes}
                    </Tree>
                </Row>
            </Modal>
        )
    }
}
export default RestartModal
