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

import React, { useMemo, useRef, useState } from 'react';
import { debounce } from 'lodash';
import { DATA_SOURCE_ENUM, DATA_SOURCE_TEXT, formItemLayout, HELP_DOC_URL } from '@/constant';
import {
	isHaveTableColumn,
	isShowSchema,
	isES,
	isCacheOnlyAll,
	isCacheExceptLRU,
	isHaveAsyncPoolSize,
	isHaveCustomParams,
} from '@/utils/is';
import {
	Button,
	Form,
	Input,
	InputNumber,
	message,
	Popconfirm,
	Select,
	Switch,
	Table,
	Tooltip,
} from 'antd';
import { QuestionCircleOutlined, CloseOutlined } from '@ant-design/icons';
import Editor from '@/components/editor';
import { asyncTimeoutNumDoc, queryFault, targetColText } from '@/components/helpDoc/docs';
import { CustomParams } from '../component/customParams';
import DataPreviewModal from '../../source/dataPreviewModal';
import { generateMapValues } from '../customParamsUtil';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IFlinkSideProps } from '@/interface';
import { Utils } from '@dtinsight/dt-utils/lib';
import { createSeries } from '@/utils';
import { isSchemaRequired } from '@/utils/is';

const FormItem = Form.Item;
const { Option } = Select;

const DATA_SOURCE_OPTIONS = [DATA_SOURCE_ENUM.MYSQL];

type IFormFieldProps = IFlinkSideProps;

interface IDimensionFormProps {
	data: Partial<IFormFieldProps>;
	sourceOptions?: IDataSourceUsedInSyncProps[];
	/**
	 * @deprecated 暂时没有支持具有 schema 的数据源
	 */
	schemaOptions?: string[];
	/**
	 * 以 searchkey 为键，搜索结果为 value
	 */
	tableOptions?: Record<string, string[]>;
	columnsOptions?: IDataColumnsProps[];
	isFlink112?: boolean;
	/**
	 * @deprecated 暂时没有支持需要展示 editor 的数据源
	 */
	isShow?: boolean;
	onTableSearch?: (
		type: DATA_SOURCE_ENUM,
		sourceId: number,
		schema?: string | undefined,
		searchKey?: string | undefined,
	) => Promise<void>;
	onValuesChange?: (preVal: Partial<IFormFieldProps>, nextVal: Partial<IFormFieldProps>) => void;
}

/**
 * 表格列操作枚举
 */
enum ColOperatorKind {
	ADD,
	ADD_ALL,
	REMOVE_ALL,
	SET_COL,
	SET_TYPE,
	SET_TARGET,
	REMOVE,
}

