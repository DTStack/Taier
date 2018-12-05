import React, { Component } from 'react';

import { Table, Input } from 'antd';
const Search = Input.Search;
class Columns extends Component {
    state = {
        dataSource: [{
            key: '1',
            column: 'id',
            type: 'string',
            desc: '1212',
            prev: '2121'
        }, {
            key: '2',
            column: 'name',
            type: 'string',
            desc: '212121',
            prev: '2121'
        }]
    }
    initColumns = () => {
        return [
            {
                title: '字段',
                dataIndex: 'column',
                width: '100px'
                // render () {
                //     return 'id'
                // }
            },
            {
                title: '类型',
                dataIndex: 'type',
                width: '100px'
                // render () {
                //     return 'id'
                // }
            },
            {
                title: '描述',
                dataIndex: 'desc',
                width: '150px'
                // render () {
                //     return 'id'
                // }
            },
            {
                title: '预览',
                dataIndex: 'prev',
                width: '150px'
                // render () {
                //     return 'id'
                // }
            }
        ]
    }

    render () {
        const columns = this.initColumns();
        const { dataSource } = this.state
        return (
            <div>
                <Search
                    placeholder='请输入字段名搜索'
                    style={{ marginBottom: '10px' }}
                />
                <Table
                    dataSource={dataSource}
                    columns={columns}
                >
                </Table>
            </div>
        )
    }
}

export default Columns;
