import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { isEmpty } from 'lodash';

import {
    Table, message, Modal,
    Card, Input, Button, Select,
    Icon, DatePicker, Tooltip,
    Form, Checkbox,
} from 'antd'

import utils from 'utils'
import SlidePane from 'widgets/slidePane'
import { Circle } from 'widgets/circle'

import Api from '../../../api'
import {
    offlineTaskStatusFilter, jobTypes,
    ScheduleTypeFilter, TASK_STATUS,
    offlineTaskTypeFilter,
    offlineTaskPeriodFilter,
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
const yesterDay = moment().subtract(1, 'days');

class OfflineTaskList extends Component {

    state = {
        tasks: {
            data: [],
        },
        loading: false,
        continue: false,
        current: 1,
        person: '',
        jobName: utils.getParameterByName('job') ? utils.getParameterByName('job') : '',
        taskStatus: isEmpty(utils.getParameterByName("status")) ? [] : [utils.getParameterByName("status")],
        bussinessDate: [new moment(yesterDay).subtract(utils.getParameterByName('date')||0, 'days'), yesterDay],
        cycDate: undefined,
        selectedRowKeys: [],
        checkAll: false,
        execTime: '', // 执行时间
        jobType: '', // 调度类型
        taskType: '',
        statistics: '',
        taskPeriodId: '',
        execTimeSort: '',
        execStartSort: '',
        execEndSort: '',
        bussinessDateSort: '',
        cycSort: '',
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
            bussinessDate, businessDateSort, jobType, current,
            execTime, taskType,
            taskPeriodId, execTimeSort,
            execStartSort, execEndSort,
            cycSort, cycDate
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
        if (bussinessDate && bussinessDate.length > 1) {
            reqParams.bizStartDay = bussinessDate[0].unix();
            reqParams.bizEndDay = bussinessDate[1].unix();
        }
        if (cycDate && cycDate.length > 1) {
            reqParams.cycStartDay = cycDate[0].unix();
            reqParams.cycEndDay = cycDate[1].unix();
        }

        if (jobType !== undefined && jobType !== '') {
            reqParams.type = jobType
        }
        if (taskStatus && taskStatus.length > 0) {
            reqParams.jobStatuses = taskStatus.join(',')
        }
        if (taskType) {
            reqParams.taskType = taskType.join(',')
        }
        if (taskPeriodId) {
            reqParams.taskPeriodId = taskPeriodId.join(',')
        }

        reqParams.execTimeSort = execTimeSort || undefined;
        reqParams.execStartSort = execStartSort || undefined;
        reqParams.execEndSort = execEndSort || undefined;
        reqParams.cycSort = cycSort || undefined;
        reqParams.businessDateSort = businessDateSort || undefined;

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
                ctx.setState({ tasks: res.data })
            }
            ctx.setState({
                loading: false
            })

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
                            ctx.setState({ selectedRowKeys: [], checkAll: false })
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
                    除去“失败”、“取消”、“完成”状态和“未删除”以外的任务才可以进行杀死操作，
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
                    Api.batchRestartAndResume({ jobIdList: selected }).then((res) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑所选任务！')
                            ctx.setState({ selectedRowKeys: [], checkAll: false })
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
                    res.status === TASK_STATUS.FINISHED ||
                    res.batchTask.isDeleted === 1
                )) return false
            }
            return true
        }
    }

    handleTableChange = (pagination, filters, sorter) => {

        const params = {
            current: pagination.current,
            taskStatus: filters.status,
            jobType: filters.type ? ilters.type[0] : '',
            selectedRowKeys: [],
            taskType: filters.taskType,
            taskPeriodId: filters.taskPeriodId,
            checkAll: false,
            execTimeSort: '',
            execStartSort: '',
            execEndSort: '',
            businessDateSort: '',
            cycSort: '',
        }

        if (sorter) {
            let { field, order } = sorter;

            switch (field) {
                case 'execTime': {
                    params.execTimeSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'execStartDate': {
                    params.execStartSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'execEndDate': {
                    params.execEndSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'cycTime': {
                    params.cycSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'businessDate': {
                    params.businessDateSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
            }
        }
        this.setState(params, () => {
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
        this.setState({ bussinessDate: value, current: 1 }, () => {
            this.search()
        })
    }

    changecycDate = (value) => {
        this.setState({ cycDate: value, current: 1 }, () => {
            this.search()
        })
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
            selectedRowKeys = this.state.tasks.data.map(item => { if (item.batchTask.isDeleted !== 1) { return item.id } })
        }

        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }

    initTaskColumns = () => {
        const { taskStatus } = this.state;
        return [{
            title: '任务名称',
            dataIndex: 'id',
            key: 'id',
            width: 120,
            render: (text, record) => {
                const name = record.batchTask && record.batchTask.name
                const showName = record.batchTask.isDeleted === 1 ?
                    `${name} (已删除)`
                    :
                    <a onClick={() => { this.showTask(record) }}>{name}</a>;
                return showName;
            },
        }, {
            title: '状态',
            width: 100,
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
                return <OfflineTaskStatus value={text} />
            },
            filters: offlineTaskStatusFilter,
            filterMultiple: true,
            filteredValue: taskStatus
        }, {
            title: '任务类型',
            width: 100,
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text, record) => {
                return <TaskType value={record.batchTask && record.batchTask.taskType} />
            },
            filters: offlineTaskTypeFilter,
        }, {
            width: 100,
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text) => {
                return <TaskTimeType value={text} />
            },
            filters: offlineTaskPeriodFilter,
        }, {
            width: 120,
            title: '业务日期',
            dataIndex: 'businessDate',
            key: 'businessDate',
            sorter: true,
        }, {
            width: 120,
            title: '计划时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
            sorter: true,
        }, {
            width: 120,
            title: '开始时间',
            dataIndex: 'execStartDate',
            key: 'execStartDate',
            sorter: true,
        }, {
            width: 120,
            title: '结束时间',
            dataIndex: 'execEndDate',
            key: 'execEndDate',
            sorter: true,
        }, {
            title: '运行时长',
            width: 100,
            dataIndex: 'execTime',
            key: 'execTime',
            sorter: true,
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
            selectedTask: null
        })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    tableFooter = (currentPageData) => {
        return (
            <div className="ant-table-row  ant-table-row-level-0">
                <div style={{ padding: '15px 10px 10px 30px', display: "inline-block" }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </div>
                <div style={{ display: "inline-block" }}>
                    <Button type="primary" size="small" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                    <Button type="primary" size="small" onClick={this.batchReloadJobs}>重跑当前及下游任务</Button>&nbsp;
                </div>
            </div>
        )
    }

    render() {
        const {
            tasks, selectedRowKeys, jobName,
            bussinessDate, current, statistics,
            selectedTask, visibleSlidePane, cycDate
        } = this.state

        const { projectUsers, project } = this.props

        const userItems = projectUsers && projectUsers.length > 0 ?
            projectUsers.map((item) => {
                return (<Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
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
            getCheckboxProps: record => ({
                disabled: record.batchTask && record.batchTask.isDeleted === 1
            })
        };

        return (
            <div>
                <h1 className="box-title" style={{ lineHeight: '50px' }}>
                    <div style={{ marginTop: '5px' }}>
                        <span className="ope-statistics">
                            <span style={{ color: "#2E3943" }}>
                                <Circle style={{ background: '#2E3943' }} />&nbsp;
                            任务实例总数: {statistics.ALL || 0}
                            </span>&nbsp;
                        <span style={{ color: "#F5A623" }}>
                                <Circle style={{ background: '#F5A623 ' }} />&nbsp;
                            等待提交: {statistics.UNSUBMIT || 0}
                            </span>&nbsp;
                        <span style={{ color: "#2491F7" }}>
                                <Circle style={{ background: '#2491F7' }} />&nbsp;
                            提交中: {statistics.SUBMITTING || 0}
                            </span>&nbsp;
                        <span style={{ color: "#F5A623" }}>
                                <Circle style={{ background: '#F5A623' }} />&nbsp;
                            等待运行: {statistics.WAITENGINE || 0}
                            </span>&nbsp;
                        <span style={{ color: "#2491F7" }}>
                                <Circle style={{ background: '#2491F7' }} />&nbsp;
                            运行中: {statistics.RUNNING || 0}
                            </span>&nbsp;
                        <span style={{ color: "#009944" }}>
                                <Circle style={{ background: '#009944' }} />&nbsp;
                            成功: {statistics.FINISHED || 0}
                            </span>&nbsp;
                        <span style={{ color: "#F5A623" }}>
                                <Circle style={{ background: '#F5A623 ' }} />&nbsp;
                            取消: {statistics.CANCELED || 0}
                            </span>&nbsp;
                        <span style={{ color: "#d62119" }}>
                                <Circle style={{ background: '#d62119' }} />&nbsp;
                            失败: {statistics.FAILED || 0}
                            </span>&nbsp;
                        <span style={{ color: "#26dad2" }}>
                                <Circle style={{ background: '#26dad2' }} />&nbsp;
                            冻结: {statistics.FROZEN || 0}
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
                                style={{ marginTop: '10px' }}
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
                                    <RangePicker
                                        size="default"
                                        style={{ width: 200 }}
                                        format="YYYY-MM-DD"
                                        disabledDate={this.disabledDate}
                                        value={bussinessDate || null}
                                        onChange={this.changeBussinessDate}
                                    />
                                </FormItem>
                                <FormItem
                                    label="计划日期"
                                >
                                    <RangePicker
                                        size="default"
                                        style={{ width: 200 }}
                                        format="YYYY-MM-DD"
                                        value={cycDate || null}
                                        onChange={this.changecycDate}
                                    />
                                </FormItem>
                            </Form>
                        }
                        extra={
                            <Tooltip title="刷新数据">
                                <Icon type="sync" onClick={this.search}
                                    style={{
                                        cursor: 'pointer',
                                        marginTop: '16px',
                                        color: '#94A8C6'
                                    }}
                                />
                            </Tooltip>
                        }
                    >
                        <Table
                            rowKey="id"
                            rowClassName={
                                (record, index) => {
                                    if (this.state.selectedTask && this.state.selectedTask.id == record.id) {
                                        return "row-select"
                                    } else {
                                        return "";
                                    }
                                }
                            }
                            style={{ marginTop: '1px' }}
                            className="m-table"
                            rowSelection={rowSelection}
                            pagination={pagination}
                            loading={this.state.loading}
                            columns={this.initTaskColumns()}
                            dataSource={tasks.data || []}
                            onChange={this.handleTableChange}
                            footer={this.tableFooter}
                            scroll={{ y: '58%' }}
                        />
                        <SlidePane
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={this.closeSlidePane}
                            visible={visibleSlidePane}
                            style={{ right: '0px', width: '75%', height: '100%', minHeight: '600px' }}
                        >
                            <TaskFlowView
                                visibleSlidePane={visibleSlidePane}
                                goToTaskDev={this.props.goToTaskDev}
                                reload={this.search}
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
