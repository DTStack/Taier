// 常量

// Engine source 类型
export const ENGINE_SOURCE_TYPE = {
    HADOOP: 1,
    LIBRA: 2
}
// Engine source 类型选项
export const ENGINE_SOURCE_TYPE_OPTIONS: any = [
    {
        name: 'Hadoop',
        value: ENGINE_SOURCE_TYPE.HADOOP
    },
    {
        name: 'LibrA',
        value: ENGINE_SOURCE_TYPE.LIBRA
    }
]
export const ENGINE_TYPE_NAME = {
    HADOOP: 'Hadoop',
    LIBRA: 'LibrA'
}
export const TABLE_TYPE = {
    HIVE: 1,
    LIBRA: 2
}
export const TABLE_NAME_BY_TABLE_TYPE = {
    1: 'Hive',
    2: 'librA'
}
export const INTERNAL_OR_EXTERNAL_TABLE = {
    EXTERNAL: 'EXTERNAL',
    MANAGED: 'MANAGED'
}
export const KYLIN_ACTION = {
    BUILD: 'Build',
    MERGE: 'Merge',
    REFRESH: 'Refresh'
}

export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    POSTGRESQL: 4,
    RDBMS: 5,
    HDFS: 6,
    HIVE_1: 26,
    HIVE_2: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10,
    ES: 11,
    REDIS: 12,
    MONGODB: 13,
    DB2: 19,
    CARBONDATA: 20,
    LIBRASQL: 21,
    GBASE: 22,
    KYLIN: 23,
    KUDU: 24,
    CLICK_HOUSE: 25
}

/**
 * 引擎类型
 */
export const ENGINE_TYPE = {
    SPARK: 0,
    LIBRA: 1,
    KYLIN: 2,
    HAWQ: 3
}

export const DATA_SOURCE_TEXT = {
    1: 'MySQL',
    2: 'Oracle',
    3: 'SQLServer',
    4: 'PostgreSQL',
    6: 'HDFS',
    7: 'Hive',
    8: 'HBase',
    9: 'FTP',
    10: 'MaxCompute',
    11: 'ElasticSearch',
    12: 'Redis',
    13: 'MongoDB',
    19: 'DB2',
    20: 'DTinsightAnalytics',
    21: 'LibrA',
    22: 'GBase',
    23: 'Kylin',
    24: 'Kudu'
}

// 锁类型
export const LOCK_TYPE = {
    OFFLINE_TASK: 'BATCH_TASK',
    OFFLINE_SCRIPT: 'BATCH_SCRIPT',
    STREAM_TASK: 'STREAM_TASK'
}

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
    EGG: 4
}
export const RESOURCE_TYPE_MAP = {
    0: 'other',
    1: 'jar',
    2: 'py',
    3: 'zip',
    4: 'egg'
}

// 调度状态
export const SCHEDULE_STATUS = {
    NORMAL: 1,
    STOPPED: 2
}

//
export const APPLY_RESOURCE_TYPE = {
    TABLE: 0,
    FUNCTION: 1,
    SOURCE: 2
}

// 数据操作类型
export const CAT_TYPE = {
    INSERT: 1,
    UPDATE: 2,
    DELETE: 3
}
export const COLLECT_TYPE = {
    ALL: 0,
    TIME: 1,
    FILE: 2
}
/**
 * 项目创建方式 (引擎 创建 or 对接)
 */
export const PROJECT_CREATE_MODEL = {
    NORMAL: 0,
    IMPORT: 1
}
export const MENU_TYPE = {
    TASK: 'TaskManager',
    TASK_DEV: 'TaskDevelop',
    SCRIPT: 'ScriptManager',
    RESOURCE: 'ResourceManager',
    FUNCTION: 'FunctionManager',
    SPARKFUNC: 'SparkSQLFunction',
    LIBRAFUNC: 'LibraSQLFunction',
    LIBRASYSFUN: 'LibraSysFunc',
    COSTOMFUC: 'CustomFunction',
    SYSFUC: 'SystemFunction',
    TABLE: 'TableQuery'
}

