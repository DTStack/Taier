import * as React from 'react'
import utils from 'utils'
import moment from 'moment'
import { range } from 'lodash'

import { Table, DatePicker } from 'antd'

import Api from '../../../../../api'

const { RangePicker } = DatePicker;

class CheckPoint extends React.Component<any, any> {
    state: any = {
        dates: [],
        list: []
    }

    componentDidMount () {
        this.getList();
    }
    initPage () {
        this.setState({
            dates: []
        })
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id
        ) {
            this.initPage();
            this.getList(nextData);
        }
    }
    getList (data?: any) {
        const { dates } = this.state;
        data = data || this.props.data;

        this.setState({
            list: []
        })

        if (!data || !data.id) {
            return;
        }

        let startTime = dates.length && dates[0] ? dates[0].valueOf() : undefined;
        let endTime = dates.length > 1 && dates[1] ? dates[1].valueOf() : undefined;

        this.setState({
            loading: true
        })

        Api.getCheckPointList({
            taskId: data.id,
            startTime,
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
                return utils.formatDateHours(time);
            }
        }, {
            title: '持续时间',
            dataIndex: 'duration',
            render (text: any) {
                return `${text}ms`
            }
        }]
    }
    changeDate (dates: any) {
        let newDates = dates;
        if (dates && dates.length) {
            if(dates[1]<dates[0]) {
                newDates = [dates[0], dates[0].clone()];
            }
        }
        this.setState({
            dates: newDates
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
        const { dates } = this.state;
        return (
            <div style={{ padding: '10px 10px 11px 0px' }}>
                <RangePicker
                    onChange={this.changeDate.bind(this)}
                    showTime={{
                        disabledSeconds: true,
                        format: 'HH:mm',
                        defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment()],
                        hideDisabledOptions: true
                    } as any}
                    style={{ width: '250px' }}
                    format="YYYY-MM-DD HH:mm"
                    value={dates}
                    disabledDate={this.disabledDate}
                // disabledTime={this.disabledTime}
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
