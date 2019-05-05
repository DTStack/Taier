import React, { Component } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class SameWidthData extends Component {
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
        const fields = [
            'bin', 'threshold', 'negative', 'positive', 'total', 'precision',
            'recall', 'fpr', 'f1_score', 'lift', 'distribution_of_nagative',
            'distribution_of_positive', 'cumulaitve_percentages_of_negative',
            'cumulaitve_percentages_of_positive', 'total_cumulative_percentages', 'KS']
        const columns = [{
            title: '序号',
            dataIndex: 'id',
            fixed: 'left',
            key: 'id',
            sorter: true
        }];
        for (let i = 0; i < fields.length; i++) {
            const field = fields[i];
            columns.push({
                title: field,
                dataIndex: field,
                key: field,
                sorter: true
            })
        }
        return columns;
    }

    render () {
        const cols = this.initialCols();
        return (
            <Table
                className="m-table"
                columns={cols}
                rowKey="index"
                style={{ height: '100%' }}
                dataSource={this.state.data}
                pagination={this.state.pagination}
                loading={this.state.loading}
                onChange={this.handleTableChange}
                scroll={{ x: '2000px' }}
            >
            </Table>
        )
    }
}

export default SameWidthData
