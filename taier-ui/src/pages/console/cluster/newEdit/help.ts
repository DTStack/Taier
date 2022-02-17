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

import { TABS_TITLE_KEY } from '@/constant';
import _ from 'lodash';
import {
	COMPONENT_TYPE_VALUE,
	COMPONENT_CONFIG_NAME,
	FILE_TYPE,
	CONFIG_ITEM_TYPE,
} from '@/constant';

const DEFAULT_PARAMS = [
	'storeType',
	'principal',
	'versionName',
	'kerberosFileName',
	'uploadFileName',
	'isMetadata',
	'isDefault',
];

// 是否为yarn、hdfs、Kubernetes组件
export function isNeedTemp(typeCode: number): boolean {
	const temp: number[] = [COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.HDFS];
	return temp.indexOf(typeCode) > -1;
}

export function isKubernetes(typeCode: number): boolean {
	return false;
}

export function isYarn(typeCode: number): boolean {
	return COMPONENT_TYPE_VALUE.YARN === typeCode;
}

export function isFLink(typeCode: number): boolean {
	return COMPONENT_TYPE_VALUE.FLINK === typeCode;
}

export function isDtscriptAgent(typeCode: number): boolean {
	return false;
}

export function isHaveGroup(typeCode: number): boolean {
	const tmp: number[] = [COMPONENT_TYPE_VALUE.FLINK, COMPONENT_TYPE_VALUE.SPARK];
	return tmp.indexOf(typeCode) > -1;
}

export function notCustomParam(typeCode: number): boolean {
	const tmp: number[] = [COMPONENT_TYPE_VALUE.SFTP];
	return tmp.indexOf(typeCode) > -1;
}

export function isOtherVersion(code: number): boolean {
	const tmp: number[] = [
		COMPONENT_TYPE_VALUE.FLINK,
		COMPONENT_TYPE_VALUE.SPARK,
		COMPONENT_TYPE_VALUE.SPARK_THRIFT,
		COMPONENT_TYPE_VALUE.HIVE_SERVER,
	];
	return tmp.indexOf(code) > -1;
}

export function isSameVersion(code: number): boolean {
	const tmp: number[] = [COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN];
	return tmp.indexOf(code) > -1;
}

export function isMultiVersion(code: number): boolean {
	const tmp: number[] = [
		COMPONENT_TYPE_VALUE.FLINK,
		COMPONENT_TYPE_VALUE.SPARK,
		COMPONENT_TYPE_VALUE.SPARK_THRIFT,
	];
	return tmp.indexOf(code) > -1;
}

export function needZipFile(type: number): boolean {
	const tmp: number[] = [FILE_TYPE.KERNEROS, FILE_TYPE.CONFIGS];
	return tmp.indexOf(type) > -1;
}

export function showDataCheckBox(code: number): boolean {
	const tmp: number[] = [COMPONENT_TYPE_VALUE.HIVE_SERVER, COMPONENT_TYPE_VALUE.SPARK_THRIFT];
	return tmp.indexOf(code) > -1;
}

export function notFileConfig(code: number): boolean {
	return false;
}

export function getActionType(mode: string): string {
	switch (mode) {
		case 'view':
			return '查看集群';
		case 'new':
			return '新增集群';
		case 'edit':
			return '编辑集群';
		default:
			return '';
	}
}

export function isDataCheckBoxs(comps: any[]): boolean {
	return comps.filter((comp) => showDataCheckBox(comp.componentTypeCode)).length === 2;
}

export function isSourceTab(activeKey: number): boolean {
	return activeKey === TABS_TITLE_KEY.SOURCE;
}

export function initialScheduling(): any[] {
	const arr = [];
	return Object.values(TABS_TITLE_KEY).map((tabKey: number) => {
		// eslint-disable-next-line no-return-assign
		return (arr[tabKey] = []);
	});
}

export function giveMeAKey(): string {
	// eslint-disable-next-line no-bitwise
	return `${new Date().getTime()}${~~(Math.random() * 100000)}`;
}

export function isViewMode(mode: string): boolean {
	return mode === 'view';
}

export function isFileParam(key: string): boolean {
	return ['kerberosFileName', 'uploadFileName'].indexOf(key) > -1;
}

export function isMetaData(key: string): boolean {
	return ['isMetadata'].indexOf(key) > -1;
}

