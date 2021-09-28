import * as React from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { cloneDeep, get } from 'lodash';
import { hashHistory } from 'react-router'
import {
    Table, message, Modal,
    Card, Button, Select,
    Icon, DatePicker, Tooltip, Form, Breadcrumb, Pagination
} from 'antd'

import { SlidePane, MultiSearchInput } from 'dt-react-component'

import TaskJobFlowView from './taskJobFlowView';

import Api from '../../../api/operation'
import { replaceObjectArrayFiledName } from './hlep'
import { offlineTaskStatusFilter, TASK_STATUS, TASK_TYPE } from '../../../consts/comm'
import { TaskStatus, TaskType } from '../../../components/status'

const Option: any = Select.Option
const confirm = Modal.confirm
const warning = Modal.warning
const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker
const yesterDay = moment().subtract(1, 'days');

class PatchDataDetail extends React.Component<any, any> {
    state: any = {
        loading: false,
        current: 1,
        pageSize: 20,
        selectedRowKeys: [],
        expandedRowKeys: [],
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
        selectedTask: {},
        searchType: 'fuzzy',
        appType: '',
        projectId: ''
    }
    _timeClock: any;
    _isUnmounted: any;

    componentDidMount () {
        const { fillJobName, appType = '', projectId = '' } = this.props.location?.state ?? {}
        this.setState({ fillJobName, appType, projectId }, () => {
            this.search();
            this.getTaskTypesX()
        });
    }

