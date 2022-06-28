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

import { useEffect, useState, useMemo, useRef } from 'react';
import { Form, Select, Button, Tabs, Spin, message } from 'antd';
import BindCommModal from '@/components/bindCommModal';
import ResourceManageModal from '@/components/resourceManageModal';
import Api from '@/api';
import { formItemLayout } from '@/constant';
import { isSparkEngine } from '@/utils/is';
import Resource from './resourceView';
import BindTenant from './bindTenant';
import type { IClusterProps } from '@/components/bindCommModal';
import type { ITableProps } from './bindTenant';
import { catalogueService } from '@/services';
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
	queues: { queueId: number; queueName: string }[];
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
		[engineList],
	);

	const clusterOptions = useMemo(
		() =>
			clusterList.map((cluster) => ({
				label: cluster.clusterName,
				value: cluster.id,
			})),
		[clusterList],
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
					<Select
						style={{ width: 264 }}
						placeholder="请选择集群"
						options={clusterOptions}
					/>
				</FormItem>
				<FormItem label="引擎" name="engineId">
					<Select
						style={{ width: 264 }}
						placeholder="请选择引擎"
						options={engineOptions}
					/>
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
				{form.getFieldValue('engineId') ? (
					<Tabs
						animated={false}
						activeKey={activeKey}
						onChange={handleChangeEngine}
						className="dt-resource-tabs"
					>
						{isSparkEngine(form.getFieldValue('engineId')) ? (
							<TabPane tab="资源全景" key="showResource">
								<Resource
									clusterName={
										clusterList.find(
											(cluster) =>
												cluster.id === form.getFieldValue('clusterId'),
										)?.clusterName
									}
								/>
							</TabPane>
						) : null}
						<TabPane tab="租户绑定" key="bindTenant">
							<BindTenant
								ref={bindTenantRef}
								clusterId={form.getFieldValue('clusterId')}
								clusterName={
									clusterOptions.find(
										(cluster) =>
											cluster.value === form.getFieldValue('clusterId'),
									)?.label
								}
								engineType={form.getFieldValue('engineId')}
								onClick={handleResourceManage}
							/>
						</TabPane>
					</Tabs>
				) : (
					<span>无法获取资源全景，请检查是否选择引擎，或该集群下无引擎</span>
				)}
			</Spin>
			<ResourceManageModal
				title={`资源管理 (${tenantInfo?.tenantName ?? ''})`}
				visible={manageModalVisible}
				isBindTenant={false}
				clusterList={clusterList}
				clusterId={form.getFieldValue('clusterId')}
				tenantId={tenantInfo?.tenantId}
				queueId={tenantInfo?.queueId}
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
