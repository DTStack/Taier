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
import GoBack from 'main/components/go-back'

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

import TaskFlowView from './taskFlowView/index'

const Option = Select.Option
const confirm = Modal.confirm
const warning = Modal.warning
const Search = Input.Search
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker


class PatchDataDetail extends Component {

    state = {
        loading: false,
        current: 1,
        selectedRowKeys: [],
        
        dutyUserId: '',
        fillJobName: '',
        jobStatuses: '',
        bizDay: '',
        
        table: {
            data: [],
        },
        statistics: '',

        visibleSlidePane: false,
        selectedTask: {},
    }

    componentDidMount() {
        this.setState({
            fillJobName: this.props.params.fillJobName
        }, this.search)
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
            fillJobName, dutyUserId, jobStatuses,
            bizDay, current,
        } = this.state;
        const reqParams = {
            currentPage: current,
            pageSize: 20,
        }
        if (fillJobName) {
            reqParams.fillJobName = fillJobName
        }
        if (dutyUserId !== '') {
            reqParams.dutyUserId = dutyUserId
        }
        if (bizDay) {
            reqParams.bizDay = moment(bizDay).unix()
        }
        if (jobStatuses && jobStatuses.length > 0) {
            reqParams.jobStatuses = jobStatuses.join(',')
        }
        this.loadPatchRecords(reqParams)
    }

    loadPatchRecords(params) {
        const ctx = this
        this.setState({ loading: true })
        Api.getFillDataDetail(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ table: res.data })
            }
            this.setState({ loading: false })
        })
        this.loadJobStatics(params)
    }
    
    loadJobStatics(params) {
        const ctx = this
        // type:  NORMAL_SCHEDULE(0), FILL_DATA(1);
        params.type = 1;
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
        if (filters.status) {
            status = filters.status
        }
        this.setState({ 
            current: pagination.current, 
            jobStatuses: status,
        }, () => {
            this.search()
        })
    }

    changeTaskName = (e) => {
        this.setState({ fillJobName: e.target.value })
    }

    changePerson = (target) => {
        this.setState({ dutyUserId: target, current: 1 }, () => {
            this.search()
        })
    }

    onSelectChange = (selectedRowKeys) => {
        this.setState({ selectedRowKeys });
    }

    changeBussinessDate = (value) => {
        this.setState({ bizDay: value, current: 1 }, () => {
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
        if (e.target.checked) {
            const selectedRowKeys = this.state.table.data.map(item => item.id)
            this.setState({
                selectedRowKeys
            })
        } else {
            this.setState({
                selectedRowKeys: []
            })
        }
    }

    initTaskColumns = () => {
        return [{
            title: '业务日期',
            dataIndex: 'bizDay',
            width: 100,
            key: 'bizDay'
        }, {
            title: '实例名称',
            dataIndex: 'jobName',
            key: 'jobName',
            width: 120,
            render: (text, record) => {
                return (
                    <a onClick={() => { this.showTask(record) }}>{ text }</a>
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
            title: '定时时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
        }, {
            title: '开始时间',
            dataIndex: 'exeStartTime',
            key: 'exeStartTime',
        }, {
            title: '运行时长（分钟）',
            dataIndex: 'exeTime',
            key: 'exeTime',
        }, {
            title: '责任人',
            width: 80,
            dataIndex: 'createUser',
            key: 'createUser',
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
            table, selectedRowKeys, fillJobName,
            bizDay, current, statistics,
            selectedTask, visibleSlidePane,
        } = this.state

        const { 
            projectUsers, project, goToTaskDev,
         } = this.props
        
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []

        const pagination = {
            total: table.totalCount,
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
                            <div>
                                <span
                                    style={{
                                        display: 'inline-block',
                                        paddingLeft: '8px',
                                        float: 'left'
                                    }}
                                >
                                    <GoBack type="left-circle-o" style={{
                                        fontSize: '18px',
                                        color: '9EABB2'
                                    }}/> 
                                    <span style={{
                                        fontSize: '14px',
                                        color: '#333333',
                                        marginLeft: '5px'
                                    }}>
                                        { fillJobName }
                                    </span>
                                </span>
                                <Form 
                                    layout="inline"
                                    style={{
                                        marginTop: '10px',
                                        marginLeft: '20px',
                                        display: 'inline-block',
                                    }}
                                    className="m-form-inline" 
                                >
                                    <FormItem
                                        label="责任人"
                                    >
                                        <Select
                                            allowClear
                                            showSearch
                                            style={{ width: 120 }}
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
                                            style={{ width: 120 }}
                                            format="YYYY-MM-DD"
                                            placeholder="业务日期"
                                            value={bizDay}
                                            onChange={this.changeBussinessDate}
                                        />
                                    </FormItem>
                                </Form>
                            </div>
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
                            dataSource={(table.data && table.data.recordList) || []}
                            onChange={this.handleTableChange}
                            footer={this.tableFooter}
                        />
                        <SlidePane 
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={ this.closeSlidePane }
                            visible={ visibleSlidePane } 
                            style={{ right: '0px', width: '80%', height: '600px' }}
                        >
                            <TaskFlowView 
                                visibleSlidePane={visibleSlidePane}
                                goToTaskDev={goToTaskDev} 
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
})(PatchDataDetail)
