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

import type { FormInstance } from 'antd';
import { Checkbox, Modal, Form } from 'antd';
import { COMPONENT_CONFIG_NAME, MAPPING_DATA_CHECK } from '@/constant';

const { confirm } = Modal;

interface IProps {
	comp: any;
	form: FormInstance;
	view: boolean;
	isCheckBoxs: boolean | undefined;
	disabledMeta: boolean | undefined;
}

export default function DataCheckbox({ comp, form, view, isCheckBoxs, disabledMeta }: IProps) {
	const getCheckValue = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		if (!isCheckBoxs) return true;
		return form.getFieldValue(`${typeCode}.isMetadata`) ?? comp?.isMetadata ?? false;
	};

	const handleChange = (e: any) => {
		const typeCode: keyof typeof COMPONENT_CONFIG_NAME = comp?.componentTypeCode ?? '';
		const showConfirm = () => {
			/**
			 * 勾选后保持勾选之前的状态，使用setState过度平缓，使用setTimeOut会有闪现的效果
			 */
			setTimeout(() => {
				form.setFieldsValue({
					[`${typeCode}.isMetadata`]: !e.target.checked,
				});
			}, 0);
			const source = !e.target.checked
				? COMPONENT_CONFIG_NAME[typeCode]
				: COMPONENT_CONFIG_NAME[
						MAPPING_DATA_CHECK[typeCode as keyof typeof MAPPING_DATA_CHECK]
				  ];
			const target = !e.target.checked
				? COMPONENT_CONFIG_NAME[
						MAPPING_DATA_CHECK[typeCode as keyof typeof MAPPING_DATA_CHECK]
				  ]
				: COMPONENT_CONFIG_NAME[typeCode];
			confirm({
				title: `确认将元数据获取方式由${source}切换为${target}？`,
				onOk: () => {
					form.setFieldsValue({
						[`${
							MAPPING_DATA_CHECK[typeCode as keyof typeof MAPPING_DATA_CHECK]
						}.isMetadata`]: !e.target.checked,
						[`${typeCode}.isMetadata`]: e.target.checked,
					});
				},
			});
		};
		if (isCheckBoxs) {
			showConfirm();
		}
	};

	const validMetadata = (_: any, value: any) => {
		if (!isCheckBoxs && !value) {
			return Promise.reject(new Error('请设置元数据获取方式'));
		}
		return Promise.resolve();
	};

	const typeCode = comp?.componentTypeCode ?? '';
	return (
		<Form.Item
			label={null}
			colon={false}
			name={`${typeCode}.isMetadata`}
			valuePropName="checked"
			initialValue={getCheckValue()}
			rules={[
				{
					validator: validMetadata,
				},
			]}
		>
			<Checkbox disabled={view || disabledMeta} onChange={handleChange}>
				设为元数据获取方式
			</Checkbox>
		</Form.Item>
	);
}