    componentWillUnmount () {
        this._isUnmounted = true;
        clearTimeout(this._timeClock);
    }

    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            hashHistory.push('/operation-ui/task-patch-data'); // 直接跳转到补数据列表页
        }
    }
    getTaskTypesX = () => {
        const ctx = this;
        let parmms = {
            appType: ctx.state.appType,
            projectId: ctx.state.projectId
        }
        Api.getTaskTypesX(parmms).then((res: any) => {
            if (res.code === 1) {
                const taskTypes = res.data;
                let taskTypeFilter = taskTypes && taskTypes.filter((item) => item.value === 'Shell' || item.value === 'ImpalaSQL').map((type: any) => {
                    return {
                        value: type.key,
                        id: type.key,
                        text: type.value
                    }
                });
                this.setState({ taskTypeFilter: taskTypeFilter });
            }
        });
    }
    debounceSearch () {
        if (this._isUnmounted) {
            return;
        }
        this._timeClock = setTimeout(() => {
            this.search(true);
        }, 36000);
    }

    getReqParams = () => {
        const {
            fillJobName, dutyUserId, jobStatuses,
            bizDay, current, taskName, taskType,
            execTimeSort, execStartSort, cycSort, retryNumSort, projectId,
            businessDateSort, expandedRowKeys, table, searchType, pageSize, appType
        } = this.state;
        const reqParams: any = {
            currentPage: current,
            pageSize
        }
        if (expandedRowKeys.length > 0) {
            const flowJobIdList: any = [];
            const arr = table.data.recordList;
            expandedRowKeys.forEach((id: any) => {
                const matched = arr.find((item: any) => item.id === id);
                if (matched) flowJobIdList.push(`${matched.jobId}`);
            })
            // TODO 这里expandedRowKeys默认取的是 id 字段， flowJobIdList参数需要的是jobId,
            // TODO 后端按理最好要统一
            reqParams.flowJobIdList = flowJobIdList;
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
        reqParams.appType = appType;
        reqParams.projectId = projectId;
        reqParams.execTimeSort = execTimeSort || undefined;
        reqParams.execStartSort = execStartSort || undefined;
        reqParams.cycSort = cycSort || undefined;
        reqParams.businessDateSort = businessDateSort || undefined;
        reqParams.retryNumSort = retryNumSort || undefined;
        reqParams.searchType = searchType;

        return reqParams;
    }
    search = (isSilent?: any) => {
        const reqParams = this.getReqParams();
        clearTimeout(this._timeClock);
        this.loadPatchRecords(reqParams, isSilent)
    }
    loadPatchRecords (params: any, isSilent: any) {
        const ctx = this
        if (!(isSilent && typeof isSilent == 'boolean')) {
            this.setState({ loading: true })
        }
        Api.getFillDataDetail(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    selectedRowKeys: []
                })
                this.debounceSearch();
                const recordList = res.data.data.recordList;
                const expandedRowKeys: any = [];
                replaceObjectArrayFiledName(recordList, 'relatedRecords', 'children');
                for (let i = 0; i < recordList.length; i++) {
                    let job = recordList[i];
                    if (job.taskType === TASK_TYPE.WORKFLOW) {
                        if (!job.children) {
                            job.children = [];
                        } else {
                            expandedRowKeys.push(job.id);
                        }
                    }
                }
                ctx.setState({ table: res.data, expandedRowKeys: expandedRowKeys })
            }
            this.setState({ loading: false })
        })
        this.loadJobStatics(params)
    }

    loadJobStatics (params: any) {
        const ctx = this
        params.type = 1;
        params.fillTaskName = params.fillJobName;
        Api.queryJobStatics(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    // 杀死所有实例
    killAllJobs = () => {
        const ctx = this
        Api.stopFillDataJobs({
            fillDataJobName: ctx.state.fillJobName,
            appType: ctx.state.appType,
            projectId: ctx.state.projectId
        }).then((res: any) => {
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
                    Api.batchStopJob({ jobIdList: selected }).then((res: any) => {
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
                    Api.batchRestartAndResume({ jobIdList: selected }).then((res: any) => {
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

    canReload = (ids: any) => { // 未运行、成功、失败的任务可以reload
        const tasks = this.state.table.data && this.state.table.data.recordList
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const task = tasks.find((item: any) => item.id === id)
                if (
                    task &&
                    task.status !== TASK_STATUS.WAIT_SUBMIT &&
                    task.status !== TASK_STATUS.FINISHED &&
                    task.status !== TASK_STATUS.RUN_FAILED &&
                    task.status !== TASK_STATUS.SUBMIT_FAILED &&
                    task.status !== TASK_STATUS.STOPED &&
                    task.status !== TASK_STATUS.KILLED &&
                    task.status !== TASK_STATUS.PARENT_FAILD
                ) return false
            }
            return true
        }
    }

    canKill = (ids: any) => { // 是否可以进行kill
        const tasks = this.state.table.data && this.state.table.data.recordList
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i]
                const res = tasks.find((task: any) => task.id === id)
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

    handleTableChange = (pagination: any, filters: any, sorter?: any) => {
        const { pageSize } = this.state;
        let params: any = {
            current: pagination.current || 1,
            pageSize: pagination.pageSize || pageSize,
            jobStatuses: filters.status,
            taskType: filters.taskType,
            selectedRowKeys: [],
            selectedRows: [],
            execTimeSort: '', // 运行时长
            execStartSort: '', // 执行开始
            cycSort: '', // 计划时间
            businessDateSort: '',
            retryNumSort: ''

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
                case 'retryNum': {
                    params.retryNumSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
            }
        }
        this.setState(params, () => {
            this.search()
        })
    }

    changePerson = (target: any) => {
        this.setState({ dutyUserId: target, current: 1 }, () => {
            this.search()
        })
    }

    onSelectChange = (selectedRowKeys: any, selectedRows: any) => {
        this.setState({ selectedRowKeys, selectedRows });
    }

    changeBussinessDate = (value: any) => {
        this.setState({ bizDay: value, current: 1 }, () => {
            this.search()
        })
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > new Date().getTime();
    }

    showTask = (task: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        })
    }

    changeTaskName = (v: any) => { // 任务名变更
        this.setState({ taskName: v })
    }

    changeSearchType = (type: any) => {
        this.setState({ searchType: type });
        this.onSearchByTaskName()
    }

    onSearchByTaskName = () => {
        this.setState({
            current: 1
        }, this.search)
    }

    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = []
        let selectedRows: any = []

        if (e.target.checked) {
            const tasks = this.state.table.data && this.state.table.data.recordList
            selectedRowKeys = tasks && tasks.map((item: any) => { if (item.batchTask.isDeleted !== 1) { return item.id } })
            selectedRows = tasks && tasks.filter((item: any) => { if (item.batchTask.isDeleted !== 1) { return true } });
        }

        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys,
            selectedRows
        })
    }

    initTaskColumns = () => {
        const { taskTypeFilter } = this.state;
        return [{
            title: '任务名称',
            dataIndex: 'jobName',
            key: 'jobName',
            width: '460px',
            fixed: 'left',
            render: (text: any, record: any) => {
                let name = record.batchTask && record.batchTask.name
                let originText = name;
                // name = utils.textOverflowExchange(name, 45);
                let showName: any;
                if (record.batchTask.isDeleted === 1) {
                    showName = `${name} (已删除)`;
                } else if (get(record, 'retryNum') && [TASK_STATUS.WAIT_RUN, TASK_STATUS.RUNNING].indexOf(record.status) > -1) {
                    showName = <a onClick={() => { this.showTask(record) }}>{name}(重试)</a>
                } else {
                    showName = <a onClick={() => { this.showTask(record) }}>{name}</a>;
                }
                return <span title={originText}>{showName}</span>;
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            fixed: 'left',
            render: (text: any, record: any) => {
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
            render: (text: any, record: any) => {
                return <TaskType value={text} />
            },
            filters: taskTypeFilter
        }, {
            title: '业务日期',
            dataIndex: 'bizDay',
            width: '120px',
            key: 'bizDay',
            sorter: true
        }, {
            title: '计划时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
            width: '180px',
            sorter: true
        }, {
            title: '开始时间',
            dataIndex: 'exeStartTime',
            key: 'exeStartTime',
            width: '180px',
            sorter: true
        }, {
            title: '运行时长',
            dataIndex: 'exeTime',
            key: 'exeTime',
            width: '150px',
            sorter: true
        }, {
            title: '重试次数',
            dataIndex: 'retryNum',
            key: 'retryNum',
            width: '120px',
            sorter: true
        }, {
            title: '责任人',
            dataIndex: 'dutyUserName',
            width: '180px',
            key: 'dutyUserName',
            render: (txt, record) => record?.batchTask?.ownerUser?.userName
        }]
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedTask: {}
        })
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
                case TASK_STATUS.RUNNING:
                case TASK_STATUS.SUBMITTING:
                case TASK_STATUS.WAIT_SUBMIT:
                case TASK_STATUS.WAIT_RUN: {
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

    onExpandRows = (expandedRows: any) => {
        this.setState({ expandedRowKeys: expandedRows })
    }

    onExpand = (expanded: any, record: any) => {
        if (expanded) {
            const { table } = this.state;
            let newTableData = cloneDeep(table);
            const { jobId } = record;
            const reqParams = this.getReqParams();
            reqParams.jobId = jobId;
            Api.getFillDataRelatedJobs(reqParams).then((res: any) => {
                if (res.code == 1) {
                    const recordList = newTableData.data.recordList;
                    const index = recordList.findIndex((task: any) => {
                        return task.jobId == jobId
                    });
                    if (index || index == 0) {
                        recordList[index] = {
                            ...res.data,
                            children: res.data.relatedRecords,
                            relatedRecords: undefined
                        };
                    }
                    this.setState({
                        table: newTableData
                    })
                }
            })
        }
    }
    renderStatus = (list: any) => {
        return list.map((item: any, index: any) => {
            const { className, children } = item;
            return (
                <span key={index} className={className}>
                    {Object.prototype.toString.call(children) === '[object Object]'
                        ? `${children.title}: ${children.dataSource || 0}`
                        : children.map((childItem: any) => {
                            return (
                                <span key={childItem.title}>
                                    {childItem.title}: {childItem.dataSource || 0}
                                </span>
                            );
                        })}
                </span>
            );
        });
    };
    render () {
        const {
            table, selectedRowKeys, fillJobName,
            bizDay, current, statistics, taskName,
            selectedTask, visibleSlidePane, searchType
        } = this.state

        const { personList } = this.props
        const columns: any = this.initTaskColumns();
        const pagination: any = {
            total: table.totalCount,
            defaultPageSize: 20,
            current,
            showQuickJumper: true,
            showTotal: (total: number) => {
                return `共 ${total} 条数据, 每页展示20条`;
            },
            onChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {}),
            onShowSizeChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {})
        };

        // rowSelection object indicates the need for row selection
        const rowSelection: any = {
            selectedRowKeys,
            onChange: this.onSelectChange,
            getCheckboxProps: (record: any) => ({
                disabled: record.batchTask && record.batchTask.isDeleted === 1
            })
        };
        const {
            ALL,
            RUNNING,
            UNSUBMIT,
            SUBMITTING,
            WAITENGINE,
            FINISHED,
            CANCELED,
            FROZEN,
            SUBMITFAILD,
            FAILED,
            PARENTFAILED
        } = statistics;
        const statusList = [
            {
                className: 'status_overview_count_font',
                children: { title: '总数', dataSource: ALL }
            },
            {
                className: 'status_overview_running_font',
                children: { title: '运行中', dataSource: RUNNING }
            },
            {
                className: 'status_overview_yellow_font',
                children: [
                    { title: '等待提交', dataSource: UNSUBMIT },
                    { title: '提交中', dataSource: SUBMITTING },
                    { title: '等待运行', dataSource: WAITENGINE }
                ]
            },
            {
                className: 'status_overview_finished_font',
                children: { title: '成功', dataSource: FINISHED }
            },
            {
                className: 'status_overview_grey_font',
                children: [
                    { title: '取消', dataSource: CANCELED },
                    { title: '冻结', dataSource: FROZEN }
                ]
            },
            {
                className: 'status_overview_fail_font',
                children: [
                    { title: '提交失败', dataSource: SUBMITFAILD },
                    { title: '运行失败', dataSource: FAILED },
                    { title: '上游失败', dataSource: PARENTFAILED }
                ]
            }
        ];

        const tableFooter = (currentPageData: any) => {
            const selectStatus = this.getSelectRowsStatus();
            const couldKill = selectStatus.haveRunning && !selectStatus.haveFail && !selectStatus.haveNotRun && !selectStatus.haveFail;
            const couldReRun = !selectStatus.haveRunning && (selectStatus.haveSuccess || selectStatus.haveFail || selectStatus.haveNotRun || selectStatus.haveFail);
            return (
                <div className="flex-between">
                    <div style={{ paddingLeft: '25px' }}>
                        <tr className="ant-table-row  ant-table-row-level-0">
                            <td>
                                <Button style={{ marginRight: 12 }} disabled={!couldKill} type="primary" onClick={this.batchKillJobs}>批量杀任务</Button>&nbsp;
                                <Button style={{ marginRight: 12 }} disabled={!couldReRun} type="primary" onClick={this.batchReloadJobs}>重跑当前及下游任务</Button>&nbsp;
                                <Button type="primary" onClick={this.killAllJobs}>杀死所有实例</Button>&nbsp;
                            </td>
                        </tr>
                    </div>
                    <div>
                        <Pagination style={{ top: 20 }} {...pagination} />
                    </div>
                </div>
            );
        };
        let clientHeight = document.documentElement.clientHeight - 390;
        return (
            <div>
                <div style={{ margin: '12px 20px' }}>
                    <Breadcrumb>
                        <Breadcrumb.Item>
                            <a href="/#/operation-ui/task-patch-data">补数据实例</a>
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>{fillJobName}</Breadcrumb.Item>
                    </Breadcrumb>
                </div>
                <div style={{ borderTop: '1px solid #ddd', padding: '16px 20px', display: 'flex', justifyContent: 'space-between' }}>
                    <Form layout="inline">
                        <FormItem label="" className="batch-operation_offlineImg dt-form-shadow-bg">
                            <MultiSearchInput
                                placeholder="按任务名称搜索"
                                style={{ height: '32px' }}
                                value={taskName}
                                searchType={searchType}
                                onChange={this.changeTaskName}
                                onTypeChange={this.changeSearchType}
                                onSearch={this.onSearchByTaskName}
                            />
                        </FormItem>
                        <FormItem
                            label="责任人"
                        >
                            <Select
                                allowClear
                                showSearch
                                style={{ width: '210px' }}
                                className="dt-form-shadow-bg"
                                placeholder="请选择责任人"
                                optionFilterProp="name"
                                onChange={this.changePerson}
                            >
                                {personList.map((item: any) => {
                                    return <Option key={item.dtuicUserId} value={`${item.dtuicUserId}`}>{item.userName}</Option>
                                })}
                            </Select>
                        </FormItem>
                        <FormItem
                            label="业务日期"
                        >
                            <RangePicker
                                disabledDate={this.disabledDate}
                                size="default"
                                style={{ width: '210px' }}
                                className="dt-form-shadow-bg"
                                format="YYYY-MM-DD"
                                ranges={{
                                    '昨天': [(moment() as any).subtract(2, 'days'), yesterDay],
                                    '最近7天': [(moment() as any).subtract(8, 'days'), yesterDay],
                                    '最近30天': [(moment() as any).subtract(31, 'days'), yesterDay]
                                }}
                                value={bizDay}
                                onChange={this.changeBussinessDate}
                            />
                        </FormItem>
                    </Form>
                    <div style={{ width: 32, height: 32, background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Tooltip title="刷新数据">
                            <Icon type="sync" onClick={this.search}
                                style={{
                                    cursor: 'pointer',
                                    width: '16px',
                                    height: '17px',
                                    color: '#94A8C6'
                                }}
                            />
                        </Tooltip>
                    </div>
                </div>
                <div className="box-2 m-card task-manage" style={{ minWidth: '1167px' }}>
                    <Card
                        hoverable
                        bordered={false}
                        loading={false}
                    >
                        <div style={{ margin: '16px 10px' }}>
                            <span className="ope-statistics">
                                {this.renderStatus(statusList)}
                            </span>
                        </div>
                        <Table
                            rowKey="id"
                            rowClassName={
                                (record: any, index: any) => {
                                    if (this.state.selectedTask && this.state.selectedTask.id == record.id) {
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            expandedRowKeys={this.state.expandedRowKeys}
                            {...{ defaultExpandAllRows: true }}
                            style={{ marginTop: '1px', height: 'calc(100vh - 270px)' }}
                            scroll={{ x: '1700px', y: clientHeight }}
                            className="dt-ant-table rdos-ant-table-placeholder dt-ant-table--border full-screen-table-120 m-data-detail-page dt-table-fixed-base dt-table-fixed-contain-footer"
                            rowSelection={rowSelection}
                            pagination={false}
                            loading={this.state.loading}
                            columns={columns}
                            dataSource={(table.data && table.data.recordList) || []}
                            onChange={this.handleTableChange}
                            onExpand={this.onExpand}
                            onExpandedRowsChange={this.onExpandRows}
                            footer={tableFooter}
                        />
                        <SlidePane
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={this.closeSlidePane}
                            visible={visibleSlidePane}
                            style={{ right: '0px', width: '60%', bottom: 0, minHeight: '400px', position: 'fixed', paddingTop: '64px' }}
                        >
                            <TaskJobFlowView
                                visibleSlidePane={visibleSlidePane}
                                taskJob={selectedTask}
                                realod={this.search}
                            />
                        </SlidePane>
                    </Card>
                </div>
            </div>
        )
    }
}

export default connect(
    (state: any) => {
        return {
            personList: state.operation.personList
        }
    }
)(PatchDataDetail)
