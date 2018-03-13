import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import moment from 'moment'
import { debounce } from 'lodash';

import {
    Table, message, Modal,
    Row, Col, Card, Input,
    Radio, Button, Select,
    Dropdown, Menu, Icon,
    DatePicker, Tooltip,
    InputNumber, Form, Checkbox,
 } from 'antd'

import utils from 'utils'
import SlidePane from 'widgets/slidePane'
import { Circle } from 'widgets/circle' 

import Api from '../../../api'
import { 
    offlineTaskStatusFilter, jobTypes, 
    ScheduleTypeFilter, TASK_STATUS
} from '../../../comm/const'

import { 
    OfflineTaskStatus, TaskTimeType, TaskType, 
} from '../../../components/status'


import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

import TaskFlowView from './taskFlowView'

const Option = Select.Option
const confirm = Modal.confirm
const warning = Modal.warning
const Search = Input.Search
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker

class OfflineTaskList extends Component {

    state = {
        tasks: {
            data: [],
        },
        loading: false,
        continue: false,
        current: 1,
        person: '',
        choose: '0',
        jobName: utils.getParameterByName('job') ? utils.getParameterByName('job') : '',
        taskStatus: '',
        bussinessDate: '',
        selectedRowKeys: [],
        checkAll: false,
        execTime: '', // 执行时间
        jobType: '', // 调度类型
        statistics: '',
        execSpendTime: '', // 执行时长
        visibleSlidePane: false,
        selectedTask: '',
    }

