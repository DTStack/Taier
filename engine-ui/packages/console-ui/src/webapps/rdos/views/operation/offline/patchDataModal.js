import moment from 'moment'
import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Input, Modal, Row, message, Form,
    Table, DatePicker, Col, Tree,
} from 'antd'

import Api from '../../../api'
import { formItemLayout } from '../../../comm/const'
import { TaskType } from '../../../components/status'

const confirm = Modal.confirm
const Search = Input.Search
const TreeNode = Tree.TreeNode
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker

function replaceTreeNode(treeNode, replace) {
    if (treeNode.id === parseInt(replace.id, 10)) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.subTaskVOS) {
        const children = treeNode.subTaskVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

class PatchData extends Component {

    state = {
        treeData:[],
        selected: [],
        expandedKeys: [],
        checkedKeys: ['0'],
    }

    componentWillReceiveProps(nextProps) {
        const task = nextProps.task
        if (nextProps.visible && task) {
            this.loadTaskTree({
                taskId: task.id,
                level: 2,
                directType: 2, // 获取下游
            })
        }
    }

    addData = () => {
        const { handCancel, router, form } = this.props
        const taskJson = this.getSelectedTasks()
        const reqParams = form.getFieldsValue()
        reqParams.taskJson = taskJson.length > 0 ? JSON.stringify(taskJson[0]) : ''
        this.props.form.validateFields((err) => {
            if (!err) {
                reqParams.fromDay = reqParams.rangeDate[0].set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0,
                }).unix()
                reqParams.toDay = reqParams.rangeDate[1].set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59,
                }).unix()
                delete reqParams.rangeDate;
                Api.patchTaskData(reqParams).then((res) => {
                    if (res.code === 1) {
                        this.showAddResult(reqParams.fillName)
                        setTimeout(() => {
                            form.resetFields()
                        }, 500)
                    }
                })
            }
        });
    }

    showAddResult = (fillJobName) => {
        this.props.handCancel()
        confirm({
            okText: '查看',
            title: '查看补数据结果',
            content: '补数据任务已在执行中，点击下方按钮查看结果',
            onOk() {
                hashHistory.push(`/operation/task-patch-data/${fillJobName}`)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    loadTaskTree = (params) => {
        const ctx = this
        Api.getTaskChildren(params).then(res => {
            if (res.code === 1) {
                const arr = res.data ? [res.data] : []
                ctx.wrapTableTree(arr)
                this.setState({ treeData: arr })
            }
        })
    }

    wrapTableTree = (data, key, parent) => {
        for (let i = 0; i < data.length; i++) {
            const newKey = key ? `${key}-${i}` : '0'
            data[i].key = newKey
            data[i].parentId = parent ? parent.id : 0
            if (data[i].subTaskVOS) {
                this.wrapTableTree(data[i].subTaskVOS, newKey, data[i])
            }
        }
    }

    getSelectedTasks = () => {
        const { treeData, checkedKeys } = this.state
        const tree = []
        const loop = (data, tree) => {
            for (let i = 0; i < data.length; i++) {
                const result = checkedKeys.find(key => {
                    return key === data[i].key
                })
                let node = {}
                if (result) {
                    node.task = data[i].id
                    tree.push(node)
                }
                if (data[i].subTaskVOS) {
                    node.children = []
                    loop(data[i].subTaskVOS, node.children)
                }
            }
        }
        loop(treeData, tree)
        return tree
    }

    cancleModal = () => {
        this.setState({
            selected: [],
        })
        this.props.handCancel()
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
            if (!node.subTaskVOS || node.subTaskVOS.length === 0) {
                 Api.getTaskChildren({
                    taskId: node.id,
                    level: 2,
                    directType: 2, // 获取下游
                 }).then(res => {
                    if (res.code === 1) {
                        const updated = ctx.state.treeData[0]
                        replaceTreeNode(updated, res.data)
                        const arr = [updated]
                        ctx.wrapTableTree(arr)
                        ctx.setState({ treeData: arr })
                    }
                })
            }
            resolve();
        })
    }

    getTreeNodes = (data) => {
        if (data && data.length > 0) {
            const nodes = data.map((item) => {
                const content = <Row>
                        <Col span="12" className="ellipsis" title={item.name}>{item.name}</Col>
                        <Col span="12"><TaskType value={item.taskType} /></Col>
                    </Row>
                if (item.subTaskVOS) {
                    return (<TreeNode
                        data={item}
                        disableCheckbox={item.key === '0'}
                        value={`${item.id}`}
                        title={content}
                        key={item.key}>{this.getTreeNodes(item.subTaskVOS)}
                    </TreeNode>);
                }
                return (<TreeNode
                    data={item}
                    disableCheckbox={item.key === '0'}
                    name={item.name}
                    value={`${item.id}`}
                    title={content}
                    key={item.key}
                />)
            });
            return nodes;
        }
        return []
    }

    render() {
        const { visible, handCancel, task } = this.props
        const { getFieldDecorator } = this.props.form;
        const { treeData } = this.state
        const treeNodes = this.getTreeNodes(treeData)
        return (
            <Modal
              title="补数据"
              okText="运行选中任务"
              visible={visible}
              onOk={this.addData}
              onCancel={this.cancleModal}
            >
                <Row style={{ lineHeight: '30px'  }}>
                    <FormItem {...formItemLayout} label="补数据名">
                        {getFieldDecorator('fillName', {
                            initialValue: `P_${task.name}_${moment().format('YYYY_MM_DD')}`,
                            rules: [{
                                required: true,
                                message: '请输入补数据名!',
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '补数据名称只能由字母、数字、下划线组成!',
                            }, {
                                max: 64,
                                message: '补数据名称不得超过64个字符！',
                            }],
                        })(
                            <Input placeholder="请输入补数据名"/>
                        )}
                    </FormItem>
                </Row>
                <Row style={{ lineHeight: '30px' }}>
                    <FormItem {...formItemLayout} label="业务日期：">
                        {getFieldDecorator('rangeDate', {
                            initialValue: [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                            rules: [{
                                type: 'array',
                                required: true,
                                message: '请选择业务时间!',
                            }],
                        })(
                            <RangePicker
                                disabledDate={this.disabledDate}
                                format="YYYY-MM-DD"
                                style={{width: '100%'}}
                            />
                        )}
                    </FormItem>
                </Row>
                <Row className="section patch-data">
                    <Row className="patch-header">
                        <Col span="12">任务名称</Col>
                        <Col span="12">任务类型</Col>
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
const wrappedForm = Form.create()(PatchData);
export default wrappedForm
