import * as React from 'react';
import { Row, Col, Select, Button, Card, Form, Tabs, Table, Input, message } from 'antd';
import Api from '../../api/console';
import { connect } from 'react-redux';
import { getTenantList } from '../../actions/console'
import { ENGINE_TYPE, ENGINE_TYPE_NAME } from '../../consts';
import BindCommModal from '../../components/bindCommModal';
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const Search = Input.Search;
const PAGESIZE = 20;

function mapStateToProps (state: any) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps (dispatch: any) {
    return {
        getTenantList () {
            dispatch(getTenantList())
        }
    }
}
@(connect(mapStateToProps, mapDispatchToProps) as any)

class ResourceManage extends React.Component<any, any> {
    state: any = {
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
        editModalKey: null
    }
    componentDidMount () {
        this.props.getTenantList(); // 租户列表
        this.initList()
    }
    searchTenant = () => {
        const { queryParams } = this.state;
        this.setState({
            loading: true
        })
        Api.searchTenant(queryParams).then((res: any) => {
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
    bindTenant (params: any) {
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.bindTenant({ ...reqParams }).then((res: any) => {
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
    switchQueue (params: any) {
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.switchQueue({ ...reqParams }).then((res: any) => {
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
                queryParams: Object.assign(this.state.queryParams, { clusterId: initCluster.clusterId, engineType: initEngine.engineType }),
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
        return clusterList.map((item: any) => {
            return <Option key={`${item.clusterId}`} value={`${item.clusterId}`}>{item.clusterName}</Option>
        })
    }
    handleChangeCluster = (value: any) => {
        const { clusterList } = this.state;
        let currentCluster: any;
        currentCluster = clusterList.filter((clusItem: any) => clusItem.clusterId == value); // 选中当前集群
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
    changeTenantName = (value: any) => {
        const queryParams = Object.assign(this.state.queryParams, { tenantName: value })
        this.setState({
            queryParams
        }, this.searchTenant)
    }
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.searchTenant)
    }
    handleEngineTab = (key: any) => {
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
    clickSwitchQueue = (record: any) => {
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
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '资源队列',
                dataIndex: 'queue',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '最小容量（%）',
                dataIndex: 'minCapacity',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '最大容量（%）',
                dataIndex: 'maxCapacity',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text: any, record: any) => {
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
            render (text: any, record: any) {
                return text
            }
        }]
    }
    render () {
        const hadoopColumns = this.initHadoopColumns();
        const libraColumns = this.initLibraColumns()
        const { tableData, queryParams, total, loading, engineList, clusterList,
            tenantModal, queueModal, modalKey, editModalKey } = this.state;
        const { tenantList } = this.props.consoleUser;
        const pagination: any = {
            current: queryParams.currentPage,
            pageSize: PAGESIZE,
            total
        }
        return (
            <div className='resource-wrapper'>
                <Row>
                    <Col span= { 12 } >
                        <Form className="m-form-inline" layout="inline">
                            <FormItem
                                label='集群'
                            >
                                <Select
                                    className='cluster-select'
                                    style={{ width: '180px' }}
                                    placeholder='请选择集群'
                                    value={`${queryParams.clusterId}`}
                                    onChange={this.handleChangeCluster}
                                >
                                    {this.clusterOptions()}
                                </Select>
                            </FormItem>
                        </Form>
                    </Col>
                    <Col span={ 12 } >
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
                            activeKey={`${queryParams.engineType}`}
                            {...{ forceRender: true }}
                        >
                            {
                                engineList && engineList.map((item: any) => {
                                    const { engineType } = item
                                    const isHadoop = engineType == ENGINE_TYPE.HADOOP
                                    const engineName = isHadoop ? ENGINE_TYPE_NAME.HADOOP : ENGINE_TYPE_NAME.LIBRA
                                    return (
                                        <TabPane className='tab-pane-wrapper' tab={engineName} key={`${engineType}`}>
                                            <Tabs
                                                className='engine-detail-tabs'
                                                tabPosition='top'
                                            >
                                                <TabPane tab="租户绑定" key={`${engineType}-tenant`}>
                                                    <div style={{ margin: 15 }}>
                                                        <Search
                                                            style={{ width: '200px', marginBottom: '20px' }}
                                                            placeholder='按租户名称搜索'
                                                            value={queryParams.tenantName}
                                                            onChange={(e: any) => {
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
                    tenantList={tenantList}
                    clusterList={clusterList}
                    isBindTenant={true}
                    onCancel={() => { this.setState({ tenantModal: false }) }}
                    onOk={this.bindTenant.bind(this)}
                />
                <BindCommModal
                    key={modalKey}
                    title='切换队列'
                    visible={queueModal}
                    isBindTenant={false}
                    tenantList={tenantList}
                    clusterList={clusterList}
                    tenantInfo={this.state.tenantInfo}
                    clusterId={queryParams.clusterId}
                    disabled={true}
                    onCancel={() => {
                        this.setState({
                            queueModal: false,
                            tenantInfo: ''
                        })
                    }}
                    onOk={this.switchQueue.bind(this)}
                />
            </div>
        )
    }
}
export default ResourceManage;
