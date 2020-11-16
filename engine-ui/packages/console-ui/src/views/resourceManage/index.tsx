import * as React from 'react';
import { Row, Col, Select, Button, Card, Form, Tabs, Table, Input, message } from 'antd';
import { get } from 'lodash';

import Api from '../../api/console';
import { ENGIN_TYPE_TEXT } from '../../consts';
import { isHadoopEngine, isTiDBEngine, isOracleEngine, isGreenPlumEngine, isKubernetesEngine } from '../../consts/clusterFunc';
import BindCommModal from '../../components/bindCommModal';
import ResourceManageModal from '../../components/resourceManageModal';
import Resource from './resourceView';

import BindAccountPane from './bindAccount';

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const Search = Input.Search;
const PAGESIZE = 20;

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
            currentPage: 1,
            clusterName: ''
        },
        loading: false,
        total: 0,
        tenantModal: false,
        queueModal: false,
        manageModal: false,
        tenantInfo: '',
        isHaveHadoop: false,
        isHaveLibra: false,
        queueList: [] // hadoop资源队列
    }

    private requestEnd: boolean = true; // 请求结束

    componentDidMount () {
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
                    tableData: get(res, 'data.data', []),
                    total: get(res, 'data.totalCount', 0),
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
        if (canSubmit && this.requestEnd) {
            this.requestEnd = false;
            Api.bindTenant({ ...reqParams }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        tenantModal: false
                    })
                    message.success('租户绑定成功')
                    this.searchTenant()
                }
                this.requestEnd = true;
            })
        }
    }
    switchQueue (params: any) {
        const { reqParams, hasKubernetes } = params;
        if (hasKubernetes) {
            Api.bindNamespace({
                clusterId: reqParams.clusterId,
                namespace: reqParams.namespace,
                queueId: reqParams.queueId
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        queueModal: false
                    })
                    message.success('切换队列成功')
                    this.searchTenant()
                }
            })
        } else {
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
    sourceManage (params: any) {
        this.setState({
            manageModal: false,
            tenantInfo: ''
        }, () => this.initList())
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
                queryParams: Object.assign(this.state.queryParams, {
                    clusterId: initCluster.clusterId,
                    engineType: initEngine.engineType
                }),
                clusterName: initCluster.clusterName,
                engineList,
                loading: true
            }, this.searchTenant)
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
            queryParams,
            clusterName: currentCluster[0].clusterName
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
        this.setState({ tenantModal: true })
    }
    clickSwitchQueue = (record: any) => {
        this.setState({
            manageModal: true,
            tenantInfo: record
        })
    }
    clickSwitchNamespace = (record: any) => {
        this.setState({
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
                    return <a onClick={() => { this.clickSwitchQueue(record) }}>
                        资源管理
                    </a>
                }
            }
        ]
    }
    initKubernetesColumns = () => {
        return [
            {
                title: '租户',
                dataIndex: 'tenantName'
            },
            {
                title: 'Namespace',
                dataIndex: 'queue'
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: 200,
                render: (text: any, record: any) => {
                    return <a onClick={ () => { this.clickSwitchNamespace(record) }}>
                        切换Namespace
                    </a>
                }
            }
        ]
    }
    initDefaultColumns = () => {
        return [
            {
                title: '租户',
                dataIndex: 'tenantName',
                render (text: any, record: any) {
                    return text
                }
            }
        ]
    }

    getColumn = (isHadoop: boolean, resourceType: string) => {
        let column: any[] = this.initHadoopColumns()
        if (!isHadoop) column = this.initDefaultColumns()
        if (isHadoop && isKubernetesEngine(resourceType)) column = this.initKubernetesColumns()
        return column
    }
    render () {
        const { tableData, queryParams, total, loading, engineList, clusterList,
            tenantModal, queueModal, modalKey, clusterName, tenantInfo, manageModal } = this.state;
        const kubernetesEngine = isKubernetesEngine(engineList[0] && engineList[0].resourceType);
        const pagination: any = {
            current: queryParams.currentPage,
            pageSize: PAGESIZE,
            total
        }
        console.log('console:', this.state);
        return (
            <div className='resource-wrapper'>
                <Row>
                    <Col span={12} >
                        <Form className="m-form-inline" layout="inline">
                            <FormItem
                                label='集群'
                            >
                                <Select
                                    className="dt-form-shadow-bg"
                                    style={{ width: '264px' }}
                                    placeholder='请选择集群'
                                    value={`${queryParams.clusterId}`}
                                    onChange={this.handleChangeCluster}
                                >
                                    {this.clusterOptions()}
                                </Select>
                            </FormItem>
                        </Form>
                    </Col>
                    <Col span={12} >
                        <Button className='terent-button' type='primary' onClick={() => { this.setState({ tenantModal: true }) }}>绑定新租户</Button>
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
                                    const { engineType, resourceType } = item
                                    const isHadoop = isHadoopEngine(engineType);
                                    const engineName = isHadoop && isKubernetesEngine(resourceType) ? 'Kubernetes' : ENGIN_TYPE_TEXT[engineType];
                                    return (
                                        <TabPane className='tab-pane-wrapper' tab={engineName} key={`${engineType}`}>
                                            <Tabs
                                                key={`${engineType}-tenant`}
                                                className='engine-detail-tabs'
                                                tabPosition='top'
                                                animated={false}
                                            >
                                                {
                                                    isHadoopEngine(engineType) ? <TabPane tab="资源全景" key={`showResource`}>
                                                        <Resource key={`${clusterName}`} clusterName={clusterName} />
                                                    </TabPane> : null
                                                }
                                                <TabPane tab="租户绑定" key={`bindTenant`}>
                                                    <div style={{ margin: 15 }}>
                                                        <Search
                                                            style={{ width: '200px', marginBottom: '20px' }}
                                                            placeholder='按租户名称搜索'
                                                            value={queryParams.tenantName}
                                                            onChange={(e: any) => {
                                                                this.setState({
                                                                    queryParams: Object.assign(this.state.queryParams, { tenantName: e.target.value })
                                                                })
                                                            }}
                                                            onSearch={this.changeTenantName}
                                                        />
                                                        <Table
                                                            className='dt-table-border'
                                                            loading={loading}
                                                            rowKey={(record: any, index) => `${index}-${record.tenantName}`}
                                                            columns={this.getColumn(isHadoop, resourceType)}
                                                            dataSource={tableData}
                                                            pagination={pagination}
                                                            onChange={this.handleTableChange}
                                                        />
                                                    </div>
                                                </TabPane>
                                                {
                                                    isHadoopEngine(engineType) && !isKubernetesEngine(resourceType) ? <TabPane tab="LDAP账号绑定" key='ldap'>
                                                        <BindAccountPane
                                                            key={`${queryParams.clusterId}-${engineType}`}
                                                            engineType={parseInt(engineType, 10)}
                                                            clusterId={queryParams.clusterId} />
                                                    </TabPane> : null
                                                }
                                                {
                                                    isTiDBEngine(engineType) || isOracleEngine(engineType) || isGreenPlumEngine(engineType)
                                                        ? <TabPane tab="账号绑定" key="bindAccount">
                                                            <BindAccountPane
                                                                key={`${queryParams.clusterId}-${engineType}`}
                                                                engineType={parseInt(engineType, 10)}
                                                                clusterId={queryParams.clusterId}
                                                            />
                                                        </TabPane>
                                                        : null
                                                }
                                            </Tabs>
                                        </TabPane>
                                    )
                                })
                            }
                        </Tabs>
                    </Card>
                </div>
                <BindCommModal
                    title='绑定新租户'
                    visible={tenantModal}
                    clusterList={clusterList}
                    isBindTenant={true}
                    onCancel={() => { this.setState({ tenantModal: false }) }}
                    onOk={this.bindTenant.bind(this)}
                />
                <BindCommModal
                    key={modalKey}
                    title={kubernetesEngine ? '切换namespace' : '切换队列'}
                    visible={queueModal}
                    isBindTenant={false}
                    isBindNamespace={kubernetesEngine}
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
                <ResourceManageModal
                    title={`资源管理 (${tenantInfo.tenantName ?? ''})`}
                    visible={manageModal}
                    isBindTenant={false}
                    clusterList={clusterList}
                    tenantInfo={this.state.tenantInfo}
                    clusterId={queryParams.clusterId}
                    disabled={true}
                    tenantId={tenantInfo?.tenantId}
                    queue={tenantInfo?.queue}
                    queueId={tenantInfo?.queueId}
                    onCancel={() => {
                        this.setState({
                            manageModal: false,
                            tenantInfo: ''
                        })
                    }}
                    onOk={this.sourceManage.bind(this)}
                />
            </div>
        )
    }
}
export default ResourceManage;
