import { useEffect, useState, useMemo } from 'react';
import { Form, Select, Button, Tabs, Spin, message } from 'antd';
import BindCommModal from '@/components/bindCommModal';
import ResourceManageModal from '@/components/resourceManageModal';
import Api from '@/api/console';
import { formItemLayout } from '@/constant';
import { isSparkEngine } from '@/utils';
import Resource from './resourceView';
import BindTenant from './bindTenant';
import type { IClusterProps } from '@/components/bindCommModal';
import type { ITableProps } from './bindTenant';
import type { ICapacityProps } from './resourceView/helper';
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
	// 多个 tab 下用到，父组件存个值
	const [queueList, setQueueList] = useState<ICapacityProps[]>([]);
	const [tenantInfo, setTenantInfo] = useState<ITableProps | undefined>(undefined);

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
			getEnginesByCluster(value.clusterId!);
		}
	};

	const handleResourceManage = (record: ITableProps) => {
		setManageModalVisible(true);
		setTenantInfo(record);
	};

	const bindTenant = (params: {
		canSubmit: boolean;
		reqParams: Record<string, any>;
		hasKubernetes: boolean;
	}) => {
		const { canSubmit, reqParams } = params;
		if (canSubmit) {
			Api.bindTenant({ ...reqParams }).then((res) => {
				if (res.code === 1) {
					setTenantVisible(false);
					message.success('租户绑定成功');
					// this.searchTenant();
				}
			});
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
									onGetQueueList={(queues) =>
										setQueueList(queues as ICapacityProps[])
									}
								/>
							</TabPane>
						) : null}
						<TabPane tab="租户绑定" key="bindTenant">
							<BindTenant
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
				queueList={queueList}
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
