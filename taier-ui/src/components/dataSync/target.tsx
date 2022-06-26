import { useEffect, useMemo, useState } from 'react';
import type { FormInstance } from 'antd';
import {
	Spin,
	Modal,
	Form,
	Select,
	Button,
	Input,
	AutoComplete,
	Radio,
	message,
	Space,
	Tooltip,
	InputNumber,
} from 'antd';
import { throttle } from 'lodash';
import {
	DDL_IDE_PLACEHOLDER,
	formItemLayout,
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	TASK_LANGUAGE,
} from '@/constant';
import { formJsonValidator } from '@/utils';
import Editor from '@/components/editor';
import type {
	IDataColumnsProps,
	ISourceMapProps,
	ITargetFormField,
	ITargetMapProps,
	IDataSourceUsedInSyncProps,
} from '@/interface';
import API from '../../api';
import {
	dataSyncExtralConfigHelp,
	es7BulkAction,
	es7Index,
	indexTypeDoc,
	partitionDesc,
	splitCharacter,
} from '../helpDoc/docs';
import { ALLOW_CREATE_TABLE_IN_SOURCE, ALLOW_CREATE_TABLE_IN_TARGET, noWhiteSpace } from './help';

const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const RadioGroup = Radio.Group;

type IFormFieldProps = ITargetFormField;

interface ITargetProps {
	/**
	 * 当前目标表表单域字段的默认值，用于编辑时的回填
	 */
	targetMap?: ITargetMapProps;
	/**
	 * 上一步选择源表所填写的信息
	 */
	sourceMap?: ISourceMapProps;
	/**
	 * @requires
	 * 数据源列表
	 */
	dataSourceList: IDataSourceUsedInSyncProps[];
	/**
	 * 是否增量模式
	 */
	isIncrementMode?: boolean;
	/**
	 * 是否只读，用于预览数据
	 */
	readonly?: boolean;
	/**
	 * 下一步或上一步的回调函数
	 * @param next 为 true 时表示下一步，否则为上一步
	 */
	onNext?: (next: boolean) => void;
	/**
	 * @deprecated
	 * 成功获取目标表的表字段时的回调函数
	 */
	onGetTableCols?: (cols: IDataColumnsProps[]) => void;
	/**
	 * 监听当前表单域改变的回调函数
	 */
	onFormValuesChanged?: (values: IFormFieldProps) => void;
}

// 允许获取分区的数据源
const ALLOW_REQUEST_HIVE = [
	DATA_SOURCE_ENUM.HIVE1X,
	DATA_SOURCE_ENUM.HIVE,
	DATA_SOURCE_ENUM.HIVE3X,
	DATA_SOURCE_ENUM.SPARKTHRIFT,
];

// 不允许请求 tableList 的数据源
const NOT_REQUEST_TABLELIST = [
	DATA_SOURCE_ENUM.KINGBASE8,
	DATA_SOURCE_ENUM.S3,
	DATA_SOURCE_ENUM.ADB_FOR_PG,
	DATA_SOURCE_ENUM.HDFS,
	DATA_SOURCE_ENUM.DORIS,
	DATA_SOURCE_ENUM.ES7,
	DATA_SOURCE_ENUM.ES6,
	DATA_SOURCE_ENUM.ES,
];

// 允许请求 schema 的数据源
const ALLOW_REQUEST_SCHEMA = [
	DATA_SOURCE_ENUM.KINGBASE8,
	DATA_SOURCE_ENUM.POSTGRESQL,
	DATA_SOURCE_ENUM.ADB_FOR_PG,
	DATA_SOURCE_ENUM.ORACLE,
	DATA_SOURCE_ENUM.DORIS,
	DATA_SOURCE_ENUM.ES,
	DATA_SOURCE_ENUM.ES6,
	DATA_SOURCE_ENUM.ES7,
];