export function isDefaultVersion(key: string): boolean {
	return ['isDefault'].indexOf(key) > -1;
}

export function isDeployMode(key: string): boolean {
	return key === 'deploymode';
}

export function isRadioLinkage(type: string): boolean {
	return type === CONFIG_ITEM_TYPE.RADIO_LINKAGE;
}

export function isGroupType(type: string): boolean {
	return type === CONFIG_ITEM_TYPE.GROUP;
}

export function isCustomType(type: string): boolean {
	return type === CONFIG_ITEM_TYPE.CUSTOM_CONTROL;
}

// 模版中存在id则为自定义参数
export function getCustomerParams(temps: any[]): any[] {
	return temps.filter((temp) => isCustomType(temp.type));
}

export function getCompsId(currentComps: any[], id: number): any[] {
	const ids: any[] = [];
	currentComps.forEach((comp) => {
		(comp?.multiVersion ?? []).forEach((vcomp: any) => {
			if (vcomp?.id === id) ids.push(vcomp.id);
		});
	});
	return ids;
}

export function getValueByJson(value: any): any {
	return value ? JSON.parse(value) : null;
}

export function getOptions(version: any[]): any[] {
	const opt: any[] = [];
	version.forEach((ver: any, index: number) => {
		opt[index] = { label: ver.key, value: ver.key };
		if (ver?.values && ver?.values?.length > 0) {
			opt[index] = {
				...opt[index],
				children: getOptions(ver.values),
			};
		}
	});
	return opt;
}

export function getCompsName(comps: any[]): any[] {
	return Array.from(comps).map((comp: any) => {
		if (isMultiVersion(comp.typeCode)) {
			return `${(COMPONENT_CONFIG_NAME as any)[comp.typeCode]} ${(
				Number(comp.versionName) / 100
			).toFixed(2)}`;
		}
		return (COMPONENT_CONFIG_NAME as any)[comp.typeCode];
	});
}

export function getInitialValue(version: any[], commVersion: string): any[] {
	const parentNode: Record<string, any> = {};
	function setParentNode(nodes: any[], parent?: any) {
		if (!nodes) return;
		return nodes.forEach((data) => {
			const node = { value: data.key, parent };
			parentNode[data.key] = node;
			setParentNode(data.values, node);
		});
	}

	function getParentNode(value: string) {
		let node: any[] = [];
		const currentNode = parentNode[value];
		node.push(currentNode.value);
		if (currentNode.parent) {
			node = [...getParentNode(currentNode.parent.value), ...node];
		}
		return node;
	}
	setParentNode(version);
	return getParentNode(commVersion);
}

// 是否 yarn 和 hdfs 组件都存在
export function isSchedulings(initialCompData: any[]): boolean {
	let scheduling = 0;
	initialCompData.forEach((comps) => {
		if (comps.findIndex((comp: any) => isSameVersion(comp.componentTypeCode)) > -1) {
			scheduling += 1;
		}
	});
	return scheduling === 2;
}

/**
 * @param param
 * 处理单条自定义参数的key\value值
 * 处理数据结构为%1532398855125918-key, %1532398855125918-value
 * 返回数据结构[{key: key, value: value, id: id}]
 */
function handleSingleParam(params: any) {
	const tempParamObj: Record<string, { key: string; value: string }> = {};
	const customParamConfig: any[] = [];
	if (!params) return {};
	Object.entries(params).forEach(([keys, values]) => {
		if (values && _.isString(values) && _.isString(keys)) {
			const p = keys.split('%')[1].split('-');
			tempParamObj[p[0]] = {
				...tempParamObj[p[0]],
				[p[1]]: values,
			};
		}
	});
	Object.keys(tempParamObj).forEach((key) => {
		customParamConfig.push({
			key: tempParamObj[key].key,
			value: tempParamObj[key].value,
			id: key,
			type: CONFIG_ITEM_TYPE.CUSTOM_CONTROL,
		});
	});
	return customParamConfig;
}

/**
 * @param param 自定义参数对象
 * @param turnp 转化为{key:value}型数据，仅支持不含group类型组组件
 * 先组内处理自定义参数再处理普通类型的自定义参数
 * 返回数据结构
 * [{
 *  group: {
 *    key: key,
 *    value: value
 *  }
 * }]
 */
