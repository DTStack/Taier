import React, { Component } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class TotalIndexData extends Component {
    state = {
        data: [],
        pagination: {},
        loading: false
    };

    componentDidMount () {
        const data = this.props.data;
        if (!data) return;
        this.fetchData({
            taskId: data.id
        })
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
                className="m-table"
                rowKey="index"
                columns={this.initialCols()}
                style={{ height: '100%' }}
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
