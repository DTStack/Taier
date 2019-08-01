import * as React from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Row, Input, Select as mSelect, message,
    Pagination, Checkbox, Form,
    DatePicker, TimePicker, Tree
} from 'antd'

import utils from 'utils'

import Api from '../../../../api'
import NoData from '../../../../components/no-data'
import { offlineTaskStatusFilter } from '../../../../comm/const'
import { TaskBadgeStatus } from '../../../../components/status'
import * as FlowAction from '../../../../store/modules/operation/taskflow'

const Search = Input.Search
const TreeNode = Tree.TreeNode
const Select: any = mSelect;
const Option: any = Select.Option
const FormItem = Form.Item

function replaceTreeNode (treeNode: any, replace: any) {
    if (treeNode && treeNode.length > 0) {
        for (let i = 0; i < treeNode.length; i += 1) {
            if (treeNode[i].data === replace.data) {
                treeNode[i] = Object.assign(treeNode[i], replace);
                return;
            }
            if (treeNode[i].children) {
                replaceTreeNode(treeNode[i].children, replace)
            }
        }
    }
}

function getTimeString (date: any) {
    return date ? date.format('HH:mm') : ''
}

class PatchData extends React.Component<any, any> {
    state: any = {
        loading: false,
        whichTask: [0],
        chooseTime: 0,
        current: 1,
        tasks: {
            data: []
        },
        owner: undefined,
        startTime: '',
        endTime: '',
        bussinessDate: utils.getParameterByName('patchBizTime')
            ? moment(utils.getParameterByName('patchBizTime')) : '',
        runningDate: utils.getParameterByName('patchBizTime') ? '' : moment(),
        taskStatus: [],
        jobType: '',
        taskName: utils.getParameterByName('patchName') || '',
        selected: '',
        expandedKeys: []
    }

    componentDidMount () {
        this.loadPatchData()
    }

