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
            current: 1,
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
            tableId: get(tableInfo, 'table.id')
        });
        if (res && res.code == 1) {
            this.setState({
                data: res.data,
                pagination: {
                    ...pagination,
                    total: res.data && res.data.length
                }
            })
        }
        this.setState({
            loading: false
        })
    }
    onTableChange (pagination: PaginationProps) {
        this.setState({
            pagination: pagination
        })
    }
    initColumn (columns: string[]): { width: number; tableColumns: any[] } {
        if (!columns) {
            return {
                width: 800,
                tableColumns: []
            }
        }
        let width = 0;
        const tableColumns = [{
            title: '序号',
            dataIndex: 'index',
            width: 100
        }].concat(columns.map((item) => {
            width += item.length * 4 + 20;
            return {
                title: item,
                dataIndex: item,
                width: item.length * 4 + 20
            }
        }));
        return {
            width: Math.max(800, width + 100),
            tableColumns
        }
    }
    initData (data: any[]) {
        const column = data[0];
        return data.slice(1).map((item, dataIndex: number) => {
            let newData: any = {
                index: dataIndex + 1
            }
            item.map((value: any, itemIndex: number) => {
                newData[column[itemIndex]] = value;
            })
            return newData;
        })
    }
    render () {
        const { data, loading } = this.state;
        const { tableColumns } = this.initColumn(data[0])
        return (
            <Table
                className='dt-ant-table'
                columns={tableColumns}
                pagination={true}
                onChange={this.onTableChange.bind(this)}
                loading={loading}
                dataSource={this.initData(data)}
                scroll={{ x: true, y: 500 }}
            />
        )
    }
}
export default DirtyTable;