export default function DimensionForm({
	data,
	sourceOptions = [],
	schemaOptions = [],
	tableOptions = {},
	columnsOptions = [],
	isFlink112 = true,
	isShow = false,
	onTableSearch,
	onValuesChange,
}: IDimensionFormProps) {
	const [form] = Form.useForm<IFormFieldProps>();
	const [visible, setVisible] = useState(false);
	const [params, setParams] = useState<Record<string, any>>({});
	const currentSearchKey = useRef('');

	const handleSearchTable = debounce((key: string) => {
		currentSearchKey.current = key;
		const { sourceId, schema, type } = form.getFieldsValue();
		onTableSearch?.(type, sourceId, schema, key);
	}, 300);

	const handleValueChanged = () => {
		onValuesChange?.(data, { ...data, ...form.getFieldsValue() });
	};

	const handleColsChanged = (ops: ColOperatorKind, idx?: number, value?: string) => {
		switch (ops) {
			case ColOperatorKind.ADD: {
				const nextCols = data.columns?.concat() || [];
				nextCols.push({});
				onValuesChange?.(data, { ...data, columns: nextCols });
				break;
			}

			case ColOperatorKind.ADD_ALL: {
				const nextCols = columnsOptions.map((col) => ({
					column: col.key,
					type: col.type,
				}));
				onValuesChange?.(data, { ...data, columns: nextCols });
				break;
			}

			case ColOperatorKind.REMOVE_ALL: {
				onValuesChange?.(data, { ...data, columns: [] });
				break;
			}

			case ColOperatorKind.SET_COL: {
				const nextCols = data.columns?.concat() || [];
				if (idx !== undefined && value) {
					nextCols[idx].column = value;
					nextCols[idx].type = columnsOptions.find((c) => c.key === value)?.type;
					onValuesChange?.(data, { ...data, columns: nextCols });
				}
				break;
			}

			case ColOperatorKind.SET_TYPE: {
				const nextCols = data.columns?.concat() || [];
				if (idx !== undefined && value) {
					nextCols[idx].type = value;
					onValuesChange?.(data, { ...data, columns: nextCols });
				}
				break;
			}

			case ColOperatorKind.SET_TARGET: {
				const nextCols = data.columns?.concat() || [];
				if (idx !== undefined && value) {
					nextCols[idx].targetCol = value;
					onValuesChange?.(data, { ...data, columns: nextCols });
				}
				break;
			}

			case ColOperatorKind.REMOVE: {
				const nextCols = data.columns?.concat() || [];
				if (idx !== undefined) {
					nextCols.splice(idx, 1);
					onValuesChange?.(data, { ...data, columns: nextCols });
				}
				break;
			}

			default:
				break;
		}
	};

	const handleCustomParamsChanged = (opType: string, id: string, value: string) => {
		if (opType === 'newCustomParam') {
			const nextParams = data.customParams?.concat() || [];
			nextParams.push({
				id: Utils.generateAKey(),
			});
			onValuesChange?.(data, { ...data, customParams: nextParams });
		} else if (opType === 'deleteCustomParam') {
			const nextParams = data.customParams?.concat() || [];
			const idx = nextParams.findIndex((p) => p.id === id);
			if (idx !== -1) {
				nextParams.splice(idx, 1);
			}
			onValuesChange?.(data, { ...data, customParams: nextParams });
		} else {
			const nextParams = data.customParams?.concat() || [];
			const idx = nextParams.findIndex((p) => p.id === id);
			if (idx !== -1) {
				nextParams[idx][opType as 'key' | 'type'] = value;
			}
			onValuesChange?.(data, { ...data, customParams: nextParams });
		}
	};

	const showPreviewModal = () => {
		const { type, sourceId, index: tableIndex, table, schema } = form.getFieldsValue();
		let nextParams = {};
		switch (type) {
			case DATA_SOURCE_ENUM.ES7: {
				if (!sourceId || !tableIndex) {
					message.error('数据预览需要选择数据源和索引！');
					return;
				}
				nextParams = { sourceId, tableName: tableIndex };
				break;
			}
			case DATA_SOURCE_ENUM.REDIS:
			case DATA_SOURCE_ENUM.UPRedis:
			case DATA_SOURCE_ENUM.HBASE:
			case DATA_SOURCE_ENUM.TBDS_HBASE:
			case DATA_SOURCE_ENUM.HBASE_HUAWEI:
			case DATA_SOURCE_ENUM.MYSQL:
			case DATA_SOURCE_ENUM.UPDRDB:
			case DATA_SOURCE_ENUM.INCEPTOR: {
				if (!sourceId || !table) {
					message.error('数据预览需要选择数据源和表！');
					return;
				}
				nextParams = { sourceId, tableName: table };
				break;
			}
			case DATA_SOURCE_ENUM.ORACLE: {
				if (!sourceId || !table || !schema) {
					message.error('数据预览需要选择数据源、表和schema！');
					return;
				}
				nextParams = { sourceId, tableName: table, schema };
				break;
			}
			case DATA_SOURCE_ENUM.SQLSERVER:
			case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER: {
				if (!sourceId || !table) {
					message.error('数据预览需要选择数据源和表！');
					return;
				}
				nextParams = { sourceId, tableName: table, schema };
				break;
			}
			default:
				break;
		}

		setVisible(true);
		setParams(nextParams);
	};

	const initialValues = useMemo<IFormFieldProps>(() => {
		const { customParams, ...restData } = data;
		return {
			...restData,
			...generateMapValues(customParams),
		};
	}, []);

	return (
		<Form<IFormFieldProps>
			{...formItemLayout}
			form={form}
			initialValues={initialValues}
			onValuesChange={handleValueChanged}
		>
			<FormItem
				label="存储类型"
				name="type"
				rules={[{ required: true, message: '请选择存储类型' }]}
			>
				<Select
					showSearch
					filterOption={(input: any, option: any) =>
						option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
					}
				>
					{DATA_SOURCE_OPTIONS.map((v) => (
						<Option value={v} key={v}>
							{DATA_SOURCE_TEXT[v]}
						</Option>
					))}
				</Select>
			</FormItem>
			<FormItem
				label="数据源"
				name="sourceId"
				rules={[{ required: true, message: '请选择数据源' }]}
			>
				<Select
					placeholder="请选择数据源"
					showSearch
					filterOption={(input: any, option: any) =>
						option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
					}
				>
					{sourceOptions.map((v) => (
						<Option key={v.dataInfoId} value={v.dataInfoId}>
							{v.dataName}
						</Option>
					))}
				</Select>
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					isShowSchema(getFieldValue('type')) && (
						<FormItem
							label="Schema"
							name="schema"
							rules={[
								{
									required: isSchemaRequired(getFieldValue('type')),
									message: '请输入Schema',
								},
							]}
						>
							<Select
								showSearch
								placeholder="请选择Schema"
								allowClear
								filterOption={(input: any, option: any) =>
									option.props.children
										.toLowerCase()
										.indexOf(input.toLowerCase()) >= 0
								}
							>
								{schemaOptions.map((v) => (
									<Option key={v} value={v}>
										{v}
									</Option>
								))}
							</Select>
						</FormItem>
					)
				}
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) => {
					const type: Partial<IFormFieldProps> = getFieldValue('type');
					switch (type) {
						case DATA_SOURCE_ENUM.REDIS:
						case DATA_SOURCE_ENUM.UPRedis: {
							return (
								<FormItem
									label="表"
									name="table"
									rules={[{ required: true, message: '请输入表名' }]}
								>
									<Input placeholder="请输入表名" />
								</FormItem>
							);
						}
						case DATA_SOURCE_ENUM.ES6:
						case DATA_SOURCE_ENUM.ES7: {
							return null;
						}
						default: {
							return (
								<FormItem
									label="表"
									name="table"
									rules={[{ required: true, message: '请选择表' }]}
								>
									<Select
										onSearch={handleSearchTable}
										filterOption={false}
										showSearch
										placeholder="请选择表"
									>
										{(tableOptions[currentSearchKey.current] || []).map((v) => (
											<Option key={v} value={v}>
												{v}
											</Option>
										))}
									</Select>
								</FormItem>
							);
						}
					}
				}}
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					isES(getFieldValue('type')) && (
						<FormItem
							label="索引"
							name="index"
							rules={[{ required: true, message: '请输入索引' }]}
						>
							<Input placeholder="请输入索引" />
						</FormItem>
					)
				}
			</FormItem>
			<FormItem
				wrapperCol={{
					offset: formItemLayout.labelCol.sm.span,
					span: formItemLayout.wrapperCol.sm.span,
				}}
			>
				<Button block type="link" onClick={showPreviewModal}>
					数据预览
				</Button>
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					isES(getFieldValue('type')) &&
					getFieldValue('type') !== DATA_SOURCE_ENUM.ES7 && (
						<FormItem
							label="索引类型"
							name="esType"
							rules={[{ required: true, message: '请输入索引类型' }]}
						>
							<Input placeholder="请输入索引类型" />
						</FormItem>
					)
				}
			</FormItem>
			<FormItem
				label="映射表"
				name="tableName"
				rules={[{ required: true, message: '请输入映射表名' }]}
			>
				<Input placeholder="请输入映射表名" />
			</FormItem>
			<FormItem required label="字段" dependencies={['type']}>
				{({ getFieldValue }) =>
					isHaveTableColumn(getFieldValue('type')) ? (
						<div className="column-container">
							<Table
								rowKey="column"
								dataSource={data.columns}
								pagination={false}
								size="small"
							>
								<Table.Column
									title="字段"
									dataIndex="column"
									key="字段"
									width="35%"
									render={(text, _, index) => {
										return (
											<Select
												value={text}
												style={{ maxWidth: 74 }}
												onChange={(value) =>
													handleColsChanged(
														ColOperatorKind.SET_COL,
														index,
														value,
													)
												}
												showSearch
											>
												{columnsOptions.map((col) => (
													<Option key={col.key} value={col.key}>
														<Tooltip
															placement="topLeft"
															title={col.key}
														>
															{col.key}
														</Tooltip>
													</Option>
												))}
											</Select>
										);
									}}
								/>
								<Table.Column
									title="类型"
									dataIndex="type"
									key="类型"
									width="25%"
									render={(text, _, index) => (
										<span
											className={
												text?.toLowerCase() === 'Not Support'.toLowerCase()
													? 'has-error'
													: ''
											}
										>
											<Tooltip
												title={text}
												trigger="hover"
												placement="topLeft"
											>
												<Input
													value={text}
													onChange={(e) =>
														handleColsChanged(
															ColOperatorKind.SET_TYPE,
															index,
															e.target.value,
														)
													}
												/>
											</Tooltip>
										</span>
									)}
								/>
								<Table.Column
									title={
										<div>
											<Tooltip
												placement="top"
												title={targetColText}
												arrowPointAtCenter
											>
												<span>
													别名 &nbsp;
													<QuestionCircleOutlined />
												</span>
											</Tooltip>
										</div>
									}
									dataIndex="targetCol"
									key="别名"
									width="30%"
									render={(text, _, index) => {
										return (
											<Input
												value={text}
												onChange={(e) =>
													handleColsChanged(
														ColOperatorKind.SET_TARGET,
														index,
														e.target.value,
													)
												}
											/>
										);
									}}
								/>
								<Table.Column
									key="delete"
									render={(_, __, index) => {
										return (
											<CloseOutlined
												style={{
													fontSize: 12,
													color: 'var(--editor-foreground)',
												}}
												onClick={() =>
													handleColsChanged(ColOperatorKind.REMOVE, index)
												}
											/>
										);
									}}
								/>
							</Table>
							<div style={{ padding: '0 20 20' }}>
								<div className="stream-btn column-btn" style={{ borderRadius: 5 }}>
									<span>
										<a onClick={() => handleColsChanged(ColOperatorKind.ADD)}>
											添加输入
										</a>
									</span>
									<span>
										<a
											onClick={() =>
												handleColsChanged(ColOperatorKind.ADD_ALL)
											}
											style={{ marginRight: 12 }}
										>
											导入全部字段
										</a>
										<Popconfirm
											title="确认清空所有字段？"
											onConfirm={() =>
												handleColsChanged(ColOperatorKind.REMOVE_ALL)
											}
											okText="确认"
											cancelText="取消"
										>
											<a>清空</a>
										</Popconfirm>
									</span>
								</div>
							</div>
						</div>
					) : (
						isShow && (
							<Editor
								style={{
									height: '100%',
								}}
								sync
							/>
						)
					)
				}
			</FormItem>
			<FormItem noStyle dependencies={['type', 'columns']}>
				{({ getFieldValue }) => {
					const { type, columns = [] } = getFieldValue('type') as IFormFieldProps;
					switch (type) {
						case DATA_SOURCE_ENUM.KUDU:
						case DATA_SOURCE_ENUM.POSTGRESQL:
						case DATA_SOURCE_ENUM.CLICKHOUSE:
						case DATA_SOURCE_ENUM.ORACLE:
						case DATA_SOURCE_ENUM.POLAR_DB_For_MySQL:
						case DATA_SOURCE_ENUM.MYSQL:
						case DATA_SOURCE_ENUM.UPDRDB:
						case DATA_SOURCE_ENUM.TIDB:
						case DATA_SOURCE_ENUM.IMPALA:
						case DATA_SOURCE_ENUM.INCEPTOR:
						case DATA_SOURCE_ENUM.KINGBASE8:
						case DATA_SOURCE_ENUM.SQLSERVER:
						case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER: {
							return (
								<FormItem label="主键" name="primaryKey">
									<Select
										mode="multiple"
										showSearch
										showArrow
										filterOption={(input: any, option: any) =>
											option.props.children
												.toLowerCase()
												.indexOf(input.toLowerCase()) >= 0
										}
									>
										{columns.map((v) => (
											<Option key={v.column} value={v.column}>
												{v.column}
											</Option>
										)) || []}
									</Select>
								</FormItem>
							);
						}
						case DATA_SOURCE_ENUM.ES6:
						case DATA_SOURCE_ENUM.ES7: {
							return (
								<FormItem name="primaryKey" label="主键">
									<Input placeholder="请输入主键" />
								</FormItem>
							);
						}
						case DATA_SOURCE_ENUM.MONGODB:
						case DATA_SOURCE_ENUM.REDIS:
						case DATA_SOURCE_ENUM.UPRedis: {
							return (
								<FormItem
									label="主键"
									name="primaryKey"
									rules={[{ required: true, message: '请选择主键' }]}
								>
									<Input
										placeholder={
											type === DATA_SOURCE_ENUM.MONGODB
												? '请输入主键'
												: '维表主键，多个字段用英文逗号隔开'
										}
									/>
								</FormItem>
							);
						}
						case DATA_SOURCE_ENUM.HBASE:
						case DATA_SOURCE_ENUM.TBDS_HBASE:
						case DATA_SOURCE_ENUM.HBASE_HUAWEI: {
							return (
								<FormItem
									label="主键"
									tooltip={
										isFlink112 && (
											<React.Fragment>
												Hbase 表主键字段支持类型可参考&nbsp;
												<a
													href={HELP_DOC_URL.HBASE}
													target="_blank"
													rel="noopener noreferrer"
												>
													帮助文档
												</a>
											</React.Fragment>
										)
									}
								>
									<div style={{ display: 'flex' }}>
										<FormItem
											style={{ flex: 1 }}
											name="hbasePrimaryKey"
											rules={[
												{ required: true, message: '请输入主键' },
												isFlink112
													? {
															pattern: /^\w{1,64}$/,
															message:
																'只能由字母，数字和下划线组成，且不超过64个字符',
													  }
													: {},
											]}
										>
											<Input placeholder="请输入主键" />
										</FormItem>
										{isFlink112 && (
											<>
												<span>&nbsp; 类型：</span>
												<FormItem
													style={{ flex: 1 }}
													name="hbasePrimaryKeyType"
													rules={[
														{
															required: true,
															message: '请输入类型',
														},
													]}
												>
													<Input placeholder="请输入类型" />
												</FormItem>
											</>
										)}
									</div>
								</FormItem>
							);
						}
						default:
							return null;
					}
				}}
			</FormItem>
			<FormItem name="parallelism" label="并行度">
				<InputNumber style={{ width: '100%' }} min={1} />
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) => (
					<FormItem
						label="缓存策略"
						name="cache"
						rules={[{ required: true, message: '请选择缓存策略' }]}
					>
						<Select
							placeholder="请选择"
							showSearch
							filterOption={(input: any, option: any) =>
								option.props.children.toLowerCase().indexOf(input.toLowerCase()) >=
								0
							}
						>
							<Option
								key="None"
								value="None"
								disabled={isCacheOnlyAll(getFieldValue('type'))}
							>
								None
							</Option>
							<Option
								key="LRU"
								value="LRU"
								disabled={isCacheExceptLRU(getFieldValue('type'))}
							>
								LRU
							</Option>
							<Option key="ALL" value="ALL">
								ALL
							</Option>
						</Select>
					</FormItem>
				)}
			</FormItem>
			<FormItem noStyle dependencies={['cache']}>
				{({ getFieldValue }) => {
					switch (getFieldValue('cache')) {
						case 'LRU':
							return (
								<>
									<FormItem
										label="缓存大小(行)"
										name="cacheSize"
										rules={[{ required: true, message: '请输入缓存大小' }]}
									>
										<InputNumber style={{ width: '100%' }} min={0} />
									</FormItem>
									<FormItem
										label="缓存超时时间"
										name="cacheTTLMs"
										rules={[{ required: true, message: '请输入缓存超时时间' }]}
									>
										<InputNumber
											style={{ width: '100%' }}
											min={0}
											addonAfter="ms"
										/>
									</FormItem>
								</>
							);

						case 'ALL':
							return (
								<FormItem
									label="缓存超时时间"
									name="cacheTTLMs"
									rules={[{ required: true, message: '请输入缓存超时时间' }]}
								>
									<InputNumber
										style={{ width: '100%' }}
										min={0}
										addonAfter="ms"
									/>
								</FormItem>
							);
						default:
							break;
					}
				}}
			</FormItem>
			<FormItem label="允许错误数据" tooltip={asyncTimeoutNumDoc} name="errorLimit">
				<InputNumber style={{ width: '100%' }} placeholder="默认为无限制" min={0} />
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					getFieldValue('type') === DATA_SOURCE_ENUM.KUDU && (
						<FormItem label="查询容错" tooltip={queryFault} name="isFaultTolerant">
							<Switch />
						</FormItem>
					)
				}
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					isHaveAsyncPoolSize(getFieldValue('type')) && (
						<FormItem name="asyncPoolSize" label="异步线程池">
							<Select>
								{createSeries(20).map((opt) => {
									return (
										<Option key={opt} value={opt}>
											{opt}
										</Option>
									);
								})}
							</Select>
						</FormItem>
					)
				}
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) =>
					isHaveCustomParams(getFieldValue('type')) && (
						<CustomParams
							customParams={data.customParams || []}
							onChange={handleCustomParamsChanged}
						/>
					)
				}
			</FormItem>
			<FormItem noStyle dependencies={['type']}>
				{({ getFieldValue }) => (
					<DataPreviewModal
						visible={visible}
						type={getFieldValue('type')}
						onCancel={() => setVisible(false)}
						params={params}
					/>
				)}
			</FormItem>
		</Form>
	);
}
