import {
	DATA_SYNC_MODE,
	CREATE_MODEL_TYPE,
	rdbmsDaType,
	TASK_TYPE_ENUM,
	FLINK_VERSIONS,
	SOURCE_TIME_TYPE,
	DATA_SOURCE_ENUM,
} from '@/constant';
import type { IFlinkSinkProps, IFlinkSourceProps, IOfflineTaskProps } from '@/interface';
import molecule from '@dtinsight/molecule';
import api from '@/api';
import { cloneDeep, isEmpty } from 'lodash';
import { message } from 'antd';
import type { Rules, RuleType, ValidateError } from 'async-validator';
import ValidSchema from 'async-validator';
import {
	havePartition,
	havePrimaryKey,
	haveTableColumn,
	haveTableList,
	haveTopic,
	isAvro,
	isHbase,
	isKafka,
	isLowerES,
	isRedis,
	isS3,
} from './enums';
import { isRDB } from '.';
import stream from '@/api/stream';

interface IParamsProps extends IOfflineTaskProps {
	// 接口要求的标记位
	preSave?: true;
	// 接口要求的标记位
	updateSource?: boolean;
	/**
	 * 离线任务的上下游依赖数组
	 */
	dependencyTasks?: IOfflineTaskProps['taskVOS'];
	/**
	 * the monaco editor content
	 */
	value?: string;
}

/**
 * 保存当前任务
 */
