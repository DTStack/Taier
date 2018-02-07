import React from 'react';
import { Table } from 'antd';
import moment from 'moment';

import Api from '../../api';

export default class TablePartition extends React.Component {

    state = {
        result: { data: [] },
        current: 1,
    }

    componentDidMount() {
        this.loadPartition();
    }

    loadPartition = () => {
        const ctx = this
        const current = this.state.current
        const table = this.props.table
        const params = {
            tableId: table.tableId,
            pageIndex: current,
            pageSize: 10,
        }
        Api.getTablePartition(params).then(res => {
            if (res.code === 1) {
                ctx.setState({
                    result: res.data
                })
            }
        })
    }

    handleTableChange = (pagination) => {
        this.setState({ current: pagination.current }, () => {
            this.loadPartition()
        })
    }

    initClumuns() {
        return [{
            title: '分区名',
            dataIndex: 'name',
            key: 'name'
        },{
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: (text) => {
                return <span>{moment(text).format('YYYY-MM-DD HH:mm:ss')}</span>
            }
        },{
            title: '存储量',
            dataIndex: 'storeSize',
            key: 'storeSize'
        }]
    }

    render() {
        const { result } = this.state
        const pagination = {
            total: result.totalCount,
            defaultPageSize: 10,
        };
        return (
            <div className="box">
                <Table 
                    key="table_partition" 
                    pagination={pagination}
                    columns={this.initClumuns()} 
                    dataSource={result.data || []} 
                    onChange={this.handleTableChange}
                />
            </div>
        )
    }

}