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

import type {
	BINARY_ROW_KEY_FLAG,
	CODE_TYPE,
	DATA_SOURCE_ENUM,
	DATA_SYNC_TYPE,
	MENU_TYPE_ENUM,
	PARAMS_ENUM,
	RESOURCE_TYPE,
	SCHEDULE_DEPENDENCY,
	SCHEDULE_STATUS,
	SOURCE_TIME_TYPE,
	TASK_PERIOD_ENUM,
	TASK_STATUS,
	TASK_TYPE_ENUM,
} from './constant';

interface IUserProps {}

export interface IResponseProps<T = any> {
	code: number;
	data: null | T;
	/**
	 * 请求异常返回的信息
	 */
	message: null | string;
	success: boolean;
}

/**
 * 目录数据结果
 */
export interface CatalogueDataProps {
	id: number;
	type: string;
	taskType: number;
	resourceType: RESOURCE_TYPE;
	name: string;
	children: CatalogueDataProps[] | null;
	catalogueType: MENU_TYPE_ENUM;
	parentId: number;
}

// 所有任务相关的类型都必要的属性
export interface ITaskBasicProps {
	taskId: number;
	name: string;
}

// 运维中心-任务类型，@todo 应该是 Job
export interface ITaskProps extends ITaskBasicProps {
	gmtModified: number;
	ownerUserId: number;
	ownerUserName: null | string;
	periodType: TASK_PERIOD_ENUM;
	scheduleStatus: SCHEDULE_STATUS;
	taskType: TASK_TYPE_ENUM;
}

// 查询任务树的遍历方向
export enum DIRECT_TYPE_ENUM {
	FATHER = 1,
	CHILD = 2,
}

// 任务上下游依赖类型
export interface ITaskStreamProps {
	taskId: number;
	taskName: string;
	taskType: TASK_TYPE_ENUM;
	scheduleStatus: SCHEDULE_STATUS;
	taskPeriodId: TASK_PERIOD_ENUM;
	gmtCreate: number;
	isFlowTask: boolean;
	jobId: string;
	tenantId: number;
	tenantName: string;
	/**
	 * 计划时间
	 */
	cycTime: string;
	operatorId: number;
	/**
	 * 操作人名称
	 */
	operatorName: string;
	status: TASK_STATUS;
	parentNode: ITaskStreamProps[];
	childNode: ITaskStreamProps[];
}

/**
 * 任务开发-资源类型
 */
export interface IResourceProps {
	createUserId: number;
	gmtCreate: number;
	gmtModified: number;
	id: number;
	modifyUser: null | string;
	modifyUserId: number;
	/**
	 * 资源所在的目录 id
	 */
	nodePid: number;
	originFileName: string;
	resourceDesc: string;
	resourceName: string;
	resourceType: RESOURCE_TYPE;
	tenantId: number;
	url: string;
}

/**
 * 所有任务类型
 */
export interface IOfflineTaskProps extends ISyncDataProps, IFlinkDataProps {
	createUserId: number;
	cron: string;
	currentProject: boolean;
	dtuicTenantId: number;
	forceUpdate: boolean;
	gmtCreate: number;
	gmtModified: number;
	id: number;
	modifyUser: IUserProps | null;
	modifyUserId: number;
	name: string;
	nodePName: string;
	nodePid: number;
	ownerUserId: number;
	scheduleConf: string;
	scheduleStatus: SCHEDULE_STATUS;
	/**
	 * 数据同步任务配置模式
	 */
	createModel: Valueof<typeof DATA_SYNC_TYPE>;
	/**
	 * 是否是增量同步模式
	 */
	syncModel: number;
	sqlText: string;
	taskDesc: string;
	taskParams: string;
	taskPeriodId: TASK_PERIOD_ENUM;
	taskPeriodType: string;
	taskType: TASK_TYPE_ENUM;
	taskVOS: null | ITaskVOProps[];
	taskVariables: null | ITaskVariableProps[];
	tenantId: string | null;
	tenantName: string | null;
	userId: number;
}

/**
 * 数据同步任务类型
 */
export interface ISyncDataProps {
	settingMap?: IChannelFormProps;
	sourceMap?: ISourceMapProps;
	targetMap?: ITargetMapProps;
	taskId: number;
}

/**
 * 数据同步任务前端表单域类型
 */
export interface ITargetFormField {
	sourceId?: number;
	table?: string;
	preSql?: string;
	postSql?: string;
	schema?: string;
	extralConfig?: string;
	partition?: string;
	path?: string;
	fileName?: string;
	fileType?: 'orc' | 'text' | 'parquet';
	fieldDelimiter?: string;
	encoding?: 'utf-8' | 'gbk';
	writeMode?: 'NONCONFLICT' | 'APPEND' | 'insert' | 'replace' | 'update';
	nullMode?: 'skip' | 'empty';
	writeBufferSize?: number;
	index?: string;
	indexType?: string;
	bulkAction?: number;
}

/**
 * 数据同步任务 TargetMap
 */
export interface ITargetMapProps extends ITargetFormField {
	name?: string;
	column?: IDataColumnsProps[];
	rowkey?: string;
	type?: DATA_SOURCE_ENUM;
}

/**
 * 数据同步任务通道传输类型
 */
export interface IChannelFormProps {
	speed: string;
	channel: string;
	record?: number;
	percentage?: number;
	isRestore?: boolean;
	isSaveDirty?: boolean;
	tableName?: string;
	lifeDay?: string | number;
	restoreColumnName?: string | number;
}

