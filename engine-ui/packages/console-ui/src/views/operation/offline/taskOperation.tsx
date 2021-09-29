/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { isEmpty, cloneDeep, get } from 'lodash';

import {
    Table, message, Modal, Select, Icon,
    DatePicker, Tooltip, Form, Dropdown,
    Menu, Col, Pagination, Row
} from 'antd';

import { SlidePane, MultiSearchInput } from 'dt-react-component';

import TaskJobFlowView from './taskJobFlowView';
import KillJobForm from './killJobForm';

import {
    offlineTaskStatusFilter, offlineTaskPeriodFilter, TASK_STATUS, TASK_TYPE
} from '../../../consts/comm';
import { APPS_TYPE, APP_TYPE_ID, PROJECT_ID } from '../../../consts';
import Api from '../../../api/operation';
import utils from '../../../utils';
import { replaceObjectArrayFiledName } from './hlep'
import { getProjectList } from '../../../actions/operation';
import { TaskStatus, TaskTimeType, TaskType } from '../../../components/status';

import './index.scss';

const Option: any = Select.Option;
const confirm = Modal.confirm;
const warning = Modal.warning;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const yesterDay = moment().subtract(1, 'days');

const createId = function (id: any) {
    return Number(Math.random().toString().substr(3, 3) + Date.now()).toString(36) + '_' + id;
};
class OfflineTaskList extends React.Component<any, any> {
    state: any = {
        tasks: {
            data: []
        },
        loading: false,
        continue: false,
        current: 1,
        pageSize: 20,
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
        retryNumSort: '',
        visibleSlidePane: false,
        selectedTask: '',
        selectedRowKeys: [],
        expandedRowKeys: [],
        killJobVisible: false,
        searchType: 'fuzzy',
        projectUsers: [],
        appType: APPS_TYPE.BATCH,
        projectId: ''
    };

    componentDidMount () {
        const { appType, pid } = this.props.router.location?.query ?? {}
        const appTypeFormCookie = utils.getCookie(APP_TYPE_ID)
        const pidFormCookie = utils.getCookie(PROJECT_ID)
        const params = {
            appType: appTypeFormCookie ?? appType ?? APPS_TYPE.BATCH,
            projectId: pidFormCookie ?? pid ?? ''
        }
        this.setState({ ...params }, () => {
            this.search()
            this.getTaskTypesX()
            this.getProjectList()
        })
    }

