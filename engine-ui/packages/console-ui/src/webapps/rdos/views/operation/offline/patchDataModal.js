import moment from 'moment'
import React, { Component } from 'react'
import { hashHistory } from 'react-router'

import {
    Input, Modal, Row, Form, DatePicker, TimePicker, Col, Tree, Checkbox, message
} from 'antd'

import Api from '../../../api'
import { formItemLayout } from '../../../comm/const'
import { TaskType } from '../../../components/status'
import HelpDoc from '../../helpDoc';
const confirm = Modal.confirm
const TreeNode = Tree.TreeNode
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker
function replaceTreeNode (treeNode, replace, replaceKey) {
    if (treeNode.key === replaceKey) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.subTaskVOS) {
        const children = treeNode.subTaskVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace, replaceKey)
        }
    }
}

class PatchData extends Component {
    state = {
        treeData: [],
        selected: [],
        expandedKeys: [],
        checkedKeys: ['0'],
        confirmLoading: false,
        loading: false, // 初始化
        startTime: '00:00', // 限制时间范围
        endTime: '23:59',
        checked: false
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const task = nextProps.task
        if (this.props.visible != nextProps.visible && nextProps.visible && task) {
            this.setState({
                checkedKeys: ['0'],
                expandedKeys: [],
                treeData: [],
                selected: []
            })
            this.loadTaskTree({
                taskId: task.id,
                level: 2,
                directType: 2 // 获取下游
            })
        }
    }

