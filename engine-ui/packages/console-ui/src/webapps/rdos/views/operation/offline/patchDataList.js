import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { Link } from 'react-router'

import {
    Row, Input, Select, Menu, message,
    Col, Radio, Pagination, Checkbox, Form,
    DatePicker, TimePicker, Table, Card,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { offlineTaskStatusFilter } from '../../../comm/const'
import { TaskBadgeStatus } from '../../../components/status'
import * as FlowAction from '../../../store/modules/operation/taskflow'

const Search = Input.Search
const RadioGroup = Radio.Group
const Option = Select.Option
const FormItem = Form.Item

function getTimeString(date) {
    return date ? date.format('HH:mm') : ''
}

class PatchDataList extends Component {

    state = {
        loading: false,
        chooseTime: 0,
        current: 1 ,
        tasks: {
            data: [],
        },
        owner: undefined,
        startTime: '',
        endTime: '',
        bussinessDate: utils.getParameterByName('patchBizTime') 
        ? moment(utils.getParameterByName('patchBizTime')) : '',
        runningDate: '',
        taskStatus: [],
        jobType: '',
        taskName: utils.getParameterByName('patchName') || '',
        selected: '',
        expandedKeys: [],
    }

    componentDidMount() {
        this.loadPatchData()
    }

    componentWillReceiveProps(nextProps) {
        const { project } = nextProps
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadPatchData()
        }
    }

    searchJob = (query) => {
        const params = {}
        if (query) {
            const taskName = utils.trim(query)
            params.fillJobName = taskName
        }
        this.loadPatchData(params)
    }

    loadPatchData(params) {
        const ctx = this
        this.setState({ loading: true, expandedKeys: [] })
        let defaultParams = this.getReqParams()
        const reqParams = Object.assign(defaultParams, params)
        Api.getFillData(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data, loading: false })
                ctx.props.dispatch(FlowAction.setTaskFlow({ id: 0 }))
            }
        })
    }

    loadPatchDataDetail(params, node) {
        const ctx = this
        this.setState({ loading: true })
        Api.getFillDataDetail(params).then((res) => {
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

    loadPatchDate(params, node) { // 查询补数据的日期列表
        const ctx = this
        this.setState({ loading: true })
        Api.getFillDate(params).then((res) => {
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

    getReqParams = () => {
        const {
            startTime, endTime,
            taskStatus, owner, jobType,
            runningDate, bussinessDate, taskName,
        } = this.state
        let reqParams = { currentPage: 1, pageSize: 10, }
        if (taskName) {
            reqParams.jobName = taskName
        }
        if (bussinessDate) {
            reqParams.bizDay = moment(bussinessDate).format('YYYY-MM-DD').unix()
        }
        if (runningDate) {
            reqParams.runDay = moment(runningDate).format('YYYY-MM-DD').unix()
        }
        if (taskStatus && taskStatus.length > 0) {
            reqParams.status = taskStatus.join(',')
        }
        if (startTime && endTime) {
            reqParams.startTime = startTime
            reqParams.endTime = endTime
        }
        if (owner) {
             reqParams.dutyUserId = owner
        }
        if (jobType) {
            reqParams.type = jobType
        }
        return reqParams
    }

    pageChange = (page) => {
        const params = { currentPage: page }
        this.setState({ current: page })
        this.loadPatchData(params)
    }

    selectTreeItem = (selectedKeys, item) => {
        const task = item.node.props.data
        if (task.batchTask) {
            this.props.dispatch(FlowAction.setTaskFlow(task))
        }
    }

    onBuisTimeChange = (date) => {
        this.setState({ bussinessDate: date, current: 1 }, this.loadPatchData);
    }

    onRunningTime = (date) => {
        this.setState({ runningDate: date, current: 1 }, this.loadPatchData);
    }

    onChangeTaskName = (e) => {
        this.setState({ taskName: e.target.value })
    }

    onOwnerChange = (value) => {
        const state = { owner: value, current: 1, }
        this.setState(state, this.loadPatchData);
    }

    onCheckChange = (checkedList) => {
        const { user } = this.props;
        const conditions = {
            person: '',
            startTime: '',
            endTime: '',
            scheduleStatus: 1,
        };
        checkedList.forEach(item => {
            if (item === 'person') {
                conditions.owner  = `${user.id}`;
            } else if (item === 'todayUpdate') {
                conditions.startTime = moment().set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0,
                }).unix()
                conditions.endTime = moment().set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59,
                }).unix()
            }
        })

        this.setState(conditions, this.loadPatchData)
    }

    initTaskColumns = () => {
        return [{
            title: '补数据名称',
            dataIndex: 'data',
            key: 'patchName',
            render: (text, record) => {
                return (
                    <Link to={`/operation/task-patch-data/${record.id}`}>{text}</Link>
                )
            },
        }, {
            title: '已完成/总任务数',
            width: 120,
            dataIndex: 'taskCount',
            key: 'taskCount',
            render: (text, record) => {
                return  '1/12'
            },
        }, {
            title: '业务日期',
            dataIndex: 'businessDate',
            key: 'businessDate'
        }, {
            title: '开始运行时间',
            dataIndex: 'execStartDate',
            key: 'execStartDate',
        }, {
            title: '操作人',
            dataIndex: 'createUser',
            key: 'createUser',
            render: (text, record) => {
                return record.batchTask && record.batchTask.createUser 
                && record.batchTask.createUser.userName
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            render: (text, record) => {
                return (
                    <a>杀死所有实例</a>
                )
            }
        }]
    }

    render() {

        const {
            tasks, startTime, endTime, whichTask, current,
            owner, bussinessDate, runningDate, taskName,
        } = this.state

        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (
                <Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []

        const showTime = bussinessDate ? 'block' : 'none';

        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current,
        };

        const title =(
            <Form 
                layout="inline"
                style={{marginTop: '10px'}}
                className="m-form-inline" 
            >
                <FormItem>
                    <Search
                        placeholder="任务名称搜索"
                        style={{ width: '120px' }}
                        value={taskName}
                        size="default"
                        onChange={this.onChangeTaskName}
                        onSearch={this.searchJob}
                    />
                </FormItem>
                <FormItem label="业务日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="业务日期"
                        style={{ width: '120px' }}
                        value={bussinessDate}
                        size="default"
                        onChange={this.onBuisTimeChange}
                    />
                </FormItem>
                <FormItem label="运行日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="运行日期"
                        style={{ width: '120px' }}
                        size="default"
                        value={runningDate}
                        onChange={this.onRunningTime}
                    />
                </FormItem>
                <FormItem label="责任人">
                    <Select
                        allowClear
                        showSearch
                        style={{ width: '120px' }}
                        placeholder="责任人"
                        optionFilterProp="name"
                        value={owner}
                        onChange={this.onOwnerChange}
                    >
                        {userItems}
                    </Select>
                </FormItem>
                <FormItem>
                    <Checkbox.Group onChange={this.onCheckChange}>
                        <Checkbox value="person">我的任务</Checkbox>
                        <Checkbox value="todayUpdate">我今天补的</Checkbox>
                    </Checkbox.Group>
                </FormItem>
            </Form>
        )

        return (
            <div className="box-1 m-card">
                <Card
                    title={title}
                    noHovering
                    bordered={false}
                    loading={false}
                >
                    <Table 
                        columns={ this.initTaskColumns() }
                        className="m-table"
                        style={{ marginTop: 1}}
                        pagination={ pagination }
                        dataSource={tasks.data || []}
                        onChange={ this.pageChange }
                    />
                </Card>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
        user: state.user,
        projectUsers: state.projectUsers,
    }
})(PatchDataList)