export function handleCustomParam(params: any, turnp?: boolean): any {
	let customParam: any = [];
	if (!params) return {};
	Object.entries(params).forEach(([key, value]) => {
		if (value && !_.isString(value)) {
			customParam = [...customParam, { [key]: handleSingleParam(value) }];
		}
	});
	if (turnp) {
		const config: Record<string, any> = {};
		customParam.concat(handleSingleParam(params)).forEach((item: any) => {
			config[item.key] = item.value;
		});
		return config;
	}
	return customParam.concat(handleSingleParam(params));
}

/**
 * @param temp 初始模版值
 * 处理初始模版值返回只包含自定义参数的键值
 * 返回结构如下
 * {
 *   %1532398855125918-key: key,
 *   %1532398855125918-value: value,
 *     group: {
 *        %1532398855125918-key: key,
 *        %1532398855125918-value: value,
 *     }
 * }
 */
export function getParamsByTemp(temp: any[]): any {
	const batchParams: any = {};
	(isDeployMode(temp[0]?.key) ? temp[0].values : temp).forEach((item: any) => {
		if (item.type === CONFIG_ITEM_TYPE.GROUP) {
			const params: Record<string, any> = {};
			item.values.forEach((groupItem: any) => {
				if (groupItem.id) {
					params[`%${groupItem.id}-key`] = groupItem?.key ?? '';
					params[`%${groupItem.id}-value`] = groupItem?.value ?? '';
				}
			});
			batchParams[item.key] = params;
		}
		if (isCustomType(item.type)) {
			batchParams[`%${item.id}-key`] = item?.key ?? '';
			batchParams[`%${item.id}-value`] = item?.value ?? '';
		}
	});
	return batchParams;
}

// 后端需要value值加单引号处理
function handleSingQuoteKeys(val: string, key: string) {
	const singQuoteKeys = ['c.NotebookApp.ip', 'c.NotebookApp.token', 'c.NotebookApp.default_url'];
	let newVal = val;
	singQuoteKeys.forEach((singlekey) => {
		if (singlekey === key && val.indexOf("'") === -1) {
			newVal = `'${val}'`;
		}
	});
	return newVal;
}

/**
 * @param
 * comp 表单组件值
 * initialCompData 初始表单组件值
 * componentTemplate用于表单回显值需要包含表单对应的value和并自定义参数
 */
export function handleComponentTemplate(comp: any, initialCompData: any): any {
	/** 外层数据先删除一层自定义参数 */
	const newComponentTemplate = JSON.parse(initialCompData.componentTemplate).filter(
		(v: any) => v.type !== CONFIG_ITEM_TYPE.CUSTOM_CONTROL,
	);
	const componentConfig = handleComponentConfig(comp);
	const customParamConfig = handleCustomParam(comp.customParam);
	let isGroup = false;

	// componentTemplate 存入 componentConfig 对应值
	Object.entries(componentConfig).forEach(([key, values]) => {
		if (!_.isString(values) && !_.isArray(values)) {
			Object.entries(values as any).forEach(([groupKey, value]) => {
				(isDeployMode(newComponentTemplate[0].key)
					? newComponentTemplate[0].values
					: newComponentTemplate
				).forEach((temps: any) => {
					if (temps.key === key) {
						// eslint-disable-next-line no-param-reassign
						temps.values = temps.values.filter(
							(temp: any) => temp.type !== CONFIG_ITEM_TYPE.CUSTOM_CONTROL,
						);
						temps.values.forEach((temp: any) => {
							if (temp.key === groupKey) {
								// eslint-disable-next-line no-param-reassign
								temp.value = value;
							}
						});
					}
				});
			});
		} else {
			newComponentTemplate.forEach((temps: any) => {
				if (temps.key === key) {
					// eslint-disable-next-line no-param-reassign
					temps.value = values;
				} else if (isRadioLinkage(temps.type)) {
					temps.values.forEach((temp: any) => {
						// eslint-disable-next-line no-param-reassign
						if (temp.values[0].key === key) temp.values[0].value = values;
					});
				}
			});
		}
	});
	if (Object.values(customParamConfig).length === 0) {
		return newComponentTemplate;
	}

	// 和并自定义参数
	customParamConfig.forEach((config: any) => {
		if (!config?.type) {
			isGroup = true;
			Object.entries(config).forEach(([key, value]) => {
				(isDeployMode(newComponentTemplate[0].key)
					? newComponentTemplate[0].values
					: newComponentTemplate
				).forEach((temp: any) => {
					if (temp.key === key && temp.type === CONFIG_ITEM_TYPE.GROUP) {
						// eslint-disable-next-line no-param-reassign
						temp.values = temp.values.concat(value);
					}
				});
			});
		}
	});
	if (!isGroup) return newComponentTemplate.concat(customParamConfig);
	return newComponentTemplate;
}

