import * as React from 'react'
import { connect } from 'react-redux'
import {
    Table, Row, Col, Select, Card,
    Input, Button, DatePicker, Form
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import {
    AlarmTriggerType,
    TaskType,
    AlarmTypes
} from '../../../components/status'
const RangePicker = DatePicker.RangePicker
const Option = Select.Option
const FormItem = Form.Item

class OfflinePanel extends React.Component<any, any> {
    state: any = {
        alarmRecords: { data: [] },
        loading: false,
        startTime: '',
        endTime: '',
        taskName: '',
        alarmPe: '',
        statistics: '',
        current: 1
    }

    componentDidMount () {
        this.loadAlarms({ pageIndex: 1 })
        this.loadAlarmStatistics()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadAlarms({ pageIndex: 1 })
            this.loadAlarmStatistics()
        }
    }

    loadAlarms (reqParams: any) {
        const ctx = this
        this.setState({ loading: true })
        Api.getOfflineAlarmRecords(reqParams).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ alarmRecords: res.data || [], loading: false })
            }
        })
    }

    loadAlarmStatistics () {
        const ctx = this
        Api.getOfflineAlarmStatistics().then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ statistics: res.data })
            }
        })
    }

    search = () => {
        const { startTime, endTime, taskName, alarmPe, current } = this.state
        const params: any = { pageIndex: current }
        if (startTime && endTime) {
            params.startTime = startTime.unix()
            params.endTime = endTime.unix()
        }
        if (taskName) { params.taskName = taskName }
        if (alarmPe) { params.receive = alarmPe }

        this.loadAlarms(params)
    }

    rangeTimeChange = (date: any) => { // 缺少时间过滤条件
        const start = (date && date[0]) ? date[0].set({
            'hour': 0,
            'minute': 0,
            'second': 0
        }) : ''
        const end = (date && date[1]) ? date[1].set({
            'hour': 23,
            'minute': 59,
            'second': 59
        }) : ''
        this.setState({
            startTime: start,
            endTime: end,
            current: 1
        }, () => {
            this.search();
        })
    }

    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            current: pagination.current
        }, this.search)
    }

    changeReceive = (target: any) => {
        this.setState({ alarmPe: target, current: 1 }
            , () => {
                this.search();
            })
    }

    changeTaskName = (evt: any) => {
        this.setState({ taskName: evt.target.value, current: 1 })
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > new Date().getTime();
    }

    initColumns = () => {
        return [{
            width: 150,
            title: '时间',
            dataIndex: 'time',
            key: 'time',
            render: (text: any) => {
                return utils.formatDateTime(text)
            }
        }, {
            title: '任务名称',
            width: 100,
            dataIndex: 'taskName',
            key: 'taskName'
        }, {
            width: 80,
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text: any) => {
                return <TaskType value={text} />
            }
        }, {
            width: 100,
            title: '触发方式',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
            render: (text: any) => {
                return <AlarmTriggerType value={text} />
            }
        }, {
            title: '任务责任人',
            width: 100,
            dataIndex: 'taskCreateUser',
            key: 'taskCreateUser'
        }, {
            width: 100,
            title: '告警方式',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
            render: (data: any) => {
                return <AlarmTypes value={data} />
            }
        }, {
            width: 100,
            title: '告警接收人',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
            render: (text: any, record: any) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map((item: any) => item.userName).join(', ')
                }
                return ''
            }
        }, {
            title: '告警内容',
            dataIndex: 'alarmContent',
            key: 'alarmContent'
        }]
    }

    render () {
        const { statistics, alarmRecords } = this.state
        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item: any) => {
                return (
                    <Option
                        key={item.id}
                        value={`${item.user.id}`}
                    >
                        {item.user.userName}
                    </Option>
                )
            }) : []
        const pagination: any = {
            total: alarmRecords.totalCount || 0,
            defaultPageSize: 10
        };
        return (
            <div className="m-card" style={{ paddingTop: '5px' }}>
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
                        <section className="m-count-section" style={{ width: '50px' }}>
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
                        <Form className="m-form-inline" layout="inline">
                            <FormItem
                                label="告警时间"
                            >
                                <RangePicker
                                    style={{ width: 200 }}
                                    disabledDate={this.disabledDate}
                                    format="YYYY-MM-DD"
                                    size="default"
                                    onChange={this.rangeTimeChange}
                                />
                            </FormItem>
                            <FormItem
                                label="任务名称"
                            >
                                <Input
                                    size="default"
                                    placeholder="任务名称"
                                    style={{ width: 126 }}
                                    onChange={this.changeTaskName}
                                    onPressEnter={this.search}
                                />
                            </FormItem>
                            <FormItem
                                label="告警接收人"
                            >
                                <Select
                                    allowClear
                                    showSearch
                                    size='default'
                                    style={{ width: 126 }}
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
                                    onClick={this.search}
                                >
                                    搜索
                                </Button>
                            </FormItem>
                        </Form>
                    }
                >
                    <Table
                        rowKey="id"
                        key="offlineAlarmList"
                        className="dt-ant-table dt-ant-table--border rdos-ant-table-placeholder"
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

export default connect((state: any) => {
    return {
        projectUsers: state.projectUsers,
        project: state.project
    }
})(OfflinePanel)
