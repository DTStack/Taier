import React, { Component } from 'react'

import { Modal, Table, Button } from 'antd'
import Api from '../../../../api'
import moment from 'moment';
class OperaRecordModal extends Component {
    state = {
        dataSource: [],
        queryParams: {
            taskId: undefined,
            currentPage: 1,
            pageSize: 20
        },
        total: 0,
        loading: false
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const { currentNodeData } = nextProps;
        if (this.props.currentNodeData && currentNodeData.id && this.props.currentNodeData.id != currentNodeData.id) {
            this.setState({
                queryParams: Object.assign({}, this.state.queryParams, {
                    taskId: currentNodeData.id
                })
            }, () => {
                this.getOperaRecordData(this.state.queryParams)
            })
        }
    }
    getOperaRecordData = (param) => {
        this.setState({
            loading: true
        })
        Api.operaRecordData(param).then(res => {
            if (res.code === 1) {
                this.setState({
                    dataSource: res.data.data || [],
                    total: res.data.totalCount,
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }
    handleTableChange = (pagination, filter) => {
        this.setState({
            queryParams: Object.assign({}, this.state.queryParams, {
                currentPage: pagination.current
            })
        }, this.getOperaRecordData(this.state.queryParams))
    }
    initColumns = () => {
        return [
            {
                title: '时间',
                width: 140,
                dataIndex: 'operateTime',
                render (text) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作人',
                width: 140,
                dataIndex: 'operatorName'
            },
            {
                title: '操作',
                width: 140,
                dataIndex: 'operateType'
            }
        ]
    }
    render () {
        const columns = this.initColumns();
        const { dataSource, queryParams, total, loading } = this.state;
        const { visible, onCancel } = this.props;
        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total
        }
        return (
            <Modal
                title="操作记录"
                visible={visible}
                onCancel={onCancel}
                footer={
                    <Button type='primary' onClick={onCancel}>关闭</Button>
                }
            >
                <Table
                    className='m-table'
                    loading={loading}
                    rowKey="operateTime"
                    dataSource={dataSource}
                    columns={columns}
                    pagination={pagination}
                    onChange={this.handleTableChange}
                />
            </Modal>
        )
    }
}
export default OperaRecordModal;
