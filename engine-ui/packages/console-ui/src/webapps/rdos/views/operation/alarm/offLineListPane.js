import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Table, Row, Col, Select, Card,
    Input, Button, DatePicker, Form,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import {
    AlarmTriggerType, 
    TaskType, 
    AlarmTypes,
} from '../../../components/status'

const RangePicker = DatePicker.RangePicker
const Option = Select.Option
const FormItem = Form.Item

class OfflinePanel extends Component {

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
        Api.getOfflineAlarmRecords(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ alarmRecords: res.data || [], loading: false })
            }
        })
    }

    loadAlarmStatistics() {
        const ctx = this
        Api.getOfflineAlarmStatistics().then((res) => {
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
            width: 150,
            title: '时间',
            dataIndex: 'time',
            key: 'time',
            render: (text) => {
                return utils.formateDateTime(text)
            },
        }, {
            title: '任务名称',
            width: 100,
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
            width: 100,
            title: '触发方式',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
            render: (text) => {
                return <AlarmTriggerType value={text} />
            },
        }, {
            title: '任务责任人',
            width: 100,
            dataIndex: 'taskCreateUser',
            key: 'taskCreateUser',
        }, {
            width: 100,
            title: '告警方式',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
            render: (data) => {
                return <AlarmTypes value={data} />
            }
        }, {
            width: 100,
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
            total: alarmRecords.totalCount || 0,
            defaultPageSize: 10,
        };
        return (
            <div className="row-item">
                <h2>告警记录-离线任务</h2>
                <Row className="row-col-padding txt-center" style={{ margin: '20px 0' }}>
                    <Col span={8} className="mini-card">
                        <div className="bd card-content">
                            <p className="count warning">{statistics.today || 0}</p>
                            <p>今日告警</p>
                        </div>
                    </Col>
                    <Col span={8} className="mini-card">
                        <div className="bd card-content">
                            <p className="count warning">{statistics.week || 0}</p>
                            <p>本周告警</p>
                        </div>
                    </Col>
                    <Col span={8} className="mini-card">
                        <div className="bd card-content">
                            <p className="count warning">{statistics.month || 0}</p>
                            <p>本月告警</p>
                        </div>
                    </Col>
                </Row>
                <Row style={{ margin: '24px 0' }}>
                    <Form layout="inline">
                        <FormItem
                            label="告警时间"
                            style={{paddingBottom: '24px'}}
                        >
                            <RangePicker
                                style={{ width: 200 }}
                                disabledDate={this.disabledDate}
                                format="YYYY-MM-DD"
                                onChange={this.rangeTimeChange}
                            />
                        </FormItem>
                        <br/>
                        <FormItem
                            label="任务名称"
                        >
                            <Input
                                placeholder="任务名称"
                                style={{ width: 200 }}
                                onChange={this.changeTaskName} 
                            />
                        </FormItem>
                        <FormItem
                            label="告警接收人"
                        >
                            <Select
                                allowClear
                                showSearch
                                style={{ width: 200 }}
                                placeholder="请选择接收人"
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
                  key="offlineAlarmList"
                  className="bd"
                  pagination={pagination}
                  loading={this.state.loading}
                  columns={this.initColumns()}
                  onChange={this.handleTableChange}
                  dataSource={alarmRecords.data}
                />
            </div>
        )
    }
}

export default connect((state) => {
    return {
        projectUsers: state.projectUsers,
        project: state.project,
    }
})(OfflinePanel)
