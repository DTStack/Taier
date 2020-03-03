
// 任务状态
export const TASK_STATE: any = {
    UNSUBMIT: 0,
    CREATED: 1,
    SCHEDULED: 2,
    DEPLOYING: 3,
    RUNNING: 4,
    FINISHED: 5,
    CANCELLING: 6,
    CANCELED: 7,
    FAILED: 8,
    SUBMITFAILD: 9,
    SUBMITTING: 10,
    RESTARTING: 11,
    MANUALSUCCESS: 12,
    KILLED: 13,
    SUBMITTED: 14,
    NOTFOUND: 15,
    WAITENGINE: 16,
    WAITCOMPUTE: 17,
    FROZEN: 18,
    ENGINEACCEPTED: 19,
    ENGINEDISTRIBUTE: 20
}
// 常量
export const DATA_SOURCE: any = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10,
    ADSMAXCOMPUTE: 11
}
export const ENGINE_TYPE = {
    HADOOP: 1,
    LIBRA: 2,
    TI_DB: 4
}
export const ENGINE_TYPE_NAME = {
    HADOOP: 'Hadoop',
    LIBRA: 'LibrA',
    TI_DB: 'TiDB'
}

export const ENGIN_TYPE_TEXT = {
    [ENGINE_TYPE.HADOOP]: 'Hadoop',
    [ENGINE_TYPE.LIBRA]: 'LibrA',
    [ENGINE_TYPE.TI_DB]: 'TiDB'
}

export const ENGINE_TYPE_ARRAY = [{ // 引擎类型下拉框数据
    name: 'Hadoop',
    value: ENGINE_TYPE_NAME.HADOOP
}, {
    name: 'LibrA',
    value: ENGINE_TYPE_NAME.LIBRA
}, {
    name: 'TiDB',
    value: ENGINE_TYPE_NAME.TI_DB
}];

export const COMPONENT_TYPE_VALUE = {
    FLINK: 0,
    SPARK: 1,
    LEARNING: 2,
    DTYARNSHELL: 3,
    HDFS: 4,
    YARN: 5,
    SPARKTHRIFTSERVER: 6,
    CARBONDATA: 7,
    LIBRASQL: 8,
    HIVESERVER: 9,
    SFTP: 10,
    IMPALASQL: 11,
    TIDB_SQL: 12
}
export const DEFAULT_COMP_TEST: any = { // 测试结果默认数据
    flinkTestResult: {},
    sparkTestResult: {},
    dtYarnShellTestResult: {},
    learningTestResult: {},
    hdfsTestResult: {},
    yarnTestResult: {},
    sparkThriftTestResult: {},
    carbonTestResult: {},
    hiveServerTestResult: {},
    libraSqlTestResult: {},
    tidbSqlTestResult: {},
    impalaSqlTestResult: {},
    sftpTestResult: {}
}
export const DEFAULT_COMP_REQUIRED: any = { // 必填默认数据
    flinkShowRequired: false,
    sparkShowRequired: false,
    dtYarnShellShowRequired: false,
    learningShowRequired: false,
    hdfsShowRequired: false,
    yarnShowRequired: false,
    hiveShowRequired: false,
    carbonShowRequired: false,
    hiveServerShowRequired: false,
    libraShowRequired: false,
    impalaSqlRequired: false,
    sftpShowRequired: false
}
export const HADOOP_GROUP_VALUE = [ // hadoop 引擎支持的组件类型(复选框)
    { label: 'HDFS', value: COMPONENT_TYPE_VALUE.HDFS, disabled: true },
    { label: 'YARN', value: COMPONENT_TYPE_VALUE.YARN, disabled: true },
    { label: 'SFTP', value: COMPONENT_TYPE_VALUE.SFTP, disabled: true },
    { label: 'Flink', value: COMPONENT_TYPE_VALUE.FLINK },
    { label: 'Spark', value: COMPONENT_TYPE_VALUE.SPARK },
    { label: 'Learning', value: COMPONENT_TYPE_VALUE.LEARNING },
    { label: 'DTScript', value: COMPONENT_TYPE_VALUE.DTYARNSHELL }, // DTYarnShell => DTScript
    { label: 'SparkThrift', value: COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER },
    { label: 'CarbonData ThriftServer', value: COMPONENT_TYPE_VALUE.CARBONDATA },
    { label: 'Hive Server', value: COMPONENT_TYPE_VALUE.HIVESERVER },
    { label: 'Impala SQL', value: COMPONENT_TYPE_VALUE.IMPALASQL }
];
export const COMPONEMT_CONFIG_KEYS = {
    FLINK: 'flinkConf',
    SPARK: 'sparkConf',
    LEARNING: 'learningConf',
    DTYARNSHELL: 'dtscriptConf',
    HDFS: 'hadoopConf',
    YARN: 'yarnConf',
    SPARKTHRIFTSERVER: 'hiveConf',
    CARBONDATA: 'carbonConf',
    LIBRASQL: 'libraConf',
    TI_DB_SQL: 'tidbConf',
    HIVESERVER: 'hiveServerConf',
    SFTP: 'sftpConf',
    IMPALASQL: 'impalaSqlConf'
}
// 组件对应的key值
export const COMPONEMT_CONFIG_KEY_ENUM = {
    [COMPONENT_TYPE_VALUE.FLINK]: COMPONEMT_CONFIG_KEYS.FLINK,
    [COMPONENT_TYPE_VALUE.SPARK]: COMPONEMT_CONFIG_KEYS.SPARK,
    [COMPONENT_TYPE_VALUE.LEARNING]: COMPONEMT_CONFIG_KEYS.LEARNING,
    [COMPONENT_TYPE_VALUE.DTYARNSHELL]: COMPONEMT_CONFIG_KEYS.DTYARNSHELL,
    [COMPONENT_TYPE_VALUE.HDFS]: COMPONEMT_CONFIG_KEYS.HDFS,
    [COMPONENT_TYPE_VALUE.YARN]: COMPONEMT_CONFIG_KEYS.YARN,
    [COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER]: COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER,
    [COMPONENT_TYPE_VALUE.CARBONDATA]: COMPONEMT_CONFIG_KEYS.CARBONDATA,
    [COMPONENT_TYPE_VALUE.HIVESERVER]: COMPONEMT_CONFIG_KEYS.HIVESERVER,
    [COMPONENT_TYPE_VALUE.LIBRASQL]: COMPONEMT_CONFIG_KEYS.LIBRASQL,
    [COMPONENT_TYPE_VALUE.TIDB_SQL]: COMPONEMT_CONFIG_KEYS.TI_DB_SQL,
    [COMPONENT_TYPE_VALUE.SFTP]: COMPONEMT_CONFIG_KEYS.SFTP,
    [COMPONENT_TYPE_VALUE.IMPALASQL]: COMPONEMT_CONFIG_KEYS.IMPALASQL
};

