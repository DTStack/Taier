import * as React from 'react'
import utils from 'utils';

import { DatePicker, Table } from 'antd';

import {
    AlarmTriggerType
} from '../../../../../../components/status'
import Api from '../../../../../../api'

const { RangePicker } = DatePicker;

class AlarmHistory extends React.Component<any, any> {
    state: any = {
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        },
        alarmRecords: [],
        loading: false,
        times: []
    }

    componentDidMount () {
        this.loadAlarms();
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.loadAlarms(data)
        }
    }
    loadAlarms (data: any) {
        const { pagination, times } = this.state;
        data = data || this.props.data || {};
        const reqParams: any = {
            ...pagination,
            pageIndex: pagination.current,
            taskId: data.id,
            startTime: times.length ? times[0].valueOf() : undefined,
            endTime: times.length ? times[1].valueOf() : undefined
        }
        this.setState({ loading: true })
        Api.getAlarmRecords(reqParams).then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code == 1) {
                this.setState({
                    alarmRecords: res.data.data || [],
                    pagination: {
                        ...pagination,
                        total: res.data.totalCount
                    }
                })
            }
        })
    }
    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: pagination.current
            }
        }, this.loadAlarms)
    }
    changeTimes (times: any) {
        this.setState({
            times: times
        }, this.loadAlarms)
    }
    initHistoryColumns () {
        return [{
            title: '时间',
            dataIndex: 'time',
            render: (text: any) => {
                return utils.formatDateTime(text)
            }
        }, {
            title: '触发方式',
            dataIndex: 'myTrigger',
            render: (text: any) => {
                return <AlarmTriggerType value={text} />
            }
        }, {
            title: '接收人',
            dataIndex: 'receiveUsers',
            render: (text: any, record: any, index: any) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map((item: any) => <span key={index}>{item.userName};</span>)
                }
                return ''
            }
        }, {
            title: '告警内容',
            dataIndex: 'alarmContent',
            width: '400px'
        }]
    }
    render () {
        const { pagination, times, loading, alarmRecords } = this.state;
        return (
            <section className="pane-alarm-configList">
                <header>
                    告警历史
                </header>

                <RangePicker
                    showTime={{ format: 'HH:mm' }}
                    style={{ width: '250px', marginBottom: '11px' }}
                    format="YYYY-MM-DD HH:mm"
                    placeholder={['告警开始时间', '告警结束时间']}
                    onChange={this.changeTimes.bind(this)}
                    value={times}
                    disabledDate={(current: any) => {
                        return current && current.valueOf() > new Date().getTime();
                    }}
                />

                <Table
                    rowKey={(record: any, index: any) => {
                        return index;
                    }}
                    className="m-table border-table"
                    columns={this.initHistoryColumns()}
                    dataSource={alarmRecords || []}
                    loading={loading}
                    pagination={pagination}
                    onChange={this.handleTableChange.bind(this)}
                />
            </section>
        )
    }
}

export default AlarmHistory;