    // eslint-disable-next-line
    componentWillReceiveProps(nextProps: any) {
        const { project } = nextProps
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadPatchData()
        }
    }

    searchJob = (query: any) => {
        const params: any = {}
        if (query) {
            const taskName = utils.trim(query)
            params.fillJobName = taskName
        }
        this.loadPatchData(params)
    }

    loadPatchData (params?: any) {
        const ctx = this
        this.setState({ loading: true, expandedKeys: [] })
        if (this.valideParams()) {
            let defaultParams = this.getReqParams()
            const reqParams = Object.assign(defaultParams, params)
            Api.getFillData(reqParams).then((res: any) => {
                if (res.code === 1) {
                    ctx.setState({ tasks: res.data, loading: false })
                    ctx.props.dispatch(FlowAction.setTaskFlow({ id: 0 }))
                }
            })
        }
    }

    loadPatchDataDetail (params: any, node: any) {
        const ctx = this
        this.setState({ loading: true })
        Api.getFillDataDetail(params).then((res: any) => {
            if (res.code === 1) {
                const tasks = Object.assign(ctx.state.tasks)
                if (res.data && res.data.length > 0) {
                    node.children = res.data
                    replaceTreeNode(tasks, node)
                }
                ctx.setState({ tasks: tasks, loading: false })
            }
        })
    }

    loadPatchDate (params: any, node: any) { // 查询补数据的日期列表
        const ctx = this
        this.setState({ loading: true })
        Api.getFillDate(params).then((res: any) => {
            if (res.code === 1) {
                const tasks = Object.assign(ctx.state.tasks)
                if (res.data && res.data.length > 0) {
                    node.children = res.data
                    replaceTreeNode(tasks, node)
                }
                ctx.setState({ tasks: tasks, loading: false })
            }
        })
    }

    valideParams = () => {
        const {
            runningDate, bussinessDate
        } = this.state

        if (!runningDate && !bussinessDate) {
            message.error('运行日期和业务日期必须选择一个！')
            return false
        }
        return true;
    }

    getReqParams = () => {
        const {
            taskStatus, owner, jobType,
            runningDate, bussinessDate, taskName
        } = this.state
        let reqParams: any = { currentPage: 1, pageSize: 10 }
        if (taskName) {
            reqParams.jobName = taskName
        }
        if (bussinessDate) {
            reqParams.bizDay = moment(bussinessDate).format('YYYY-MM-DD')
        }
        if (runningDate) {
            reqParams.runDay = moment(runningDate).format('YYYY-MM-DD')
        }
        if (taskStatus && taskStatus.length > 0) {
            reqParams.status = taskStatus.join(',')
        }
        if (owner) {
            reqParams.dutyUserId = owner
        }
        if (jobType) {
            reqParams.type = jobType
        }
        return reqParams
    }

    asyncTree = (treeNode: any) => {
        const ctx = this
        const { startTime, endTime, taskStatus, bussinessDate, taskName } = this.state
        const node = treeNode.props.data
        const parent = treeNode.props.parent
        return new Promise((resolve: any) => {
            if (!node.children || node.children.length === 0) { // PRE_VIEW(0), BIZ_DAY(1)
                if (node.type === 0) {
                    const params: any = {
                        fillJobName: node.data,
                        jobName: taskName
                    }
                    if (taskStatus && taskStatus.length > 0) {
                        params.status = taskStatus.join(',')
                    }
                    if (bussinessDate) {
                        params.bizDay = bussinessDate.format('YYYY-MM-DD')
                    }
                    ctx.loadPatchDate(params, node)
                } else if (node.type === 1) {
                    const params: any = {
                        bizDay: node.data,
                        fillJobName: parent.data || '',
                        jobName: taskName
                    }
                    params.fromTime = getTimeString(startTime)
                    params.toTime = getTimeString(endTime)
                    ctx.loadPatchDataDetail(params, node)
                }
            }
            resolve();
        })
    }

    pageChange = (page: any) => {
        const params: any = { currentPage: page }
        this.setState({ current: page })
        this.loadPatchData(params)
    }

    selectTreeItem = (selectedKeys: any, item: any) => {
        const task = item.node.props.data
        if (task.batchTask) {
            this.props.dispatch(FlowAction.setTaskFlow(task))
        }
    }

    onWhichTask = (value: any) => {
        const val = value && value.length > 0 ? [value[value.length - 1]] : []
        const { user } = this.props
        // const userId = parseInt(, 10)
        const data: any = {
            owner: `${user.id}`,
            whichTask: val,
            current: 1
        }
        if (val[0] === 2) { // is mine
            data.runningDate = moment()
        }
        this.setState(data, () => {
            this.loadPatchData()
        })
    }

    onTaskStatus = (e: any) => {
        this.setState({ taskStatus: e, current: 1 }, () => {
            this.loadPatchData()
        })
    }

    onBuisTimeChange = (date: any) => {
        this.setState({ bussinessDate: date, current: 1 }, () => {
            this.loadPatchData()
        });
    }

    onRunningTime = (date: any) => {
        this.setState({ runningDate: date, current: 1 }, () => {
            this.loadPatchData()
        });
    }

    onStartTime = (date: any) => {
        this.setState({ startTime: date, current: 1 }, () => {
            this.loadPatchData()
        });
    }

    onEndTime = (date: any) => {
        this.setState({ endTime: date, current: 1 }, () => {
            this.loadPatchData()
        });
    }

    onChangeTaskName = (e: any) => {
        this.setState({ taskName: e.target.value })
    }

    onOwnerChange = (value: any) => {
        const state: any = { owner: value, current: 1 }
        if (value === 0 || value === undefined) {
            state.whichTask = 0
        }
        this.setState(state, () => {
            this.loadPatchData()
        });
    }

    onExpandTree = (expandedKeys: any) => {
        this.setState({ expandedKeys })
    }

    getTreeNodes = (data?: any, parent?: any) => {
        if (data && data.length > 0) {
            const pid = parent ? parent.data : 0
            return data.map((item: any, index: any) => {
                if (item.children) {
                    return (<TreeNode
                        data={item}
                        parent={parent}
                        value={`${item.data}`}
                        title={<span>{item.data}</span>}
                        key={`${pid}-${item.data}-${index}`}>{this.getTreeNodes(item.children, item)}
                    </TreeNode>);
                }
                const content = item.batchTask ? (
                    <span>
                        <p><TaskBadgeStatus value={item.status} /> {item.batchTask.name}</p>
                        <p>{item.businessDate}</p>
                    </span>) : item.data
                return (<TreeNode
                    data={item}
                    parent={parent}
                    value={`${item.data}`}
                    title={content}
                    key={`${pid}-${item.data}-${index}`} isLeaf={item.batchTask}
                />)
            });
        }
        return <NoData />
    }

    render () {
        const {
            tasks, startTime, endTime, whichTask,
            owner, bussinessDate, runningDate, taskName
        } = this.state
        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item: any) => {
                return (
                    <Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                        {item.user.userName}
                    </Option>)
            }) : []
        const statusFilter = offlineTaskStatusFilter && offlineTaskStatusFilter.length > 0
            ? offlineTaskStatusFilter.map((item: any) => {
                return (<Option key={item.id} value={item.value} name={item.text}>
                    {item.text}
                </Option>)
            }) : []
        const showTime = bussinessDate ? 'block' : 'none'

        const treeNodes = this.getTreeNodes(tasks.data)
        return (
            <div className="flow-operation">
                <div className="filter-bar bd-bottom">
                    <Form layout="inline">
                        <FormItem>
                            <Search
                                placeholder="任务名称搜索"
                                style={{ width: '280px' }}
                                value={taskName}
                                onChange={this.onChangeTaskName}
                                onSearch={this.searchJob}
                            />
                        </FormItem>
                        <FormItem label="运行日期">
                            <DatePicker
                                format="YYYY-MM-DD"
                                placeholder="运行日期"
                                value={runningDate}
                                onChange={this.onRunningTime}
                            />
                        </FormItem>
                        <FormItem label="业务日期">
                            <DatePicker
                                format="YYYY-MM-DD"
                                placeholder="业务日期"
                                value={bussinessDate}
                                onChange={this.onBuisTimeChange}
                            />
                        </FormItem>
                        <FormItem
                            style={{ paddingBottom: '10px', display: showTime }}
                        >
                            <TimePicker
                                value={startTime}
                                style={{ width: '80px' }}
                                format="HH:mm"
                                onChange={this.onStartTime}
                                placeholder="开始"
                            />&nbsp;~&nbsp;
                            <TimePicker
                                format="HH:mm"
                                style={{ width: '80px' }}
                                value={endTime}
                                onChange={this.onEndTime}
                                placeholder="截止"
                            />
                        </FormItem>
                        <FormItem>
                            <Checkbox.Group value={whichTask} onChange={this.onWhichTask}>
                                <Checkbox value={1}>我的任务</Checkbox>
                                <Checkbox value={2}>我今天补的</Checkbox>
                            </Checkbox.Group>
                        </FormItem>
                        <FormItem label="责任人">
                            <Select
                                allowClear
                                showSearch
                                size='Default'
                                style={{ width: '126px' }}
                                placeholder="责任人"
                                optionFilterProp="name"
                                value={owner}
                                onChange={this.onOwnerChange}
                            >
                                {userItems}
                            </Select>
                        </FormItem>
                        <FormItem label="&nbsp;&nbsp;&nbsp;&nbsp;状态">
                            <Select
                                allowClear
                                style={{ width: '200px' }}
                                placeholder="选择状态"
                                mode="multiple"
                                onChange={this.onTaskStatus}
                            >
                                {statusFilter}
                            </Select>
                        </FormItem>
                    </Form>
                </div>
                <Row className="task-list" style={{ padding: '5px' }}>
                    <Tree
                        showLine
                        autoExpandParent={false}
                        expandedKeys={this.state.expandedKeys}
                        onExpand={this.onExpandTree}
                        onSelect={this.selectTreeItem}
                        loadData={this.asyncTree}
                    >
                        {treeNodes}
                    </Tree>
                    <Pagination
                        simple
                        className="txt-right"
                        onChange={this.pageChange}
                        current={this.state.current}
                        total={tasks.totalCount} />
                </Row>
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        project: state.project,
        user: state.user,
        projectUsers: state.projectUsers
    }
})(PatchData)
