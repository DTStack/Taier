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

import { useEffect, useMemo, useRef,useState } from 'react';
import { Button, Form, message,Select, Spin, Tabs } from 'antd';

import Api from '@/api';
import type { IClusterProps } from '@/components/bindCommModal';
import BindCommModal from '@/components/bindCommModal';
import ResourceManageModal from '@/components/resourceManageModal';
import { formItemLayout } from '@/constant';
import { catalogueService } from '@/services';
import { isSparkEngine } from '@/utils/is';
import type { ITableProps } from './bindTenant';
import BindTenant from './bindTenant';
import Resource from './resourceView';
import './resource.scss';

const FormItem = Form.Item;
const { TabPane } = Tabs;

interface IEnginesProps {
    clusterId: number;
    engineName: string;
    engineType: number;
    gmtCreate: number;
    gmtModified: number;
    id: number;
}

interface IFormFieldProps {
    clusterId: number;
    engineId: number;
}

export default () => {
    const [form] = Form.useForm<IFormFieldProps>();
    const [clusterList, setClusterList] = useState<IClusterProps[]>([]);
    const [engineList, setEngineList] = useState<IEnginesProps[]>([]);
    const [tenantModal, setTenantVisible] = useState(false);
    const [tabLoading, setTabLoading] = useState(false);
    const [activeKey, setActiveKey] = useState('');
    const [manageModalVisible, setManageModalVisible] = useState(false);
    const [tenantInfo, setTenantInfo] = useState<ITableProps | undefined>(undefined);

    const bindTenantRef = useRef<any>(null);

    const getClusterList = async () => {
        setTabLoading(true);
        Api.getAllCluster()
            .then((res) => {
                if (res.code === 1) {
                    setClusterList(res.data || []);

                    if (res.data?.[0]) {
                        form.setFieldsValue({
                            clusterId: res.data[0].id,
                            engineId: undefined,
                        });

                        return getEnginesByCluster(res.data[0].id);
                    }
                }
            })
            .finally(() => {
                setTabLoading(false);
            });
    };

    const getEnginesByCluster = async (clusterId: number) => {
        const res = await Api.getEnginesByCluster({ clusterId });
        if (res.code) {
            const engines = res.data.engines || [];
            const nextEngineId = engines[0]?.engineType;
            // reset the tab after getting the engines
            if (isSparkEngine(nextEngineId)) {
                setActiveKey('showResource');
            } else {
                setActiveKey('bindTenant');
            }
            form.setFieldsValue({
                engineId: nextEngineId,
            });
            setEngineList(engines);
        }
    };

    const handleChangeEngine = (activeTab: string) => {
        setActiveKey(activeTab);
    };

    const handleValuesChange = (value: Partial<IFormFieldProps>) => {
        if (value.hasOwnProperty('clusterId')) {
            // reset engineId when clusterId changed
            form.resetFields(['engineId']);
            getEnginesByCluster(value.clusterId!);
        }
    };

    const handleResourceManage = (record: ITableProps) => {
        setManageModalVisible(true);
        setTenantInfo(record);
    };

    const bindTenant = async (params: Record<string, any>) => {
        const res = await Api.bindTenant({ ...params });
        if (res.code === 1) {
            setTenantVisible(false);
            message.success('租户绑定成功');
            // 刷新租户列表
            bindTenantRef.current?.getTenant();
            // 刷新目录树
            catalogueService.loadRootFolder();
            // 刷新资源管理
            getClusterList();
        }
    };

    const sourceManage = () => {
        setManageModalVisible(false);
        setTenantInfo(undefined);
        getClusterList();
    };

    useEffect(() => {
        getClusterList();
    }, []);

    const engineOptions = useMemo(
        () =>
            engineList.map((engine) => ({
                label: engine.engineName,
                value: engine.engineType,
            })),
        [engineList]
    );

    const clusterOptions = useMemo(
        () =>
            clusterList.map((cluster) => ({
                label: cluster.clusterName,
                value: cluster.id,
            })),
        [clusterList]
    );

    return (
        <div className="resource-wrapper">
            <Form<IFormFieldProps>
                form={form}
                className="dt-resource-form"
                layout="inline"
                onValuesChange={handleValuesChange}
                {...formItemLayout}
            >
                <FormItem label="集群" name="clusterId">
                    <Select style={{ width: 200 }} placeholder="请选择集群" options={clusterOptions} />
                </FormItem>
                <FormItem label="引擎" name="engineId">
                    <Select style={{ width: 200 }} placeholder="请选择引擎" options={engineOptions} />
                </FormItem>
            </Form>
            <Button
                className="dt-resource-tenant"
                type="primary"
                onClick={() => {
                    setTenantVisible(true);
                }}
            >
                绑定新租户
            </Button>
            <Spin spinning={tabLoading}>
                <Tabs
                    animated={false}
                    activeKey={activeKey}
                    onChange={handleChangeEngine}
                    className="dt-resource-tabs"
                    destroyInactiveTabPane
                >
                    {isSparkEngine(form.getFieldValue('engineId')) ? (
                        <TabPane tab="资源全景" key="showResource">
                            <Resource clusterId={form.getFieldValue('clusterId')} />
                        </TabPane>
                    ) : null}
                    <TabPane tab="租户绑定" key="bindTenant">
                        <BindTenant
                            ref={bindTenantRef}
                            clusterId={form.getFieldValue('clusterId')}
                            clusterName={
                                clusterOptions.find((cluster) => cluster.value === form.getFieldValue('clusterId'))
                                    ?.label
                            }
                            engineType={form.getFieldValue('engineId')}
                            onClick={handleResourceManage}
                        />
                    </TabPane>
                </Tabs>
            </Spin>
            <ResourceManageModal
                title={`资源管理 (${tenantInfo?.tenantName ?? ''})`}
                visible={manageModalVisible}
                isBindTenant={false}
                clusterId={form.getFieldValue('clusterId')}
                tenantId={tenantInfo?.tenantId}
                queueName={tenantInfo?.queueName}
                onCancel={() => {
                    setManageModalVisible(false);
                    setTenantInfo(undefined);
                }}
                onOk={sourceManage}
            />
            <BindCommModal
                title="绑定新租户"
                visible={tenantModal}
                clusterList={clusterList}
                isBindTenant
                onCancel={() => {
                    setTenantVisible(false);
                }}
                onOk={bindTenant}
            />
        </div>
    );
};
