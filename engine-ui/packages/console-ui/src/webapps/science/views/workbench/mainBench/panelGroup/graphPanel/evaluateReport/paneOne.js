import React, { Component } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class TotalIndexData extends Component {
    state = {
        data: [],
        pagination: {},
        loading: false
    };

    handleTableChange = (pagination, filters, sorter) => {
        const pager = { ...this.state.pagination };
        pager.current = pagination.current;
        this.setState({
            pagination: pager
        });
        this.fetchData({
            limit: pagination.pageSize,
            page: pagination.current,
            sortField: sorter.field,
            sortOrder: sorter.order,
            ...filters
        });
    }

    fetchData = async (params = {}) => {
        this.setState({ loading: true });
        const res = await API.getTotalIndexData({
            limit: 10,
            ...params
        });
        if (res.code === 1) {
            this.setState({
                data: res.data
            })
        }
        this.setState({ loading: false });
    }

    initialCols = () => {
        return [{
            title: 'index',
            dataIndex: 'index',
            key: 'index',
            sorter: true,
            width: '50%'
        }, {
            title: 'Value',
            dataIndex: 'value',
            key: 'value',
            width: '50%'
        }]
    }

    render () {
        return (
            <Table
                columns={this.initialCols()}
                rowKey="index"
                dataSource={this.state.data}
                pagination={this.state.pagination}
                loading={this.state.loading}
                onChange={this.handleTableChange}
            >
            </Table>
        )
    }
}

export default TotalIndexData
