/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import moment from 'moment'
import * as React from 'react'
import { hashHistory } from 'react-router'
import { range } from 'lodash';

import {
    Input, Modal, Row, Form, DatePicker, TimePicker, Col, Tree, Checkbox, message, Tooltip, Icon
} from 'antd'

import { visitTree } from './hlep';
import Api from '../../../api/operation'
import { formItemLayout } from '../../../consts/comm'
import { TaskType } from '../../../components/status'
const confirm = Modal.confirm
const TreeNode = Tree.TreeNode
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker
function replaceTreeNode (treeNode: any, replace: any, replaceKey: any) {
    if (treeNode.key === replaceKey) {
        Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.subTaskVOS) {
        const children = treeNode.subTaskVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace, replaceKey)
        }
    }
}

class PatchData extends React.Component<any, any> {
    state: any = {
        treeData: [],
        selected: [],
        expandedKeys: [],
        checkedKeys: [],
        confirmLoading: false,
        loading: false, // 初始化
        startTime: '00:00', // 限制时间范围
        endTime: '23:59',
        checked: false
    }

    componentDidUpdate (prevProps: any) {
        const nextProps = this.props;
        const { task } = nextProps;
        console.log(task)
        if (prevProps.visible != nextProps.visible && nextProps.visible && task) {
            this.setState({
                checkedKeys: [`${task.id}`],
                expandedKeys: [],
                treeData: [],
                selected: []
            });
            this.loadTaskTree({
                taskId: task.id,
                level: 2,
                type: 2,
                appType: task.appType, // 获取下游
                projectId: task.projectId
            })
        }
    }

