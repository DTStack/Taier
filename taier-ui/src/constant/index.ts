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

import type molecule from '@dtinsight/molecule';
import type { ISubMenuProps } from '@dtinsight/molecule/esm/components';

/**
 * ID 集合
 */
export enum ID_COLLECTIONS {
    /**
     * 创建任务
     */
    TASK_CREATE_ID = 'task.create',
    /**
     * 任务运行按钮
     */
    TASK_RUN_ID = 'task.run',
    /**
     * 任务停止按钮
     */
    TASK_STOP_ID = 'task.stop',
    /**
     * 任务提交到运维中心按钮
     */
    TASK_SUBMIT_ID = 'task.submit',
    /**
     * 任务保存按钮
     */
    TASK_SAVE_ID = 'task.save',
    /**
     * 任务运维按钮
     */
    TASK_OPS_ID = 'task_ops',
    /**
     * 任务格式化按钮
     */
    TASK_FORMAT_ID = 'task.format',
    /**
     * 任务转化为脚本按钮
     */
    TASK_CONVERT_SCRIPT = 'task.convert.script',
    /**
     * 导入数据源按钮
     */
    TASK_IMPORT_ID = 'task.import',
    /**
     * 语法检查按钮
     */
    TASK_SYNTAX_ID = 'task.syntax',
    /**
     * 环境参数编辑器唯一标识符
     */
    ENV_PARAMS_ID = 'env.params',
    /**
     * 面板-日志
     */
    OUTPUT_LOG_ID = 'panel.output.log',
    /**
     * 状态栏-任务语言 Panel
     */
    LANGUAGE_STATUS_BAR = 'language',
    /**
     * 任务目录树右键「编辑」项
     */
    FOLDERTREE_CONTEXT_EDIT = 'explorer.edit',
    /**
     * 创建任务 Tab 的 ID 前缀
     */
    CREATE_TASK_PREFIX = 'createTask',
    /**
     * 创建文件夹 Tab 的 ID 前缀
     */
    CREATE_FOLDER_PREFIX = 'createFolder',
    /**
     * 编辑任务 Tab 的 ID 前缀
     */
    EDIT_TASK_PREFIX = 'editTask',
    /**
     * 编辑文件夹 Tab 的 ID 前缀
     */
    EDIT_FOLDER_PREFIX = 'editFolder',
    /**
     * 创建数据源 Tab 的 ID 前缀
     */
    CREATE_DATASOURCE_PREFIX = 'create-datasource',
    /**
     * 编辑数据源 Tab 的 ID 前缀
     */
    EDIT_DATASOURCE_PREFIX = 'edit-datasource',
    /**
     * 资源管理-上传资源
     */
    RESOURCE_UPLOAD = 'resource.upload',
    /**
     * 资源管理-替换资源
     */
    RESOURCE_REPLACE = 'resource.replace',
    /**
     * 资源管理-创建文件夹
     */
    RESOURCE_CREATE = 'resource.create.folder',
    /**
     * 资源管理-删除资源
     */
    RESOURCE_DELETE = 'resource.delete',
    /**
     * 资源管理-编辑文件夹
     */
    RESOURCE_EDIT = 'resource.edit',
    /**
     * 函数管理-新建文件夹
     */
    FUNCTION_CREATE_FOLDER = 'function.create.folder',
    /**
     * 函数管理-新建自定义函数
     */
    FUNCTION_CREATE = 'function.create',
    /**
     * 函数管理-编辑函数
     */
    FUNCTION_EDIT = 'function.edit',
    /**
     * 函数管理-删除函数
     */
    FUNCTION_DELETE = 'function.delete',
    /**
     * 主题存储健
     */
    COLOR_THEME_ID = 'taier.colorTheme',
    /**
     * 新增租户
     */
    ADD_TENANT = 'add.tenant',
    /**
     * 任务切换事件
     */
    TASK_SWITCH_EVENT = 'task.switch.event',
}

export const OFFSET_RESET_FORMAT = 'YYYY-MM-DD HH:mm:ss';

/**
 * 高可用配置的 placeholder
 */
export const HDFSCONG = `{
    "dfs.nameservices": "defaultDfs",
    "dfs.ha.namenodes.defaultDfs": "namenode1",
    "dfs.namenode.rpc-address.defaultDfs.namenode1": "",
    "dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
    }`;

export const DDL_IDE_PLACEHOLDER =
    'CREATE TABLE employee (eid int,\n\tname String,\n\tsalary String,\n\tdestination String\n) STORED AS ORC lifecycle 10';

// 表单正常布局
export const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
    },
};

export const scheduleConfigLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
    },
};

// 表单对称布局
export const specFormItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};

// 表单行label居中对齐
export const tailFormItemLayout = {
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0,
        },
        sm: {
            span: 14,
            offset: 8,
        },
    },
};

/**
 * 任务类型
 */
export enum TASK_TYPE_ENUM {
    /**
     * 虚节点
     */
    VIRTUAL = -1,
    /**
     * SparkSQL
     */
    SPARK_SQL = 0,
    /**
     * SparkJar
     */
    SPARK = 1,
    SYNC = 2,
    /**
     * FlinkSQL
     */
    SQL = 5,
    /**
     * 实时任务
     */
    DATA_ACQUISITION = 6,
    HIVE_SQL = 7,
    OCEANBASE = 8,
    /**
     * 工作流
     */
    WORK_FLOW = 10,
    /**
     * FlinkJar
     */
    FLINK = 11,
    PYTHON = 12,
    SHELL = 13,
    CLICKHOUSE = 14,
    DORIS = 15,
    /**
     * Python Spark
     */
    PY_SPARK = 16,
    MYSQL = 17,
    GREENPLUM = 18,
    GAUSS_DB = 19,
    POSTGRE_SQL = 20,
    SQL_SERVER = 21,
    TiDB = 22,
    VERTICA = 23,
    MAX_COMPUTE = 24,
    HADOOP_MR = 25,
    DATAX,
    ORACLE_SQL = 27,
}

/**
 * 数据源类型
 */
