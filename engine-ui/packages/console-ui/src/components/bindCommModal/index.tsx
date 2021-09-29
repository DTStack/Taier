/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useState, useRef, useEffect } from 'react';
import { Modal, Form, Select, Icon, Tooltip, Input } from 'antd';
// import { debounce } from 'lodash';

import API from '../../api/account';

import { useEnv } from '../customHooks'

import { formItemLayout, ENGINE_TYPE, ENGIN_TYPE_TEXT } from '../../consts'

const Option = Select.Option;
const FormItem = Form.Item

const CustomModal: React.FC = (props: any) => {
    const { form, form: { getFieldDecorator, resetFields }, visible, onOk, onCancel, title, isBindTenant,
        disabled, tenantInfo = {}, clusterId: Id, clusterList, isBindNamespace } = props
    const [tenantList, setTenantList] = useState([])
    const prevVisible = useRef(null)
    const [clusterId, setClusterId] = useState(Id)
    const { env, queueList } = useEnv({ clusterId: clusterId || Id, visible, form, clusterList })
    // 切换集群
    useEffect(() => {
        prevVisible.current = visible
        if (visible === false) {
            resetFields()
            setClusterId(undefined)
        }
        if (visible) { onSearchTenantUser() }
    }, [visible, resetFields])

    const onSearchTenantUser = () => {
        API.getTenantsList().then((res: any) => {
            if (res.code === 1) {
                setTenantList(res.data || [])
            }
        })
    }

    const handleChangeCluster = (e) => {
        setClusterId(e)
    }

    // const debounceSearchTenant = debounce(onSearchTenantUser, 1000);

    const getServiceParam = () => {
        let params: any = {
            canSubmit: false,
            reqParams: {}
        }
        const { getFieldsValue, validateFields } = props?.form;
        const reqParams = getFieldsValue();
        validateFields((err: any) => {
            if (!err) {
                params.canSubmit = true;
                params.reqParams = reqParams;
                // 切换队列覆盖默认值name
                if (!isBindTenant) params.reqParams = Object.assign(reqParams, { tenantId: tenantInfo.tenantId });
                if (isBindNamespace) {
                    params.reqParams = Object.assign(reqParams, {
                        tenantId: tenantInfo.tenantId,
                        queueId: tenantInfo.queueId
                    });
                }
                params.hasKubernetes = env[ENGINE_TYPE.KUBERNETES]
            }
        })
        return params
    }

    const getEnginName = () => {
        let enginName = [];
        for (const key in ENGINE_TYPE) {
            if (ENGINE_TYPE[key] !== ENGINE_TYPE.KUBERNETES && ENGINE_TYPE[key] !== ENGINE_TYPE.HADOOP) {
                enginName = env[ENGINE_TYPE[key]] ? [
                    ...enginName, ENGIN_TYPE_TEXT[ENGINE_TYPE[key]]
                ] : enginName
            }
        }
        return enginName;
    }

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
                            initialValue: tenantInfo.tenantName || ''
                        })(
                            <Select
                                allowClear
                                // showSearch
                                placeholder='请搜索要绑定的租户'
                                optionFilterProp="title"
                                disabled={disabled}
                                // onSearch={debounceSearchTenant}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {tenantList && tenantList.map((tenantItem: any) => {
                                    return <Option key={`${tenantItem.dtUicTenantId}`} value={`${tenantItem.dtUicTenantId}`} title={tenantItem.tenantName}>{tenantItem.tenantName}</Option>
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
                            initialValue: Id || ''
                        })(
                            <Select
                                allowClear
                                placeholder='请选择集群'
                                disabled={disabled}
                                onChange={handleChangeCluster}
                            >
                                {clusterList.map((clusterItem: any) => {
                                    return <Option key={`${clusterItem.clusterId}`} value={`${clusterItem.clusterId}`}>{clusterItem.clusterName}</Option>
                                })}
                            </Select>
                        )}
                    </Form.Item>
                    {
                        env[ENGINE_TYPE.KUBERNETES] && (
                            <div
                                className='border-item'
                            >
                                <div className='engine-title'>Kubernetes</div>
                                <Form.Item
                                    label='Namespace'
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator('namespace', {
                                        initialValue: tenantInfo?.queue || ''
                                    })(
                                        <Input />
                                    )}
                                </Form.Item>
                            </div>
                        )
                    }
                    {
                        env[ENGINE_TYPE.HADOOP] && !env[ENGINE_TYPE.KUBERNETES] ? (
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
                                            message: '资源队列不可为空！'
                                        }],
                                        initialValue: tenantInfo?.tenantName
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
                                    <span>
                                        创建项目时，自动关联到租户的{bindEnginName.join('、')}引擎
                                    </span>
                                </div>
                            </div>
                        ) : null
                    }
                </Form>
            </React.Fragment>
        </Modal>
    )
}
const areEqual = (prevprops, nextprops) => {
    if (prevprops?.visible !== nextprops?.visible || nextprops?.visible === true) return false
    return true
}

export default Form.create<any>()(React.memo(CustomModal, areEqual));
