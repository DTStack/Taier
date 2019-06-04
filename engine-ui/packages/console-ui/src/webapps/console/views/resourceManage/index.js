import React from 'react';
import { Row, Col, Select, Button, Card, Form, Tabs, Table, Input } from 'antd';
import Api from '../../api/console';
import { ENGINE_TYPE } from '../../consts';
import BindTenantModal from '../../components/bindTenant';
import SwitchQueue from '../../components/switchQueue';
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const Search = Input.Search;
const PAGESIZE = 20;
class ResourceManage extends React.Component {
    state = {
        tableData: [{
            'tenantId': 1,
            'tenantName': 'dtstack',
            'queue': 'default.a',
            'queueId': 1,
            'maxCapacity': 10.1,
            'minCapacity': 10.1
        },
        {
            'tenantId': 1,
            'tenantName': 'dtstack.b',
            'queue': 'default.b',
            'queueId': 1,
            'maxCapacity': 10.1,
            'minCapacity': 10.1
        }],
        allClusterList: [],
        engineList: [
            'hadoop',
            'libra'
        ],
        queryParams: {
            clusterId: '',
            engineType: '',
            tenantName: '',
            pageSize: PAGESIZE,
            currentPage: 1
        },
        total: 0,
        tenantModal: false,
        queueModal: false,
        tenantInfo: {},
        clusterInfo: {}, // 集群信息
        isHaveHadoop: false,
        isHaveLibra: false,
        queueList: [], // hadoop资源队列
        modalKey: '',
        editModalKey: ''
    }
    componentDidMount () {
        this.getAllClusterLists() // 获取集群(含集群下引擎列表)
        this.searchTenant(); // 根据集群、引擎获取数据
    }
    searchTenant = (params) => {
        // const queryParams = Object.assign(this.state.queryParams, {
        //     clusterId: this.state.allClusterList[0],
        //     engineType: this.state.engineList[0]
        // });
        const { queryParams } = this.state;
        Api.searchTenant(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data || [],
                    total: res.totalCount
                })
            }
        })
    }
    getAllClusterLists = async () => {
        const res = await Api.getAllCluster();
        if (res.code === 1) {
            this.setState({
                allClusterList: res.data || []
            })
        }
    }
    clusterOptions = () => {
        const { allClusterList } = this.state;
        allClusterList.map(item => {
            return <Option key={`${item.clusterId}`} value={`${item.clusterId}`}>{item.clusterName}</Option>
        })
    }
    handleChangeCluster = (value) => {
        // Api.getEngineListByCluster({
        //     clusterId: value
        // }).then(res => {
        //     if (res.code === 1) {
        //         this.setState({
        //             engineList: res.data || [],
        //             queryParams: Object.assign(this.state.queryParams, { clusterId: value, engineType: res.data[0] })
        //         }, this.searchTenant)
        //     }
        // })
        const { allClusterList } = this.state;
        // let engines = []
        // 选中集群
        allClusterList.forEach(item => {
            if (item.clusterId === value) {
                this.setState({
                    clusterInfo: item || {}
                })
                item.engines && item.engines.map(engine => {
                    if (engine.engineType === ENGINE_TYPE.HADOOP) {
                        this.setState({
                            queueList: engine.queues,
                            isHaveHadoop: true
                        })
                    }
                    if (engine.engineType === ENGINE_TYPE.LIBRA) {
                        this.setState({
                            isHaveLibra: true
                        })
                    }
                })
            }
        })
        // 区分engine
        // engines.forEach(item => {
        //     if (item.engineType === ENGINE_TYPE.HADOOP) {
        //         this.setState({
        //             queueList: item.queues,
        //             isShowHadoop: true
        //         })
        //     }
        //     if (item.engineType === ENGINE_TYPE.LIBRA) {
        //         this.setState({
        //             isShowHadoop: true
        //         })
        //     }
        // })
    }
    changeTenantName = (value) => {
        const queryParams = Object.assign(this.state.queryParams, { tenantName: value })
        this.setState({
            queryParams
        }, this.searchTenant)
    }
    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.searchTenant)
    }
    handleEngineTab = (key) => {
        const queryParams = Object.assign(this.state.queryParams, {
            engineType: key,
            tenantName: '',
            currentPage: 1
        })
        this.setState({
            queryParams
        }, this.searchTenant)
    }
    showTenant () {
        this.setState({ tenantModal: true, editModalKey: Math.random() })
    }
    clickSwitchQueue = (record) => {
        this.setState({
            modalKey: Math.random(),
            queueModal: true,
            tenantInfo: record
        })
    }
    initColumns = () => {
        return [
            {
                title: '租户',
                dataIndex: 'tenantName',
                render (text, record) {
                    return text
                }
            },
            {
                title: '资源队列',
                dataIndex: 'queue',
                render (text, record) {
                    return text
                }
            },
            {
                title: '最小容量（%）',
                dataIndex: 'minCapacity',
                render (text, record) {
                    return text
                }
            },
            {
                title: '最大容量（%）',
                dataIndex: 'maxCapacity',
                render (text, record) {
                    return text
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text, record) => {
                    return <a onClick={this.clickSwitchQueue.bind(this, record)}>
                        切换队列
                    </a>
                }
            }
        ]
    }
    render () {
        const columns = this.initColumns();
        const { tableData, queryParams, total, engineList,
            tenantModal, queueModal, tenantInfo, clusterInfo,
            queueList, isHaveHadoop, isHaveLibra, modalKey, editModalKey } = this.state;
        const pagination = {
            current: queryParams.currentPage,
            pageSize: PAGESIZE,
            total
        }
        return (
            <div className='resource-wrapper'>
                <Row>
                    <Col span='12'>
                        <Form className="m-form-inline" layout="inline">
                            <FormItem
                                label='集群'
                            >
                                <Select
                                    className='cluster-select'
                                    style={{ width: '180' }}
                                    placeholder='请选择集群'
                                    onChange={this.handleChangeCluster}
                                >
                                    {this.clusterOptions()}
                                </Select>
                            </FormItem>
                        </Form>
                    </Col>
                    <Col span='12'>
                        <Button className='terent-button' type='primary' onClick={() => { this.showTenant() }}>绑定新租户</Button>
                    </Col>
                </Row>
                <div className="resource-content">
                    <Card
                        className='console-tabs resource-tab-width'
                        bordered={false}
                    >
                        <Tabs
                            tabPosition='left'
                            onChange={this.handleEngineTab}
                        >
                            {
                                engineList && engineList.map(item => {
                                    return (
                                        <TabPane className='tab-pane-wrapper' tab={item} key={item}>
                                            <Tabs
                                                className='engine-detail-tabs'
                                                tabPosition='top'
                                            >
                                                <TabPane tab="租户绑定" key="tenant">
                                                    <div style={{ margin: 15 }}>
                                                        <Search
                                                            style={{ width: '200px', marginBottom: '20' }}
                                                            placeholder='按租户名称搜索'
                                                            onSearch={this.changeTenantName}
                                                        />
                                                        <Table
                                                            className='m-table border-table'
                                                            columns={columns}
                                                            dataSource={tableData}
                                                            pagination={pagination}
                                                            onChange={this.handleTableChange}
                                                        />
                                                    </div>
                                                </TabPane>
                                                {/* <TabPane tab="队列管理" key="queue">队列管理</TabPane> */}
                                            </Tabs>
                                        </TabPane>
                                    )
                                })
                            }
                        </Tabs>
                    </Card>
                </div>
                <BindTenantModal
                    key={editModalKey}
                    visible={tenantModal}
                    onCancel={() => { this.setState({ tenantModal: false }) }}
                />
                <SwitchQueue
                    key={modalKey}
                    visible={queueModal}
                    tenantInfo={tenantInfo}
                    queueList={queueList}
                    clusterInfo={clusterInfo}
                    isHaveHadoop={isHaveHadoop}
                    isHaveLibra={isHaveLibra}
                    searchTenant={this.searchTenant}
                    onCancel={() => { this.setState({ queueModal: false }) }}
                />
            </div>
        )
    }
}
export default ResourceManage;
