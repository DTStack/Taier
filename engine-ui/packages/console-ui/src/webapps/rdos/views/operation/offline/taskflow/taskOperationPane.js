import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Row, Input, Select, Checkbox,
    Col, Radio, Pagination, Form,
    DatePicker, Menu, TimePicker,
} from 'antd'

import utils from 'utils'
import Api from '../../../../api'
import { TaskBadgeStatus } from '../../../../components/status'
import NoData from '../../../../components/no-data'
import { offlineTaskStatusFilter } from '../../../../comm/const'
import * as FlowAction from '../../../../store/modules/operation/taskflow'

const Search = Input.Search
const RadioGroup = Radio.Group
const Option = Select.Option
const FormItem = Form.Item

class TaskOperation extends Component {

    state = {
        loading: false,
        chooseTime: utils.getParameterByName('time') ? '' : 0,
        current: 1,
        tasks: {
            data: [],
        },
        whichTask: [0],
        person: undefined,
        taskName: utils.getParameterByName('tname') 
        ? utils.getParameterByName('tname') : '',
        taskStatus: [],
        startTime: moment().set({
            'hour': 0,
            'minute': 0,
            'second': 0,
        }),
        endTime: moment().set({
            'hour': 18,
            'minute': 0,
            'second': 0,
        }),
        bussinessDate: utils.getParameterByName('time') 
        ? moment(utils.getParameterByName('time')) : moment().subtract(1, 'days'),
        selectedTask: utils.getParameterByName('jobId') ? parseInt(utils.getParameterByName('jobId'), 10) : '',
    }

    componentDidMount() {
        this.loadTaskList()
    }