export default function Target({
	targetMap,
	sourceMap,
	dataSourceList,
	isIncrementMode = false,
	readonly,
	onNext,
	onGetTableCols,
	onFormValuesChanged,
}: ITargetProps) {
	const [form] = Form.useForm<IFormFieldProps>();
	const [tableListLoading, setTableListLoading] = useState(false);
	const [modalInfo, setModalInfo] = useState({ loading: false, visible: false });
	const [tableList, setTableList] = useState<string[]>([]);
	const [fetching, setFetching] = useState(false);
	const [loading, setLoading] = useState(false);
	const [schemaList, setSchemaList] = useState<string[]>([]);
	const [editorInfo, setEditorInfo] = useState({ textSql: '', sync: false });
	const [tablePartitionList, setPartitionList] = useState<string[]>([]);

	const getTableList = throttle((sourceId: number, schema?: string, name?: string) => {
		setFetching(true);
		setTableListLoading(true);
		setTableList([]);
		API.getOfflineTableList({
			sourceId,
			isSys: false,
			schema,
			name,
			isRead: false,
		})
			.then((res) => {
				if (res.code === 1) {
					setTableList(res.data || []);
				}
			})
			.finally(() => {
				setTableListLoading(false);
				setFetching(false);
			});
	}, 500);

	const getSchemaList = (sourceId: number) => {
		API.getAllSchemas({
			sourceId,
		}).then((res) => {
			if (res.code === 1) {
				setSchemaList(res.data || []);
			}
		});
	};

	const getHivePartitions = (
		sourceId: number = form.getFieldValue('sourceId'),
		table: string = form.getFieldValue('table'),
	) => {
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target) return;
		// TODO 这里获取 Hive 分区的条件有点模糊
		if (ALLOW_REQUEST_HIVE.includes(target.dataTypeCode)) {
			API.getHivePartitionsForDataSource({
				sourceId,
				tableName: table,
			}).then((res) => {
				setPartitionList(res.data || []);
			});
		}
	};

	const getTableColumn = () => {
		setModalInfo((info) => ({ ...info, loading: true }));

		const { sourceId, table, index, indexType, schema } = form.getFieldsValue();
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target) return;

		const ES_DATASOURCE = [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7];
		const targetType = target.dataTypeCode;

		// es 数据源：
		// - tableName 取自字段 indexType
		// - schema 取自字段 index
		const querySchema = ES_DATASOURCE.includes(targetType) ? index : schema;
		const queryTableName = ES_DATASOURCE.includes(targetType) ? indexType : table;

		// Hive 作为结果表时，需要获取分区字段
		const includePart =
			+targetType === DATA_SOURCE_ENUM.HIVE1X ||
			+targetType === DATA_SOURCE_ENUM.HIVE ||
			+targetType === DATA_SOURCE_ENUM.HIVE3X ||
			+targetType === DATA_SOURCE_ENUM.SPARKTHRIFT;

		/**
		 * @deprecated remove in next version
		 * The table columns request not longer made there, it should make where it need to be made
		 * Remove it before ensuring there will be no affected to dataSync task
		 */
		return;
		setLoading(true);
		// get table columns for third steps
		API.getOfflineTableColumn({
			sourceId,
			schema: querySchema,
			tableName: queryTableName,
			isIncludePart: includePart,
		})
			.then((res) => {
				if (res.code === 1) {
					onGetTableCols?.(res.data || []);
				}
			})
			.finally(() => {
				setModalInfo((info) => ({ ...info, loading: false }));
				setLoading(false);
			});
	};

	const checkIsNativeHive = (tableName: string) => {
		const sourceId = form.getFieldValue('sourceId');
		if (!tableName || !sourceId) {
			return false;
		}
	};

	const handleFormFieldsChanged = (
		changedValue: Partial<IFormFieldProps>,
		values: IFormFieldProps,
	) => {
		if (changedValue.hasOwnProperty('table')) {
			const { table } = changedValue;
			if (table) {
				// 获取表列字段
				getTableColumn();
				// 检测是否有 native hive
				checkIsNativeHive(table as string);
				// 获取 Hive 分区字段
				getHivePartitions();
			}

			form.setFieldsValue({ partition: undefined });
		}

		if (changedValue.hasOwnProperty('schema')) {
			const { schema } = changedValue;
			if (schema) {
				getTableList(values.sourceId!, changedValue.schema);
			}
			form.setFieldsValue({ table: undefined });
		}

		if (changedValue.hasOwnProperty('index')) {
			const target = dataSourceList.find((d) => d.dataInfoId === values.sourceId);
			if (!target) return;
			const LOWER_ES_VERSION = [DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES];
			const isES5orES6 = LOWER_ES_VERSION.includes(target.dataTypeCode);

			if (isES5orES6) {
				// 低版本的 es 需要先拿 indexType 才可以获取 columns
				getTableList(values.sourceId!, changedValue.index);
			} else {
				// 高版本的 es 没有 indexType 字段，直接获取 columns
				getTableColumn();
			}
		}

		if (changedValue.hasOwnProperty('indexType')) {
			getTableColumn();
		}

		// 修改数据源需要重置 writeMode
		if (changedValue.hasOwnProperty('sourceId')) {
			form.setFieldsValue({
				writeMode: 'insert',
			});
		}

		// It's better to use form.getFieldsValue rather than the values params is for
		// there are some set methods before this function which will lead to an out of date values
		onFormValuesChanged?.(form.getFieldsValue());
	};

	const handleSourceChanged = (sourceId: number) => {
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target) return;
		if (!NOT_REQUEST_TABLELIST.includes(target.dataTypeCode)) {
			getTableList(sourceId);
		}

		// 有schema才需要获取schemalist
		if (ALLOW_REQUEST_SCHEMA.includes(target.dataTypeCode)) {
			getSchemaList(sourceId);
		}

		// reset table value
		const resetField = {
			table: undefined,
			index: undefined,
			indexType: undefined,
			schema: undefined,
		};
		form.setFieldsValue(resetField);
		handleFormFieldsChanged(resetField, form.getFieldsValue());
	};

	const handleShowCreateModal = () => {
		const { schema, table, sourceId } = form.getFieldsValue();
		const tableName =
			typeof sourceMap?.table === 'string'
				? sourceMap?.table
				: sourceMap?.table && sourceMap?.table[0];
		const targetTableName = Array.isArray(table) ? table[0] : table;
		setLoading(true);
		API.getCreateTargetTable({
			originSourceId: sourceMap?.sourceId,
			tableName,
			partition: sourceMap?.partition,
			targetSourceId: sourceId,
			originSchema: sourceMap?.schema || null,
			targetSchema: schema || null,
		})
			.then((res) => {
				if (res.code === 1) {
					let textSql: string = res.data;
					if (targetTableName) {
						const reg = /create\s+table\s+`(.*)`\s*\(/i;
						textSql = textSql.replace(reg, (match, p1) =>
							match.replace(p1, targetTableName),
						);
					}
					setEditorInfo({
						textSql,
						sync: true,
					});
					setModalInfo({
						visible: true,
						loading: false,
					});
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const createTable = () => {
		const { sourceId } = form.getFieldsValue();
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target) return;
		setModalInfo({ loading: true, visible: true });
		API.createDdlTable({
			sql: editorInfo.textSql,
			sourceId,
		})
			.then((res) => {
				if (res.code === 1) {
					getTableList(sourceId!);
					form.setFieldsValue({ table: res.data });
					// for triggering this function
					handleFormFieldsChanged({ table: res.data }, form.getFieldsValue());
					message.success('表创建成功!');
					setModalInfo((i) => ({ ...i, visible: false }));
				}
			})
			.finally(() => {
				setModalInfo((i) => ({ ...i, loading: false }));
			});
	};

	const validateChineseCharacter = (data: IFormFieldProps) => {
		const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
		let has = false;
		const fieldsName: string[] = [];
		if (data.path && reg.test(data.path)) {
			has = true;
			fieldsName.push('路径');
		}
		if (data.fileName && reg.test(data.fileName)) {
			has = true;
			fieldsName.push('文件名');
		}
		if (data.fieldDelimiter && reg.test(data.fieldDelimiter)) {
			has = true;
			fieldsName.push('列分隔符');
		}
		if (has) {
			message.warning(`${fieldsName.join('、')}参数中有包含中文或者中文标点符号！`);
		}
	};

	const handleSubmit = () => {
		form.validateFields().then((values) => {
			validateChineseCharacter(values);
			onNext?.(true);
		});
	};

	const renderDynamicForm = (f: Pick<FormInstance, 'getFieldValue'>) => {
		const target = dataSourceList.find((l) => l.dataInfoId === f.getFieldValue('sourceId'));
		if (!target) return;

		// 只有「源表」以及「目标表」都满足情况的条件下才支持生成目标表
		const oneKeyCreateTable = ALLOW_CREATE_TABLE_IN_TARGET.includes(target.dataTypeCode) &&
			ALLOW_CREATE_TABLE_IN_SOURCE.includes(sourceMap!.type!) && (
				<Button type="link" loading={loading} onClick={handleShowCreateModal}>
					一键生成目标表
				</Button>
			);

		switch (target.dataTypeCode) {
			case DATA_SOURCE_ENUM.MYSQL:
			case DATA_SOURCE_ENUM.SQLSERVER: {
				return (
					<>
						<FormItem label="表名" key="table" required>
							<FormItem
								name="table"
								rules={[
									{
										required: true,
										message: '请选择表',
									},
								]}
								noStyle
							>
								<Select
									getPopupContainer={(container) => container.parentNode}
									showSearch
									optionFilterProp="value"
									filterOption={false}
									onSearch={(str) =>
										getTableList(f.getFieldValue('sourceId'), undefined, str)
									}
									notFoundContent={fetching ? <Spin size="small" /> : null}
								>
									{tableList.map((table) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>
							</FormItem>
							{oneKeyCreateTable}
						</FormItem>
						<FormItem name="preSql" label="导入前准备语句" key="preSql">
							<TextArea placeholder="请输入导入数据前执行的SQL脚本" />
						</FormItem>
						<FormItem name="postSql" label="导入后准备语句" key="postSql">
							<Input.TextArea placeholder="请输入导入数据后执行的SQL脚本" />
						</FormItem>
						<FormItem
							label="主键冲突"
							key="writeMode-mysql"
							name="writeMode"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="insert"
						>
							<Select>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
								<Option key="writeModeReplace" value="replace">
									replace
									into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）
								</Option>
								<Option key="writeModeUpdate" value="update">
									on duplicate key
									update（当主键/约束冲突，update数据，未映射的字段值不变）
								</Option>
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.ORACLE: {
				return (
					<>
						<FormItem name="schema" label="schema" key="schema">
							<Select showSearch showArrow allowClear>
								{schemaList.map((copateValue) => {
									return (
										<Option key={copateValue} value={copateValue}>
											{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
											{copateValue === 'ROW_NUMBER()'
												? 'ROW_NUMBER'
												: copateValue}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						<FormItem label="表名" key="table" required>
							<FormItem
								name="table"
								rules={[
									{
										required: true,
										message: '请选择表',
									},
								]}
								noStyle
							>
								<Select
									getPopupContainer={(container) => container.parentNode}
									showSearch
									optionFilterProp="value"
									filterOption={false}
									onSearch={(val) =>
										getTableList(f.getFieldValue('sourceId'), undefined, val)
									}
									notFoundContent={fetching ? <Spin size="small" /> : null}
								>
									{tableList.map((table) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>
							</FormItem>
							{oneKeyCreateTable}
						</FormItem>
						<FormItem name="preSql" label="导入前准备语句" key="preSql">
							<TextArea placeholder="请输入导入数据前执行的SQL脚本" />
						</FormItem>
						<FormItem name="postSql" label="导入后准备语句" key="postSql">
							<Input.TextArea placeholder="请输入导入数据后执行的SQL脚本" />
						</FormItem>
						<FormItem
							label="主键冲突"
							key="writeMode-mysql"
							name="writeMode"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="insert"
						>
							<Select>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.HIVE:
			case DATA_SOURCE_ENUM.HIVE1X:
			case DATA_SOURCE_ENUM.HIVE3X:
			case DATA_SOURCE_ENUM.SPARKTHRIFT: {
				return (
					<>
						<FormItem label="表名" key="table" required>
							<FormItem
								name="table"
								rules={[
									{
										required: true,
										message: '请选择表',
									},
								]}
								noStyle
							>
								<Select
									getPopupContainer={(container) => container.parentNode}
									showSearch
									notFoundContent={fetching ? <Spin size="small" /> : null}
									optionFilterProp="value"
								>
									{tableList.map((table) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>
							</FormItem>
							{oneKeyCreateTable}
						</FormItem>
						{Boolean(tablePartitionList.length) && (
							<FormItem
								tooltip={partitionDesc}
								name="partition"
								label="分区"
								rules={[
									{
										required: true,
										message: '目标分区为必填项！',
									},
								]}
							>
								<AutoComplete showSearch showArrow placeholder="请填写分区信息">
									{tablePartitionList.map((pt) => {
										return (
											<AutoComplete.Option key={`rdb-${pt}`} value={pt}>
												{pt}
											</AutoComplete.Option>
										);
									})}
								</AutoComplete>
							</FormItem>
						)}
						<FormItem
							label="写入模式"
							key="writeMode-hive"
							name="writeMode"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="replace"
						>
							<RadioGroup>
								<Radio value="replace">覆盖（Insert Overwrite）</Radio>
								<Radio value="insert">追加（Insert Into）</Radio>
							</RadioGroup>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.POSTGRESQL: {
				return (
					<>
						<FormItem name="schema" label="schema" key="schema">
							<Select showSearch showArrow allowClear>
								{schemaList.map((copateValue) => {
									return (
										<Option key={copateValue} value={copateValue}>
											{copateValue}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						<FormItem label="表名" key="table" required>
							<FormItem
								name="table"
								rules={[
									{
										required: true,
										message: '请选择表',
									},
								]}
								noStyle
							>
								<Select
									getPopupContainer={(container) => container.parentNode}
									showSearch
									onSearch={(val) =>
										getTableList(f.getFieldValue('sourceid'), undefined, val)
									}
									notFoundContent={fetching ? <Spin size="small" /> : null}
								>
									{tableList.map((table) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>
							</FormItem>
							{oneKeyCreateTable}
						</FormItem>
						<FormItem name="preSql" label="导入前准备语句" key="preSql">
							<TextArea placeholder="请输入导入数据前执行的SQL脚本" />
						</FormItem>
						<FormItem name="postSql" label="导入后准备语句" key="postSql">
							<Input.TextArea placeholder="请输入导入数据后执行的SQL脚本" />
						</FormItem>
						<FormItem
							label="主键冲突"
							key="writeMode-mysql"
							name="writeMode"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="insert"
						>
							<Select>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.HDFS: {
				return (
					<>
						<FormItem
							name="path"
							label="路径"
							key="path"
							rules={[
								{
									required: true,
								},
							]}
						>
							<Input placeholder="例如: /app/batch" />
						</FormItem>
						<FormItem
							name="fileName"
							label="文件名"
							key="fileName"
							rules={[
								{
									required: true,
								},
							]}
						>
							<Input placeholder="请输入文件名" />
						</FormItem>
						<FormItem
							name="fileType"
							label="文件类型"
							key="fileType"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="orc"
						>
							<Select getPopupContainer={(container) => container.parentNode}>
								<Option value="orc">orc</Option>
								<Option value="text">text</Option>
								<Option value="parquet">parquet</Option>
							</Select>
						</FormItem>
						<FormItem
							name="fieldDelimiter"
							tooltip={splitCharacter}
							label="列分隔符"
							key="fieldDelimiter"
							initialValue={','}
						>
							<Input placeholder={`例如: 目标为hive则 分隔符为\\001`} />
						</FormItem>
						<FormItem
							name="encoding"
							label="编码"
							key="encoding"
							rules={[
								{
									required: true,
								},
							]}
							initialValue={'utf-8'}
						>
							<Select getPopupContainer={(container) => container.parentNode}>
								<Option value="utf-8">utf-8</Option>
								<Option value="gbk">gbk</Option>
							</Select>
						</FormItem>
						<FormItem
							label="写入模式"
							key="writeMode-hdfs"
							name="writeMode"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="APPEND"
						>
							<RadioGroup>
								<Radio value="NONCONFLICT" style={{ float: 'left' }}>
									覆盖（Insert Overwrite）
								</Radio>
								<Radio value="APPEND" style={{ float: 'left' }}>
									追加（Insert Into）
								</Radio>
							</RadioGroup>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.HBASE: {
				return (
					<>
						<FormItem
							label="表名"
							key="table"
							name="table"
							rules={[
								{
									required: true,
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								filterOption={false}
								onSearch={(str) =>
									getTableList(f.getFieldValue('sourceId'), undefined, str)
								}
							>
								{tableList.map((table) => {
									return (
										<Option key={`hbase-target-${table}`} value={table}>
											{table}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						<FormItem
							name="encoding"
							label="编码"
							key="encoding"
							rules={[
								{
									required: true,
								},
							]}
							initialValue="utf-8"
						>
							<Select getPopupContainer={(container) => container.parentNode}>
								<Option value="utf-8">utf-8</Option>
								<Option value="gbk">gbk</Option>
							</Select>
						</FormItem>
						<FormItem
							label="读取为空时的处理方式"
							key="nullMode"
							name="nullMode"
							rules={[
								{
									required: true,
									message: '请选择为空时的处理方式!',
								},
							]}
							initialValue="skip"
						>
							<RadioGroup>
								<Radio value="skip">SKIP</Radio>
								<Radio value="empty">EMPTY</Radio>
							</RadioGroup>
						</FormItem>
						<FormItem name="writeBufferSize" label="写入缓存大小" key="writeBufferSize">
							<Input placeholder="请输入缓存大小" type="number" min={0} suffix="KB" />
						</FormItem>
					</>
				);
			}

			case DATA_SOURCE_ENUM.ES:
			case DATA_SOURCE_ENUM.ES6:
			case DATA_SOURCE_ENUM.ES7: {
				const LOWER_ES_VERSION = [DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES];
				const isES5orES6 = LOWER_ES_VERSION.includes(target.dataTypeCode);

				return (
					<>
						<FormItem
							name="index"
							label="index"
							key="index"
							rules={[
								{
									required: true,
									message: '请输入index',
								},
								{
									max: 128,
									message: '仅支持1-128个任意字符',
								},
								{
									validator: noWhiteSpace,
								},
							]}
							tooltip={es7Index}
							trigger="onBlur"
							validateTrigger="onBlur"
						>
							<AutoComplete<string, { label: string; value: string }>
								getPopupContainer={(container) => container.parentNode}
								placeholder="请输入index"
								style={{ width: '100%' }}
								options={schemaList.map((schema) => ({
									label: schema,
									value: schema,
								}))}
								filterOption={(inputValue, option) =>
									!!option?.value
										?.toUpperCase()
										.includes(inputValue.toUpperCase())
								}
							/>
						</FormItem>
						{isES5orES6 && (
							<FormItem
								name="indexType"
								label="type"
								key="indexType"
								rules={[
									{
										required: true,
										message: '请输入indexType',
									},
									{
										max: 128,
										message: '仅支持1-128个任意字符',
									},
									{
										validator: noWhiteSpace,
									},
								]}
								tooltip={indexTypeDoc}
								trigger="onBlur"
								validateTrigger="onBlur"
							>
								<AutoComplete<string, { label: string; value: string }>
									getPopupContainer={(container) => container.parentNode}
									style={{ width: '100%' }}
									options={tableList.map((table) => ({
										label: table,
										value: table,
									}))}
									placeholder="请输入indexType"
									filterOption={(inputValue, option) =>
										!!option?.value
											?.toUpperCase()
											.includes(inputValue.toUpperCase())
									}
								/>
							</FormItem>
						)}
						<FormItem
							name="bulkAction"
							label="bulkAction"
							key="bulkAction"
							rules={[
								{
									required: true,
									message: '请输入bulkAction',
								},
							]}
							initialValue={100}
							tooltip={es7BulkAction}
						>
							<InputNumber
								min={1}
								max={200000}
								precision={0}
								placeholder="请输入bulkAction"
								style={{ width: '100%' }}
							/>
						</FormItem>
					</>
				);
			}

			default:
				return null;
		}
	};

	useEffect(() => {
		if (targetMap?.sourceId) {
			// es 数据源的 schema 取自字段 index
			const ES_DATASOURCE = [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7];
			if (!NOT_REQUEST_TABLELIST.includes(targetMap.type!)) {
				getTableList(
					targetMap.sourceId,
					ES_DATASOURCE.includes(targetMap.type!) ? targetMap.index : targetMap.schema,
				);
			}

			if (ALLOW_REQUEST_SCHEMA.includes(targetMap.type!)) {
				getSchemaList(targetMap.sourceId);
			}

			if (targetMap?.table) {
				getHivePartitions(targetMap.sourceId, targetMap.table);
			}
		}
	}, []);

	const initialValue = useMemo<IFormFieldProps>(() => {
		return {
			sourceId: targetMap?.sourceId,
			extralConfig: targetMap?.extralConfig,
			table: targetMap?.table,
			preSql: targetMap?.preSql,
			postSql: targetMap?.postSql,
			writeMode: targetMap?.writeMode,
			schema: targetMap?.schema,
			partition: targetMap?.partition,
			path: targetMap?.path,
			fileName: targetMap?.fileName,
			fileType: targetMap?.fileType,
			fieldDelimiter: targetMap?.fieldDelimiter,
			encoding: targetMap?.encoding,
			nullMode: targetMap?.nullMode,
			writeBufferSize: targetMap?.writeBufferSize,
			index: targetMap?.index,
			indexType: targetMap?.indexType,
			bulkAction: targetMap?.bulkAction,
		};
	}, []);

	if (!sourceMap) return null;

	return (
		<Spin spinning={tableListLoading}>
			<div className="g-step2">
				<Form<IFormFieldProps>
					form={form}
					{...formItemLayout}
					initialValues={initialValue}
					onValuesChange={handleFormFieldsChanged}
					preserve={false}
				>
					<FormItem noStyle dependencies={['sourceId']}>
						{({ getFieldValue }) => {
							const target = dataSourceList.find(
								(l) => l.dataInfoId === getFieldValue('sourceId'),
							);
							if (!target) return;
							// const mode =
							// 	target.dataTypeCode === DATA_SOURCE_ENUM.IMPALA ? 'sql' : 'sql';
							return (
								<Modal
									className="m-codemodal"
									title={<span>建表语句</span>}
									confirmLoading={modalInfo.loading}
									destroyOnClose
									maskClosable={false}
									style={{ height: 424 }}
									visible={modalInfo.visible}
									onCancel={() =>
										setModalInfo({ visible: false, loading: false })
									}
									onOk={createTable}
								>
									<Editor
										style={{ height: 400, marginTop: 1 }}
										language={TASK_LANGUAGE.MYSQL}
										value={editorInfo.textSql}
										sync={editorInfo.sync}
										placeholder={DDL_IDE_PLACEHOLDER}
										options={{
											minimap: { enabled: false },
										}}
										onChange={(val) =>
											setEditorInfo({
												textSql: val,
												sync: false,
											})
										}
									/>
								</Modal>
							);
						}}
					</FormItem>

					<FormItem
						name="sourceId"
						label="数据同步目标"
						rules={[
							{
								required: true,
							},
						]}
					>
						<Select
							getPopupContainer={(container) => container.parentNode}
							showSearch
							onSelect={handleSourceChanged}
							optionFilterProp="name"
						>
							{dataSourceList.map((src) => {
								const title = `${src.dataName}（${
									DATA_SOURCE_TEXT[src.dataTypeCode]
								}）`;

								// 暂时支持以下类型的数据源
								const tmpSupportDataSource = [
									DATA_SOURCE_ENUM.MYSQL,
									DATA_SOURCE_ENUM.ORACLE,
									DATA_SOURCE_ENUM.POSTGRESQL,
									DATA_SOURCE_ENUM.HIVE,
									DATA_SOURCE_ENUM.HIVE1X,
									DATA_SOURCE_ENUM.HIVE3X,
									DATA_SOURCE_ENUM.ES,
									DATA_SOURCE_ENUM.ES6,
									DATA_SOURCE_ENUM.ES7,
									DATA_SOURCE_ENUM.SPARKTHRIFT,
								];

								/**
								 * 增量模式禁用非 HIVE, HDFS数据源
								 */
								const disableSelect =
									!tmpSupportDataSource.includes(src.dataTypeCode) ||
									(isIncrementMode &&
										src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE1X &&
										src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE &&
										src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE3X &&
										src.dataTypeCode !== DATA_SOURCE_ENUM.SPARKTHRIFT &&
										src.dataTypeCode !== DATA_SOURCE_ENUM.HDFS);

								return (
									<Option
										key={src.dataInfoId}
										disabled={disableSelect}
										name={src.dataName}
										value={src.dataInfoId}
									>
										{disableSelect ? (
											<Tooltip title="目前暂时不支持该数据源类型">
												{title}
											</Tooltip>
										) : (
											title
										)}
									</Option>
								);
							})}
						</Select>
					</FormItem>
					<FormItem noStyle dependencies={['sourceId']}>
						{(f) => renderDynamicForm(f)}
					</FormItem>
					<FormItem noStyle dependencies={['sourceId']}>
						{({ getFieldValue }) =>
							getFieldValue('sourceId') !== undefined && (
								<FormItem
									tooltip={dataSyncExtralConfigHelp}
									label="高级配置"
									name="extralConfig"
									rules={[
										{
											validator: formJsonValidator,
										},
									]}
								>
									<TextArea
										placeholder={
											'以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize'
										}
										autoSize={{ minRows: 2, maxRows: 6 }}
									/>
								</FormItem>
							)
						}
					</FormItem>
				</Form>
				{!readonly && (
					<div className="steps-action">
						<Space>
							<Button onClick={() => onNext?.(false)}>上一步</Button>
							<Button type="primary" onClick={handleSubmit} loading={loading}>
								下一步
							</Button>
						</Space>
					</div>
				)}
			</div>
		</Spin>
	);
}