export const API_MODE: any = {
    GUIDE: 0,
    SQL: 1
}
export const API_METHOD: any = {
    POST: 1
    // GET:2
}

/* eslint-disable-next-line */
export const API_METHOD_key = {
    1: 'POST',
    2: 'GET'
}

export const API_STATUS: any = {
    '-1': 'NO_APPLY',
    '0': 'IN_HAND',
    '1': 'PASS',
    '2': 'REJECT',
    '3': 'STOPPED',
    '4': 'DISABLE',
    '5': 'EXPIRED'
}
export const API_USER_STATUS: any = {
    'NO_APPLY': -1,
    'IN_HAND': 0,
    'PASS': 1,
    'REJECT': 2,
    'STOPPED': 3,
    'DISABLE': 4,
    'EXPIRED': 5
}
export const API_SYSTEM_STATUS: any = {
    SUCCESS: 0,
    STOP: 1,
    EDITTING: 2
}
export const API_DELETE: any = {
    'YES': 0,
    'NO': 1
}
export const EXCHANGE_API_STATUS: any = {
    '-1': 'nothing',
    0: 'inhand',
    1: 'success',
    2: 'notPass',
    3: 'stop',
    4: 'disabled',
    5: 'expired'
}
export const EXCHANGE_APPLY_STATUS: any = {
    0: 'notApproved',
    1: 'pass',
    2: 'rejected',
    3: 'stop',
    4: 'disabled',
    5: 'expired'

}
export const EXCHANGE_ADMIN_API_STATUS: any = {
    0: 'success',
    1: 'stop',
    2: 'editting'
}

