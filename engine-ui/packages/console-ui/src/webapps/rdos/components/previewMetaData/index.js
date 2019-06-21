import React, { Component } from 'react';
import { Modal, Table } from 'antd';
import Api from '../../api/index';

const PAGESIZE = 20;
export default class PreviewMetaData extends Component {
    constructor (props) {
        super(props);
        this.state = {
            loading: false,
            dbName: '',
            engineType: '',
            tableData: [],
            pageSize: PAGESIZE,
            currentPage: 1,
            total: 0
        }
    }
    /* eslint-disable */
    UNSAFE_componentWillReceiveProps (nextProps) {
        if (nextProps.dbName && nextProps.visible && nextProps.visible != this.props.visible) {
            // 获取数据库详情
            const { dbName, engineType } = nextProps;
            this.setState({
                dbName,
                engineType
            }, () => {
                this.getDBTableList(dbName, engineType)
            })
        }
    }
    getDBTableList = (dbName, engineType) => {
        this.setState({
            loading: true
        })
        Api.getDBTableList({
            dbName,
            engineType
        }).then(
            res => {
                if (res.code === 1) {
                    this.setState({
                        tableData: res.data || [],
                        total: res.data.total,
                        loading: false
                    })
                } else {
                    this.setState({
                        loading: false
                    })
                }
            }
        )
    }
    initColumns = () => {
        return [{
            title: '表名',
            render (text, record) {
                return text
            }
        }]
    }
    handleTable = (pagination, filters, sorter) => {
        const { dbName, engineType } = this.state;
        this.setState({
            currentPage: pagination.current,
        }, this.getDBTableList(dbName, engineType))
    }
    render () {
        const { tableData, loading, total, currentPage, dbName } = this.state;
        const { visible, onCancel } = this.props;
        const columns = this.initColumns();
        const pagination = {
            current: currentPage,
            pageSize: PAGESIZE,
            total
        }
        return (
            <Modal
                title='预览元数据'
                visible={visible}
                onCancel={onCancel}
                onOk={onCancel}
            >
                <div style={{ padding: '0 0 10 5' }}>{`数据库名：${dbName}`}</div>
                <Table
                    className='m-table'
                    loading={loading}
                    columns={columns}
                    onChange={this.handleTable}
                    pagination={pagination}
                    dataSource={tableData}
                />
            </Modal>
        )
    }
}
