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

import { useEffect,useState } from 'react';
import { InfoCircleOutlined } from '@ant-design/icons';
import { Form, Input, Modal, Select, Spin } from 'antd';

import api from '@/api';
import { ENGINE_SOURCE_TYPE,ENGINE_SOURCE_TYPE_ENUM, formItemLayout } from '@/constant';
import { getEngineSourceTypeName } from '@/utils/enums';
import { useEnv } from '../customHooks';
import './index.scss';

const { Option } = Select;
const FormItem = Form.Item;

interface IBindModal {
    /**
     * 标题
     */
    title: string;
    /**
     * Modal 是否可见
     */
    visible: boolean;
    /**
     * 是否绑定租户
     */
    isBindTenant?: boolean;
    /**
     * 是否绑定 NameSpace
     */
    isBindNamespace?: boolean;
    /**
     * 集群 Id
     */
    clusterId?: number;
    /**
     * 表单域是否全部不可选
     */
    disabled?: boolean;
    /**
     * 编辑时，如果有默认值
     */
    tenantInfo?: any;
    clusterList: IClusterProps[];
    onCancel?: () => void;
    onOk?: (params: IFormFieldProps) => Promise<void>;
}

export interface IClusterProps {
    canModifyMetadata: boolean;
    clusterId: number;
    clusterName: string;
    gmtCreate: number;
    gmtModified: number;
    id: number;
    isDeleted: number;
}

interface IFormFieldProps {
    tenantId: number;
    clusterId: number;
    queueName: string;
}

interface ITenantProps {
    tenantId: number;
    tenantName: string;
}

