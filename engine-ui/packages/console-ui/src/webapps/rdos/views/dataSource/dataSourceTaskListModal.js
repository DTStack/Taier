import React from 'react';
import { Table, Modal, Button } from 'antd';
import { connect } from 'react-redux';

import Api from '../../api'
import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction'

@connect((state) => {
    return {
        project: state.project
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
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
        const { type, dataSource } = this.props;
        this.setState({
            loading: true
        })
        const params = {
            sourceId: dataSource.id,
            pageSize: pagination.pageSize,
            currentPage: pagination.current
        }
        let func = '';
        if (type == 'stream') {
            func = 'getTaskOfStreamSource';
        } else if (type == 'offline') {
            func = 'getTaskOfOfflineSource';
        }
        Api[func](params)
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
        const { type } = this.props;
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
                if (type == 'stream') {
                    return <a target="_blank" href={`${location.pathname}#/realtime?taskId=${record.id}`}>编辑</a>
                } else {
                    return <a target="_blank" href={`${location.pathname}#/offline?taskId=${record.id}`}>编辑</a>
                }
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