export const dataSourceTypes: any = [ // 数据源类型
    '未知类型',
    'MySql',
    'Oracle',
    'SQLServer',
    'PostgreSQL',
    'RDBMS',
    'HDFS',
    'Hive',
    'HBase',
    'FTP',
    'MaxCompute'
]
// 检验各组件数据
export const validateFlinkParams: any = [ // flink
    'flinkConf.flinkZkAddress',
    'flinkConf.flinkHighAvailabilityStorageDir',
    'flinkConf.flinkZkNamespace',
    'flinkConf.gatewayHost',
    'flinkConf.gatewayPort',
    'flinkConf.gatewayJobName',
    'flinkConf.deleteOnShutdown',
    'flinkConf.randomJobNameSuffix',
    'flinkConf.typeName',
    'flinkConf.clusterMode',
    'flinkConf.flinkJarPath',
    // 'flinkConf.flinkJobHistory',
    // 'flinkConf.flinkJobHistory',
    // 'flinkConf.flinkJobHistory',
    // 'flinkConf.flinkPrincipal',
    // 'flinkConf.flinkKeytabPath',
    // 'flinkConf.flinkKrb5ConfPath',
    // 'flinkConf.zkPrincipal',
    // 'flinkConf.zkKeytabPath',
    // 'flinkConf.zkLoginName',
    'flinkConf.kerberosFile',
    'flinkConf.flinkSessionSlotCount'
]
export const validateHiveParams: any = [ // hive <=> Spark Thrift Server
    'hiveConf.jdbcUrl',
    'hiveConf.driverClassName',
    'hiveConf.kerberosFile'
]
export const validateCarbonDataParams: any = [ // carbonData
    'carbonConf.jdbcUrl',
    'carbonConf.kerberosFile'
]
export const validateImpalaSqlParams: any = [ // impalaSql
    'impalaSqlConf.jdbcUrl'
]
export const validateHiveServerParams: any = [ // carbonData
    'hiveServerConf.jdbcUrl',
    'hiveServerConf.kerberosFile'
]
export const validateSparkParams: any = [ // spark
    'sparkConf.typeName',
    'sparkConf.sparkYarnArchive',
    'sparkConf.sparkSqlProxyPath',
    'sparkConf.sparkPythonExtLibPath',
    // 'sparkConf.sparkPrincipal',
    // 'sparkConf.sparkKeytabPath',
    // 'sparkConf.sparkKrb5ConfPath',
    // 'sparkConf.zkPrincipal',
    // 'sparkConf.zkKeytabPath',
    // 'sparkConf.zkLoginName',
    'sparkConf.kerberosFile'
]
export const validateDtYarnShellParams: any = [
    'dtscriptConf.jlogstashRoot',
    'dtscriptConf.javaHome',
    'dtscriptConf.hadoopHomeDir',
    'dtscriptConf.kerberosFile'
]