    componentDidMount() {
        this.search()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.setState({ current: 1 }, () => {
                this.search()
            })
        }
    }

    search = () => {
        const {
            jobName, person, taskStatus,
            bussinessDate, jobType, current,
            execTime, execSpendTime,
        } = this.state
        const reqParams = {
            currentPage: current,
        }
        if (jobName) {
            reqParams.taskName = jobName
        }
        if (person) {
            reqParams.ownerId = person
        }
        if (bussinessDate) {
            reqParams.startTime = bussinessDate.set({
                'hour': 0,
                'minute': 0,
                'second': 0,
            }).unix()
            reqParams.endTime = bussinessDate.set({
                'hour': 23,
                'minute': 59,
                'second': 59,
            }).unix()
        }
        if (execTime.length > 0) {
            reqParams.execStartTime = execTime[0].unix()
            reqParams.execEndTime = execTime[1].unix()
        }
        if (execSpendTime) {// 执行时长
            reqParams.execTime = execSpendTime
        }
        if (jobType !== undefined && jobType !== '') {
            reqParams.type = jobType
        }
        if (taskStatus && taskStatus.length > 0) {
            reqParams.jobStatuses = taskStatus.join(',')
        }
        this.loadTaskList(reqParams)
    }

    loadTaskList(params) { // currentPage, pageSize, isTimeSortDesc, status
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            currentPage: 1,
            pageSize: 20,
            type: 0,
        }, params)
        Api.queryJobs(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data, loading: false })
            }
        })
        this.loadJobStatics(params)
    }

    loadJobStatics(params) {
        const ctx = this
        // type:  NORMAL_SCHEDULE(0), FILL_DATA(1);
        params.type = 0;
        Api.queryJobStatics(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    batchKillJobs = () => { // 批量重跑
        const ctx = this
        const selected = this.state.selectedRowKeys

        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要杀死的任务！',
            })
            return
        }
        if (this.canKill(selected)) {
            confirm({
                title: '确认提示',
                content: '确定要杀死选择的任务？',
                onOk() {
                    Api.batchStopJob({ jobIdList: selected }).then((res) => {
                        if (res.code === 1) {
                            ctx.setState({ selectedRowKeys: [], checkAll: false  })
                            message.success('已经成功杀死所选任务！')
                            ctx.search()
                        }
                    })
                },
            });
        } else {
            warning({
                title: '提示',
                content: `
                    除去“失败”、“停止”、“完成”状态以外的任务才可以进行杀死操作，
                    请您重新选择!
                `,
            })
        }
    }

    batchReloadJobs = () => { // 批量重跑
        const ctx = this
        const selected = this.state.selectedRowKeys
        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要重跑的任务！',
            })
            return
        }
        if (this.canReload(selected)) {
            confirm({
                title: '确认提示',
                content: '确认需要重跑选择的任务？',
                onOk() {
                    Api.batchRestartAndResume({jobIdList: selected}).then((res) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑所选任务！')
                            ctx.setState({ selectedRowKeys: [], checkAll: false  })
                            ctx.search()
                        }
                    })
                },
            });
        } else {
            warning({
                title: '提示',
                content: `
                    只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                    请您重新选择!
                `,
            })
        }
    }

    canReload = (ids) => { // 未运行、成功、失败的任务可以reload
        const tasks = this.state.tasks.data
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const res = tasks.find(task => task.id === id)
                if (
                    res &&
                    res.status !== TASK_STATUS.WAIT_SUBMIT &&
                    res.status !== TASK_STATUS.FINISHED && 
                    res.status !== TASK_STATUS.RUN_FAILED &&
                    res.status !== TASK_STATUS.SUBMIT_FAILED && 
                    res.status !== TASK_STATUS.STOPED
                ) return false
            }
            return true
        }
    }

    canKill = (ids) => { // 是否可以进行kill
        const tasks = this.state.tasks.data
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const res = tasks.find(task => task.id === id)
                if (res && (
                    res.status === TASK_STATUS.SUBMIT_FAILED || 
                    res.status === TASK_STATUS.STOPED || 
                    res.status === TASK_STATUS.FINISHED
                )) return false
            }
            return true
        }
    }

    handleTableChange = (pagination, filters) => {
        let status;
        let jobType;
        if (filters.status) {
            status = filters.status
        }
        if (filters.type) {
            jobType = filters.type[0]
        }
        this.setState({ 
            current: pagination.current, 
            taskStatus: status,
            jobType,
            selectedRowKeys: [],
            checkAll: false,
        }, () => {
            this.search()
        })
    }

    changeTaskName = (e) => {
        this.setState({ jobName: e.target.value })
    }

    changePerson = (target) => {
        this.setState({ person: target, current: 1 }, () => {
            this.search()
        })
    }

    onSelectChange = (selectedRowKeys) => {
        this.setState({ selectedRowKeys });
    }

    onJobTypeChange = (value) => {
        this.setState({ jobType: value, current: 1 }, () => {
            this.search()
        });
    }

    changeBussinessDate = (value) => {
        const yesterday = moment().subtract(1, 'days').format('YYYY-MM-DD')
        const beforeDay = moment().subtract(2, 'days').format('YYYY-MM-DD')
        const selected = value ? value.format('YYYY-MM-DD') : ''
        let choose = '';
        if (selected === yesterday) {
            choose = '0'
        } else if (selected === beforeDay) {
            choose = '1'
        }
        this.setState({ choose: choose, bussinessDate: value, current: 1 }, () => {
            this.search()
        })
    }

    onTimeChange = (e) => {
        const val = e.target.value
        if (val === '0') {
            this.setState({ bussinessDate: moment().subtract(1, 'days') })
        } else if (val === '1') {
            this.setState({ bussinessDate: moment().subtract(2, 'days') })
        } else {
            this.setState({ bussinessDate: '' })
        }
        this.setState({ choose: val, current: 1 }, () => {
            this.search()
        });
    }

    onExecTimeChange = (dates) => {
        this.setState({ execTime: dates, current: 1 }, () => {
            this.search()
        })
    }

    onExecSpendTime = (execSpendTime) => {
        this.setState({ execSpendTime, current: 1 }, () => {
            this.search()
        })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > new Date().getTime();
    }

    showTask = (task) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        })
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []

        if (e.target.checked) {
            selectedRowKeys = this.state.tasks.data.map(item => item.id)
        }

        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'id',
            key: 'id',
            width: 100,
            render: (text, record) => {
                return (
                    <a onClick={() => { this.showTask(record) }}>{
                        record.batchTask && record.batchTask.name
                    }</a>
                )
            },
        }, {
            title: '状态',
            width: 80,
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
                return <OfflineTaskStatus value={text} />
            },
            filters: offlineTaskStatusFilter,
            filterMultiple: true,
        }, {
            title: '任务类型',
            width: 80,
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text, record) => {
                return  <TaskType value={record.batchTask && record.batchTask.taskType} />
            },
        }, {
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text) => {
                return <TaskTimeType value={text} />
            },
        }, {
            title: '业务日期',
            dataIndex: 'businessDate',
            key: 'businessDate'
        }, {
            title: '定时时间',
            dataIndex: 'cycTime',
            key: 'cycTime'
        }, {
            title: '开始时间',
            dataIndex: 'execStartDate',
            key: 'execStartDate',
        }, {
            title: '运行时长',
            dataIndex: 'execTime',
            key: 'execTime',
        }, {
            title: '责任人',
            width: 100,
            dataIndex: 'createUser',
            key: 'createUser',
            render: (text, record) => {
                return record.batchTask && record.batchTask.createUser 
                && record.batchTask.createUser.userName
            }
        }]
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
        })
    }

    tableFooter = (currentPageData) => {
        return (
            <tr className="ant-table-row  ant-table-row-level-0">
                <td style={{ padding: '15px 10px 10px 30px' }}>
                    <Checkbox
                        checked={ this.state.checkAll }
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </td>
                <td>
                    <Button type="primary" size="small" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                    <Button type="primary" size="small" onClick={this.batchReloadJobs}>批量重跑</Button>&nbsp;
                </td>
            </tr>
        )
    }

    render() {
        const { 
            tasks, selectedRowKeys, jobName,
            bussinessDate, current, statistics,
            selectedTask, visibleSlidePane,
        } = this.state

        const { projectUsers, project } = this.props
        
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []

        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current,
        };

        // rowSelection object indicates the need for row selection
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };

        return (
            <div>
                <h1 className="box-title" style={{lineHeight: '50px'}}>
                <div style={{ marginTop: '5px' }}>
                    <span className="ope-statistics">
                        <span style={{color: "#2E3943"}}>
                            <Circle style={{ background: '#2E3943' }} />&nbsp;
                            任务实例总数: {statistics.ALL || 0}
                        </span>&nbsp;
                        <span style={{color: "#F5A623"}}>
                            <Circle style={{ background: '#F5A623 ' }} />&nbsp;
                            等待提交: {statistics.UNSUBMIT || 0}
                        </span>&nbsp;
                        <span style={{color: "#2491F7"}}>
                            <Circle style={{ background: '#2491F7' }} />&nbsp;
                            提交中: {statistics.SUBMITTING || 0}
                        </span>&nbsp;
                        <span style={{color: "#F5A623"}}>
                            <Circle style={{ background: '#F5A623' }} />&nbsp;
                            等待运行: {statistics.WAITENGINE || 0}
                        </span>&nbsp;
                        <span style={{color: "#2491F7"}}>
                            <Circle style={{ background: '#2491F7' }} />&nbsp;
                            运行中: {statistics.RUNNING || 0}
                        </span>&nbsp;
                        <span style={{color: "#009944"}}>
                            <Circle style={{ background: '#009944' }} />&nbsp;
                            完成: {statistics.FINISHED || 0}
                        </span>&nbsp;
                        <span style={{color: "#d62119"}}>
                            <Circle style={{ background: '#d62119' }} />&nbsp;
                            失败: {statistics.FAILED || 0}
                        </span>&nbsp;
                    </span>
                    </div>
                </h1>
                <div className="box-2 m-card">
                    <Card 
                        noHovering
                        bordered={false}
                        loading={false}
                        title={
                            <Form 
                                layout="inline"
                                style={{marginTop: '10px'}}
                                className="m-form-inline" 
                            >
                                <FormItem label="">
                                    <Search
                                        placeholder="按任务名称搜索"
                                        style={{ width: 150 }}
                                        size="default"
                                        value={jobName}
                                        onChange={this.changeTaskName}
                                        onSearch={this.search}
                                    />
                                </FormItem>
                                <FormItem
                                    label="责任人"
                                >
                                    <Select
                                        allowClear
                                        showSearch
                                        style={{ width: 150 }}
                                        placeholder="责任人"
                                        optionFilterProp="name"
                                        onChange={this.changePerson}
                                    >
                                        {userItems}
                                    </Select>
                                </FormItem>
                                <FormItem
                                    label="业务日期"
                                >
                                    <DatePicker
                                        size="default"
                                        style={{ width: 150 }}
                                        format="YYYY-MM-DD"
                                        placeholder="业务日期"
                                        value={bussinessDate}
                                        onChange={this.changeBussinessDate}
                                    />
                                </FormItem>
                                <FormItem
                                    label="执行时间"
                                >
                                    <RangePicker
                                        style={{ width: 180 }}
                                        size="default"
                                        format="YYYY-MM-DD"
                                        disabledDate={this.disabledDate}
                                        onChange={this.onExecTimeChange}
                                    />
                                </FormItem>
                            </Form>
                        }
                        extra={
                            <Icon type="reload" onClick={this.search} 
                                style={{
                                    cursor: 'pointer',
                                    marginTop: '16px',
                                    color: '#94A8C6'
                                }}
                            />
                        }
                    > 
                         <Table
                            rowKey="id"
                            style={{marginTop: '1px'}}
                            className="m-table"
                            rowSelection={rowSelection}
                            pagination={pagination}
                            loading={this.state.loading}
                            columns={this.initTaskColumns()}
                            dataSource={tasks.data || []}
                            onChange={this.handleTableChange}
                            footer={this.tableFooter}
                            scroll={{ y: '65%' }}
                        />
                        <SlidePane 
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={ this.closeSlidePane }
                            visible={ visibleSlidePane } 
                            style={{ right: '0px', width: '80%', height: '100%', minHeight: '600px' }}
                        >
                            <TaskFlowView 
                                visibleSlidePane={visibleSlidePane}
                                goToTaskDev={this.props.goToTaskDev} 
                                taskJob={selectedTask} 
                                project={project}
                            />
                        </SlidePane>
                    </Card>
                </div>
            </div>
        )
    }
}

export default connect((state) => {
    return {
        project: state.project,
        projectUsers: state.projectUsers,
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineTaskList)
