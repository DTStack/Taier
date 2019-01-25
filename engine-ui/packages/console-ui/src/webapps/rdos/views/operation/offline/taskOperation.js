import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { isEmpty, cloneDeep } from 'lodash';

import {
    Table, message, Modal,
    Card, Input, Button, Select,
    Icon, DatePicker, Tooltip,
    Form, Checkbox
} from 'antd'

import utils from 'utils'
import { replaceObjectArrayFiledName } from 'funcs';
import SlidePane from 'widgets/slidePane'
import { Circle } from 'widgets/circle'

import Api from '../../../api'
import {
    offlineTaskStatusFilter,
    offlineTaskPeriodFilter,
    PROJECT_TYPE,
    TASK_STATUS,
    TASK_TYPE
} from '../../../comm/const'

import {
    TaskStatus, TaskTimeType, TaskType
} from '../../../components/status'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'

import TaskJobFlowView from './taskJobFlowView'

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
            data: []
        },
        loading: false,
        continue: false,
        current: 1,
        person: '',
        jobName: utils.getParameterByName('job') ? utils.getParameterByName('job') : '',
        taskStatus: isEmpty(utils.getParameterByName('status')) ? [] : utils.getParameterByName('status').split(','),
        bussinessDate: [moment(yesterDay).subtract(utils.getParameterByName('date') || 0, 'days'), yesterDay],
        cycDate: undefined,
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
        selectedRowKeys: [],
        expandedRowKeys: []
    }

    componentDidMount () {
        this.search()
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

    search = () => {
        const {
            jobName, person, taskStatus,
            bussinessDate, businessDateSort, jobType, current,
            taskType, taskPeriodId, execTimeSort,
            execStartSort, execEndSort,
            cycSort, cycDate
        } = this.state
        const reqParams = {
            currentPage: current
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

    loadTaskList (params) { // currentPage, pageSize, isTimeSortDesc, status
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            currentPage: 1,
            pageSize: 20,
            type: 0
        }, params)
        Api.queryJobs(reqParams).then((res) => {
            if (res.code === 1) {
                res.data.data = res.data.data || [];
                const expandedRowKeys = [];
                replaceObjectArrayFiledName(res.data.data, 'relatedJobs', 'children');
                for (let i = 0; i < res.data.data.length; i++) {
                    let job = res.data.data[i];
                    if (job.batchTask && job.batchTask.taskType == TASK_TYPE.WORKFLOW) {
                        if (!job.children) {
                            job.children = [];
                        } else {
                            expandedRowKeys.push(job.id);
                        }
                    }
                }
                ctx.setState({ tasks: res.data, expandedRowKeys: expandedRowKeys })
            }
            ctx.setState({
                loading: false
            })
        })
        this.loadJobStatics(params)
    }

    loadJobStatics (params) {
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
                            ctx.setState({ selectedRowKeys: [], checkAll: false })
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
                            ctx.setState({ selectedRowKeys: [], checkAll: false })
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

    canReload = (ids) => { // 未运行、成功、失败/上游失败的任务可以reload
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
                    res.status !== TASK_STATUS.STOPED &&
                    res.status !== TASK_STATUS.PARENT_FAILD
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
        const params = {
            current: pagination.current,
            taskStatus: filters.status,
            jobType: filters.type ? filters.type[0] : '',
            selectedRowKeys: [],
            taskType: filters.taskType,
            taskPeriodId: filters.taskPeriodId,
            checkAll: false,
            execTimeSort: '',
            execStartSort: '',
            execEndSort: '',
            businessDateSort: '',
            cycSort: ''
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

    onSearchByTaskName = () => {
        this.setState({
            current: 1
        }, this.search)
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
        this.setState({
            cycDate: value,
            current: 1
        }, () => {
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
        const { taskTypeFilter } = this.props;

        return [{
            title: '任务名称',
            dataIndex: 'id',
            key: 'id',
            width: '200px',
            render: (text, record) => {
                const name = record.batchTask && record.batchTask.name
                const showName = record.batchTask.isDeleted === 1
                    ? `${name} (已删除)`
                    : <a onClick={() => { this.showTask(record) }}>{name}</a>;
                return showName;
            },
            fixed: 'left'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: '100px',
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
            fixed: 'left',
            filters: offlineTaskStatusFilter,
            filterMultiple: true,
            filteredValue: taskStatus
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text, record) => {
                return <TaskType value={record.batchTask && record.batchTask.taskType} />
            },
            width: '90px',
            filters: taskTypeFilter
        }, {
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text) => {
                return <TaskTimeType value={text} />
            },
            width: '90px',
            filters: offlineTaskPeriodFilter
        }, {
            title: '业务日期',
            dataIndex: 'businessDate',
            key: 'businessDate',
            sorter: true
        }, {
            title: '计划时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
            sorter: true
        }, {
            title: '开始时间',
            dataIndex: 'execStartDate',
            key: 'execStartDate',
            sorter: true
        }, {
            title: '结束时间',
            dataIndex: 'execEndDate',
            key: 'execEndDate',
            sorter: true
        }, {
            title: '运行时长',
            dataIndex: 'execTime',
            key: 'execTime',
            sorter: true
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser',
            render: (text, record) => {
                return record.batchTask && record.batchTask.ownerUser &&
                    record.batchTask.ownerUser.userName
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
                <div style={{ padding: '15px 20px 10px 23px', display: 'inline-block' }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </div>
                <div style={{ display: 'inline-block' }}>
                    <Button type="primary" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                    <Button type="primary" onClick={this.batchReloadJobs}>重跑当前及下游任务</Button>&nbsp;
                </div>
            </div>
        )
    }

    onExpandRows = (expandedRows) => {
        this.setState({ expandedRowKeys: expandedRows })
    }

    onExpand = (expanded, record) => {
        if (expanded) {
            if (record.children && record.children.length) {
                return;
            }
            const { tasks } = this.state;
            let newTasks = cloneDeep(tasks);
            const { jobId } = record;
            Api.getRelatedJobs({
                jobId
            }).then((res) => {
                if (res.code == 1) {
                    const index = newTasks.data.findIndex((task) => {
                        return task.jobId == jobId
                    });
                    if (index || index == 0) {
                        newTasks.data[index] = {
                            ...res.data,
                            children: res.data.relatedJobs,
                            relatedJobs: undefined
                        };
                    }
                    this.setState({
                        tasks: newTasks
                    })
                }
            })
        } else {
            console.log('record')
        }
    }

    render () {
        const {
            tasks, selectedRowKeys, jobName,
            bussinessDate, current, statistics,
            selectedTask, visibleSlidePane, cycDate
        } = this.state

        const { projectUsers, project } = this.props

        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (<Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const pagination = {
            total: tasks.totalCount,
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
                            <Form
                                layout="inline"
                                style={{ marginTop: '10px' }}
                                className="m-form-inline"
                            >
                                <FormItem label="">
                                    <Search
                                        placeholder="按任务名称搜索"
                                        style={{ width: 200 }}
                                        size="default"
                                        value={jobName}
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
                                        size='default'
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
                                        size="default"
                                        style={{ width: 200 }}
                                        format="YYYY-MM-DD"
                                        disabledDate={this.disabledDate}
                                        ranges={{
                                            '昨天': [moment().subtract(2, 'days'), yesterDay],
                                            '最近7天': [moment().subtract(8, 'days'), yesterDay],
                                            '最近30天': [moment().subtract(31, 'days'), yesterDay]
                                        }}
                                        value={bussinessDate || null}
                                        onChange={this.changeBussinessDate}
                                    />
                                </FormItem>
                                <FormItem
                                    label="计划时间"
                                >
                                    <RangePicker
                                        size="default"
                                        style={{ width: 270 }}
                                        showTime
                                        format="YYYY/MM/DD HH:mm:ss"
                                        ranges={{
                                            '今天': [moment(), moment()],
                                            '最近7天': [moment().subtract(7, 'days'), moment()],
                                            '最近30天': [moment().subtract(30, 'days'), moment()]
                                        }}
                                        value={cycDate || null}
                                        onChange={this.changecycDate}
                                        onOk={this.search}
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
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            style={{ marginTop: '1px' }}
                            className="m-table full-screen-table-120"
                            expandedRowKeys={this.state.expandedRowKeys}
                            rowSelection={rowSelection}
                            pagination={pagination}
                            loading={this.state.loading}
                            columns={this.initTaskColumns()}
                            dataSource={tasks.data || []}
                            onChange={this.handleTableChange}
                            footer={this.tableFooter}
                            onExpand={this.onExpand}
                            onExpandedRowsChange={this.onExpandRows}
                            scroll={{ x: '1400px' }}
                        />
                        <SlidePane
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={this.closeSlidePane}
                            visible={visibleSlidePane}
                            style={{ right: '0px', width: '60%', height: '100%', minHeight: '600px', position: 'fixed', paddingTop: '50px' }}
                        >
                            <TaskJobFlowView
                                isPro={isPro}
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
        taskTypeFilter: state.offlineTask.comm.taskTypeFilter
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineTaskList)