export const validateLearningParams: any = [
    'learningConf.learningPython3Path',
    'learningConf.kerberosFile'
]
export const validateLibraParams: any = [
    'libraConf.jdbcUrl',
    'libraConf.driverClassName'
]
export const validateSftpDataParams: any = [ // carbonData
    'sftpConf.host',
    'sftpConf.port',
    'sftpConf.path',
    'sftpConf.username',
    'sftpConf.password'
]
// 服务器传参与界面渲染 key_map
export const SPARK_KEY_MAP: any = {
    'spark.yarn.appMasterEnv.PYSPARK_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_PYTHON',
    'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON'
}
export const SPARK_KEY_MAP_DOTS: any = {
    'sparkYarnAppMasterEnvPYSPARK_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
    'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON'
}
export const DTYARNSHELL_KEY_MAP: any = {
    // comm
    'java.home': 'javaHome',
    'hadoop.home.dir': 'hadoopHomeDir',
    // python
    'python2.path': 'python2Path',
    'python3.path': 'python3Path',
    // jupyter
    'jupyter.path': 'jupyterPath',
    'c.NotebookApp.open_browser': 'cNotebookAppOpen_browser',
    'c.NotebookApp.allow_remote_access': 'cNotebookAppAllow_remote_access',
    'c.NotebookApp.ip': 'cNotebookAppIp',
    'c.NotebookApp.token': 'cNotebookAppToken',
    'c.NotebookApp.default_url': 'cNotebookAppDefault_url'
}
export const DTYARNSHELL_KEY_MAP_DOTS: any = {
    // comm
    'javaHome': 'java.home',
    'hadoopHomeDir': 'hadoop.home.dir',
    // python
    'python2Path': 'python2.path',
    'python3Path': 'python3.path',
    // jupyter
    'jupyterPath': 'jupyter.path',
    'cNotebookAppOpen_browser': 'c.NotebookApp.open_browser',
    'cNotebookAppAllow_remote_access': 'c.NotebookApp.allow_remote_access',
    'cNotebookAppIp': 'c.NotebookApp.ip',
    'cNotebookAppToken': 'c.NotebookApp.token',
    'cNotebookAppDefault_url': 'c.NotebookApp.default_url'
}
export const FLINK_KEY_MAP: any = {
    'yarn.jobmanager.heap.mb': 'yarnJobmanagerHeapMb',
    'yarn.taskmanager.heap.mb': 'yarnTaskmanagerHeapMb',
    'yarn.taskmanager.numberOfTaskSlots': 'yarnTaskmanagerNumberOfTaskSlots',
    'yarn.taskmanager.numberOfTaskManager': 'yarnTaskmanagerNumberOfTaskManager',
    // prometheus相关
    'metrics.reporter.promgateway.class': 'metricsReporterPromgatewayClass',
    'metrics.reporter.promgateway.host': 'metricsReporterPromgatewayHost',
    'metrics.reporter.promgateway.port': 'metricsReporterPromgatewayPort',
    'metrics.reporter.promgateway.jobName': 'metricsReporterPromgatewayJobName',
    'metrics.reporter.promgateway.randomJobNameSuffix': 'metricsReporterPromgatewayRandomJobNameSuffix',
    'metrics.reporter.promgateway.deleteOnShutdown': 'metricsReporterPromgatewayDeleteOnShutdown',
    // flinkJobHistory =>
    'historyserver.web.address': 'historyserverWebAddress',
    'historyserver.web.port': 'historyserverWebPort',

    'high-availability.cluster-id': 'high-availabilityCluster-id',
    'high-availability.zookeeper.path.root': 'high-availabilityZookeeperPathRoot',
    'high-availability.zookeeper.quorum': 'high-availabilityZookeeperQuorum',
    'jobmanager.archive.fs.dir': 'jobmanagerArchiveFsDir',
    'high-availability.storageDir': 'high-availabilityStorageDir'
}
export const FLINK_KEY_MAP_DOTS: any = {
    'yarnJobmanagerHeapMb': 'yarn.jobmanager.heap.mb',
    'yarnTaskmanagerHeapMb': 'yarn.taskmanager.heap.mb',
    'yarnTaskmanagerNumberOfTaskSlots': 'yarn.taskmanager.numberOfTaskSlots',
    'yarnTaskmanagerNumberOfTaskManager': 'yarn.taskmanager.numberOfTaskManager',
    'stateCheckpointsDir': 'state.checkpoints.dir',
    'stateCheckpointsNum-retained': 'state.checkpoints.num-retained',
    // prometheus相关
    'metricsReporterPromgatewayClass': 'metrics.reporter.promgateway.class',
    'metricsReporterPromgatewayHost': 'metrics.reporter.promgateway.host',
    'metricsReporterPromgatewayPort': 'metrics.reporter.promgateway.port',
    'metricsReporterPromgatewayJobName': 'metrics.reporter.promgateway.jobName',
    'metricsReporterPromgatewayRandomJobNameSuffix': 'metrics.reporter.promgateway.randomJobNameSuffix',
    'metricsReporterPromgatewayDeleteOnShutdown': 'metrics.reporter.promgateway.deleteOnShutdown',
    // flinkJobHistory =>
    'historyserverWebAddress': 'historyserver.web.address',
    'historyserverWebPort': 'historyserver.web.port',

    'high-availabilityCluster-id': 'high-availability.cluster-id',
    'high-availabilityZookeeperPathRoot': 'high-availability.zookeeper.path.root',
    'high-availabilityZookeeperQuorum': 'high-availability.zookeeper.quorum',
    'jobmanagerArchiveFsDir': 'jobmanager.archive.fs.dir',
    'high-availabilityStorageDir': 'high-availability.storageDir'
}
// 非用户自定义参数
export const notExtKeysFlink: any = [
    'typeName',
    // 'high-availability',
    'high-availability.zookeeper.quorum',
    'high-availability.storageDir',
    'high-availability.zookeeper.path.root',
    'metrics.reporter.promgateway.class',
    'metrics.reporter.promgateway.host',
    'metrics.reporter.promgateway.port',
    'metrics.reporter.promgateway.jobName',
    'metrics.reporter.promgateway.randomJobNameSuffix',
    'metrics.reporter.promgateway.deleteOnShutdown',
    'jarTmpDir',
    'flinkPluginRoot', 'remotePluginRootDir',
    'clusterMode', 'flinkJarPath',
    'historyserver.web.address',
    'historyserver.web.port',
    'high-availability.cluster-id',
    // 'flinkPrincipal', 'flinkKeytabPath', 'flinkKrb5ConfPath',
    // 'zkPrincipal', 'zkKeytabPath', 'zkLoginName',
    'yarn.jobmanager.heap.mb',
    'yarn.taskmanager.heap.mb', 'yarn.taskmanager.numberOfTaskSlots', 'yarn.taskmanager.numberOfTaskManager',
    'openKerberos', 'kerberosFile',
    'flinkSessionSlotCount',
    'state.checkpoints.dir',
    // 'jobmanagerArchiveFsDir',
    'jobmanager.archive.fs.dir',
    'state.checkpoints.num-retained'
];
export const notExtKeysSpark: any = [
    'typeName', 'sparkYarnArchive',
    'sparkSqlProxyPath', 'sparkPythonExtLibPath', 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
    'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON',
    // 'sparkPrincipal', 'sparkKeytabPath',
    // 'sparkKrb5ConfPath', 'zkPrincipal', 'zkKeytabPath', 'zkLoginName',
    'openKerberos', 'kerberosFile'
];
export const notExtKeysLearning: any = [
    'typeName', 'learning.python3.path',
    'learning.python2.path',
    'learning.history.address', 'learning.history.webapp.address',
    'learning.history.webapp.https.address',
    'openKerberos', 'kerberosFile'
];
// DTscript
export const notExtKeysDtyarnShell: any = [
    'typeName', 'jlogstash.root',
    'pythonConf', 'jupyterConf',
    'java.home', 'hadoop.home.dir',
    'openKerberos', 'kerberosFile'
]
export const notExtKeyDtscriptPython: any = [
    'typeName',
    'python2.path',
    'python3.path'
]
export const notExtKeyDtscriptJupter: any = [
    'typeName',
    'jupyter.path',
    'c.NotebookApp.open_browser',
    'c.NotebookApp.allow_remote_access',
    'c.NotebookApp.ip',
    'c.NotebookApp.token',
    'c.NotebookApp.default_url'
]
export const notExtKeysSparkThrift: any = [
    'jdbcUrl', 'username', 'password',
    'driverClassName', 'useConnectionPool', 'maxPoolSize',
    'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows',
    'queryTimeout', 'checkTimeout',
    'openKerberos', 'kerberosFile'
]
export const notExtKeysHiveServer: any = [
    'driverClassName', 'jdbcUrl', 'username', 'password',
    'openKerberos', 'kerberosFile'
]
export const notExtKeysLibraSql: any = [
    'jdbcUrl', 'username', 'password',
    'driverClassName', 'useConnectionPool', 'maxPoolSize',
    'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows',
    'queryTimeout', 'checkTimeout'
]

