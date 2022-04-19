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

import {
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	DATA_SOURCE_VERSION,
	defaultColsText,
	DEFAULT_MAPPING_TEXT,
	FLINK_VERSIONS,
	formItemLayout,
	hbaseColsText,
	hbaseColsText112,
	HELP_DOC_URL,
	KAFKA_DATA_LIST,
	KAFKA_DATA_TYPE,
} from '@/constant';
import { isRDB } from '@/utils';
import {
	haveCollection,
	haveDataPreview,
	haveParallelism,
	havePrimaryKey,
	haveTableColumn,
	haveTableList,
	haveTopic,
	haveUpdateMode,
	haveUpdateStrategy,
	haveUpsert,
	isAvro,
	isES,
	isHbase,
	isKafka,
	isSqlServer,
	showBucket,
	isShowSchema,
	isRedis,
} from '@/utils/enums';
import {
	Button,
	Checkbox,
	Form,
	Input,
	InputNumber,
	message,
	Popconfirm,
	Radio,
	Select,
	Table,
	Tooltip,
} from 'antd';
import { CloseOutlined, UpOutlined, DownOutlined } from '@ant-design/icons';
import Column from 'antd/lib/table/Column';
import { debounce, isUndefined } from 'lodash';
import React, { useMemo, useRef, useState } from 'react';
import { generateMapValues, getColumnsByColumnsText } from '../customParamsUtil';
import { CustomParams } from '../component/customParams';
import DataPreviewModal from '../../source/dataPreviewModal';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IFlinkSinkProps } from '@/interface';
import Editor from '@/components/editor';
import { Utils } from '@dtinsight/dt-utils/lib';
import classNames from 'classnames';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 默认可选择的数据源
 */
const DATA_SOURCE_OPTIONS = [
	DATA_SOURCE_ENUM.HBASE,
	DATA_SOURCE_ENUM.ES6,
	DATA_SOURCE_ENUM.ES7,
	DATA_SOURCE_ENUM.MYSQL,
];

interface IResultProps {
	isShow: boolean;
	sync: boolean;
	/**
	 * 数据源下拉菜单
	 */
	dataSourceOptionList: IDataSourceUsedInSyncProps[];
	tableOptionType: Record<string, string[]>;
	/**
	 * 表下拉菜单
	 */
	tableColumnOptionType: IDataColumnsProps[];
	/**
	 * topic 下拉菜单
	 */
	topicOptionType: string[];
	data: Partial<IFlinkSinkProps>;
	/**
	 * 当前 flink 版本
	 */
	componentVersion: string;
	getTableType: (
		params: { sourceId: number; type: DATA_SOURCE_ENUM; schema?: string },
		searchKey?: string,
	) => void;
	onFormValuesChanged: (
		prevData: Partial<IFlinkSinkProps>,
		nextValues: Partial<IFlinkSinkProps>,
	) => void;
	textChange: () => void;
}

enum COLUMNS_OPERATORS {
	/**
	 * 导入一条字段
	 */
	ADD_ONE_LINE,
	/**
	 * 导入全部字段
	 */
	ADD_ALL_LINES,
	/**
	 * 删除全部字段
	 */
	DELETE_ALL_LINES,
	/**
	 * 删除一条字段
	 */
	DELETE_ONE_LINE,
	/**
	 * 编辑字段
	 */
	CHANGE_ONE_LINE,
}

/**
 * 是否需要禁用更新模式
 */
const isDisabledUpdateMode = (
	type: DATA_SOURCE_ENUM,
	isHiveTable?: boolean,
	version?: string,
): boolean => {
	if (type === DATA_SOURCE_ENUM.IMPALA) {
		if (isUndefined(isHiveTable) || isHiveTable === true) {
			return true;
		}
		if (isHiveTable === false) {
			return false;
		}

		return false;
	}

	return !haveUpsert(type, version);
};

/**
 * 根据 type 渲染不同模式的 Option 组件
 */
const originOption = (type: string, arrData: any[]) => {
	switch (type) {
		case 'currencyType':
			return arrData.map((v) => {
				return (
					<Option key={v} value={`${v}`}>
						<Tooltip placement="topLeft" title={v}>
							<span className="panel-tooltip">{v}</span>
						</Tooltip>
					</Option>
				);
			});
		case 'columnType':
			return arrData.map((v) => {
				return (
					<Option key={v.key} value={`${v.key}`}>
						<Tooltip placement="topLeft" title={v.key}>
							<span className="panel-tooltip">{v.key}</span>
						</Tooltip>
					</Option>
				);
			});
		case 'primaryType':
			return arrData.map((v) => {
				return (
					<Option key={v.column} value={`${v.column}`}>
						{v.column}
					</Option>
				);
			});
		case 'kafkaPrimaryType':
			return arrData.map((v) => {
				return (
					<Option key={v.field} value={`${v.field}`}>
						{v.field}
					</Option>
				);
			});
		default:
			return null;
	}
};