export enum DATA_SOURCE_ENUM {
    MYSQL = 1,
    MySQL8 = 1001,
    MySQLPXC = 98,
    POLAR_DB_For_MySQL = 28,
    ORACLE = 2,
    SQLSERVER = 3,
    SQLSERVER_2017_LATER = 32,
    POSTGRESQL = 4,
    DB2 = 19,
    DMDB = 35,
    RDBMS = 5,
    KINGBASE8 = 40,
    DMDB_For_Oracle = 67,
    HIVE = 7,
    HIVE1X = 27,
    HIVE3X = 50,
    MAXCOMPUTE = 10,
    GREENPLUM6 = 36,
    LIBRA = 21,
    GBASE_8A = 22,
    DORIS = 57,
    HDFS = 6,
    FTP = 9,
    S3 = 41,
    AWS_S3 = 51,
    SPARKTHRIFT = 45,
    IMPALA = 29,
    CLICKHOUSE = 25,
    TIDB = 31,
    CARBONDATA = 20,
    KUDU = 24,
    ADS = 15,
    ADB_FOR_PG = 54,
    Kylin = 23,
    PRESTO = 48,
    OCEANBASE = 49,
    INCEPTOR = 52,
    TRINO = 59,
    HBASE = 8,
    HBASE2 = 39,
    PHOENIX = 30,
    PHOENIX5 = 38,
    ES = 11,
    ES6 = 33,
    ES7 = 46,
    MONGODB = 13,
    REDIS = 12,
    SOLR = 53,
    HBASE_GATEWAY = 99,
    KAFKA_2X = 37,
    KAFKA = 26,
    KAFKA_11 = 14,
    KAFKA_10 = 17,
    KAFKA_09 = 18,
    EMQ = 34,
    WEBSOCKET = 42,
    SOCKET = 44,
    RESTFUL = 47,
    VERTICA = 43,
    INFLUXDB = 55,
    OPENTSDB = 56,
    BEATS = 16,
    Spark = 1002,
    KylinRestful = 58,
    TBDS_HDFS = 60,
    TBDS_HBASE = 61,
    TBDS_KAFKA = 62,
    DorisRestful = 64,
    HIVE3_CDP = 65,
    KAFKA_HUAWEI = 70,
    HBASE_HUAWEI = 71,
    DRDS = 72,
    UPDRDB = 73,
    UPRedis = 74,
    CSP_S3 = 75,
    KAFKA_CONFLUENT = 79,
}

export const DATA_SOURCE_ENUM_OBJ = {
    MYSQL: 1,
    MySQL8: 1001,
    MySQLPXC: 98,
    POLAR_DB_For_MySQL: 28,
    ORACLE: 2,
    SQLSERVER: 3,
    SQLSERVER_2017_LATER: 32,
    POSTGRESQL: 4,
    DB2: 19,
    DMDB: 35,
    RDBMS: 5,
    KINGBASE8: 40,
    DMDB_For_Oracle: 67,
    HIVE: 7,
    HIVE1X: 27,
    HIVE3X: 50,
    MAXCOMPUTE: 10,
    GREENPLUM6: 36,
    LIBRA: 21,
    GBASE_8A: 22,
    DORIS: 57,
    HDFS: 6,
    FTP: 9,
    S3: 41,
    AWS_S3: 51,
    SPARKTHRIFT: 45,
    IMPALA: 29,
    CLICKHOUSE: 25,
    TIDB: 31,
    CARBONDATA: 20,
    KUDU: 24,
    ADS: 15,
    ADB_FOR_PG: 54,
    Kylin: 23,
    PRESTO: 48,
    OCEANBASE: 49,
    INCEPTOR: 52,
    TRINO: 59,
    HBASE: 8,
    HBASE2: 39,
    PHOENIX: 30,
    PHOENIX5: 38,
    ES: 11,
    ES6: 33,
    ES7: 46,
    MONGODB: 13,
    REDIS: 12,
    SOLR: 53,
    HBASE_GATEWAY: 99,
    KAFKA_2X: 37,
    KAFKA: 26,
    KAFKA_11: 14,
    KAFKA_10: 17,
    KAFKA_09: 18,
    EMQ: 34,
    WEBSOCKET: 42,
    SOCKET: 44,
    RESTFUL: 47,
    VERTICA: 43,
    INFLUXDB: 55,
    OPENTSDB: 56,
    BEATS: 16,
    Spark: 1002,
    KylinRestful: 58,
    TBDS_HDFS: 60,
    TBDS_HBASE: 61,
    TBDS_KAFKA: 62,
    DorisRestful: 64,
    HIVE3_CDP: 65,
    DRDS: 72,
    UPDRDB: 73,
    UPRedis: 74,
    CSP_S3: 75,
} as const;

/**
 * 目录结构类型
 */
export enum CATALOGUE_TYPE {
    /**
     * 任务开发
     */
    TASK = 'task',
    /**
     * 资源管理
     */
    RESOURCE = 'resource',
    /**
     * 函数管理
     */
    FUNCTION = 'function',
}

/**
 * 支持分库分表的数据源类型
 */
export const SUPPROT_SUB_LIBRARY_DB_ARRAY = [DATA_SOURCE_ENUM.MYSQL];

/**
 * 帮助文档跳转目录
 */
export const HELP_DOC_URL = {
    INDEX: '/public/helpSite/batch/v3.0/Summary.html',
    DATA_SOURCE_ENUM: '/public/helpSite/batch/v3.0/DataIntegration/Overview.html',
    DATA_SYNC: '/public/helpSite/batch/v3.0/DataIntegration/JobConfig.html',
    TASKPARAMS: '/public/helpSite/batch/v3.0/DataDevelop/ScheduleConfig.html#ParamConfig',
    FORCE_ORDER: `/public/helpSite/stream/v4.0/StreamSync/Kafka.html#_写入kafka目标源`,
    JOB_CONFIG: `/public/helpSite/stream/v4.0/DataCollection.html#collection_jobConfig`,
    JOB_SETTING: `/public/helpSite/stream/v4.0/DataDvlp/JobSetting.html`,
    ASSET_MANAGE: `/public/helpSite/assets/v4.0/Datamodel/standardtable.html#_功能介绍`,
    HBASE: `/public/helpSite/stream/v4.0/DataSource/HBase.html`,
};
/**
 *
 * 数据同步模式
 */
