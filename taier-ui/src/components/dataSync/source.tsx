import { useEffect, useMemo, useState } from 'react';
import type { FormInstance } from 'antd';
import api from '@/api';
import {
	BINARY_ROW_KEY_FLAG,
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	formItemLayout,
	SUPPROT_SUB_LIBRARY_DB_ARRAY,
} from '@/constant';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import { formJsonValidator } from '@/utils';
import { isRDB } from '@/utils/is';
import {
	Form,
	Select,
	Spin,
	Input,
	Button,
	AutoComplete,
	Row,
	Col,
	message,
	Table,
	Tooltip,
	Radio,
} from 'antd';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import {
	dataFilterDoc,
	dataSyncExtralConfigHelp,
	es7Index,
	es7Query,
	hdfsPath,
	incrementColumnHelp,
	indexTypeDoc,
	partitionDesc,
	selectKey,
	splitCharacter,
} from '../helpDoc/docs';
import TableCell from '../tableCell';
import type { ColumnsType } from 'antd/lib/table';
import type { ISourceFormField, IDataColumnsProps, ISourceMapProps } from '@/interface';

const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const RadioGroup = Radio.Group;

type IFormFieldProps = ISourceFormField;
interface ISourceProps {
	/**
	 * 当前源表字段的默认值
	 */
	sourceMap?: ISourceMapProps;
	/**
	 * @requires
	 * 数据源列表
	 */
	dataSourceList: IDataSourceUsedInSyncProps[];
	/**
	 * 是否增量
	 */
	isIncrementMode?: boolean;
	/**
	 * 是否只读，用于预览数据
	 */
	readonly?: boolean;
	/**
	 * 下一步的回调函数
	 */
	onNext?: () => void;
	/**
	 * @deprecated
	 * 成功获取源表的表字段时的回调函数
	 */
	onGetTableCols?: (cols: IDataColumnsProps[]) => void;
	/**
	 * 监听当前表单域改变的回调函数
	 */
	onFormValuesChanged?: (values: IFormFieldProps) => void;
}

interface IPreviewTableProps {
	columns: ColumnsType<Record<string, any>>;
	dataSource: Record<string, string | number | null>[];
}

// 不允许请求 tableList 的数据源
const NOT_REQUEST_TABLE_SOURCE = [
	DATA_SOURCE_ENUM.KINGBASE8,
	DATA_SOURCE_ENUM.S3,
	DATA_SOURCE_ENUM.ADB_FOR_PG,
	DATA_SOURCE_ENUM.OPENTSDB,
	DATA_SOURCE_ENUM.ES7,
	DATA_SOURCE_ENUM.ES,
	DATA_SOURCE_ENUM.ES6,
	DATA_SOURCE_ENUM.HDFS,
	DATA_SOURCE_ENUM.FTP,
];

// 不支持数据预览的数据源
const NOT_PREVIEW_SOURCE = [
	DATA_SOURCE_ENUM.HDFS,
	DATA_SOURCE_ENUM.HBASE,
	DATA_SOURCE_ENUM.FTP,
	DATA_SOURCE_ENUM.OPENTSDB,
	DATA_SOURCE_ENUM.ES7,
	DATA_SOURCE_ENUM.ES,
	DATA_SOURCE_ENUM.ES6,
];