    getTaskTypesX = () => {
        const ctx = this;
        let parmms = {
            appType: ctx.state.appType,
            projectId: ctx.state.projectId
        };
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

    getProjectList = (value?: string) => {
        const { dispatch } = this.props
        const { appType, projectId } = this.state
        dispatch(getProjectList({ name: value ?? '', appType, projectId }))
    }

    getReqParams = () => {
        const {
            jobName,
            person,
            taskStatus,
            bussinessDate,
            businessDateSort,
            jobType,
            current,
            taskType,
            taskPeriodId,
            execTimeSort,
            execStartSort,
            execEndSort,
            retryNumSort,
            cycSort,
            cycDate,
            searchType,
            pageSize,
            appType,
            projectId
            // taskTypeFilter
        } = this.state;
        const reqParams: any = {
            currentPage: current,
            pageSize,
            projectId,
            appType
        };
        if (jobName) {
            reqParams.taskName = jobName;
        }
        if (person) {
            reqParams.ownerId = person;
        }
        reqParams.appType = appType
        reqParams.projectId = projectId
        if (bussinessDate?.length > 1) {
            reqParams.bizStartDay = bussinessDate[0].unix();
            reqParams.bizEndDay = bussinessDate[1].unix();
        }
        if (cycDate?.length > 1) {
            reqParams.cycStartDay = cycDate[0].unix();
            reqParams.cycEndDay = cycDate[1].unix();
        }
        if (jobType !== undefined && jobType !== '') {
            reqParams.type = jobType;
        }
        if (taskStatus?.length > 0) {
            reqParams.jobStatuses = taskStatus.join(',');
        }
        if (taskType) {
            reqParams.taskType = taskType.join(',');
        }
        if (taskPeriodId) {
            reqParams.taskPeriodId = taskPeriodId.join(',');
        }

        reqParams.execTimeSort = execTimeSort || undefined;
        reqParams.execStartSort = execStartSort || undefined;
        reqParams.execEndSort = execEndSort || undefined;
        reqParams.cycSort = cycSort || undefined;
        reqParams.businessDateSort = businessDateSort || undefined;
        reqParams.retryNumSort = retryNumSort || undefined;
        reqParams.searchType = searchType;

        return reqParams;
    };

    search = () => {
        const reqParams = this.getReqParams();
        this.loadTaskList(reqParams);
    };

    loadTaskList (params: any) {
        // currentPage, pageSize, isTimeSortDesc, status
        const ctx = this;
        const { pageSize, current } = this.state;
        this.setState({ loading: true, expandedRowKeys: [] });
        const reqParams = Object.assign(
            {
                currentPage: current || 1,
                pageSize: pageSize || 20,
                type: 0
            },
            params
        );
        Api.queryJobs(reqParams).then((res: any) => {
            if (res.code === 1) {
                res.data.data = res.data.data || [];
                const expandedRowKeys: any = [];
                replaceObjectArrayFiledName(res.data.data, 'relatedJobs', 'children');
                for (let i = 0; i < res.data.data.length; i++) {
                    let job = res.data.data[i];
                    job.uid = createId(job.id);
                    if (job.batchTask && job.batchTask.taskType == TASK_TYPE.WORKFLOW) {
                        if (!job.children) {
                            job.children = [];
                        } else {
                            expandedRowKeys.push(job.uid);
                        }
                    }
                    // 工作流的子节点需要增加uid，否则会无法被选中
                    if (job.children) {
                        job.children.map((item: any) => {
                            item.uid = createId(item.id);
                        });
                    }
                }
                ctx.setState({ tasks: res.data, expandedRowKeys: expandedRowKeys });
            }
            ctx.setState({
                loading: false
            });
        });
        this.loadJobStatics(params);
    }

    loadJobStatics (params: any) {
        const ctx = this;
        // type:  NORMAL_SCHEDULE(0), FILL_DATA(1);
        params.type = 0;
        Api.queryJobStatics(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data });
            }
        });
    }

    batchKillJobs = () => {
        // 批量杀死
        const ctx = this;
        const selected = this.state.selectedRowKeys;

        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要杀死的任务！'
            });
            return;
        }
        if (this.canKill(selected)) {
            confirm({
                title: '确认提示',
                content: '确定要杀死选择的任务？',
                onOk () {
                    Api.batchStopJob({ jobIdList: selected.map((item: any) => item.split('_')[1]) }).then(
                        (res: any) => {
                            if (res.code === 1) {
                                ctx.setState({ selectedRowKeys: [], checkAll: false });
                                message.success('已经成功杀死所选任务！');
                                ctx.search();
                            }
                        }
                    );
                }
            });
        } else {
            warning({
                title: '提示',
                content: `
                   “失败、取消、成功、冻结”状态和“已删除”的任务，不能被杀死！
                `
            });
        }
    };
    // 批量按照业务日期杀死任务
    showKillJobsByDate = (show: any) => {
        this.setState({
            killJobVisible: show
        });
    };
    reloadCurrentJob = () => {
        // 重跑当前任务
        const ctx = this;
        const selected = this.state.selectedRowKeys;
        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要重跑的任务！'
            });
            return;
        }
        if (this.canReload(selected)) {
            confirm({
                title: '确认提示',
                content: '确认需要重跑当前选中的任务？',
                onOk () {
                    // 接口等待后端
                    Api.batchRestartAndResume({
                        jobIdList: selected.map((item: any) => item.split('_')[1]),
                        runCurrentJob: true
                    }).then((res: any) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑当前选中的任务！');
                            ctx.setState({ selectedRowKeys: [], checkAll: false });
                            ctx.search();
                        }
                    });
                }
            });
        } else {
            warning({
                title: '提示',
                content: `
                        只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                        请您重新选择!
                    `
            });
        }
    };

    batchReloadJobs = () => {
        // 批量重跑
        const ctx = this;
        const selected = this.state.selectedRowKeys;
        if (!selected || selected.length <= 0) {
            warning({
                title: '提示',
                content: '您没有选择任何需要重跑的任务！'
            });
            return;
        }
        if (this.canReload(selected)) {
            confirm({
                title: '确认提示',
                content: '确认需要重跑选择的任务及其全部下游任务？',
                onOk () {
                    Api.batchRestartAndResume({
                        jobIdList: selected.map((item: any) => item.split('_')[1]),
                        appType: ctx.state.appType,
                        projectId: ctx.state.projectId
                    }).then((res: any) => {
                        if (res.code === 1) {
                            message.success('已经成功重跑当前选中及其全部下游任务');
                            ctx.setState({ selectedRowKeys: [], checkAll: false });
                            ctx.search();
                        }
                    });
                }
            });
        } else {
            warning({
                title: '提示',
                content: `
                    只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                    请您重新选择!
                `
            });
        }
    };

    canReload = (ids: any) => {
        // 未运行、成功、失败/上游失败的任务可以reload
        const tasks = this.state.tasks.data;
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i];
                const res = tasks.find((task: any) => task.id === id);
                if (
                    res &&
                    res.status !== TASK_STATUS.WAIT_SUBMIT &&
                    res.status !== TASK_STATUS.FINISHED &&
                    res.status !== TASK_STATUS.RUN_FAILED &&
                    res.status !== TASK_STATUS.SUBMIT_FAILED &&
                    res.status !== TASK_STATUS.STOPED &&
                    res.status !== TASK_STATUS.KILLED &&
                    res.status !== TASK_STATUS.PARENT_FAILD
                ) { return false; }
            }
            return true;
        }
    };

    canKill = (ids: any) => {
        // 是否可以进行kill
        const tasks = this.state.tasks.data;
        if (ids && ids.length > 0) {
            for (let i = 0; i < ids.length; i++) {
                const id = ids[i];
                const res = tasks.find((task: any) => task.id === id);
                if (
                    res &&
                    ((res.status !== TASK_STATUS.WAIT_SUBMIT &&
                        res.status !== TASK_STATUS.SUBMITTING &&
                        res.status !== TASK_STATUS.WAIT_RUN &&
                        res.status !== TASK_STATUS.RUNNING) ||
                        res.batchTask.isDeleted === 1)
                ) { return false; }
            }
            return true;
        }
    };

    handleTableChange = (pagination: any, filters?: any, sorter?: any) => {
        const { current, pageSize } = this.state;
        const params: any = {
            current: current === pagination.current ? 1 : pagination.current,
            pageSize: pagination.pageSize || pageSize,
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
            cycSort: '',
            retryNumSort: ''
        };

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
                case 'batchEngineJob.retryNum': {
                    params.retryNumSort = order === 'descend' ? 'desc' : 'asc';
                    break;
                }
            }
        }
        this.setState(params, () => {
            this.search();
        });
    };

    changeTaskName = (v: any) => {
        this.setState({ jobName: v });
    };

    changeSearchType = (type: any) => {
        this.setState({ searchType: type });
        this.onSearchByTaskName();
    };

    onSearchByTaskName = () => {
        this.setState(
            {
                current: 1
            },
            this.search
        );
    };

    changePerson = (target: any) => {
        this.setState({ person: target, current: 1 }, () => {
            this.search();
        });
    };

    onSelectChange = (selectedRowKeys: any) => {
        this.setState({ selectedRowKeys });
    };

    onJobTypeChange = (value: any) => {
        this.setState({ jobType: value, current: 1 }, () => {
            this.search();
        });
    };

    changeBussinessDate = (value: any) => {
        this.setState({ bussinessDate: value, current: 1 }, () => {
            this.search();
        });
    };

    changeCycDate = (value: any) => {
        this.setState(
            {
                cycDate: value,
                current: 1
            },
            () => {
                this.search();
            }
        );
    };

    showTask = (task: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        });
    };

    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = [];
        const { expandedRowKeys } = this.state;

        const tasksRowKeys = (data: any) => {
            data.forEach((item: any) => {
                if (!(item.batchTask && item.batchTask.isDeleted === 1)) {
                    selectedRowKeys.push(item.uid);
                }
                if (expandedRowKeys.includes(item.uid) && item.children && item.children.length) {
                    tasksRowKeys(item.children);
                }
            });
        };
        if (e.target.checked) {
            tasksRowKeys(this.state.tasks.data);
        }
        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        });
    };

    changeProject = (value: string | number) => {
        this.setState({ projectId: value }, this.search)
    }

    initTaskColumns = () => {
        const { taskStatus, taskTypeFilter } = this.state;
        // const { taskTypeFilter } = this.props;

        return [
            {
                title: '任务名称',
                dataIndex: 'id',
                key: 'id',
                width: 300,
                render: (text: any, record: any) => {
                    let name = record.batchTask && record.batchTask.name;
                    let originText = name;
                    let showName: any;
                    if (record.batchTask.isDeleted === 1) {
                        showName = `${name} (已删除)`;
                    } else if (
                        get(record, 'batchEngineJob.retryNum') &&
                        [TASK_STATUS.WAIT_RUN, TASK_STATUS.RUNNING].indexOf(record.status) > -1
                    ) {
                        showName = (
                            <a
                                onClick={() => {
                                    this.showTask(record);
                                }}
                            >
                                {name}(重试)
                            </a>
                        );
                    } else {
                        showName = (
                            <a
                                onClick={() => {
                                    this.showTask(record);
                                }}
                            >
                                {name}
                            </a>
                        );
                    }
                    return <span title={originText}>{showName}</span>;
                },
                fixed: true
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 120,
                render: (text: any, record: any) => {
                    return (
                        <span>
                            <TaskStatus value={text} />
                            {record.isDirty && text == TASK_STATUS.FINISHED ? (
                                <Tooltip title="部分数据未同步成功，建议检查配置">
                                    <Icon type="info-circle-o" style={{ color: '#ee9b1e', marginLeft: '5px' }} />
                                </Tooltip>
                            ) : null}
                        </span>
                    );
                },
                filters: offlineTaskStatusFilter,
                filterMultiple: true,
                filteredValue: taskStatus
            },
            {
                title: '任务类型',
                dataIndex: 'taskType',
                key: 'taskType',
                render: (text: any, record: any) => {
                    return <TaskType value={record.batchTask && record.batchTask.taskType} />;
                },
                width: 100,
                filters: taskTypeFilter
            },
            {
                title: '调度周期',
                dataIndex: 'taskPeriodId',
                key: 'taskPeriodId',
                render: (text: any) => {
                    return <TaskTimeType value={text} />;
                },
                width: 100,
                filters: offlineTaskPeriodFilter
            },
            {
                title: '业务日期',
                dataIndex: 'businessDate',
                key: 'businessDate',
                width: 120,
                sorter: true
            },
            {
                title: '计划时间',
                dataIndex: 'cycTime',
                key: 'cycTime',
                width: 160,
                sorter: true
            },
            {
                title: '开始时间',
                dataIndex: 'execStartDate',
                key: 'execStartDate',
                width: 160,
                sorter: true
            },
            {
                title: '结束时间',
                dataIndex: 'execEndDate',
                key: 'execEndDate',
                width: 160,
                sorter: true
            },
            {
                title: '运行时长',
                dataIndex: 'execTime',
                key: 'execTime',
                width: 130,
                sorter: true
            },
            {
                title: '重试次数',
                dataIndex: 'batchEngineJob.retryNum',
                key: 'retryNum',
                width: 110,
                sorter: true
            },
            {
                title: '责任人',
                dataIndex: 'createUser',
                key: 'createUser',
                width: 200,
                fixed: 'right',
                render: (text: any, record: any) => {
                    return record.batchTask && record.batchTask.ownerUser && record.batchTask.ownerUser.userName;
                }
            }
        ];
    };

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedTask: null
        });
    };
    disabledDate = (current: any) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    };

    onExpandRows = (expandedRows: any) => {
        this.setState({ expandedRowKeys: expandedRows });
    };

    onExpand = (expanded: any, record: any) => {
        if (expanded) {
            const { tasks } = this.state;
            let newTasks = cloneDeep(tasks);
            const { jobId } = record;
            const reqParams = this.getReqParams();
            reqParams.jobId = jobId;
            Api.getRelatedJobs(reqParams).then((res: any) => {
                if (res.code == 1) {
                    let newData = newTasks.data.map((task: any) => {
                        if (task?.children.length && task?.children.some((item: any) => item.jobId == jobId)) {
                            task.children = task.children.map((element: any) => {
                                if (element.jobId == jobId) {
                                    element = {
                                        ...res.data,
                                        uid: element.uid,
                                        children:
                                            res.data.relatedJobs &&
                                            res.data.relatedJobs.map((ele: any) =>
                                                Object.assign({}, ele, { uid: createId(ele.id) })
                                            ),
                                        relatedJobs: undefined
                                    };
                                }
                                return element;
                            });
                        } else if (task.jobId == jobId) {
                            task = {
                                ...res.data,
                                uid: task.uid,
                                children:
                                    res.data.relatedJobs &&
                                    res.data.relatedJobs.map((ele: any) =>
                                        Object.assign({}, ele, { uid: createId(ele.id) })
                                    ),
                                relatedJobs: undefined
                            };
                        }
                        return task;
                    });
                    newTasks.data = newData;
                    this.setState({
                        tasks: newTasks
                    });
                }
            });
        } else {
            console.log('record');
        }
    };
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
            tasks, selectedRowKeys, jobName, bussinessDate, current, statistics, selectedTask,
            visibleSlidePane, cycDate, killJobVisible, searchType, pageSize,
            appType, projectId
        } = this.state;

        const { personList } = this.props;

        const pagination: any = {
            total: tasks.totalCount,
            showTotal: () => (
                <div>
                    共<span style={{ color: '#16ABF5' }}>{tasks.totalCount}</span>条数据
                </div>
            ),
            defaultPageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            pageSizeOptions: ['10', '20', '50', '100', '200'],
            current,
            pageSize,
            onChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {}),
            onShowSizeChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {})
        };

        const tableFooter = (currentPageData: any) => {
            const menu = (
                <Menu onClick={() => this.showKillJobsByDate(true)} style={{ width: 114 }}>
                    <Menu.Item key="1">按业务日期杀</Menu.Item>
                </Menu>
            );
            const reRunTaskMenu = (
                <Menu onClick={() => this.batchReloadJobs()}>
                    <Menu.Item key="1">重跑当前及全部下游任务</Menu.Item>
                </Menu>
            );
            return (
                <div className="felx-between">
                    <div style={{ display: 'inline-block' }}>
                        <Dropdown.Button
                            type="primary"
                            onClick={this.batchKillJobs}
                            overlay={menu}
                            trigger={['click']}
                            style={{ marginRight: 10, marginLeft: 26 }}
                            icon={<Icon type="down" />}
                        >
                            批量杀任务
                        </Dropdown.Button>
                        <Dropdown.Button
                            type="primary"
                            onClick={this.reloadCurrentJob}
                            overlay={reRunTaskMenu}
                            trigger={['click']}
                            icon={<Icon type="down" />}
                        >
                            重跑当前任务
                        </Dropdown.Button>
                    </div>
                    <Pagination {...pagination } style={{ top: 12 }} />
                </div>
            );
        };

        // rowSelection object indicates the need for row selection
        const rowSelection: any = {
            selectedRowKeys,
            onChange: this.onSelectChange,
            getCheckboxProps: (record: any) => ({
                disabled: record.batchTask && record.batchTask.isDeleted === 1
            })
        };
        const columns: any = this.initTaskColumns();
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

        const getTitle = (label: string) => {
            return <span className="form-label">{label}</span>
        }

        const clientHeight = document.documentElement.clientHeight - 330;

        return (
            <div className="c-taskOperation__wrap">
                <Form layout="inline" style={{ marginBottom: 12 }}>
                    <Row>
                        <Col span={23}>
                            <FormItem label="" className="batch-operation_offlineImg dt-form-shadow-bg">
                                <MultiSearchInput
                                    placeholder="按任务名称搜索"
                                    value={jobName}
                                    searchType={searchType}
                                    onChange={this.changeTaskName}
                                    onTypeChange={this.changeSearchType}
                                    onSearch={this.onSearchByTaskName}
                                />
                            </FormItem>
                            <FormItem label={getTitle('责任人')}>
                                <Select
                                    allowClear
                                    showSearch
                                    size="default"
                                    style={{ width: 210 }}
                                    placeholder="请选择责任人"
                                    optionFilterProp="name"
                                    className="dt-form-shadow-bg"
                                    onChange={this.changePerson}
                                >
                                    {personList.map((item: any) => {
                                        return <Option key={item.dtuicUserId} value={`${item.dtuicUserId}`}>{item.userName}</Option>
                                    })}
                                </Select>
                            </FormItem>
                            <FormItem label={getTitle('业务日期')}>
                                <div className="m-date-margin">
                                    <RangePicker
                                        size="default"
                                        style={{ width: 210 }}
                                        className="dt-form-shadow-bg"
                                        format="YYYY-MM-DD"
                                        disabledDate={this.disabledDate}
                                        ranges={{
                                            昨天: [(moment() as any).subtract(2, 'days'), yesterDay],
                                            最近7天: [(moment() as any).subtract(8, 'days'), yesterDay],
                                            最近30天: [(moment() as any).subtract(31, 'days'), yesterDay]
                                        }}
                                        value={bussinessDate || null}
                                        onChange={this.changeBussinessDate}
                                    />
                                </div>
                            </FormItem>
                            <FormItem label={getTitle('计划时间')}>
                                <div className="m-date-margin">
                                    <RangePicker
                                        size="default"
                                        style={{ width: 210 }}
                                        className="dt-form-shadow-bg"
                                        showTime
                                        format="YYYY/MM/DD HH:mm:ss"
                                        ranges={{
                                            今天: [moment() as any, moment() as any],
                                            最近7天: [(moment() as any).subtract(7, 'days'), moment() as any],
                                            最近30天: [(moment() as any).subtract(30, 'days'), moment() as any]
                                        }}
                                        value={cycDate || null}
                                        onChange={this.changeCycDate}
                                        onOk={this.search}
                                    />
                                </div>
                            </FormItem>
                        </Col>
                        <Col span={1}>
                            <div className="office__refresh_normal">
                                <Tooltip title="刷新数据">
                                    <Icon
                                        type="sync"
                                        onClick={this.search}
                                        className="refresh__icon_normal"
                                    />
                                </Tooltip>
                            </div>
                        </Col>
                    </Row>
                </Form>
                <div className="c-taskOperation__wrap__tab">
                    <div className="ope-statistics" style={{ padding: '16px 10px' }}>
                        {this.renderStatus(statusList)}
                    </div>
                    <Table
                        rowKey="uid"
                        rowClassName={(record: any, index: any) => {
                            if (this.state.selectedTask && this.state.selectedTask.id == record.id) {
                                return 'row-select';
                            } else {
                                return '';
                            }
                        }}
                        style={{ height: 'calc(100vh - 218px)' }}
                        className="dt-table-fixed-contain-footer"
                        expandedRowKeys={this.state.expandedRowKeys}
                        rowSelection={rowSelection}
                        pagination={false}
                        loading={this.state.loading}
                        columns={columns}
                        dataSource={tasks.data || []}
                        onChange={this.handleTableChange}
                        footer={tableFooter}
                        onExpand={this.onExpand}
                        onExpandedRowsChange={this.onExpandRows}
                        scroll={{ x: 1709.6, y: clientHeight }}
                    />
                    <SlidePane
                        className="m-tabs bd-top bd-right m-slide-pane"
                        onClose={this.closeSlidePane}
                        visible={visibleSlidePane}
                        style={{
                            right: '0px',
                            width: '60%',
                            height: '100%',
                            minHeight: '600px',
                            position: 'fixed',
                            paddingTop: '64px'
                        }}
                    >
                        <TaskJobFlowView
                            visibleSlidePane={visibleSlidePane}
                            goToTaskDev={this.props.goToTaskDev}
                            reload={this.search}
                            taskJob={selectedTask}
                        />
                    </SlidePane>

                    <KillJobForm
                        visible={killJobVisible}
                        appType={appType}
                        projectId={projectId}
                        autoFresh={this.search}
                        onCancel={() => this.showKillJobsByDate(false)}
                    />
                </div>
            </div>
        );
    }
}

export default connect(
    (state: any) => {
        return {
            projectList: state.operation.projectList,
            personList: state.operation.personList
        }
    }
)(OfflineTaskList);
