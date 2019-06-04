import React from 'react';
import { Modal, Form, Select, Icon, message } from 'antd';
import { formItemLayout, ENGINE_TYPE } from '../../consts'
import Api from '../../api/console';
const Option = Select.Option;
class BindTenantModal extends React.Component {
    state = {
        tenantList: [],
        clusterList: [], // 含engine信息
        queueList: [], // 队列
        isShowHadoop: false,
        isShowLibra: false
    }
    componentDidMount () {
        this.getAllTenantLists();
        this.getAllClusterLists();
    }
    getAllTenantLists = () => {
        Api.getAllTenant().then(res => {
            if (res.code === 1) {
                this.setState({
                    tenantList: res.data || []
                })
            }
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
    changeCluster = (value) => {
        const { clusterList } = this.state;
        let engines = []
        // 选中集群
        clusterList.forEach(item => {
            if (item.clusterId === value) {
                engines = item.engines
            }
        })
        // 区分engine
        engines.forEach(item => {
            if (item.engineType === ENGINE_TYPE.HADOOP) {
                this.setState({
                    queueList: item.queues,
                    isShowHadoop: true
                })
            }
            if (item.engineType === ENGINE_TYPE.LIBRA) {
                this.setState({
                    isShowHadoop: true
                })
            }
        })
    }
    tenantOptions = () => {
        const { tenantList } = this.state;
        tenantList.map(item => {
            return <Option key={`${item.tenantId}`} value={`${item.tenantId}`}>{item.tenantName}</Option>
        })
    }
    clusterOptions = () => {
        const { clusterList } = this.state;
        clusterList.map(item => {
            return <Option key={`${item.clusterId}`} value={`${item.clusterId}`}>{item.clusterName}</Option>
        })
    }
    queueOptions = () => {
        const { queueList } = this.state;
        queueList.map(item => {
            return <Option key={`${item.clusterId}`} value={`${item.queueId}`}>{item.queue}</Option>
        })
    }
    confirmBind = () => {
        const { getFieldsValue, validateFields } = this.props.form;
        const reqParams = getFieldsValue();
        validateFields(err => {
            if (!err) {
                Api.bindTenant(reqParams).then(res => {
                    if (res.code === 1) {
                        message.success('租户绑定成功')
                        this.props.onCancel();
                    }
                })
            }
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible } = this.props;
        const { isShowHadoop, isShowLibra } = this.state;
        return (
            <Modal
                title='绑定新租户'
                visible={visible}
                onOk={this.confirmBind}
                onCancel={this.props.onCancel}
                className='no-padding-modal'
            >
                <React.Fragment>
                    <div className='info-title'>
                        <Icon type="info-circle" style={{ color: '#2491F7' }} />
                        <span className='info-text'>将租户绑定到集群，可使用集群内的每种计算引擎，绑定后，不能切换其他集群。</span>
                    </div>
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
                                    placeholder='请选择租户'
                                >
                                    {this.tenantOptions()}
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
                                    onChange={this.changeCluster}
                                >
                                    {this.clusterOptions()}
                                </Select>
                            )}
                        </Form.Item>
                        {
                            isShowHadoop ? (
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
                                                {this.queueOptions()}
                                            </Select>
                                        )}
                                    </Form.Item>
                                </div>
                            ) : null
                        }
                        {
                            isShowLibra ? (
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
export default Form.create()(BindTenantModal);
