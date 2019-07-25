import React from 'react';
import { Table } from 'antd';
import moment from 'moment';

import Api from '../../api/dataManage';

export default class TablePartition extends React.Component {
    state = {
        result: { data: [] },
        current: 1,
        loading: false
    }

    componentDidMount () {
        if (this.props.table) {
            this.loadPartition();
        }
    }

    componentDidUpdate (prevProps) {
        const { table: oldTable } = prevProps;
        const { table } = this.props;
        if (
            table &&
            table.id &&
            (!oldTable || oldTable.id != table.id)
        ) {
            this.loadPartition();
        }
    }

    loadPartition = () => {
        const ctx = this
        const current = this.state.current
        const table = this.props.table
        const params = {
            tableId: table.id,
            pageIndex: current,
            pageSize: 10
        }
        this.setState({
            loading: true,
            result: {}
        })
        Api.getTablePartition(params).then(res => {
            this.setState({
                loading: false
            })
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

    initClumuns () {
        return [{
            title: '分区名',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '更新时间',
            dataIndex: 'lastDDLTime',
            key: 'lastDDLTime',
            render: (text) => {
                return <span>{moment(text).format('YYYY-MM-DD HH:mm:ss')}</span>
            }
        }, {
            title: '存储量',
            dataIndex: 'storeSize',
            key: 'storeSize'
        }]
    }

    render () {
        const { result, loading } = this.state
        const { pagination, havaBorder } = this.props
        const realPagination = pagination || {
            defaultPageSize: 10
        };
        realPagination.total = result.totalCount
        return (
            <div className="box">
                <Table
                    key="table_partition"
                    className={`dt-ant-table dt-ant-table--border ${havaBorder ? 'border-table' : ''}`}
                    pagination={realPagination}
                    columns={this.initClumuns()}
                    dataSource={result.data || []}
                    onChange={this.handleTableChange}
                    loading={loading}
                />
            </div>
        )
    }
}
