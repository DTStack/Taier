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
import { Row, Col, Input, Form } from 'antd';
import {
	getCustomerParams,
	isNeedTemp,
	giveMeAKey,
	getValueByJson,
	isGroupType,
	isMultiVersion,
} from '../../../help';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import { formItemLayout } from '@/constant';
import { memo, useEffect, useState } from 'react';
import './index.scss';

interface IProp {
	typeCode: Valueof<typeof COMPONENT_TYPE_VALUE>;
	form: FormInstance;
	view: boolean;
	template: any;
	hadoopVersion?: string | number;
	comp?: any;
	maxWidth?: number;
	labelCol?: number;
	wrapperCol?: number;
}

const FormItem = Form.Item;

function CustomParams({
	typeCode,
	form,
	view,
	template,
	hadoopVersion,
	comp,
	maxWidth,
	labelCol,
	wrapperCol,
}: IProp) {
	const [customParams, setCustomParams] = useState<any[]>([]);

	// 新增自定义参数
	const addCustomerParams = () => {
		setCustomParams((p) => [...p, {}]);
	};

	// 新增自定义参数
	const deleteCustomerParams = (id: number) => {
		const newCustomParam = customParams.filter((param: any, index: number) => index !== id);
		setCustomParams(newCustomParam);
	};

	const handleCustomParam = (e: any, id: number, type?: string) => {
		const { value } = e.target;
		let formField = `${typeCode}`;
		if (isMultiVersion(typeCode)) formField = `${formField}.${hadoopVersion}`;

		const feildName = isGroupType(template.type)
			? `${formField}.customParam.${template.key}`
			: `${formField}.customParam`;

		const compConfig = getValueByJson(comp?.componentConfig) ?? {};
		const config = form.getFieldValue(`${formField}.specialConfig`) ?? compConfig;
		const keyAndValue = Object.entries(config);

		if (type) {
			const newCustomParam = customParams.map((param: any, index: number) => {
				if (index === id) {
					return { ...param, value };
				}
				return param;
			});
			setCustomParams(newCustomParam);
			return;
		}

		/**
		 * 与已渲染表单值、模版固定参数比较自定义参数是否相同
		 *  yarn、hdfs组件需要比较componentConfig中的key值是否相同
		 */
		let sameAtTemp = -1;
		let sameAtParams = false;

		if (!isNeedTemp(typeCode)) {
			sameAtTemp = (isGroupType(template.type) ? template.values : template)?.findIndex(
				(param: any) => param.key === value,
			);
		} else {
			sameAtTemp = keyAndValue.findIndex(([key]: any[]) => key === value);
		}

		const customParamsValues = form.getFieldValue(feildName) || {};
		// eslint-disable-next-line no-restricted-syntax
		for (const [key, name] of Object.entries(customParamsValues)) {
			if (key.startsWith('%') && key.endsWith('-key') && value === name) {
				sameAtParams = true;
				break;
			}
		}

		const newCustomParam = customParams.map((param: any, index: number) => {
			if (index === id) {
				return {
					...param,
					isSameKey: sameAtParams || sameAtTemp > -1,
					key: value,
				};
			}
			return param;
		});
		setCustomParams(newCustomParam);
	};

	const renderAddCustomParam = () => {
		return (
			<Row>
				<Col span={labelCol ?? formItemLayout.labelCol.sm.span} />
				<Col
					className="m-card"
					style={{ marginBottom: '20px' }}
					span={formItemLayout.wrapperCol.sm.span}
				>
					<a onClick={() => addCustomerParams()}>添加自定义参数</a>
				</Col>
			</Row>
		);
	};

	useEffect(() => {
		setCustomParams(getCustomerParams(isGroupType(template.type) ? template.values : template));
	}, []);

	const groupKey = template.key;

	if (customParams.length === 0) {
		return !view ? renderAddCustomParam() : null;
	}

	return (
		<>
			{customParams &&
				customParams.map((param: any, index: number) => {
					let formField = `${typeCode}`;
					if (isMultiVersion(typeCode)) formField = `${formField}.${hadoopVersion}`;
					const fieldName = groupKey
						? `${formField}.customParam.${groupKey}`
						: `${formField}.customParam`;
					const publicKey = giveMeAKey();
					return (
						<Row key={index}>
							<Col
								span={labelCol ?? formItemLayout.labelCol.sm.span}
								style={{ textAlign: 'right' }}
							>
								<FormItem required>
									<FormItem
										noStyle
										name={`${fieldName}.%${publicKey}-key`}
										rules={[
											{
												required: true,
												message: '请输入参数属性名',
											},
										]}
										initialValue={param.key || ''}
									>
										<Input
											disabled={view}
											style={{
												width: 'calc(100% - 18px)',
												maxWidth: 200,
											}}
											onBlur={(e) => handleCustomParam(e, index)}
										/>
									</FormItem>
									<span
										style={{
											marginLeft: 2,
											marginRight: 6,
										}}
									>
										:
									</span>
								</FormItem>
							</Col>
							<Col span={wrapperCol ?? formItemLayout.wrapperCol.sm.span}>
								<FormItem
									name={`${fieldName}.%${publicKey}-value`}
									rules={[
										{
											required: true,
											message: '请输入参数属性值',
										},
									]}
									initialValue={param.value || ''}
								>
									<Input
										disabled={view}
										style={{
											maxWidth: maxWidth ? 680 : 'unset',
										}}
										onBlur={(e) => handleCustomParam(e, index, 'value')}
									/>
								</FormItem>
							</Col>
							{!view && (
								<a
									className="formItem-right-text"
									onClick={() => deleteCustomerParams(index)}
								>
									删除
								</a>
							)}
							{!view && param.isSameKey && (
								<span className="formItem-right-text">该参数已存在</span>
							)}
						</Row>
					);
				})}
			{!view && renderAddCustomParam()}
		</>
	);
}

export default memo(CustomParams, (pre, next) => {
	return pre.template === next.template;
});
