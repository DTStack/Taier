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
import React, { useMemo } from 'react';
import { isArray } from 'lodash';
import type { FormInstance } from 'antd';
import { Input, Radio, Select, Checkbox, Tooltip, Row, Col, Form } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import {
	getValueByJson,
	isDeployMode,
	isRadioLinkage,
	isCustomType,
	isMultiVersion,
	isDtscriptAgent,
	showHover,
} from '../help';
import CustomParams from './components/customParams';
import NodeLabel from './components/nodeLabel';
import { formItemLayout, COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE } from '@/constant';
import './index.scss';

const HOVER_TEXT: Record<number, string> = {
	[COMPONENT_TYPE_VALUE.MYSQL]: '示例：jdbc:mysql://localhost:3306/def',
	[COMPONENT_TYPE_VALUE.DB2]: '示例：jdbc:db2://localhost:60000/def',
	[COMPONENT_TYPE_VALUE.OCEANBASE]: '示例：jdbc:mysql://localhost:2881',
	[COMPONENT_TYPE_VALUE.SQLSERVER]:
		'示例：jdbc:jtds:sqlserver://172.16.101.246:1433;databaseName=db_dev',
};

interface IProps {
	comp: any;
	form: FormInstance;
	view: boolean;
	clusterInfo?: any;
	itemLayout?: any;
}

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { Option } = Select;
const CheckboxGroup = Checkbox.Group;

export default function FormConfig({ comp, form, view, clusterInfo, itemLayout }: IProps) {
	const renderOptoinsType = (temp: any) => {
		switch (temp.type) {
			case CONFIG_ITEM_TYPE.RADIO:
			case CONFIG_ITEM_TYPE.RADIO_LINKAGE:
				return (
					<RadioGroup disabled={view}>
						{temp.values.map((c: any) => {
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
						{temp.values.map((c: any) => {
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
						{temp.values.map((c: any) => {
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
	const renderConfigItem = (temp: any, groupKey?: string) => {
		const typeCode: Valueof<typeof COMPONENT_TYPE_VALUE> = comp?.componentTypeCode ?? '';
		const hadoopVersion = comp?.hadoopVersion ?? '';
		const layout = itemLayout ?? formItemLayout;
		const initialValue =
			temp.key === 'deploymode' && !isArray(temp.value) ? temp.value.split() : temp.value;

		let formField: number | string = typeCode;
		if (isMultiVersion(typeCode)) formField = `${formField}.${hadoopVersion}`;

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
					{isDtscriptAgent(typeCode) && (
						<NodeLabel form={form} view={view} clusterInfo={clusterInfo} />
					)}
					{showHover(typeCode, temp.key) && (
						<Tooltip title={HOVER_TEXT[typeCode]}>
							<QuestionCircleOutlined
								style={{
									fontSize: '16px',
									position: 'absolute',
									top: 0,
									right: '-24px',
								}}
							/>
						</Tooltip>
					)}
				</FormItem>
			)
		);
	};

	// 渲染group级别配置项
	const renderGroupConfigItem = (temps: any, notParams?: boolean) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const hadoopVersion = comp?.hadoopVersion ?? '';
		let formField = typeCode;
		if (isMultiVersion(typeCode)) {
			formField = `${formField}.${hadoopVersion}`;
		}

		const dependencyValue = temps?.dependencyKey
			? form.getFieldValue(`${formField}.componentConfig.${temps?.dependencyKey}`)
			: [];

		if ((dependencyValue || []).includes(temps?.dependencyValue) || !temps?.dependencyValue) {
			if (notParams) {
				return temps.values.map((temp: any) => {
					return renderConfigItem(temp);
				});
			}
			return (
				<div className="c-formConfig__group" key={temps.key}>
					<div className="group__title">{temps.key}</div>
					<div className="group__content">
						{temps.values.map((temp: any) => {
							return renderConfigItem(temp, temps.key);
						})}
						<CustomParams
							typeCode={typeCode}
							hadoopVersion={hadoopVersion}
							form={form}
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
		const hadoopVersion = comp?.hadoopVersion ?? '';
		const template = getValueByJson(comp?.componentTemplate) ?? [];

		return template.map((temps: any, index: number) => {
			/**
			 * 根据根结点deploymode判断是否需要读取二级数据
			 * Radio联动类型数据不添加自定义参数
			 */
			if (isDeployMode(temps.key) || isRadioLinkage(temps.type)) {
				return (
					<React.Fragment key={temps.key}>
						{renderConfigItem(temps)}
						{temps.values.map((temp: any) =>
							renderGroupConfigItem(temp, isRadioLinkage(temps.type)),
						)}
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
							hadoopVersion={hadoopVersion}
							form={form}
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

	const renderKubernetsConfig = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const config =
			form.getFieldValue(`${typeCode}.specialConfig`) ?? comp?.componentConfig ?? '';

		return (
			<>
				{config ? (
					<div className="c-formConfig__kubernetsContent">
						配置文件参数已被加密，此处不予显示
					</div>
				) : null}
				<FormItem name={`${typeCode}.specialConfig`} initialValue={config || {}} noStyle>
					<></>
				</FormItem>
			</>
		);
	};

	const renderYarnOrHdfsConfig = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const template = getValueByJson(comp?.componentTemplate) ?? [];
		const compConfig = getValueByJson(comp?.componentConfig) ?? {};
		const config = form.getFieldValue(`${typeCode}.specialConfig`) ?? compConfig;
		const keyAndValue = Object.entries(config);

		return (
			<>
				{keyAndValue.map(([key, value]: any[]) => {
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
					form={form}
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
				return renderYarnOrHdfsConfig();
			case COMPONENT_TYPE_VALUE.KUBERNETES:
				return renderKubernetsConfig();
			case COMPONENT_TYPE_VALUE.MYSQL:
			case COMPONENT_TYPE_VALUE.SQLSERVER:
			case COMPONENT_TYPE_VALUE.DB2:
			case COMPONENT_TYPE_VALUE.OCEANBASE:
			case COMPONENT_TYPE_VALUE.SFTP:
			case COMPONENT_TYPE_VALUE.TIDB_SQL:
			case COMPONENT_TYPE_VALUE.LIBRA_SQL:
			case COMPONENT_TYPE_VALUE.ORACLE_SQL:
			case COMPONENT_TYPE_VALUE.IMPALA_SQL:
			case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
			case COMPONENT_TYPE_VALUE.PRESTO_SQL:
			case COMPONENT_TYPE_VALUE.FLINK:
			case COMPONENT_TYPE_VALUE.SPARK:
			case COMPONENT_TYPE_VALUE.DTYARNSHELL:
			case COMPONENT_TYPE_VALUE.LEARNING:
			case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
			case COMPONENT_TYPE_VALUE.NFS:
			case COMPONENT_TYPE_VALUE.HIVE_SERVER:
			case COMPONENT_TYPE_VALUE.DTSCRIPT_AGENT:
			case COMPONENT_TYPE_VALUE.INCEPTOR_SQL:
			case COMPONENT_TYPE_VALUE.ANALYTIC_DB:
			case COMPONENT_TYPE_VALUE.FLINK_ON_STANDALONE: {
				return rendeConfigForm();
			}
			default:
				return null;
		}
	};

	const typeCode = comp?.componentTypeCode ?? '';
	const className = useMemo(
		() => `c-formConfig__container ${isDtscriptAgent(typeCode) ? 'c-formConfig__full' : ''}`,
		[typeCode],
	);

	return <div className={className}>{renderComponentsConfig()}</div>;
}