export default ({
    title,
    visible,
    clusterList,
    clusterId,
    isBindTenant,
    disabled,
    tenantInfo,
    onCancel,
    onOk,
}: IBindModal) => {
    const [form] = Form.useForm<IFormFieldProps>();
    const [tenantList, setTenantList] = useState<ITenantProps[]>([]);
    const [queueList, setQueueList] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [queueLoading, setQueueLoading] = useState(false);
    // 是否支持队列选择，只有当前集群下有 hadoop 才支持队列选择
    const [isHaveQueue, setHaveQueue] = useState(false);
    const { env } = useEnv({
        clusterId: form?.getFieldValue('clusterId') || clusterId,
        visible,
        form,
        clusterList,
    });

    const onSearchTenantUser = () => {
        api.getTenantList().then((res) => {
            if (res.code === 1) {
                setTenantList(res.data || []);
            }
        });
    };

    const handleClusterChanged = (value: number) => {
        getQueueList(value);
    };

    const getQueueList = async (value: number) => {
        setQueueLoading(true);
        const res = await api.getEnginesByCluster({ clusterId: value });
        if (res.code) {
            const engines = res.data.engines || [];
            const hadoopEngine = engines.find((e: any) => e.engineType === ENGINE_SOURCE_TYPE_ENUM.HADOOP);
            setHaveQueue(!!hadoopEngine);
            if (hadoopEngine) {
                const queueRes = await api.getClusterResources({
                    clusterId: value,
                });
                if (queueRes.code === 1) {
                    setQueueList(queueRes.data.queues?.map((q: any) => q.queueName) || []);
                }
            }
        }
        setQueueLoading(false);
    };

    useEffect(() => {
        onSearchTenantUser();
    }, []);

    const getEnginName = () => {
        let enginName: any[] = [];
        Object.keys(ENGINE_SOURCE_TYPE).forEach((key) => {
            if (
                (ENGINE_SOURCE_TYPE as any)[key] !== ENGINE_SOURCE_TYPE.KUBERNETES &&
                (ENGINE_SOURCE_TYPE as any)[key] !== ENGINE_SOURCE_TYPE.HADOOP
            ) {
                enginName = env[(ENGINE_SOURCE_TYPE as any)[key]]
                    ? [...enginName, getEngineSourceTypeName((ENGINE_SOURCE_TYPE as any)[key])]
                    : enginName;
            }
        });
        return enginName;
    };

    const handleModalOk = () => {
        form.validateFields()
            .then((values) => {
                const params = {
                    tenantId: values.tenantId,
                    clusterId: values.clusterId,
                    queueName: values.queueName,
                };

                setLoading(true);
                onOk?.(params).finally(() => {
                    setLoading(false);
                });
            })
            .catch(() => {});
    };

    const bindEnginName = getEnginName();

    return (
        <Modal
            title={title}
            visible={visible}
            onOk={handleModalOk}
            confirmLoading={loading || queueLoading}
            onCancel={onCancel}
            width="600px"
            destroyOnClose
            className={isBindTenant ? 'no-padding-modal' : ''}
        >
            <>
                {isBindTenant && (
                    <div className="info-title">
                        <InfoCircleOutlined style={{ color: '#2491F7' }} />
                        <span className="info-text">
                            将租户绑定到集群，可使用集群内的每种计算引擎，绑定后，不能切换其他集群。
                        </span>
                    </div>
                )}
                <Form form={form} preserve={false}>
                    <FormItem
                        label="租户"
                        {...formItemLayout}
                        name="tenantId"
                        rules={[
                            {
                                required: true,
                                message: '租户不可为空！',
                            },
                        ]}
                        initialValue={tenantInfo?.tenantName}
                    >
                        <Select
                            allowClear
                            placeholder="请搜索要绑定的租户"
                            optionFilterProp="title"
                            disabled={disabled}
                            filterOption={(input, option) =>
                                option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                            }
                        >
                            {tenantList.map((tenantItem) => {
                                return (
                                    <Option
                                        key={tenantItem.tenantId}
                                        value={tenantItem.tenantId}
                                        title={tenantItem.tenantName}
                                    >
                                        {tenantItem.tenantName}
                                    </Option>
                                );
                            })}
                        </Select>
                    </FormItem>
                    <FormItem
                        label="集群"
                        {...formItemLayout}
                        name="clusterId"
                        rules={[
                            {
                                required: true,
                                message: '集群不可为空！',
                            },
                        ]}
                        initialValue={clusterId}
                    >
                        <Select allowClear placeholder="请选择集群" disabled={disabled} onChange={handleClusterChanged}>
                            {clusterList.map((clusterItem) => {
                                return (
                                    <Option key={clusterItem.clusterId} value={clusterItem.clusterId}>
                                        {clusterItem.clusterName}
                                    </Option>
                                );
                            })}
                        </Select>
                    </FormItem>
                    {isHaveQueue && (
                        <Spin spinning={queueLoading}>
                            <FormItem
                                label="队列"
                                {...formItemLayout}
                                name="queueName"
                                rules={[
                                    {
                                        required: true,
                                        message: '队列不可为空！',
                                    },
                                ]}
                            >
                                <Select allowClear placeholder="请选择队列">
                                    {queueList.map((item) => {
                                        return (
                                            <Option key={`${item}`} value={`${item}`}>
                                                {item}
                                            </Option>
                                        );
                                    })}
                                </Select>
                            </FormItem>
                        </Spin>
                    )}
                    {env[ENGINE_SOURCE_TYPE.KUBERNETES] && (
                        <div className="border-item">
                            <div className="engine-title">Kubernetes</div>
                            <FormItem
                                label="Namespace"
                                {...formItemLayout}
                                name="namespace"
                                initialValue={tenantInfo?.queue || ''}
                            >
                                <Input />
                            </FormItem>
                        </div>
                    )}
                    {bindEnginName.length > 0 ? (
                        <div className="border-item">
                            <div className="engine-name">
                                <span>
                                    创建项目时，自动关联到租户的
                                    {bindEnginName.join('、')}引擎
                                </span>
                            </div>
                        </div>
                    ) : null}
                </Form>
            </>
        </Modal>
    );
};