/**
 * 数据同步任务 SourceMap 类型
 */
export interface ISourceMapProps extends ISourceFormField {
	name?: string;
	sourceId?: number;
	/**
	 * 数据同步任务「字段映射」
	 */
	column?: IDataColumnsProps[];
	sourceList?: {
		key: string;
		tables?: string[] | string;
		type: DATA_SOURCE_ENUM;
		name: string;
		sourceId?: number;
	}[];
	type?: DATA_SOURCE_ENUM;

	[key: string]: any;
}

/**
 * 数据库表字段信息
 */
export interface IDataColumnsProps {
	comment?: string;
	isPart?: boolean;
	key: string | number;
	part?: boolean;
	type: string;
	value?: string;
	index?: string;
	cf?: string;
	format?: string;
}

/**
 * 前端表单保存的值
 */
export interface ISourceFormField {
	sourceId?: number;
	table?: string | string[];
	/**
	 * Only used in Oracle and PostgreSQL
	 */
	schema?: string;
	where?: string;
	splitPK?: string;
	extralConfig?: string;
	increColumn?: string | number;
	/**
	 * Only used in HDFS
	 */
	path?: string;
	/**
	 * Only used in HDFS
	 */
	fileType?: 'orc' | 'text' | 'parquet';
	/**
	 * Only used in HDFS
	 */
	fieldDelimiter?: string;
	encoding?: 'utf-8' | 'gbk';
	/**
	 * Only used in Hive and SparkShrift
	 */
	partition?: string;
	/**
	 * 开始行健
	 */
	startRowkey?: string;
	/**
	 * 结束行健
	 */
	endRowkey?: string;
	/**
	 * 行健二进制转换
	 */
	isBinaryRowkey?: Valueof<typeof BINARY_ROW_KEY_FLAG>;
	/**
	 * 每次 RPC 请求获取行数
	 */
	scanCacheSize?: number;
	/**
	 * 每次 RPC 请求获取列数
	 */
	scanBatchSize?: number;
	/**
	 * 索引
	 */
	index?: string;
	/**
	 * 索引类型
	 */
	indexType?: string;
	query?: string;
}

/**
 * 离线任务类型中的调度依赖 JSON 字符串格式化后数据类型
 */
export interface IScheduleConfProps {
	/**
	 * 初始化的时候该配置为 false
	 */
	selfReliance: false | SCHEDULE_DEPENDENCY;
	min?: number | string;
	hour?: number | string;
	/**
	 * 数字字符串，如 '0'
	 */
	periodType: TASK_PERIOD_ENUM | string;
	/**
	 * YYYY-MM-DD 格式
	 */
	beginDate: string;
	endDate: string;
	endHour?: string | number;
	endMin?: string | number;
	beginHour?: number | string;
	beginMin?: number | string;
	isFailRetry: boolean;
	gapMin?: string | number;
	/**
	 * 数字字符串，如 '5'
	 */
	maxRetryNum: number | string;
	isExpire?: boolean;
	isLastInstance?: boolean;
	weekDay?: string | number;
	day?: string | number;
	gapHour?: string | number;
}

/**
 * 任务上下游依赖类型
 */
export interface ITaskVOProps extends IOfflineTaskProps {
	taskId: number;
}

export interface ITaskVariableProps {
	paramCommand: string;
	paramName: string;
	type: PARAMS_ENUM;
}

/**
 * 离线-函数管理属性
 */
export interface IFunctionProps {
	/**
	 * 命令格式
	 */
	commandFormate: string;
	className?: string | null;
	createUser?: null | IUserProps;
	createUserId: number;
	gmtCreate: number;
	gmtModified: number;
	id: number;
	modifyUser?: null | IUserProps;
	modifyUserId: number;
	name: string;
	nodePid: number;
	paramDesc: string;
	purpose: string;
	sqlText?: null | string;
	/**
	 * 函数类型
	 */
	taskType?: TASK_TYPE_ENUM;
	resources?: number | null;
	type: number;
}

export interface IDataSourceProps {
	dataInfoId: number;
	dataType: DATA_SOURCE_ENUM;
	// 0 for false, 1 for true
	isMeta: number;
	appNames: string;
	dataDesc: string;
	dataName: string;
	dataVersion: string;
	gmtModified: string;
	isImport: number;
	schemaName: string;
	status: number;
	linkJson: string | null;
	type?: DATA_SOURCE_ENUM;
}

export interface IDataSourceUsedInSyncProps {
	dataInfoId: number;
	dataName: string;
	dataTypeCode: DATA_SOURCE_ENUM;
}

/**
 * flinkSQL 任务的属性
 */
export interface IFlinkDataProps {
	source: IFlinkSourceProps;
}

export interface IFlinkSourceProps {
	charset: CODE_TYPE;
	columns: [{ column: 'id'; type: 'int' }];
	columnsText: string;
	offset: number;
	offsetReset: string;
	offsetUnit: string;
	offsetValue: string;
	parallelism: number;
	procTime: string;
	sourceDataType: string;
	sourceId: number;
	sourceName: string;
	table: string;
	timeType: SOURCE_TIME_TYPE;
	timeTypeArr: SOURCE_TIME_TYPE[];
	timeZone: string;
	topic: string;
	type: DATA_SOURCE_ENUM;
	// 自定义参数
	customParams: any;

	// the unique key for front-end panel
	panelKey: string;
}
