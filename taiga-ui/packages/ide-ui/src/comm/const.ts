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

/**
 * 存储项目ID的 key 名称
 */
export const PROJECT_KEY = 'project_id';

export const formItemLayout: any = {
    // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};

// 发布的item类别
export const publishType = {
    TASK: 0,
    TABLE: 1,
    RESOURCE: 2,
    FUNCTION: 3,
    PRODUCER: 4,
};

export const TASK_TYPE = {
    // 任务类型
    VIRTUAL_NODE: -1,
    /**
     * SparkSQL
     */
    SQL: 0,
    MR: 1,
    SYNC: 2,
    PYTHON: 3,
    R: 4,
    DEEP_LEARNING: 5,
    PYTHON_23: 6,
    SHELL: 7,
    ML: 8,
    HAHDOOPMR: 9,
    WORKFLOW: 10, // 工作流
    DATA_COLLECTION: 11, // 实时采集
    CARBONSQL: 12, // CarbonSQL
    NOTEBOOK: 13,
    EXPERIMENT: 14,
    LIBRASQL: 15,
    CUBE_KYLIN: 16,
    HIVESQL: 17,
    IMPALA_SQL: 18, // ImpalaSQL
    TI_DB_SQL: 19,
    ORACLE_SQL: 20,
    GREEN_PLUM_SQL: 21,
    TENSORFLOW_1X: 22,
    KERAS: 23,
    PRESTO: 24,
    PYTORCH: 25,
    INCEPTOR: 28,
    SHELL_AGENT: 29,
    ADB: 30,
};

export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    POSTGRESQL: 4,
    RDBMS: 5,
    HDFS: 6,
    HIVE_2: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10,
    ES: 11,
    REDIS: 12,
    MONGODB: 13,
    ADS: 15,
    DB2: 19,
    CARBONDATA: 20,
    LIBRASQL: 21,
    GBASE: 22,
    KYLIN: 23,
    KUDU: 24,
    CLICK_HOUSE: 25,
    HIVE_1: 27,
    POLAR_DB: 28,
    IMPALA: 29,
    PHOENIX: 30,
    TI_DB: 31,
    SQLSERVER_2017_LATER: 32,
    /**
     * 达梦数据库
     */
    DM: 35,
    GREEN_PLUM: 36,
    PHOENIX5: 38,
    KINGBASE: 40,
    HIVE_SERVER: 45,
    HIVE_3: 50,
    S3: 51,
    INCEPTOR: 52,
    ADB: 54,
    INFLUXDB: 55,
    OPEN_TS_DB: 56,
};

export const OPENTSDB_CULUMNS = [
    {
        type: 'STRING',
        key: 'metric',
    },
    {
        type: 'STRING',
        key: 'timestamp',
    },
    {
        type: 'STRING',
        key: 'value',
    },
    {
        type: 'STRING',
        key: 'tags',
    },
];

export const SUPPROT_SUB_LIBRARY_DB_ARRAY: any = [
    // 支持分库分表的数据库类型r
    DATA_SOURCE.MYSQL,
    // DATA_SOURCE.ORACLE,
    // DATA_SOURCE.SQLSERVER,
    // DATA_SOURCE.POSTGRESQL,
];

export const HELP_DOC_URL = {
    INDEX: '/public/helpSite/batch/v3.0/Summary.html',
    DATA_SOURCE: '/public/helpSite/batch/v3.0/DataIntegration/Overview.html',
    DATA_SYNC: '/public/helpSite/batch/v3.0/DataIntegration/JobConfig.html',
    TASKPARAMS:
        '/public/helpSite/batch/v3.0/DataDevelop/ScheduleConfig.html#ParamConfig',
};

export const DATA_SYNC_MODE = {
    // 数据同步模式-正常/增量
    NORMAL: 0, // 正常
    INCREMENT: 1, // 增量
};

