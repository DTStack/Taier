import * as React from 'react'
import utils from 'utils'
import moment from 'moment'
import { range } from 'lodash'

import { Table, DatePicker, TimePicker } from 'antd'

import Api from '../../../../../api'
import { disableRangeCreater } from 'funcs';

class CheckPoint extends React.Component<any, any> {
    state: any = {
        day: moment(),
        beginTime: moment('00:00:00', 'HH:mm:ss'),
        endTime: moment('23:59:59', 'HH:mm:ss'),
        list: []
    }

    componentDidMount () {
        this.getList();
    }
    initPage () {
        this.setState({
            day: moment(),
            beginTime: moment('00:00:00', 'HH:mm:ss'),
            endTime: moment('23:59:59', 'HH:mm:ss')
        })
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id
        ) {
            this.initPage();
            this.getList(nextData);
        }
    }
    getList (data?: any) {
        let { day, beginTime, endTime } = this.state;
        data = data || this.props.data;

        this.setState({
            list: []
        })

        if (!data || !data.id) {
            return;
        }

        beginTime = moment(day.format('YYYY MM DD') + ' ' + beginTime.format('HH:mm:ss'), 'YYYY MM DD HH:mm:ss').valueOf();
        endTime = moment(day.format('YYYY MM DD') + ' ' + endTime.format('HH:mm:ss'), 'YYYY MM DD HH:mm:ss').valueOf();

        this.setState({
            loading: true
        })

        Api.getCheckPointList({
            taskId: data.id,
            startTime: beginTime,
            endTime
        }).then(
            (res: any) => {
                if (res.code == 1) {
                    this.setState({
                        list: res.data.checkpointList
                    })
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    initCheckPointColumns () {
        return [{
            title: 'StartTime',
            dataIndex: 'time',
            render (time: any) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '持续时间',
            dataIndex: 'duration',
            render (text: any) {
                return `${text}ms`
            }
        }]
    }
    changeDate (date: moment.Moment) {
        this.setState({
            day: date
        }, this.getList.bind(this))
    }
    changeTime (type: string, date: moment.Moment) {
        this.setState({
            [type]: date
        }, this.getList.bind(this))
    }
    disabledDate = (current: any) => {
        const now = moment('23:59:59', 'HH:mm:ss')
        return current > now && current != now;
    }
    /**
     * antd有bug，这个暂时不能用
     */
    disabledTime = (current: any, type: any) => {
        if (!current) {
            return;
        }

        const now = moment();

        if (type == 'start') {
            current = current.length > 1 ? current[0] : current
        } else if (type == 'end') {
            current = current.length > 1 ? current[1] : current
        }
        let disabledHours: any = []; let disabledMinutes: any = [];
        if (now.format('YYYY-MM-DD') == current.format('YYYY-MM-DD')) {
            disabledHours = range(0, 24).map((num: any) => {
                return now.hour() < num ? num : null
            }).filter(Boolean)
        }
        if (now.format('YYYY-MM-DD HH') == current.format('YYYY-MM-DD HH')) {
            disabledMinutes = range(0, 60).map((num: any) => {
                return now.minute() < num ? num : null
            }).filter(Boolean)
        }
        console.log(disabledHours, disabledMinutes, type)
        return {
            disabledHours: () => {
                return disabledHours;
            },
            disabledMinutes: () => {
                return disabledMinutes;
            }
        }
    }
    getTableTitle = () => {
        const { day, beginTime, endTime } = this.state;
        return (
            <div style={{ padding: '10px 10px 11px 0px' }}>
                <DatePicker
                    onChange={this.changeDate.bind(this)}
                    style={{ width: '150px', marginRight: 10 }}
                    value={day}
                    disabledDate={this.disabledDate}
                    allowClear={false}
                    placeholder='请选择日期'
                />
                开始时间：<TimePicker
                    style={{ marginRight: 10 }}
                    allowEmpty={false}
                    onChange={this.changeTime.bind(this, 'beginTime')}
                    value={beginTime}
                    placeholder='开始时间'
                    disabledHours={() => {
                        return disableRangeCreater(beginTime, endTime, 'hour')
                    }}
                    disabledMinutes={() => {
                        return disableRangeCreater(beginTime, endTime, 'minute')
                    }}
                    disabledSeconds={() => {
                        return disableRangeCreater(beginTime, endTime, 'second')
                    }}
                />
                截止时间：<TimePicker
                    allowEmpty={false}
                    onChange={this.changeTime.bind(this, 'endTime')}
                    value={endTime}
                    placeholder='结束时间'
                    disabledHours={() => {
                        return disableRangeCreater(beginTime, endTime, 'hour', true)
                    }}
                    disabledMinutes={() => {
                        return disableRangeCreater(beginTime, endTime, 'minute', true)
                    }}
                    disabledSeconds={() => {
                        return disableRangeCreater(beginTime, endTime, 'second', true)
                    }}
                />
            </div>
        )
    }
    render () {
        const { list } = this.state;
        return (
            <div style={{ padding: '0px 20px 20px 25px' }}>
                {this.getTableTitle()}
                <Table
                    rowKey={(record: any, index: any) => {
                        return index
                    }}
                    className="dt-ant-table dt-ant-table--border dt-ant-table--border-lr border-table"
                    columns={this.initCheckPointColumns()}
                    dataSource={list}
                    pagination={{
                        pageSize: 15
                    } as any}
                />
            </div>
        )
    }
}

export default CheckPoint;