export default function saveTask() {
	const currentTask = molecule.editor.getState().current?.tab;
	if (!currentTask) return Promise.reject();
	const data = currentTask.data as IOfflineTaskProps;
	const { taskType } = data;
	switch (taskType) {
		case TASK_TYPE_ENUM.SPARK_SQL:
		case TASK_TYPE_ENUM.HIVE_SQL: {
			const params: IParamsProps = cloneDeep(data);
			// 修改task配置时接口要求的标记位
			params.preSave = true;

			// 接口要求上游任务字段名修改为dependencyTasks
			if (params.taskVOS) {
				params.dependencyTasks = params.taskVOS.map((o) => o);
				Reflect.deleteProperty(params, 'taskVOS');
			}
			params.sqlText = params.value || '';

			return api.saveOfflineJobData(params).then((res) => {
				if (res.code === 1) {
					message.success('保存成功！');
					return res;
				}
				return Promise.reject();
			});
		}
		case TASK_TYPE_ENUM.SYNC: {
			const params: IParamsProps = cloneDeep(data);
			const DATASYNC_FIELDS = ['settingMap', 'sourceMap', 'targetMap'] as const;
			if (DATASYNC_FIELDS.every((f) => params.hasOwnProperty(f) && params[f])) {
				const isIncrementMode =
					params.sourceMap.syncModel !== undefined &&
					DATA_SYNC_MODE.INCREMENT === params.sourceMap.syncModel;
				if (!isIncrementMode) {
					params.sourceMap!.increColumn = undefined; // Delete increColumn
				}

				// 服务端需要的参数
				params.sourceMap!.rdbmsDaType = rdbmsDaType.Poll;
			} else {
				return Promise.reject(new Error('请检查数据同步任务是否填写正确'));
			}

			// 修改task配置时接口要求的标记位
			params.preSave = true;

			// 接口要求上游任务字段名修改为dependencyTasks
			if (params.taskVOS) {
				params.dependencyTasks = params.taskVOS.map((o) => o);
				Reflect.deleteProperty(params, 'taskVOS');
			}
			params.sqlText = params.value || '';

			return api.saveOfflineJobData(params).then((res) => {
				if (res.code === 1) {
					message.success('保存成功！');
					return res;
				}
				return Promise.reject();
			});
		}
		case TASK_TYPE_ENUM.SQL: {
			const params: IParamsProps = cloneDeep(data);
			const { componentVersion, createModel, side = [] } = params;
			const isFlinkSQLGuide = createModel === CREATE_MODEL_TYPE.GUIDE || !createModel;

			/**
			 * 校验源表和结果表和维表
			 */
			if (isFlinkSQLGuide) {
				// errors 的二维数组，第一维区分源表结果表维表，第二维区分具体表中的某一个源
				return validTableData(params)
					.then((errors) => {
						// 如果所有的结果都是 null 则表示校验全通过,否则不通过
						if (!errors.every((tableErrors) => tableErrors.every((e) => e === null))) {
							return Promise.reject();
						}

						const err = checkSide(side, componentVersion);
						if (err) {
							message.error(err);
							return Promise.reject();
						}

						params.preSave = true;
						params.sqlText = params.value || '';
						// 后端区分右键编辑保存
						params.updateSource = true;

						return params;
					})
					.then((preParams) => {
						const { sink = [] } = preParams;
						// 对结果表中多余参数进行处理
						if (Array.isArray(sink)) {
							// sink.forEach((pane) => {
							// 	if ('havePartitionfields' in pane) {
							// 		delete pane.havePartitionfields;
							// 	}
							// });
						}
						// 1.12 时时间特征勾选了 ProcTime，ProcTime 名称字段未填写时需补上 proc_time
						if (
							preParams.componentVersion === FLINK_VERSIONS.FLINK_1_12 &&
							Array.isArray(preParams.source)
						) {
							preParams.source.forEach((form) => {
								if (form.timeTypeArr.includes(1)) {
									// eslint-disable-next-line no-param-reassign
									form.procTime = form.procTime || 'proc_time';
								}
							});
						}

						return preParams;
					})
					.then((realParams) => {
						return stream.saveTask(realParams).then((res) => {
							if (res.code === 1) {
								message.success('保存成功！');
								return res;
							}
							return Promise.reject();
						});
					});
			}
			return Promise.reject();
		}
		case TASK_TYPE_ENUM.DATA_ACQUISITION: {
			const params: IParamsProps = cloneDeep(data);
			// TODO：实时采集任务 dataSourceList 移除
			// @ts-ignore
			const { sourceMap, targetMap = {}, createModel, dataSourceList } = params;
			/**
			 * 当目标数据源为Hive时，必须勾选Json平铺
			 */
			const haveJson =
				isKafka(sourceMap?.type) ||
				sourceMap?.type === DATA_SOURCE_ENUM.EMQ ||
				sourceMap?.type === DATA_SOURCE_ENUM.SOCKET;
			const index = dataSourceList.findIndex((v: any) => v.id === targetMap?.sourceId);
			if (
				index > -1 &&
				dataSourceList[index].type === DATA_SOURCE_ENUM.HIVE &&
				!sourceMap.pavingData &&
				!haveJson
			) {
				message.error('请勾选嵌套Json平铺后重试');
				return Promise.reject();
			}

			params.preSave = true;
			// 后端区分右键编辑保存
			params.updateSource = true;
			params.sqlText = params.value || '';

			if (createModel === CREATE_MODEL_TYPE.GUIDE) {
				const { distributeTable } = sourceMap;
				/**
				 * [ {name:'table', table: []} ] => {'table':[]}
				 */
				if (distributeTable && distributeTable.length) {
					const newDistributeTable: any = {};
					distributeTable.forEach((table: any) => {
						newDistributeTable[table.name] = table.tables || [];
					});
					params.sourceMap = {
						...sourceMap,
						distributeTable: newDistributeTable,
					};
				}

				Reflect.deleteProperty(params, 'sourceParams');
				Reflect.deleteProperty(params, 'sinkParams');
				Reflect.deleteProperty(params, 'sideParams');
			}

			return stream.saveTask(params).then((res) => {
				if (res.code === 1) {
					message.success('保存成功！');
					return res;
				}
				return Promise.reject();
			});
		}

		default:
			return Promise.reject();
	}
}

const checkSide = (sides: IOfflineTaskProps['side'], componentVersion: string) => {
	if (sides) {
		for (let i = 0; i < sides.length; i += 1) {
			const side = sides[i];
			const { type, primaryKey, hbasePrimaryKey, hbasePrimaryKeyType } = side;
			switch (type) {
				case DATA_SOURCE_ENUM.REDIS:
				case DATA_SOURCE_ENUM.UPRedis: {
					if (!primaryKey || !primaryKey.length) {
						return `维表${i + 1}中的主键不能为空`;
					}
					return null;
				}
				case DATA_SOURCE_ENUM.HBASE:
				case DATA_SOURCE_ENUM.TBDS_HBASE:
				case DATA_SOURCE_ENUM.HBASE_HUAWEI: {
					if (!hbasePrimaryKey) {
						return `维表${i + 1}中的主键不能为空`;
					}
					if (!hbasePrimaryKeyType && componentVersion === '1.12') {
						return `维表${i + 1}中的主键类型不能为空`;
					}
					return null;
				}
				default:
					return null;
			}
		}
	}
	return null;
};

