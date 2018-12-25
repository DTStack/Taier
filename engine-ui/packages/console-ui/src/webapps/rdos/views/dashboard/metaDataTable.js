import React from 'react';
import utils from 'utils';
import { Table, Row, Col } from 'antd';

import api from '../../api';
import { metaFormLayout } from './metaImportForm';

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
        return (
            <section className='l-metaImport__database__table'>
                <Row>
                    <Col {...metaFormLayout.labelCol}></Col>
                    <Col {...metaFormLayout.wrapperCol}>
                        <Table
                            className='m-table border-table'
                            columns={this.getColumns()}
                            dataSource={tables}
                            scroll={{ y: '300px' }}
                            loading={loading}
                            pagination={false}
                        />
                    </Col>
                </Row>
            </section>
        )
    }
}
export default MetaDataTable;