export const PROJECT_TYPE = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}

// 发布的item类别
export const publishType = {
    TASK: 0,
    TABLE: 1,
    RESOURCE: 2,
    FUNCTION: 3
}

// 发布状态
export const publishStatus = {
    UNSUBMIT: 0,
    SUCCESS: 1,
    FAIL: 2
}

export const PROJECT_ROLE = { // 项目角色
    PROJECT_OWNER: 2, // 项目所有者
    TENANT_OWVER: 1, // 租户所有者
    VISITOR: 4 // 访客
}

export const RDB_TYPE_ARRAY: any = [ // sql/oracle/sqlserver/postgresql/db2
    DATA_SOURCE.MYSQL,
    DATA_SOURCE.ORACLE,
    DATA_SOURCE.SQLSERVER,
    DATA_SOURCE.POSTGRESQL,
    DATA_SOURCE.DB2,
    DATA_SOURCE.GBASE,
    DATA_SOURCE.CLICK_HOUSE
]

export const SUPPROT_SUB_LIBRARY_DB_ARRAY: any = [ // 支持分库分表的数据库类型r
    DATA_SOURCE.MYSQL
    // DATA_SOURCE.ORACLE,
    // DATA_SOURCE.SQLSERVER,
    // DATA_SOURCE.POSTGRESQL,
]

export const TASK_TYPE = { // 任务类型
    VIRTUAL_NODE: -1,
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
    IMPALA_SQL: 18// ImpalaSQL
}

export const LEARNING_TYPE = {// 深度学习框架
    TENSORFLOW: 0,
    MXNET: 1
}
export const PYTON_VERSION = {
    PYTHON2: 2,
    PYTHON3: 3
}

export const DATA_SYNC_TYPE = { // 数据同步配置模式
    GUIDE: 0,
    SCRIPT: 1
}

export const DATA_SYNC_MODE = { // 数据同步模式-正常/增量
    NORMAL: 0, // 正常
    INCREMENT: 1 // 增量
}

export const DEAL_MODEL_TYPE = {// python和深度学习操作类型
    EDIT: 1,
    RESOURCE: 0
}

export const SCRIPT_TYPE = { // 脚本类型
    SQL: 0,
    PYTHON2: 1,
    PYTHON3: 2,
    SHELL: 3,
    LIBRASQL: 4,
    IMPALA_SQL: 5
}

export const TASK_TYPE_ARRAY: any = [ //
    TASK_TYPE.SQL,
    TASK_TYPE.MR,
    TASK_TYPE.SYNC,
    TASK_TYPE.PYTHON,
    TASK_TYPE.VIRTUAL_NODE
]

export const HELP_DOC_URL = {
    INDEX: '/public/helpSite/batch/v3.0/Summary.html',
    DATA_SOURCE: '/public/helpSite/batch/v3.0/DataIntegration/Overview.html',
    DATA_SYNC: '/public/helpSite/batch/v3.0/DataIntegration/JobConfig.html',
    TASKPARAMS: '/public/helpSite/batch/v3.0/DataDevelop/ScheduleConfig.html#ParamConfig'
}
export const HADOOPMR_INITIAL_VALUE = {
    'mapper': 'org.apache.hadoop.examples.WordCount$TokenizerMapper',
    'reducer': 'org.apache.hadoop.examples.WordCount$IntSumReducer',
    'inputPath': 'input.txt',
    'outputPath': 'output3.txt'
}
export const TASK_STATUS = { // 任务状态
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
    DO_FAIL: 22
}

// 表模型规则
// "1":"层级",
// "2":"主题域",
// "3":"刷新频率",
// "4":"增量",
// "5":"自定义"
export const TABLE_MODEL_RULE = {
    LEVEL: 1,
    SUBJECT: 2,
    FREQUENCY: 3,
    INCREMENT: 4,
    CUSTOM: 5
}

