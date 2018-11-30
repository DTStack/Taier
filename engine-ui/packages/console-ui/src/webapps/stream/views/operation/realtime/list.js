import React, { Component } from 'react'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router';
import moment from 'moment'
import { isEmpty } from 'lodash'
import {
    Table, message, Modal, Button,
    Input, Card, Popconfirm,
    DatePicker, TimePicker,
    Select, Form, Tooltip, Icon
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { taskStatusFilter, TASK_STATUS, TASK_TYPE } from '../../../comm/const'
import { TaskStatus, TaskStatusOverview } from '../../../components/status'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

import DetailPane from './pane'
import LogInfo from './logInfo'
import GoOnTask from './goOnTask'

const Search = Input.Search
const confirm = Modal.confirm
const { RangePicker } = DatePicker
const FormItem = Form.Item

class RealTimeTaskList extends Component {
    state = {
        tasks: {
            data: []
        },
        filter: {},
        loading: false,
        continue: false,
        logVisible: false,
        visibleSlidePane: false,
        selectTask: null,
        current: 1,
        taskName: utils.getParameterByName('tname') || '',
        goOnTask: '',
        logInfo: '',
        overview: {},
        taskTypes: [],
        sorter: {}
    }

    componentDidMount () {
        if (this.props.project.id !== 0) {
            const { location } = this.props.router || {};
            const { state = {} } = location || {};

            this.loadTaskTypes();
            this.loadCount();

            this.setState({
                filter: state && state.statusList ? { status: state.statusList } : {}
            }, () => {
                this.loadTaskList()
            })
        }
    }

    loadTaskTypes = () => {
        Api.getRealtimeTaskTypes().then(res => {
            if (res.code === 1) {
                this.setState({
                    taskTypes: res.data || []
                })
            }
        })
    }

    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            if (!this.state.taskTypes || !this.state.taskTypes.length) {
                this.loadTaskTypes();
            }
            this.setState({
                visibleSlidePane: false,
                selectTask: null
            })
            this.loadTaskList()
            this.loadCount();
        }
    }

    componentWillUnmount () {
        clearTimeout(this._timeClock);
        this._isUnmounted = true;
    }

    loadCount () {
        Api.taskStatistics({
            taskName: this.state.taskName
        }).then(
            (res) => {
                if (res.code == 1) {
                    this.setState({
                        overview: res.data
                    })
                }
            }
        )
    }

    searchTask = (query) => {
        this.closeSlidePane();
        this.setState({
            taskName: query
        }, () => {
            this.loadTaskList();
            this.loadCount();
        })
    }
    /**
     * 这里判断是否需要自动刷新，
     * 当有等待提交之类的状态，则自动刷新
     */
    debounceLoadtask (resData = {}) {
        if (this._isUnmounted) {
            return;
        }
        const { data } = resData;
        if (!data) {
            return;
        }
        let haveRun = false;
        let haveRunList = [
            TASK_STATUS.RUNNING,
            TASK_STATUS.STOPING,
            TASK_STATUS.SUBMITTING,
            TASK_STATUS.RESTARTING,
            TASK_STATUS.WAIT_RUN,
            TASK_STATUS.WAIT_COMPUTE
        ];
        for (let i = 0; i < data.length; i++) {
            let status = data[i].status;
            if (haveRunList.indexOf(status) > -1) {
                haveRun = true;
                break;
            }
        }
        if (!haveRun) {
            return;
        }
        this._timeClock = setTimeout(() => {
            this.loadTaskList(null, true);
            this.loadCount();
        }, 5000);
    }
    onChange = (e) => {
        this.setState({
            continue: e.target.value
        });
    }
    exchangeOrderKey (key) {
        const orderMap = {
            gmtModified: 'gmt_modified'
        }
        return orderMap[key];
    }
    loadTaskList (params, isSilent) { // currentPage, pageSize, isTimeSortDesc, status
        const ctx = this
        if (!isSilent || typeof isSilent != 'boolean') {
            this.setState({ loading: true })
        }
        const { sorter = {}, current, pageSize } = ctx.state;
        const reqParams = Object.assign({
            currentPage: current,
            pageSize: 20,
            taskName: this.state.taskName,
            isTimeSortDesc: true,
            statusList: this.state.filter.status,
            type: this.state.filter.taskType,
            orderBy: this.exchangeOrderKey(sorter.columnKey),
            sort: utils.exchangeOrder(sorter.order)
        }, params)
        clearTimeout(this._timeClock);
        Api.getTasks(reqParams).then((res) => {
            if (res.code === 1) {
                this.debounceLoadtask(res.data);
                ctx.setState({ tasks: res.data })
            }
            ctx.setState({ loading: false })
        })
    }

    updateTaskStatus = (task, mode) => {
        const ctx = this
        const current = this.state.current
        const status = task.status
        const isRestore = status === TASK_STATUS.STOPED ||
            status === TASK_STATUS.RUN_FAILED ||
            status === TASK_STATUS.WAIT_SUBMIT ||
            status === TASK_STATUS.SUBMIT_FAILED
            ? 1 : 0

        switch (status) {
        case TASK_STATUS.WAIT_SUBMIT:
        case TASK_STATUS.STOPED:
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.KILLED:
        case TASK_STATUS.SUBMIT_FAILED: {
            if (mode !== 'normal' && (status === TASK_STATUS.STOPED || status === TASK_STATUS.RUN_FAILED)) { // 续跑
                if (task.taskType == TASK_TYPE.DATA_COLLECTION) {
                    Api.startTask({
                        id: task.id,
                        isRestoration: 0
                    }).then((res) => {
                        if (res.code === 1) {
                            message.success('续跑操作成功！')
                            ctx.loadTaskList({ pageIndex: current })
                            ctx.loadCount();
                        }
                    })
                } else {
                    this.setState({ goOnTask: task.id })
                }
            } else {
                Api.startTask({
                    id: task.id,
                    isRestoration: isRestore
                }).then((res) => {
                    if (res.code === 1) {
                        message.success('任务操作成功！')
                        ctx.loadTaskList({ pageIndex: current })
                        ctx.loadCount();
                    }
                })
            }
            break;
        }
        case TASK_STATUS.RUNNING:
        case TASK_STATUS.SUBMITTING:
        case TASK_STATUS.RESTARTING:
        case TASK_STATUS.WAIT_RUN:
        case TASK_STATUS.WAIT_COMPUTE: {
            Api.stopTask({
                id: task.id
            }).then((res) => {
                if (res.code === 1) {
                    message.success('任务正在停止！')
                    ctx.loadTaskList({ pageIndex: current })
                    ctx.loadCount();
                }
            })
            break;
        }
        }
    }

    recoverTask (task) {
        const ctx = this
        Api.startTask({
            id: task.id,
            isRestoration: 0
        }).then((res) => {
            if (res.code === 1) {
                message.success('任务操作成功！')
                ctx.loadTaskList()
                ctx.loadCount();
            }
        })
    }

    handleTableChange = (pagination, filters, sorter) => {
        const { location } = this.props.router;
        if (location.state) {
            hashHistory.replace({
                pathname: location.pathname
            })
        }
        this.closeSlidePane();
        this.setState({
            current: pagination.current,
            filter: filters,
            sorter: sorter
        }, this.loadTaskList.bind(this))
    }

    logInfo = (task) => {
        Api.getTaskLogs({ taskId: task.taskId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    logInfo: res.data,
                    logVisible: true
                })
            }
        })
    }

    chooseTask = (record, index) => {
        this.setState({
            selectTask: record.id,
            visibleSlidePane: true
        })
    }

    openTask = (task) => {
        this.props.dispatch(BrowserAction.openPage({
            id: task.id
        }))
    }

    closeSlidePane () {
        this.setState({
            visibleSlidePane: false,
            selectTask: null
        })
    }

    initTaskColumns = () => {
        const { taskTypes, filter } = this.state;
        let taskTypesMap = {};
        taskTypes.forEach((type) => {
            taskTypesMap[type.key] = type.value;
        })
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            width: 150,
            render: (text, record, index) => {
                return <a onClick={() => { this.chooseTask(record, index) }}>{text}</a>
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 180,
            render: (text) => {
                return <TaskStatus value={text} />
            },
            filters: taskStatusFilter,
            filteredValue: filter.status,
            filterMultiple: true
        }, {
            title: '业务延时',
            dataIndex: 'bizDelay',
            key: 'bizDelay',
            width: 150,
            render (text) {
                return utils.formatTime(text);
            }
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            width: 150,
            render: (text) => {
                return taskTypesMap[text];
            },
            filters: taskTypes.map((taskType) => {
                return {
                    text: taskType.value,
                    value: taskType.key
                }
            }),
            filterMultiple: true
        }, {
            title: '责任人',
            dataIndex: 'createUserName',
            key: 'createUserName',
            width: 200
        }, {
            title: '最近操作时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            width: 150,
            render: text => utils.formatDateTime(text),
            sorter: true
        }, {
            title: '最近操作人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: 150
        }, {
            title: '操作',
            width: 150,
            key: 'operation',
            render: (text, record) => {
                return this.getDealButton(record)
            }
        }]
    }

    getDealButton (record, isPane) {
        if (!record) {
            return null;
        }
        let normal = ''
        let recover = ''
        let goOn = ''
        let popTxt = '确定执行当前操作吗?'
        switch (record.status) {
        case TASK_STATUS.WAIT_SUBMIT:
        case TASK_STATUS.SUBMIT_FAILED:
            normal = '提交'
            break;
        case TASK_STATUS.FINISHED:
            recover = <a>重跑</a>
            popTxt = '重跑，则任务将丢弃停止前的状态，重新运行'
            break;
        case TASK_STATUS.STOPED:
        case TASK_STATUS.KILLED:
            goOn = '续跑'
            popTxt = '重跑，则任务将丢弃停止前的状态，重新运行'
            recover = <a>重跑</a>
            break;
        case TASK_STATUS.RUN_FAILED:
            goOn = '续跑'
            normal = '重试'
            break;
        case TASK_STATUS.SUBMIT_FAILED:
            normal = '重试'
            break;
        case TASK_STATUS.RUNNING:
        case TASK_STATUS.WAIT_RUN:
        case TASK_STATUS.WAIT_COMPUTE:
        case TASK_STATUS.SUBMITTING:
        case TASK_STATUS.RESTARTING:
            normal = '停止'
            break;
        default:
            break;
        }

        if (record.taskType == TASK_TYPE.DATA_COLLECTION) {
            normal = normal == '重试' ? null : normal;
            recover = null;
        }
        if (isPane) {
            return (
                <span className="buttonMargin">
                    <Button type="primary" onClick={() => { this.openTask(record) }}>修改</Button>
                    {goOn ? <Button type="primary" onClick={() => { this.updateTaskStatus(record) }}>{goOn}</Button> : null}
                    {normal ? <Button type="primary" onClick={() => { this.updateTaskStatus(record, 'normal') }}>{normal}</Button> : null}
                    {recover ? <Popconfirm
                        okText="确定"
                        cancelText="取消"
                        onConfirm={() => { this.recoverTask(record) }}
                        title={popTxt}
                    >
                        <Button type="primary">{recover}</Button>
                    </Popconfirm> : null}
                </span>
            )
        } else {
            let arr = [];

            arr.push(<a onClick={() => { this.openTask(record) }}>修改</a>)
            goOn && arr.push(<a onClick={() => { this.updateTaskStatus(record) }}>{goOn}</a>)
            normal && arr.push(<a onClick={() => { this.updateTaskStatus(record, 'normal') }}>{normal}</a>)
            recover && arr.push(<Popconfirm
                okText="确定"
                cancelText="取消"
                onConfirm={() => { this.recoverTask(record) }}
                title={popTxt}
            >
                {recover}
            </Popconfirm>)

            /**
             * 在每个按钮之间插入间隔符
             */
            arr = arr.reduce((one, two) => {
                if (one.length) {
                    return one.concat(<span className="ant-divider" />, two);
                }
                return one.concat(two);
            }, [])

            return (
                <div key={record.id}>
                    {arr}
                </div>
            )
        }
    }

    hideGoOnTask = () => {
        this.setState({
            goOnTask: ''
        })
    }
    goOnTaskSuccess = () => {
        this.hideGoOnTask();
        this.loadTaskList()
        this.loadCount()
    }
    render () {
        const { tasks, logInfo, selectTask, overview, current } = this.state
        const dataSource = tasks.data || [];
        const detailPaneData = dataSource.find((item) => {
            return item.id == selectTask
        }) || {};
        const pagination = {
            total: tasks.totalCount,
            pageSize: 20,
            current: current
        };
        return (
            <div className="box-1 m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div>
                            <Search
                                placeholder="按任务名称搜索"
                                style={{ width: 200, marginTop: '10px' }}
                                defaultValue={utils.getParameterByName('tname') || ''}
                                onSearch={this.searchTask}
                            />
                            <TaskStatusOverview data={overview} />
                        </div>
                    }
                    extra={
                        <Tooltip title="刷新数据">
                            <Icon type="sync" onClick={() => {
                                this.loadCount();
                                this.loadTaskList()
                            }}
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
                        className="m-table full-screen-table-90"
                        rowClassName={
                            (record, index) => {
                                if (selectTask == index) {
                                    return 'row-select'
                                } else {
                                    return '';
                                }
                            }
                        }
                        style={{ marginTop: '1px' }}
                        pagination={pagination}
                        loading={this.state.loading}
                        columns={this.initTaskColumns()}
                        dataSource={dataSource}
                        onChange={this.handleTableChange}
                    />
                    <GoOnTask
                        visible={!!this.state.goOnTask}
                        taskId={this.state.goOnTask}
                        onOk={this.goOnTaskSuccess}
                        onCancel={this.hideGoOnTask}
                    />
                </Card>
                <Modal
                    width="60%"
                    title={`日志-${logInfo.taskId}`}
                    wrapClassName="vertical-center-modal modal-body-nopadding m-log-modal"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                >
                    <LogInfo log={logInfo} height="520px" />
                </Modal>
                <DetailPane
                    data={detailPaneData}
                    visibleSlidePane={this.state.visibleSlidePane}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                    extButton={this.getDealButton(detailPaneData, true)}
                />
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project
    }
})(RealTimeTaskList)
