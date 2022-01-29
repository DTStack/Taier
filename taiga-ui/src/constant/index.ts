import type { ISubMenuProps } from '@dtinsight/molecule/esm/components';

export const TASK_RUN_ID = 'task.run';
export const TASK_STOP_ID = 'task.stop';
export const TASK_CREATE_ID = 'task.create';
export const TASK_SUBMIT_ID = 'task.submit';
export const TASK_SAVE_ID = 'task.save';
export const TASK_RELEASE_ID = 'task.release';
export const TASK_OPS_ID = 'task_ops';
export const TASK_ATTRIBUTONS = 'task.attributions';
export const TASK_PARAMS_ID = 'task.params';
export const TASK_SCHEDULE_CONFIG = 'task.schedule.config';

export const ENV_PARAMS = 'env.params';

export const OUTPUT_LOG = 'panel.output.log';

export const FOLDERTREE_CONTEXT_EDIT = 'explorer.edit';

export const CREATE_TASK_PREFIX = 'createTask';
export const CREATE_FOLDER_PREFIX = 'createFolder';
export const EDIT_TASK_PREFIX = 'editTask';
export const EDIT_FOLDER_PREFIX = 'editFolder';

export const CREATE_DATASOURCE_PREFIX = 'create-datasource';

/**
 * 高可用配置的 placeholder
 */
export const HDFSCONG = `{
    "dfs.nameservices": "defaultDfs",
    "dfs.ha.namenodes.defaultDfs": "namenode1",
    "dfs.namenode.rpc-address.defaultDfs.namenode1": "",
    "dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
    }`;

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
	VIRTUAL_NODE = -1,
	/**
	 * SparkSQL
	 */
	SQL = 0,
	MR = 1,
	SYNC = 2,
	PYTHON = 3,
	R = 4,
	DEEP_LEARNING = 5,
	PYTHON_23 = 6,
	SHELL = 7,
	ML = 8,
	HAHDOOPMR = 9,
	WORKFLOW = 10, // 工作流
	DATA_COLLECTION = 11, // 实时采集
	CARBONSQL = 12, // CarbonSQL
	NOTEBOOK = 13,
	EXPERIMENT = 14,
	LIBRASQL = 15,
	CUBE_KYLIN = 16,
	HIVESQL = 17,
	IMPALA_SQL = 18, // ImpalaSQL
	TI_DB_SQL = 19,
	ORACLE_SQL = 20,
	GREEN_PLUM_SQL = 21,
	TENSORFLOW_1X = 22,
	KERAS = 23,
	PRESTO = 24,
	PYTORCH = 25,
	INCEPTOR = 28,
	SHELL_AGENT = 29,
	ADB = 30,
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
	DRDS = 72,
	UPDRDB = 73,
	UPRedis = 74,
	CSP_S3 = 75,
}

/**
 * 目录结构类型
 */