export const hdfsFieldTypes: any = [ // hdfs 类型
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
    'DATE'
]

export const hbaseFieldTypes = [ // HBase 类型
    'BOOLEAN',
    'INT',
    'STRING',
    'LONG',
    'DOUBLE',
    'SHORT',
    'FLOAT'
]

export const mysqlFieldTypes: any = [// mysql类型
    'BIT',
    'BOOL',
    'TINYINT',
    'SMALLINT',
    'MEDIUMINT',
    'INT',
    'BIGINT',
    'FLOAT',
    'DOUBLE',
    'DECIMAL',
    'CHAR',
    'VARCHAR',
    'TINYTEXT',
    'TEXT',
    'MEDIUMTEXT',
    'LONGTEXT',
    'DATETIME',
    'DATE',
    'TIMESTAMP',
    'TIME',
    'YEAR'
]

// 表模型规则列表
export const tableModelRules: any = [{
    name: '层级',
    value: TABLE_MODEL_RULE.LEVEL
}, {
    name: '主题域',
    value: TABLE_MODEL_RULE.SUBJECT
}, {
    name: '增量',
    value: TABLE_MODEL_RULE.INCREMENT
}, {
    name: '刷新频率',
    value: TABLE_MODEL_RULE.FREQUENCY
}, {
    name: '自定义',
    value: TABLE_MODEL_RULE.CUSTOM
}]

// 实时任务状态过滤选项
// 16,17 等待运行
export const taskStatusFilter: any = [{
    text: '等待提交',
    value: TASK_STATUS.WAIT_SUBMIT
}, {
    text: '提交中',
    value: TASK_STATUS.SUBMITTING
}, {
    text: '提交失败',
    value: TASK_STATUS.SUBMIT_FAILED
}, {
    text: '等待运行',
    value: TASK_STATUS.WAIT_RUN
}, {
    text: '运行中',
    value: TASK_STATUS.RUNNING
}, {
    text: '取消',
    value: TASK_STATUS.STOPED
}, {
    text: '失败',
    value: TASK_STATUS.RUN_FAILED
}]

export const taskStatus = {
    'ALL': null as any,
    'UNSUBMIT': 0,
    'WAITING_RUN': 16,
    'FINISHED': 5,
    'RUNNING': 4,
    'CANCELED': 7,
    'FAILED': 8,
    'SUBMITFAILD': 9,
    'PARENTFAILED': 21,
    'SUBMITTING': 10,
    'FROZEN': 18
}

// 离线任务状态过滤选项
// UNSUBMIT 0;
// RUNNING  4;
// FINISHED 5;
// FAILED :8;
// WAITENGINE : 16:
// SUBMITTING: 10;
// CANCELED: 7;
// FROZEN: 18;
export const offlineTaskStatusFilter: any = [{
    id: 1,
    text: '等待提交',
    value: 0
}, {
    id: 2,
    text: '提交中',
    value: 10
}, {
    id: 3,
    text: '等待运行',
    value: 16
}, {
    id: 4,
    text: '运行中',
    value: 4
}, {
    id: 5,
    text: '成功',
    value: 5
}, {
    id: 6,
    text: '取消',
    value: 7
}, {
    id: 7,
    text: '提交失败',
    value: 9
}, {
    id: 8,
    text: '运行失败',
    value: 8
}, {
    id: 9,
    text: '上游失败',
    value: 21
}, {
    id: 10,
    text: '冻结',
    value: 18
}]