export const DATA_SOURCE_TEXT = {
    [DATA_SOURCE.MYSQL]: 'MySQL',
    [DATA_SOURCE.ORACLE]: 'Oracle',
    [DATA_SOURCE.SQLSERVER]: 'SQLServer',
    [DATA_SOURCE.POSTGRESQL]: 'PostgreSQL',
    [DATA_SOURCE.HDFS]: 'HDFS',
    [DATA_SOURCE.HIVE_3]: 'Hive3',
    [DATA_SOURCE.HIVE_2]: 'Hive2',
    [DATA_SOURCE.HIVE_1]: 'Hive1',
    [DATA_SOURCE.HIVE_SERVER]: 'SparkThrift2.x',
    [DATA_SOURCE.HBASE]: 'HBase',
    [DATA_SOURCE.FTP]: 'FTP',
    [DATA_SOURCE.MAXCOMPUTE]: 'MaxCompute',
    [DATA_SOURCE.ES]: 'ElasticSearch',
    [DATA_SOURCE.REDIS]: 'Redis',
    [DATA_SOURCE.MONGODB]: 'MongoDB',
    [DATA_SOURCE.DB2]: 'DB2',
    [DATA_SOURCE.CARBONDATA]: 'DTinsightAnalytics',
    [DATA_SOURCE.LIBRASQL]: 'GaussDB', // 更名
    [DATA_SOURCE.GBASE]: 'GBase',
    [DATA_SOURCE.KYLIN]: 'Kylin',
    [DATA_SOURCE.KUDU]: 'Kudu',
    [DATA_SOURCE.CLICK_HOUSE]: 'ClickHouse',
    [DATA_SOURCE.POLAR_DB]: 'PolarDB',
    [DATA_SOURCE.IMPALA]: 'Impala',
    [DATA_SOURCE.PHOENIX]: 'Phoenix',
    [DATA_SOURCE.PHOENIX5]: 'Phoenix5',
    [DATA_SOURCE.TI_DB]: 'TiDB',
    [DATA_SOURCE.DM]: 'DMDB',
    [DATA_SOURCE.GREEN_PLUM]: 'Greenplum',
    [DATA_SOURCE.KINGBASE]: 'Kingbase',
    [DATA_SOURCE.S3]: 'AWS S3',
    [DATA_SOURCE.INCEPTOR]: 'Inceptor',
    [DATA_SOURCE.ADB]: 'AnalyticDB PostgreSQL',
    [DATA_SOURCE.INFLUXDB]: 'InfluxDB',
    [DATA_SOURCE.OPEN_TS_DB]: 'OpenTSDB',
};

export const RDB_TYPE_ARRAY: any = [
    // sql/oracle/sqlserver/postgresql/db2
    DATA_SOURCE.MYSQL,
    DATA_SOURCE.ORACLE,
    DATA_SOURCE.SQLSERVER,
    DATA_SOURCE.POSTGRESQL,
    DATA_SOURCE.ADB,
    DATA_SOURCE.DB2,
    DATA_SOURCE.GBASE,
    DATA_SOURCE.POLAR_DB,
    DATA_SOURCE.CLICK_HOUSE,
    DATA_SOURCE.TI_DB,
    DATA_SOURCE.DM,
    DATA_SOURCE.GREEN_PLUM,
    DATA_SOURCE.KINGBASE,
    DATA_SOURCE.ADB,
];

