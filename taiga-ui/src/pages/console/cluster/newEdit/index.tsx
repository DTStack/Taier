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

import { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Form, Tabs, message, Modal, Carousel, Spin } from 'antd';
import { history } from 'umi';
import { cloneDeep } from 'lodash';
import TestRestIcon from '@/components/testResultIcon';
import MultiVersionComp from './components/multiVerComp';
import { ExclamationCircleFilled } from '@ant-design/icons';
import type { ENGINE_SOURCE_TYPE_ENUM } from '@/constant';
import {
	TABS_TITLE_KEY,
	COMPONENT_CONFIG_NAME,
	DEFAULT_COMP_VERSION,
	COMP_ACTION,
} from '@/constant';
import type { CarouselRef } from 'antd/lib/carousel';
import { convertToObj } from '@/utils';
import Api from '@/api/console';
import {
	initialScheduling,
	isViewMode,
	getModifyComp,
	isSameVersion,
	getCompsId,
	isMultiVersion,
	getCurrentComp,
	includesCurrentComp,
	getSingleTestStatus,
	isDataCheckBoxs,
	showDataCheckBox,
	getCompsName,
	isSchedulings,
} from './help';
import FileConfig from './fileConfig';
import FormConfig from './formConfig';
import ToolBar from './components/toolbar';
import ComponentButton from './components/compsBtn';
import MetaIcon from './components/metaIcon';
import './index.scss';

const { TabPane } = Tabs;
const { confirm } = Modal;

const TABS_TITLE = {
	[TABS_TITLE_KEY.COMMON]: {
		iconName: 'icon-gonggongzujian',
		name: '公共组件',
	},
	[TABS_TITLE_KEY.SOURCE]: {
		iconName: 'icon-ziyuantiaodu',
		name: '资源调度组件',
	},
	[TABS_TITLE_KEY.STORE]: { iconName: 'icon-cunchuzujian', name: '存储组件' },
	[TABS_TITLE_KEY.COMPUTE]: {
		iconName: 'icon-jisuanzujian',
		name: '计算组件',
	},
};
const TABS_POP_VISIBLE = {
	[TABS_TITLE_KEY.COMMON]: false,
	[TABS_TITLE_KEY.SOURCE]: false,
	[TABS_TITLE_KEY.STORE]: false,
	[TABS_TITLE_KEY.COMPUTE]: false,
};

export type IVersionData = Record<
	string,
	{
		dependencyKey: string | null;
		dependencyValue: string | null;
		deployTypes: string | null;
		id: number;
		key: string;
		required: boolean;
		type: string | null;
		value: string;
		values: string | null;
	}[]
>;

interface IComponentProps {
	componentConfig: string;
	componentName: string;
	componentTemplate: string;
	componentTypeCode: number;
	deployType: number;
	engineId: ENGINE_SOURCE_TYPE_ENUM;
	gmtCreate: number;
	gmtModified: number;
	hadoopVersion: string;
	id: number;
	isDefault: boolean;
	storeType: number;
}

type ITestStatus = Record<number, any>;

interface IScheduleComponent {
	components: {
		componentTypeCode: keyof typeof COMPONENT_CONFIG_NAME;
		multiVersion: (IComponentProps | undefined)[];
	}[];
	schedulingCode: number;
	schedulingName: string;
}