const validTableData = async (currentPage: IOfflineTaskProps) => {
	const VALID_FIELDS = ['source', 'sink', 'side'] as const;
	const FIELDS_MAPPING = { source: '源表', sink: '结果表', side: '维表' } as const;
	const FIELDS_VALID_FUNCTION_MAPPING = {
		source: validDataSource,
		sink: validDataOutput,
		side: validDataSide,
	};
	return Promise.all(
		VALID_FIELDS.map((key) => {
			const tableData = currentPage[key];
			return dataValidator(
				currentPage,
				tableData,
				FIELDS_VALID_FUNCTION_MAPPING[key],
				FIELDS_MAPPING[key],
			);
		}),
	);
};

/**
 * 校验器，用于发起校验以及校验结束后提示错误信息
 */
export async function dataValidator<T extends any[]>(
	currentPage: IOfflineTaskProps,
	data: T,
	validator: (
		item: T[number],
		version: IOfflineTaskProps['componentVersion'],
	) => Promise<ValidateError[] | null>,
	text: string,
) {
	const { componentVersion } = currentPage;
	const errors = await Promise.all(data.map((item) => validator(item, componentVersion)));
	errors.forEach((error, index) => {
		if (error) {
			console.error(error);
			const tableName = data[index]?.tableName;
			message.error(
				`${text} ${index + 1} ${tableName ? `(${tableName})` : ''}: ${error[0].message}`,
			);
		}
	});
	return errors;
}

/**
 * 校验 Flink 的源表表单值
 */
const validDataSource = async (data: IFlinkSourceProps, componentVersion?: string) => {
	const validDes = generateValidDesSource(data, componentVersion);
	const validator = new ValidSchema(validDes);
	const err = await new Promise<ValidateError[] | null>((resolve) => {
		validator.validate(data, (errors) => {
			resolve(errors);
		});
	});
	return err;
};

/**
 * 为 Flink 的源表表单生成校验规则
 */
