import React, { Component } from 'react';
import { Modal, Table } from 'antd';
import Api from '../../api/index';
export default class PreviewMetaData extends Component {
    state = {
        tableData: []
    }
    /* eslint-disable */
    UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.props.dbName && nextProps.visible && nextProps.visible != this.props.visible) {
            // 获取数据库详情
            this.getDbTableList()
        }
    }
    async getDbTableList () {
        const { engineType, dbName } = this.props;
        const res = await Api.getDBTableList({
            engineType,
            dbName
        })
        if (res.code === 1) {
            this.setState({
                tableData: res.data || []
            })
        }
    }
    exChangeData = (tableData) => {
        let data = [];
        tableData.map(item => {
            data.push({
                tableName: item
            })
        })
    }
    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            render (text, record) {
                return text
            }
        }]
    }
    render () {
        const { tableData } = this.state;
        const { visible, onCancel } = this.props;
        const columns = this.initColumns;
        return (
            <Modal
                title='预览元数据'
                visible={visible}
                onCancel={onCancel}
            >
                <Table
                    columns={columns}
                    dataSource={this.exChangeData(tableData)}
                />
            </Modal>
        )
    }
}
