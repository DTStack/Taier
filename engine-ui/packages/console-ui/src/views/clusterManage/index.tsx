import * as React from 'react';
import { hashHistory } from 'react-router';
import { Table, Button, message, Popconfirm } from 'antd';
import moment from 'moment';
import AddEngineModal from '../../components/addEngineModal';
import Api from '../../api/console'
const PAGE_SIZE = 10;

class ClusterManage extends React.Component<any, any> {
    state: any = {
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: true
        },
        newClusterModal: false,
        editModalKey: ''
    }
    componentDidMount () {
        this.getResourceList();
    }
    getResourceList () {
        const { table } = this.state;
        const { pageIndex } = table;
        Api.getClusterList({
            currentPage: pageIndex,
            pageSize: PAGE_SIZE
        })
            .then(
                (res: any) => {
                    if (res.code == 1) {
                        this.setState({
                            dataSource: res.data.data,
                            table: {
                                ...table,
                                loading: false,
                                total: res.data.totalCount
                            }
                        })
                    } else {
                        this.setState({
                            table: {
                                ...table,
                                loading: false
                            }
                        })
                    }
                }
            )
    }
    getPagination () {
        const { pageIndex, total } = this.state.table;
        return {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    initTableColumns () {
        return [
            {
                title: '集群名称',
                dataIndex: 'clusterName'
            },
            {
                title: '修改时间',
                dataIndex: 'gmtModified',
                render (text: any) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                width: '170px',
                render: (text: any, record: any) => {
                    return (
                        <div>
                            <a onClick={this.viewCluster.bind(this, record)}>查看</a>
                            <span className="ant-divider" ></span>
                            <Popconfirm
                                placement="topRight"
                                title={`删除集群后不可恢复，确认删除集群 ${record.clusterName}?`}
                                onConfirm={this.deleteCluster.bind(this, record)}
                                okText="确认"
                                cancelText="取消"
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </div>
                    )
                }
            }
        ]
    }
    deleteCluster (item: any) {
        Api.deleteCluster({
            clusterId: item.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('集群删除成功');
                this.getResourceList();
            }
        })
    }
    viewCluster (item: any) {
        hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
                cluster: item,
                mode: 'view'
            }
        })
    }
    newCluster = () => {
        this.setState({
            editModalKey: Math.random(),
            newClusterModal: true
        })
    }
    onCancel () {
        this.setState({ newClusterModal: false })
    }

    onSubmit (params: any) {
        Api.addCluster({ ...params }).then((res: any) => {
            if (res.code === 1) {
                this.onCancel()
                hashHistory.push({
                    pathname: '/console/clusterManage/editCluster',
                    state: {
                        mode: 'new',
                        cluster: res.data
                    }
                })
                message.success('集群新增成功！')
            }
        })
    }
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const queryParams = Object.assign(this.state.table, { pageIndex: pagination.current, loading: true })
        this.setState({
            table: queryParams
        }, this.getResourceList)
    }
    render () {
        const { dataSource, table, newClusterModal, editModalKey } = this.state;
        const { loading } = table;
        const columns = this.initTableColumns();
        return (
            <React.Fragment>
                <div className="c-clusterManage__title">
                    <span className="c-clusterManage__title__span">多集群管理</span>
                    <Button className="c-clusterManage__title__btn" type="primary" onClick={this.newCluster}>新增集群</Button>
                </div>
                <div className="contentBox">
                    <Table
                        rowKey={(record: any, index: any) => {
                            return `clusterManage-${record.id}`
                        }}
                        className="dt-table-border dt-table-last-row-noborder"
                        pagination={this.getPagination()}
                        loading={loading}
                        dataSource={dataSource}
                        columns={columns}
                        onChange={this.handleTableChange}
                    />
                </div>
                <AddEngineModal
                    key={editModalKey}
                    title='新增集群'
                    visible={newClusterModal}
                    onCancel={this.onCancel.bind(this)}
                    onOk={this.onSubmit.bind(this)}
                />
            </React.Fragment>
        )
    }
}

export default ClusterManage;
