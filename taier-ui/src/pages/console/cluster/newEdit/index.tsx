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

/* eslint-disable react/no-array-index-key */
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { Form, Tabs, message, Modal, Spin } from 'antd';
import { history } from 'umi';
import { cloneDeep } from 'lodash';
import TestRestIcon from '@/components/testResultIcon';
import MultiVersionComp from './components/multiVerComp';
import { ExclamationCircleFilled } from '@ant-design/icons';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import {
	TABS_TITLE_KEY,
	COMPONENT_CONFIG_NAME,
	DEFAULT_COMP_VERSION,
	COMP_ACTION,
	DRAWER_MENU_ENUM,
} from '@/constant';
import { convertToObj } from '@/utils';
import Api from '@/api';
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
import ComponentButton from './components/compsBtn';
import MetaIcon from './components/metaIcon';
import {
	CommonComponentIcon,
	SchedulingComponentIcon,
	StoreComponentIcon,
	ComputeComponentIcon,
} from '@/components/icon';
import SingleVerComp from './components/singleVerComp';
import { FormContext } from './context';
import type {
	ISaveCompsData,
	IModifyComp,
	IEditClusterRefProps,
	IScheduleComponent,
	IComponentProps,
	IScheduleComponentComp,
	IConfirmComps,
	IGetLoadTemplateParams,
	ITestConnectsParams,
	ITestErrorMsg,
	ITestStatusProps,
	IVersionData,
} from './interface';
import './index.scss';

const { TabPane } = Tabs;
const { confirm } = Modal;

const TABS_TITLE = {
	[TABS_TITLE_KEY.COMMON]: {
		icon: <CommonComponentIcon style={{ marginRight: 2 }} />,
		name: '公共组件',
	},
	[TABS_TITLE_KEY.SOURCE]: {
		icon: <SchedulingComponentIcon style={{ marginRight: 2 }} />,
		name: '资源调度组件',
	},
	[TABS_TITLE_KEY.STORE]: {
		icon: <StoreComponentIcon style={{ marginRight: 2 }} />,
		name: '存储组件',
	},
	[TABS_TITLE_KEY.COMPUTE]: {
		icon: <ComputeComponentIcon style={{ marginRight: 2 }} />,
		name: '计算组件',
	},
};
const TABS_POP_VISIBLE = {
	[TABS_TITLE_KEY.COMMON]: false,
	[TABS_TITLE_KEY.SOURCE]: false,
	[TABS_TITLE_KEY.STORE]: false,
	[TABS_TITLE_KEY.COMPUTE]: false,
};

/**
 * 测试连通性返回结果类型
 */
type ITestStatus = Record<number, ITestStatusProps>;

