/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 16:36:23
*/
import * as React from 'react';
import styled from 'styled-components';

import { Table, Select, Card, Button, Modal, message } from 'antd';
import Api from '../../api/console';
import '../../styles/main.scss';

import { JobStage } from '../../consts/index';
import Resource from '../../components/resource';

const Option = Select.Option;

export const RedTxt = styled.span`
    color: #FF5F5C;
`

class QueueManage extends React.Component<any, any> {
    state = {
        dataSource: [],
        table: {
            loading: false
        },
        clusterList: [],
        clusterId: undefined,
        clusterMap: {},
        nodeList: [],
        // 节点值
        node: undefined,
        // 剩余资源
        isShowResource: false,
        editModalKey: null
    }

    componentDidMount () {
        this.getClusterSelect();
    }

    // 渲染集群
    getClusterDetail () {
        const { table, node } = this.state;
        let { clusterId } = this.state;
        const cluster = this.getClusterItem(clusterId);
        if (cluster) {
            this.setState({
                table: {
                    ...table,
                    loading: true
                }
            })
            Api.getClusterDetail({
                clusterName: cluster.clusterName,
                nodeAddress: node
            }).then((res: any) => {
                if (res.code == 1) {
                    let data = res.data;
                    this.setState({
                        dataSource: data || [],
                        table: {
                            ...table,
                            loading: false
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
            })
        }
    }

    // 获取集群下拉数据
    getClusterSelect () {
        return Api.getAllCluster().then((res: any) => {
            if (res.code == 1) {
                const data = res.data;
                let clusterMap: any = {};
                let clusterList: any = [];
                if (Array.isArray(data)) {
                    clusterMap = data.reduce((pre, curr) => {
                        return {
                            ...pre,
                            [curr.id]: curr.clusterName
                        }
                    }, {});
                    clusterList = data.map(item => {
                        return {
                            ...item,
                            id: item.id + ''
                        }
                    })
                }
                this.setState({
                    clusterList,
                    clusterMap,
                    clusterId: data && data[0] && data[0].id + '' // 首次取第一项集群名称展示
                }, () => {
                    this.getNodeAddressSelect();
                })
            }
        })
    }
    // 获取集群下拉视图
    getClusterOptionView () {
        const clusterList = this.state.clusterList;
        return clusterList.map((item: any, index: any) => {
            return <Option key={item.id} value={`${item.id}`} data-item={item}>{item.clusterName}</Option>
        })
    }

    // 集群option改变
    clusterOptionChange (clusterId: any) {
        this.setState({
            clusterId: clusterId
        }, this.getClusterDetail)
    }

    // 获取节点下拉数据
    getNodeAddressSelect () {
        return Api.getNodeAddressSelect().then((res: any) => {
            if (res.code == 1) {
                const data = res.data;
                this.setState({
                    nodeList: data || []
                    // node: data && data.length ? data[0] : undefined
                }, this.getClusterDetail.bind(this))
            }
        })
    }

    // 获取节点下拉视图
    getNodeAddressOptionView () {
        const { nodeList } = this.state;
        return nodeList.map((item: any, index: any) => {
            return <Option key={index} value={item}>{item}</Option>
        })
    }
    // 节点option改变
    nodeAddressOptionChange (value: any) {
        this.setState({
            node: value
        }, this.getClusterDetail.bind(this))
    }

    // 表格换页
    onTableChange = (pagination: any, filters: any, sorter: any) => {
        const table = Object.assign(this.state.table, { pageIndex: pagination.current })
        this.setState({
            table
        },
        () => {
            this.getClusterDetail();
        })
    }

    handleKillAll = (e: any) => {
        this.setState({
            isShowAllKill: true,
            isKillAllTasks: true
        })
    }

    // 剩余资源
    handleClickResource () {
        this.setState({
            isShowResource: true,
            editModalKey: Math.random()
        })
    }

    handleCloseResource () {
        this.setState({
            isShowResource: false
        })
    }

    // 查看明细(需要传入参数 集群,引擎,group) detailInfo
    viewDetails (record: any, jobStage: JobStage) {
        let { clusterId, node } = this.state;
        const cluster = this.getClusterItem(clusterId);
        this.props.router.push({
            pathname: '/console/queueManage/detail',
            query: {
                node: node,
                jobStage: jobStage,
                clusterName: cluster.clusterName,
                engineType: record.engineType,
                groupName: record.groupName,
                jobResource: record.jobResource
            }
        });
    }

    getClusterItem = (clusterId: string) => {
        const { clusterList = [] } = this.state;
        return clusterList.find(cluster => cluster.id === clusterId);
    }

    onKillAllTask (record: any) {
        const { node } = this.state;
        const ctx = this;
        Modal.confirm({
            title: '杀死全部',
            okText: '杀死全部',
            okType: 'danger',
            cancelText: '取消',
            width: '460px',
            iconType: 'close-circle',
            content: <div style={{ fontSize: '14px' }}>
                <p>杀死所有<RedTxt>队列中、已存储、等待重试、等待资源</RedTxt>的任务</p>
                <p><RedTxt>运行中的任务不会被杀死</RedTxt>，可点击运行中的任务，并执行批量杀死操作</p>
            </div>,
            async onOk () {
                const res = await Api.killAllTask({
                    engineType: record.engineType,
                    jobResource: record.jobResource,
                    nodeAddress: node,
                    groupName: record.groupName
                });
                if (res.code === 1) {
                    message.success('杀死全部成功！');
                    ctx.getClusterDetail();
                }
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    initTableColumns () {
        const colText = (text: string, record: any, jobStage: JobStage) => (
            <a onClick={this.viewDetails.bind(this, record, jobStage)}>{ text || 0 }</a>
        );
        return [
            {
                title: '计算类型',
                dataIndex: 'engineType',
                render (text: any, record: any) {
                    return colText(text, record, JobStage.Queueing);
                }
            },
            {
                title: '队列中(等待时长)',
                width: 220,
                dataIndex: 'priorityJobSize',
                render (text: any, record: any) {
                    const txt = text + (record.priorityWaitTime ? ` (${record.priorityWaitTime})` : '');
                    return colText(txt, record, JobStage.Queueing);
                }
            },
            {
                title: '已存储',
                dataIndex: 'dbJobSize',
                render (text: any, record: any) {
                    return colText(text, record, JobStage.Saved);
                }
            },
            {
                title: '等待重试',
                dataIndex: 'restartJobSize',
                render (text: any, record: any) {
                    return colText(text, record, JobStage.WaitTry);
                }
            },
            {
                title: '等待资源',
                dataIndex: 'lackingJobSize',
                render (text: any, record: any) {
                    return colText(text, record, JobStage.WaitResource);
                }
            },
            {
                title: '运行中',
                dataIndex: 'submittedJobSize',
                render (text: any, record: any) {
                    return colText(text, record, JobStage.Running);
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text: any, record: any) => {
                    return (
                        <div>
                            <a onClick={this.onKillAllTask.bind(this, record)}>杀死全部</a>
                        </div>
                    )
                }
            }
        ]
    }

    render () {
        const columns = this.initTableColumns();
        const {
            dataSource, table, clusterId, node,
            editModalKey, clusterList, isShowResource
        } = this.state;
        const { loading } = table;

        return (
            <div className=" api-mine nobackground m-card height-auto" style={{ marginTop: '20px' }}>
                <div style={{ margin: '20px' }}>
                    集群：
                    <Select
                        size="large"
                        style={{ width: 150, marginRight: '10px' }}
                        placeholder="选择集群"
                        onChange={this.clusterOptionChange.bind(this)}
                        value={clusterId}
                    >
                        {
                            this.getClusterOptionView()
                        }
                    </Select>
                        节点：
                    <Select
                        size="large"
                        style={{ width: 150 }}
                        placeholder="选择节点"
                        allowClear={true}
                        onChange={this.nodeAddressOptionChange.bind(this)}
                        value={node}
                    >
                        {
                            this.getNodeAddressOptionView()
                        }
                    </Select>
                    <div style={{ float: 'right' }}>
                        <Button size="large" type="primary" onClick={this.handleClickResource.bind(this)}>剩余资源</Button>
                        <Button size="large" style={{ marginLeft: '8px' }} onClick={this.getClusterDetail.bind(this)}>刷新</Button>
                    </div>
                </div>
                <Card
                    style={{ marginTop: '0px' }}
                    className="box-1"
                    noHovering
                >
                    <Table
                        rowKey={(record: any) => {
                            return record.clusterId
                        }}
                        className="m-table s-table"
                        loading={loading}
                        columns={columns}
                        dataSource={dataSource}
                        onChange={this.onTableChange}
                    >
                    </Table>
                </Card>
                <Resource
                    key={editModalKey}
                    visible={isShowResource}
                    onCancel={this.handleCloseResource.bind(this)}
                    clusterList={clusterList}
                />
            </div>
        )
    }
}
export default QueueManage;
