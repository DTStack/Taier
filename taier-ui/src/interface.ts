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
    CREATE_MODEL_TYPE,
    DATA_SOURCE_ENUM,
    DATA_SYNC_MODE,
    DIRTY_DATA_SAVE,
    FLINK_VERSIONS,
    KAFKA_DATA_TYPE,
    MENU_TYPE_ENUM,
    PARAMS_ENUM,
    PythonVersionKind,
    RESOURCE_TYPE,
    SCHEDULE_DEPENDENCY,
    SCHEDULE_STATUS,
    SOURCE_TIME_TYPE,
    TASK_PERIOD_ENUM,
    TASK_STATUS,
    TASK_TYPE_ENUM,
    UDF_TYPE_VALUES,
} from './constant';

interface IUserProps {}

/**
 * 请求返回体
 */
export interface IResponseBodyProps<T = any> {
    code: number;
    data: T;
    message: string;
    success: boolean;

    [key: string]: any;
}

/**
 * 目录数据结果
 */
export interface CatalogueDataProps {
    id: number;
    /**
     * 目录类型，区分 file 或者 folder 等
     */
    type: string;
    /**
     * 任务类型，非任务为 null
     */
    taskType: TASK_TYPE_ENUM | null;
    resourceType: RESOURCE_TYPE;
    name: string;
    children: CatalogueDataProps[] | null;
    catalogueType: MENU_TYPE_ENUM;
    parentId: number;
    taskDesc?: string;
}

// 运维中心-任务类型，@todo 应该是 Job
export interface ITaskProps {
    gmtModified: number;
    ownerUserId: number;
    ownerUserName: null | string;
    periodType: TASK_PERIOD_ENUM;
    scheduleStatus: SCHEDULE_STATUS;
    taskType: TASK_TYPE_ENUM;
    taskId: number;
    name: string;

    // 工作流任务的子节点
    children?: ITaskProps[];
}

/**
 * 实时任务(Job)管理——任务类型
 */
export interface IStreamJobProps {
    id: number;
    name: string;
    jobId: string;
    status: TASK_STATUS;
    componentVersion: string;
    strategyName: string;
    taskType: TASK_TYPE_ENUM;
    createUserName: string;
    execStartTime: string;
    gmtModified: string;
    modifyUserName: string;
    originSourceType: number;
    createModel: Valueof<typeof CREATE_MODEL_TYPE>;
}

/**
 * 查询任务树的遍历方向
 */
export enum DIRECT_TYPE_ENUM {
    /**
     * 向上遍历
     */
    FATHER = 1,
    /**
     * 向下遍历
     */
    CHILD = 2,
}

/**
 * 任务上下游依赖类型
 */
export interface IUpstreamJobProps {
    taskId: number;
    taskName: string;
    taskType: TASK_TYPE_ENUM;
    scheduleStatus: SCHEDULE_STATUS;
    taskPeriodId: TASK_PERIOD_ENUM;
    gmtCreate: number;
    /**
     * 是否是工作流任务
     */
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
    taskGmtCreate: number;
    status: TASK_STATUS;
    parentNode: IUpstreamJobProps[];
    childNode: IUpstreamJobProps[];
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
    computeType: IComputeType;
    url: string;
}

/**
 * 所有任务类型
 */
export interface IOfflineTaskProps extends ISyncDataProps, IFlinkSQLProps, IWorkflowProps, IFlinkProps, IPythonProps {
    createUserId: number;
    cron: string;
    currentProject: boolean;
    gmtCreate: number;
    gmtModified: number;
    id: number;
    modifyUser: IUserProps | null;
    modifyUserId: number;
    name: string;
    computeType: IComputeType;
    /**
     * 任务绑定的数据源 id
     */
    datasourceId?: number;
    nodePName: string;
    nodePid: number;
    ownerUserId: number;
    scheduleConf: string;
    scheduleStatus: SCHEDULE_STATUS;
    /**
     * 任务配置模式
     */
    createModel: Valueof<typeof CREATE_MODEL_TYPE>;
    /**
     * @deprecated
     * 是否是增量同步模式, 接口要求把该字段放到 sourceMap 中
     */
    syncModel: number;
    sqlText: string;
    taskDesc: string;
    taskParams: string;
    taskPeriodId: TASK_PERIOD_ENUM;
    taskPeriodType: string;
    taskType: TASK_TYPE_ENUM;
    /**
     * 任务依赖
     */
    dependencyTasks: null | ITaskVOProps[];
    taskVariables: null | ITaskVariableProps[];
    tenantId: string | null;
    tenantName: string | null;
    userId: number;
    // 是否支持脏数据记录
    openDirtyDataManage?: boolean;
    // 脏数据管理字段收集
    taskDirtyDataManageVO?: {
        id: number;
        maxRows?: number;
        maxCollectFailedRows?: number;
        outputType?: DIRTY_DATA_SAVE;
        linkInfo?: {
            sourceId: number;
        };
        tableName?: string;
        lifeCycle?: any;
        logPrintInterval?: number;
    };
}

