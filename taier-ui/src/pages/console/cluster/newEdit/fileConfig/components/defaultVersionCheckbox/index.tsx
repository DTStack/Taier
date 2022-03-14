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

import { useLayoutEffect } from 'react';
import type { FormInstance } from 'antd';
import { Checkbox, Tooltip, Form } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';

/** 用于设置了默认版本，取消勾选其他默认版本 */
const MAPPING_DEFAULT_VERSION = ['1.10'];

interface IProps {
	comp: any;
	form: FormInstance;
	view: boolean;
	isDefault: boolean | undefined;
}

export default function DefaultVersionCheckbox({ comp, form, view, isDefault }: IProps) {
	const getCheckValue = () => {
		if (isDefault) return true;
		return comp?.isDefault ?? false;
	};

	useLayoutEffect(() => {
		if (isDefault) {
			const typeCode = comp?.componentTypeCode ?? '';
			const versionName = comp?.versionName ?? '';
			form.setFieldsValue({
				[`${typeCode}.${versionName}.isDefault`]: true,
			});
		}
	}, [isDefault]);

	const validDefaultdata = (_: any, value: any) => {
		const error = new Error('请设置默认版本');
		// 只有一个版本时
		if (isDefault) {
			return value ? Promise.resolve() : Promise.reject(error);
		}
		// 有多个版本时，都没选得提示
		let hasTrue = false;
		const typeCode = comp?.componentTypeCode ?? '';
		// eslint-disable-next-line no-restricted-syntax
		for (const v of MAPPING_DEFAULT_VERSION) {
			hasTrue = hasTrue || !!form.getFieldValue(`${typeCode}.${v}.isDefault`);
		}
		// 传 null 或者 undefined 回调都不会继续，没法三元
		if (hasTrue) {
			return Promise.resolve();
		}
		return Promise.reject(error);
	};

	const handleChange = (e: any) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';

		if (!isDefault && e.target.checked) {
			// eslint-disable-next-line no-restricted-syntax
			for (const v of MAPPING_DEFAULT_VERSION) {
				if (v !== versionName) {
					form.setFieldsValue({
						[`${typeCode}.${v}.isDefault`]: !e.target.checked,
					});
				}
			}
		}
		form.setFieldsValue({
			[`${typeCode}.${versionName}.isDefault`]: e.target.checked,
		});
	};

	const typeCode = comp?.componentTypeCode ?? '';
	const versionName = comp?.versionName ?? '';

	return (
		<>
			<Form.Item label={null} colon={false}>
				<Form.Item
					noStyle
					name={`${typeCode}.${versionName}.isDefault`}
					valuePropName="checked"
					initialValue={getCheckValue()}
					rules={[
						{
							validator: validDefaultdata,
						},
					]}
				>
					<Checkbox disabled={view} onChange={handleChange}>
						设置为默认版本
					</Checkbox>
				</Form.Item>
				<Tooltip
					overlayClassName="big-tooltip"
					title="默认版本将用于离线开发的数据同步与实时开发的实时采集"
				>
					<QuestionCircleOutlined style={{ marginLeft: 4 }} />
				</Tooltip>
			</Form.Item>
		</>
	);
}
