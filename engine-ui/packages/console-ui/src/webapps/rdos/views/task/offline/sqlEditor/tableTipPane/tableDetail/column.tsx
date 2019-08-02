import * as React from 'react';

import { Input, Table, Icon } from 'antd';

import ajax from '../../../../../../api'

const Search = Input.Search;

class ExtraPaneTableDetailColumn extends React.Component<any, any> {
    state: any = {
        searchValue: undefined,
        overview: {},
        isLoading: false
    }
    componentDidMount () {
        this.getOverview();
    }
    getOverview () {
        this.setState({
            isLoading: true
        })
        const tableId = this.getTableId();
        if (tableId) {
            ajax.previewTable({ tableId: tableId }).then((res: any) => {
                if (res.code === 1 && res.data && res.data[1]) {
                    this.resolveOverviewData(res.data[0], res.data[1]);
                }
                this.setState({
                    isLoading: false
                })
            });
        }
    }
    resolveOverviewData (keys: any, data: any) {
        let overview: any = {};
        for (let i = 0; i < keys.length; i++) {
            overview[keys[i]] = data[i];
        }
        this.setState({
            overview: overview
        })
    }
    search (e: any) {
        this.setState({
            searchValue: e.target.value
        })
    }
    initColumns () {
        const { isLoading, overview } = this.state;
        return [{
            title: '字段',
            dataIndex: 'columnName',
            width: '100px'
        }, {
            title: '类型',
            dataIndex: 'columnType',
            width: '80px'
        }, {
            title: '描述',
            dataIndex: 'comment',
            width: '80px'
        }, {
            title: <span>{isLoading && <Icon type="loading" />}示例</span>,
            dataIndex: 'example',
            render: (text: any, record: any) => {
                return overview[record.columnName]
            }
        }]
    }
    getTableId () {
        const { columns } = this.props;
        if (columns && columns.length) {
            return columns[0].tableId;
        } else {
            return null;
        }
    }
    filterColumns () {
        const { columns } = this.props;
        const { searchValue } = this.state;
        if (!searchValue) {
            return columns;
        }
        return columns.filter((column: any) => {
            return column.columnName.indexOf(searchValue) > -1
        })
    }
    render () {
        const pagination: any = { size: 'small' }
        return (
            <div className="c-table__detail__columns">
                <Search
                    className="c-table__detail__search"
                    placeholder="输入字段名搜索"
                    onChange={this.search.bind(this)}
                />
                <Table
                    className="dt-ant-table dt-ant-table--border border-table"
                    columns={this.initColumns()}
                    dataSource={this.filterColumns()}
                    pagination={pagination}
                />
            </div>
        )
    }
}

export default ExtraPaneTableDetailColumn;