export default forwardRef((_, ref) => {
	const [form] = Form.useForm();
	const [activeKey, setActiveKey] = useState<TABS_TITLE_KEY>(TABS_TITLE_KEY.COMMON);
	const [schedulingComponent, setScheduling] = useState<IScheduleComponentComp[][]>(() =>
		initialScheduling(),
	);
	const [versionData, setVersion] = useState<IVersionData>({});
	const [saveCompsData, setSaveCompsData] = useState<ISaveCompsData[]>([]);
	const [testStatus, setStatus] = useState<ITestStatus>({});
	const [disabledMeta, setDisabledMeta] = useState(false);
	const [popVisible, setPopVisible] = useState(TABS_POP_VISIBLE);
	const [loading, setLoading] = useState(false);

	// 多版本组件的componentTypeCode与对应的versionName数组映射
	const [versionMap, setVersionMap] = useState<Record<number, string[]>>({});

	const mode = (history.location.query?.mode as string) || 'view';

	useImperativeHandle(
		ref,
		(): IEditClusterRefProps => ({
			testConnects,
			handleComplete: () => {
				const showConfirm = (comps: Set<IModifyComp>) => {
					confirm({
						title: `${getCompsName(comps).join('、')} 尚未保存，是否退出编辑？`,
						content: null,
						icon: <ExclamationCircleFilled color="#FAAD14" />,
						okText: '确定',
						cancelText: '取消',
						onOk: () => {
							history.push({
								query: {
									drawer: DRAWER_MENU_ENUM.CLUSTER,
								},
							});
						},
						onCancel: () => {},
					});
				};
				form.validateFields().then((values) => {
					const valuesObj = convertToObj(values);
					const modifyComps = getModifyComp(valuesObj, schedulingComponent, versionMap);

					if (modifyComps.size) {
						showConfirm(modifyComps);
					} else {
						history.push({
							query: {
								drawer: DRAWER_MENU_ENUM.CLUSTER,
							},
						});
					}
				});
			},
		}),
	);

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
					scheduling?.forEach((comps: IScheduleComponent) => {
						initData[comps.schedulingCode] = comps.components;
					});
					setScheduling(initData);
					setDisabledMeta(!res.data.canModifyMetadata);
					return getSaveComponentList(res.data.clusterName);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const getSaveComponentList = async (clusterName: string): Promise<number[]> => {
		const res = await Api.getComponentStore({ clusterName });
		if (res.code === 1 && res.data) {
			const nextSaveCompsData: ISaveCompsData[] = [];
			res.data.forEach((item: COMPONENT_TYPE_VALUE) => {
				nextSaveCompsData.push({
					key: item,
					value: COMPONENT_CONFIG_NAME[item],
				});
			});
			setSaveCompsData(nextSaveCompsData);

			return res.data;
		}

		return [];
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
		key?: string | number,
		params?: IGetLoadTemplateParams,
		nextScheduling?: IScheduleComponentComp[][],
	) => {
		const components = nextScheduling || schedulingComponent;
		const clusterName = history.location.query?.clusterName;
		const clusterId = history.location.query?.clusterId;
		const typeCode = (
			typeof key !== 'undefined' ? Number(key) : components[activeKey][0]?.componentTypeCode
		) as keyof typeof DEFAULT_COMP_VERSION;
		const comp = getCurrentComp(components[activeKey], {
			typeCode,
		});
		const saveParams: Partial<IComponentProps> = {
			componentTypeCode: Number(typeCode),
			versionName: params?.compVersion ?? '',
		};

		const fisrtVersionData = versionData?.hadoopVersion?.[0]?.values?.[0]?.key;
		const versionName =
			params?.compVersion ?? DEFAULT_COMP_VERSION[typeCode] ?? fisrtVersionData ?? '';

		if (isMultiVersion(typeCode) && !params?.compVersion) return;

		const resLists = await getSaveComponentList(clusterName as string);
		if (
			(!comp?.componentTemplate && components[activeKey]?.length) ||
			params?.compVersion ||
			params?.storeType
		) {
			const res = await Api.getLoadTemplate({
				clusterId,
				componentType: typeCode,
				versionName,
				storeType: params?.storeType ?? resLists[0] ?? '',
				deployType: params?.deployType ?? '',
			});
			if (res.code === 1) {
				saveParams.componentTemplate = JSON.stringify(res.data);
			}
			saveComp(saveParams);
		}
	};

	const saveComp = (params: Partial<IComponentProps>, type?: string) => {
		const newCompData = schedulingComponent;
		schedulingComponent[activeKey] = schedulingComponent[activeKey].map((comp) => {
			const newComp = { ...comp };
			if (newComp.componentTypeCode !== params.componentTypeCode) {
				return newComp;
			}
			if (type === COMP_ACTION.ADD) {
				newComp.multiVersion.push(undefined);
			}
			newComp.multiVersion = newComp.multiVersion.map((vcomp) => {
				if (!vcomp) {
					return { ...params };
				}
				if (!isMultiVersion(params.componentTypeCode!)) {
					return { ...vcomp, ...params };
				}
				if (!vcomp?.versionName || vcomp?.versionName === params.versionName) {
					return { ...vcomp, ...params };
				}
				return vcomp;
			}) as IComponentProps[];
			return newComp;
		});
		setScheduling(cloneDeep(newCompData));
	};

	const testConnects = (params?: ITestConnectsParams, callBack?: (bool: boolean) => void) => {
		const typeCode = params?.typeCode ?? '';
		const versionName = params?.versionName ?? '';
		const deployType = params?.deployType ?? '';
		form.validateFields()
			.then((rawValues) => {
				const values = convertToObj(rawValues);
				if (typeCode || typeCode === 0) {
					// 只对比当前组件的当前版本
					const modifyComps = getModifyComp(values, schedulingComponent, {
						[typeCode]: [versionName],
					});
					const includesCurrent = includesCurrentComp(Array.from(modifyComps), {
						typeCode,
						versionName,
					});
					if (modifyComps.size > 0 && includesCurrent) {
						let desc = COMPONENT_CONFIG_NAME[typeCode] as string;
						if (isMultiVersion(typeCode)) {
							desc = `${desc} ${versionName}`;
						}
						message.error(`组件 ${desc} 参数变更未保存，请先保存再测试组件连通性`);
						return;
					}

					callBack?.(true);
					Api.testConnect({
						clusterName: history.location.query?.clusterName as string,
						deployType,
						componentType: typeCode,
						versionName: versionName ?? '',
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
					const modifyComps = getModifyComp(values, schedulingComponent, versionMap);
					if (modifyComps.size > 0) {
						const compsName = getCompsName(modifyComps).join('、');
						message.error(`组件 ${compsName} 参数变更未保存，请先保存再测试组件连通性`);
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
				const isMultiVers = isMultiVersion(typeCode as number);
				const inculdesVersErr = Object.keys(currentCompErr).includes(versionName);
				if (isMultiVers && inculdesVersErr) {
					message.error('请检查配置');
					return;
				}

				const includesTypeErr = Object.keys(err).includes(String(typeCode));
				if ((err && !typeCode) || (err && !isMultiVers && includesTypeErr)) {
					message.error('请检查配置');
				}
			});
	};

	const setTestStatus = (status: ITestStatusProps | ITestStatusProps[], isSingle?: boolean) => {
		if (Array.isArray(status)) {
			const nextTestStatus: ITestStatus = {};
			status.forEach((temp) => {
				nextTestStatus[temp.componentTypeCode] = { ...temp };
			});
			setStatus(nextTestStatus);
		} else if (isSingle) {
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
					typeCode: status.componentTypeCode as number,
					versionName: status?.componentVersion as string,
				},
				status,
				testStatus,
			);
			multiVersion = multiVersion.filter((ver) => ver);

			let sign = false; // 标记是否有测试连通性失败的多版本组件
			const errorMsg: ITestErrorMsg[] = [];

			multiVersion.forEach((mv) => {
				if (mv && !mv.result) {
					sign = true;
					errorMsg.push({
						componentVersion: mv.componentVersion,
						errorMsg: mv.errorMsg,
					});
				}
			});

			const msg: ITestStatusProps = {
				result: null,
				errorMsg: [],
				multiVersion,
				componentTypeCode: status.componentTypeCode,
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
		}
	};

	const handleConfirm = async (action: string, comps: IConfirmComps, mulitple?: boolean) => {
		const newCompData = schedulingComponent;
		let currentCompArr = newCompData[activeKey];
		if (Array.isArray(comps) && comps.length && action !== COMP_ACTION.DELETE) {
			const initialComp = comps.map((code) => {
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
			const { componentTypeCode, versionName, id = '' } = comps as IComponentProps;
			const componentIds = getCompsId(currentCompArr, id);
			let res: { code?: number } = {};
			if (componentIds.length) {
				res = await Api.deleteComponent({ componentId: componentIds[0] });
			}

			if (res?.code === 1 || !componentIds.length) {
				const wrapper = new Set<IScheduleComponentComp>();
				currentCompArr.forEach((comp) => {
					if (isMultiVersion(comp.componentTypeCode) && mulitple) {
						// eslint-disable-next-line no-param-reassign
						comp.multiVersion = comp.multiVersion.filter(
							(vComp) => vComp?.versionName !== versionName,
						);
						wrapper.add(comp);
					}
					if (comp.componentTypeCode !== componentTypeCode) wrapper.add(comp);
				});
				currentCompArr = Array.from(wrapper);

				const multiVersion = getSingleTestStatus(
					{ typeCode: componentTypeCode, versionName },
					null,
					testStatus,
				);
				let fieldValue: Record<string, unknown> = {};
				if (isMultiVersion(componentTypeCode)) {
					fieldValue = { [versionName]: {} };
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

	const handleCompVersion = (typeCode: number, version: string) => {
		if (!isSameVersion(Number(typeCode))) {
			form.setFieldsValue({ [`${typeCode}.versionName`]: version });
			getLoadTemplate(typeCode, { compVersion: version });
			return;
		}
		form.setFieldsValue({
			[typeCode]: {
				versionName: version[version.length - 1],
				hadoopVersionSelect: version,
			},
		});
		getLoadTemplate(typeCode, {
			compVersion: version[version.length - 1],
		});
	};

	const onTabChange = (key: string) => {
		setActiveKey(Number(key));
		setVersionMap({});
	};

	useEffect(() => {
		getDataList();
		getVersionData();
	}, []);

	const isScheduling = isSchedulings(schedulingComponent);

	const tabPanelTab = (key: TABS_TITLE_KEY) => {
		return (
			<span className="dt-cluster-component-tab-title">
				{TABS_TITLE[key].icon}
				{TABS_TITLE[key].name}
			</span>
		);
	};

	const tabBarExtraContent = (comps: IScheduleComponentComp[]) => {
		return !isViewMode(mode) ? (
			<ComponentButton
				comps={comps}
				popVisible={popVisible[activeKey]}
				activeKey={activeKey}
				handleConfirm={handleConfirm}
				handlePopVisible={handlePopVisible}
			/>
		) : null;
	};

	const handleTabChange = (tabActiveKey: string) => {
		if (!isMultiVersion(Number(tabActiveKey))) {
			getLoadTemplate(tabActiveKey);
		}
	};

	const onVersionChange = (versionName: string, typeCode: COMPONENT_TYPE_VALUE) => {
		if (typeCode || typeCode === 0) {
			const versions = versionMap[typeCode] || [];
			if (versionName && !versions.includes(versionName)) {
				versions.push(versionName);
			}
			setVersionMap({ ...versionMap, [typeCode]: versions });
		}
	};

	const renderContent = (comp: IScheduleComponentComp, isCheckBoxs: boolean) => {
		const clusterInfo = {
			clusterName: history.location.query?.clusterName as string,
			clusterId: history.location.query?.clusterId as string,
		};
		return (
			<TabPane
				tab={
					<div className="flex items-center">
						{COMPONENT_CONFIG_NAME[comp.componentTypeCode]}
						{showDataCheckBox(comp.componentTypeCode) && (
							<MetaIcon
								comp={comp}
								isMetadata={form.getFieldValue(
									`${comp.componentTypeCode}.isMetadata`,
								)}
							/>
						)}
						<TestRestIcon testStatus={testStatus[comp.componentTypeCode] ?? {}} />
					</div>
				}
				key={comp.componentTypeCode.toString()}
			>
				{isMultiVersion(comp.componentTypeCode) ? (
					<MultiVersionComp
						comp={comp}
						view={isViewMode(mode)}
						saveCompsData={saveCompsData}
						isCheckBoxs={isCheckBoxs}
						versionData={versionData}
						testStatus={testStatus[comp.componentTypeCode]?.multiVersion ?? []}
						clusterInfo={clusterInfo}
						saveComp={saveComp}
						getLoadTemplate={getLoadTemplate}
						testConnects={testConnects}
						handleConfirm={handleConfirm}
						onVersionChange={onVersionChange}
					/>
				) : (
					comp?.multiVersion?.map((vcomp) => (
						<SingleVerComp
							comp={vcomp!}
							key={`${vcomp?.versionName || vcomp?.componentTypeCode}`}
							view={isViewMode(mode)}
							disabledMeta={disabledMeta}
							isCheckBoxs={isCheckBoxs}
							isSchedulings={isScheduling}
							versionData={versionData}
							saveCompsData={saveCompsData}
							clusterInfo={clusterInfo}
							saveComp={saveComp}
							handleCompVersion={handleCompVersion}
							testConnects={testConnects}
							handleConfirm={handleConfirm}
						/>
					))
				)}
			</TabPane>
		);
	};

	return (
		<FormContext.Provider value={form}>
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
					{schedulingComponent.map((__, key: TABS_TITLE_KEY) => (
						<TabPane tab={tabPanelTab(key)} key={String(key)} />
					))}
				</Tabs>
				<Spin spinning={loading}>
					{schedulingComponent.map((comps, key) => {
						if (key !== activeKey) {
							return <div key={key} />;
						}
						// 存在HiveServer、SparkThrift两个组件
						const isCheckBoxs = isDataCheckBoxs(comps);

						return (
							<div className="relative" key={String(key)}>
								<Tabs
									tabPosition="left"
									tabBarExtraContent={tabBarExtraContent(comps)}
									className="c-editCluster__container__componentTabs"
									onChange={handleTabChange}
									destroyInactiveTabPane
								>
									{comps?.map((comp) => renderContent(comp, isCheckBoxs))}
								</Tabs>
								{comps?.length === 0 && (
									<div key={activeKey} className="empty-logo">
										<img src="images/emptyLogo.svg" />
									</div>
								)}
							</div>
						);
					})}
				</Spin>
			</div>
		</FormContext.Provider>
	);
});
