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
import React from 'react';
import { isArray } from 'lodash';
import { Input, Radio, Select, Checkbox, Tooltip, Row, Col, Form } from 'antd';
import {
	getValueByJson,
	isDeployMode,
	isRadioLinkage,
	isCustomType,
	isMultiVersion,
} from '../help';
import CustomParams from './components/customParams';
import { formItemLayout, COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE } from '@/constant';
import { useContextForm } from '../context';
import type { IComponentProps, ICompTemplate } from '../interface';
import './index.scss';

interface IColProps {
	xs?: { span: number };
	sm?: { span: number };
}

interface IItemLayout {
	labelCol: IColProps;
	wrapperCol: IColProps;
}

interface IProps {
	comp: IComponentProps;
	view: boolean;
	itemLayout?: IItemLayout;
}

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { Option } = Select;
const CheckboxGroup = Checkbox.Group;

export default function FormConfig({ comp, view, itemLayout }: IProps) {
	const form = useContextForm();

	const renderOptoinsType = (temp: ICompTemplate) => {
		switch (temp.type) {
			case CONFIG_ITEM_TYPE.RADIO:
			case CONFIG_ITEM_TYPE.RADIO_LINKAGE:
				return (
					<RadioGroup disabled={view}>
						{temp.values?.map((c) => {
							return (
								<Radio key={c.key} value={c.value}>
									{c.key}
								</Radio>
							);
						})}
					</RadioGroup>
				);
			case CONFIG_ITEM_TYPE.SELECT:
				return (
					<Select disabled={view} style={{ width: 200 }}>
						{temp.values?.map((c) => {
							return (
								<Option key={c.key} value={c.value}>
									{c.key}
								</Option>
							);
						})}
					</Select>
				);
			case CONFIG_ITEM_TYPE.CHECKBOX:
				return (
					<CheckboxGroup disabled={view} className="c-componentConfig__checkboxGroup">
						{temp.values?.map((c) => {
							return (
								<Checkbox key={c.key} value={`${c.value}`}>
									{c.key}
								</Checkbox>
							);
						})}
					</CheckboxGroup>
				);
			case CONFIG_ITEM_TYPE.PASSWORD:
				return (
					<Input.Password
						disabled={view}
						style={{ maxWidth: 680 }}
						visibilityToggle={false}
					/>
				);
			default:
				return <Input disabled={view} style={{ maxWidth: 680 }} />;
		}
	};

	// 渲染单个配置项
	const renderConfigItem = (temp: ICompTemplate, groupKey?: string) => {
		const typeCode: COMPONENT_TYPE_VALUE = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		const layout = itemLayout ?? formItemLayout;
		const initialValue =
			temp.key === 'deploymode' && !isArray(temp.value) ? [temp.value] : temp.value;

		let formField: number | string = typeCode;
		if (isMultiVersion(typeCode)) formField = `${formField}.${versionName}`;

		const fieldName = groupKey
			? `${formField}.componentConfig.${groupKey}`
			: `${formField}.componentConfig`;

		return (
			!isCustomType(temp.type) && (
				<FormItem
					label={
						<Tooltip title={temp.key}>
							<span className="c-formConfig__label">{temp.key}</span>
						</Tooltip>
					}
					key={temp.key}
					required={temp.required}
					{...layout}
				>
					<FormItem
						noStyle
						name={`${fieldName}.${temp.key.split('.').join('%')}`}
						rules={[
							{
								required: temp.required,
								message: `请输入${temp.key}`,
							},
						]}
						initialValue={initialValue}
					>
						{renderOptoinsType(temp)}
					</FormItem>
				</FormItem>
			)
		);
	};

	// 渲染group级别配置项
	const renderGroupConfigItem = (temps: ICompTemplate, notParams?: boolean) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		let formField: number | string = typeCode;
		if (isMultiVersion(typeCode)) {
			formField = `${formField}.${versionName}`;
		}

		const dependencyValue = temps?.dependencyKey
			? form.getFieldValue(`${formField}.componentConfig.${temps?.dependencyKey}`)
			: [];

		if ((dependencyValue || []).includes(temps?.dependencyValue) || !temps?.dependencyValue) {
			if (notParams) {
				return temps.values?.map((temp) => {
					return renderConfigItem(temp);
				});
			}
			return (
				<div className="c-formConfig__group" key={temps.key}>
					<div className="group__title">{temps.key}</div>
					<div className="group__content">
						{temps.values?.map((temp) => {
							return renderConfigItem(temp, temps.key);
						})}
						<CustomParams
							typeCode={typeCode}
							hadoopVersion={versionName}
							view={view}
							template={temps}
							maxWidth={680}
						/>
					</div>
				</div>
			);
		}
	};

	const rendeConfigForm = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		const template: ICompTemplate[] = getValueByJson(comp?.componentTemplate) ?? [];

		return template.map((temps, index) => {
			/**
			 * 根据根结点deploymode判断是否需要读取二级数据
			 * Radio联动类型数据不添加自定义参数
			 */
			if (isDeployMode(temps.key) || isRadioLinkage(temps.type)) {
				const formField = isMultiVersion(typeCode)
					? `${typeCode}.${versionName}`
					: `${typeCode}`;
				// 对应renderConfigItem方法中的FormItem的name
				const fieldName = `${formField}.componentConfig.${temps.key.split('.').join('%')}`;

				return (
					<React.Fragment key={temps.key}>
						{renderConfigItem(temps)}
						<FormItem
							noStyle
							shouldUpdate={(prev, curr) => prev[fieldName] !== curr[fieldName]}
						>
							{() =>
								temps.values?.map((temp) =>
									renderGroupConfigItem(temp, isRadioLinkage(temps.type)),
								)
							}
						</FormItem>
					</React.Fragment>
				);
			}

			if (temps.type === CONFIG_ITEM_TYPE.GROUP) {
				return renderGroupConfigItem(temps);
			}

			return (
				<React.Fragment key={temps.key}>
					{renderConfigItem(temps)}
					{index === template.length - 1 ? (
						<CustomParams
							typeCode={typeCode}
							hadoopVersion={versionName}
							view={view}
							template={template}
							maxWidth={680}
							labelCol={itemLayout?.labelCol?.sm?.span}
							wrapperCol={itemLayout?.wrapperCol?.sm?.span}
						/>
					) : null}
				</React.Fragment>
			);
		});
	};

	const renderYarnOrHdfsConfig = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const template: ICompTemplate[] = getValueByJson(comp?.componentTemplate) ?? [];
		const compConfig = getValueByJson(comp?.componentConfig) ?? {};
		const config = form.getFieldValue(`${typeCode}.specialConfig`) ?? compConfig;
		const keyAndValue: [string, string][] = Object.entries(config);

		return (
			<>
				{keyAndValue.map(([key, value]) => {
					return (
						<Row key={key} className="zipConfig-item">
							<Col
								className="formitem-textname"
								span={formItemLayout.labelCol.sm.span}
							>
								<Tooltip title={key} placement="topRight">
									<span className="form-text-name">{key}</span>
								</Tooltip>
								<span>：</span>
							</Col>
							<Col
								className="formitem-textvalue"
								span={formItemLayout.wrapperCol.sm.span}
							>
								{`${value}`}
							</Col>
						</Row>
					);
				})}
				<FormItem name={`${typeCode}.specialConfig`} initialValue={config || {}} noStyle>
					<span style={{ display: 'none' }} />
				</FormItem>
				<CustomParams
					key={String(template.length)}
					typeCode={typeCode}
					comp={comp}
					view={view}
					template={template}
					labelCol={formItemLayout.labelCol.sm.span}
					wrapperCol={formItemLayout.wrapperCol.sm.span}
				/>
			</>
		);
	};

	const renderComponentsConfig = () => {
		const typeCode = comp?.componentTypeCode ?? '';

		switch (typeCode) {
			case COMPONENT_TYPE_VALUE.YARN:
			case COMPONENT_TYPE_VALUE.HDFS:
				return (
					<FormItem noStyle dependencies={[`${typeCode}.specialConfig`]}>
						{() => renderYarnOrHdfsConfig()}
					</FormItem>
				);
			case COMPONENT_TYPE_VALUE.SFTP:
			case COMPONENT_TYPE_VALUE.FLINK:
			case COMPONENT_TYPE_VALUE.SPARK:
			case COMPONENT_TYPE_VALUE.HIVE_SERVER:
			case COMPONENT_TYPE_VALUE.SPARK_THRIFT: {
				return rendeConfigForm();
			}
			default:
				return null;
		}
	};

	return <div className="c-formConfig__container">{renderComponentsConfig()}</div>;
}