export default function ResultForm({
	isShow,
	sync,
	dataSourceOptionList,
	tableOptionType,
	tableColumnOptionType,
	topicOptionType,
	data,
	componentVersion,
	textChange,
	getTableType,
	onFormValuesChanged,
}: IResultProps) {
	const [form] = Form.useForm();
	const [visible, setVisible] = useState(false);
	const [params, setParams] = useState<Record<string, any>>({});
	const [showAdvancedParams, setShowAdvancedParams] = useState(false);
	const searchKey = useRef('');

	// 表远程搜索
	const debounceHandleTableSearch = debounce(
		(value: string, currentData: Partial<IFlinkSinkProps>) => {
			if (currentData.sourceId) {
				searchKey.current = value;
				getTableType(
					{
						sourceId: currentData.sourceId,
						type: currentData.type!,
						schema: currentData.schema,
					},
					value,
				);
			}
		},
		300,
	);

	const debounceEditorChange = debounce(
		(value: string) => {
			textChange();
			handleFormFieldChanged({ columnsText: value }, form.getFieldsValue());
		},
		300,
		{ maxWait: 2000 },
	);

	const getPlaceholder = (sourceType: DATA_SOURCE_ENUM) => {
		if (isHbase(sourceType)) {
			return componentVersion === FLINK_VERSIONS.FLINK_1_12
				? hbaseColsText112
				: hbaseColsText;
		}
		return defaultColsText;
	};

	const showPreviewModal = () => {
		const { sourceId, index: tableIndex, table, type, schema } = data;
		let nextParams: Record<string, any> = {};

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
			case DATA_SOURCE_ENUM.HIVE:
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

	const renderTableOptions = () => {
		return (tableOptionType[searchKey.current] || []).map((v) => (
			<Option key={v} value={`${v}`}>
				<Tooltip placement="topLeft" title={v}>
					<span className="panel-tooltip">{v}</span>
				</Tooltip>
			</Option>
		));
	};

	/**
	 * 监听表单数据修改，联动修改表单的值
	 */
	const handleFormFieldChanged = (changed: Partial<IFlinkSinkProps>, values: IFlinkSinkProps) => {
		const fields = Object.keys(changed);
		const allFormFields = Object.keys(values) as (keyof IFlinkSinkProps)[];
		if (fields.includes('type')) {
			const resetValues = allFormFields.reduce((pre, cur) => {
				const next = pre;
				if (cur !== 'type') {
					if (cur === 'parallelism') {
						next[cur] = 1;
					}
					if (cur === 'batchWaitInterval') {
						next[cur] = isRDB(changed.type) ? 1000 : undefined;
					}
					if (cur === 'batchSize') {
						next[cur] = isRDB(changed.type) ? 100 : undefined;
					}
					if (cur === 'columns') {
						next[cur] = [];
					}
					if (cur === 'updateMode') {
						next[cur] = 'append';
					}
					if (cur === 'allReplace') {
						next[cur] = 'false';
					}
					if (cur === 'bulkFlushMaxActions') {
						next[cur] = 100;
					}
					next[cur] = undefined;
				}

				return next;
			}, {} as Partial<IFlinkSinkProps>);

			if (isKafka(changed.type)) {
				if (changed.type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
					resetValues.sinkDataType = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
				} else {
					resetValues.sinkDataType = 'dt_nest';
				}
			}

			form.setFieldsValue(resetValues);
			onFormValuesChanged(data, { ...values, ...resetValues });
			return;
		}

		if (fields.includes('sourceId')) {
			const resetValues = allFormFields.reduce((pre, cur) => {
				const next = pre;
				if (
					![
						'createType',
						'type',
						'sourceId',
						'customParams',
						'batchWaitInterval',
						'batchSize',
					].includes(cur)
				) {
					if (cur === 'columns') {
						next[cur] = [];
					} else if (cur === 'topic') {
						next[cur] = '';
					} else if (cur === 'parallelism') {
						next[cur] = 1;
					} else if (cur === 'updateMode') {
						next[cur] = 'append';
					} else if (cur === 'allReplace') {
						next[cur] = 'false';
					} else if (cur === 'sinkDataType' && isKafka(values.type)) {
						if (values.type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
							next[cur] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
						} else {
							next[cur] = 'dt_nest';
						}
					} else if (cur === 'bulkFlushMaxActions') {
						next[cur] = 100;
					} else {
						next[cur] = undefined;
					}
				}
				return next;
			}, {} as Partial<IFlinkSinkProps>);

			form.setFieldsValue(resetValues);
			onFormValuesChanged(data, { ...values, ...resetValues });
			return;
		}

		if (fields.includes('schema')) {
			const resetValues = allFormFields.reduce((pre, cur) => {
				const next = pre;
				if (
					![
						'createType',
						'type',
						'sourceId',
						'schema',
						'customParams',
						'batchWaitInterval',
						'batchSize',
					].includes(cur)
				) {
					if (cur === 'columns') {
						next[cur] = [];
					} else if (cur === 'topic') {
						next[cur] = '';
					} else if (cur === 'parallelism') {
						next[cur] = 1;
					} else if (cur === 'updateMode') {
						next[cur] = 'append';
					} else if (cur === 'allReplace') {
						next[cur] = 'false';
					} else if (cur === 'bulkFlushMaxActions') {
						next[cur] = 100;
					} else {
						next[cur] = undefined;
					}
				}
				return next;
			}, {} as Partial<IFlinkSinkProps>);

			form.setFieldsValue(resetValues);
			onFormValuesChanged(data, { ...values, ...resetValues });
			return;
		}

		if (fields.includes('table') || fields.includes('collection')) {
			const resetValues = allFormFields.reduce((pre, cur) => {
				const skip = [
					'createType',
					'type',
					'sourceId',
					'schema',
					'table',
					'customParams',
					'batchWaitInterval',
					'batchSize',
				];

				// 修改 collection 后不清空 collection
				if (fields.includes('collection')) {
					skip.push('collection');
				}

				const next = pre;

				if (!skip.includes(cur)) {
					if (cur === 'columns') {
						next[cur] = [];
					} else if (cur === 'parallelism') {
						next[cur] = 1;
					} else if (cur === 'updateMode') {
						next[cur] = 'append';
					} else if (cur === 'allReplace') {
						next[cur] = 'false';
					} else if (cur === 'bulkFlushMaxActions') {
						next[cur] = 100;
					} else {
						next[cur] = undefined;
					}
				}

				return next;
			}, {} as Partial<IFlinkSinkProps>);

			resetValues.columns = [];
			form.setFieldsValue(resetValues);
			onFormValuesChanged(data, { ...values, ...resetValues });
			return;
		}

		if (fields.includes('sinkDataType')) {
			if (!isAvro(changed.sinkDataType)) {
				const resetValues = {
					schemaInfo: undefined,
				};

				form.setFieldsValue(resetValues);
				return;
			}
		}

		if (fields.includes('columnsText')) {
			const resetValues = {
				partitionKeys: undefined,
			};

			form.setFieldsValue(resetValues);
			onFormValuesChanged(data, { ...values, ...resetValues });
			return;
		}

		onFormValuesChanged(data, values);
	};

	const handleColumnsChanged = (
		ops: COLUMNS_OPERATORS,
		index?: number,
		value?: Partial<{
			type: string;
			column: string | number;
		}>,
	) => {
		switch (ops) {
			case COLUMNS_OPERATORS.ADD_ALL_LINES: {
				const columns = tableColumnOptionType.map((column) => {
					return {
						column: column.key,
						type: column.type,
					};
				});
				onFormValuesChanged(data, { ...data, columns });
				break;
			}

			case COLUMNS_OPERATORS.DELETE_ALL_LINES: {
				onFormValuesChanged(data, { ...data, columns: [], primaryKey: [] });
				break;
			}

			case COLUMNS_OPERATORS.ADD_ONE_LINE: {
				const nextCols = (data.columns || []).concat();
				nextCols.push({});
				onFormValuesChanged(data, { ...data, columns: nextCols });
				break;
			}

			case COLUMNS_OPERATORS.DELETE_ONE_LINE: {
				const nextCols = (data.columns || []).concat();
				const deletedCol = nextCols.splice(index!, 1);
				// 删除一条字段的副作用是若该行是 primrayKey 则删除
				if (
					Array.isArray(data.primaryKey) &&
					data.primaryKey.findIndex((key) => key === deletedCol[0].column) !== -1
				) {
					const idx = data.primaryKey.findIndex((key) => key === deletedCol[0].column);
					data.primaryKey.splice(idx, 1);
				}
				onFormValuesChanged(data, { ...data, columns: nextCols });
				break;
			}

			case COLUMNS_OPERATORS.CHANGE_ONE_LINE: {
				const nextCols = (data.columns || []).concat();
				nextCols[index!] = value!;
				onFormValuesChanged(data, { ...data, columns: nextCols });
				break;
			}

			default:
				break;
		}
	};

	/**
	 * 自定义参数修改
	 * @param opType 操作类型,区分是新增还是删除还是修改, 如果是 key 或者 type 表示修改
	 * @param id
	 * @param value
	 */
	const handleCustomParamsChanged = (opType: string, id: string, value: string) => {
		if (opType === 'newCustomParam') {
			const nextParams = data.customParams?.concat() || [];
			nextParams.push({
				id: Utils.generateAKey(),
			});
			handleFormFieldChanged(
				{ customParams: nextParams },
				{ ...form.getFieldsValue(), customParams: nextParams },
			);
		} else if (opType === 'deleteCustomParam') {
			const nextParams = data.customParams?.concat() || [];
			const idx = nextParams.findIndex((p) => p.id === id);
			if (idx !== -1) {
				nextParams.splice(idx, 1);
			}
			handleFormFieldChanged(
				{ customParams: nextParams },
				{ ...form.getFieldsValue(), customParams: nextParams },
			);
		} else {
			const nextParams = data.customParams?.concat() || [];
			const idx = nextParams.findIndex((p) => p.id === id);
			if (idx !== -1) {
				nextParams[idx][opType as 'key' | 'type'] = value;
			}
			handleFormFieldChanged(
				{ customParams: nextParams },
				{ ...form.getFieldsValue(), customParams: nextParams },
			);
		}
	};

	const initialValues = useMemo(() => {
		const { customParams, ...restData } = data;
		return {
			...restData,
			...generateMapValues(customParams),
		};
	}, []);

	const topicOptionTypes = originOption('currencyType', topicOptionType);
	const tableColumnOptionTypes = originOption('columnType', tableColumnOptionType);

	// isShowPartition 为 false 为 kudu 表
	const disableUpdateMode = false;

	const schemaRequired = [
		DATA_SOURCE_ENUM.POSTGRESQL,
		DATA_SOURCE_ENUM.KINGBASE8,
		DATA_SOURCE_ENUM.SQLSERVER,
		DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
	].includes(data.type!);

	const isFlink112 = useMemo(
		() => componentVersion === FLINK_VERSIONS.FLINK_1_12,
		[componentVersion],
	);

	const primaryKeyOptionTypes = useMemo(
		() =>
			isFlink112 && isKafka(data?.type)
				? originOption('kafkaPrimaryType', getColumnsByColumnsText(data?.columnsText))
				: originOption('primaryType', data.columns || []),
		[isFlink112, data],
	);

	return (
		<div className="title-content">
			<Form<IFlinkSinkProps>
				{...formItemLayout}
				form={form}
				initialValues={initialValues}
				onValuesChange={handleFormFieldChanged}
			>
				<FormItem
					label="存储类型"
					name="type"
					rules={[{ required: true, message: '请选择存储类型' }]}
				>
					<Select
						className="right-select"
						showSearch
						style={{ width: '100%' }}
						filterOption={(input, option) =>
							option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
						}
					>
						{DATA_SOURCE_OPTIONS.map((item) => (
							<Option key={item} value={item}>
								{DATA_SOURCE_TEXT[item]}
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
						showSearch
						placeholder="请选择数据源"
						className="right-select"
						filterOption={(input, option) =>
							option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
						}
					>
						{dataSourceOptionList.map((v) => (
							<Option key={v.dataInfoId} value={v.dataInfoId}>
								{v.dataName}
								{DATA_SOURCE_VERSION[v.dataTypeCode] &&
									` (${DATA_SOURCE_VERSION[v.dataTypeCode]})`}
							</Option>
						))}
					</Select>
				</FormItem>
				<FormItem noStyle dependencies={['type']}>
					{({ getFieldValue }) => (
						<>
							{haveCollection(getFieldValue('type')) && (
								<FormItem
									label="Collection"
									name="collection"
									rules={[{ required: true, message: '请选择Collection' }]}
								>
									<Select
										showSearch
										allowClear
										placeholder="请选择Collection"
										className="right-select"
										filterOption={(input, option) =>
											option?.props.children
												.toLowerCase()
												.indexOf(input.toLowerCase()) >= 0
										}
									>
										{renderTableOptions()}
									</Select>
								</FormItem>
							)}
							{showBucket(getFieldValue('type')) && (
								<>
									<FormItem
										label="Bucket"
										name="bucket"
										rules={[
											{
												required: Boolean(schemaRequired),
												message: '请选择Bucket',
											},
										]}
									>
										<Select
											showSearch
											allowClear
											placeholder="请选择Bucket"
											className="right-select"
										>
											{renderTableOptions()}
										</Select>
									</FormItem>
									<FormItem
										label="ObjectName"
										name="objectName"
										rules={[{ required: true, message: '请输入ObjectName' }]}
										tooltip="默认以标准存储，txt格式保存至S3 Bucket内"
									>
										<Input
											placeholder="请输入ObjectName"
											style={{ width: '90%' }}
										/>
									</FormItem>
								</>
							)}
							{isShowSchema(getFieldValue('type')) && (
								<FormItem
									label="Schema"
									name="schema"
									rules={[
										{
											required: Boolean(schemaRequired),
											message: '请输入Schema',
										},
									]}
								>
									<Select
										showSearch
										allowClear
										placeholder="请选择Schema"
										className="right-select"
										options={[]}
									/>
								</FormItem>
							)}
							{haveTopic(getFieldValue('type')) && (
								<FormItem
									label="Topic"
									name="topic"
									rules={[{ required: true, message: '请选择Topic' }]}
								>
									<Select
										placeholder="请选择Topic"
										className="right-select"
										showSearch
									>
										{topicOptionTypes}
									</Select>
								</FormItem>
							)}
							{haveTableList(getFieldValue('type')) &&
								![DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(
									getFieldValue('type'),
								) && (
									<FormItem
										label="表"
										name="table"
										rules={[{ required: true, message: '请选择表' }]}
									>
										<Select
											showSearch
											className="right-select"
											onSearch={(value: string) =>
												debounceHandleTableSearch(value, data)
											}
											filterOption={false}
										>
											{renderTableOptions()}
										</Select>
									</FormItem>
								)}
							{isRedis(getFieldValue('type')) && (
								<FormItem
									label="表"
									name="table"
									rules={[{ required: true, message: '请输入表名' }]}
								>
									<Input />
								</FormItem>
							)}
							{isES(getFieldValue('type')) && (
								<FormItem
									label="索引"
									tooltip={
										<span>
											{
												'支持输入{column_name}作为动态索引，动态索引需包含在映射表字段中，更多请参考'
											}
											<a
												rel="noopener noreferrer"
												target="_blank"
												href={HELP_DOC_URL.INDEX}
											>
												帮助文档
											</a>
										</span>
									}
									name="index"
									rules={[{ required: true, message: '请输入索引' }]}
								>
									<Input placeholder="请输入索引" />
								</FormItem>
							)}
							{haveDataPreview(getFieldValue('type')) && (
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
							)}
							{isRedis(getFieldValue('type')) && (
								<FormItem
									label="主键"
									name="primaryKey"
									rules={[{ required: true, message: '请输入主键' }]}
								>
									<Input placeholder="结果表主键，多个字段用英文逗号隔开" />
								</FormItem>
							)}
							{isES(getFieldValue('type')) && (
								<FormItem
									label="id"
									tooltip="id生成规则：填写字段的索引位置（从0开始）"
									name="esId"
								>
									<Input placeholder="请输入id" />
								</FormItem>
							)}
							{[DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6].includes(
								getFieldValue('type'),
							) && (
								<FormItem
									label="索引类型"
									name="esType"
									rules={[{ required: true, message: '请输入索引类型' }]}
								>
									<Input placeholder="请输入索引类型" />
								</FormItem>
							)}
							{getFieldValue('type') === DATA_SOURCE_ENUM.HBASE && (
								<FormItem
									label="rowKey"
									tooltip={
										isFlink112 ? (
											<>
												Hbase 表 rowkey 字段支持类型可参考&nbsp;
												<a
													href={HELP_DOC_URL.HBASE}
													target="_blank"
													rel="noopener noreferrer"
												>
													帮助文档
												</a>
											</>
										) : (
											"支持拼接规则：md5(fieldA+fieldB) + fieldC + '常量字符'"
										)
									}
								>
									<div style={{ display: 'flex' }}>
										<FormItem
											style={{ flex: 1 }}
											name="rowKey"
											rules={[
												{ required: true, message: '请输入rowKey' },
												isFlink112
													? {
															pattern: /^\w{1,64}$/,
															message:
																'只能由字母，数字和下划线组成，且不超过64个字符',
													  }
													: {},
											]}
										>
											<Input
												placeholder={
													isFlink112
														? '请输入 rowkey'
														: 'rowKey 格式：填写字段1+填写字段2'
												}
											/>
										</FormItem>
										{isFlink112 && (
											<>
												<span>&nbsp; 类型：</span>
												<FormItem
													style={{ flex: 1 }}
													name="rowKeyType"
													rules={[
														{
															required: true,
															message: '请输入rowKey类型',
														},
													]}
												>
													<Input placeholder="请输入类型" />
												</FormItem>
											</>
										)}
									</div>
								</FormItem>
							)}
							{[DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(
								getFieldValue('type'),
							) && (
								<FormItem
									label="rowKey"
									tooltip="支持拼接规则：md5(fieldA+fieldB) + fieldC + '常量字符'"
									name="rowKey"
									rules={[{ required: true, message: '请输入rowKey' }]}
								>
									<Input placeholder="rowKey 格式：填写字段1+填写字段2 " />
								</FormItem>
							)}
						</>
					)}
				</FormItem>
				<FormItem
					label="映射表"
					name="tableName"
					rules={[{ required: true, message: '请输入映射表名' }]}
				>
					<Input placeholder="请输入映射表名" />
				</FormItem>
				<FormItem label="字段" required dependencies={['type']}>
					{({ getFieldValue }) =>
						haveTableColumn(getFieldValue('type')) ? (
							<div className="column-container">
								<Table<IFlinkSinkProps['columns'][number]>
									rowKey="column"
									dataSource={data.columns}
									pagination={false}
									size="small"
								>
									<Column<IFlinkSinkProps['columns'][number]>
										title="字段"
										dataIndex="column"
										key="字段"
										width="45%"
										render={(text, record, index) => {
											return (
												<Select
													value={text}
													showSearch
													className="sub-right-select column-table__select"
													onChange={(val) =>
														handleColumnsChanged(
															COLUMNS_OPERATORS.CHANGE_ONE_LINE,
															index,
															{
																column: val,
																// assign type automatically
																type: tableColumnOptionType.find(
																	(c) => c.key.toString() === val,
																)?.type,
															},
														)
													}
												>
													{tableColumnOptionTypes}
												</Select>
											);
										}}
									/>
									<Column<IFlinkSinkProps['columns'][number]>
										title="类型"
										dataIndex="type"
										key="类型"
										width="45%"
										render={(text: string, record, index) => (
											<span
												className={
													text?.toLowerCase() ===
													'Not Support'.toLowerCase()
														? 'has-error'
														: ''
												}
											>
												<Tooltip
													title={text}
													trigger={'hover'}
													placement="topLeft"
													overlayClassName="numeric-input"
												>
													<Input
														value={text}
														className="column-table__input"
														onChange={(e) => {
															handleColumnsChanged(
																COLUMNS_OPERATORS.CHANGE_ONE_LINE,
																index,
																{
																	...record,
																	type: e.target.value,
																},
															);
														}}
													/>
												</Tooltip>
											</span>
										)}
									/>
									<Column
										key="delete"
										render={(_, __, index) => {
											return (
												<CloseOutlined
													style={{
														fontSize: 12,
														color: 'var(--editor-foreground)',
													}}
													onClick={() =>
														handleColumnsChanged(
															COLUMNS_OPERATORS.DELETE_ONE_LINE,
															index,
														)
													}
												/>
											);
										}}
									/>
								</Table>
								<div className="stream-btn column-btn">
									<span>
										<a
											onClick={() =>
												handleColumnsChanged(COLUMNS_OPERATORS.ADD_ONE_LINE)
											}
										>
											添加输入
										</a>
									</span>
									<span>
										<a
											onClick={() =>
												handleColumnsChanged(
													COLUMNS_OPERATORS.ADD_ALL_LINES,
												)
											}
											style={{ marginRight: 12 }}
										>
											导入全部字段
										</a>
										{data?.columns?.length ? (
											<Popconfirm
												title="确认清空所有字段？"
												onConfirm={() =>
													handleColumnsChanged(
														COLUMNS_OPERATORS.DELETE_ALL_LINES,
													)
												}
												okText="确认"
												cancelText="取消"
											>
												<a>清空</a>
											</Popconfirm>
										) : (
											<a style={{ color: 'var(--editor-foreground)' }}>
												清空
											</a>
										)}
									</span>
								</div>
							</div>
						) : (
							isShow && (
								<Editor
									style={{
										minHeight: 202,
									}}
									sync={sync}
									options={{
										minimap: {
											enabled: false,
										},
									}}
									placeholder={getPlaceholder(data.type!)}
									value={data.columnsText}
									onChange={debounceEditorChange}
								/>
							)
						)
					}
				</FormItem>
				<FormItem noStyle dependencies={['type']}>
					{({ getFieldValue }) => (
						<>
							{isKafka(getFieldValue('type')) && (
								<React.Fragment>
									<FormItem
										label="输出类型"
										name="sinkDataType"
										rules={[{ required: true, message: '请选择输出类型' }]}
									>
										<Select style={{ width: '100%' }}>
											{getFieldValue('type') ===
											DATA_SOURCE_ENUM.KAFKA_CONFLUENT ? (
												<Option
													value={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
													key={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
												>
													{KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
												</Option>
											) : (
												KAFKA_DATA_LIST.map(({ text, value }) => (
													<Option value={value} key={text + value}>
														{text}
													</Option>
												))
											)}
										</Select>
									</FormItem>
									<FormItem noStyle dependencies={['sinkDataType']}>
										{({ getFieldValue: getField }) =>
											isAvro(getField('sinkDataType')) && (
												<FormItem
													label="Schema"
													name="schemaInfo"
													rules={[
														{
															required: !isFlink112,
															message: '请输入Schema',
														},
													]}
												>
													<Input.TextArea
														rows={9}
														placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
													/>
												</FormItem>
											)
										}
									</FormItem>
								</React.Fragment>
							)}
							{haveUpdateMode(getFieldValue('type')) && (
								<>
									<FormItem
										label="更新模式"
										name="updateMode"
										rules={[{ required: true, message: '请选择更新模式' }]}
									>
										<Radio.Group
											disabled={isDisabledUpdateMode(
												getFieldValue('type'),
												disableUpdateMode,
												componentVersion,
											)}
											className="right-select"
										>
											<Radio value="append">追加(append)</Radio>
											<Radio
												value="upsert"
												disabled={
													getFieldValue('type') ===
													DATA_SOURCE_ENUM.CLICKHOUSE
												}
											>
												更新(upsert)
											</Radio>
										</Radio.Group>
									</FormItem>
									<FormItem noStyle dependencies={['updateMode']}>
										{({ getFieldValue: getField }) => (
											<>
												{getField('updateMode') === 'upsert' &&
													haveUpdateStrategy(getFieldValue('type')) && (
														<FormItem
															label="更新策略"
															name="allReplace"
															initialValue="false"
															rules={[
																{
																	required: true,
																	message: '请选择更新策略',
																},
															]}
														>
															<Select className="right-select">
																<Option key="true" value="true">
																	Null值替换原有数据
																</Option>
																<Option key="false" value="false">
																	Null值不替换原有数据
																</Option>
															</Select>
														</FormItem>
													)}
												{getField('updateMode') === 'upsert' &&
													(havePrimaryKey(getFieldValue('type')) ||
														!isDisabledUpdateMode(
															getFieldValue('type'),
															disableUpdateMode,
															componentVersion,
														)) && (
														<FormItem
															label="主键"
															tooltip="主键必须存在于表字段中"
															name="primaryKey"
															rules={[
																{
																	required: true,
																	message: '请输入主键',
																},
															]}
														>
															<Select
																className="right-select"
																listHeight={200}
																mode="multiple"
																showSearch
																showArrow
																filterOption={(input, option) =>
																	option?.props.children
																		.toLowerCase()
																		.indexOf(
																			input.toLowerCase(),
																		) >= 0
																}
															>
																{primaryKeyOptionTypes}
															</Select>
														</FormItem>
													)}
											</>
										)}
									</FormItem>
								</>
							)}
						</>
					)}
				</FormItem>
				{/* 高级参数按钮 */}
				<div style={{ margin: '12px 0', textAlign: 'center' }}>
					<span
						style={{ cursor: 'pointer', color: '#666666' }}
						onClick={() => {
							setShowAdvancedParams(!showAdvancedParams);
						}}
					>
						高级参数&nbsp;
						{showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
					</span>
				</div>
				{/* 高级参数抽屉 */}
				<FormItem hidden={!showAdvancedParams} noStyle dependencies={['type']}>
					{({ getFieldValue }) => (
						<>
							{haveParallelism(getFieldValue('type')) && (
								<FormItem name="parallelism" label="并行度">
									<InputNumber
										className={classNames('number-input')}
										style={{ width: '100%' }}
										min={1}
										precision={0}
									/>
								</FormItem>
							)}
							{isES(getFieldValue('type')) && isFlink112 && (
								<FormItem name="bulkFlushMaxActions" label="数据输出条数">
									<InputNumber
										className={classNames('number-input')}
										style={{ width: '100%' }}
										min={1}
										max={10000}
										precision={0}
									/>
								</FormItem>
							)}
							{isKafka(getFieldValue('type')) && (
								<FormItem
									label=""
									name="enableKeyPartitions"
									valuePropName="checked"
								>
									<Checkbox style={{ marginLeft: 90 }} defaultChecked={false}>
										根据字段(Key)分区
									</Checkbox>
								</FormItem>
							)}
							{getFieldValue('type') === DATA_SOURCE_ENUM.ES7 && (
								<FormItem
									label="索引映射"
									tooltip={
										<span>
											ElasticSearch的索引映射配置，仅当动态索引时生效，更多请参考
											<a
												rel="noopener noreferrer"
												target="_blank"
												href={HELP_DOC_URL.INDEX}
											>
												帮助文档
											</a>
										</span>
									}
									name="indexDefinition"
								>
									<Input.TextArea
										placeholder={DEFAULT_MAPPING_TEXT}
										style={{ minHeight: '200px' }}
									/>
								</FormItem>
							)}
							<FormItem noStyle dependencies={['enableKeyPartitions']}>
								{({ getFieldValue: getField }) =>
									getField('enableKeyPartitions') && (
										<FormItem
											label="分区字段"
											name="partitionKeys"
											rules={[{ required: true, message: '请选择分区字段' }]}
										>
											<Select
												className="right-select"
												mode="multiple"
												showSearch
												showArrow
												filterOption={(input, option) =>
													option?.props.children
														.toLowerCase()
														.indexOf(input.toLowerCase()) >= 0
												}
											>
												{getColumnsByColumnsText(
													getField('columnsText'),
												).map((column) => {
													const fields = column.field?.trim();
													return (
														<Option value={fields} key={fields}>
															{fields}
														</Option>
													);
												})}
											</Select>
										</FormItem>
									)
								}
							</FormItem>
							{isRDB(getFieldValue('type')) && (
								<>
									<FormItem
										label="数据输出时间"
										name="batchWaitInterval"
										rules={[{ required: true, message: '请输入数据输出时间' }]}
									>
										<InputNumber
											className={classNames('number-input')}
											style={{ width: '100%' }}
											min={0}
											max={600000}
											precision={0}
											addonAfter="ms/次"
										/>
									</FormItem>
									<FormItem
										label="数据输出条数"
										name="batchSize"
										rules={[{ required: true, message: '请输入数据输出条数' }]}
									>
										<InputNumber
											className={classNames('number-input')}
											style={{ width: '100%' }}
											min={0}
											max={
												getFieldValue('type') === DATA_SOURCE_ENUM.KUDU
													? 100000
													: 10000
											}
											precision={0}
											addonAfter="条/次"
										/>
									</FormItem>
								</>
							)}
							{!haveParallelism(getFieldValue('type')) && (
								<FormItem
									label="分区类型"
									tooltip="分区类型包括 DAY、HOUR、MINUTE三种。若分区不存在则会自动创建，自动创建的分区时间以当前任务运行的服务器时间为准"
									name="partitionType"
									initialValue="DAY"
								>
									<Select className="right-select">
										<Option value="DAY">DAY</Option>
										<Option value="HOUR">HOUR</Option>
										<Option value="MINUTE">MINUTE</Option>
									</Select>
								</FormItem>
							)}
							{/* 添加自定义参数 */}
							{!isSqlServer(getFieldValue('type')) && (
								<CustomParams
									customParams={data.customParams || []}
									onChange={handleCustomParamsChanged}
								/>
							)}
						</>
					)}
				</FormItem>
			</Form>
			<DataPreviewModal
				visible={visible}
				type={form.getFieldValue('type')}
				onCancel={() => {
					setVisible(false);
				}}
				params={params}
			/>
		</div>
	);
}
