import React, { useState, useRef, useEffect } from 'react';
import { Modal, Form, Select, Icon, Tooltip } from 'antd';
import { debounce } from 'lodash';

import API from 'dt-common/src/api';

import { useEnv } from '../customHooks'

import { formItemLayout } from '../../consts'

const Option = Select.Option;
const FormItem = Form.Item

const CustomModal: React.FC = (props: any) => {
    const { form, form: { getFieldDecorator }, visible, onOk, onCancel, title, isBindTenant,
        disabled, tenantInfo, clusterId, clusterList } = props
    const [tenantList, setTenantList] = useState([])
    const prevVisible = useRef(null)
    const { env, queueList } = useEnv({ clusterId, visible: prevVisible.current !== visible && visible === true, form, clusterList })

    // 切换集群
    useEffect(() => {
        // form?.resetFields(['queueId']);
        prevVisible.current = visible
    }, [visible])

    const onSearchTenantUser = (value: string) => {
        API.getFullTenants(value).then((res: any) => {
            if (res.success) {
                setTenantList(res.data || [])
            }
        })
    }

    const debounceSearchTenant = debounce(onSearchTenantUser, 1000);

    const getServiceParam = () => {
        let params: any = {
            canSubmit: false,
            reqParams: {}
        }
        const { getFieldsValue, validateFields } = props?.form;
        const reqParams = getFieldsValue();
        const { tenantInfo = {}, isBindTenant } = props;
        validateFields((err: any) => {
            if (!err) {
                params.canSubmit = true;
                params.reqParams = isBindTenant ? reqParams : Object.assign(reqParams, { tenantId: tenantInfo.tenantId }); // 切换队列覆盖默认值name
            }
        })
        return params
    }

    const getEnginName = () => {
        const { hasLibra, hasTiDB, hasOracle, hasGreenPlum } = env;
        let enginName = [];
        enginName = hasLibra ? [...enginName, 'Libra'] : enginName;
        enginName = hasTiDB ? [...enginName, 'TiDB'] : enginName;
        enginName = hasOracle ? [...enginName, 'Oracle'] : enginName;
        enginName = hasGreenPlum ? [...enginName, 'Greenplum'] : enginName;
        return enginName;
    }

    const { hasHadoop } = env;
    const bindEnginName = getEnginName();
    return (
        <Modal
            title={title}
            visible={visible}
            onOk={() => { onOk(getServiceParam()) }}
            onCancel={onCancel}
            width='600px'
            className={isBindTenant ? 'no-padding-modal' : ''}
        >
            <>
                {
                    isBindTenant && <div className='info-title'>
                        <Icon type="info-circle" style={{ color: '#2491F7' }} />
                        <span className='info-text'>将租户绑定到集群，可使用集群内的每种计算引擎，绑定后，不能切换其他集群。</span>
                    </div>
                }
                <Form>
                    <FormItem
                        label='租户'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('tenantId', {
                            rules: [{
                                required: true,
                                message: '租户不可为空！'
                            }],
                            initialValue: tenantInfo && `${tenantInfo.tenantName}`
                        })(<Select
                            allowClear
                            showSearch
                            placeholder='请搜索要绑定的租户'
                            optionFilterProp="title"
                            disabled={disabled}
                            onSearch={debounceSearchTenant}
                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {tenantList && tenantList.map((tenantItem: any) => {
                                return <Option key={`${tenantItem.tenantId}`} value={`${tenantItem.tenantId}`} title={tenantItem.tenantName}>{tenantItem.tenantName}</Option>
                            })}
                        </Select>)
                        }

                    </FormItem>
                    {
                        hasHadoop ? (
                            <div
                                className='border-item'
                            >
                                <div className='engine-title'>Hadoop</div>
                                <FormItem
                                    label={
                                        (<span>
                                            资源队列&nbsp;
                                            <Tooltip title="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群">
                                                <Icon type="question-circle-o" />
                                            </Tooltip>
                                        </span>)
                                    }
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator('queueId', {
                                        rules: [{
                                            required: true,
                                            message: '租户不可为空！'
                                        }],
                                        initialValue: tenantInfo && `${tenantInfo.tenantName}`
                                    })(<Select
                                        allowClear
                                        placeholder='请选择资源队列'
                                    >
                                        {queueList.map((item: any) => {
                                            return <Option key={`${item.queueId}`} value={`${item.queueId}`}>{item.queueName}</Option>
                                        })}
                                    </Select>)
                                    }
                                </FormItem>
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
            </>
        </Modal>
    )
}
// const areEqual = (prevprops, nextprops) => {
//     if (prevprops.visible !== nextprops.visible) return false
//     return true
// }

export default Form.create<any>()(CustomModal);
