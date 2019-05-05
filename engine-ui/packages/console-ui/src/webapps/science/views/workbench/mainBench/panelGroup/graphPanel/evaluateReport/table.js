import React, { PureComponent } from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

function getID (current, index) {
    return (current - 1) * 10 + (index + 1);
}

class TableDetail extends PureComponent {
    state = {
        tableData: [],
        pagination: {
            current: 1,
            pageSize: 10
        },
        loading: false
    };

    componentDidMount () {
        this.fetchData()
    }
    handleTableChange = (pagination, filters, sorter) => {
        const pager = { ...this.state.pagination };
        pager.current = pagination.current;
        this.setState({
            pagination: pager
        });
        this.fetchData({
            sortField: sorter.field,
            sortOrder: sorter.order,
            ...filters
        });
    }

    fetchData = async (params = {}) => {
        const { data, indexType, queryParams } = this.props;
        if (!data) return;

        this.setState({ loading: true });
        const res = await API.getEvaluateReportTableData({
            num: 100,
            inputType: indexType,
            ...params,
            ...queryParams
        });
        if (res.code === 1) {
            this.setState({
                tableData: res.data || []
            })
        }
        this.setState({ loading: false });
    }

    initialCols = (fields) => {
        const { pagination } = this.state;
        const columns = [{
            title: '序号',
            dataIndex: 'id',
            fixed: 'left',
            key: 'id',
            sorter: true,
            render: (text, item, index) => {
                return getID(pagination.current, index);
            }
        }];
        if (fields) {
            for (let i = 0; i < fields.length; i++) {
                const field = fields[i];
                columns.push({
                    title: field,
                    dataIndex: field,
                    key: field,
                    sorter: true,
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
        return (
            <Table
                className="m-table"
                columns={cols}
                rowKey={(record, index) => {
                    const rowKey = `${indexType}-${record[index]}-${index}`
                    console.log('rowKey', rowKey);
                    return rowKey;
                }}
                style={{ height: '100%' }}
                dataSource={dataSource}
                pagination={this.state.pagination}
                loading={this.state.loading}
                onChange={this.handleTableChange}
                scroll={{ x: '2000px' }}
            >
            </Table>
        )
    }
}

export default TableDetail
