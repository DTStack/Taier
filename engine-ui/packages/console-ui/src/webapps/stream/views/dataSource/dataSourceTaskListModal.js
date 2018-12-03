import React from 'react';
import { Table, Modal, Button } from 'antd';
import { connect } from 'react-redux';

import Api from '../../api'

@connect((state) => {
    return {
        project: state.project
    }
})
class DataSourceTaskListModal extends React.Component {
    state = {
        visible: false,
        loading: false,
        taskList: [],
        pagination: {
            current: 1,
            pageSize: 10,
            total: 0
        }
    }
    closeModal () {
        this.setState({
            visible: false
        })
    }
    showModal () {
        this.setState({
            visible: true
        })
        this.getTaskList();
    }
    getTaskList () {
        const { pagination } = this.state;
        const { dataSource } = this.props;
        this.setState({
            loading: true
        })
        const params = {
            sourceId: dataSource.id,
            pageSize: pagination.pageSize,
            currentPage: pagination.current
        }
        Api.getTaskOfStreamSource(params)
            .then(
                (res) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        this.setState({
                            taskList: res.data.data || [],
                            pagination: {
                                ...pagination,
                                total: res.data.totalCount
                            }
                        })
                    }
                }
            )
    }
    initColumns () {
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '操作',
            dataIndex: 'edit',
            key: 'edit',
            width: '100px',
            render (t, record) {
                return <a rel="noopener noreferrer" target="_blank" href={`${location.pathname}#/realtime?taskId=${record.id}`}>编辑</a>
            }
        }]
    }
    handleTableChange (pagination) {
        this.setState({ pagination: pagination }, () => {
            this.getTaskList();
        })
    }
    render () {
        const { visible, pagination, loading, taskList } = this.state;
        const { dataSource = {}, children } = this.props;
        return (
            <div>
                <Modal
                    title={`数据源(${dataSource.dataName})应用状态`}
                    visible={visible}
                    maskClosable={false}
                    onCancel={this.closeModal.bind(this)}
                    footer={(
                        <Button type="primary" onClick={this.closeModal.bind(this)}>关闭</Button>
                    )}
                >
                    <Table
                        className="m-table"
                        rowKey="id"
                        pagination={pagination}
                        onChange={this.handleTableChange.bind(this)}
                        loading={loading}
                        columns={this.initColumns()}
                        dataSource={taskList}
                    />
                </Modal>
                <a onClick={this.showModal.bind(this)}>{children}</a>
            </div>
        )
    }
}

export default DataSourceTaskListModal;