    addData = (prevProps: any) => {
        const { loading } = this.state;
        const nextProps = this.props;
        const { task } = nextProps;
        if (loading) {
            message.warn('加载中，请稍后再试');
            return;
        }
        const { form } = this.props
        const taskJson = this.getSelectedTasks()
        if (taskJson.length == 0) {
            message.warn('未选择任务');
            return;
        }
        const reqParams = form.getFieldsValue()
        reqParams.taskJson = JSON.stringify(taskJson);
        if (task.projectId) {
            reqParams.projectId = task.projectId
        } else {
            reqParams.projectId = '-1'
        }
        reqParams.tenantId = task.tenantId
        reqParams.appType = task.appType
        this.props.form.validateFields((err: any) => {
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
                Api.patchTaskData(reqParams).then((res: any) => {
                    this.setState({
                        confirmLoading: false
                    })
                    if (res.code === 1) {
                        this.showAddResult({
                            fillJobName: reqParams.fillName,
                            appType: reqParams.appType,
                            projectId: reqParams.projectId
                        })
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

    showAddResult = (params: any) => {
        this.props.handCancel()
        confirm({
            okText: '查看',
            title: '查看补数据结果',
            content: '补数据任务已在执行中，点击下方按钮查看结果',
            onOk () {
                hashHistory.push({
                    pathname: `/operation-ui/task-patch-data/detail`,
                    state: params
                })
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    loadTaskTree = (params: any) => {
        const ctx = this
        ctx.setState({
            loading: true
        })
        Api.getTaskChildren(params).then((res: any) => {
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

    wrapTableTree = (data?: any, key?: any, parent?: any) => {
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
        let _checkedKeys = checkedKeys
        const tree: any = []
        const loop = (data: any, tree: any) => {
            for (let i = 0; i < data.length; i++) {
                let _key
                const result = _checkedKeys.find((key: any) => {
                    if (key === `${data[i].id}`) {
                        _key = key
                        return true
                    }
                    return false
                })
                if (result) {
                    _checkedKeys = _checkedKeys.filter(item => item !== _key)
                    let node: any = {}
                    let children = []
                    if (result && Array.isArray(data[i].taskRuleList) && data[i].taskRuleList.length > 0) {
                        for (let item of data[i].taskRuleList) {
                            children.push({
                                task: item.id,
                                appType: item.appType
                            })
                        }
                    }
                    if (result) {
                        node.task = data[i].id
                        node.appType = data[i].appType
                        node.projectId = data[i].projectId
                        node.children = children
                        console.log(node)
                        tree.push(node)
                    }
                }
                if (data[i].subTaskVOS && _checkedKeys.length !== 0) {
                    loop(data[i].subTaskVOS, result && tree.length !== 0 ? tree[tree.length - 1].children : tree)
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

    onCheck = (checkedKeys: any, checkedNodes: any) => {
        const checked: string[] = checkedKeys.checked;
        let checkedSet: Set<string> = new Set(checked);
        const node = checkedNodes.node;
        const treeData = this.state.treeData;
        interface Child {
            level: number;
            key: string;
        }
        /**
         * 获取所有经过改节点的路径
         */
        function getPath (tree: any[], key: string): {
            children: string[][];
            parents: string[];
        } {
            let children: Child[][] = [];
            let parents: string[] = [];
            visitTree(tree, (node, level) => {
                let nodeKey = node.key;
                if (nodeKey == key) {
                    return;
                }
                if (node.key.indexOf(key) == 0) {
                    let lastPath = children[children.length - 1];
                    const lastLevel = (lastPath && lastPath.length) ? lastPath[lastPath.length - 1].level : null;
                    if (lastLevel == null || lastLevel >= level) {
                        const newRootPath = (lastPath || []).map((item) => { return item.level < level ? item : null; }).filter(Boolean);
                        let newPath = newRootPath.concat({
                            level: level,
                            key: nodeKey
                        });
                        children.push(newPath)
                    } else {
                        lastPath.push({
                            level: level,
                            key: nodeKey
                        });
                    }
                }
                if (key.indexOf(node.key) == 0) {
                    parents.push(nodeKey);
                }
            });
            return {
                children: children.map((childArr) => {
                    return childArr.map((child) => {
                        return child.key;
                    })
                }),
                parents
            }
        }
        function add (key: any, tree: any, result: any) {
            if (!tree) {
                return;
            }
            let { children, parents } = getPath(tree, key);
            for (let i = 0; i < children.length; i++) {
                let childArr = children[i];
                for (let j = childArr.length - 1; j >= 0; j--) {
                    let child = childArr[j];
                    if (checked.includes(child)) {
                        let pushFunc = result.add.bind(checkedSet);
                        childArr.slice(0, j).map(pushFunc)
                    }
                }
            }
            for (let i = 0; i < parents.length; i++) {
                let parent = parents[i];
                if (checked.includes(parent)) {
                    let pushFunc = result.add.bind(checkedSet);
                    parents.slice(i + 1, parents.length).map(pushFunc)
                }
            }
        }

        function remove (key: any, tree: any, result: any) {
            if (!tree) {
                return;
            }
            let { children, parents } = getPath(tree, key);
            let haveParent = new Set(parents.concat(checked)).size !== parents.length + checked.length;
            if (haveParent) {
                let deleteFunc = result.delete.bind(checkedSet);
                for (let i = 0; i < children.length; i++) {
                    let childArr = children[i];
                    childArr.map(deleteFunc);
                }
            }
        }

        if (checkedNodes.checked) {
            add(node.props.eventKey, treeData, checkedSet);
        } else {
            remove(node.props.eventKey, treeData, checkedSet)
        }
        this.setState({ checkedKeys: [...Array.from(checkedSet)] })
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    asyncTree = (treeNode: any) => {
        const ctx = this
        return new Promise((resolve: any) => {
            const node = treeNode.props.data;
            console.log(node)
            const isWorkflowNode = node.flowId && node.flowId !== 0;
            if (isWorkflowNode) {
                resolve();
                return true;
            } else {
                Api.getTaskChildren({
                    taskId: node.id,
                    level: 2,
                    type: 2, // 获取下游
                    appType: node.appType,
                    projectId: node.projectId
                }).then((res: any) => {
                    if (res.code === 1) {
                        const updated = ctx.state.treeData[0]
                        replaceTreeNode(updated, res.data, node.key)
                        const arr: any = [updated]
                        ctx.wrapTableTree(arr)
                        ctx.setState({ treeData: arr })
                    }
                })
                resolve();
            }
        })
    }

    getTreeNodes = (data: any) => {
        if (data && data.length > 0) {
            const nodes = data.map((item: any) => {
                const name = item.name;
                let nameVal = name;
                if (nameVal && nameVal.length > 10) {
                    nameVal = name.substring(0, 10);
                }
                const titleFix = { title: name };
                const itemValue = `${item.id}`;
                const content = <Row>
                    <Col span={6} className="ellipsis" {...titleFix}>
                        <Tooltip placement="topLeft" title={name}>
                            {nameVal === name ? nameVal : `${nameVal}...`}
                        </Tooltip>
                    </Col>
                    <Col span={5}><TaskType value={item.taskType} /></Col>
                    <Col span={6}>{item.tenantName}</Col>
                    <Col style={{ textAlign: 'right', paddingRight: '8px' }} span={6}>{item.projectName}</Col>
                </Row>
                if (item.subTaskVOS) {
                    return (
                        <TreeNode
                            data={item}
                            value={itemValue}
                            title={content}
                            key={itemValue}
                        >
                            {this.getTreeNodes(item.subTaskVOS)}
                        </TreeNode>
                    );
                }
                return (<TreeNode
                    data={item}
                    name={name}
                    value={itemValue}
                    title={content}
                    isLeaf
                    key={itemValue}
                />)
            });
            return nodes;
        }
        return []
    }

    expandChange (expandedKeys: any, expand: any) {
        if (!expand.expanded) {
            const key = expand.node.props.data.key;
            expandedKeys = expandedKeys.filter(
                (item: any) => {
                    return item.indexOf(key) != 0
                }
            )
        }
        this.setState({
            expandedKeys: expandedKeys
        })
    }

    splitTime = (time: any) => {
        return time.split(':');
    }

    disabledHours = (timeType: any) => {
        const { startTime, endTime } = this.state;
        // 开始时间
        const startTimeHour = Number(this.splitTime(startTime)[0]);
        // 结束时间
        const endTimeHour = Number(this.splitTime(endTime)[0]);
        const hours = range(0, 60)
        // console.log(startTimeHour, endTimeHour)
        if (timeType == 'start') {
            hours.splice(0, endTimeHour + 1); // 不禁用的小时
            return hours;
        } else if (timeType == 'end') {
            hours.splice(startTimeHour, 24); // 不禁用的小时
            return hours;
        }
    }

    disabledMinutes = (timeType: any) => {
        const { startTime, endTime } = this.state;
        // 开始时间
        const startTimeHour = Number(this.splitTime(startTime)[0]);
        const startTimeMinute = Number(this.splitTime(startTime)[1]);
        // 结束时间
        const endTimeHour = Number(this.splitTime(endTime)[0]);
        const endTimeMinute = Number(this.splitTime(endTime)[1]);
        if (timeType == 'start' && startTimeHour == endTimeHour) {
            return range(endTimeMinute + 1, 60)
        } else if (timeType == 'end' && startTimeHour == endTimeHour) {
            return range(0, startTimeMinute)
        } else {
            return [];
        }
    }

    changeCheckbox = (e: any) => {
        this.setState({
            checked: e.target.checked,
            startTime: '00:00',
            endTime: '23:59'
        })
    }

    getData = (node: any, arr?: any) => {
        let nodeList = arr || [];
        if (node && node.length) {
            node.forEach((item: any) => {
                nodeList.push(item.id.toString())
                if (item.subTaskVOS) {
                    this.getData(item.subTaskVOS, nodeList);
                }
            })
        }
        return nodeList;
    }

    seleceAll = (val: any) => {
        if (!val.target.checked) {
            this.setState({
                checkedKeys: []
            })
        } else {
            const { treeData } = this.state;
            const checkedKeys = this.getData(treeData);
            this.setState({
                checkedKeys
            })
        }
    }

    render () {
        const { visible, task } = this.props;
        const { getFieldDecorator } = this.props.form;
        const { treeData, confirmLoading, checked } = this.state;
        const treeNodes = this.getTreeNodes(treeData);
        const patchName = `P_${task && task.name}_${moment().format('YYYY_MM_DD_mm_ss')}`
        const format = 'HH:mm';
        return visible && (
            <Modal
                title="补数据"
                okText="运行选中任务"
                visible={true}
                width={650}
                onOk={this.addData}
                onCancel={this.cancleModal}
                confirmLoading={confirmLoading}
            >
                <Row style={{ lineHeight: '30px' }}>
                    <FormItem {...formItemLayout} label="补数据名">
                        {getFieldDecorator('fillName', {
                            initialValue: patchName,
                            rules: [{
                                required: true,
                                message: '请输入补数据名!'
                            }, {
                                pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
                                message: '补数据名称只能由字母、数字、中文、下划线组成!'
                            }, {
                                max: 128,
                                message: '补数据名称不得超过128个字符！'
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
                <Row style={{ lineHeight: '30px', margin: '-10px 0px 10px 0px' }}>
                    <Col offset={formItemLayout.labelCol.sm.span}>
                        <Checkbox
                            checked={checked}
                            onChange={this.changeCheckbox}
                        >选择分钟粒度</Checkbox>
                        {/* <HelpDoc style={style} doc="minuteParticleHelp" /> */}
                        <Tooltip
                            placement="top"
                            title='产生指定的业务日期内，指定的时间范围内计划开始运行的实例，例如：
                            业务日期：2019-01-01~2019-01-03
                            具体时间：01:30~03:00
                            表示：2019-01-01~2019-01-03期间内，每天的01:30~03:00开始运行的实例，时间范围为闭区间，时间范围选择了23:59后，计划23:59开始运行的实例也会产生 支持将数值类型、Timestamp类型作为增量标识字段
                            选择分钟粒度后，补数据时，跨周期依赖配置无效'>
                            <Icon type="question-circle" />
                        </Tooltip>
                    </Col>
                </Row>
                {
                    checked ? (
                        <Row style={{ lineHeight: '30px' }}>
                            <FormItem {...formItemLayout} label="具体时间">
                                <Col span={11}>
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
                                            onChange={(time: any, timeString: any) => {
                                                this.setState({
                                                    startTime: timeString
                                                })
                                            }}
                                        />
                                    )}
                                </Col>
                                <Col span={2}>
                                    <span style={{ display: 'inline-block', width: '100%', textAlign: 'center' }}>
                                        -
                                    </span>
                                </Col>
                                <Col span={11}>
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
                                            onChange={(time: any, timeString: any) => {
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
                        <Col span={6}>
                            <Checkbox
                                onChange={this.seleceAll.bind(this)}
                                style={{ marginRight: 10 }}
                            />
                            任务名称
                        </Col>
                        <Col span={6}>任务类型</Col>
                        <Col span={6}>所属租户</Col>

                        <Col style={{ textAlign: 'right', paddingRight: '8px' }} span={6}>所属项目</Col>
                    </Row>
                    <Tree
                        checkable
                        multiple
                        checkStrictly
                        autoExpandParent={false}
                        onCheck={this.onCheck}
                        loadData={this.asyncTree.bind(this)}
                        checkedKeys={this.state.checkedKeys}
                        expandedKeys={this.state.expandedKeys}
                        onExpand={this.expandChange.bind(this)}
                    >
                        {treeNodes}
                    </Tree>
                </Row>
            </Modal>
        )
    }
}
const wrappedForm = Form.create<any>()(PatchData);
export default wrappedForm
