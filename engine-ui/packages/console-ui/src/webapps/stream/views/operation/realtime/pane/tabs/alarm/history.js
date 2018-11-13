import React from "react"
import utils from "utils";

import { DatePicker, Table } from "antd";

import {
    AlarmTriggerType
} from '../../../../../../components/status'
import Api from '../../../../../../api'



const { RangePicker } = DatePicker;

class AlarmHistory extends React.Component {

    state = {
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        },
        alarmRecords: [],
        loading: false,
        times: []
    }

    componentDidMount() {
        this.loadAlarms();
    }
    componentWillReceiveProps(nextProps) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.loadAlarms(data)
        }
    }
    loadAlarms(data) {
        const { pagination, times } = this.state;
        data = data || this.props.data || {};
        const reqParams = {
            ...pagination,
            pageIndex: pagination.current,
            taskId: data.id,
            startTime: times.length ? times[0].valueOf() : undefined,
            endTime: times.length ? times[1].valueOf() : undefined,
        }
        this.setState({ loading: true })
        Api.getAlarmRecords(reqParams).then((res) => {
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
    handleTableChange = (pagination, filters) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: pagination.current,
            }
        }, this.loadAlarms)
    }
    changeTimes(times) {
        this.setState({
            times: times
        }, this.loadAlarms)
    }
    initHistoryColumns() {
        return [{
            title: '时间',
            dataIndex: 'time',
            render: (text) => {
                return utils.formatDateTime(text)
            },
        }, {
            title: '触发方式',
            dataIndex: 'myTrigger',
            render: (text) => {
                return <AlarmTriggerType value={text} />
            },
        }, {
            title: '接收人',
            dataIndex: 'receiveUsers',
            render: (text, record) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map(item => <span>{item.userName};</span>)
                }
                return ''
            },
        }, {
            title: '告警内容',
            dataIndex: 'alarmContent',
            width: "400px"
        }]
    }
    render() {
        const { pagination, times, loading, alarmRecords } = this.state;
        return (
            <section className="pane-alarm-configList">
                <header>
                    告警历史
                    </header>

                <RangePicker
                    showTime={{ format: "HH:mm" }}
                    style={{ width: "250px", marginBottom: "11px" }}
                    format="YYYY-MM-DD HH:mm"
                    placeholder={["告警开始时间", "告警结束时间"]}
                    onChange={this.changeTimes.bind(this)}
                    value={times}
                    disabledDate={(current) => {
                        return current && current.valueOf() > new Date().getTime();
                    }}
                />

                <Table
                    rowKey={(record,index)=>{
                        return index;
                    }}
                    className="m-table"
                    columns={this.initHistoryColumns()}
                    dataSource={alarmRecords||[]}
                    loading={loading}
                    pagination={pagination}
                    onChange={this.handleTableChange.bind(this)}
                />
            </section>
        )
    }
}

export default AlarmHistory;