/**
 * @param comp
 * @param turnp 格式 => 为tue时对应componentConfig格式为{%-key:value}
 * 返回componentConfig
 */
export function handleComponentConfig(comp: any, turnp?: boolean): any {
	// 处理componentConfig
	const componentConfig: Record<string, any> = {};
	function wrapperKey(key: string): string {
		if (turnp) return key.split('.').join('%');
		return key.split('%').join('.');
	}
	Object.entries(comp?.componentConfig ?? {}).forEach(([key, values]) => {
		componentConfig[wrapperKey(key)] = values;
		if (!_.isString(values) && !_.isArray(values)) {
			const groupConfig: Record<string, any> = {};
			Object.entries(values as any).forEach(([groupKey, value]) => {
				const wrapper = wrapperKey(groupKey);
				groupConfig[wrapper] = turnp
					? value
					: handleSingQuoteKeys(value as string, wrapper);
			});
			componentConfig[key] = groupConfig;
		}
	});
	return componentConfig;
}

/**
 * @param comp
 * @param typeCode
 * 返回包含自定义参数的componentConfig
 * typeCode识别是否有组类别
 */
export function handleComponentConfigAndCustom(comp: any, typeCode: number): any {
	// 处理componentConfig
	let componentConfig = handleComponentConfig(comp);
	const isFlinkStandalone = componentConfig?.clusterMode === 'standalone'; // flink 组件含有 standalone 类型，没有 group 包裹

	// 自定义参数和componentConfig和并
	const customParamConfig = handleCustomParam(comp.customParam);
	if (isHaveGroup(typeCode) && !isFlinkStandalone && customParamConfig.length) {
		// eslint-disable-next-line no-restricted-syntax
		for (const config of customParamConfig) {
			// eslint-disable-next-line
			for (const key in config) {
				// eslint-disable-next-line no-restricted-syntax
				for (const groupConfig of config[key]) {
					componentConfig[key] = {
						...componentConfig[key],
						[groupConfig.key]: groupConfig.value,
					};
				}
			}
		}
	}
	if ((!isHaveGroup(typeCode) || isFlinkStandalone) && Object.values(customParamConfig).length) {
		// eslint-disable-next-line no-restricted-syntax
		for (const item of customParamConfig) {
			componentConfig = {
				...componentConfig,
				[item.key]: item.value,
			};
		}
	}
	return componentConfig;
}

export function getSingleTestStatus(
	params: { typeCode: number; versionName?: string },
	value: any,
	testStatus: any,
): any[] {
	const typeCode = params.typeCode ?? '';
	const versionName = params.versionName ?? '';
	const currentStatus = testStatus[String(typeCode)] ?? {};
	let multiVersion = currentStatus?.multiVersion ?? [];

	if (multiVersion.length) {
		let sign = false;
		multiVersion = multiVersion.map((version: any) => {
			if (version?.componentVersion === versionName) {
				sign = true;
				return value;
			}
			return version;
		});
		if (!sign && value) multiVersion.push(value);
	}
	if (!multiVersion.length && value) multiVersion.push(value);
	return multiVersion;
}

export function includesCurrentComp(
	modifyComps: any[],
	params: { typeCode: number; versionName?: string },
): boolean {
	const { typeCode, versionName } = params;
	modifyComps.forEach((comp) => {
		if (comp.typeCode === typeCode && !comp.versionName) return true;
		if (comp.typeCode === typeCode && comp.versionName === versionName) return true;
	});
	return false;
}