export enum DATA_SYNC_MODE {
    /**
     * 正常
     */
    NORMAL = 0,
    /**
     * 增量
     */
    INCREMENT = 1,
}

/**
 * 任务模式枚举
 */
export const CREATE_MODEL_TYPE = {
    /**
     * 向导模式
     */
    GUIDE: 0,
    /**
     * 脚本模式
     */
    SCRIPT: 1,
    /**
     * 只有 FlinkSQL 支持 GRAPH
     * @deprecated
     */
    GRAPH: 2,
} as const;

/**
 * 数据源对应名称
 */
export const DATA_SOURCE_TEXT: Partial<{ [key in DATA_SOURCE_ENUM]: string }> = {
    [DATA_SOURCE_ENUM.MYSQL]: 'MySQL',
    [DATA_SOURCE_ENUM.ORACLE]: 'Oracle',
    [DATA_SOURCE_ENUM.SQLSERVER]: 'SQL Server',
    [DATA_SOURCE_ENUM.POSTGRESQL]: 'PostgreSQL',
    [DATA_SOURCE_ENUM.HDFS]: 'HDFS2.x',
    [DATA_SOURCE_ENUM.HIVE3X]: 'Hive3',
    [DATA_SOURCE_ENUM.HIVE]: 'Hive2.x',
    [DATA_SOURCE_ENUM.HIVE1X]: 'Hive1.x',
    [DATA_SOURCE_ENUM.SPARKTHRIFT]: 'SparkThrift',
    [DATA_SOURCE_ENUM.HBASE]: 'HBase1.x',
    [DATA_SOURCE_ENUM.HBASE2]: 'HBase2.x',
    [DATA_SOURCE_ENUM.FTP]: 'FTP',
    [DATA_SOURCE_ENUM.MAXCOMPUTE]: 'MaxCompute',
    [DATA_SOURCE_ENUM.ES]: 'Elasticsearch5.x',
    [DATA_SOURCE_ENUM.ES6]: 'Elasticsearch6.x',
    [DATA_SOURCE_ENUM.ES7]: 'Elasticsearch7.x',
    [DATA_SOURCE_ENUM.REDIS]: 'Redis',
    [DATA_SOURCE_ENUM.KAFKA_2X]: 'Kafka2.x',
    [DATA_SOURCE_ENUM.KAFKA]: 'Kafka',
    [DATA_SOURCE_ENUM.KAFKA_11]: 'Kafka_0.11',
    [DATA_SOURCE_ENUM.KAFKA_10]: 'Kafka_0.10',
    [DATA_SOURCE_ENUM.MONGODB]: 'MongoDB',
    [DATA_SOURCE_ENUM.DB2]: 'DB2',
    [DATA_SOURCE_ENUM.CARBONDATA]: 'DTinsightAnalytics',
    [DATA_SOURCE_ENUM.LIBRA]: 'GaussDB', // 更名
    [DATA_SOURCE_ENUM.GBASE_8A]: 'GBase',
    [DATA_SOURCE_ENUM.Kylin]: 'Kylin',
    [DATA_SOURCE_ENUM.KUDU]: 'Kudu',
    [DATA_SOURCE_ENUM.CLICKHOUSE]: 'ClickHouse',
    [DATA_SOURCE_ENUM.POLAR_DB_For_MySQL]: 'PolarDB',
    [DATA_SOURCE_ENUM.IMPALA]: 'Impala',
    [DATA_SOURCE_ENUM.PHOENIX]: 'Phoenix',
    [DATA_SOURCE_ENUM.PHOENIX5]: 'Phoenix5',
    [DATA_SOURCE_ENUM.TIDB]: 'TiDB',
    [DATA_SOURCE_ENUM.DMDB]: 'DMDB',
    [DATA_SOURCE_ENUM.GREENPLUM6]: 'Greenplum',
    [DATA_SOURCE_ENUM.KINGBASE8]: 'Kingbase',
    [DATA_SOURCE_ENUM.S3]: 'AWS S3',
    [DATA_SOURCE_ENUM.INCEPTOR]: 'Inceptor',
    [DATA_SOURCE_ENUM.ADB_FOR_PG]: 'AnalyticDB PostgreSQL',
    [DATA_SOURCE_ENUM.INFLUXDB]: 'InfluxDB',
    [DATA_SOURCE_ENUM.OPENTSDB]: 'OpenTSDB',
    [DATA_SOURCE_ENUM.KAFKA_HUAWEI]: 'Kafka_huawei',
    [DATA_SOURCE_ENUM.HBASE_HUAWEI]: 'Hbase_huawei',
    [DATA_SOURCE_ENUM.KAFKA_CONFLUENT]: 'Confluent',
    [DATA_SOURCE_ENUM.DorisRestful]: 'Doris(http)',
};

/**
 * 隶属于 RDB 的数据源
 */
export const RDB_TYPE_ARRAY = [
    DATA_SOURCE_ENUM.MYSQL,
    DATA_SOURCE_ENUM.ORACLE,
    DATA_SOURCE_ENUM.SQLSERVER,
    DATA_SOURCE_ENUM.POSTGRESQL,
    DATA_SOURCE_ENUM.ADB_FOR_PG,
    DATA_SOURCE_ENUM.DB2,
    DATA_SOURCE_ENUM.GBASE_8A,
    DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
    DATA_SOURCE_ENUM.CLICKHOUSE,
    DATA_SOURCE_ENUM.TIDB,
    DATA_SOURCE_ENUM.DMDB,
    DATA_SOURCE_ENUM.GREENPLUM6,
    DATA_SOURCE_ENUM.KINGBASE8,

    // 实时计算的关系型数据库
    DATA_SOURCE_ENUM.UPDRDB,
    DATA_SOURCE_ENUM.IMPALA,
    DATA_SOURCE_ENUM.KUDU,
    DATA_SOURCE_ENUM.HBASE,
    DATA_SOURCE_ENUM.TBDS_HBASE,
    DATA_SOURCE_ENUM.HBASE_HUAWEI,
    DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
    DATA_SOURCE_ENUM.SOLR,
];