const generateValidDesSource = (
	data: IOfflineTaskProps['source'][number],
	componentVersion?: string,
): Rules => {
	const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;
	const haveSchema =
		isKafka(data?.type) &&
		isAvro(data?.sourceDataType) &&
		componentVersion !== FLINK_VERSIONS.FLINK_1_12;

	return {
		type: [{ required: true, message: '请选择类型' }],
		sourceId: [{ required: true, message: '请选择数据源' }],
		topic: [{ required: true, message: '请选择Topic' }],
		table: [{ required: true, message: '请输入映射表名' }],
		columnsText: [{ required: true, message: '字段信息不能为空！' }],
		sourceDataType: [{ required: isKafka(data?.type), message: '请选择读取类型' }],
		schemaInfo: [{ required: !!haveSchema, message: '请输入Schema' }],
		timeColumn: [
			{
				required:
					(!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
					(isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
				message: '请选择时间列',
			},
		],
		offset: [
			{
				required:
					(!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
					(isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
				message: '请输入最大延迟时间',
			},
		],
	};
};

/**
 * 校验 Flink 的结果表
 */
const validDataOutput = async (data: IFlinkSinkProps, componentVersion?: string) => {
	const validDes = generateValidDesOutPut(data, componentVersion);
	const validator = new ValidSchema(validDes);
	const err = await new Promise<ValidateError[] | null>((resolve) => {
		validator.validate(data, (errors) => {
			resolve(errors);
		});
	});
	return err;
};

/**
 * 动态生成 Flink 结果表的校验规则
 */
const generateValidDesOutPut = (data?: IFlinkSinkProps, componentVersion?: string): Rules => {
	const schemaRequired =
		data?.type &&
		[
			DATA_SOURCE_ENUM.POSTGRESQL,
			DATA_SOURCE_ENUM.KINGBASE8,
			DATA_SOURCE_ENUM.SQLSERVER,
			DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
		].includes(data.type);
	const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;

	return {
		type: [{ required: true, message: '请选择存储类型' }],
		sourceId: [{ required: true, message: '请选择数据源' }],
		topic: [{ required: haveTopic(data?.type), message: '请选择Topic' }],
		table: [{ required: haveTableList(data?.type) && !isS3(data?.type), message: '请选择表' }],
		tableName: [{ required: true, message: '请输入映射表名' }],
		columns: [
			{
				required: haveTableColumn(data?.type),
				message: '字段信息不能为空',
				type: 'array' as RuleType,
			},
			{ validator: checkColumnsData },
		],
		columnsText: [{ required: !haveTableColumn(data?.type), message: '字段信息不能为空' }],
		collection: [
			{ required: data?.type === DATA_SOURCE_ENUM.SOLR, message: '请选择Collection' },
		],
		objectName: [{ required: isS3(data?.type), message: '请输入ObjectName' }],
		schema: [{ required: schemaRequired, message: '请选择schema' }],
		partitionfields: [
			{
				required:
					// @ts-ignore
					havePartition(data?.type) && data?.isShowPartition && data?.havePartitionfields,
				message: '请选择分区',
			},
		],
		'table-input': [{ required: isRedis(data?.type), message: '请输入表名' }],
		index: [{ required: isLowerES(data?.type), message: '请输入索引' }],
		'primaryKey-input': [{ required: isRedis(data?.type), message: '请输入主键' }],
		esType: [{ required: isLowerES(data?.type), message: '请输入索引类型' }],
		rowKey: [{ required: isHbase(data?.type), message: '请输入rowKey' }],
		rowKeyType: [{ required: isHbase(data?.type) && isFlink112, message: '请输入rowKey类型' }],
		sinkDataType: [{ required: isKafka(data?.type), message: '请选择输出类型！' }],
		updateMode: [{ required: true, message: '请选择更新模式' }],
		primaryKey: [
			{
				required: data?.updateMode === 'upsert' && havePrimaryKey(data?.type),
				message: '请输入主键',
			},
		],
		partitionKeys: [{ required: data?.enableKeyPartitions, message: '请选择分区字段' }],
		batchWaitInterval: [{ required: isRDB(data?.type), message: '请输入数据输出时间' }],
		batchSize: [{ required: isRDB(data?.type), message: '请输入数据输出条数' }],
	};
};

// 校验字段信息
function checkColumnsData(rule: any, value: any, callback: any, source: any) {
	if (haveTableColumn(source?.type)) {
		if (isEmpty(value) || value?.some((item: any) => isEmpty(item))) {
			const err = '请填写字段信息';
			return callback(err);
		}
		if (
			value?.some(
				(item: { type: string }) =>
					item.type?.toLowerCase() === 'Not Support'.toLowerCase(),
			)
		) {
			const err = '字段中存在不支持的类型，请重新填写';
			return callback(err);
		}
	}
	callback();
}

/**
 * 校验 Flink 维表
 */
const validDataSide = async (data: IOfflineTaskProps['side'], componentVersion?: string) => {
	const validDes = generateValidDesSide(data, componentVersion);
	const validator = new ValidSchema(validDes);
	const err = await new Promise<ValidateError[] | null>((resolve) => {
		validator.validate(data, (errors) => {
			resolve(errors);
		});
	});
	return err;
};

/**
 * 动态生成 Flink 的维表校验字段
 */
export const generateValidDesSide = (
	data: IOfflineTaskProps['side'][number],
	componentVersion?: string,
): Rules => {
	const isCacheLRU = data?.cache === 'LRU';
	const isCacheTLLMSReqiured = data?.cache === 'LRU' || data?.cache === 'ALL';
	const schemaRequired = [
		DATA_SOURCE_ENUM.POSTGRESQL,
		DATA_SOURCE_ENUM.KINGBASE8,
		DATA_SOURCE_ENUM.SQLSERVER,
		DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
	].includes(data?.type);
	const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;

	return {
		type: [{ required: true, message: '请选择存储类型' }],
		sourceId: [{ required: true, message: '请选择数据源' }],
		table: [{ required: haveTableList(data?.type), message: '请选择表' }],
		tableName: [{ required: true, message: '请输入映射表名' }],
		columns: [
			{
				required: haveTableColumn(data?.type),
				message: '字段信息不能为空',
				type: 'array',
			},
			{ validator: checkColumnsData },
		],
		columnsText: [{ required: !haveTableColumn(data?.type), message: '字段信息不能为空' }],
		schema: [{ required: schemaRequired, message: '请选择Schema' }],
		// 'table-input': [{ required: isRedis, message: '请输入表名' }],
		index: [{ required: isLowerES(data?.type), message: '请输入索引' }],
		esType: [{ required: isLowerES(data?.type), message: '请输入索引类型' }],
		primaryKey: [{ required: false, message: '请输入主键' }],
		// 'primaryKey-input': [{ required: isRedis || isMongoDB, message: '请输入主键' }],
		hbasePrimaryKey: [{ required: isHbase(data?.type), message: '请输入主键' }],
		hbasePrimaryKeyType: [
			{ required: isHbase(data?.type) && isFlink112, message: '请输入主键类型' },
		],
		cache: [{ required: true, message: '请选择缓存策略' }],
		cacheSize: [{ required: isCacheLRU, message: '请输入缓存大小' }],
		cacheTTLMs: [{ required: isCacheTLLMSReqiured, message: '请输入缓存超时时间' }],
	};
};
