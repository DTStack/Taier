import React, { Component } from 'react';

import { Table, Input } from 'antd';
const Search = Input.Search;

const PAGE_SIZE = 20;

class Columns extends Component {
    state = {
        table: {
            pageIndex: 1,
            total: this.props.tableColumns && this.props.tableColumns.length
        }
    }
    initColumns = () => {
        return [
            {
                title: '字段',
                dataIndex: 'name',
                width: '150px'
            },
            {
                title: '类型',
                dataIndex: 'type',
                width: '100px'
            },
            {
                title: '描述',
                dataIndex: 'comment',
                width: '250px'
            },
            {
                title: '预览',
                dataIndex: 'dataList',
                width: '150px',
                render (dataList) {
                    if (dataList) {
                        return dataList.map(item => {
                            return item.join(' 、')
                        })
                    } else {
                        return '-'
                    }
                }
            }
        ]
    }
    getPagination () {
        const { pageIndex, total } = this.state.table;
        return {
            currentPage: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    // onTableChange = (page) => {
    //     this.setState({
    //         table: {
    //             pageIndex: page.current
    //         }
    //     })
    // }
    render () {
        const columns = this.initColumns();
        const { tableColumns, tableColumnsLoading } = this.props
        return (
            <div>
                <Search
                    placeholder='请输入字段名搜索'
                    style={{ marginBottom: '10px' }}
                />
                <Table
                    dataSource={tableColumns}
                    columns={columns}
                    pagination={this.getPagination()}
                    // onChange={this.onTableChange}
                    loading={tableColumnsLoading}
                    size='small'
                >
                </Table>
            </div>
        )
    }
}

export default Columns;