/**
 * 任务目录的菜单类型
 */
export enum MENU_TYPE_ENUM {
    TASK = 'TaskManager',
    TASK_DEV = 'TaskDevelop',
    SCRIPT = 'ScriptManager',
    RESOURCE = 'ResourceManager',
    FUNCTION = 'FunctionManager',
    PROCEDURE = 'ProcedureManager',
    SPARKFUNC = 'SparkSQLFunction',
    FLINKFUNC = 'FlinkSQLFunction',
    LIBRAFUNC = 'LibraSQLFunction',
    LIBRASYSFUN = 'LibraSysFunc',
    COSTOMFUC = 'CustomFunction',
    COSTOMPROD = 'CustomProcedure',
    SYSFUC = 'SystemFunction',
    COMPONENT = 'ComponentManager',
    TABLE = 'TableQuery',
    TIDB_FUNC = 'TiDBSQLFunction',
    TIDB_SYS_FUNC = 'TiDBSysFunc',
    ORACLE_FUNC = 'OracleSQLFunction',
    ORACLE_SYS_FUNC = 'OracleSysFunc',
    GREEN_PLUM = 'GreenPlumSQLFunction',
    GREEN_PLUM_FUNC = 'GreenPlumCustomFunction',
    GREEN_PLUM_SYS_FUNC = 'GreenPlumSysFunc',
    GREEN_PLUM_PROD = 'ProcedureFunction',
}

/**
 * 引擎类型
 */
export enum ENGINE_SOURCE_TYPE_ENUM {
    HADOOP = 1,
    LIBRA = 2,
    TI_DB = 4,
    ORACLE = 5,
    GREEN_PLUM = 6,
    PRESTO = 7,
    FLINK_ON_STANDALONE = 8,
    ADB = 9,
    MYSQL = 10,
    SQLSERVER = 11,
    DB2 = 12,
    OCEANBASE = 13,
    KUBERNETES = 'Kubernetes',
}

/**
 * 引擎类型
 * Why we should have double ENGINE_SOURCE_TYPE
 * It's used for `Object.keys`
 */
export const ENGINE_SOURCE_TYPE = {
    HADOOP: 1,
    LIBRA: 2,
    TI_DB: 4,
    ORACLE: 5,
    GREEN_PLUM: 6,
    PRESTO: 7,
    FLINK_ON_STANDALONE: 8,
    ANALYTIC_DB: 9,
    KUBERNETES: 'Kubernetes',
    MYSQL: 10,
    SQLSERVER: 11,
    DB2: 12,
    OCEANBASE: 13,
};

/**
 * 任务状态
 */
export enum TASK_STATUS {
    WAIT_SUBMIT = 0,
    CREATED = 1,
    INVOKED = 2,
    DEPLOYING = 3,
    RUNNING = 4,
    FINISHED = 5,
    STOPING = 6,
    STOPED = 7,
    RUN_FAILED = 8, // 运行失败
    SUBMIT_FAILED = 9, // 提交失败
    SUBMITTING = 10,
    RESTARTING = 11,
    SET_SUCCESS = 12,
    KILLED = 13,
    SUBMITTED = 14,
    TASK_STATUS_NOT_FOUND = 15, // 暂时无法获取任务状态
    WAIT_RUN = 16,
    WAIT_COMPUTE = 17,
    FROZEN = 18,
    ENGINEACCEPTED = 19,
    PARENT_FAILD = 21, // 上游失败
    DO_FAIL = 22,
    COMPUTING = 23,
    LACKING = 25,
    AUTO_CANCEL = 26, // 自动取消
}

/**
 * 任务状态集合，运行中
 */
export const RUNNING_STATUS = [TASK_STATUS.RUNNING, TASK_STATUS.TASK_STATUS_NOT_FOUND];
export const FINISH_STATUS = [TASK_STATUS.FINISHED, TASK_STATUS.SET_SUCCESS];
export const FAILED_STATUS = [TASK_STATUS.DO_FAIL, TASK_STATUS.SUBMIT_FAILED];
export const SUBMITFAILD_STATUS = [TASK_STATUS.SUBMIT_FAILED];
export const PARENTFAILED_STATUS = [TASK_STATUS.PARENT_FAILD];
/**
 * 运行失败集合
 */
export const RUN_FAILED_STATUS = [TASK_STATUS.RUN_FAILED];
/**
 * 等待运行集合
 */
export const WAIT_STATUS = [
    TASK_STATUS.WAIT_RUN,
    TASK_STATUS.WAIT_COMPUTE,
    TASK_STATUS.RESTARTING,
    TASK_STATUS.SUBMITTED,
    TASK_STATUS.CREATED,
    TASK_STATUS.COMPUTING,
];
export const SUBMITTING_STATUS = [TASK_STATUS.SUBMITTING];
/**
 * 停止集合
 */
export const STOP_STATUS = [TASK_STATUS.KILLED, TASK_STATUS.AUTO_CANCEL];
export const FROZEN_STATUS = [TASK_STATUS.FROZEN];

/**
 * 任务状态过滤筛选
 */
export const TASK_STATUS_FILTERS = [
    {
        text: '等待提交',
        value: TASK_STATUS.WAIT_SUBMIT,
    },
    {
        text: '提交中',
        value: TASK_STATUS.SUBMITTING,
    },
    {
        text: '提交失败',
        value: TASK_STATUS.SUBMIT_FAILED,
    },
    {
        text: '等待运行',
        value: TASK_STATUS.WAIT_RUN,
    },
    {
        text: '运行中',
        value: TASK_STATUS.RUNNING,
    },
    {
        text: '成功',
        value: TASK_STATUS.FINISHED,
    },
    {
        text: '手动取消',
        value: TASK_STATUS.STOPED,
    },
    {
        text: '超时取消',
        value: TASK_STATUS.AUTO_CANCEL,
    },
    {
        text: '运行失败',
        value: TASK_STATUS.RUN_FAILED,
    },
    {
        text: '失败中',
        value: TASK_STATUS.DO_FAIL,
    },
    {
        text: '停止中',
        value: TASK_STATUS.STOPING,
    },
    {
        text: '上游失败',
        value: TASK_STATUS.PARENT_FAILD,
    },
    {
        text: '冻结',
        value: TASK_STATUS.FROZEN,
    },
];