    componentWillReceiveProps(nextProps) {
        const { project, graphStatus } = nextProps
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.dispatch(FlowAction.setTaskFlow({ id: 0 }))
            this.setState({ current: 1 }, () => {
                this.loadTaskList()
            })
        }
        // console.log('componentWillReceiveProps:', graphStatus)
        // if (graphStatus && graphStatus === 'change') {
        //     this.loadTaskList()
        // }
    }

    searchTask = (query) => {
        const params = {}
        if (query) {
            const taskName = utils.trim(query)
            params.taskName = taskName
        }
        this.loadTaskList(params)
    }

    getReqParams = () => {
        const {
            person,
            taskName,
            bussinessDate,
            startTime,
            endTime,
            taskStatus,
        } = this.state
        const reqParams = { 
            currentPage: 1,
            pageSize: 10, 
            type: 0,
            hasAttach: false,
        }
        if (taskName) {
            reqParams.taskName = taskName
        }
        if (person) {
            reqParams.ownerId = person
        }
        if (taskStatus.length > 0) {
            reqParams.jobStatuses = taskStatus.join(',')
        }
        if (bussinessDate) {
            reqParams.startTime = bussinessDate.set({
                'hour': startTime.get('hour'),
                'minute': startTime.get('minute'),
                'second': startTime.get('second'),
            }).unix()
            reqParams.endTime = bussinessDate.set({
                'hour': endTime.get('hour'),
                'minute': endTime.get('minute'),
                'second': endTime.get('second'),
            }).unix()
        }
        return reqParams
    }

    loadTaskList(params) { // currentPage, pageSize, status
        const ctx = this
        this.setState({ loading: true })
        let defaultParams = this.getReqParams()
        const reqParams = Object.assign(defaultParams, params)
        Api.queryJobs(reqParams).then((res) => {
            if (res.code === 1) {
                const resData = res.data
                if (resData && resData.data && resData.data.length > 0) {
                    const defaultTask = resData.data[0]
                    ctx.props.dispatch(FlowAction.setTaskFlow(defaultTask))
                    ctx.setState({ selectedTask: `${defaultTask.id}` })
                } else {
                    ctx.props.dispatch(FlowAction.setTaskFlow({ id: 0 }))
                }
                ctx.setState({ 
                    tasks: resData, 
                    loading: false, 
                })
            }
        })
    }

    pageChange = (page) => {
        const params = { currentPage: page }
        this.setState({ current: page })
        this.loadTaskList(params)
    }

    onChange = (value) => {
        const { user } = this.props
        const val = value && value.length > 0 ? [value[value.length -1]] : []
        const state = { whichTask: val, current: 1, person: `${user.id}` }
        if (val[0] === 2) { // 失败的任务
            state.taskStatus = [8] // 运行失败
        } else if (val[0] === 3) { // 未完成的任务
            state.taskStatus = [10, 4, 16] // 未提交，运行中, 等待运行
        } else {
            state.taskStatus = []
        }
        this.setState(state, () => {
            this.loadTaskList()
        });
    }

    onTimeChange = (e) => {
        const val = e.target.value
        const state = { chooseTime: val, current: 1 }
        if (val === 0) {
            state.bussinessDate = moment().subtract(1, 'days') 
        } else if (val === 1) {
            state.bussinessDate = moment().subtract(2, 'days') 
        } else {
            state.bussinessDate = ''
        }
        this.setState(state, () => {
            this.loadTaskList()
        })
    }

    onTaskStatus = (value) => {
        this.setState({ taskStatus: value, current: 1, }, () => {
            this.loadTaskList()
        })
    }

    changePerson = (target) => {
        const state = { person: target, current: 1, }
        if (target === 0 || target === undefined) {
            state.whichTask = 0
        }
        this.setState(state, () => {
            this.loadTaskList()
        })
    }

    bizTimeChange = (date) => {
        this.setState({ bussinessDate: date, chooseTime: '', current: 1, }, () => {
            this.loadTaskList()
        });
    }

    onStartTime = (date) => {
        this.setState({ startTime: date, current: 1, }, () => {
            this.loadTaskList()
        });
    }

    onEndTime = (date) => {
        this.setState({ endTime: date, current: 1, }, () => {
            this.loadTaskList()
        });
    }

    onChangeTaskName = (e) => {
        this.setState({ taskName: e.target.value })
    }

    chooseTask = (e) => {
        const task = e.item.props.task
        if (task) {
            this.setState({ selectedTask: `${task.id}` })
            this.props.dispatch(FlowAction.setTaskFlow(task))
        }
    }

    getTaskItems = (tasks) => {
        if (tasks && tasks.length > 0) {
            return tasks.map(item => {
                return (
                    <Menu.Item
                        task={item}
                        key={item.id}
                        className="bd-bottom">
                        <Row style={{ position: 'relative' }}>
                            <Col span="2" style={{ position: 'absolute', top: '16px', left: '5px', lineHeight: '0' }}>
                                <TaskBadgeStatus value={item.status}/>
                            </Col>
                            <Col span="22" style={{ paddingLeft: '30px', lineHeight: '20px' }}>
                                <p>{item.batchTask ? item.batchTask.name : ''}</p>
                                <p>{item.businessDate}</p>
                            </Col>
                        </Row>
                    </Menu.Item>
                )
            })
        }
        return <NoData style={{ fontSize: '12px' }}/>
    }

    render() {
        const {
            tasks, bussinessDate, chooseTime,
            taskName, whichTask, startTime, 
            endTime, taskStatus, selectedTask, person,
        } = this.state
        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
                return (<Option key={item.id} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []
        const statusFilter = offlineTaskStatusFilter && offlineTaskStatusFilter.length > 0 ?
        offlineTaskStatusFilter.map((item) => {
            return (<Option key={item.id} value={item.value} name={item.text}>
                {item.text}
            </Option>)
        }) : []
        const taskItems = this.getTaskItems(tasks.data)
        const showTime = chooseTime !== 2 ? 'inline-block' : 'none'
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
                                onSearch={this.searchTask}
                            />
                        </FormItem>
                        <FormItem label="业务日期">
                            <Radio.Group
                                size="small"
                                value={chooseTime}
                                onChange={this.onTimeChange}
                            >
                                <Radio.Button value={0}>昨日</Radio.Button>
                                <Radio.Button value={1}>前天</Radio.Button>
                                <Radio.Button value={2}>全部</Radio.Button>
                            </Radio.Group>
                        </FormItem>
                        <FormItem style={{marginRight: '5px'}}>
                            <DatePicker
                                format="YYYY-MM-DD"
                                placeholder="业务日期"
                                style={{ width: '100px' }}
                                value={bussinessDate}
                                onChange={this.bizTimeChange}
                            />
                        </FormItem>
                        <FormItem style={{ display: showTime, marginRight: '0px' }}>
                            <TimePicker
                                style={{ width: '80px' }}
                                value={startTime}
                                format="HH:mm"
                                onChange={this.onStartTime}
                                placeholder="开始"
                            />~
                            <TimePicker
                                style={{ width: '80px' }}
                                value={endTime}
                                format="HH:mm"
                                onChange={this.onEndTime}
                                placeholder="截止"
                            />
                        </FormItem>
                        <FormItem>
                            <Checkbox.Group value={whichTask} onChange={this.onChange}>
                                <Checkbox value={1}>我的任务</Checkbox>
                                <Checkbox value={2}>我的出错任务</Checkbox>
                                <Checkbox value={3}>我的未完成任务</Checkbox>
                            </Checkbox.Group>
                        </FormItem>
                        <FormItem
                            label="责任人"
                        >
                            <Select
                                allowClear
                                showSearch
                                style={{ width: '200px' }}
                                placeholder="责任人"
                                value={person}
                                optionFilterProp="name"
                                onChange={this.changePerson}
                                >
                                    {userItems}
                            </Select>
                        </FormItem>
                        <FormItem
                            label="&nbsp;&nbsp;&nbsp;&nbsp;状态"
                        >
                           <Select
                                allowClear
                                style={{ width: '200px' }}
                                placeholder="选择状态"
                                value={taskStatus}
                                onChange={this.onTaskStatus}
                                mode="multiple"
                            >
                                {statusFilter}
                            </Select>
                        </FormItem>
                    </Form>
                </div>
                <Row className="task-list">
                    <Menu
                        onClick={this.chooseTask}
                        style={{ width: '100%', borderRight: '0' }}
                        selectedKeys={[selectedTask]}
                        mode="inline"
                    >
                        {taskItems}
                    </Menu>
                    <div className="txt-right" style={{ padding: '10px' }}>
                        <Pagination
                            simple
                            defaultCurrent={1}
                            current={this.state.current}
                            total={tasks.totalCount}
                            onChange={this.pageChange}
                        />
                    </div>
                </Row>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
        user: state.user,
        projectUsers: state.projectUsers,
        graphStatus: state.operation.graphStatus,
    }
})(TaskOperation)