    addData = () => {
        const { loading } = this.state;
        if (loading) {
            message.warn('加载中，请稍后再试');
            return;
        }
        const { form } = this.props
        const taskJson = this.getSelectedTasks()
        const reqParams = form.getFieldsValue()
        reqParams.taskJson = taskJson.length > 0 ? JSON.stringify(taskJson[0]) : ''
        this.props.form.validateFields((err) => {
            if (!err) {
                this.setState({
                    confirmLoading: true
                })
                reqParams.fromDay = reqParams.rangeDate[0].set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0
                }).unix()
                reqParams.toDay = reqParams.rangeDate[1].set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59
                }).unix()
                delete reqParams.rangeDate;
                reqParams.concreteStartTime = reqParams.concreteStartTime && reqParams.concreteStartTime.format('HH:mm')
                reqParams.concreteEndTime = reqParams.concreteEndTime && reqParams.concreteEndTime.format('HH:mm')
                Api.patchTaskData(reqParams).then((res) => {
                    this.setState({
                        confirmLoading: false
                    })
                    if (res.code === 1) {
                        this.showAddResult(reqParams.fillName)
                        setTimeout(() => {
                            form.resetFields()
                            this.setState({
                                checked: false
                            })
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
            onOk () {
                hashHistory.push(`/operation/task-patch-data/${fillJobName}`)
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    loadTaskTree = (params) => {
        const ctx = this
        ctx.setState({
            loading: true
        })
        Api.getTaskChildren(params).then(res => {
            ctx.setState({
                loading: false
            })
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
            startTime: '00:00',
            endTime: '23:59',
            checked: false
        })
        this.props.form.resetFields()
        this.props.handCancel()
    }

    onCheck = (checkedKeys, checkedNodes) => {
        const checked = checkedKeys.checked;
        let checkedSet = new Set(checked);
        const node = checkedNodes.node;
        const treeData = this.state.treeData;

        function addParents (key, tree, result) {
            if (!tree) {
                return;
            }
            for (let i = 0; i < tree.length; i++) {
                let node = tree[i];
                let nodeKey = node.key;
                if (key.indexOf(nodeKey) == 0) {
                    result.add(nodeKey);
                }
                addParents(key, node.subTaskVOS, result);
            }
        }

        function removeChildren (key, tree, result) {
            if (!tree) {
                return;
            }
            for (let i = 0; i < tree.length; i++) {
                let node = tree[i];
                let nodeKey = node.key;
                if (nodeKey.indexOf(key) == 0) {
                    result.delete(nodeKey);
                }
                removeChildren(key, node.subTaskVOS, result);
            }
        }

        if (checkedNodes.checked) {
            addParents(node.props.eventKey, treeData, checkedSet);
        } else {
            removeChildren(node.props.eventKey, treeData, checkedSet)
        }

        if (checkedSet && checkedSet.size > 0) {
            this.setState({ checkedKeys: [...checkedSet] })
        }
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    asyncTree = (treeNode) => {
        const ctx = this
        return new Promise((resolve) => {
            const node = treeNode.props.data;
            const isWorkflowNode = node.flowId && node.flowId !== 0;
            if (isWorkflowNode) {
                resolve();
                return true;
            } else {
                Api.getTaskChildren({
                    taskId: node.id,
                    level: 2,
                    directType: 2 // 获取下游
                }).then(res => {
                    if (res.code === 1) {
                        const updated = ctx.state.treeData[0]
                        replaceTreeNode(updated, res.data, node.key)
                        const arr = [updated]
                        ctx.wrapTableTree(arr)
                        ctx.setState({ treeData: arr })
                    }
                })
                resolve();
            }
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

    expandChange (expandedKeys, expand) {
        if (!expand.expanded) {
            const key = expand.node.props.data.key;
            expandedKeys = expandedKeys.filter(
                (item) => {
                    return item.indexOf(key) != 0
                }
            )
        }
        this.setState({
            expandedKeys: expandedKeys
        })
    }
    range = (start, end) => {
        const result = [];
        for (let i = start; i < end; i++) {
            result.push(i);
        }
        return result;
    }
    splitTime = (time) => {
        return time.split(':');
    }
    disabledHours = (timeType) => {
        const { startTime, endTime } = this.state;
        // 开始时间
        const startTimeHour = Number(this.splitTime(startTime)[0]);
        // 结束时间
        const endTimeHour = Number(this.splitTime(endTime)[0]);
        const hours = this.range(0, 60)
        // console.log(startTimeHour, endTimeHour)
        if (timeType == 'start') {
            hours.splice(0, endTimeHour + 1); // 不禁用的小时
            return hours;
        } else if (timeType == 'end') {
            hours.splice(startTimeHour, 24); // 不禁用的小时
            return hours;
        }
    }
    disabledMinutes = (timeType) => {
        const { startTime, endTime } = this.state;
        // 开始时间
        const startTimeHour = Number(this.splitTime(startTime)[0]);
        const startTimeMinute = Number(this.splitTime(startTime)[1]);
        // 结束时间
        const endTimeHour = Number(this.splitTime(endTime)[0]);
        const endTimeMinute = Number(this.splitTime(endTime)[1]);
        if (timeType == 'start' && startTimeHour == endTimeHour) {
            return this.range(endTimeMinute + 1, 60)
        } else if (timeType == 'end' && startTimeHour == endTimeHour) {
            return this.range(0, startTimeMinute)
        } else {
            return [];
        }
    }
    changeCheckbox = (e) => {
        this.setState({
            checked: e.target.checked,
            startTime: '00:00',
            endTime: '23:59'
        })
    }
    render () {
        const { visible, task } = this.props;
        const { getFieldDecorator } = this.props.form;
        const { treeData, confirmLoading, checked } = this.state;
        const treeNodes = this.getTreeNodes(treeData);
        // const randomNumber = Math.floor(Math.random() * (100 - 1) + 1);
        const pacthName = `P_${task && task.name}_${moment().format('YYYY_MM_DD_mm_ss')}`
        const format = 'HH:mm';
        const style = {
            position: 'absolute',
            right: 85,
            top: -8
        }
        return (
            <Modal
                title="补数据"
                okText="运行选中任务"
                visible={visible}
                onOk={this.addData}
                onCancel={this.cancleModal}
                confirmLoading={confirmLoading}
            >
                <Row style={{ lineHeight: '30px' }}>
                    <FormItem {...formItemLayout} label="补数据名">
                        {getFieldDecorator('fillName', {
                            initialValue: pacthName,
                            rules: [{
                                required: true,
                                message: '请输入补数据名!'
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '补数据名称只能由字母、数字、下划线组成!'
                            }, {
                                max: 64,
                                message: '补数据名称不得超过64个字符！'
                            }]
                        })(
                            <Input placeholder="请输入补数据名" />
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
                                message: '请选择业务时间!'
                            }]
                        })(
                            <RangePicker
                                disabledDate={this.disabledDate}
                                format="YYYY-MM-DD"
                                style={{ width: '100%' }}
                            />
                        )}
                    </FormItem>
                </Row>
                <Row style={{ lineHeight: '30px' }}>
                    <div style={{ float: 'left', margin: '-16 0 0 120' }}>
                        <Checkbox
                            checked={checked}
                            onChange={this.changeCheckbox}
                        >选择分钟粒度</Checkbox>
                    </div>
                    <HelpDoc style={style} doc="minuteParticleHelp" />
                </Row>
                {
                    checked ? (
                        <Row style={{ lineHeight: '30px' }}>
                            <FormItem {...formItemLayout} label="具体时间">
                                <Col span='11'>
                                    {getFieldDecorator('concreteStartTime', {
                                        initialValue: moment('00:00', format),
                                        rules: [{
                                            required: true,
                                            message: '请选择具体时间!'
                                        }]
                                    })(
                                        <TimePicker
                                            format={format}
                                            style={{ width: '100%' }}
                                            allowEmpty={false}
                                            disabledHours={this.disabledHours.bind(this, 'start')}
                                            disabledMinutes={this.disabledMinutes.bind(this, 'start')}
                                            onChange={(time, timeString) => {
                                                this.setState({
                                                    startTime: timeString
                                                })
                                            }}
                                        />
                                    )}
                                </Col>
                                <Col span='2'>
                                    <span style={{ display: 'inline-block', width: '100%', textAlign: 'center' }}>
                                        -
                                    </span>
                                </Col>
                                <Col span='11'>
                                    {getFieldDecorator('concreteEndTime', {
                                        initialValue: moment('23:59', format),
                                        rules: [{
                                            required: true,
                                            message: '请选择具体时间!'
                                        }]
                                    })(
                                        <TimePicker
                                            format="HH:mm"
                                            style={{ width: '100%' }}
                                            allowEmpty={false}
                                            disabledHours={this.disabledHours.bind(this, 'end')}
                                            disabledMinutes={this.disabledMinutes.bind(this, 'end')}
                                            onChange={(time, timeString) => {
                                                this.setState({
                                                    endTime: timeString
                                                })
                                            }}
                                        />
                                    )}
                                </Col>
                            </FormItem>
                        </Row>
                    ) : null
                }
                <Row className="section patch-data">
                    <Row className="patch-header">
                        <Col span="12">任务名称</Col>
                        <Col span="12">任务类型</Col>
                    </Row>
                    <Tree
                        autoExpandParent={false}
                        checkable
                        multiple
                        checkStrictly
                        expandedKeys={this.state.expandedKeys}
                        onCheck={this.onCheck}
                        checkedKeys={this.state.checkedKeys}
                        loadData={this.asyncTree}
                        onExpand={this.expandChange.bind(this)}
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