/**
 * hdfs 类型
 */
export const HDFS_FIELD_TYPES = [
    'STRING',
    'VARCHAR',
    'CHAR',
    'TINYINT',
    'SMALLINT',
    'INT',
    'BIGINT',
    'FLOAT',
    'DECIMAL',
    'DOUBLE',
    'TIMESTAMP',
    'DATE',
];

// HBase 类型
export const HBASE_FIELD_TYPES = ['BOOLEAN', 'INT', 'STRING', 'LONG', 'DOUBLE', 'SHORT', 'FLOAT'];

// 资源类型
export enum RESOURCE_TYPE {
    OTHER = 0,
    JAR = 1,
    PY = 2,
    ZIP = 3,
    EGG = 4,
    YARN = 'YARN',
    KUBERNETES = 'Kubernetes',
}

/**
 * 任务周期
 */
export enum TASK_PERIOD_ENUM {
    MINUTE = 0,
    HOUR = 1,
    DAY = 2,
    WEEK = 3,
    MONTH = 4,
}

/**
 * 任务调度状态
 */
export enum SCHEDULE_STATUS {
    /**
     * 正常
     */
    NORMAL = 0,
    /**
     * 冻结
     */
    FORZON = 1,
    /**
     * @deprecated 前端用不到
     */
    STOPPED = 2,
}

export enum RESTART_STATUS_ENUM {
    /**
     * 重跑当前任务
     */
    CURRENT = 0,
    /**
     * 重跑当前任务及全部下游
     */
    DOWNSTREAM = 1,
    /**
     * 置成功并恢复调度
     */
    SUCCESSFULLY_AND_RESUME = 2,
}

// 数据统计类型
export enum STATISTICS_TYPE_ENUM {
    /**
     * 周期实例
     */
    SCHEDULE = 0,
    /**
     * 补数据
     */
    FILL_DATA = 1,
}

/**
 * 菜单抽屉类别
 */
export enum DRAWER_MENU_ENUM {
    /**
     * 任务管理
     */
    TASK = 'task',
    /**
     * 实时任务管理
     */
    STREAM_TASK = 'stream-task',
    /**
     * 周期实例
     */
    SCHEDULE = 'schedule',
    /**
     * 补数据实例
     */
    PATCH = 'patch',
    /**
     * 补数据实例明细
     */
    PATCH_DETAIL = 'patch-detail',
    /**
     * 队列管理
     */
    QUEUE = 'queue',
    /**
     * 队列管理明细
     */
    QUEUE_DETAIL = 'queue-detail',
    /**
     * 资源管理
     */
    RESOURCE = 'resource',
    /**
     * 多集群管理
     */
    CLUSTER = 'cluster',
    /**
     * 多集群管理明细
     */
    CLUSTER_DETAIL = 'cluster-detail',
}

/**
 * 运维中心下拉菜单
 */
export const OPERATIONS: molecule.model.IMenuBarItem[] = [
    {
        id: DRAWER_MENU_ENUM.TASK,
        name: '离线任务',
    },
    {
        id: DRAWER_MENU_ENUM.STREAM_TASK,
        name: '实时任务',
    },
    {
        id: DRAWER_MENU_ENUM.SCHEDULE,
        name: '周期实例',
    },
    {
        id: DRAWER_MENU_ENUM.PATCH,
        name: '补数据实例',
    },
];

/**
 * 控制台下拉菜单
 */
export const CONSOLE = [
    {
        id: DRAWER_MENU_ENUM.QUEUE,
        name: '队列管理',
    },
    {
        id: DRAWER_MENU_ENUM.RESOURCE,
        name: '资源管理',
    },
    {
        id: DRAWER_MENU_ENUM.CLUSTER,
        name: '多集群管理',
    },
];

/**
 * 控制台队列任务类型
 */
export enum JOB_STAGE_ENUM {
    /**
     * 已存储
     */
    Saved = 1,
    /**
     * 队列中
     */
    Queueing = 2,
    /**
     * 等待重试
     */
    WaitTry = 3,
    /**
     * 等待资源
     */
    WaitResource = 4,
    /**
     * 运行中
     */
    Running = 5,
}

export enum SCHEDULE_TYPE {
    Capacity = 'capacityScheduler',
    Fair = 'fairScheduler',
    FIFO = 'fifoScheduler',
}

/**
 * 项目创建方式 (引擎 创建 or 对接)
 */
export enum PROJECT_CREATE_MODEL {
    NORMAL = 0,
    IMPORT = 1,
}

/**
 * 离线任务周期过滤项
 */
export const offlineTaskPeriodFilter = [
    {
        id: 1,
        text: '分钟任务',
        value: TASK_PERIOD_ENUM.MINUTE,
    },
    {
        id: 2,
        text: '小时任务',
        value: TASK_PERIOD_ENUM.HOUR,
    },
    {
        id: 3,
        text: '天任务',
        value: TASK_PERIOD_ENUM.DAY,
    },
    {
        id: 4,
        text: '周任务',
        value: TASK_PERIOD_ENUM.WEEK,
    },
    {
        id: 5,
        text: '月任务',
        value: TASK_PERIOD_ENUM.MONTH,
    },
];

/**
 * 多集群中集群组件配置项中与 memory 有关项
 */
export const MEMORY_ITEMS = [
    'executor.memory',
    'driver.memory',
    'jobmanager.memory.mb',
    'taskmanager.memory.mb',
    'worker.memory',
    'executor.memory',
];

/**
 * 资源管理页面的 actions
 */
