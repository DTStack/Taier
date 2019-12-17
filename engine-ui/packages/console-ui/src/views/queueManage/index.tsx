/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 16:36:23
*/
import * as React from 'react';
import { Table, Tabs, Select, Card, Button } from 'antd';
import utils from 'dt-common/src/utils'
import { isNaN } from 'lodash';
import Api from '../../api/console';
import '../../styles/main.scss';
import TaskDetail from './taskDetail';

const PAGE_SIZE = 10;
const Option = Select.Option;
class QueueManage extends React.Component<any, any> {
    state: any = {
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: false
        },
        nowView: utils.getParameterByName('tab') || 'overview',
        clusterList: [],
        clusterId: undefined,
        nodeList: [],
        // 节点值
        node: undefined,
        // 会重新渲染detail组件
        resetKey: Math.random()
    }

    componentDidMount () {
        this.getClusterSelect();
        this.getNodeAddressSelect();
        this.getClusterDetail();
    }
    // 渲染集群
    getClusterDetail () {
        const { table, node, clusterList } = this.state;
        const { pageIndex } = table;
        let { clusterId } = this.state;
        if (isNaN(Number(clusterId))) { // clusterName 转化 id
            clusterList.map((item: any) => {
                if (item.clusterName == clusterId) {
                    return clusterId = item.id
                }
            })
        }
        if (node) {
            this.setState({
                table: {
                    ...table,
                    loading: true
                }
            })
            Api.getClusterDetail({
                currentPage: pageIndex,
                pageSize: PAGE_SIZE,
                clusterId: clusterId,
                node: node
            }).then((res: any) => {
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
            })
        }
    }
    // 获取集群下拉数据
    getClusterSelect () {
        return Api.getAllCluster().then((res: any) => {
            if (res.code == 1) {
                const data = res.data;
                this.setState({
                    clusterList: data || [],
                    clusterId: data && data[0] && data[0].clusterName // 首次取第一项集群名称展示
                })
            }
        })
    }
    // 获取集群下拉视图
    getClusterOptionView () {
        const clusterList = this.state.clusterList;
        return clusterList.map((item: any, index: any) => {
            return <Option key={item.id} value={`${item.id}`}>{item.clusterName}</Option>
        })
    }
    // 集群option改变
    clusterOptionChange (clusterId: any) {
        const { node } = this.state;
        const { table } = this.state;
        if (!clusterId) {
            this.setState({
                dataSource: [],
                clusterId: undefined,
                node: node,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            })
        } else {
            this.setState({
                clusterId: clusterId,
                node: node
            }, (node) ? this.getClusterDetail.bind(this) : null)
        }
    }
    // 获取节点下拉数据
    getNodeAddressSelect () {
        return Api.getNodeAddressSelect().then((res: any) => {
            if (res.code == 1) {
                const data = res.data;
                this.setState({
                    nodeList: data || [],
                    node: data && data.length ? data[0] : undefined
                }, this.getClusterDetail.bind(this))
                console.log(data);
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
    nodeAddressrOptionChange (value: any) {
        const { table } = this.state;
        if (!value) {
            this.setState({
                dataSource: [],
                node: value,
                table: {
                    ...table,
                    loading: false,
                    total: 0
                }
            })
        } else {
            this.setState({
                node: value
            }, this.getClusterDetail.bind(this))
        }
    }
    // 页表
    getPagination () {
        const { pageIndex, total } = this.state.table;
        return {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
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
    initTableColumns () {
        return [
            {
                title: '组件',
                dataIndex: 'engine',
                render (text: any, record: any) {
                    return record.engineType;
                }
            },
            {
                title: 'group名称',
                dataIndex: 'groupName',
                render (text: any, record: any) {
                    return record.groupName;
                }
            },
            {
                title: '头部等待时长',
                dataIndex: 'headWait',
                render (text: any, record: any) {
                    return record.waitTime
                }
            },
            {
                title: '总任务数',
                dataIndex: 'totalCount',
                render (text: any, record: any) {
                    return record.groupSize;
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text: any, record: any) => {
                    return (
                        <div>
                            <a onClick={this.viewDetails.bind(this, record)}>查看明细</a>
                        </div>
                    )
                }
            }
        ]
    }
    // 查看明细(需要传入参数 集群,引擎,group) detailInfo
    viewDetails (record: any) {
        this.props.router.push({
            pathname: '/console/queueManage',
            query: {
                tab: 'detail',
                clusterName: record.clusterName,
                engineType: record.engineType,
                groupName: record.groupName,
                node: this.state.node

            }
        });
        this.setState({
            nowView: 'detail',
            resetKey: Math.random()
        })
    }
    // 面板切换
    handleClick (e: any) {
        this.setState({
            nowView: e
        })
        if (e == 'detail') {
            this.setState({
                resetKey: Math.random()
            })
        }
        this.props.router.push({
            pathname: '/console/queueManage',
            query: {
                tab: e
            }
        });
    }

    render () {
        const columns = this.initTableColumns();
        const { dataSource, table, clusterList, nodeList, clusterId, nowView, node } = this.state;
        const { loading } = table;
        const query = this.props.router.location.query;
        return (
            <div className=" api-mine nobackground m-card height-auto m-tabs" style={{ marginTop: '20px' }}>
                <Card
                    style={{ marginTop: '0px' }}
                    className="box-1"
                    noHovering
                >
                    <Tabs
                        style={{ overflow: 'unset' }}
                        animated={false}
                        onChange={this.handleClick.bind(this)}
                        activeKey={nowView}
                    >
                        <Tabs.TabPane tab="概览" key="overview">
                            <div style={{ margin: '20px' }}>
                                集群：
                                <Select style={{ width: 150, marginRight: '10px' }}
                                    placeholder="选择集群"
                                    allowClear
                                    onChange={this.clusterOptionChange.bind(this)}
                                    value={clusterId}
                                >
                                    {
                                        this.getClusterOptionView()
                                    }
                                </Select>

                                 节点：
                                <Select style={{ width: 150 }}
                                    placeholder="选择节点"
                                    allowClear={true}
                                    // defaultValue="1"
                                    onChange={this.nodeAddressrOptionChange.bind(this)}
                                    value={node}
                                >
                                    {
                                        this.getNodeAddressOptionView()
                                    }
                                </Select>
                                <div style={{ float: 'right' }}>
                                    <Button onClick={this.getClusterDetail.bind(this)}>刷新</Button>
                                </div>
                            </div>
                            <Table
                                rowKey={(record: any) => {
                                    return record.clusterId
                                }}
                                className="m-table s-table"
                                loading={loading}
                                columns={columns}
                                dataSource={dataSource}
                                pagination={this.getPagination()}
                                onChange={this.onTableChange}
                            >
                            </Table>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="明细" key="detail">
                            <TaskDetail
                                key={this.state.resetKey}
                                clusterList={clusterList}
                                nodeList={nodeList}
                                query={query}
                            >
                            </TaskDetail>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>
            </div>
        )
    }
}
export default QueueManage;
