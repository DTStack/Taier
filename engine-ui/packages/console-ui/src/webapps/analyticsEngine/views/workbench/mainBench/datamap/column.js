import React, { Component } from 'react';

import { Table, Input } from 'antd';
import '../../../../styles/views/dataMap.scss';
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
                width: '100px',
                className: 'dataMapTableColumns'
            },
            {
                title: '类型',
                dataIndex: 'type',
                width: '150px',
                className: 'dataMapTableColumns'
            },
            {
                title: '描述',
                dataIndex: 'comment',
                width: '150px',
                className: 'dataMapTableColumns',
                render (comment) {
                    if (comment) {
                        return comment
                    } else {
                        return '-'
                    }
                }
            },
            {
                title: '预览',
                dataIndex: 'dataList',
                width: '150px',
                className: 'dataMapTableColumns',
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
            <div className='dataMapTable' style={{ marginLeft: '20px' }}>
                <Search
                    placeholder='请输入字段名搜索'
                    style={{ marginBottom: '20px' }}
                />
                <Table
                    dataSource={tableColumns}
                    columns={columns}
                    pagination={this.getPagination()}
                    // onChange={this.onTableChange}
                    loading={tableColumnsLoading}
                    // size='small'
                    bordered={true}
                >
                </Table>
            </div>
        )
    }
}

export default Columns;
