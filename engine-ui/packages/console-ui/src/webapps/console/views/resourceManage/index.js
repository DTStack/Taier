import React from 'react';
import { Row, Col, Select, Button, Card, Form, Tabs, Table, Input, message } from 'antd';
import Api from '../../api/console';
import { ENGINE_TYPE, ENGINE_TYPE_NAME } from '../../consts';
import BindCommModal from '../../components/bindCommModal';
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const Search = Input.Search;
const PAGESIZE = 20;
class ResourceManage extends React.Component {
    state = {
        tableData: [],
        clusterList: [],
        engineList: [],
        queryParams: {
            clusterId: '',
            engineType: '',
            tenantName: '',
            pageSize: PAGESIZE,
            currentPage: 1
        },
        loading: false,
        total: 0,
        tenantModal: false,
        queueModal: false,
        tenantInfo: '',
        isHaveHadoop: false,
        isHaveLibra: false,
        queueList: [], // hadoop资源队列
        modalKey: '',
        editModalKey: ''
    }
    componentDidMount () {
        this.initList()
    }
    searchTenant = () => {
        const { queryParams } = this.state;
        this.setState({
            loading: true
        })
        Api.searchTenant(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data.data || [],
                    total: res.totalCount,
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }
    bindTenant (params) {
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.bindTenant({ ...reqParams }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        tenantModal: false
                    })
                    message.success('租户绑定成功')
                    this.searchTenant()
                }
            })
        }
    }
    switchQueue (params) {
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.switchQueue({ ...reqParams }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        queueModal: false
                    })
                    message.success('切换队列成功')
                    this.searchTenant()
                }
            })
        }
    }
    initList = async () => {
        const res = await Api.getAllCluster();
        if (res.code === 1) {
            const data = res.data || [];
            const engineList = (data[0] && data[0].engines) || [];
            const initCluster = data[0] || [];
            const initEngine = engineList[0] || [];
            this.setState({
                clusterList: data,
                engineList,
                loading: true
            })
            const queryParams = Object.assign(this.state.queryParams, {
                clusterId: initCluster.clusterId,
                engineType: initEngine.engineType
            })
            const response = await Api.searchTenant(queryParams)
            if (response.code === 1) {
                this.setState({
                    tableData: response.data.data || [],
                    total: response.totalCount,
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        }
    }
    clusterOptions = () => {
        const { clusterList } = this.state;
        return clusterList.map(item => {
            return <Option key={`${item.clusterId}`} value={`${item.clusterId}`}>{item.clusterName}</Option>
        })
    }
    handleChangeCluster = (value) => {
        const { clusterList } = this.state;
        let currentCluster;
        currentCluster = clusterList.filter(clusItem => clusItem.clusterId == value); // 选中当前集群
        const currentEngineList = (currentCluster[0] && currentCluster[0].engines) || [];
        const queryParams = Object.assign(this.state.queryParams, {
            clusterId: value,
            engineType: currentEngineList[0] && currentEngineList[0].engineType,
            tenantName: '',
            pageSize: PAGESIZE,
            currentPage: 1
        })
        this.setState({
            engineList: currentEngineList,
            queryParams
        }, this.searchTenant)
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
        console.log('key', key)
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
    initHadoopColumns = () => {
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
                    return <a onClick={ () => { this.clickSwitchQueue(record) }}>
                        切换队列
                    </a>
                }
            }
        ]
    }
    initLibraColumns = () => {
        return [{
            title: '租户',
            dataIndex: 'tenantName',
            render (text, record) {
                return text
            }
        }]
    }
    render () {
        const hadoopColumns = this.initHadoopColumns();
        const libraColumns = this.initLibraColumns()
        const { tableData, queryParams, total, loading, engineList,
            tenantModal, queueModal, modalKey, editModalKey } = this.state;
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
                        <Button className='terent-button' type='primary' onClick={() => { this.setState({ editModalKey: Math.random(), tenantModal: true }) }}>绑定新租户</Button>
                    </Col>
                </Row>
                <div className="resource-content">
                    <Card
                        className='console-tabs resource-tab-width'
                        bordered={false}
                    >
                        <Tabs
                            tabPosition='left'
                            defaultActiveKey={`${engineList[0] && engineList[0].engineType}`}
                            onChange={this.handleEngineTab}
                            forceRender={true}
                        >
                            {
                                engineList && engineList.map(item => {
                                    const isHadoop = item.engineType == ENGINE_TYPE.HADOOP
                                    const engineName = isHadoop ? ENGINE_TYPE_NAME.HADOOP : ENGINE_TYPE_NAME.LIBRA
                                    return (
                                        <TabPane className='tab-pane-wrapper' tab={engineName} key={`${item.engineType}`}>
                                            <Tabs
                                                className='engine-detail-tabs'
                                                tabPosition='top'
                                            >
                                                <TabPane tab="租户绑定" key="tenant">
                                                    <div style={{ margin: 15 }}>
                                                        <Search
                                                            style={{ width: '200px', marginBottom: '20' }}
                                                            placeholder='按租户名称搜索'
                                                            value={queryParams.tenantName}
                                                            onChange={(e) => {
                                                                this.setState({
                                                                    queryParams: Object.assign(this.state.queryParams, { tenantName: e.target.value })
                                                                })
                                                            } }
                                                            onSearch={this.changeTenantName}
                                                        />
                                                        <Table
                                                            className='m-table border-table'
                                                            loading={loading}
                                                            columns={isHadoop ? hadoopColumns : libraColumns}
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
                <BindCommModal
                    key={editModalKey}
                    title='绑定新租户'
                    visible={tenantModal}
                    isBindTenant={true}
                    onCancel={() => { this.setState({ tenantModal: false }) }}
                    onOk={this.bindTenant.bind(this)}
                />
                <BindCommModal
                    key={modalKey}
                    title='切换队列'
                    visible={queueModal}
                    isBindTenant={false}
                    tenantInfo={this.state.tenantInfo}
                    clusterId={this.state.queryParams.clusterId}
                    disabled={true}
                    onCancel={() => {
                        this.setState({
                            queueModal: false
                        })
                    }}
                    onOk={this.switchQueue.bind(this)}
                />
            </div>
        )
    }
}
export default ResourceManage;
