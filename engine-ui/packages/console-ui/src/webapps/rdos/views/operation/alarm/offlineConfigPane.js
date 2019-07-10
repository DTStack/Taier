import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    Table, Select, Form, Card,
    Input, Button, message, Popconfirm
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { AlarmStatusFilter } from '../../../comm/const'

import {
    AlarmStatus, AlarmTriggerType, AlarmTypes, TaskType
} from '../../../components/status'

import AlarmForm from './offLineAlarmForm'

const Option = Select.Option
const FormItem = Form.Item

class OfflineConfig extends Component {
    state = {
        configs: { data: [] },
        taskList: [],
        loading: false,
        visible: false,
        visibleEdit: false,
        alarmInfo: '',
        taskName: '',
        alarmPeo: '',
        alarmStatus: '',
        current: 1
    }

    componentDidMount () {
        this.loadAlarmRules()
        this.loadTaskList()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadAlarmRules()
            this.loadTaskList()
        }
    }

    loadAlarmRules (params) {
        const ctx = this
        const reqForm = params || { pageIndex: 1 }
        this.setState({ loading: true })
        Api.getOfflineAlarmList(reqForm).then((res) => {
            ctx.setState({ configs: res.data || {}, loading: false })
        })
    }

    search = () => {
        const { taskName, alarmPeo, current, alarmStatus } = this.state
        const params = { pageIndex: current }
        if (taskName) { params.taskName = taskName }
        if (alarmPeo) { params.ownerId = alarmPeo }
        if (alarmStatus !== '') { params.alarmStatus = alarmStatus }
        this.loadAlarmRules(params)
    }

    handleTableChange = (pagination, filters) => {
        this.setState({
            current: pagination.current,
            alarmStatus: filters.alarmStatus ? filters.alarmStatus[0] : ''
        }, this.search)
    }

    addAlarm = async (alarm) => {
        try {
            let res = await Api.addOfflineAlarm(alarm);
            if (res.code === 1) {
                message.success('添加告警成功！')
                this.setState({ visible: false })
                this.loadAlarmRules();
                return res;
            }
            return null;
        } catch (e) {
            return null;
        }
    }

    deleteAlarm (alarm) {
        const ctx = this
        Api.deleteOfflineAlarm(alarm).then((res) => {
            if (res.code === 1) {
                message.success('删除告警成功！')
                ctx.loadAlarmRules()
            }
        })
    }

    updateAlarmStatus (alarm) {
        const ctx = this
        const params = { alarmId: alarm.alarmId }
        if (alarm.alarmStatus === 0) {
            Api.closeOfflineAlarm(params).then((res) => {
                if (res.code === 1) {
                    message.success('关闭告警成功！')
                    ctx.loadAlarmRules()
                }
            })
        } else if (alarm.alarmStatus === 1) {
            Api.openOfflineAlarm(params).then((res) => {
                if (res.code === 1) {
                    message.success('开启告警成功！')
                    ctx.loadAlarmRules()
                }
            })
        }
    }

    updateAlarm = async (alarm) => {
        alarm.id = this.state.alarmInfo.alarmId
        try {
            let res = await Api.updateOfflineAlarm(alarm);
            if (res.code === 1) {
                message.success('告警更新成功！')
                this.setState({ visibleEdit: false })
                this.loadAlarmRules();
                return res;
            }
            return null;
        } catch (e) {
            return null;
        }
    }

    loadTaskList = () => {
        const ctx = this
        const params = {
            pageIndex: 1,
            pageSize: 1000
        }
        Api.getOfflineTasksByProject(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({
                    taskList: res.data || [],
                    loading: false
                })
            }
        })
    }

    changeTaskName = (evt) => {
        this.setState({ taskName: evt.target.value, current: 1 })
    }

    changeReceive = (target) => {
        this.setState({ alarmPeo: target, current: 1 })
    }

    initEdit = (alarm) => {
        this.setState({ alarmInfo: alarm, visibleEdit: true })
    }

    initColumns = () => {
        return [{
            title: '告警名称',
            dataIndex: 'alarmName',
            key: 'alarmName'
        }, {
            width: 80,
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName'
        }, {
            width: 80,
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text) => {
                return <TaskType value={text} />
            }
        }, {
            width: 80,
            title: '触发方式',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
            render: (text) => {
                return <AlarmTriggerType value={text} />
            }
        }, {
            width: 80,
            title: '告警方式',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
            render: (data) => {
                return <AlarmTypes value={data} />
            }
        }, {
            title: '告警接收人',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
            render: (text, record) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map((item, index) => <span key={index}>{item.userName};</span>)
                }
                return ''
            }
        }, {
            title: '状态',
            dataIndex: 'alarmStatus',
            key: 'alarmStatus',
            render: (text) => {
                return <AlarmStatus value={text} />
            },
            filters: AlarmStatusFilter,
            filterMultiple: false
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: text => utils.formatDateTime(text)
        }, {
            title: '创建人',
            dataIndex: 'createUser',
            key: 'createUser'
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
                let isOpen = ''
                if (record.alarmStatus === 1) {
                    isOpen = '开启'
                } else if (record.alarmStatus === 0) {
                    isOpen = '关闭'
                }
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除这条告警吗?"
                            onConfirm={() => { this.deleteAlarm(record) }}
                            okText="确定"
                            cancelText="取消"
                        >
                            <a>删除</a>
                        </Popconfirm>
                        <span className="ant-divider" />
                        <a onClick={() => { this.updateAlarmStatus(record) }}>{isOpen}</a>
                    </div>
                )
            }
        }]
    }

    render () {
        const { visible, loading, configs,
            alarmInfo, taskList, visibleEdit
        } = this.state

        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (
                    <Option
                        key={item.id}
                        value={`${item.user.id}`}
                        name={item.user.userName}
                    >
                        {item.user.userName}
                    </Option>
                )
            }) : []

        const pagination = {
            total: configs.totalCount,
            defaultPageSize: 10
        };
        return (
            <div className="m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Form
                            className="m-form-inline"
                            layout="inline"
                            style={{ marginTop: '10px' }}
                        >
                            <FormItem label="任务名称">
                                <Input
                                    style={{ width: 126 }}
                                    placeholder="任务名称"
                                    size="default"

                                    onChange={this.changeTaskName}
                                />
                            </FormItem>
                            <FormItem>
                                <Select
                                    allowClear
                                    showSearch
                                    size='Default'
                                    style={{ width: 126 }}
                                    placeholder="选择创建人"
                                    optionFilterProp="name"
                                    onChange={this.changeReceive}
                                >
                                    {userItems}
                                </Select>
                            </FormItem>
                            <FormItem>
                                <Button
                                    size="default"
                                    type="primary"
                                    onClick={this.search}
                                >
                                    搜索
                                </Button>
                            </FormItem>
                        </Form>
                    }
                    extra={
                        <Button
                            style={{ marginTop: '10px' }}
                            type="primary"
                            onClick={() => { this.setState({ visible: true }) }}
                        >
                            添加告警
                        </Button>
                    }
                >
                    <Table
                        rowKey="alarmId"
                        key="offlineConfig"
                        className="m-table"
                        pagination={pagination}
                        loading={loading}
                        columns={this.initColumns()}
                        onChange={this.handleTableChange}
                        dataSource={configs.data || []}
                    />
                </Card>
                <AlarmForm
                    {...this.props}
                    title="修改告警规则"
                    alarmInfo={alarmInfo}
                    taskList={taskList}
                    wrapClassName="vertical-center-modal"
                    visible={visibleEdit}
                    onOk={this.updateAlarm}
                    onCancel={() => { this.setState({ visibleEdit: false }) }}
                />
                <AlarmForm
                    {...this.props}
                    title="创建告警规则"
                    alarmInfo={{}}
                    taskList={taskList}
                    wrapClassName="vertical-center-modal"
                    visible={visible}
                    onOk={this.addAlarm}
                    onCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        )
    }
}
export default connect((state) => {
    return {
        projectUsers: state.projectUsers,
        project: state.project,
        user: state.user
    }
})(OfflineConfig)