export const offlineTaskTypeFilter: any = [
    {
        id: 0,
        text: '虚节点',
        value: TASK_TYPE.VIRTUAL_NODE
    }, {
        id: 1,
        text: 'SparkSQL',
        value: TASK_TYPE.SQL
    }, {
        id: 2,
        text: 'Spark',
        value: TASK_TYPE.MR
    }, {
        id: 3,
        text: '数据同步',
        value: TASK_TYPE.SYNC
    }, {
        id: 11,
        text: '工作流',
        value: TASK_TYPE.WORKFLOW
    }, {
        id: 5,
        text: 'PySpark',
        value: TASK_TYPE.PYTHON
    }, {
        id: 6,
        text: '深度学习',
        value: TASK_TYPE.DEEP_LEARNING
    }, {
        id: 7,
        text: 'Python',
        value: TASK_TYPE.PYTHON_23
    }, {
        id: 8,
        text: 'Shell',
        value: TASK_TYPE.SHELL
    }, {
        id: 9,
        text: '机器学习',
        value: TASK_TYPE.ML
    }, {
        id: 10,
        text: 'HadoopMR',
        value: TASK_TYPE.HAHDOOPMR
    }, {
        id: 12,
        text: 'CarbonSQL',
        value: TASK_TYPE.CARBONSQL
    }, {
        id: 13,
        text: 'Notebook',
        value: TASK_TYPE.NOTEBOOK
    }, {
        id: 14,
        text: '算法实验',
        value: TASK_TYPE.EXPERIMENT
    }, {
        id: 15,
        text: 'LibraSQL',
        value: TASK_TYPE.LIBRASQL
    }, {
        id: 16,
        text: 'ImpalaSQL',
        value: TASK_TYPE.IMPALA_SQL
    }]

export const offlineTaskPeriodFilter: any = [{
    id: 1,
    text: '分钟任务',
    value: 0
}, {
    id: 2,
    text: '小时任务',
    value: 1
}, {
    id: 3,
    text: '天任务',
    value: 2
}, {
    id: 4,
    text: '周任务',
    value: 3
}, {
    id: 5,
    text: '月任务',
    value: 4
}]

export const ScheduleTypeFilter: any = [{ // 调度过滤
    text: '周期调度',
    value: 0
}, {
    text: '补数据',
    value: 1
}]

export const AlarmStatusFilter: any = [{ // 告警状态过滤选项
    text: '正常',
    value: 0
}, {
    text: '关闭',
    value: 1
}]

export const jobTypes: any = [{ // 调度类型 0-周期调度 ， 1-补数据类型
    text: '全部',
    value: ''
}, {
    text: '周期调度',
    value: 0
}, {
    text: '补数据',
    value: 1
}]

export const DataSourceTypeFilter: any = [{ // 离线数据源类型过滤选项
    text: 'MySQL',
    value: DATA_SOURCE.MYSQL
}, {
    text: 'Oracle',
    value: DATA_SOURCE.ORACLE
}, {
    text: 'SQLServer',
    value: DATA_SOURCE.SQLSERVER
}, {
    text: 'PostgreSQL',
    value: DATA_SOURCE.POSTGRESQL
}, {
    text: 'HDFS',
    value: DATA_SOURCE.HDFS
}, {
    text: 'Hive2.x',
    value: DATA_SOURCE.HIVE_2
}, {
    text: 'Hive1.x',
    value: DATA_SOURCE.HIVE_1
}, {
    text: 'HBase',
    value: DATA_SOURCE.HBASE
}, {
    text: 'MaxCompute',
    value: DATA_SOURCE.MAXCOMPUTE
}, {
    text: 'FTP',
    value: DATA_SOURCE.FTP
}, {
    text: 'ElasticSearch',
    value: DATA_SOURCE.ES
}, {
    text: 'Redis',
    value: DATA_SOURCE.REDIS
}, {
    text: 'MongoDB',
    value: DATA_SOURCE.MONGODB
}, {
    text: 'ClickHouse',
    value: DATA_SOURCE.CLICK_HOUSE
}];

export const propEditorOptions = { // 编辑器选项
    mode: 'text/x-properties',
    lint: true,
    indentWithTabs: true,
    smartIndent: true,
    lineNumbers: true,
    autofocus: false
}

export const jsonEditorOptions = { // json编辑器选项
    mode: 'application/json',
    lint: true,
    indentWithTabs: true,
    smartIndent: true,
    lineNumbers: true,
    autofocus: false,
    matchBrackets: true
}