export default function Source({
	sourceMap,
	readonly,
	dataSourceList,
	isIncrementMode = false,
	onNext,
	onGetTableCols,
	onFormValuesChanged,
}: ISourceProps) {
	const [form] = Form.useForm<IFormFieldProps>();
	const [tableListLoading, setTableListLoading] = useState(false);
	const [loading, setLoading] = useState(false);
	// 表名 loading 状态
	const [fetching, setFetching] = useState(false);
	const [previewLoading, setPreviewLoading] = useState(false);
	// 缓存各个不同 sourceId 的 table，防止重复请求
	const [tableList, setTableList] = useState<Record<number, string[]>>({});
	const [copateList, setCopateList] = useState<IDataColumnsProps[]>([]);
	const [schemaList, setSchemaList] = useState<string[]>([]);
	const [incrementColumns, setIncrementCols] = useState<IDataColumnsProps[]>([]);
	const [tablePartitionList, setTablePartitionList] = useState<string[]>([]);
	const [showPreview, setShowPreview] = useState(false);
	const [previewTable, setPreviewTable] = useState<IPreviewTableProps>({
		columns: [],
		dataSource: [],
	});

	/**
	 * 获取表名
	 */
	const getTableList = (sourceId?: number, schema?: string, str?: string) => {
		setTableListLoading(true);
		setFetching(true);
		if (!sourceId) return;
		api.getOfflineTableList({
			sourceId,
			schema,
			isSys: false,
			name: str,
			isRead: true,
		})
			.then((res) => {
				if (res && res.code === 1) {
					setTableList((l) => ({ ...l, [sourceId]: res.data || [] }));
				}
			})
			.finally(() => {
				setTableListLoading(false);
				setFetching(false);
			});
	};

	/**
	 * 获取切分键
	 * @param specificParams 初始化阶段，form 表单未赋值导致无法通过 form 表单复制，则通过该值获取参数
	 */
	const getCopate = (specificParams?: ISourceFormField) => {
		const { table, sourceId, schema } = specificParams || form.getFieldsValue();
		const tableName = Array.isArray(table) ? table[0] : table;
		api.getOfflineColumnForSyncopate({
			sourceId,
			tableName,
			schema,
		}).then((res) => {
			if (res.code === 1) {
				setCopateList(res.data || []);
			}
		});
	};

	const getTableColumn = () => {
		const { sourceId, table, indexType, index, schema } = form.getFieldsValue();
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target) return;
		let tableName: string | undefined = '';
		let querySchema: string | undefined = '';
		// ES 数据源：
		// - tableName 字段取自 indexType,
		// - schema 字段取自 index
		const ES_DATASOURCE = [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7];
		if (ES_DATASOURCE.includes(target.dataTypeCode)) {
			tableName = indexType;
			querySchema = index;
		} else {
			tableName = Array.isArray(table) ? table[0] : table;
			querySchema = schema;
		}

		let canRequest = false;
		const HIGHER_ES_VERSION = [DATA_SOURCE_ENUM.ES7];
		// 高版本的 ES 数据源如 ES7 不存在 indexType 字段，则 tableName 字段必为空
		// 所以数据源类型为 ES7 的时候是否可以请求通过判断 schema 是否存在
		if (HIGHER_ES_VERSION.includes(target.dataTypeCode)) {
			canRequest = !!querySchema;
		} else {
			canRequest = !!tableName;
		}

		if (!canRequest) {
			// 重置切分键
			form.setFieldsValue({
				splitPK: '',
			});
			setLoading(false);
		} else {
			const sourceType = target.dataTypeCode;
			// 获取切分键
			if (isRDB(sourceType) || sourceType === DATA_SOURCE_ENUM.POSTGRESQL) {
				getCopate();
			}

			// Hive，Impala 作为结果表时，需要获取分区字段
			const includePart =
				+sourceType === DATA_SOURCE_ENUM.HIVE1X ||
				+sourceType === DATA_SOURCE_ENUM.HIVE ||
				+sourceType === DATA_SOURCE_ENUM.HIVE3X ||
				+sourceType === DATA_SOURCE_ENUM.SPARKTHRIFT;

			/**
			 * @deprecated remove in next version
			 * The table columns request not longer made there, it should make where it need to be made
			 * Remove it before ensuring there will be no affected to dataSync task
			 */
			setLoading(false);
			return;
			api.getOfflineTableColumn({
				sourceId,
				schema: querySchema,
				tableName,
				isIncludePart: includePart,
			})
				.then((res) => {
					if (res.code === 1) {
						onGetTableCols?.(res.data || []);
					}
				})
				.finally(() => {
					setLoading(false);
				});
		}
	};

	// 获取 hive 分区
	const getHivePartions = ({ sourceId, table } = form.getFieldsValue()) => {
		const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
		if (!target || !table) return;
		const sourceType = target.dataTypeCode;
		// 以下数据源支持 hive 分区
		const ALLOW_REQUEST_HIVE = [
			DATA_SOURCE_ENUM.HIVE,
			DATA_SOURCE_ENUM.HIVE3X,
			DATA_SOURCE_ENUM.SPARKTHRIFT,
			DATA_SOURCE_ENUM.HIVE1X,
		];
		if (ALLOW_REQUEST_HIVE.includes(sourceType)) {
			api.getHivePartitionsForDataSource({
				sourceId,
				tableName: table,
			}).then((res) => {
				setTablePartitionList(res.data || []);
			});
		}
	};

	/**
	 * 支持在 didmount 阶段 form 表单还未赋值的情况下，通过 params 获取参数请求接口
	 */
	const loadIncrementColumn = async (
		rawParams?: Pick<IFormFieldProps, 'sourceId' | 'table' | 'schema'>,
	) => {
		const { sourceId, schema, table } = rawParams || form.getFieldsValue();
		if (!table) return;
		const params = {
			sourceId,
			tableName: table,
			schema,
		};
		const res = await api.getIncrementColumns(params);

		if (res.code === 1) {
			setIncrementCols(res.data || []);
		}
	};

	// 获取 schema
	const getSchemaList = (schema?: string) => {
		const { sourceId } = form.getFieldsValue();
		api.getAllSchemas({
			sourceId,
			schema,
		}).then((res) => {
			if (res.code === 1) {
				setSchemaList(res.data || []);
			}
		});
	};

	const handleFieldChanged = (
		changeValues: Partial<IFormFieldProps>,
		values: IFormFieldProps,
	) => {
		if (changeValues.hasOwnProperty('sourceId')) {
			// reset all fields except sourceId
			form.resetFields(Object.keys(values).filter((key) => key !== 'sourceId'));
		}

		if (changeValues.hasOwnProperty('table')) {
			setLoading(true);

			// 加载表字段
			getTableColumn();
			// 重置分区字段
			form.setFieldsValue({ partition: undefined });
			// 加载分区字段
			getHivePartions();
			// 加载增量模式字段
			if (isIncrementMode) {
				loadIncrementColumn();
				form.resetFields(['increColumn']);
			}
		}

		if (changeValues.hasOwnProperty('schema') && changeValues.schema) {
			getTableList(values.sourceId, changeValues.schema);
			form.setFieldsValue({
				table: undefined,
				increColumn: undefined,
			});
		}

		if (changeValues.hasOwnProperty('index')) {
			const targetSource = dataSourceList.find((d) => d.dataInfoId === values.sourceId);
			if (targetSource && changeValues.index) {
				const LOWER_ES_VERSION = [DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES];
				const isES5orES6 = LOWER_ES_VERSION.includes(targetSource?.dataTypeCode);
				// 低版本的 es 还需要获取 indexType 才可以获取 columns
				if (isES5orES6) {
					getTableList(values.sourceId, changeValues.index);
				} else {
					// 高版本直接获取 columns
					getTableColumn();
				}
			}

			form.setFieldsValue({
				indexType: undefined,
			});
		}

		if (changeValues.hasOwnProperty('indexType') && changeValues.indexType) {
			getTableColumn();
		}

		// It's better to use form.getFieldsValue rather than the values params is for
		// there are some set methods before this function which will lead to an out of date values
		onFormValuesChanged?.(form.getFieldsValue());
	};

	const handleSourceChanged = (value: number) => {
		const targetDataSource = dataSourceList.find((d) => d.dataInfoId === value)!;
		const ALLOW_REQUEST_SCHEMA = [
			DATA_SOURCE_ENUM.KINGBASE8,
			DATA_SOURCE_ENUM.ORACLE,
			DATA_SOURCE_ENUM.POSTGRESQL,
			DATA_SOURCE_ENUM.INFLUXDB,
			DATA_SOURCE_ENUM.ADB_FOR_PG,
			DATA_SOURCE_ENUM.ES6,
			DATA_SOURCE_ENUM.ES,
			DATA_SOURCE_ENUM.ES7,
		];
		if (ALLOW_REQUEST_SCHEMA.includes(targetDataSource.dataTypeCode)) {
			getSchemaList();
		}

		if (!NOT_REQUEST_TABLE_SOURCE.includes(targetDataSource.dataTypeCode)) {
			getTableList(value);
		}

		// hidden preview when data source changed everytime
		setShowPreview(false);

		// Once the source changed, everything would be reset to undefined
		const resetField = {
			table: undefined,
			splitPK: undefined,
			extralConfig: undefined,
			schema: undefined,
			partition: undefined,
			where: undefined,
			index: undefined,
			indexType: undefined,
		};
		form.setFieldsValue(resetField);
		// the series of set methods are async function, so we can get the values after changed
		handleFieldChanged(resetField, form.getFieldsValue());
	};

	const handleLoadPreview = () => {
		const sourceId = form.getFieldValue('sourceId');
		const schema = form.getFieldValue('schema');
		const table = form.getFieldValue('table');

		if (!sourceId || !table) {
			message.error('数据源或表名缺失');
			return;
		}
		const tableName = Array.isArray(table) ? table[0] : table;

		if (!showPreview) {
			setPreviewLoading(true);
			api.getDataSourcePreview({
				sourceId,
				tableName,
				schema,
			})
				.then((res) => {
					if (res.code === 1) {
						const { columnList, dataList } = res.data as {
							columnList: string[];
							dataList: string[][];
						};

						const columns = columnList.map((s) => {
							return {
								title: s,
								dataIndex: s,
								key: s,
								width: 20 + s.length * 10,
								render: (text: string) => {
									return (
										<TableCell
											style={{ textIndent: 'none' }}
											readOnly
											value={text || ''}
										/>
									);
								},
							};
						});
						const dataSource = dataList.map((arr, i) => {
							const o: Record<string, string | number> = {};
							for (let j = 0; j < arr.length; j += 1) {
								o.key = i;
								o[columnList[j]] = arr[j];
							}
							return o;
						});

						setPreviewTable({ columns, dataSource });
						setShowPreview(true);
					}
				})
				.finally(() => {
					setPreviewLoading(false);
				});
		} else {
			setShowPreview(false);
		}
	};

	const validateChineseCharacter = (data: any) => {
		const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
		let has = false;
		const fieldsName: string[] = [];
		if (data.path && reg.test(data.path)) {
			has = true;
			fieldsName.push('路径');
		}
		if (data.fieldDelimiter && reg.test(data.fieldDelimiter)) {
			has = true;
			fieldsName.push('列分隔符');
		}
		if (has) {
			message.warning(`${fieldsName.join('、')}参数中有包含中文或者中文标点符号！`);
		}
	};

	const handleNext = () => {
		form.validateFields().then((values) => {
			// 校验中文字符，如果有则发出警告
			validateChineseCharacter(values);
			onNext?.();
		});
	};

	const validatePath = (_: any, value: string) => {
		const { getFieldValue } = form;
		const sourceId = getFieldValue('sourceId');
		if (getFieldValue('fileType') === 'orc') {
			return api
				.getOfflineTableColumn({
					sourceId,
					tableName: value,
				})
				.then((res) => {
					if (res.code === 1) {
						// handleTableColumnChange(res.data);
						return Promise.resolve();
					}
					return Promise.reject(new Error('该路径无效！'));
				});
		}

		return Promise.resolve();
	};

	const renderIncrementColumns = () => {
		const columnsOpts = incrementColumns.map((o) => (
			<Option key={o.key}>
				{o.key}（{o.type}）
			</Option>
		));
		return isIncrementMode ? (
			<FormItem
				label="增量标识字段"
				name="increColumn"
				rules={[
					{
						required: true,
						message: '必须选择增量标识字段！',
					},
				]}
				tooltip={incrementColumnHelp}
			>
				<Select placeholder="请选择增量标识字段">{columnsOpts}</Select>
			</FormItem>
		) : (
			''
		);
	};

	const renderDynamicForm = (f: Pick<FormInstance, 'getFieldValue'>) => {
		const sourceId = f.getFieldValue('sourceId');
		const targetSource = dataSourceList.find((l) => l.dataInfoId === sourceId);

		switch (targetSource?.dataTypeCode) {
			case DATA_SOURCE_ENUM.MYSQL:
			case DATA_SOURCE_ENUM.SQLSERVER: {
				return (
					<>
						<FormItem
							label={supportSubLibrary ? '表名(批量)' : '表名'}
							name="table"
							rules={[
								{
									required: true,
									message: '数据源表为必选项！',
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								mode={supportSubLibrary ? 'multiple' : undefined}
								showSearch
								showArrow
								optionFilterProp="value"
								filterOption={(input, option) =>
									// @ts-ignore
									option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
								}
								notFoundContent={fetching ? <Spin size="small" /> : null}
							>
								{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
									return (
										<Option key={`rdb-${table}`} value={table}>
											{table}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						{renderIncrementColumns()}
						<FormItem
							tooltip={dataFilterDoc}
							label="数据过滤"
							key="where"
							name="where"
							rules={[
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
								{
									validator: (_, value) => {
										if (!/(‘|’|”|“)/.test(value)) {
											return Promise.resolve();
										}
										return Promise.reject(new Error('当前输入含有中文引号'));
									},
								},
							]}
						>
							<TextArea placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步" />
						</FormItem>
						<FormItem tooltip={selectKey} label="切分键" key="splitPK" name="splitPK">
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								allowClear
							>
								{copateList.map((copateValue) => {
									return (
										<Option key={copateValue.key} value={copateValue.key}>
											{copateValue.key}
										</Option>
									);
								})}
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.ORACLE: {
				return (
					<>
						<FormItem label="schema" name="schema">
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
						<FormItem
							label="表名"
							key="rdbtable"
							name="table"
							rules={[
								{
									required: true,
									message: '数据源表为必选项！',
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								notFoundContent={fetching ? <Spin size="small" /> : null}
								optionFilterProp="value"
								filterOption={(input: any, option: any) => {
									return (
										option.children
											.toLowerCase()
											.indexOf(input.toLowerCase()) >= 0
									);
								}}
							>
								{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
									return (
										<Option key={`rdb-${table}`} value={table}>
											{table}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						{renderIncrementColumns()}
						<FormItem
							tooltip={dataFilterDoc}
							label="数据过滤"
							key="where"
							name="where"
							rules={[
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
								{
									validator: (_, value) => {
										if (!/(‘|’|”|“)/.test(value)) {
											return Promise.resolve();
										}
										return Promise.reject(new Error('当前输入含有中文引号'));
									},
								},
							]}
						>
							<TextArea placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步" />
						</FormItem>
						<FormItem tooltip={selectKey} label="切分键" key="splitPK" name="splitPK">
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								allowClear
							>
								{copateList.map((copateValue) => {
									return (
										<Option
											key={`copate-${copateValue.key}`}
											value={copateValue.key}
										>
											{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
											{copateValue.key === 'ROW_NUMBER()'
												? 'ROW_NUMBER'
												: copateValue.key}
										</Option>
									);
								})}
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.HBASE: {
				return (
					<>
						<FormItem
							name="table"
							label="表名"
							rules={[
								{
									required: true,
									message: '数据源表为必选项！',
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								notFoundContent={fetching ? <Spin size="small" /> : null}
								filterOption={false}
							>
								{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
									return (
										<Option key={`rdb-${table}`} value={table}>
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
						<FormItem name="startRowkey" label="开始行健" key="startRowkey">
							<Input placeholder="startRowkey" />
						</FormItem>
						<FormItem name="endRowkey" label="结束行健" key="endRowkey">
							<Input placeholder="endRowkey" />
						</FormItem>
						<FormItem
							name="isBinaryRowkey"
							label="行健二进制转换"
							key="isBinaryRowkey"
							initialValue={BINARY_ROW_KEY_FLAG.FALSE}
						>
							<RadioGroup>
								<Radio value={BINARY_ROW_KEY_FLAG.FALSE}>FALSE</Radio>
								<Radio value={BINARY_ROW_KEY_FLAG.TRUE}>TRUE</Radio>
							</RadioGroup>
						</FormItem>
						<FormItem
							name="scanCacheSize"
							label="每次RPC请求获取行数"
							key="scanCacheSize"
						>
							<Input
								placeholder="请输入大小, 默认为256"
								type="number"
								min={0}
								suffix="行"
							/>
						</FormItem>
						<FormItem
							name="scanBatchSize"
							label="每次RPC请求获取列数"
							key="scanBatchSize"
						>
							<Input
								placeholder="请输入大小, 默认为100"
								type="number"
								min={0}
								suffix="列"
							/>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.POSTGRESQL: {
				return (
					<>
						<FormItem label="schema" key="schema" name="schema">
							<Select showSearch showArrow allowClear>
								{schemaList.map((copateValue) => {
									return (
										<Option key={`copate-${copateValue}`} value={copateValue}>
											{copateValue}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						<FormItem
							name="table"
							label="表名"
							key="rdbtable"
							rules={[
								{
									required: true,
									message: '数据源表为必选项！',
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								optionFilterProp="value"
							>
								{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
									return (
										<Option key={`rdb-${table}`} value={table}>
											{table}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						{renderIncrementColumns()}
						<FormItem
							tooltip={dataFilterDoc}
							label="数据过滤"
							key="where"
							name="where"
							rules={[
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
								{
									validator: (_, value) => {
										if (!/(‘|’|”|“)/.test(value)) {
											return Promise.resolve();
										}
										return Promise.reject(new Error('当前输入含有中文引号'));
									},
								},
							]}
						>
							<TextArea placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步" />
						</FormItem>
						<FormItem tooltip={selectKey} label="切分键" key="splitPK" name="splitPK">
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								showArrow
								allowClear
							>
								{copateList.map((copateValue) => {
									return (
										<Option
											key={`copate-${copateValue.key}`}
											value={copateValue.key}
										>
											{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
											{copateValue.key === 'ROW_NUMBER()'
												? 'ROW_NUMBER'
												: copateValue.key}
										</Option>
									);
								})}
							</Select>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.HDFS: {
				return (
					<>
						<FormItem
							tooltip={hdfsPath}
							label="路径"
							key="path"
							name="path"
							rules={[
								{
									required: true,
									message: '路径不得为空！',
								},
								{
									max: 200,
									message: '路径不得超过200个字符！',
								},
								{
									validator: validatePath,
								},
							]}
							validateTrigger="onSubmit"
						>
							<Input placeholder="例如: /rdos/batch" />
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
							initialValue="text"
						>
							<Select getPopupContainer={(container) => container.parentNode}>
								<Option value="orc">orc</Option>
								<Option value="text">text</Option>
								<Option value="parquet">parquet</Option>
							</Select>
						</FormItem>
						<FormItem noStyle dependencies={['fileType']}>
							{({ getFieldValue }) => (
								<>
									<FormItem
										style={{
											display:
												getFieldValue('fileType') === 'text'
													? 'flex'
													: 'none',
										}}
										tooltip={splitCharacter}
										label="列分隔符"
										key="fieldDelimiter"
										name="fieldDelimiter"
									>
										<Input placeholder={`若不填写，则默认为\\001`} />
									</FormItem>
									<FormItem
										label="编码"
										key="encoding"
										style={{
											display:
												getFieldValue('fileType') === 'text'
													? 'flex'
													: 'none',
										}}
										name="encoding"
										rules={[
											{
												required: true,
											},
										]}
										initialValue="utf-8"
									>
										<Select
											getPopupContainer={(container) => container.parentNode}
										>
											<Option value="utf-8">utf-8</Option>
											<Option value="gbk">gbk</Option>
										</Select>
									</FormItem>
								</>
							)}
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
						<FormItem
							name="table"
							label="表名"
							key="table"
							rules={[
								{
									required: true,
									message: '数据源表为必选项！',
								},
							]}
						>
							<Select
								getPopupContainer={(container) => container.parentNode}
								showSearch
								optionFilterProp="value"
							>
								{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
									return (
										<Option key={`rdb-${table}`} value={table}>
											{table}
										</Option>
									);
								})}
							</Select>
						</FormItem>
						<FormItem
							tooltip={partitionDesc}
							label="分区"
							key="partition"
							name="partition"
						>
							<AutoComplete
								showSearch
								showArrow
								placeholder="请填写分区信息"
								filterOption={(input: any, option: any) => {
									return (
										option.props.value
											.toLowerCase()
											.indexOf(input.toLowerCase()) >= 0
									);
								}}
							>
								{tablePartitionList.map((pt) => {
									return (
										<AutoComplete.Option key={`rdb-${pt}`} value={pt}>
											{pt}
										</AutoComplete.Option>
									);
								})}
							</AutoComplete>
						</FormItem>
					</>
				);
			}
			case DATA_SOURCE_ENUM.ES:
			case DATA_SOURCE_ENUM.ES6:
			case DATA_SOURCE_ENUM.ES7: {
				const LOWER_ES_VERSION = [DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES];
				const isES5orES6 = LOWER_ES_VERSION.includes(targetSource?.dataTypeCode);
				return (
					<>
						<FormItem
							name="index"
							label="index"
							key="index"
							rules={[
								{
									required: true,
									message: '请选择index！',
								},
							]}
							tooltip={es7Index}
						>
							<Select
								style={{ width: '100%' }}
								placeholder="请选择index"
								showSearch
								allowClear
								filterOption={(input, option) =>
									!!option?.value
										?.toString()
										.toLowerCase()
										.includes(input.toLowerCase())
								}
							>
								{schemaList?.map((item) => (
									<Option key={item} value={item}>
										{item}
									</Option>
								))}
							</Select>
						</FormItem>
						{isES5orES6 && (
							<FormItem
								name="indexType"
								label="type"
								key="indexType"
								rules={[
									{
										required: true,
										message: '请选择indexType！',
									},
								]}
								tooltip={indexTypeDoc}
							>
								<Select
									style={{ width: '100%' }}
									placeholder="请选择indexType！"
									showSearch
									allowClear
								>
									{(tableList[f.getFieldValue('sourceId')] || []).map((table) => {
										return (
											<Option key={table} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>
							</FormItem>
						)}
						<FormItem
							name="query"
							label="query"
							key="query"
							rules={[
								{
									max: 1024,
									message: '仅支持1-1024个任意字符',
								},
							]}
							tooltip={es7Query}
						>
							<Input.TextArea placeholder='"match_all":{}' />
						</FormItem>
					</>
				);
			}
			default:
				return null;
		}
	};

	useEffect(() => {
		if (sourceMap?.sourceId) {
			if (!NOT_REQUEST_TABLE_SOURCE.includes(sourceMap.type!)) {
				getTableList(sourceMap.sourceId, sourceMap.schema);
			}

			const sourceType = sourceMap.type;
			// 获取切分键
			if (isRDB(sourceType) || sourceType === DATA_SOURCE_ENUM.POSTGRESQL) {
				getCopate(sourceMap);
			}

			if (sourceMap.increColumn) {
				loadIncrementColumn({
					sourceId: sourceMap.sourceId,
					table: sourceMap.table as string,
					schema: sourceMap.schema,
				});
			}

			if (sourceMap.table) {
				getHivePartions({
					sourceId: sourceMap.sourceId,
					table: sourceMap.table,
				});
			}
		}
	}, []);

	// 非增量模式
	const supportSubLibrary =
		SUPPROT_SUB_LIBRARY_DB_ARRAY.indexOf(sourceMap?.sourceList?.[0]?.type || -1) > -1 &&
		!isIncrementMode;

	const initialValues = useMemo<IFormFieldProps | undefined>(() => {
		if (sourceMap) {
			return {
				sourceId: sourceMap.sourceId,
				table: supportSubLibrary ? sourceMap.sourceList?.[0].tables : sourceMap.table,
				increColumn: sourceMap.increColumn,
				where: sourceMap.where,
				splitPK: sourceMap.splitPK,
				schema: sourceMap.schema,
				partition: sourceMap.partition,
				path: sourceMap.path,
				fileType: sourceMap.fileType,
				fieldDelimiter: sourceMap.fieldDelimiter,
				encoding: sourceMap.encoding,
				extralConfig: sourceMap.extralConfig,
				startRowkey: sourceMap.startRowkey,
				endRowkey: sourceMap.endRowkey,
				isBinaryRowkey: sourceMap.isBinaryRowkey,
				scanCacheSize: sourceMap.scanCacheSize,
				index: sourceMap.index,
				indexType: sourceMap.indexType,
				query: sourceMap.query,
			};
		}

		return undefined;
	}, [supportSubLibrary]);

	return (
		<Spin spinning={tableListLoading}>
			<Form<IFormFieldProps>
				form={form}
				{...formItemLayout}
				initialValues={initialValues}
				onValuesChange={handleFieldChanged}
			>
				<FormItem
					label="数据源"
					name="sourceId"
					rules={[
						{
							required: true,
							message: '数据源为必填项',
						},
					]}
				>
					<Select<number>
						getPopupContainer={(node) => node.parentNode}
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

							const disableSelect =
								!tmpSupportDataSource.includes(src.dataTypeCode) ||
								// 增量模式需要禁用非关系型数据库
								(isIncrementMode && !isRDB(src.dataTypeCode));

							return (
								<Option
									key={src.dataInfoId}
									name={src.dataName}
									value={src.dataInfoId}
									disabled={disableSelect}
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
						typeof getFieldValue('sourceId') !== 'undefined' && (
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
				<FormItem noStyle dependencies={['sourceId']} hidden={readonly}>
					{({ getFieldValue }) => {
						const target = dataSourceList.find(
							(l) => l.dataInfoId === getFieldValue('sourceId'),
						);
						return (
							<Row>
								<Col span={24} className="text-center">
									<Button
										type="link"
										disabled={
											!getFieldValue('sourceId') ||
											!target ||
											NOT_PREVIEW_SOURCE.includes(target.dataTypeCode)
										}
										onClick={handleLoadPreview}
										loading={previewLoading}
									>
										数据预览
										{showPreview ? <UpOutlined /> : <DownOutlined />}
									</Button>
								</Col>
								{showPreview && (
									<Col span={24}>
										<Table
											dataSource={previewTable.dataSource}
											columns={previewTable.columns}
											scroll={{
												x: previewTable.columns.reduce((a, b) => {
													return a + (b.width as number);
												}, 0),
											}}
											pagination={false}
											bordered={false}
										/>
									</Col>
								)}
							</Row>
						);
					}}
				</FormItem>
			</Form>
			{!readonly && (
				<div className="text-center">
					<Button type="primary" onClick={handleNext} loading={loading}>
						下一步
					</Button>
				</div>
			)}
		</Spin>
	);
}
