import React from 'react';
import { Modal, Form, Select, message, Input } from 'antd';
import { formItemLayout } from '../../consts'
import Api from '../../api/console';

const Option = Select.Option;
class SwitchQueue extends React.Component {
    queueOptions = () => {
        const { queueList } = this.props;
        queueList.map(item => {
            return <Option key={`${item.clusterId}`} value={`${item.queueId}`}>{item.queue}</Option>
        })
    }
    confirmSwitchQueue = () => {
        const { getFieldValue, validateFields, resetFields } = this.props.form;
        validateFields(err => {
            if (!err) {
                Api.switchQueue({
                    tenantId: getFieldValue('tenantId'),
                    queueId: getFieldValue('queueId')
                }).then(res => {
                    if (res.code === 1) {
                        message.success('切换队列成功！');
                        resetFields();
                        setTimeout(() => {
                            this.props.onCancel();
                        }, 10);
                    }
                })
            }
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, tenantInfo, clusterInfo, isHaveHadoop, isHaveLibra } = this.props;
        return (
            <Modal
                title='切换队列'
                visible={visible}
                onOk={this.confirmSwitchQueue}
                onCancel={this.props.onCancel}
                // className='no-padding-modal'
            >
                <React.Fragment>
                    {/* <div className='info-title'>
                        <Icon type="info-circle" style={{ color: '#2491F7' }} />
                        <span className='info-text'>将租户绑定到集群，可使用集群内的每种计算引擎，绑定后，不能切换其他集群。</span>
                    </div> */}
                    <Form>
                        <Form.Item
                            label="租户"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('queueId', {
                                rules: [{
                                    required: true,
                                    message: '租户不可为空！'
                                }],
                                initialValue: tenantInfo && tenantInfo.tenantId
                            })(
                                <Input type="hidden" />
                            )}
                            <span>{tenantInfo && tenantInfo.tenantName}</span>
                        </Form.Item>
                        <Form.Item
                            label="集群"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('queueId', {
                                rules: [{
                                    required: true,
                                    message: '集群不可为空！'
                                }],
                                initialValue: clusterInfo && clusterInfo.clusterId
                            })(
                                <Input type="hidden" />
                            )}
                            <span>{clusterInfo && clusterInfo.clusterName}</span>
                        </Form.Item>
                        {
                            isHaveHadoop ? (
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
                            isHaveLibra ? (
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
export default Form.create()(SwitchQueue);
