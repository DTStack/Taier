import * as React from 'react';
import { hashHistory } from 'react-router';
import { Card, Table, Button, message } from 'antd';
import AddEngineModal from '../../components/addEngineModal';
import moment from 'moment';
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
                dataIndex: 'clusterName',
                width: '400px'
            },
            {
                title: '修改时间',
                dataIndex: 'gmtModified',
                width: '300px',
                render (text: any) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                width: '400px',
                render: (text: any, record: any) => {
                    return (
                        <div>
                            <a onClick={this.editCluster.bind(this, record)}>修改</a>
                            <span className="ant-divider" ></span>
                            <a onClick={this.viewCluster.bind(this, record)}>查看</a>
                        </div>
                    )
                }
            }
        ]
    }
    editCluster (item: any) {
        hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
                cluster: item,
                mode: 'edit'
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
        // this.setState({
        //     editModalKey: Math.random(),
        //     newClusterModal: true
        // })
        hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
                mode: 'new'
            }
        })
    }
    onCancel () {
        this.setState({ newClusterModal: false })
    }

    onSubmit (params: any) {
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.addCluster({ ...reqParams }).then((res: any) => {
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

        const cardTitle = (
            <div>多集群管理 <Button type="primary" onClick={this.newCluster} style={{ float: 'right', marginTop: '9px' }}>新增集群</Button></div>
        )
        return (
            <div className="contentBox m-card">
                <Card
                    noHovering
                    title={cardTitle}
                >
                    <Table
                        rowKey={(record: any) => {
                            return record.id
                        }}
                        className="m-table"
                        pagination={this.getPagination()}
                        loading={loading}
                        dataSource={dataSource}
                        columns={columns}
                        onChange={this.handleTableChange}
                    />
                </Card>
                <AddEngineModal
                    key={editModalKey}
                    title='新增集群'
                    visible={newClusterModal}
                    onCancel={this.onCancel.bind(this)}
                    onOk={this.onSubmit.bind(this)}
                />
            </div>
        )
    }
}

export default ClusterManage;
