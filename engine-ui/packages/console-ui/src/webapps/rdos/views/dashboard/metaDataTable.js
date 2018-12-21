import React from 'react';
import utils from 'utils';
import { Table } from 'antd';

import api from '../../api';

class MetaDataTable extends React.Component {
    state = {
        tables: [],
        loading: false
    }
    componentDidMount () {
        this.getTableList();
    }
    async getTableList () {
        this.setState({
            tables: []
        })
        const { database } = this.props;
        if (database) {
            this.setState({
                loading: true
            })
            let res = await api.getTableListFromDataBase({
                database
            });
            if (res.code == 1) {
                this.setState({
                    tables: res.data
                })
            }
            this.setState({
                loading: false
            })
        }
    }
    getColumns () {
        return [{
            title: '表名',
            dataIndex: 'name',
            width: 300
        }, {
            title: '存储位置',
            dataIndex: 'location',
            width: 400
        }, {
            title: '存储量',
            dataIndex: 'totalSize',
            width: 100,
            render (text) {
                return utils.convertBytes(text);
            }
        }];
    }
    render () {
        const { tables, loading } = this.state;
        const { database } = this.props;
        return (
            <section>
                <p>数据库名：{database}</p>
                <Table
                    className='m-table border-table'
                    columns={this.getColumns()}
                    dataSource={tables}
                    scroll={{ y: '300px' }}
                    loading={loading}
                    pagination={false}
                />
            </section>
        )
    }
}
export default MetaDataTable;
