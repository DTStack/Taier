import * as React from 'react';

import { get } from 'lodash';

import { Table } from 'antd';

import Api from '../../../../../../api';
import { TableInfo } from './';
import { PaginationProps } from 'antd/lib/pagination';

interface DirtyTableState {
    data: any[];
    pagination: PaginationProps;
    loading: boolean;
}
class DirtyTable extends React.PureComponent<{ tableInfo: TableInfo }, DirtyTableState> {
    state: DirtyTableState = {
        data: [],
        pagination: {
            total: 0,
            current: 0,
            pageSize: 10
        },
        loading: false
    }
    componentDidMount () {
        this.getData();
    }
    async getData () {
        const { tableInfo } = this.props;
        const { pagination } = this.state;
        if (!tableInfo) {
            return;
        }
        this.setState({
            loading: true
        })
        let res = await Api.getDirtyDataTableOverview({
            tableId: get(tableInfo, 'table.id'),
            pageNo: pagination.current,
            pageSize: pagination.pageSize
        });
        if (res && res.code == 1) {
            this.setState({
                data: res.data
            })
        }
        this.setState({
            loading: false
        })
    }
    onTableChange (pagination: PaginationProps) {
        this.setState({
            pagination: pagination
        }, this.getData.bind(this))
    }
    initColumn (columns: string[]): { width: number; tableColumns: any[]} {
        if (!columns) {
            return {
                width: 800,
                tableColumns: []
            }
        }
        let width = 0;
        const tableColumns = [{
            title: '序号',
            key: 'index'
        }].concat(columns.map((item) => {
            width += item.length * 8 + 20;
            return {
                title: item,
                key: item,
                width: item.length * 8 + 20
            }
        }));
        return {
            width: Math.max(800, width),
            tableColumns
        }
    }
    render () {
        const { data, pagination } = this.state;
        const { width, tableColumns } = this.initColumn(data[0])
        return (
            <Table
                className='dt-ant-table'
                columns={tableColumns}
                pagination={pagination}
                onChange={this.onTableChange.bind(this)}
                dataSource={data.slice(1)}
                scroll={{ x: width }}
            />
        )
    }
}
export default DirtyTable;
