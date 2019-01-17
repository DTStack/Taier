import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import {
    Table, message, Modal,
    Card, Input, Button, Select,
    Icon, DatePicker, Tooltip, Form, Checkbox
} from 'antd'

import SlidePane from 'widgets/slidePane'
import { Circle } from 'widgets/circle'
import GoBack from 'main/components/go-back'
import { replaceObjectArrayFiledName } from 'funcs';

import Api from '../../../api'
import {
    offlineTaskStatusFilter,
    TASK_STATUS
} from '../../../comm/const'

import {
    TaskStatus, TaskType
} from '../../../components/status'

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
const yesterDay = moment().subtract(1, 'days');
class PatchDataDetail extends Component {
    state = {
        loading: false,
        current: 1,
        selectedRowKeys: [],
        selectedRows: [],
        checkAll: false,

        dutyUserId: '',
        fillJobName: '',
        jobStatuses: '',
        taskName: '',
        bizDay: undefined,
        taskType: '',

        table: {
            data: []
        },
        statistics: '',

        visibleSlidePane: false,
        selectedTask: {}
    }

    componentDidMount () {
        this.setState({
            fillJobName: this.props.params.fillJobName
        }, this.search)
    }
    componentWillUnmount () {
        this._isUnmounted = true;
        clearTimeout(this._timeClock);
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.setState({ current: 1, visibleSlidePane: false }, () => {
                this.search()
            })
        }
    }
    debounceSearch () {
        if (this._isUnmounted) {
            return;
        }
        this._timeClock = setTimeout(() => {
            this.search(true);
        }, 5000);
    }
    search = (isSilent) => {
        const {
            fillJobName, dutyUserId, jobStatuses,
            bizDay, current, taskName, taskType,
            execTimeSort, execStartSort, cycSort,
            businessDateSort
        } = this.state;
        const reqParams = {
            currentPage: current,
            pageSize: 20
        }
        if (fillJobName) {
            reqParams.fillJobName = fillJobName
        }
        if (taskName) {
            reqParams.taskName = taskName
        }
        if (dutyUserId !== '') {
            reqParams.dutyUserId = dutyUserId
        }
        if (bizDay && bizDay.length > 1) {
            reqParams.bizStartDay = bizDay[0].unix();
            reqParams.bizEndDay = bizDay[1].unix();
        }
        if (jobStatuses && jobStatuses.length > 0) {
            reqParams.jobStatuses = jobStatuses.join(',')
        }
        if (taskType) {
            reqParams.taskType = taskType.join(',')
        }

        reqParams.execTimeSort = execTimeSort || undefined;
        reqParams.execStartSort = execStartSort || undefined;
        reqParams.cycSort = cycSort || undefined;
        reqParams.businessDateSort = businessDateSort || undefined;
        clearTimeout(this._timeClock);
        this.loadPatchRecords(reqParams, isSilent)
    }

    loadPatchRecords (params, isSilent) {
        const ctx = this
        if (!(isSilent && typeof isSilent == 'boolean')) {
            this.setState({ loading: true })
        }
        Api.getFillDataDetail(params).then((res) => {
            if (res.code === 1) {
                this.debounceSearch();
                replaceObjectArrayFiledName(res.data.data.recordList, 'relatedRecords', 'children');
                ctx.setState({ table: res.data })
            }
            this.setState({ loading: false })
        })
        this.loadJobStatics(params)
    }

    loadJobStatics (params) {
        const ctx = this
        params.type = 1;
        params.fillTaskName = params.fillJobName;
        Api.queryJobStatics(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    // 杀死所有实例
    killAllJobs = () => {
        Api.stopFillDataJobs({
            fillDataJobName: this.state.fillJobName
        }).then(res => {
            if (res.code === 1) {
                this.search();
                message.success('已成功杀死所有实例！')
            }
        })
    }

    batchKillJobs = () => { // 批量重跑
        const ctx = this
        const selected = this.state.selectedRowKeys

        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要杀死的任务！'
            })
            return
        }
        if (this.canKill(selected)) {
            confirm({
                title: '确认提示',
                content: '确定要杀死选择的任务？',
                onOk () {
                    Api.batchStopJob({ jobIdList: selected }).then((res) => {
                        if (res.code === 1) {
                            ctx.setState({ selectedRowKeys: [], selectedRows: [], checkAll: false })
                            message.success('已经成功杀死所选任务！')
                            ctx.search()
                        }
                    })
                }
            });
        } else {
            warning({
                title: '提示',
                content: `
                    “失败”、“取消”、“成功”状态和“已删除”的任务，不能被杀死 !
                `
            })
        }
    }

    batchReloadJobs = () => { // 批量重跑
        const ctx = this
        const selected = this.state.selectedRowKeys
        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要重跑的任务！'
            })
            return
        }
        if (this.canReload(selected)) {
            confirm({
                title: '确认提示',
                content: '确认需要重跑选择的任务？',
                onOk () {
                    Api.batchRestartAndResume({ jobIdList: selected }).then((res) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑所选任务！')
                            ctx.setState({ selectedRowKeys: [], selectedRows: [], checkAll: false })
                            ctx.search()
                        }
                    })
                }
            });
        } else {
            warning({
                title: '提示',
                content: `
                    只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                    请您重新选择!
                `
            })
        }
    }

    canReload = (ids) => { // 未运行、成功、失败的任务可以reload
        const tasks = this.state.table.data && this.state.table.data.recordList
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const task = tasks.find(item => item.id === id)
                if (
                    task &&
                    task.status !== TASK_STATUS.WAIT_SUBMIT &&
                    task.status !== TASK_STATUS.FINISHED &&
                    task.status !== TASK_STATUS.RUN_FAILED &&
                    task.status !== TASK_STATUS.SUBMIT_FAILED &&
                    task.status !== TASK_STATUS.STOPED &&
                    task.status !== TASK_STATUS.PARENT_FAILD
                ) return false
            }
            return true
        }
    }

    canKill = (ids) => { // 是否可以进行kill
        const tasks = this.state.table.data && this.state.table.data.recordList
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const res = tasks.find(task => task.id === id)
                if (res && (
                    res.status === TASK_STATUS.SUBMIT_FAILED ||
                    res.status === TASK_STATUS.RUN_FAILED ||
                    res.status === TASK_STATUS.PARENT_FAILD ||
                    res.status === TASK_STATUS.STOPED ||
                    res.status === TASK_STATUS.FINISHED ||
                    res.batchTask.isDeleted === 1
                )) return false
            }
            return true
        }
    }

    handleTableChange = (pagination, filters, sorter) => {
        let params = {
            current: pagination.current,
            jobStatuses: filters.status,
            taskType: filters.taskType,
            selectedRowKeys: [],
            selectedRows: [],
            execTimeSort: '', // 运行时长
            execStartSort: '', // 执行开始
            cycSort: '', // 计划时间
            businessDateSort: ''

        }
        if (sorter) {
            let { field, order } = sorter;

            switch (field) {
                case 'exeTime': {
                    params.execTimeSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'exeStartTime': {
                    params.execStartSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'cycTime': {
                    params.cycSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
                case 'bizDay': {
                    params.businessDateSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
            }
        }
        this.setState(params, () => {
            this.search()
        })
    }

    changePerson = (target) => {
        this.setState({ dutyUserId: target, current: 1 }, () => {
            this.search()
        })
    }

    onSelectChange = (selectedRowKeys, selectedRows) => {
        this.setState({ selectedRowKeys, selectedRows });
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

    changeTaskName = (e) => { // 任务名变更
        this.setState({ taskName: e.target.value })
    }

    onSearchByTaskName = () => {
        this.setState({
            current: 1
        }, this.search)
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []
        let selectedRows = []

        if (e.target.checked) {
            const tasks = this.state.table.data && this.state.table.data.recordList
            selectedRowKeys = tasks && tasks.map(item => { if (item.batchTask.isDeleted !== 1) { return item.id } })
            selectedRows = tasks && tasks.filter(item => { if (item.batchTask.isDeleted !== 1) { return true } });
        }

        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys,
            selectedRows
        })
    }

    initTaskColumns = () => {
        const { taskTypeFilter } = this.props;
        return [{
            title: '任务名称',
            dataIndex: 'jobName',
            key: 'jobName',
            width: '200px',
            fixed: 'left',
            render: (text, record) => {
                const showName = record.batchTask.isDeleted === 1
                    ? `${text} (已删除)`
                    : <a onClick={() => { this.showTask(record) }}>{text}</a>;
                return showName
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            fixed: 'left',
            render: (text, record) => {
                return <span>
                    <TaskStatus value={text} />
                    {record.isDirty && text == TASK_STATUS.FINISHED
                        ? <Tooltip
                            title="部分数据未同步成功，建议检查配置"
                        >
                            <Icon type="info-circle-o" style={{ color: '#ee9b1e', marginLeft: '5px' }} />
                        </Tooltip>
                        : null}
                </span>
            },
            width: '110px',
            filters: offlineTaskStatusFilter,
            filterMultiple: true
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            width: '100px',
            render: (text, record) => {
                return <TaskType value={text} />
            },
            filters: taskTypeFilter
        }, {
            title: '业务日期',
            dataIndex: 'bizDay',
            key: 'bizDay',
            sorter: true
        }, {
            title: '计划时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
            sorter: true
        }, {
            title: '开始时间',
            dataIndex: 'exeStartTime',
            key: 'exeStartTime',
            sorter: true
        }, {
            title: '运行时长',
            dataIndex: 'exeTime',
            key: 'exeTime',
            width: '100px',
            sorter: true
        }, {
            title: '责任人',
            dataIndex: 'dutyUserName',
            key: 'dutyUserName'
        }]
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedTask: {}
        })
    }

    tableFooter = (currentPageData) => {
        const selectStatus = this.getSelectRowsStatus();
        const couldKill = selectStatus.haveRunning && !selectStatus.haveFail && !selectStatus.haveNotRun && !selectStatus.haveFail;
        const couldReRun = !selectStatus.haveRunning && (selectStatus.haveSuccess || selectStatus.haveFail || selectStatus.haveNotRun || selectStatus.haveFail);
        return (
            <tr className="ant-table-row  ant-table-row-level-0">
                <td style={{ padding: '15px 10px 10px 22px' }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </td>
                <td>
                    <Button disabled={!couldKill} type="primary" size="small" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                    <Button disabled={!couldReRun} type="primary" size="small" onClick={this.batchReloadJobs}>重跑当前及下游任务</Button>&nbsp;
                    <Button type="primary" size="small" onClick={this.killAllJobs}>杀死所有实例</Button>&nbsp;
                </td>
            </tr>
        )
    }

    getSelectRowsStatus () {
        let haveFail, haveNotRun, haveSuccess, haveRunning;
        const { selectedRows } = this.state;
        for (let i = 0; i < selectedRows.length; i++) {
            let row = selectedRows[i];
            switch (row.status) {
                case TASK_STATUS.RUN_FAILED:
                case TASK_STATUS.PARENT_FAILD:
                case TASK_STATUS.SUBMIT_FAILED: {
                    haveFail = true;
                    break;
                }
                case TASK_STATUS.RUNNING: {
                    haveRunning = true;
                    break;
                }
                case TASK_STATUS.FINISHED: {
                    haveSuccess = true;
                    break;
                }
                default: {
                    haveNotRun = true;
                    break;
                }
            }
        }
        return {
            haveFail, haveNotRun, haveSuccess, haveRunning
        }
    }
    render () {
        const {
            table, selectedRowKeys, fillJobName,
            bizDay, current, statistics, taskName,
            selectedTask, visibleSlidePane
        } = this.state

        const {
            projectUsers, project, goToTaskDev
        } = this.props

        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (<Option key={item.id} value={`${item.userId}`} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 20,
            current
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
                            <span className="status_overview_count_font">
                                <Circle className="status_overview_count" />&nbsp;
                            任务实例总数: &nbsp;{statistics.ALL || 0}
                            </span>&nbsp;
                            <span className="status_overview_wait_submit_font">
                                <Circle className="status_overview_wait_submit" />&nbsp;
                            等待提交: &nbsp;{statistics.UNSUBMIT || 0}
                            </span>&nbsp;
                            <span className="status_overview_submmitting_font">
                                <Circle className="status_overview_submmitting" />&nbsp;
                            提交中: &nbsp;{statistics.SUBMITTING || 0}
                            </span>&nbsp;
                            <span className="status_overview_wait_run_font">
                                <Circle className="status_overview_wait_run" />&nbsp;
                            等待运行: &nbsp;{statistics.WAITENGINE || 0}
                            </span>&nbsp;
                            <span className="status_overview_running_font">
                                <Circle className="status_overview_running" />&nbsp;
                            运行中: &nbsp;{statistics.RUNNING || 0}
                            </span>&nbsp;
                            <span className="status_overview_finished_font">
                                <Circle className="status_overview_finished" />&nbsp;
                            成功: &nbsp;{statistics.FINISHED || 0}
                            </span>&nbsp;
                            <span className="status_overview_stoped_font">
                                <Circle className="status_overview_stoped" />&nbsp;
                            取消: &nbsp;{statistics.CANCELED || 0}
                            </span>&nbsp;
                            <span className="status_overview_fail_font">
                                <Circle className="status_overview_fail" />&nbsp;
                            提交失败: &nbsp;{statistics.SUBMITFAILD || 0}
                            </span>&nbsp;
                            <span className="status_overview_fail_font">
                                <Circle className="status_overview_fail" />&nbsp;
                            运行失败: &nbsp;{statistics.FAILED || 0}
                            </span>&nbsp;
                            <span className="status_overview_fail_font">
                                <Circle className="status_overview_fail" />&nbsp;
                            上游失败: &nbsp;{statistics.PARENTFAILED || 0}
                            </span>&nbsp;
                            <span className="status_overview_frozen_font">
                                <Circle className="status_overview_frozen" />&nbsp;
                            冻结: &nbsp;{statistics.FROZEN || 0}
                            </span>&nbsp;
                        </span>
                    </div>
                </h1>
                <div className="box-2 m-card task-manage">
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
                                    <GoBack
                                        type="textButton"
                                        url="/operation/task-patch-data"
                                    />
                                    <span style={{
                                        fontSize: '14px',
                                        color: '#333333',
                                        marginLeft: '5px'
                                    }}>
                                        {fillJobName}
                                    </span>
                                </span>
                                <Form
                                    layout="inline"
                                    style={{
                                        marginTop: '10px',
                                        marginLeft: '20px',
                                        display: 'inline-block'
                                    }}
                                    className="m-form-inline"
                                >
                                    <FormItem label="">
                                        <Search
                                            placeholder="按任务名称"
                                            style={{ width: 126 }}
                                            value={taskName}
                                            size="default"
                                            onChange={this.changeTaskName}
                                            onSearch={this.onSearchByTaskName}
                                        />
                                    </FormItem>
                                    <FormItem
                                        label="责任人"
                                    >
                                        <Select
                                            allowClear
                                            showSearch
                                            size='Default'
                                            style={{ width: 126 }}
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
                                            disabledDate={this.disabledDate}
                                            size="default"
                                            style={{ width: 270 }}
                                            showTime
                                            format="YYYY/MM/DD HH:mm:ss"
                                            ranges={{
                                                '昨天': [moment().subtract(2, 'days'), yesterDay],
                                                '最近7天': [moment().subtract(8, 'days'), yesterDay],
                                                '最近30天': [moment().subtract(31, 'days'), yesterDay]
                                            }}
                                            value={bizDay}
                                            onChange={this.changeBussinessDate}
                                        />
                                    </FormItem>
                                </Form>
                            </div>
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
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            style={{ marginTop: '1px' }}
                            scroll={{ x: '1200px' }}
                            className="m-table full-screen-table-120"
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
                            onClose={this.closeSlidePane}
                            visible={visibleSlidePane}
                            style={{ right: '0px', width: '60%', height: '100%', minHeight: '600px', position: 'fixed', paddingTop: '50px' }}
                        >
                            <TaskFlowView
                                visibleSlidePane={visibleSlidePane}
                                goToTaskDev={goToTaskDev}
                                taskJob={selectedTask}
                                project={project}
                                realod={this.search}
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
        taskTypeFilter: state.offlineTask.comm.taskTypeFilter
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(PatchDataDetail)
