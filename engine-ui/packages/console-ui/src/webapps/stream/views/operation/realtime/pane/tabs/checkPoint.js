import React from "react"
import utils from "utils"
import moment from "moment"
import { range } from "lodash"

import { Table, DatePicker } from "antd"

import Api from "../../../../../api"

const { RangePicker } = DatePicker;

class CheckPoint extends React.Component {

    state = {
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        },
        dates: [],
        list: [],
        overview: {}
    }

    componentDidMount() {
        console.log("CheckPoint")
        this.getList();
        this.getOverview();
    }
    initPage() {
        this.setState({
            pagination: {
                total: 0,
                pageSize: 10,
                current: 1
            },
            dates:[],
            overview:{}
        })
    }
    componentWillReceiveProps(nextProps) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id
        ) {
            this.initPage();
            this.getList(nextData);
            this.getOverview(nextData);
        }
    }
    getList(data) {
        const { pagination, dates } = this.state;
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
            endTime,
            currentPage: pagination.current,
            pageSize: pagination.pageSize
        }).then(
            (res) => {
                if (res.code == 1) {
                    this.setState({
                        list: res.data.data,
                        pagination: {
                            ...pagination,
                            total: res.data.totalCount
                        }
                    })
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    getOverview(data) {
        data = data || this.props.data;

        this.setState({
            overview: {}
        })

        if (!data || !data.id) {
            return;
        }

        Api.getCheckPointOverview({
            taskId: data.id
        }).then(
            (res) => {
                if (res.code == 1) {
                    this.setState({
                        overview: res.data
                    })
                }
            }
        )
    }
    initCheckPointColumns() {
        return [{
            title: 'StartTime',
            dataIndex: 'time',
            render(time) {
                return utils.formatDateHours(time);
            }
        },{
            title: '持续时间',
            dataIndex: 'duration',
            render(text){
                return `${text}ms`
            }
        }, ]
    }
    changeDate(dates) {
        this.setState({
            dates: dates
        }, this.getList.bind(this))
    }
    disabledDate = (current) => {
        const now = new moment()
        return current > now && current != now;
    }
    /**
     * antd有bug，这个暂时不能用
     */
    disabledTime = (current, type) => {
        if (!current) {
            return;
        }

        const now = new moment();

        if (type == "start") {
            current = current.length > 1 ? current[0] : current
        } else if (type == "end") {
            current = current.length > 1 ? current[1] : current
        }
        let disabledHours = [], disabledMinutes = [];
        if (now.format('YYYY-MM-DD') == current.format('YYYY-MM-DD')) {
            disabledHours = range(0, 24).map((num) => {
                return now.hour() < num ? num : null
            }).filter(Boolean)
        }
        if (now.format('YYYY-MM-DD HH') == current.format('YYYY-MM-DD HH')) {
            disabledMinutes = range(0, 60).map((num) => {
                return now.minute() < num ? num : null
            }).filter(Boolean)
        }
        console.log(disabledHours,disabledMinutes,type)
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
        const { overview, dates } = this.state;
        return (
            <div style={{ padding: "10px 10px 11px 0px" }}>
                <RangePicker
                    onChange={this.changeDate.bind(this)}
                    showTime={{
                        disabledSeconds: true,
                        format: "HH:mm",
                        defaultValue: [moment('00:00:00', 'HH:mm:ss'),moment()],
                        hideDisabledOptions:true
                    }}
                    style={{ width: "250px" }}
                    format="YYYY-MM-DD HH:mm"
                    value={dates}
                    disabledDate={this.disabledDate}
                    // disabledTime={this.disabledTime}
                />
                <span className="checkpoint-overview">
                    <span>checkpoint总数：{overview.totalCount}个</span>
                    <span>成功：{overview.successCount}个</span>
                    <span>失败：{overview.failCount}个</span>
                </span>
            </div>
        )
    }
    onTableChange(page, filters, sorter) {
        const { pagination } = this.state;
        this.setState({
            pagination: {
                ...pagination,
                current: page.current
            }
        }, this.getList.bind(this))
    }
    render() {
        const { pagination, list } = this.state;
        return (
            <div style={{ padding: "0px 20px 20px 25px" }}>
                {this.getTableTitle()}
                <Table
                    rowKey={(record,index)=>{
                        return index
                    }}
                    className="m-table"
                    columns={this.initCheckPointColumns()}
                    dataSource={list}
                    pagination={pagination}
                    onChange={this.onTableChange.bind(this)}
                />
            </div>
        )
    }
}

export default CheckPoint;