export const notExtKeysTidbSql: any = [
    'jdbcUrl', 'username', 'password',
    'driverClassName', 'useConnectionPool', 'maxPoolSize',
    'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows',
    'queryTimeout', 'checkTimeout'
]

export const formItemLayout: any = { // 表单常用布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};
export const longLabelFormLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 10 }
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

export const rowFormItemLayout: any = { // 单行末尾布局
    labelCol: { span: 0 },
    wrapperCol: { span: 24 }
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
        data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
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
            type: 'value',
            axisLabel: {
                formatter: '{value} 个',
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

export const doubleLineAreaChartOptions: any = {// 堆叠折现图默认选项

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
        data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
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
        bottom: '30',
        top: 40,
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
            nameGap: 25,
            type: 'value',
            name: '调用次数',
            axisLabel: {
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            nameLocation: 'end',
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: false,
            minInterval: 1
        },
        {
            nameGap: 25,
            type: 'value',
            name: '失败率 (%)',
            axisLabel: {
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            nameLocation: 'end',
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
            },
            max: 100
        }
    ],
    series: []
};

export const pieOption: any = {
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
        orient: 'horizontal',
        x: 'center',
        y: 'bottom',
        data: ['参数错误', '调用超时', '异常访问', '超出限额', '禁用', '其他'],
        itemWidth: 5,
        itemHeight: 5,
        textStyle: {
            color: '#666'
        }
    },
    series: [
        {
            name: '错误类型',
            type: 'pie',
            radius: ['35%', '55%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            labelLine: {
                normal: {
                    show: false
                }
            },
            data: [
                {
                    value: 335,
                    name: '参数错误',
                    itemStyle: {
                        normal: {
                            color: '#1C86EE'
                        }
                    }
                },
                {
                    value: 310,
                    name: '调用超时',
                    itemStyle: {
                        normal: {

                            color: '#EE9A00'
                        }
                    }
                },
                {
                    value: 234,
                    name: '异常访问',
                    itemStyle: {
                        normal: {

                            color: '#EE4000'
                        }
                    }
                },
                {
                    value: 535,
                    name: '超出限额',
                    itemStyle: {
                        normal: {

                            color: '#40E0D0'
                        }
                    }
                },
                {
                    value: 1158,
                    name: '禁用',
                    itemStyle: {
                        normal: {

                            color: '#71C671'
                        }
                    }
                },
                {
                    value: 548,
                    name: '其他',
                    itemStyle: {
                        normal: {

                            color: '#A2B5CD'
                        }
                    }
                }
            ]
        }
    ]
}
