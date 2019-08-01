import * as React from 'react';
import { Modal, Table } from 'antd';
import Api from '../../api/index';

const PAGESIZE = 20;
export default class PreviewMetaData extends React.Component<any, any> {
    constructor (props: any) {
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
    UNSAFE_componentWillReceiveProps(nextProps: any) {
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
    getDBTableList = (dbName: any, engineType: any) => {
        this.setState({
            loading: true
        })
        Api.getDBTableList({
            dbName,
            engineType
        }).then(
            (res: any) => {
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
            render (text: any, record: any) {
                return text
            }
        }]
    }
    handleTable = (pagination: any, filters: any, sorter: any) => {
        const { dbName, engineType } = this.state;
        this.setState({
            currentPage: pagination.current,
        }, this.getDBTableList.bind(this, dbName, engineType))
    }
    render () {
        const { tableData, loading, total, currentPage, dbName } = this.state;
        const { visible, onCancel } = this.props;
        const columns = this.initColumns();
        const pagination: any = {
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
                    className='dt-ant-table dt-ant-table--border'
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
