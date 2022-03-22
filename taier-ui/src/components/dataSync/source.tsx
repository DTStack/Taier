import { API } from '@/api/dataSource';
import api from '@/api';
import {
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	formItemLayout,
	SUPPROT_SUB_LIBRARY_DB_ARRAY,
} from '@/constant';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import { filterValueOption, formJsonValidator, isRDB } from '@/utils';
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
} from 'antd';
import type { FormInstance } from 'rc-field-form';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import { useMemo, useState } from 'react';
import {
	dataFilterDoc,
	dataSyncExtralConfigHelp,
	hdfsPath,
	incrementColumnHelp,
	partitionDesc,
	selectKey,
	splitCharacter,
} from '../helpDoc/docs';
import TableCell from '../tableCell';
import type { ColumnsType } from 'antd/lib/table';
import type { ISourceFormField, IDataColumnsProps, ISourceMapProps } from './interface';

const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;

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
	const getTableList = (sourceId?: number, schema?: any, str?: any) => {
		setTableListLoading(true);
		setFetching(true);
		if (!sourceId) return;
		API.getOfflineTableList({
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

	// 获取切分键
	const getCopate = () => {
		const { table, sourceId, schema } = form.getFieldsValue();
		const tableName = Array.isArray(table) ? table[0] : table;
		API.getOfflineColumnForSyncopate({
			sourceId,
			tableName,
			schema,
		}).then((res) => {
			if (res.code === 1) {
				setCopateList(res.data || []);
			}
		});
	};

	// 获取表字段
	const getTableColumn = () => {
		const { sourceId, table, schema } = form.getFieldsValue();
		const tableName = Array.isArray(table) ? table[0] : table;
		if (!tableName) {
			// 重置切分键
			form.setFieldsValue({
				splitPK: '',
			});
			setLoading(false);
		} else {
			const target = dataSourceList.find((l) => l.dataInfoId === sourceId);
			if (!target) return;
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

			// 获取表字段为第三步做准备
			API.getOfflineTableColumn({
				sourceId,
				schema,
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
	const getHivePartions = () => {
		const { sourceId, table } = form.getFieldsValue();
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
			// Reset partition
			form.setFieldsValue({ partition: undefined });
			API.getHivePartitions({
				sourceId,
				tableName: table,
			}).then((res) => {
				setTablePartitionList(res.data || []);
			});
		}
	};

	const loadIncrementColumn = async () => {
		const { sourceId, schema, table } = form.getFieldsValue();
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
		API.getAllSchemas({
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
		if (changeValues.hasOwnProperty('table')) {
			setLoading(true);

			// 加载表字段
			getTableColumn();
			// 加载分区字段
			getHivePartions();
			// 加载增量模式字段
			if (isIncrementMode) {
				loadIncrementColumn();
			}
		}

		if (changeValues.hasOwnProperty('schema') && changeValues.schema) {
			getTableList(values.sourceId, changeValues.schema);
			form.setFieldsValue({
				table: undefined,
				increColumn: undefined,
			});
		}

		// It's better to use form.getFieldsValue rather than the values params is for
		// there are some set methods before this function which will lead to an out of date values
		onFormValuesChanged?.(form.getFieldsValue());
	};

	const handleSourceChanged = (value: number) => {
		const targetDataSource = dataSourceList.find((d) => d.dataInfoId === value)!;
		// KINGBASE/ORACLE需要加schema字段
		const ALLOW_REQUEST_SCHEMA = [DATA_SOURCE_ENUM.ORACLE, DATA_SOURCE_ENUM.POSTGRESQL];
		if (ALLOW_REQUEST_SCHEMA.includes(targetDataSource.dataTypeCode)) {
			getSchemaList();
		}

		const NOT_REQUEST_TABLE_SOURCE = [
			DATA_SOURCE_ENUM.KINGBASE8,
			DATA_SOURCE_ENUM.S3,
			DATA_SOURCE_ENUM.ADB_FOR_PG,
			DATA_SOURCE_ENUM.HDFS,
		];

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
			API.getDataPreview({
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
			return API.getOfflineTableColumn({
				sourceId,
				tableName: value,
			}).then((res) => {
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

	const renderDynamicForm = (f: FormInstance) => {
		const sourceId = f.getFieldValue('sourceId');
		const targetSource = dataSourceList.find((l) => l.dataInfoId === sourceId);

		switch (targetSource?.dataTypeCode) {
			case DATA_SOURCE_ENUM.MYSQL: {
				return (
					<>
						<FormItem
							label="表名(批量)"
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
								mode="multiple"
								showSearch
								showArrow
								optionFilterProp="value"
								filterOption={false}
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
								mode="multiple"
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
								filterOption={filterValueOption}
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
			default:
				return null;
		}
	};

	const initialValues = useMemo<IFormFieldProps | undefined>(() => {
		if (sourceMap) {
			// 非增量模式
			const supportSubLibrary =
				SUPPROT_SUB_LIBRARY_DB_ARRAY.indexOf(sourceMap?.sourceList?.[0]?.type || -1) > -1 &&
				!isIncrementMode;
			return {
				sourceId: sourceMap.sourceId,
				table: supportSubLibrary ? sourceMap.sourceList?.[0].tables : sourceMap.type?.table,
				where: sourceMap.type?.where,
				splitPK: sourceMap.type?.splitPK,
				schema: sourceMap.schema || sourceMap.type?.schema,
				partition: sourceMap.type?.partition,
				path: sourceMap.type?.path,
				fileType: sourceMap.type?.fileType,
				fieldDelimiter: sourceMap.type?.fieldDelimiter,
				encoding: sourceMap.type?.encoding,
				extralConfig: sourceMap.extralConfig,
			};
		}

		return undefined;
	}, []);

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
								DATA_SOURCE_ENUM.HDFS,
								DATA_SOURCE_ENUM.HIVE,
								DATA_SOURCE_ENUM.HIVE1X,
								DATA_SOURCE_ENUM.HIVE3X,
								DATA_SOURCE_ENUM.SPARKTHRIFT,
							];

							const disableSelect =
								!tmpSupportDataSource.includes(src.dataTypeCode) ||
								src.dataTypeCode === DATA_SOURCE_ENUM.ES ||
								src.dataTypeCode === DATA_SOURCE_ENUM.REDIS ||
								src.dataTypeCode === DATA_SOURCE_ENUM.MONGODB ||
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
											target?.dataTypeCode === DATA_SOURCE_ENUM.HDFS
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
