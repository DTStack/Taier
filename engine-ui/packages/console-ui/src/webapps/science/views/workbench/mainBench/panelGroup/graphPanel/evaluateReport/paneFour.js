import React, { Component } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class SameFrequencyData extends Component {
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
            'bin', 'data_range', 'negative', 'positive', 'total',
            'precision', 'recall', 'fpr', 'f1_score', 'lift',
            'distribution_of_nagative', 'distribution_of_positive',
            'cumulaitve_percentages_of_negative', 'cumulaitve_percentages_of_positive',
            'total_cumulative_percentages', 'KS']
        const columns = [{
            title: '序号',
            dataIndex: 'id',
            key: 'id',
            sorter: true
        }];

        fields.forEach(field => {
            columns.push({
                title: field,
                dataIndex: field,
                key: field,
                sorter: true
            })
        });
        return fields;
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

export default SameFrequencyData