export function getCurrentComp(
	initialCompDataArr: any[],
	params: { typeCode: number; versionName?: string },
): any {
	const { typeCode, versionName } = params;
	let currentComp = {};
	// eslint-disable-next-line no-restricted-syntax
	for (const comp of initialCompDataArr) {
		// eslint-disable-next-line no-restricted-syntax
		for (const vcomp of comp?.multiVersion ?? []) {
			if (vcomp?.componentTypeCode === typeCode) {
				if (!versionName && vcomp) currentComp = vcomp;
				if (vcomp?.versionName === versionName) currentComp = vcomp;
			}
		}
	}
	return currentComp;
}

export function getCurrent1Comp(
	initialCompDataArr: any[],
	params: { typeCode: number; versionName?: string },
): any {
	const { typeCode, versionName } = params;
	let currentComp = {};
	// eslint-disable-next-line no-restricted-syntax
	for (const compArr of initialCompDataArr) {
		// eslint-disable-next-line no-restricted-syntax
		for (const comp of compArr) {
			// eslint-disable-next-line no-restricted-syntax
			for (const vcomp of comp?.multiVersion ?? []) {
				if (vcomp?.componentTypeCode === typeCode) {
					if (!versionName && vcomp) currentComp = vcomp;
					if (vcomp?.versionName === versionName) currentComp = vcomp;
				}
			}
		}
	}
	return currentComp;
}

/**
 * @param comp 已渲染组件表单值
 * @param initialComp 组件初始值
 *
 * 通过比对表单值和初始值对比是否变更
 * 返回含有组件code数组
 *
 */
function handleCurrentComp(comp: any, initialComp: any, typeCode: number): boolean {
	/**
	 * 基本参数对比
	 * 文件对比，只比较文件名称
	 */
	for (let index = 0; index < DEFAULT_PARAMS.length; index += 1) {
		const param = DEFAULT_PARAMS[index];
		let compValue = comp[param];
		if (isFileParam(param)) {
			compValue = comp[param]?.name ?? comp[param];
		}
		if (isMetaData(param)) {
			if (comp[param] === true) compValue = 1;
			if (comp[param] === false) compValue = 0;
		}
		if (
			(compValue || compValue === 0) &&
			!_.isEqual(compValue, initialComp[param]?.name ?? initialComp[param])
		) {
			return true;
		}
	}

	/**
	 * 除 hdfs、yarn、kerberos组件
	 * 对比之前先处理一遍表单的数据和自定义参数, 获取含有自定义参数的componentConfig
	 */
	if (!isNeedTemp(Number(typeCode))) {
		const compConfig = handleComponentConfigAndCustom(comp, Number(typeCode));
		if (!Object.values(compConfig).length) return false;
		if (
			!_.isEqual(
				compConfig,
				initialComp?.componentConfig ? JSON.parse(initialComp.componentConfig) : {},
			)
		) {
			return true;
		}
	} else {
		/** 比对 hdfs、yarn 自定义参数 */
		const compTemp = comp.customParam ? handleSingleParam(comp.customParam) : [];
		const initialTemp = getCustomerParams(getValueByJson(initialComp?.componentTemplate) ?? []);
		if (!_.isEqual(compTemp, initialTemp)) {
			return true;
		}
	}
	return false;
}

/**
 * @param comps 已渲染各组件表单值
 * @param initialCompData 各组件初始值
 *
 * 通过比对表单值和初始值对比是否变更
 * 返回含有组件code数组
 *
 */
export function getModifyComp(comps: any, initialCompData: any[]): any {
	const modifyComps = new Set();
	Object.entries(comps).forEach(([typeCode, comp]) => {
		if (isMultiVersion(Number(typeCode))) {
			Object.entries(comp as any).forEach(([versionName, vcomp]) => {
				if (!DEFAULT_PARAMS.includes(versionName)) {
					const initialComp = getCurrent1Comp(initialCompData, {
						typeCode: Number(typeCode),
						versionName,
					});
					if (handleCurrentComp(vcomp, initialComp, Number(typeCode))) {
						modifyComps.add({
							typeCode: Number(typeCode),
							versionName,
						});
					}
				}
			});
		} else {
			const initialComp = getCurrent1Comp(initialCompData, {
				typeCode: Number(typeCode),
			});
			if (handleCurrentComp(comp, initialComp, Number(typeCode))) {
				modifyComps.add({ typeCode: Number(typeCode) });
			}
		}
	});

	return modifyComps;
}

/** 指定引擎的 jdbcUrl 项展示 hover 提示 */
export function showHover(componentTypeValue: number, label: string) {
	return false;
}
