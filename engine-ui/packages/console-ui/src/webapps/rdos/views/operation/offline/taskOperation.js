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
    InputNumber, Form,
 } from 'antd'

import utils from 'utils'
 
import Api from '../../../api'
import { 
    offlineTaskStatusFilter, jobTypes, 
    ScheduleTypeFilter, TASK_STATUS
} from '../../../comm/const'

import { 
    OfflineTaskStatus, TaskTimeType, TaskType, 
} from '../../../components/status'

import { Circle } from '../../../components/circle' 

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

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
        jobName: '',
        taskStatus: '',
        bussinessDate: moment().subtract(1, 'days'),
        selectedRowKeys: [],
        execTime: '', // 执行时间
        jobType: '', // 调度类型
        statistics: '',
        execSpendTime: '', // 执行时长
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
            pageSize: 10,
            hasAttach: true, // 列表模式
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
        Api.queryJobStatics(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    killAllJobs = () => {
        const ctx = this
        Api.batchStopJob({ isAll: 1 }).then((res) => {
            if (res.code === 1) {
                message.success('已经成功启动杀死所有任务！')
                ctx.search()
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
                            ctx.setState({ selectedRowKeys: [] })
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
                            ctx.setState({ selectedRowKeys: [] })
                            ctx.search()
                        }
                    })
                },
            });
        } else {
            warning({
                title: '提示',
                content: `
                    只有“未运行、成功、失败”状态下的任务可以进行重跑操作，
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
                    res.status !== TASK_STATUS.SUBMIT_FAILED
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
        console.log('onExecSpendTime:', execSpendTime)
        this.setState({ execSpendTime, current: 1 }, () => {
            this.search()
        })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > new Date().getTime();
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'id',
            key: 'id',
            render: (text, record) => {
                return (
                    <article style={{fontSize: '12px'}}>
                        <div>
                            <TaskTimeType value={record.taskPeriodId} />
                            <a onClick={() => { this.props.goToTaskDev(record.batchTask.id) }}>
                                {record.batchTask && record.batchTask.name}
                            </a>
                            <span>({
                                record.batchTask && record.batchTask.createUser 
                                && record.batchTask.createUser.userName
                            })</span>&nbsp;
                            <i className="i">
                                <TaskType value={record.batchTask && record.batchTask.taskType} />
                            </i>&nbsp;
                        </div>
                        <div style={{marginTop: '5px'}}>
                            业务日期：{record.businessDate}&nbsp;
                            定时时间：{record.cycTime}&nbsp;
                            创建时间：{record.gmtCreate ? utils.formatDateTime(record.gmtCreate) : ''}
                        </div>
                    </article>
                )
            },
        }, {
            title: '调度类型',
            dataIndex: 'type',
            key: 'type',
            render: (text) => {
                return text === 1 ? '补数据' : '周期调度'
            },
            filters: ScheduleTypeFilter,
        }, {
            title: '开始时间',
            dataIndex: 'execStartDate',
            key: 'execStartDate',
        }, {
            title: '结束时间',
            dataIndex: 'execEndDate',
            key: 'execEndDate',
        }, {
            title: '运行时长',
            dataIndex: 'execTime',
            key: 'execTime',
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
                return <OfflineTaskStatus value={text} />
            },
            filters: offlineTaskStatusFilter,
            filterMultiple: true,
        }, {
            title: '操作',
            key: 'operation',
            render: (text, record) => {
                let url = `/operation/task-flow?jobId=${record.id}&time=${record.businessDate}&tname=${record.batchTask && record.batchTask.name}`
                if (record.type === 1) { // 如果是补数据类型
                    url = `/operation/task-patch-data/?patchName=${record.batchTask && record.batchTask.name}&patchBizTime=${record.businessDate}`
                }
                const menu = (
                    <Menu onClick={this.clickMenu}>
                        <Menu.Item key="flow">
                            <Link to={url}>
                                流程
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="log">
                            <Link to={`/operation/task-log/${record.jobId}`}>日志</Link>
                        </Menu.Item>
                    </Menu>
                );
                return (
                    <Dropdown overlay={menu} trigger={['click']}>
                        <Button style={{ marginLeft: 8 }}>
                            操作 <Icon type="down" />
                        </Button>
                    </Dropdown>
                )
            },
        }]
    }

    render() {
        const { tasks, selectedRowKeys, bussinessDate, current, statistics } = this.state
        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={item.userId} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []
        const jobTypeItems = jobTypes && jobTypes.length > 0 ?
        jobTypes.map((item) => {
            return (<Option key={item.value} value={item.value} name={item.text}>
                {item.text}
            </Option>)
        }) : []
        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 10,
            current,
        };
        // rowSelection object indicates the need for row selection
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };
        return (
            <div className="operation-content">
                <article className="section">
                    <table className="bd my-table">
                        <tr>
                            <td colSpan={2}>
                                <Form layout="inline">
                                    <FormItem
                                        label="业务日期"
                                    >
                                        <DatePicker
                                            style={{ width: 100 }}
                                            format="YYYY-MM-DD"
                                            placeholder="业务日期"
                                            value={bussinessDate}
                                            onChange={this.changeBussinessDate}
                                        />
                                    </FormItem>
                                    <FormItem
                                        label=""
                                    >
                                        <Radio.Group value={this.state.choose} onChange={this.onTimeChange}>
                                            <Radio.Button value="0">昨日</Radio.Button>
                                            <Radio.Button value="1">前天</Radio.Button>
                                            <Radio.Button value="2">全部</Radio.Button>
                                        </Radio.Group>
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
                                        label=""
                                    >
                                        <Search
                                            placeholder="按任务名称搜索"
                                            style={{ width: 150 }}
                                            onChange={this.changeTaskName}
                                            onSearch={this.search}
                                        />
                                    </FormItem>
                                </Form>
                            </td>
                        </tr>
                        <tr className="bd-top">
                            <td className="tb-title bd-right">执行时间:</td>
                            <td>
                                <Form layout="inline">
                                    <FormItem
                                        label=""
                                    >
                                        <RangePicker
                                            format="YYYY-MM-DD"
                                            disabledDate={this.disabledDate}
                                            onChange={this.onExecTimeChange}
                                        />
                                    </FormItem>
                                    <FormItem
                                        label="耗时大于"
                                    >
                                        <InputNumber 
                                            min={0} 
                                            onChange={debounce(
                                                this.onExecSpendTime, 500, { 'maxWait': 2000 })}/
                                        > 秒
                                    </FormItem>
                                </Form>
                            </td>
                        </tr>
                    </table>
                    <div style={{ marginTop: '20px' }}>
                        <Button type="primary" onClick={this.killAllJobs}>杀死全部任务</Button>&nbsp;
                        <Button type="primary" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                        <Button type="primary" onClick={this.batchReloadJobs}>批量重跑</Button>&nbsp;
                    </div>
                    <div style={{ paddingBottom: '15px', marginTop: '15px', lineHeight: '28px' }}>
                        <span className="ope-statistics">
                            <span style={{color: "#43576a"}}>
                                <Circle style={{ background: '#43576a' }} />&nbsp;
                                任务实例总数: {statistics.ALL || 0}
                            </span>&nbsp;
                            <span style={{color: "#00a0e9"}}>
                                <Circle style={{ background: '#00a0e9' }} />&nbsp;
                                等待提交: {statistics.UNSUBMIT || 0}
                            </span>&nbsp;
                            <span style={{color: "#ec6941"}}>
                                <Circle style={{ background: '#ec6941' }} />&nbsp;
                                提交中: {statistics.SUBMITTING || 0}
                            </span>&nbsp;
                            <span style={{color: "#009944"}}>
                                <Circle style={{ background: '#009944' }} />&nbsp;
                                等待运行: {statistics.WAITENGINE || 0}
                            </span>&nbsp;
                            <span style={{color: "#00a0e9"}}>
                                <Circle style={{ background: '#00a0e9' }} />&nbsp;
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
                        <Tooltip placement="bottom" title="图形模式">
                            <Link className="right" to="/operation/task-flow">
                                <Button icon="pie-chart">图形模式</Button>
                            </Link>
                        </Tooltip>
                    </div>
                    <Table
                      rowKey="id"
                      className="section-border "
                      rowSelection={rowSelection}
                      pagination={pagination}
                      loading={this.state.loading}
                      columns={this.initTaskColumns()}
                      dataSource={tasks.data || []}
                      onChange={this.handleTableChange}
                    />
                </article>
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
