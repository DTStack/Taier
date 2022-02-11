import { useState, useEffect } from 'react';
import { Modal, Select, Input, Checkbox, Form } from 'antd';
import api from '@/api/console';
import { InfoCircleOutlined } from '@ant-design/icons';
import {
	formItemLayout,
	ENGINE_SOURCE_TYPE_ENUM,
	ENGINE_SOURCE_TYPE,
	PROJECT_CREATE_MODEL,
	COMPONENT_CONFIG_NAME,
	COMPONENT_TYPE_VALUE,
} from '@/constant';
import { useEnv } from '../customHooks';
import HelpDoc from '../helpDoc';
import EngineConfigItem from './engineForm';
import { getEngineSourceTypeName } from '@/utils/enums';
import { convertToObj } from '@/utils';
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
	onOk?: (params: {
		canSubmit: boolean;
		reqParams: Record<string, any>;
		hasKubernetes: boolean;
	}) => Promise<void>;
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
	const [metaComponent, setMetaComponent] = useState<
		Valueof<typeof COMPONENT_TYPE_VALUE> | undefined
	>(undefined);
	const [loading, setLoading] = useState(false);
	const { env, queueList } = useEnv({
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

	const getMetaComponent = (value: number) => {
		api.getMetaComponent({
			clusterId: value,
		}).then((res) => {
			if (res.code === 1) {
				setMetaComponent(res.data);
			}
		});
	};

	const handleClusterChanged = (value: number) => {
		if (typeof value !== 'undefined') {
			getMetaComponent(value);
		}
	};

	useEffect(() => {
		onSearchTenantUser();
		if (typeof clusterId !== 'undefined') {
			getMetaComponent(clusterId);
		}
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
		form.validateFields().then((rawValues) => {
			const values = convertToObj(rawValues);

			const params: any = {
				tenantId: values.tenantId,
				clusterId: values.clusterId,
				bindDBList: [],
			};

			params.bindDBList.push({
				componentCode: metaComponent,
				createFlag: !values.hadoop.createModel,
				dbName:
					values.hadoop.createModel === PROJECT_CREATE_MODEL.NORMAL
						? undefined
						: values.hadoop.database,
			});

			setLoading(true);
			onOk?.(params).finally(() => {
				setLoading(false);
			});
		});
	};

	const bindEnginName = getEnginName();

	return (
		<Modal
			title={title}
			visible={visible}
			onOk={handleModalOk}
			confirmLoading={loading}
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
						initialValue={tenantInfo?.tenantName || ''}
					>
						<Select
							allowClear
							placeholder="请搜索要绑定的租户"
							optionFilterProp="title"
							disabled={disabled}
							filterOption={(input, option) =>
								option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >=
								0
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
						initialValue={clusterId || ''}
					>
						<Select
							allowClear
							placeholder="请选择集群"
							disabled={disabled}
							onChange={handleClusterChanged}
						>
							{clusterList.map((clusterItem) => {
								return (
									<Option
										key={clusterItem.clusterId}
										value={clusterItem.clusterId}
									>
										{clusterItem.clusterName}
									</Option>
								);
							})}
						</Select>
					</FormItem>
					<FormItem noStyle dependencies={['clusterId']}>
						{({ getFieldValue: getValue }) => {
							const hadoopName = metaComponent
								? COMPONENT_CONFIG_NAME[metaComponent]
								: '未知';
							return (
								(getValue('clusterId') || getValue('clusterId') === 0) && (
									<>
										<FormItem {...formItemLayout} label="计算引擎配置">
											<FormItem
												noStyle
												name="enableHadoop"
												initialValue={true}
												valuePropName="checked"
											>
												<Checkbox>Hadoop</Checkbox>
											</FormItem>
											<HelpDoc param="Hive 2.x" doc="extraHive" />
										</FormItem>
										<FormItem
											noStyle
											dependencies={['clusterId', 'enableHadoop']}
										>
											{({ getFieldValue }) => (
												<EngineConfigItem
													form={form}
													formParentField="hadoop"
													formItemLayout={formItemLayout}
													checked={getFieldValue('enableHadoop')}
													metaComponent={metaComponent}
													hadoopName={hadoopName}
													clusterId={getFieldValue('clusterId')}
													engineType={ENGINE_SOURCE_TYPE_ENUM.HADOOP}
												/>
											)}
										</FormItem>
									</>
								)
							);
						}}
					</FormItem>

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
					{env[ENGINE_SOURCE_TYPE.HADOOP] && !env[ENGINE_SOURCE_TYPE.KUBERNETES] ? (
						<div className="border-item">
							<div className="engine-title">Hadoop</div>
							<FormItem
								label="资源队列"
								{...formItemLayout}
								tooltip="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群"
								name="queueId"
								rules={[
									{
										required: true,
										message: '资源队列不可为空！',
									},
								]}
								initialValue={tenantInfo?.tenantName}
							>
								<Select allowClear placeholder="请选择资源队列">
									{queueList.map((item: any) => {
										return (
											<Option
												key={`${item.queueId}`}
												value={`${item.queueId}`}
											>
												{item.queueName}
											</Option>
										);
									})}
								</Select>
							</FormItem>
						</div>
					) : null}
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
