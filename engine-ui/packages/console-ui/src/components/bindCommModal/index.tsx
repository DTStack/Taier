import * as React from 'react';
import { Modal, Form, Select, Icon, Tooltip } from 'antd';
import { debounce } from 'lodash';

import API from 'dt-common/src/api';

import { formItemLayout, ENGINE_TYPE } from '../../consts'
const Option = Select.Option;

class BindCommModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            queueList: [],
            tenantList: [],
            hasHadoop: false,
            hasLibra: false,
            hasTiDB: false,
            hasOracle: false,
            hasGreenPlum: false,
            hasPresto: false,
            clusterId: props.clusterId
        }
    }

    componentDidMount () {
        const { clusterId } = this.state;
        if (clusterId) { // 新增租户初始化
            this.handleChangeCluster(clusterId)
        }
    }

    // 切换集群
    handleChangeCluster = (value: any) => {
        const { clusterList } = this.props;
        this.props.form.resetFields(['queueId']);
        let currentCluster: any;
        currentCluster = clusterList.filter((clusItem: any) => clusItem.clusterId == value); // 选中当前集群

        const currentEngineList = (currentCluster[0] && currentCluster[0].engines) || [];
        const hadoopEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.HADOOP);
        const libraEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.LIBRA);
        const tiDBEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.TI_DB);
        const oracleEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.ORACLE);
        const greenPlumEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.GREEN_PLUM);
        const prestoEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.PRESTO);

        const hasHadoop = hadoopEngine.length >= 1;
        const hasLibra = libraEngine.length >= 1;
        const hasTiDB = tiDBEngine.length > 0;
        const hasOracle = oracleEngine.length > 0;
        const hasGreenPlum = greenPlumEngine.length > 0;
        const hasPresto = prestoEngine.length > 0;

        const queueList = hasHadoop && hadoopEngine[0] && hadoopEngine[0].queues;
        this.setState({
            hasHadoop,
            hasLibra,
            hasTiDB,
            queueList,
            hasOracle,
            hasGreenPlum,
            hasPresto
        })
    }

    onSearchTenantUser = (value: string) => {
        API.getFullTenants(value).then((res: any) => {
            if (res.success) {
                this.setState({
                    tenantList: res.data || []
                })
            }
        })
    }

    debounceSearchTenant = debounce(this.onSearchTenantUser, 1000);

    getServiceParam () {
        let params: any = {
            canSubmit: false,
            reqParams: {}
        }
        const { getFieldsValue, validateFields } = this.props.form;
        const reqParams = getFieldsValue();
        const { tenantInfo = {}, isBindTenant } = this.props;
        validateFields((err: any) => {
            if (!err) {
                params.canSubmit = true;
                params.reqParams = isBindTenant ? reqParams : Object.assign(reqParams, { tenantId: tenantInfo.tenantId }); // 切换队列覆盖默认值name
            }
        })
        return params
    }

    getEnginName () {
        const { hasLibra, hasTiDB, hasOracle, hasGreenPlum, hasPresto } = this.state;
        let enginName = [];
        enginName = hasLibra ? [...enginName, 'Libra'] : enginName;
        enginName = hasTiDB ? [...enginName, 'TiDB'] : enginName;
        enginName = hasOracle ? [...enginName, 'Oracle'] : enginName;
        enginName = hasGreenPlum ? [...enginName, 'Greenplum'] : enginName;
        enginName = hasPresto ? [...enginName, 'Presto'] : enginName;
        return enginName;
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onOk, onCancel, title, isBindTenant,
            disabled, clusterList, tenantInfo, clusterId } = this.props;
        const { hasHadoop, queueList, tenantList } = this.state;
        const bindEnginName = this.getEnginName();
        return (
            <Modal
                title={title}
                visible={visible}
                onOk={() => { onOk(this.getServiceParam()) }}
                onCancel={onCancel}
                width='600px'
                className={isBindTenant ? 'no-padding-modal' : ''}
            >
                <React.Fragment>
                    {
                        isBindTenant && <div className='info-title'>
                            <Icon type="info-circle" style={{ color: '#2491F7' }} />
                            <span className='info-text'>将租户绑定到集群，可使用集群内的每种计算引擎，绑定后，不能切换其他集群。</span>
                        </div>
                    }
                    <Form>
                        <Form.Item
                            label="租户"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('tenantId', {
                                rules: [{
                                    required: true,
                                    message: '租户不可为空！'
                                }],
                                initialValue: tenantInfo && `${tenantInfo.tenantName}`
                            })(
                                <Select
                                    allowClear
                                    showSearch
                                    placeholder='请搜索要绑定的租户'
                                    optionFilterProp="title"
                                    disabled={disabled}
                                    onSearch={this.debounceSearchTenant}
                                    filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                >
                                    {tenantList && tenantList.map((tenantItem: any) => {
                                        return <Option key={`${tenantItem.tenantId}`} value={`${tenantItem.tenantId}`} title={tenantItem.tenantName}>{tenantItem.tenantName}</Option>
                                    })}
                                </Select>
                            )}
                        </Form.Item>
                        <Form.Item
                            label="集群"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('clusterId', {
                                rules: [{
                                    required: true,
                                    message: '集群不可为空！'
                                }],
                                initialValue: clusterId && `${clusterId}`
                            })(
                                <Select
                                    allowClear
                                    placeholder='请选择集群'
                                    disabled={disabled}
                                    onChange={this.handleChangeCluster}
                                >
                                    {clusterList.map((clusterItem: any) => {
                                        return <Option key={`${clusterItem.clusterId}`} value={`${clusterItem.clusterId}`}>{clusterItem.clusterName}</Option>
                                    })}
                                </Select>
                            )}
                        </Form.Item>
                        {
                            hasHadoop ? (
                                <div
                                    className='border-item'
                                >
                                    <div className='engine-title'>Hadoop</div>
                                    <Form.Item
                                        label={(
                                            <span>
                                                资源队列&nbsp;
                                                <Tooltip title="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群">
                                                    <Icon type="question-circle-o" />
                                                </Tooltip>
                                            </span>
                                        )}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('queueId')(
                                            <Select
                                                allowClear
                                                placeholder='请选择资源队列'
                                            >
                                                {queueList.map((item: any) => {
                                                    return <Option key={`${item.queueId}`} value={`${item.queueId}`}>{item.queueName}</Option>
                                                })}
                                            </Select>
                                        )}
                                    </Form.Item>
                                </div>
                            ) : null
                        }
                        {
                            bindEnginName.length > 0 ? (
                                <div className='border-item'>
                                    <div className="engine-name">
                                        创建项目时，自动关联到租户的{bindEnginName.join('、')}引擎
                                    </div>
                                </div>
                            ) : null
                        }
                    </Form>
                </React.Fragment>
            </Modal>
        )
    }
}
export default Form.create<any>()(BindCommModal);