export default forwardRef((_, ref) => {
	const [form] = Form.useForm();
	const tabsRef = useRef<CarouselRef>(null);
	const [activeKey, setActiveKey] = useState(0);
	const [schedulingComponent, setScheduling] = useState<IScheduleComponent['components'][]>(() =>
		initialScheduling(),
	);
	const [versionData, setVersion] = useState<IVersionData>({});
	const [saveCompsData, setSaveCompsData] = useState<
		{
			key: number;
			value: string;
		}[]
	>([]);
	const [testStatus, setStatus] = useState<ITestStatus>({});
	const [disabledMeta, setDisabledMeta] = useState(false);
	const [popVisible, setPopVisible] = useState(TABS_POP_VISIBLE);
	const [loading, setLoading] = useState(false);
	const mode = (history.location.query?.mode as string) || 'view';

	useImperativeHandle(ref, () => ({
		testConnects,
		handleComplete: () => {
			const showConfirm = (arr: any[]) => {
				confirm({
					title: `${getCompsName(arr).join('、')}尚未保存，是否需要保存？`,
					content: null,
					icon: <ExclamationCircleFilled color="#FAAD14" />,
					okText: '保存',
					cancelText: '取消',
					onOk: () => {},
					onCancel: () => {
						history.push('/console/clusterManage');
					},
				});
			};
			form.validateFields().then((values) => {
				const modifyCompsArr = getModifyComp(values, schedulingComponent);
				if (!modifyCompsArr.size) {
					// history.push('/console/clusterManage');
					return;
				}
				showConfirm(modifyCompsArr);
			});
		},
	}));

	const getDataList = () => {
		const clusterId = history.location.query?.clusterId;
		setLoading(true);
		Api.getClusterInfo({
			clusterId: Number(clusterId),
		})
			.then((res) => {
				if (res.code === 1) {
					const initData = initialScheduling();
					const { scheduling } = res.data;
					if (scheduling) {
						scheduling.forEach((comps: IScheduleComponent) => {
							initData[comps.schedulingCode] = comps.components;
						});
					}
					setScheduling(initData);
					setDisabledMeta(!res.data.canModifyMetadata);
					return getSaveComponentList(res.data.clusterName);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const getSaveComponentList = async (clusterName: string) => {
		const res = await Api.getComponentStore({ clusterName });
		if (res.code === 1 && res.data) {
			const nextSaveCompsData: any[] = [];
			res.data.forEach((item: number) => {
				nextSaveCompsData.push({
					key: item,
					value: (COMPONENT_CONFIG_NAME as any)[item],
				});
			});
			setSaveCompsData(nextSaveCompsData);
		}
	};

	const getVersionData = () => {
		Api.getVersionData().then((res) => {
			if (res.code === 1) {
				setVersion(res.data);
			}
		});
	};

	// 获取组件模板
	const getLoadTemplate = async (
		key?: string,
		params?: any,
		nextScheduling?: IScheduleComponent['components'][],
	) => {
		const components = nextScheduling || schedulingComponent;
		const clusterName = history.location.query?.clusterName;
		const typeCode = (
			key ? Number(key) : components[activeKey][0]?.componentTypeCode
		) as keyof typeof DEFAULT_COMP_VERSION;
		const comp = getCurrentComp(components[activeKey], {
			typeCode,
		});
		const saveParams: any = {
			componentTypeCode: Number(typeCode),
			hadoopVersion: params?.compVersion ?? '',
		};
		const version = params?.compVersion ?? DEFAULT_COMP_VERSION[typeCode] ?? '';
		const originVersion = isSameVersion(Number(typeCode)) ? version : '';

		if (isMultiVersion(typeCode) && !params?.compVersion) return;

		if (
			(!comp?.componentTemplate && components[activeKey]?.length) ||
			params?.compVersion ||
			params?.storeType
		) {
			const res = await Api.getLoadTemplate({
				clusterName,
				componentType: typeCode,
				version,
				originVersion,
				storeType: params?.storeType ?? form.getFieldValue(`${typeCode}.storeType`) ?? '',
				deployType: params?.deployType ?? '',
			});
			if (res.code === 1) saveParams.componentTemplate = JSON.stringify(res.data);
			saveComp(saveParams);
			getSaveComponentList(clusterName as string);
		}
	};

	const saveComp = (params: any, type?: string) => {
		const newCompData = cloneDeep(schedulingComponent);
		newCompData[activeKey] = schedulingComponent[activeKey].map((comp) => {
			if (comp.componentTypeCode !== params.componentTypeCode) return comp;
			if (type === COMP_ACTION.ADD) comp.multiVersion.push(undefined);
			// eslint-disable-next-line no-param-reassign
			comp.multiVersion = comp.multiVersion.map((vcomp: any) => {
				if (!vcomp) return { ...params };
				if (!isMultiVersion(params.componentTypeCode)) return { ...vcomp, ...params };
				if (!vcomp?.hadoopVersion || vcomp?.hadoopVersion === params.hadoopVersion)
					return { ...vcomp, ...params };
				return vcomp;
			});
			return comp;
		});
		setScheduling(newCompData);
	};

	const testConnects = (params?: any, callBack?: (bool: boolean) => void) => {
		const typeCode = params?.typeCode ?? '';
		const hadoopVersion = params?.hadoopVersion ?? '';
		const deployType = params?.deployType ?? '';
		form.validateFields()
			.then((rawValues) => {
				const values = convertToObj(rawValues);
				const modifyComps = getModifyComp(values, schedulingComponent);
				if (typeCode || typeCode === 0) {
					if (
						modifyComps.size > 0 &&
						includesCurrentComp(Array.from(modifyComps), {
							typeCode,
							hadoopVersion,
						})
					) {
						let desc = (COMPONENT_CONFIG_NAME as any)[typeCode];
						if (isMultiVersion(typeCode))
							desc = `${desc} ${(Number(hadoopVersion) / 100).toFixed(2)}`;
						message.error(`组件 ${desc} 参数变更未保存，请先保存再测试组件连通性`);
						return;
					}
					callBack?.(true);
					Api.testConnect({
						clusterName: history.location.query?.clusterName as string,
						deployType,
						componentType: typeCode,
						componentVersion: hadoopVersion ?? '',
					})
						.then((res) => {
							if (res.code === 1) {
								setTestStatus(res.data, true);
							}
						})
						.finally(() => {
							callBack?.(false);
						});
				} else {
					if (modifyComps.size > 0) {
						message.error(
							`组件 ${getCompsName(modifyComps).join(
								'、',
							)} 参数变更未保存，请先保存再测试组件连通性`,
						);
						return;
					}
					callBack?.(true);
					Api.testConnects({
						clusterName: history.location.query?.clusterName as string,
					})
						.then((res) => {
							if (res.code === 1) {
								setTestStatus(res.data);
							}
						})
						.finally(() => {
							callBack?.(false);
						});
				}
			})
			.catch((err) => {
				// 当前组件错误校验
				const currentCompErr = err ? err[String(typeCode)] || {} : {};
				if (
					isMultiVersion(typeCode) &&
					Object.keys(currentCompErr).includes(hadoopVersion)
				) {
					message.error('请检查配置');
					return;
				}
				if (
					(err && !typeCode) ||
					(err &&
						!isMultiVersion(typeCode) &&
						Object.keys(err).includes(String(typeCode)))
				) {
					message.error('请检查配置');
				}
			});
	};

	const setTestStatus = (status: any, isSingle?: boolean) => {
		if (isSingle) {
			const currentComp = schedulingComponent[activeKey].find(
				(comp) => comp.componentTypeCode === status.componentTypeCode,
			);
			if (!isMultiVersion(status.componentTypeCode)) {
				setStatus((t) => ({
					...t,
					[status.componentTypeCode]: {
						...status,
					},
				}));
				return;
			}
			let multiVersion = getSingleTestStatus(
				{
					typeCode: status.componentTypeCode,
					hadoopVersion: status?.componentVersion,
				},
				status,
				testStatus,
			);
			multiVersion = multiVersion.filter((ver) => ver);

			let sign = false; // 标记是否有测试连通性失败的多版本组件
			const errorMsg: any[] = [];

			multiVersion.forEach((mv) => {
				if (mv && !mv.result) {
					sign = true;
					errorMsg.push({
						componentVersion: mv.componentVersion,
						errorMsg: mv.errorMsg,
					});
				}
			});

			const msg: any = {
				result: null,
				errorMsg: [],
				multiVersion,
			};
			if (!sign && currentComp?.multiVersion?.length === multiVersion.length) {
				msg.result = true;
			}
			if (sign) {
				msg.result = false;
				msg.errorMsg = errorMsg;
			}

			setStatus((s) => ({
				...s,
				[status.componentTypeCode]: msg,
			}));
			return;
		}
		const nextTestStatus: any = {};
		status.forEach((temp: any) => {
			nextTestStatus[temp.componentTypeCode] = { ...temp };
		});
		setStatus(nextTestStatus);
	};

	const handleConfirm = async (action: string, comps: any | any[], mulitple?: boolean) => {
		const newCompData = schedulingComponent;
		let currentCompArr = newCompData[activeKey];
		if (comps.length && action !== COMP_ACTION.DELETE) {
			const initialComp = comps.map((code: any) => {
				if (!isMultiVersion(code))
					return {
						componentTypeCode: code,
						multiVersion: [undefined],
					};
				return { componentTypeCode: code, multiVersion: [] };
			});
			currentCompArr = currentCompArr.concat(initialComp);
		}

		if (action === COMP_ACTION.DELETE) {
			const { componentTypeCode, hadoopVersion, id = '' } = comps;
			const componentIds = getCompsId(currentCompArr, id);
			let res: any;
			if (componentIds.length) {
				res = await Api.deleteComponent({ componentIds });
			}

			if (res?.code === 1 || !componentIds.length) {
				const wrapper = new Set<IScheduleComponent['components'][number]>();
				currentCompArr.forEach((comp) => {
					if (isMultiVersion(comp.componentTypeCode) && mulitple) {
						// eslint-disable-next-line no-param-reassign
						comp.multiVersion = comp.multiVersion.filter(
							(vComp) => vComp?.hadoopVersion !== hadoopVersion,
						);
						wrapper.add(comp);
					}
					if (comp.componentTypeCode !== componentTypeCode) wrapper.add(comp);
				});
				currentCompArr = Array.from(wrapper);

				const multiVersion = getSingleTestStatus(
					{ typeCode: componentTypeCode, hadoopVersion },
					null,
					testStatus,
				);
				let fieldValue: any = {};
				if (isMultiVersion(componentTypeCode)) {
					fieldValue = { [hadoopVersion]: {} };
				}

				form.setFieldsValue({
					[componentTypeCode]: fieldValue,
				});
				setStatus((s) => ({
					...s,
					[componentTypeCode]: {
						...s[componentTypeCode],
						result: null,
						multiVersion,
					},
				}));
			}
		}

		newCompData[activeKey] = currentCompArr;
		setScheduling(newCompData);
		getLoadTemplate(undefined, undefined, newCompData);
	};

	const handlePopVisible = (visible?: boolean) => {
		setPopVisible((visibles) => ({
			...visibles,
			[activeKey]: visible ?? true,
		}));
	};

	const handleCompVersion = (typeCode: string, version: string) => {
		if (!isSameVersion(Number(typeCode))) {
			form.setFieldsValue({ [`${typeCode}.hadoopVersion`]: version });
			getLoadTemplate(typeCode, { compVersion: version });
			return;
		}
		form.setFieldsValue({
			[typeCode]: {
				hadoopVersion: version[version.length - 1],
				hadoopVersionSelect: version,
			},
		});
		getLoadTemplate(typeCode, {
			compVersion: version[version.length - 1],
		});
	};

	const onTabChange = (key: string) => {
		setActiveKey(Number(key));
		tabsRef.current?.goTo(Number(key));
	};

	useEffect(() => {
		getDataList();
		getVersionData();
	}, []);

	const isScheduling = isSchedulings(schedulingComponent);

	return (
		<div className="dt-cluster">
			<Tabs
				tabPosition="top"
				onChange={onTabChange}
				activeKey={activeKey.toString()}
				className="dt-cluster-component"
				tabBarExtraContent={{
					left: <div className="dt-cluster-title">集群配置</div>,
				}}
			>
				{schedulingComponent.map((__, key) => (
					<TabPane
						tab={
							<span className="dt-cluster-component-tab-title">
								<i
									className={`iconfont ${TABS_TITLE[key].iconName}`}
									style={{ marginRight: 2 }}
								/>
								{TABS_TITLE[key].name}
							</span>
						}
						key={String(key)}
					/>
				))}
			</Tabs>
			<Spin spinning={loading}>
				<Carousel
					ref={tabsRef}
					className="dt-cluster-component-tab-content"
					dots={false}
					draggable={false}
					infinite={false}
				>
					{schedulingComponent.map((comps, key) => {
						if (key !== activeKey) {
							return <div />;
						}
						// 存在HiveServer、SparkThrift两个组件
						const isCheckBoxs = isDataCheckBoxs(comps);
						if (comps?.length === 0) {
							return (
								<div key={activeKey} className="empty-logo">
									<img src="assets/imgs/emptyLogo.svg" />
								</div>
							);
						}

						return (
							<div>
								<Tabs
									tabPosition="left"
									tabBarExtraContent={
										!isViewMode(mode) && (
											<ComponentButton
												comps={comps}
												popVisible={popVisible[activeKey]}
												activeKey={activeKey}
												handleConfirm={handleConfirm}
												handlePopVisible={handlePopVisible}
											/>
										)
									}
									className="c-editCluster__container__componentTabs"
									onChange={(tabActiveKey) => {
										if (!isMultiVersion(Number(tabActiveKey))) {
											getLoadTemplate(tabActiveKey);
										}
									}}
								>
									{comps?.length > 0 &&
										comps.map((comp) => {
											return (
												<TabPane
													tab={
														<div className="flex">
															{
																COMPONENT_CONFIG_NAME[
																	comp.componentTypeCode
																]
															}
															{showDataCheckBox(
																comp.componentTypeCode,
															) && (
																<MetaIcon
																	comp={comp}
																	isMetadata={form.getFieldValue(
																		`${comp.componentTypeCode}.isMetadata`,
																	)}
																/>
															)}
															<TestRestIcon
																testStatus={
																	testStatus[
																		comp.componentTypeCode
																	] ?? {}
																}
															/>
														</div>
													}
													key={comp.componentTypeCode.toString()}
												>
													<>
														{isMultiVersion(comp.componentTypeCode) ? (
															<Form
																className="dt-cluster-content"
																form={form}
															>
																<MultiVersionComp
																	comp={comp}
																	form={form}
																	view={isViewMode(mode)}
																	saveCompsData={saveCompsData}
																	versionData={versionData}
																	testStatus={
																		testStatus[
																			comp.componentTypeCode
																		]?.multiVersion ?? []
																	}
																	clusterInfo={{
																		clusterName:
																			history.location.query
																				?.clusterName,
																		clusterId:
																			history.location.query
																				?.clusterId,
																	}}
																	saveComp={saveComp}
																	getLoadTemplate={
																		getLoadTemplate
																	}
																	testConnects={testConnects}
																	handleConfirm={handleConfirm}
																/>
															</Form>
														) : (
															comp?.multiVersion?.map((vcomp) => {
																return (
																	<Form
																		className="dt-cluster-content"
																		form={form}
																	>
																		<FileConfig
																			comp={vcomp}
																			view={isViewMode(mode)}
																			disabledMeta={
																				disabledMeta
																			}
																			isCheckBoxs={
																				isCheckBoxs
																			}
																			isSchedulings={
																				isScheduling
																			}
																			form={form}
																			versionData={
																				versionData
																			}
																			saveCompsData={
																				saveCompsData
																			}
																			clusterInfo={{
																				clusterName:
																					history.location
																						.query
																						?.clusterName,
																				clusterId:
																					history.location
																						.query
																						?.clusterId,
																			}}
																			saveComp={saveComp}
																			handleCompVersion={
																				handleCompVersion
																			}
																		/>
																		<FormConfig
																			comp={vcomp}
																			view={isViewMode(mode)}
																			form={form}
																			clusterInfo={{
																				clusterName:
																					history.location
																						.query
																						?.clusterName,
																				clusterId:
																					history.location
																						.query
																						?.clusterId,
																			}}
																		/>
																		{!isViewMode(mode) && (
																			<ToolBar
																				comp={vcomp}
																				clusterInfo={{
																					clusterName:
																						history
																							.location
																							.query
																							?.clusterName,
																					clusterId:
																						history
																							.location
																							.query
																							?.clusterId,
																				}}
																				form={form}
																				saveComp={saveComp}
																				testConnects={
																					testConnects
																				}
																				handleConfirm={
																					handleConfirm
																				}
																			/>
																		)}
																	</Form>
																);
															})
														)}
													</>
												</TabPane>
											);
										})}
								</Tabs>
							</div>
						);
					})}
				</Carousel>
			</Spin>
		</div>
	);
});
