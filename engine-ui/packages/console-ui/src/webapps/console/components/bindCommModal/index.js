import React from 'react';
import { Modal, Form, Select, Icon } from 'antd';
import { formItemLayout, ENGINE_TYPE } from '../../consts'
const Option = Select.Option;

class BindCommModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            queueList: [],
            hasHadoop: false,
            hasLibra: false,
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
    handleChangeCluster = (value) => {
        const { clusterList } = this.props;
        let currentCluster;
        currentCluster = clusterList.filter(clusItem => clusItem.clusterId == value); // 选中当前集群
        const currentEngineList = (currentCluster[0] && currentCluster[0].engines) || [];
        const hadoopEngine = currentEngineList.filter(item => item.engineType == ENGINE_TYPE.HADOOP)
        const libraEngine = currentEngineList.filter(item => item.engineType == ENGINE_TYPE.LIBRA)
        const hasHadoop = hadoopEngine.length >= 1;
        const hasLibra = libraEngine.length >= 1;
        const queueList = hasHadoop && hadoopEngine[0] && hadoopEngine[0].queues;
        this.setState({
            hasHadoop,
            hasLibra,
            queueList
        })
    }
    /* eslint-disable */
    getServiceParam () {
        let params = {
            canSubmit: false,
            reqParams: {}
        }
        const { getFieldsValue, validateFields } = this.props.form;
        const reqParams = getFieldsValue();
        const { tenantInfo = {}, isBindTenant } = this.props;
        validateFields(err => {
            if (!err) {
                params.canSubmit = true,
                params.reqParams = isBindTenant ? reqParams : Object.assign(reqParams, { tenantId: tenantInfo.tenantId }) // 切换队列覆盖默认值name
            }
        })
        return params
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onOk, onCancel, title, isBindTenant,
            disabled, tenantList, clusterList, tenantInfo, clusterId } = this.props;
        const { hasHadoop, hasLibra, queueList } = this.state;
        return (
            <Modal
                title={title}
                visible={visible}
                onOk={() => { onOk(this.getServiceParam()) }}
                onCancel={onCancel}
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
                                    placeholder='请选择租户'
                                    optionFilterProp="children"
                                    filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                    disabled={disabled}
                                >
                                    {tenantList && tenantList.map(tenantItem => {
                                        return <Option key={`${tenantItem.tenantId}`} value={`${tenantItem.tenantId}`}>{tenantItem.tenantName}</Option>
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
                                    {clusterList.map(clusterItem => {
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
                                        label="资源队列"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('queueId', {
                                            rules: [{
                                                required: true,
                                                message: '资源队列不可为空！'
                                            }]
                                        })(
                                            <Select
                                                allowClear
                                                placeholder='请选择资源队列'
                                            >
                                                {queueList.map(item => {
                                                    return <Option key={`${item.queueId}`} value={`${item.queueId}`}>{item.queueName}</Option>
                                                })}
                                            </Select>
                                        )}
                                    </Form.Item>
                                </div>
                            ) : null
                        }
                        {
                            hasLibra ? (
                                <div
                                    className='border-item'
                                >
                                    <div className='engine-title'>
                                        <span>LibrA</span>
                                        <div style={{ fontSize: '13px', marginTop: '5px' }}>创建项目时，自动关联到租户的LibrA引擎</div>
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
export default Form.create()(BindCommModal);