export enum CATELOGUE_TYPE {
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
 * 数据源对应名称
 */
export const DATA_SOURCE_TEXT: Partial<{ [key in DATA_SOURCE_ENUM]: string }> = {
	[DATA_SOURCE_ENUM.MYSQL]: 'MySQL',
	[DATA_SOURCE_ENUM.ORACLE]: 'Oracle',
	[DATA_SOURCE_ENUM.SQLSERVER]: 'SQLServer',
	[DATA_SOURCE_ENUM.POSTGRESQL]: 'PostgreSQL',
	[DATA_SOURCE_ENUM.HDFS]: 'HDFS',
	[DATA_SOURCE_ENUM.HIVE3X]: 'Hive3',
	[DATA_SOURCE_ENUM.HIVE]: 'Hive2',
	[DATA_SOURCE_ENUM.HIVE1X]: 'Hive1',
	[DATA_SOURCE_ENUM.SPARKTHRIFT]: 'SparkThrift2.x',
	[DATA_SOURCE_ENUM.HBASE]: 'HBase',
	[DATA_SOURCE_ENUM.FTP]: 'FTP',
	[DATA_SOURCE_ENUM.MAXCOMPUTE]: 'MaxCompute',
	[DATA_SOURCE_ENUM.ES]: 'ElasticSearch',
	[DATA_SOURCE_ENUM.REDIS]: 'Redis',
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
	PARENT_FAILD = 21, // 上游失败
	SUBMITTING = 10,
	RESTARTING = 11,
	SET_SUCCESS = 12,
	KILLED = 13,
	TASK_STATUS_NOT_FOUND = 15, // 暂时无法获取任务状态
	WAIT_RUN = 16,
	WAIT_COMPUTE = 17,
	FROZEN = 18,
	DO_FAIL = 22,
	AUTO_CANCEL = 24, // 自动取消
}

// 离线任务状态
export const OFFLINE_TASK_STATUS_FILTERS = [
	{
		id: 1,
		text: '等待提交',
		value: TASK_STATUS.WAIT_SUBMIT,
	},
	{
		id: 2,
		text: '提交中',
		value: TASK_STATUS.SUBMITTING,
	},
	{
		id: 3,
		text: '等待运行',
		value: TASK_STATUS.WAIT_RUN,
	},
	{
		id: 4,
		text: '运行中',
		value: TASK_STATUS.RUNNING,
	},
	{
		id: 5,
		text: '成功',
		value: TASK_STATUS.FINISHED,
	},
	{
		id: 6,
		text: '手动取消',
		value: TASK_STATUS.STOPED,
	},
	{
		id: 11,
		text: '自动取消',
		value: TASK_STATUS.AUTO_CANCEL,
	},
	{
		id: 7,
		text: '提交失败',
		value: TASK_STATUS.SUBMIT_FAILED,
	},
	{
		id: 8,
		text: '运行失败',
		value: TASK_STATUS.RUN_FAILED,
	},
	{
		id: 9,
		text: '上游失败',
		value: TASK_STATUS.PARENT_FAILD,
	},
	{
		id: 10,
		text: '冻结',
		value: TASK_STATUS.FROZEN,
	},
];

/**
 * 脚本类型
 */
export enum SCRIPT_TYPE {
	SQL = 0,
	PYTHON2 = 1,
	PYTHON3 = 2,
	SHELL = 3,
	LIBRASQL = 4,
	IMPALA_SQL = 5,
	TI_DB_SQL = 6,
}

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
	NORMAL = 0,
	FORZON = 1,
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

export enum DRAWER_MENU_ENUM {
	/**
	 * 任务管理
	 */
	TASK = 'task',
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

export const OPERATIONS = [
	{
		id: DRAWER_MENU_ENUM.TASK,
		name: '任务管理',
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

export enum FOLDERMENU_TOOLTIPS {
	UPLOAD = 'upload',
	REPLACE = 'replace',
	CREATE = 'create-folder',
}

/**
 * 资源管理 tooltip
 */
export const folderMenu: ISubMenuProps = [
	{
		id: FOLDERMENU_TOOLTIPS.UPLOAD,
		name: '上传资源',
	},
	{
		id: FOLDERMENU_TOOLTIPS.REPLACE,
		name: '替换资源',
	},
	{
		id: FOLDERMENU_TOOLTIPS.CREATE,
		name: '新建文件夹',
	},
];

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
export const TABS_TITLE_KEY = {
	/**
	 * 公共组件
	 */
	COMMON: 0,
	/**
	 * 资源调度组件
	 */
	SOURCE: 1,
	/**
	 * 存储组件
	 */
	STORE: 2,
	/**
	 * 计算组件
	 */
	COMPUTE: 3,
};

/**
 * 组件枚举
 */
export const COMPONENT_TYPE_VALUE = {
	FLINK: 0,
	SPARK: 1,
	HDFS: 2,
	YARN: 3,
	SPARK_THRIFT: 4,
	HIVE_SERVER: 5,
	SFTP: 6,
} as const;

export const COMPONENT_CONFIG_NAME = {
	[COMPONENT_TYPE_VALUE.FLINK]: 'Flink',
	[COMPONENT_TYPE_VALUE.SPARK]: 'Spark',
	[COMPONENT_TYPE_VALUE.HDFS]: 'HDFS',
	[COMPONENT_TYPE_VALUE.YARN]: 'YARN',
	[COMPONENT_TYPE_VALUE.SPARK_THRIFT]: 'SparkThrift',
	[COMPONENT_TYPE_VALUE.HIVE_SERVER]: 'HiveServer',
	[COMPONENT_TYPE_VALUE.SFTP]: 'SFTP',
} as const;

export const VERSION_TYPE = {
	[COMPONENT_TYPE_VALUE.FLINK]: 'Flink',
	[COMPONENT_TYPE_VALUE.HIVE_SERVER]: 'HiveServer',
	[COMPONENT_TYPE_VALUE.SPARK]: 'Spark',
	[COMPONENT_TYPE_VALUE.SPARK_THRIFT]: 'SparkThrift',
} as const;

export const CONFIG_BUTTON_TYPE = {
	[TABS_TITLE_KEY.COMMON]: [
		{
			code: COMPONENT_TYPE_VALUE.SFTP,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.SFTP],
		},
	],
	[TABS_TITLE_KEY.SOURCE]: [
		{
			code: COMPONENT_TYPE_VALUE.YARN,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.YARN],
		},
	],
	[TABS_TITLE_KEY.STORE]: [
		{
			code: COMPONENT_TYPE_VALUE.HDFS,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.HDFS],
		},
	],
	[TABS_TITLE_KEY.COMPUTE]: [
		{
			code: COMPONENT_TYPE_VALUE.SPARK,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.SPARK],
		},
		{
			code: COMPONENT_TYPE_VALUE.SPARK_THRIFT,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.SPARK_THRIFT],
		},
		{
			code: COMPONENT_TYPE_VALUE.FLINK,
			componentName: (COMPONENT_CONFIG_NAME as any)[COMPONENT_TYPE_VALUE.FLINK],
		},
	],
};

export const FILE_TYPE = {
	KERNEROS: 0,
	CONFIGS: 1,
	PARAMES: 2,
} as const;

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

export const CONFIG_FILE_DESC = {
	[COMPONENT_TYPE_VALUE.YARN]: 'zip格式，至少包括yarn-site.xml和core-site.xml',
	[COMPONENT_TYPE_VALUE.HDFS]: 'zip格式，至少包括core-site.xml、hdfs-site.xml、hive-site.xml',
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
