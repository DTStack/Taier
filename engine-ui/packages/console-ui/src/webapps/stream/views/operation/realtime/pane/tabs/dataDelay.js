import React from "react"

import { Table } from "antd"

class DataDelay extends React.Component {
    state = {
        pagination: {
            total: 0,
            defaultPageSize: 10,
        }
    }
    componentDidMount() {
        console.log("DataDelay")
    }
    initDelayListColumns() {
        return [{
            title: '分区ID',
            dataIndex: 'id',
        }, {
            title: '延迟消息数',
            dataIndex: 'delayNum',
        }, {
            title: '总消息数',
            dataIndex: 'count',
        }, {
            title: '当前消费位置',
            dataIndex: 'index',
        }, {
            title: '操作',
            dataIndex: 'deal',
        }]
    }
    render() {
        const { pagination } = this.state;
        return (
            <div style={{padding:"21px 20px 20px 25px"}}>
                <Table
                    className="m-table"
                    columns={this.initDelayListColumns()}
                    dataSource={[]}
                    pagination={pagination}
                />
            </div>
        )
    }
}

export default DataDelay;