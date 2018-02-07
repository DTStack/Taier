import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import moment from 'moment'

import {
    Table, message, Modal,
    Row, Col, Card, Input,
    Radio, Button, Select,
    Menu, Dropdown, Icon,
    DatePicker, Tag, Form,
 } from 'antd'

 import utils from 'utils'

 import Api from '../../../api'
import { taskStatusFilter } from '../../../comm/const'
import { TaskTimeType, TaskType } from '../../../components/status'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

import PatchData from './patchData'
import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

const FormItem = Form.Item
const Option = Select.Option
const Search = Input.Search
const RangePicker = DatePicker.RangePicker

class OfflineTaskMana extends Component {

    state = {
        tasks: {
            data: [],
        },
        loading: false,
        current: 1, // 当前页
        patchDataVisible: false,
        person: '',
        taskName: utils.getParameterByName('tname') ? utils.getParameterByName('tname') : '',
        selectedTask: '',
        startTime: '',
        endTime: '',
    }

    componentDidMount() {
        if (this.props.project.id !== 0) {
            this.search()
        }
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (project && oldProj.id !== project.id) {
            this.setState({current: 1}, () => {
                this.search()
            })
        }
    }

    search = () => {
        const reqParams = {}
        const {
            taskName, person,
            startTime, endTime,
        } = this.state
        if (taskName) {
            reqParams.name = taskName
        }
        if (startTime && endTime) {
            reqParams.startTime = startTime.unix()
            reqParams.endTime = endTime.unix()
        }
        if (person) {
            reqParams.ownerId = person
        }
        this.loadTaskList(reqParams)
    }

    loadTaskList(params) { // currentPage, pageSize, isMine, status
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            currentPage: 1,
            pageSize: 10,
        }, params)
        Api.queryOfflineTasks(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data, loading: false })
            }
        })
    }

    handleTableChange = (pagination, filters) => {
        const params = {}
        if (filters.status) {
            params.status = filters.status[0]
        }
        params.currentPage = pagination.current
        this.setState({ current: pagination.current })
        this.loadTaskList(params)
    }

    chooseTask = (task) => {
        const { dispatch, router } = this.props
        Api.getTask({ taskId: task.id }).then((res) => {
            if (res.code === 1) {
                dispatch(BrowserAction.newPage(res.data))
                router.push('/realtime/task')
            }
        })
    }

    clickMenu = (target) => {
        const task = target.item.props.value
        const { workbench } = this.props
        if (target.key === 'patch') {
            this.setState({
                patchDataVisible: true,
                selectedTask: task
            })
        } else if (target.key === 'edit') {
            this.props.goToTaskDev(task.id)
        }
    }

    changePerson = (target) => { // 责任人变更
        this.setState({ person: target, current: 1 }, this.search)
    }

    changeTaskName = (e) => {// 任务名变更
        this.setState({ taskName: e.target.value })
    }

    rangeTimeChange = (date) => { // 缺少时间过滤条件

        const start = date.length >= 0 ? date[0] : ''
        const end = date.length >= 1 ? date[1] : ''
        
        this.setState({
            startTime: start,
            endTime: end,
            current: 1,
        }, this.search)
        
    }

    disabledDate = (current) => {
        return current && current.valueOf() > new Date().getTime();
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => {
                const schedule = JSON.parse(record.scheduleConf)
                return (
                    <article style={{fontSize: '12px'}}>
                        <div>
                            <TaskTimeType value={record.taskPeriodId} />
                            <a onClick={() => { this.props.goToTaskDev(record.id) }}>{record.name}</a>&nbsp;
                            <span>({record.createUser && record.createUser.userName})</span>&nbsp;
                            <i className="i"><TaskType value={record.taskType}/></i>
                        </div>
                        <div style={{marginTop: '5px'}}>
                            调度生效日期：{schedule.beginDate} ~ {schedule.endDate}&nbsp;
                            具体调度时间：{record.cron}
                        </div>
                    </article>
                )
            },
        }, {
            title: '修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text) => {
                return <span>{utils.formateDateTime(text)}</span>
            },
        }, {
            title: '操作',
            key: 'operation',
            render: (text, record) => {
               const menu = (
                    <Menu onClick={this.clickMenu}>
                        <Menu.Item key="edit" value={record}>修改</Menu.Item>
                        <Menu.Item key="patch" value={record}>补数据</Menu.Item>
                        <Menu.Item key="execTime">
                           <Link to={`/operation/task-runtime/${record.id}`}>运行时间</Link>
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
        const { projectUsers } = this.props
        const { tasks, patchDataVisible, selectedTask, current, taskName } = this.state
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={item.userId} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []
        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 10,
            current,
        };
        return (
            <div className="operation-content">
                <article className="section">
                    <div style={{ paddingBottom: '15px' }}>
                        <table className="bd my-table">
                            <tr>
                                <td colSpan={2}>
                                    <Form layout="inline">
                                        <FormItem
                                            label="修改时间"
                                        >
                                        <RangePicker
                                            style={{ width: 200 }}
                                            disabledDate={this.disabledDate}
                                            format="YYYY-MM-DD"
                                            onChange={this.rangeTimeChange}
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
                                                <Option key={0} value={0} name="全部">
                                                    全部
                                                </Option>
                                                {userItems}
                                            </Select>
                                        </FormItem>
                                        <FormItem label="">
                                            <Search
                                                placeholder="按任务名称搜索"
                                                style={{ width: 150 }}
                                                value={taskName}
                                                onChange={this.changeTaskName}
                                                onSearch={this.search}
                                            />
                                        </FormItem>
                                    </Form>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <Table
                      rowKey="id"
                      className="section-border"
                      pagination={pagination}
                      loading={this.state.loading}
                      columns={this.initTaskColumns()}
                      dataSource={tasks.data || []}
                      onChange={this.handleTableChange}
                    />
                </article>
                <PatchData
                  visible={patchDataVisible}
                  task={selectedTask}
                  handCancel={() => { this.setState({ patchDataVisible: false }) }}
                />
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
        projectUsers: state.projectUsers,
        workbench: state.workbench,
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineTaskMana)