/**
 * Flink 任务类型
 */
interface IFlinkProps {
    mainClass?: string;
    exeArgs?: string;
    resourceIdList?: number[];
}

/**
 * Python 任务类型
 */
interface IPythonProps {
    pythonVersion?: PythonVersionKind;
}

/**
 * 数据同步任务类型
 */
export interface ISyncDataProps {
    settingMap?: IChannelFormProps;
    sourceMap: ISourceMapProps;
    targetMap?: ITargetMapProps;
    taskId: number;
}

export interface IWorkflowProps {
    flowId: number;
    flowName: string;
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
    /**
     * 同步任务是否增量
     */
    syncModel: DATA_SYNC_MODE;

    [key: string]: any;
}

/**
 * 数据库表字段信息
 */
export interface IDataColumnsProps {
    comment?: string;
    isPart?: boolean;
    /**
     * Generally, it's the unique key for rows
     */
    key: string | number;
    part?: boolean;
    type: string;
    value?: string;
    name?: string;
    /**
     * It's the unique key in FTP for rows
     */
    index?: string;
    cf?: string;
    format?: string;
}

/**
 * 前端表单保存的值
 */
export interface ISourceFormField extends ISourceFieldWithFTP {
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
    isBinaryRowkey?: BINARY_ROW_KEY_FLAG;
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

export interface ISourceFieldWithFTP {
    path?: string;
    /**
     * It'll rename to fileType when task submitted
     */
    ['fileType|FTP']?: string;
    /**
     * It'll rename to fieldDelimiter when task submitted
     */
    ['fieldDelimiter|FTP']?: string;
    /**
     * 是否包含表头
     */
    isFirstLineHeader?: boolean;
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
export type ITaskVOProps = Pick<IOfflineTaskProps, 'id' | 'name' | 'tenantId' | 'tenantName'>;

/**
 * 任务参数
 */
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
    className?: string;
    createUser?: IUserProps;
    createUserId: number;
    gmtCreate: number;
    gmtModified: number;
    id: number;
    modifyUser?: IUserProps;
    modifyUserId: number;
    name: string;
    nodePid: number;
    paramDesc: string;
    purpose: string;
    sqlText?: string;
    /**
     * 函数类型
     */
    taskType?: TASK_TYPE_ENUM;
    /**
     * UDF类型
     */
    udfType?: UDF_TYPE_VALUES;
    resources?: number;
    type: number;
}

/**
 * 数据源类型
 */
export interface IDataSourceProps {
    dataInfoId: number;
    dataType: string;
    dataTypeCode: DATA_SOURCE_ENUM;
    appNames: string;
    dataDesc: string;
    dataName: string;
    dataVersion: string;
    gmtModified: string;
    isImport: number;
    schemaName: string;
    status: number;
    linkJson: string | null;
}

/**
 * 在同步任务中用到的数据源类型
 */
export interface IDataSourceUsedInSyncProps {
    dataInfoId: number;
    dataName: string;
    dataTypeCode: DATA_SOURCE_ENUM;
    dataType: string;
}

/**
 * flinkSQL 任务的属性
 */
export interface IFlinkSQLProps {
    source: IFlinkSourceProps[];
    sink: IFlinkSinkProps[];
    side: IFlinkSideProps[];
    /**
     * @description 任务类型，目前来说 flinkSQL 暂时只有 1.12
     */
    componentVersion: Valueof<typeof FLINK_VERSIONS>;
}

export interface IFlinkSourceProps {
    charset: CODE_TYPE;
    columns: Partial<{ type: IDataColumnsProps['type']; column: IDataColumnsProps['key'] }>[];
    columnsText: string;
    offset: number;
    offsetReset: string;
    offsetUnit: string;
    offsetValue: string;
    parallelism: number;
    procTime: string;
    schemaInfo: string;
    sourceDataType: KAFKA_DATA_TYPE;
    sourceId: number;
    sourceName: string;
    table: string;
    timeColumn: string;
    timeType: SOURCE_TIME_TYPE;
    timeTypeArr: SOURCE_TIME_TYPE[];
    // 时间戳
    timestampOffset: number;
    timeZone: string;
    topic: string;
    type: DATA_SOURCE_ENUM;
    // 自定义参数
    customParams: { id: string; key?: string; type?: string }[];
}

export interface IFlinkSinkProps {
    collection?: string;
    bucket?: string;
    objectName?: string;
    schema?: string;
    columns: Partial<{ type: IDataColumnsProps['type']; column: IDataColumnsProps['key'] }>[];
    parallelism: number;
    sourceId: number;
    sourceName: string;
    table?: string;
    tableName: string;
    index?: string;
    esId?: string;
    esType?: string;
    rowKey?: string;
    rowKeyType?: string;
    sinkDataType?: KAFKA_DATA_TYPE;
    schemaInfo?: string;
    topic?: string;
    type: DATA_SOURCE_ENUM;
    updateMode: 'append' | 'upsert';
    allReplace?: 'true' | 'false';
    primaryKey?: string | string[];
    bulkFlushMaxActions?: number;
    enableKeyPartitions?: boolean;
    indexDefinition?: string;
    partitionKeys?: string[];
    batchWaitInterval?: number;
    batchSize?: number;
    partitionType?: string;
    columnsText?: string;
    // 自定义参数
    customParams: { id: string; key?: string; type?: string }[];
}

/**
 * FlinkSQL 维表数据结构
 */
export interface IFlinkSideProps {
    type: DATA_SOURCE_ENUM;
    sourceId: number;
    schema?: string;
    table?: string;
    index?: string;
    esType?: string;
    tableName?: string;
    primaryKey?: string[] | string;
    hbasePrimaryKey?: string;
    hbasePrimaryKeyType?: string;
    parallelism?: number;
    columns?: Partial<{
        type: IDataColumnsProps['type'];
        column: IDataColumnsProps['key'];
        targetCol?: string;
    }>[];
    cache?: 'None' | 'LRU' | 'ALL';
    cacheSize?: number;
    cacheTTLMs?: number;
    errorLimit?: number;
    isFaultTolerant?: boolean;
    asyncPoolSize?: number;
    customParams?: { id: string; key?: string; type?: string }[];
}

/**
 * 实时-任务属性参数
 */
export interface IStreamJobParamsProps {
    id: number;
    name: string;
    exeArgs: string;
    sqlText: string;
    taskDesc: string;
    mainClass: string;
    taskParams: string;
    originSourceType: number;
    createModel: number;
    taskType: number;
    targetSourceType: number;
    sourceParams: string;
    sinkParams: string;
    sideParams: string;
    resourceList: IResourceList[];
    additionalResourceList: IResourceList[];
}

/**
 * 实时-资源相关的参数
 */
export interface IResourceList {
    id: number;
    url: string;
    originFileName: string;
    projectId: number;
    resourceDesc: string;
    resourceName: string;
    isAdditionResource: number;
}

/**
 * 当前任务所属计算环境，区分实时任务和离线任务
 */
export enum IComputeType {
    /**
     * 实时任务
     */
    STREAM = 0,
    /**
     * 离线任务
     */
    BATCH = 1,
}

/**
 * 当前任务的任务类型
 */
export enum IJobType {
    /**
     * SQL 类的任务
     */
    SQL = 0,
    /**
     * 非 SQL 类的任务
     */
    OTHERS = 1,
}

export enum RightBarKind {
    /**
     * 任务属性
     */
    TASK = 'task',
    /**
     * 调度依赖
     */
    DEPENDENCY = 'dependency',
    /**
     * 任务参数
     */
    TASK_PARAMS = 'task_params',
    /**
     * 环境参数
     */
    ENV_PARAMS = 'env_params',
    /**
     * 任务设置
     */
    TASK_CONFIG = 'task_config',
    /**
     * 源表
     */
    FLINKSQL_SOURCE = 'flinksql_source',
    /**
     * 结果表
     */
    FLINKSQL_RESULT = 'flinksql_result',
    /**
     * 维表
     */
    FLINKSQL_DIMENSION = 'flinksql_dimension',
    /**
     * 队列管理
     */
    QUEUE = 'queue',
}

export enum DeletedKind {
    notDeleted = 0,
    isDeleted = 1,
}
