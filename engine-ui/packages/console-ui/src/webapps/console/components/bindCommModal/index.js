import React from 'react';
import { Modal, Form, Select, Icon } from 'antd';
import { formItemLayout, ENGINE_TYPE } from '../../consts'
import { getTenantList } from '../../actions/console'
import Api from '../../api/console';
import { connect } from 'react-redux';
const Option = Select.Option;

function mapStateToProps (state) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps (dispatch) {
    return {
        getTenantList () {
            dispatch(getTenantList())
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class BindCommModal extends React.Component {
    state = {
        clusterList: [], // 含engine、队列信息
        queueList: [],
        hasHadoop: false,
        hasLibra: false
    }
    // componentDidMount () {
    //     this.props.getTenantList(); // 租户列表
    //     this.getAllClusterLists();
    // }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        // if (this.props.visible && nextProps.clusterId && !nextProps.isBindTenant) {
        //     this.getAllClusterLists();
        //     this.handleChangeCluster(nextProps.clusterId)
        // }
        // if (this.props.visible && nextProps.isBindTenant) {
        //     this.props.getTenantList(); // 租户列表
        //     this.getAllClusterLists();
        // }
    }
    setInitialVal = () => { // 切换队列默认值
        const { setFieldsValue } = this.props.form;
        const { tenantInfo } = this.props;
        setFieldsValue({
            tenantId: `${tenantInfo.tenantId}`
        })
    }
    getAllClusterLists = async () => {
        const res = await Api.getAllCluster();
        if (res.code === 1) {
            this.setState({
                clusterList: res.data || []
            })
        }
    }
    // 切换集群
    handleChangeCluster = (value) => {
        const { clusterList } = this.state;
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
        validateFields(err => {
            if (!err) {
                params.canSubmit = true,
                params.reqParams = reqParams
            }
        })
        return params
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onOk, onCancel, title, isBindTenant,
            disabled, consoleUser } = this.props;
        const { tenantList } = consoleUser
        const { hasHadoop, hasLibra, clusterList, queueList } = this.state;
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
                                }]
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
                                }]
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
