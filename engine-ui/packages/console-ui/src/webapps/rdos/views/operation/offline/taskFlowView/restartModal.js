import moment from 'moment'
import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Input, Modal, Row, message, Form,
    Table, DatePicker, Col, Tree
} from 'antd'

import Api from '../../../../api'
import { formItemLayout, TASK_STATUS } from '../../../../comm/const'
import { TaskType, TaskStatus } from '../../../../components/status'

const confirm = Modal.confirm
const Search = Input.Search
const TreeNode = Tree.TreeNode
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker

function replaceTreeNode (treeNode, replace) {
    if (treeNode.id === parseInt(replace.id, 10)) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.taskVOS) {
        const children = treeNode.taskVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

class RestartModal extends Component {
    state = {
        treeData: [],
        selected: [],
        expandedKeys: [],
        currentNode: '',
        checkedKeys: []
    }

    componentWillReceiveProps (nextProps) {
        const node = nextProps.restartNode
        const visible = nextProps.visible

        if (visible && node) {
            this.setState({
                currentNode: node,
                treeData: [node],
                checkedKeys: [`${node.id}`]
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
        const { onCancel, router, restartNode } = this.props
        const checked = this.state.checkedKeys

        if (checked.length === 0) {
            message.warning('请未选择任务要重跑的任务!')
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
        const checked = checkedKeys.checked
        if (checked && checked.length > 0) {
            this.setState({ checkedKeys: checked })
        }
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    asyncTree = (treeNode) => {
        const ctx = this
        const { dispatch } = this.props
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
                const disabed = item.id === currentNode.id;
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
                        disableCheckbox={disabed}
                        value={id}
                        title={content}
                        key={id}>
                        {this.getTreeNodes(item.children, currentNode)}
                    </TreeNode>);
                }
                return (<TreeNode
                    data={item}
                    disableCheckbox={disabed}
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
