import React, { Component } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class TableDetail extends Component {
    state = {
        tableData: [],
        pagination: {
            current: 1,
            pageSize: 10
        },
        loading: false
    };
    componentDidUpdate (prevProps, prevState) {
        if (this.props.visible && !prevProps.visible) {
            this.fetchData();
        }
    }
    componentDidMount () {
        this.fetchData()
    }
    handleTableChange = (pagination, filters, sorter) => {
        const pager = { ...this.state.pagination };
        pager.current = pagination.current;
        this.setState({
            pagination: pager
        });
    }

    fetchData = (params = {}) => {
        const { data, indexType, queryParams } = this.props;
        if (!data) return;

        this.setState({ loading: true });
        API.getEvaluateReportTableData({
            num: 100,
            taskId: data.id,
            inputType: indexType,
            ...params,
            ...queryParams
        }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data || []
                })
            }
            this.setState({ loading: false });
        });
    }

    initialCols = (fields) => {
        const columns = [];
        if (fields) {
            for (let i = 0; i < fields.length; i++) {
                const field = fields[i];
                columns.push({
                    title: field,
                    dataIndex: field,
                    key: field,
                    sorter: (a, b) => {
                        return a[i] - b[i];
                    },
                    render: (text, record) => {
                        return record[i];
                    }
                })
            }
        }
        return columns;
    }

    render () {
        const { tableData } = this.state;
        const { indexType } = this.props;
        const cols = this.initialCols(tableData[0]);
        const dataSource = tableData.length > 1 ? tableData.slice(1, tableData.length) : [];
        const scroll = { x: cols.length < 5 ? true : 2000, y: 250 };
        return (
            <Table
                className="m-table border-table"
                columns={cols}
                rowKey={(record, index) => {
                    const rowKey = `${indexType}-${record[index]}-${index}`
                    return rowKey;
                }}
                style={{ height: '100%' }}
                dataSource={dataSource}
                pagination={this.state.pagination}
                loading={this.state.loading}
                onChange={this.handleTableChange}
                scroll={scroll}
            >
            </Table>
        )
    }
}

export default TableDetail
