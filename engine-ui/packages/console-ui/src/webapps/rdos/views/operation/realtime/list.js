import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { isEmpty } from "lodash"
import {
    Table, message, Modal,
    Input, Card, Popconfirm,
    DatePicker, TimePicker,
    Select, Form,Tooltip,Icon
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { taskStatusFilter, TASK_STATUS } from '../../../comm/const'
import { TaskStatus } from '../../../components/status'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

import LogInfo from './logInfo'
import GoOnTask from './goOnTask'

const Search = Input.Search
const confirm = Modal.confirm
const { RangePicker } = DatePicker
const FormItem = Form.Item

class RealTimeTaskList extends Component {

    state = {
        tasks: {
            data: [],
        },
        filter: isEmpty(utils.getParameterByName("status")) ? [] : [utils.getParameterByName("status")],
        loading: false,
        continue: false,
        logVisible: false,
        current: 1,
        taskName: utils.getParameterByName('tname') || '',
        goOnTask: '',
        logInfo: '',
    }

    componentDidMount() {
        if (this.props.project.id !== 0) {
            this.loadTaskList()
        }
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadTaskList()
        }
    }

    searchTask = (query) => {
        this.setState({
            taskName: query,
        }, this.loadTaskList)
    }

    onChange = (e) => {
        this.setState({
            continue: e.target.value,
        });
    }

    loadTaskList(params) { // currentPage, pageSize, isTimeSortDesc, status
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            currentPage: 1,
            pageSize: 20,
            taskName: this.state.taskName,
            isTimeSortDesc: true,
            status: this.state.filter[0]
        }, params)
        Api.getTasks(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data })
            }
            ctx.setState({ loading: false })
        })
    }

    updateTaskStatus = (task, mode) => {
        const ctx = this
        const current = this.state.current
        const status = task.status
        const stopArr = [4, 16, 17, 11, 10];
        const startArr = [0, 7, 8, 9];
        const isRestore = status === 7 || status === 8 || status === 0 ? 1 : 0
        
        switch(status){
            case TASK_STATUS.WAIT_SUBMIT:
            case TASK_STATUS.STOPED:
            case TASK_STATUS.RUN_FAILED:
            case TASK_STATUS.SUBMIT_FAILED:{
                if (mode !== 'normal' && (status === 7 || status === 8)) { // 续跑
                    this.setState({ goOnTask: task.id })
                } else {
                    Api.startTask({
                        id: task.id,
                        isRestoration: isRestore,
                    }).then((res) => {
                        if (res.code === 1) {
                            message.success('任务操作成功！')
                            ctx.loadTaskList({ pageIndex: current })
                        }
                    })
                }
                break;
            }
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.SUBMITTING:
            case TASK_STATUS.RESTARTING:
            case TASK_STATUS.WAIT_RUN:
            case TASK_STATUS.WAIT_COMPUTE:{
                Api.stopTask({
                    id: task.id,
                }).then((res) => {
                    if (res.code === 1) {
                        message.success('任务已执行停止！')
                        ctx.loadTaskList({ pageIndex: current })
                    }
                })
                break;
            }
        }
    }

    recoverTask(task) {
        const ctx = this
        Api.startTask({
            id: task.id,
            isRestoration: 0,
        }).then((res) => {
            if (res.code === 1) {
                message.success('任务操作成功！')
                ctx.loadTaskList()
            }
        })
    }

    handleTableChange = (pagination, filters) => {
        const params = {}
        if (filters.status) {
            params.status = filters.status[0]
        }
        params.currentPage = pagination.current
        this.setState({ current: pagination.current, filter: filters.status })
        this.loadTaskList(params)
    }

    logInfo = (task) => {
        Api.getTaskLogs({ taskId: task.taskId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    logInfo: res.data,
                    logVisible: true,
                })
            }
        })
    }

    chooseTask = (task) => {
        this.props.dispatch(BrowserAction.openPage({
            id: task.id,
        }))
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => {
                return <a onClick={() => { this.logInfo(record) }}>{text}</a>
            },
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text) => {
                return <span style={{ fontSize: '12px' }}>{text === 0 ? 'SQL任务' : 'MR任务'}</span>
            },
        }, {
            title: '全部状态',
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
                return <TaskStatus value={text} />
            },
            filters: taskStatusFilter,
            filteredValue: this.state.filter,
            filterMultiple: false,
        }, {
            title: '责任人',
            dataIndex: 'createUserName',
            key: 'createUserName',
        }, {
            title: '最近操作时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
            sorter: (a, b) => a.gmtModified - b.gmtModified,
        }, {
            title: '最近操作人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
        }, {
            title: '操作',
            width: 120,
            key: 'operation',
            render: (text, record) => {
                let normal = ''
                let recover = ''
                let goOn = ''
                let popTxt = '确定执行当前操作吗?'
                switch (record.status) {
                    case TASK_STATUS.WAIT_SUBMIT:
                        normal = '提交'
                        break;
                    case TASK_STATUS.FINISHED:
                        recover = <a>重跑</a>
                        popTxt = '重跑，则任务将丢弃停止前的状态，重新运行'
                        break;
                    case TASK_STATUS.STOPED:
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

                return (
                    <div key={record.id}>
                        <a onClick={() => { this.chooseTask(record) }}>修改</a>
                        {goOn ? <span className="ant-divider" /> : ''}
                        <a onClick={() => { this.updateTaskStatus(record) }}>{goOn}</a>
                        {normal ? <span className="ant-divider" /> : ''}
                        <a onClick={() => { this.updateTaskStatus(record, 'normal') }}>{normal}</a>
                        {recover ? <span className="ant-divider" /> : ''}
                        <Popconfirm
                            okText="确定"
                            cancelText="取消"
                            onConfirm={() => { this.recoverTask(record) }}
                            title={popTxt}
                        >
                            {recover}
                        </Popconfirm>
                    </div>
                )
            },
        }]
    }

    hideGoOnTask = () => {
        this.setState({
            goOnTask: ''
        })
    }

    render() {
        const { tasks, logInfo } = this.state
        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 10,
        };
        return (
            <div className="box-1 m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Search
                            placeholder="按任务名称搜索"
                            style={{ width: 200, marginTop: '10px' }}
                            defaultValue={utils.getParameterByName('tname') || ''}
                            onSearch={this.searchTask}
                        />
                    }
                    extra={
                        <Tooltip title="刷新数据">
                            <Icon type="sync" onClick={this.loadTaskList.bind(this,null)}
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
                        className="m-table"
                        style={{ marginTop: '1px' }}
                        pagination={pagination}
                        loading={this.state.loading}
                        columns={this.initTaskColumns()}
                        dataSource={tasks.data || []}
                        onChange={this.handleTableChange}
                    />
                    <GoOnTask
                        visible={this.state.goOnTask ? true : false}
                        taskId={this.state.goOnTask}
                        onOk={this.hideGoOnTask}
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
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
    }
})(RealTimeTaskList)
