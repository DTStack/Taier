import * as React from 'react';
import { Table, Modal, Button, Input } from 'antd';
import { connect } from 'react-redux';

import Api from '../../api'
import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction'

const Search = Input.Search

@(connect((state: any) => {
    return {
        project: state.project
    }
}, (dispatch: any) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id: any) => {
            actions.openTaskInDev(id)
        }
    }
}) as any)
class DataSourceTaskListModal extends React.Component<any, any> {
    state: any = {
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
    getTaskList (reqParams?: any) {
        const { pagination } = this.state;
        const { type, dataSource } = this.props;
        this.setState({
            loading: true
        })
        const params = Object.assign({
            sourceId: dataSource.id,
            pageSize: pagination.pageSize,
            currentPage: pagination.current
        }, reqParams);

        let func = '';
        if (type == 'stream') {
            func = 'getTaskOfStreamSource';
        } else if (type == 'offline') {
            func = 'getTaskOfOfflineSource';
        }
        (Api as any)[func](params)
            .then(
                (res: any) => {
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

    search = (query: any) => {
        this.getTaskList({
            taskName: query,
            currentPage: 1
        })
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
            render (t: any, record: any) {
                if (type == 'stream') {
                    return <a target="_blank" rel="noopener noreferrer" href={`${location.pathname}#/realtime?taskId=${record.id}`}>编辑</a>
                } else {
                    return <a target="_blank" rel="noopener noreferrer" href={`${location.pathname}#/offline?taskId=${record.id}`}>编辑</a>
                }
            }
        }]
    }
    handleTableChange (pagination: any) {
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
                    <Search
                        placeholder="按任务名称搜索"
                        style={{ width: 200, marginBottom: '10px' }}
                        onSearch={this.search}
                    />
                    <Table
                        className="dt-ant-table dt-ant-table--border"
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