export const RESOURCE_ACTIONS = {
    UPLOAD: {
        id: ID_COLLECTIONS.RESOURCE_UPLOAD,
        name: '上传资源',
    } as ISubMenuProps,
    REPLACE: {
        id: ID_COLLECTIONS.RESOURCE_REPLACE,
        name: '替换资源',
    } as ISubMenuProps,
    CREATE: {
        id: ID_COLLECTIONS.RESOURCE_CREATE,
        name: '新建文件夹',
    } as ISubMenuProps,
    DELETE: { id: ID_COLLECTIONS.RESOURCE_DELETE, name: '删除' } as ISubMenuProps,
    EDIT: { id: ID_COLLECTIONS.RESOURCE_EDIT, name: '编辑' } as ISubMenuProps,
} as const;

/**
 * 函数管理页面的 actions
 */
export const FUNCTOIN_ACTIONS = {
    CREATE_FOLDER: {
        id: ID_COLLECTIONS.FUNCTION_CREATE_FOLDER,
        name: '新建文件夹',
    } as ISubMenuProps,
    CREATE_FUNCTION: {
        id: ID_COLLECTIONS.FUNCTION_CREATE,
        name: '新建自定义函数',
    } as ISubMenuProps,
    EDIT: {
        id: ID_COLLECTIONS.FUNCTION_EDIT,
        name: '编辑',
    } as ISubMenuProps,
    DELETE: {
        id: ID_COLLECTIONS.FUNCTION_DELETE,
        name: '删除',
    } as ISubMenuProps,
} as const;

/**
 * 调度依赖中的跨周期依赖
 */
export enum SCHEDULE_DEPENDENCY {
    /**
     * 不依赖上一调度周期
     */
    NULL = 0,
    /**
     * 自依赖，等待上一调度周期成功，才能继续运行
     */
    AFTER_SUCCESS = 1,
    /**
     * 等待下游任务的上一周期成功，才能继续运行
     */
    AFTER_SUCCESS_IN_QUEUE = 2,
    /**
     * 自依赖，等待上一调度周期结束，才能继续运行
     */
    AFTER_DONE = 3,
    /**
     * 等待下游任务的上一周期结束，才能继续运行
     */
    AFTER_DONE_IN_QUEUE = 4,
}

/**
 * 任务参数类型
 */
export enum PARAMS_ENUM {
    /**
     * 系统参数
     */
    SYSTEM = 0,
    /**
     * 自定义参数
     */
    CUSTOM = 1,
}

/**
 * 多集群组件标题
 */
export enum TABS_TITLE_KEY {
    /**
     * 公共组件
     */
    COMMON = 0,
    /**
     * 资源调度组件
     */
    SOURCE = 1,
    /**
     * 存储组件
     */
    STORE = 2,
    /**
     * 计算组件
     */
    COMPUTE = 3,
}

/**
 * 组件枚举
 */
export enum COMPONENT_TYPE_VALUE {
    FLINK = 0,
    SPARK = 1,
    HDFS = 2,
    YARN = 3,
    SPARK_THRIFT = 4,
    HIVE_SERVER = 5,
    SFTP = 6,
}

export const COMPONENT_CONFIG_NAME = {
    [COMPONENT_TYPE_VALUE.FLINK]: 'Flink',
    [COMPONENT_TYPE_VALUE.SPARK]: 'Spark',
    [COMPONENT_TYPE_VALUE.HDFS]: 'HDFS',
    [COMPONENT_TYPE_VALUE.YARN]: 'YARN',
    [COMPONENT_TYPE_VALUE.SPARK_THRIFT]: 'SparkThrift',
    [COMPONENT_TYPE_VALUE.HIVE_SERVER]: 'HiveServer',
    [COMPONENT_TYPE_VALUE.SFTP]: 'SFTP',
} as const;

/**
 * 控制台-多集群管理按钮类别
 */
export const CONFIG_BUTTON_TYPE = {
    [TABS_TITLE_KEY.COMMON]: [
        {
            code: COMPONENT_TYPE_VALUE.SFTP,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SFTP],
        },
    ],
    [TABS_TITLE_KEY.SOURCE]: [
        {
            code: COMPONENT_TYPE_VALUE.YARN,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.YARN],
        },
    ],
    [TABS_TITLE_KEY.STORE]: [
        {
            code: COMPONENT_TYPE_VALUE.HDFS,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HDFS],
        },
    ],
    [TABS_TITLE_KEY.COMPUTE]: [
        {
            code: COMPONENT_TYPE_VALUE.SPARK,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK],
        },
        {
            code: COMPONENT_TYPE_VALUE.SPARK_THRIFT,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK_THRIFT],
        },
        {
            code: COMPONENT_TYPE_VALUE.FLINK,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.FLINK],
        },
        {
            code: COMPONENT_TYPE_VALUE.HIVE_SERVER,
            componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HIVE_SERVER],
        },
    ],
};

/**
 * 控制台-多集群管理文件类别
 */
export const FILE_TYPE = {
    KERNEROS: 0,
    CONFIGS: 1,
    PARAMES: 2,
} as const;

/**
 * 控制台-多集群管理组件类别
 */
export const CONFIG_ITEM_TYPE = {
    RADIO: 'RADIO',
    INPUT: 'INPUT',
    SELECT: 'SELECT',
    CHECKBOX: 'CHECKBOX',
    PASSWORD: 'PASSWORD',
    GROUP: 'GROUP',
    RADIO_LINKAGE: 'RADIO_LINKAGE',
    CUSTOM_CONTROL: 'CUSTOM_CONTROL',
} as const;

export const DEFAULT_COMP_VERSION = {
    [COMPONENT_TYPE_VALUE.FLINK]: '180',
    [COMPONENT_TYPE_VALUE.SPARK]: '210',
    [COMPONENT_TYPE_VALUE.SPARK_THRIFT]: '2.x',
    [COMPONENT_TYPE_VALUE.HIVE_SERVER]: '2.x',
} as const;

export const COMP_ACTION = {
    DELETE: 'DELETE',
    ADD: 'ADD',
} as const;

export const MAPPING_DATA_CHECK = {
    [COMPONENT_TYPE_VALUE.HIVE_SERVER]: COMPONENT_TYPE_VALUE.SPARK_THRIFT,
    [COMPONENT_TYPE_VALUE.SPARK_THRIFT]: COMPONENT_TYPE_VALUE.HIVE_SERVER,
} as const;

