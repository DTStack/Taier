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

import { useMemo, useState } from 'react';
import { Popconfirm, Button, message, Modal } from 'antd';
import Api from '@/api';
import { convertToObj } from '@/utils';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import {
	COMPONENT_CONFIG_NAME,
	COMP_ACTION,
	FLINK_DEPLOY_TYPE,
	FLINK_DEPLOY_NAME,
} from '@/constant';
import { CloseCircleOutlined } from '@ant-design/icons';
import {
	handleComponentConfigAndCustom,
	handleComponentConfig,
	isNeedTemp,
	handleCustomParam,
	isMultiVersion,
	isFLink,
	handleComponentTemplate,
} from '../../help';
import type {
	IComponentProps,
	IClusterInfo,
	ISaveComp,
	ITestConnects,
	IHandleConfirm,
	IScheduleComponentComp,
} from '../../interface';
import { useContextForm } from '../../context';

interface IProps {
	comp: IComponentProps | IScheduleComponentComp;
	clusterInfo: IClusterInfo;
	mulitple?: boolean;
	saveComp: ISaveComp;
	handleConfirm?: IHandleConfirm;
	testConnects?: ITestConnects;
}

export default function ToolBar({
	comp: newComp,
	clusterInfo,
	mulitple,
	saveComp,
	testConnects: onTestConnects,
	handleConfirm,
}: IProps) {
	const comp = newComp as IComponentProps;
	const form = useContextForm();
	const [loading, setLoading] = useState(false);

	const onOk = () => {
		const typeCode: COMPONENT_TYPE_VALUE = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		const deployType = comp?.deployType ?? '';

		form.validateFields()
			.then((rawValues) => {
				const values = convertToObj(rawValues);

				/**
				 * componentTemplate yarn等组件直接传自定义参数，其他组件需处理自定义参数和入group中
				 * componentConfig yarn等组件传值specialConfig，合并自定义参数，其他组件需处理自定义参数合并到对应config中
				 */
				let currentComp = values[typeCode];
				if (mulitple && versionName) {
					const versionArr: string[] = versionName.split('.');
					currentComp = versionArr.reduce((pre, cur) => {
						const next = pre;
						return next[cur];
					}, values[typeCode]);
				}

				let componentConfig: string | undefined;
				if (isNeedTemp(typeCode)) {
					componentConfig = JSON.stringify({
						...currentComp?.specialConfig,
						...handleCustomParam(currentComp.customParam, true),
					});
				} else {
					componentConfig = JSON.stringify(
						handleComponentConfigAndCustom(currentComp, typeCode),
					);
				}

				const params = {
					isDefault: currentComp?.isDefault ?? '',
					storeType: currentComp?.storeType ?? '',
					principal: currentComp?.principal ?? '',
					principals: currentComp?.principals ?? [],
					versionName: mulitple ? versionName : currentComp.versionName || '',
					isMetadata: currentComp.isMetadata ? 1 : 0,
					componentTemplate: '',
					componentConfig,
				};

				if (isNeedTemp(typeCode)) {
					params.componentTemplate = !currentComp.customParam
						? '[]'
						: JSON.stringify(handleCustomParam(currentComp.customParam));
				} else {
					params.componentTemplate = JSON.stringify(
						handleComponentTemplate(currentComp, comp),
					);
				}
				/**
				 * TODO LIST
				 * resources2, kerberosFileName 这个两个参数后期可以去掉
				 * 保存组件后不加上组件id，防止出现上传文件后立即点击不能下载的现象，后续交互优化
				 */
				Api.saveComponent({
					...params,
					deployType,
					clusterId: clusterInfo.clusterId,
					componentCode: typeCode,
					clusterName: clusterInfo.clusterName,
					resources1: currentComp?.uploadFileName ?? '',
					resources2: '',
					kerberosFileName: currentComp?.kerberosFileName?.name ?? '',
				}).then((res: { code: number; data: IComponentProps }) => {
					if (res.code === 1) {
						saveComp({
							...params,
							id: res.data.id,
							componentTypeCode: typeCode,
							deployType,
							uploadFileName: currentComp?.uploadFileName ?? '',
							kerberosFileName: currentComp?.kerberosFileName ?? '',
						});
						message.success('保存成功');
					}
				});
			})
			.catch((err) => {
				if (err && Object.keys(err).includes(String(typeCode))) {
					message.error('请检查配置');
				}
			});
	};

	const testConnects = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName;
		const deployType = comp?.deployType ?? '';
		onTestConnects?.({ typeCode, versionName, deployType }, (testLoading: boolean) => {
			setLoading(testLoading);
		});
	};

	const onConfirm = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = isMultiVersion(typeCode) ? comp?.versionName : '';
		const componentConfig = handleComponentConfig(
			{
				componentConfig: comp?.componentConfig ? JSON.parse(comp?.componentConfig) : {},
			},
			true,
		);
		const fieldValue = isMultiVersion(typeCode)
			? { [versionName]: { componentConfig } }
			: { componentConfig };

		form.setFieldsValue({ [typeCode]: fieldValue });
	};

	const showModal = () => {
		Modal.confirm({
			title: '确认要删除组件？',
			content: '此操作执行后不可逆，是否确认将当前组件删除？',
			icon: <CloseCircleOutlined color="#FF5F5C" />,
			okText: '删除',
			okType: 'danger',
			cancelText: '取消',
			onOk: () => {
				handleConfirm?.(COMP_ACTION.DELETE, comp, mulitple);
			},
		});
	};

	const typeCode: keyof typeof COMPONENT_CONFIG_NAME = comp?.componentTypeCode ?? '';
	const versionName = comp?.versionName ?? '';
	const deployType: keyof typeof FLINK_DEPLOY_NAME = comp?.deployType ?? '';
	const defaultText = COMPONENT_CONFIG_NAME[typeCode];

	const text = useMemo(
		() =>
			isFLink(typeCode)
				? FLINK_DEPLOY_NAME[deployType ?? FLINK_DEPLOY_TYPE.YARN]
				: COMPONENT_CONFIG_NAME[typeCode],
		[typeCode, deployType],
	);

	const multipleText = useMemo(() => `${text} ${versionName}`, [text, versionName]);

	if (isMultiVersion(typeCode) && !mulitple) {
		return (
			<div className="c-toolbar__container">
				<Button style={{ marginLeft: 8 }} onClick={showModal}>
					删除{`${defaultText}`}组件
				</Button>
			</div>
		);
	}

	return (
		<div className="c-toolbar__container">
			<Popconfirm
				title="确认取消当前更改？"
				okText="确认"
				cancelText="取消"
				onConfirm={onConfirm}
			>
				<Button>取消</Button>
			</Popconfirm>
			<Button style={{ marginLeft: 8 }} onClick={showModal}>
				{mulitple ? `删除${multipleText}组件` : `删除${defaultText}组件`}
			</Button>
			<Button style={{ marginLeft: 8 }} loading={loading} onClick={testConnects}>
				{mulitple ? `测试${multipleText}连通性` : `测试${defaultText}连通性`}
			</Button>
			<Button style={{ marginLeft: 8 }} type="primary" onClick={onOk}>
				{mulitple ? `保存${multipleText}组件` : `保存${defaultText}组件`}
			</Button>
		</div>
	);
}
