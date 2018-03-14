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
    Checkbox, Tabs,
 } from 'antd'

 import utils from 'utils'
 import SlidePane from 'widgets/slidePane'

 import Api from '../../../api'
import { taskStatusFilter } from '../../../comm/const'
import { TaskTimeType, TaskType } from '../../../components/status'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

import PatchDataModal from './patchDataModal'
import TaskView from './taskView'
import TaskRuntime from './taskRuntime'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

const FormItem = Form.Item
const Option = Select.Option
const Search = Input.Search
const TabPane = Tabs.TabPane
const RangePicker = DatePicker.RangePicker

class OfflineTaskMana extends Component {

    state = {
        tasks: {
            data: [],
        },
        loading: false,
        patchDataVisible: false,
        visibleSlidePane: false,
        checkAll: false,

        current: 1, // 当前页
        person: '',
        taskName: utils.getParameterByName('tname') ? utils.getParameterByName('tname') : '',
        selectedTask: '',
        startTime: '',
        endTime: '',
        selectedRowKeys: [],
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
            reqParams.startTime = startTime
            reqParams.endTime = endTime
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
            pageSize: 20,
        }, params)
        Api.queryOfflineTasks(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data })
            }
            this.setState({ loading: false })
        })
    }

    forzenTasks = (mode) => {
        const ctx = this
        const selected = this.state.selectedRowKeys
       
        if (!selected || selected.length <= 0) {
            message.error('您没有选择任何任务！')
            return false;
        }
       
        Api.forzenTask({
            taskIdList: selected, 
            scheduleStatus: mode  //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ selectedRowKeys: [] })
                ctx.search()
            }
        })
    }

    handleTableChange = (pagination, filters) => {
        const params = {}
        if (filters.status) {
            params.status = filters.status[0]
        }
        params.currentPage = pagination.current
        this.setState({ 
            checkAll: false, 
            selectedRowKeys: [], 
            current: pagination.current, 
        })
        this.loadTaskList(params)
    }

    clickPatchData = (task) => {
        this.setState({
            patchDataVisible: true,
            selectedTask: task
        })
    }

    showTask = (task) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        })
    }

    clickMenu = (target) => {
        const task = target.item.props.value
        const { workbench } = this.props
        if (target.key === 'edit') {
            this.props.goToTaskDev(task.id)
        }
    }

    changePerson = (target) => { // 责任人变更
        this.setState({ person: target, current: 1 }, this.search)
    }

    changeTaskName = (e) => {// 任务名变更
        this.setState({ taskName: e.target.value })
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []
        if (e.target.checked) {
            selectedRowKeys = this.state.tasks.data.map(item => item.id)
        }

        this.setState({
            selectedRowKeys,
            checkAll: e.target.checked,
        })
    }

    onCheckChange = (checkedList) => {
        const { user } = this.props;
        const conditions = {
            person: '',
            startTime: '',
            endTime: '',
            scheduleStatus: 1,
        };
        checkedList.forEach(item => {
            if (item === 'person') {
                conditions.person  = user.id;
            } else if (item === 'todayUpdate') {
                conditions.startTime = moment().set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0,
                }).unix()
                conditions.endTime = moment().set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59,
                }).unix()
            } else if (item === 'stopped') {
                conditions.scheduleStatus = 2; // 任务状态(1:正常 2：冻结)
            }
        })

        this.setState(conditions, this.search)
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
        })
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            width: 120,
            render: (text, record) => {
                return <a onClick={() => { this.showTask(record) }}>{record.name}</a>
            },
        }, {
            title: '发布时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text) => {
                return <span>{utils.formatDateTime(text)}</span>
            },
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text) => {
                return <TaskType value={text}/>
            },
        }, {
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text) => {
                return <TaskTimeType value={text} />
            },
        }, {
            title: '责任人',
            dataIndex: 'userName',
            key: 'userName',
            render: (text, record) => {
                return <span>{record.createUser && record.createUser.userName}</span>
            },
        }, {
            title: '操作',
            key: 'operation',
            width: 120,
            render: (text, record) => {
                return (
                    <span>
                        <a onClick={()=> {this.clickPatchData(record)}}>补数据</a>
                        <span className="ant-divider"></span>
                        <a onClick={()=> {this.props.goToTaskDev(record.id)}}>修改</a>
                    </span>
                )
            },
        }]
    }

    tableFooter = (currentPageData) => {
        return (
            <Row>
                <Col className="inline" style={{ padding: '15px 10px 10px 30px' }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </Col>
                <Col className="inline" style={{ paddingLeft: '15px' }}>
                    <Button 
                        size="small"
                        type="primary" 
                        onClick={this.forzenTasks.bind(this, 2)}
                    >
                        冻结
                    </Button>
                    <Button 
                        size="small"
                        onClick={this.forzenTasks.bind(this, 1)}
                    >
                        解冻
                    </Button>
                </Col>
            </Row>
        )
    }

    render() {
        const { projectUsers, project } = this.props
        const { 
            tasks, patchDataVisible, selectedTask, 
            current, taskName, visibleSlidePane, selectedRowKeys
        } = this.state;

        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []

        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current,
        };

        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys
                })
            },
            selectedRowKeys: selectedRowKeys,
        };

        return (
            <div className="box-1 m-card">
                <Card 
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Form
                            style={{marginTop: '10px'}}
                            className="m-form-inline"
                            layout="inline"
                        >
                            <FormItem label="">
                                <Search
                                    placeholder="按任务名称"
                                    style={{ width: 150 }}
                                    value={taskName}
                                    size="default"
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
                            <FormItem>
                                <Checkbox.Group onChange={this.onCheckChange} >
                                    <Checkbox value="person">我的任务</Checkbox>
                                    <Checkbox value="todayUpdate">今日修改</Checkbox>
                                    <Checkbox value="stopped">冻结的任务</Checkbox>
                                </Checkbox.Group>
                            </FormItem>
                        </Form>
                    }
                >
                    <Table
                        rowKey="id"
                        style={{marginTop: '1px'}}
                        className="m-table"
                        pagination={pagination}
                        rowSelection={rowSelection} 
                        loading={this.state.loading}
                        columns={this.initTaskColumns()}
                        dataSource={tasks.data || []}
                        onChange={this.handleTableChange}
                        footer={this.tableFooter}
                    />
                    <SlidePane 
                        className="m-tabs bd-top bd-right m-slide-pane"
                        onClose={ this.closeSlidePane }
                        visible={ visibleSlidePane } 
                        style={{ right: '0px', width: '80%', height: '100%', minHeight: '600px'  }}
                    >
                        <Tabs animated={false}>
                            <TabPane tab="依赖视图" key="taskFlow"> 
                                <TaskView 
                                    visibleSlidePane={visibleSlidePane}
                                    goToTaskDev={this.props.goToTaskDev} 
                                    clickPatchData={this.clickPatchData}
                                    tabData={selectedTask}
                                />
                            </TabPane>
                            <TabPane tab="运行报告" key="runTime"> 
                                <TaskRuntime 
                                    visibleSlidePane={visibleSlidePane}
                                    tabData={selectedTask} 
                                />
                            </TabPane>
                        </Tabs>
                    </SlidePane>
                </Card>
                <PatchDataModal
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
        user: state.user,
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineTaskMana)
