import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    Table, Row, Col, Select, Form,
    Input, Button, message, Popconfirm,
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
    }

    componentDidMount() {
        this.loadAlarmRules()
        this.loadTaskList()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadAlarmRules()
            this.loadTaskList()
        }
    }

    loadAlarmRules(params) {
        const ctx = this
        const reqForm = params || { pageIndex: 1 }
        this.setState({ loading: true })
        Api.getOfflineAlarmList(reqForm).then((res) => {
            ctx.setState({ configs: res.data || {}, loading: false })
        })
    }

    search = () => {
        const { taskName, alarmPeo } = this.state
        const params = { pageIndex: 1 }
        if (taskName) { params.taskName = taskName }
        if (alarmPeo) { params.ownerId = alarmPeo }
        this.loadAlarmRules(params)
    }

    handleTableChange = (pagination, filters) => {
        const params = { pageIndex: pagination.current }
        if (filters.alarmStatus) {
            params.alarmStatus = filters.alarmStatus[0]
        }
        this.loadAlarmRules(params)
    }

    addAlarm = (alarm) => {
        const ctx = this
        Api.addOfflineAlarm(alarm).then((res) => {
            if (res.code === 1) {
                ctx.setState({ visible: false })
                ctx.loadAlarmRules()
            }
        })
    }

    deleteAlarm(alarm) {
        const ctx = this
        Api.deleteOfflineAlarm(alarm).then((res) => {
            if (res.code === 1) {
                message.success('删除告警成功！')
                ctx.loadAlarmRules()
            }
        })
    }

    updateAlarmStatus(alarm) {
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

    updateAlarm = (alarm) => {
        const ctx = this
        alarm.id = this.state.alarmInfo.alarmId
        Api.updateOfflineAlarm(alarm).then((res) => {
            if (res.code === 1) {
                message.success('告警更新成功！')
                ctx.setState({ visibleEdit: false })
                ctx.loadAlarmRules()
            }
        })
    }

    loadTaskList = () => {
        const ctx = this
        const params = {
            pageIndex: 1,
            pageSize: 100,
        }
        Api.getOfflineTasksByProject(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({
                    taskList: res.data || [],
                    loading: false,
                })
            }
        })
    }

    changeTaskName = (evt) => {
        this.setState({ taskName: evt.target.value })
    }

    changeReceive = (target) => {
        this.setState({ alarmPeo: target })
    }

    initEdit = (alarm) => {
        this.setState({ alarmInfo: alarm, visibleEdit: true })
    }

    initColumns = () => {
        return [{
            title: '告警名称',
            dataIndex: 'alarmName',
            key: 'alarmName',
        }, {
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
            width: 80,
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text) => {
                return <TaskType value={text} />
            },
        }, {
            title: '触发方式',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
            render: (text) => {
                return <AlarmTriggerType value={text} />
            },
        }, {
            width: 100,
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
                    return recivers.map(item => <span>{item.userName};</span>)
                }
                return ''
            },
        }, {
            title: '状态',
            dataIndex: 'alarmStatus',
            key: 'alarmStatus',
            render: (text) => {
                return <AlarmStatus value={text} />
            },
            filters: AlarmStatusFilter,
            filterMultiple: false,
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: text => utils.formatDateTime(text),
        }, {
            title: '创建人',
            dataIndex: 'createUser',
            key: 'createUser',
        }, {
            width: 120,
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
            },
        }]
    }

    render() {

        const { visible, loading, configs,
            alarmInfo, taskList, visibleEdit,
        } = this.state

        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (
                <Option 
                    key={item.id} 
                    value={`${item.userId}`} 
                    name={item.user.userName}
                >
                    {item.user.userName}
                </Option>
            )
        }) : []

        const pagination = {
            total: configs.totalCount,
            defaultPageSize: 10,
        };
        return (
            <div className="row-item">
                <h2>
                    自定义告警-离线任务
                    <Button
                      style={{ marginTop: '-5px' }}
                      type="primary"
                      className="right"
                      onClick={() => { this.setState({ visible: true }) }}
                    >
                      添加告警
                    </Button>
                </h2>
                <Row style={{ margin: '24px 0' }}>
                   <Form layout="inline">
                        <FormItem
                            label="任务名称"
                        >
                            <Input
                                style={{ width: 200 }}
                                placeholder="任务名称"
                                onChange={this.changeTaskName}
                            />
                        </FormItem>
                        <FormItem>
                             <Select
                                allowClear
                                showSearch
                                style={{ width: 200 }}
                                placeholder="选择创建人"
                                optionFilterProp="name"
                                onChange={this.changeReceive}
                            >
                                {userItems}
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" onClick={this.search}>搜索</Button>
                        </FormItem>
                    </Form>
                </Row>
                <Table
                  rowKey="id"
                  key="offlineConfig"
                  className="bd"
                  pagination={pagination}
                  loading={loading}
                  columns={this.initColumns()}
                  onChange={this.handleTableChange}
                  dataSource={configs.data || []}
                />
                <AlarmForm
                  {...this.props}
                  title="修改离线任务告警规则"
                  alarmInfo={alarmInfo}
                  taskList={taskList}
                  wrapClassName="vertical-center-modal"
                  visible={visibleEdit}
                  onOk={this.updateAlarm}
                  onCancel={() => { this.setState({ visibleEdit: false }) }}
                />
                <AlarmForm
                  {...this.props}
                  title="创建离线任务告警规则"
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
        user: state.user,
    }
})(OfflineConfig)