export const transformRuleType = { // 整库迁移高级设置转换类型
    1: 'nameRule',
    2: 'columnRule',
    3: 'typeRule'
}

export const originTypeTransformRule: any = [ // 整库迁移高级设置字段转换规则
    'tinyint',
    'smallint',
    'mediumint',
    'int',
    'bigint',
    'varchar',
    'char',
    'tinytext',
    'text',
    'mediumtext',
    'longtext',
    'year',
    'float',
    'double',
    'decimal',
    'date',
    'datetime',
    'timestamp',
    'time',
    'bit',
    'bool'
]

export const targetTypeTransformRule: any = [ // 整库迁移高级设置字段转换规则
    'BIGINT',
    'STRING',
    'DOUBLE',
    'TIMESTAMP',
    'BOOLEAN'
]

export const formItemLayout: any = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

export const tailFormItemLayout: any = { // 表单末尾布局
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0
        },
        sm: {
            span: 14,
            offset: 6
        }
    }
}

export const lineAreaChartOptions: any = {// 堆叠折现图默认选项
    title: {
        text: '堆叠区域图',
        textStyle: {
            fontSize: 12
        },
        textAlign: 'left'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
    legend: {
        data: ['邮件营销', '联盟广告', '视频广告']
    },
    toolbox: {
        feature: {
            saveAsImage: {
                show: false
            }
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            boundaryGap: false,
            data: [],
            axisTick: {
                show: true
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD'
                }
            },
            axisLabel: {
                textStyle: {
                    color: '#666666'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            splitLine: {
                color: '#666666'
            }
        }
    ],
    yAxis: [
        {
            name: '数量(个)',
            type: 'value',
            axisLabel: {
                formatter: '{value}',
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            nameLocation: 'end',
            nameGap: 20,
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    color: '#DDDDDD',
                    type: 'dashed'
                }
            }
        }
    ],
    series: []
};

export const pieChartOptions: any = {
    title: {
        text: '某站点用户访问来源',
        subtext: '',
        textAlign: 'left',
        textBaseline: 'top',
        textStyle: {
            fontSize: 14,
            fontWeight: 'bold'
        },
        x: 'left'
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        right: 'right',
        top: 'middle',
        data: []
    },
    color: ['#5d99f2', '#F5A623', '#9EABB2', '#8bc34a'],
    series: [
        {
            name: '访问来源',
            type: 'pie',
            radius: '50%',
            center: ['50%', '45%'],
            data: []
        }
    ]
};

export const defaultBarOption: any = {
    title: {
        text: '世界人口总量',
        textStyle: {
            fontSize: 12,
            fontWeight: 'bold'
        }
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    color: ['#5dd1f2', '#5d99f2', '#9a64fb', '#5df2c3', '#eeeeee'],
    legend: {
        data: ['2011年', '2012年']
    },
    grid: {
        left: '3%',
        right: '3%',
        top: 35,
        bottom: '1%',
        show: false,
        containLabel: true
    },
    xAxis: {
        type: 'value',
        show: false,
        boundaryGap: [0, 0.01]
    },
    yAxis: {
        type: 'category',
        data: ['巴西', '美国', '印度', '中国', '世界人口(万)'],
        axisLine: {
            lineStyle: {
                color: '#dddddd',
                width: 2
            }
        },
        position: 'top',
        axisLabel: {
            textStyle: {
                color: '#666666'
            }
        },
        axisTick: {
            show: false
        }
    },
    series: [
        {
            name: '',
            type: 'bar',
            barWidth: 20,
            silent: true,
            barGap: '100%',
            barCategoryGap: 25,
            barMinHeight: 50,
            cursor: 'initial',
            center: [-10, '0%'],
            label: {
                normal: {
                    show: true,
                    formatter: '{c} GB',
                    position: 'insideTopLeft',
                    offset: [0, -2]
                }
            },
            data: [23489, 29034, 104970, 131744, 630230]
        }
    ]
};