export const MENU_TYPE = {
    TASK: 'TaskManager',
    TASK_DEV: 'TaskDevelop',
    SCRIPT: 'ScriptManager',
    RESOURCE: 'ResourceManager',
    FUNCTION: 'FunctionManager',
    PROCEDURE: 'ProcedureManager',
    SPARKFUNC: 'SparkSQLFunction',
    LIBRAFUNC: 'LibraSQLFunction',
    LIBRASYSFUN: 'LibraSysFunc',
    COSTOMFUC: 'CustomFunction',
    COSTOMPROD: 'CustomProcedure',
    SYSFUC: 'SystemFunction',
    COMPONENT: 'ComponentManager',
    TABLE: 'TableQuery',
    TIDB_FUNC: 'TiDBSQLFunction',
    TIDB_SYS_FUNC: 'TiDBSysFunc',
    ORACLE_FUNC: 'OracleSQLFunction',
    ORACLE_SYS_FUNC: 'OracleSysFunc',
    GREEN_PLUM: 'GreenPlumSQLFunction',
    GREEN_PLUM_FUNC: 'GreenPlumCustomFunction',
    GREEN_PLUM_SYS_FUNC: 'GreenPlumSysFunc',
    GREEN_PLUM_PROD: 'ProcedureFunction',
};

// Engine source 类型
export const ENGINE_SOURCE_TYPE = {
    HADOOP: 1,
    LIBRA: 2,
    TI_DB: 4,
    ORACLE: 5,
    GREEN_PLUM: 6,
    ADB: 9,
};

export const taskStatus = {
    ALL: null as any,
    UNSUBMIT: 0,
    WAITING_RUN: 16,
    FINISHED: 5,
    RUNNING: 4,
    CANCELED: 7,
    FAILED: 8,
    SUBMITFAILD: 9,
    PARENTFAILED: 21,
    SUBMITTING: 10,
    FROZEN: 18,
};

export const offlineTaskStatusFilter: any = [
    {
        id: 1,
        text: '等待提交',
        value: 0,
    },
    {
        id: 2,
        text: '提交中',
        value: 10,
    },
    {
        id: 3,
        text: '等待运行',
        value: 16,
    },
    {
        id: 4,
        text: '运行中',
        value: 4,
    },
    {
        id: 5,
        text: '成功',
        value: 5,
    },
    {
        id: 6,
        text: '手动取消',
        value: 7,
    },
    {
        id: 11,
        text: '自动取消',
        value: 24,
    },
    {
        id: 7,
        text: '提交失败',
        value: 9,
    },
    {
        id: 8,
        text: '运行失败',
        value: 8,
    },
    {
        id: 9,
        text: '上游失败',
        value: 21,
    },
    {
        id: 10,
        text: '冻结',
        value: 18,
    },
];

export const TASK_STATUS = {
    // 任务状态
    ALL: null as any,
    WAIT_SUBMIT: 0,
    CREATED: 1,
    INVOKED: 2,
    DEPLOYING: 3,
    RUNNING: 4,
    FINISHED: 5,
    STOPING: 6,
    STOPED: 7,
    RUN_FAILED: 8, // 运行失败
    SUBMIT_FAILED: 9, // 提交失败
    PARENT_FAILD: 21, // 上游失败
    SUBMITTING: 10,
    RESTARTING: 11,
    SET_SUCCESS: 12,
    KILLED: 13,
    TASK_STATUS_NOT_FOUND: 15, // 暂时无法获取任务状态
    WAIT_RUN: 16,
    WAIT_COMPUTE: 17,
    FROZEN: 18,
    DO_FAIL: 22,
    AUTO_CANCEL: 24, // 自动取消
};

export const SCRIPT_TYPE = {
    // 脚本类型
    SQL: 0,
    PYTHON2: 1,
    PYTHON3: 2,
    SHELL: 3,
    LIBRASQL: 4,
    IMPALA_SQL: 5,
    TI_DB_SQL: 6,
};

export const hdfsFieldTypes: any = [
    // hdfs 类型
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

export const hbaseFieldTypes = [
    // HBase 类型
    'BOOLEAN',
    'INT',
    'STRING',
    'LONG',
    'DOUBLE',
    'SHORT',
    'FLOAT',
];

// 资源类型
export const RESOURCE_TYPE = {
    0: 'other',
    OTHER: 0,
    1: 'jar',
    JAR: 1,
    2: 'py',
    PY: 2,
    3: 'zip',
    ZIP: 3,
    4: 'egg',
    EGG: 4,
};