export const FLINK_DEPLOY_TYPE = {
    STANDALONE: 0,
    YARN: 1,
} as const;

export const FLINK_DEPLOY_NAME = {
    [FLINK_DEPLOY_TYPE.STANDALONE]: 'Flink on Standalone',
    [FLINK_DEPLOY_TYPE.YARN]: 'Flink on YARN',
} as const;

/**
 * @description 脚本模式下 选择源时 不支持选择的数据源类型
 */
export const notSupportSourceTypesInScript = [DATA_SOURCE_ENUM.Kylin, DATA_SOURCE_ENUM.IMPALA, DATA_SOURCE_ENUM.DORIS];

/**
 * @description 脚本模式下 选择目标时 不支持选择的数据源类型
 */
export const notSupportTargetTypesInScript = [
    DATA_SOURCE_ENUM.Kylin,
    DATA_SOURCE_ENUM.INFLUXDB,
    DATA_SOURCE_ENUM.IMPALA,
    DATA_SOURCE_ENUM.INFLUXDB,
];

/**
 * 数据同步任务 HBASE 是否行健二进制转换
 */
export enum BINARY_ROW_KEY_FLAG {
    FALSE = '0',
    TRUE = '1',
}

/**
 * 关系型数据库类型
 */
export const rdbmsDaType = {
    Binlog: 1,
    Poll: 2,
    CDC: 3,
    LOGMINER: 4,
};

/**
 * 任务语言类别
 */
export enum TASK_LANGUAGE {
    SPARKSQL = 'sparksql',
    HIVESQL = 'hivesql',
    FLINKSQL = 'flinksql',
    MYSQL = 'mysql',
    PLSQL = 'plsql',
    SQL = 'sql',
    JSON = 'json',
    PYTHON = 'python',
    SHELL = 'shell',
}

/**
 * FLinkSQL 的版本
 */
export const FLINK_VERSIONS = {
    FLINK_1_12: '1.12',
    FLINK_1_10: '1.10',
    FLINK_1_8: '1.8',
} as const;

/**
 * FlinkSQL 目前支持的版本
 */
export const FLINK_VERSION_TYPE = [{ value: FLINK_VERSIONS.FLINK_1_12, label: 'flink1.12', text: '1.12' }];

export enum KAFKA_DATA_TYPE {
    TYPE_JSON = 'dt_nest',
    TYPE_CSV = 'csv',
    TYPE_AVRO = 'avro',
    TYPE_COLLECT_JSON = 'json',
    TYPE_COLLECT_TEXT = 'text',
    TYPE_AVRO_CONFLUENT = 'avro-confluent',
}

export const KAFKA_DATA_LIST = [
    { text: 'json', value: KAFKA_DATA_TYPE.TYPE_JSON },
    { text: 'csv', value: KAFKA_DATA_TYPE.TYPE_CSV },
    { text: 'avro', value: KAFKA_DATA_TYPE.TYPE_AVRO },
];
// 表来源
export enum TABLE_SOURCE {
    DATA_ASSET = 1,
    DATA_CREATE = 0,
}
/** 源表中时间特征字段 */
export enum SOURCE_TIME_TYPE {
    PROC_TIME = 1,
    EVENT_TIME = 2,
}
export enum NODE_TYPE {
    KAFKA_11 = 'COMPONENT_SOURCE',
    MYSQL_DES = 2, // 维表mysql
    SELECT = 'COMPONENT_SELECT',
    WINDOW = 'COMPONENT_WINDOW',
    FILTER = 'COMPONENT_FILTER',
    JOIN = 6,
    MYSQL_RESULT = 'COMPONENT_RESULT', // 结果表mysql
}

// 脏数据保存方式
export enum DIRTY_DATA_SAVE {
    NO_SAVE = 'log',
    BY_MYSQL = 'jdbc',
}

export enum COLLECT_TYPE {
    ALL = 0,
    TIME = 1,
    FILE = 2,
    SCN = 3,
    BEGIN = 4,
    LSN = 5,
}
export enum QOS_TYPE {
    AT_MOST_ONCE = 0,
    AT_LEAST_ONCE = 1,
    EXACTLY_ONCE = 2,
}
// 任务类型
export enum SYNC_TYPE {
    BINLOG = 1,
    INTERVAL = 2,
    RESET = 3,
    LogMiner = 4,
}
/**
 * 数据操作类型
 */
export enum CAT_TYPE {
    /**
     * 插入
     */
    INSERT = 1,
    /**
     * 更新
     */
    UPDATE = 2,
    /**
     * 删除
     */
    DELETE = 3,
}
/**
 * 读取方式
 */
export enum READ_MODE_TYPE {
    /**
     * 固定消息长度读取
     */
    LENGTH = 'FixedLength',
    /**
     * 分割字符串读取
     */
    STRING = 'DelimiterBased',
}

