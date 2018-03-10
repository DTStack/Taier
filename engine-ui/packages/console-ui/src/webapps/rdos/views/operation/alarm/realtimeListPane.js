import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Table, Row, Col, Select, Card,
    Input, Button, DatePicker, Form,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { AlarmTriggerType, TaskType } from '../../../components/status'

const RangePicker = DatePicker.RangePicker
const Option = Select.Option
const FormItem = Form.Item

class RealTimePanel extends Component {

    state = {
        alarmRecords: { data: [] },
        loading: false,
        startTime: '',
        endTime: '',
        taskName: '',
        alarmPe: '',
        statistics: '',
    }

    componentDidMount() {
        this.loadAlarms({ pageIndex: 1 })
        this.loadAlarmStatistics()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadAlarms({ pageIndex: 1 })
            this.loadAlarmStatistics()
        }
    }

    loadAlarms(reqParams) {
        const ctx = this
        this.setState({ loading: true })
        Api.getAlarmRecords(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ alarmRecords: res.data || [], loading: false })
            }
        })
    }

    loadAlarmStatistics() {
        const ctx = this
        Api.getAlarmStatistics().then((res) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    search = () => {
        const { startTime, endTime, taskName, alarmPe } = this.state
        const params = { pageIndex: 1 }
        if (startTime && endTime) {
            params.startTime = startTime.unix()
            params.endTime = endTime.unix()
        }
        if (taskName) { params.taskName = taskName }
        if (alarmPe) { params.receive = alarmPe }
        this.loadAlarms(params)
    }

    timeRangeChange = (date) => {
        if (date.length > 1) {
            this.setState({
                startTime: date[0].toDate().getTime(),
                endTime: date[1].toDate().getTime(),
            })
        }
    }

    rangeTimeChange = (date) => { // 缺少时间过滤条件
        this.setState({
            startTime: date[0],
            endTime: date[1],
        })
    }

    handleTableChange = (pagination, filters) => {
        if (filters.status) {
            this.loadAlarms({
                pageIndex: pagination.current,
                status: filters.status[0],
            })
        } else {
            this.loadAlarms({
                pageIndex: pagination.current,
            })
        }
    }


    changeReceive = (target) => {
        this.setState({ alarmPe: target })
    }

    changeTaskName = (evt) => {
        this.setState({ taskName: evt.target.value })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > new Date().getTime();
    }

    initColumns = () => {
        return [{
            title: '时间',
            dataIndex: 'time',
            key: 'time',
            render: (text) => {
                return utils.formatDateTime(text)
            },
        }, {
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
        }, {
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
            title: '任务责任人',
            dataIndex: 'taskCreateUser',
            key: 'taskCreateUser',
        }, {
            title: '告警接收人',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
            render: (text, record) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map(item => item.userName)
                }
                return ''
            },
        }, {
            title: '告警内容',
            dataIndex: 'alarmContent',
            key: 'alarmContent',
        }]
    }

    render() {
        const { statistics, alarmRecords } = this.state
        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (<Option key={item.id} value={`${item.user.dtuicUserId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []
        const pagination = {
            total: alarmRecords.totalCount || 0,
            defaultPageSize: 10,
        };
        return (
            <div className="m-card">
                <Row className="m-count box-1">
                    <Col span={6}>
                        <section className="m-count-section">
                            <span className="m-count-title">今日</span>
                            <span className="m-count-content font-organge">{statistics.today || 0}</span>
                        </section>
                    </Col>
                    <Col span={12}>
                        <section className="m-count-section">
                            <span className="m-count-title">近7天</span>
                            <span className="m-count-content font-blue">{statistics.week || 0}</span>
                        </section>
                    </Col>
                    <Col span={6}>
                        <section className="m-count-section">
                            <span className="m-count-title">近30天</span>
                            <span className="m-count-content font-darkgreen">{statistics.month || 0}</span>
                        </section>
                    </Col>
                </Row>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Form className="m-form-inline"  layout="inline">
                            <FormItem label="告警时间">
                                <RangePicker
                                    style={{ width: 200 }}
                                    disabledDate={this.disabledDate}
                                    format="YYYY-MM-DD"
                                    size="default"
                                    onChange={this.rangeTimeChange}
                                />
                            </FormItem>
                            <FormItem label="任务名称">
                            <Input
                                placeholder="任务名称"
                                size="default"
                                allowClear
                                style={{ width: 120 }}
                                onChange={this.changeTaskName} 
                            />
                            </FormItem>
                            <FormItem
                                label="告警接收人"
                            >
                                <Select
                                    allowClear
                                    showSearch
                                    style={{ width: 120 }}
                                    placeholder="请选择接收人"
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
                                    onClick={this.search}>
                                    搜索
                                </Button>
                            </FormItem>
                        </Form>
                    }
                >
                   <Table
                        rowKey="id"
                        key="realtimeAlarmList"
                        className="m-table"
                        pagination={pagination}
                        loading={this.state.loading}
                        columns={this.initColumns()}
                        onChange={this.handleTableChange}
                        dataSource={alarmRecords.data}
                    />
                </Card>
            </div>
        )
    }
}

export default connect((state) => {
    return {
        projectUsers: state.projectUsers,
        project: state.project,
    }
})(RealTimePanel)