export const READ_MODE_NAME = {
    [READ_MODE_TYPE.LENGTH]: '固定消息长度读取',
    [READ_MODE_TYPE.STRING]: '分割字符串读取',
};
export const RESTFUL_PROPTOCOL = [{ text: 'HTTP/HTTPS', value: 'http' }];
export const RESTFUL_METHOD = [
    { text: 'GET', value: 'get' },
    { text: 'POST', value: 'post' },
];
export const RESTFUL_RESP_MODE = [
    { text: 'TEXT', value: 'text' },
    { text: 'JSON', value: 'json' },
];
export const SLOAR_CONFIG_TYPE = {
    1: '选择已有Slot',
    2: '创建Slot',
};
export enum PARTITION_TYPE {
    HOUR = 0,
    DAY = 1,
}
export enum WRITE_TABLE_TYPE {
    AUTO = '0',
    HAND = '1',
}
/** 有版本区分的数据源的版本后缀 */
export const DATA_SOURCE_VERSION: Partial<{ [key in DATA_SOURCE_ENUM]: string }> = {
    [DATA_SOURCE_ENUM.HIVE]: '2.x',
    [DATA_SOURCE_ENUM.HIVE3X]: '3.x',
    [DATA_SOURCE_ENUM.ES]: '5.x',
    [DATA_SOURCE_ENUM.ES6]: '6.x',
    [DATA_SOURCE_ENUM.ES7]: '7.x',
    [DATA_SOURCE_ENUM.KAFKA_2X]: '2.x',
    [DATA_SOURCE_ENUM.KAFKA]: '1.x',
    [DATA_SOURCE_ENUM.KAFKA_11]: '0.11',
    [DATA_SOURCE_ENUM.KAFKA_10]: '0.10',
    [DATA_SOURCE_ENUM.KAFKA_HUAWEI]: 'HUAWEI',
    [DATA_SOURCE_ENUM.KAFKA_CONFLUENT]: '5.x',
};
export const NEST_KEYS = [
    { text: ', (英文逗号)', value: ',' },
    { text: '_ (下划线)', value: '_' },
    { text: '/ (斜杠)', value: '/' },
    { text: '. (英文句号)', value: '.' },
    { text: '- (中划线)', value: '-' },
    { text: ': (冒号)', value: ':' },
];
export const RESTFUL_STRATEGY = [
    { text: '立刻停止任务', value: 'stop' },
    { text: '连续三次后停止任务', value: 'retry' },
];
export enum INTERVAL_TYPE {
    EVERY = 0,
    WORKDAY = 1,
    CUSTOM = 2,
}
// 启停策略 - 启动方式
export enum STRATEGY_TYPE {
    RERUN = 0,
    CONTINUERUN = 1,
}
export const STRATEGY_START_TYPE = [
    { text: '重跑', value: STRATEGY_TYPE.RERUN },
    { text: '续跑', value: STRATEGY_TYPE.CONTINUERUN },
];
export enum CODE_TYPE {
    UTF_8 = 'utf-8',
    GBK_2312 = 'gbk2312',
    GB_2312 = 'gb2312',
}
export enum TABLE_TYPE {
    SOURCE_TABLE = 1,
    OUTPUT_TABLE = 2,
    DIMENSION_TABLE = 3,
}
export const hbaseColsText = `列簇: 字段名 类型 as 别名, 比如
user_info: name varchar as name
user_info: age varchar as age
一行一个字段, 采用回车分隔`;
export const hbaseColsText112 = `列簇 row<字段1 类型，字段2 类型>，比如
user_id row<id int>
user_info row<name varchar, age varchar>
一行一个列簇，采用回车分隔。列簇中的多个字段以逗号分隔`;

export const defaultColsText = '字段 类型, 比如 id int 一行一个字段';
export const DEFAULT_MAPPING_TEXT = `默认使用ElasticSearch自带的Mapping参数创建索引，用户可手动输入参数进行控制，比如
{
    "settings": {
        "number_of_shards": 5,
        "number_of_replicas": 3
    },
    "mappings": {
        "properties": {
            "user_id": {
                "type": "long"
            },
            "user_name": {
                "type": "text"
            },
            "user_age": {
                "type": "text"
            },
            "birthday": {
                "type": "date"
            }
        }
    }
}`;

export enum UDF_TYPE_VALUES {
    UDF = 0,
    UDTF = 1,
    UDAF = 2,
}

export const UDF_TYPE_NAMES = {
    [UDF_TYPE_VALUES.UDF]: 'UDF',
    [UDF_TYPE_VALUES.UDTF]: 'UDTF',
    [UDF_TYPE_VALUES.UDAF]: 'UDAF',
} as const;

export const FLINK_VERSION_TYPE_FILTER = [
    {
        value: FLINK_VERSIONS.FLINK_1_12,
        label: 'flink1.12',
        text: '1.12',
    },
];

export enum FLINK_SQL_TYPE {
    GUIDE = 0,
    SCRIPT = 1,
    GRAPH = 2,
}

// 强制停止类型
export enum IForceType {
    NOTFORCE = 0,
    ISFORCE = 1,
}

export enum CHECK_TYPE_VALUE {
    CHECK_POINT = 0,
    CHECK_POINT_FILE = 2,
}

/** 手动绑定弹框状态 */
export enum HAND_BUTTON_STATUS {
    READY_TIED = 0,
    TIED_SUCCESS = 1,
    TIED_MIDDLE = -1,
}

/** 绑定状态 */
export enum HAND_TIED_STATUS {
    TIED_WAIT = 0,
    TIED_SUCCESS = 1,
    TIED_FAIED = 2,
}

/** 绑定类型 */
export enum BIND_TYPE {
    AUTO = 1, // 强制绑定
    MANUAL = 0, // 手动绑定
}

/** metric 状态 */
export enum METRIC_STATUS_TYPE {
    NORMAL = 1,
    ABNORMAL = 2,
}

export enum UNIT_TYPE {
    B = 0,
    KB = 1,
    MB = 2,
    GB = 3,
    TB = 4,
}

export const SOURCE_INPUT_BPS_UNIT_TYPE: Partial<{ [key in UNIT_TYPE]: string }> = {
    [UNIT_TYPE.B]: 'Bps',
    [UNIT_TYPE.KB]: 'Kbps',
    [UNIT_TYPE.MB]: 'Mbps',
    [UNIT_TYPE.GB]: 'Gbps',
    [UNIT_TYPE.TB]: 'Tbps',
};

export const COLLECTION_BPS_UNIT_TYPE: Partial<{ [key in UNIT_TYPE]: string }> = {
    [UNIT_TYPE.B]: 'Bytes',
    [UNIT_TYPE.KB]: 'Kbytes',
    [UNIT_TYPE.MB]: 'Mbytes',
    [UNIT_TYPE.GB]: 'Gbytes',
    [UNIT_TYPE.TB]: 'Tbytes',
};

export const CHARTS_COLOR = ['#339CFF', '#15D275', '#5579ED', '#00C3E5', '#16DFB4', '#86E159'] as const;

export enum PythonVersionKind {
    py2 = 2,
    py3 = 3,
}

/**
 * For distinguish the same name but used in different dataSource type.
 *
 * There is a `path` field both in FTP and HDFS, but the two fileds are completely different,
 * So the one named `path` and another named `path|FTP`
 */
export const NAME_SEPARATOR = '|';
