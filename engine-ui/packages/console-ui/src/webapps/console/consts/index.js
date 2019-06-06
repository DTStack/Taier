
// 任务状态
export const TASK_STATE = {
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
export const DATA_SOURCE = {
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
    LIBRA: 2
}
export const ENGINE_TYPE_NAME = {
    HADOOP: 'HADOOP',
    LIBRA: 'LIBRA'
}
export const ENGINE_TYPE_ARRAY = [{ // 引擎类型下拉框数据
    name: 'Hadoop',
    value: 'HADOOP'
}, {
    name: 'Libra',
    value: 'LIBRA'
}]
export const COMPONENT_TYPE_VALUE = {
    FLINK: 0,
    SPARK: 1,
    LEARNING: 2,
    DTYARNSHELL: 3,
    HDFS: 4,
    YARN: 5,
    SPARKTHRIFTSERVER: 6,
    CARBONDATA: 7,
    LIBRASQL: 8
}

export const hadoopEngineOptionsValue = [ // 非华为集群支持的引擎类型options, checkbox支持数据格式
    { label: 'Flink', value: 0 },
    { label: 'Spark', value: 1 },
    { label: 'Learning', value: 2 },
    { label: 'DTYarnShell', value: 3 },
    { label: 'HDFS', value: 4, disabled: true },
    { label: 'YARN', value: 5, disabled: true },
    { label: 'SparkThrift', value: 6 },
    { label: 'CarbonData', value: 7 }
];
export const hadoopEngineOptions = [ // 华为集群支持的引擎 options
    'Flink',
    'Spark',
    'DTYarnShell',
    'Learning',
    'HDFS',
    'YARN',
    'SparkThrift',
    'CarbonData',
    'LibrA'
];
export const API_MODE = {
    GUIDE: 0,
    SQL: 1
}
export const API_METHOD = {
    POST: 1
    // GET:2
}
/* eslint-disable-next-line */
export const API_METHOD_key = {
    1: 'POST',
    2: 'GET'
}

export const API_STATUS = {
    '-1': 'NO_APPLY',
    '0': 'IN_HAND',
    '1': 'PASS',
    '2': 'REJECT',
    '3': 'STOPPED',
    '4': 'DISABLE',
    '5': 'EXPIRED'
}
export const API_USER_STATUS = {
    'NO_APPLY': -1,
    'IN_HAND': 0,
    'PASS': 1,
    'REJECT': 2,
    'STOPPED': 3,
    'DISABLE': 4,
    'EXPIRED': 5
}
export const API_SYSTEM_STATUS = {
    SUCCESS: 0,
    STOP: 1,
    EDITTING: 2
}
export const API_DELETE = {
    'YES': 0,
    'NO': 1
}
export const EXCHANGE_API_STATUS = {
    '-1': 'nothing',
    0: 'inhand',
    1: 'success',
    2: 'notPass',
    3: 'stop',
    4: 'disabled',
    5: 'expired'
}
export const EXCHANGE_APPLY_STATUS = {
    0: 'notApproved',
    1: 'pass',
    2: 'rejected',
    3: 'stop',
    4: 'disabled',
    5: 'expired'

}
export const EXCHANGE_ADMIN_API_STATUS = {
    0: 'success',
    1: 'stop',
    2: 'editting'
}

export const dataSourceTypes = [ // 数据源类型
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
// hdfs、yarn、learning、libra暂无必填数据,
// 为了能获取到一组域数据，validateFields第一个参数不可为空，取每组数据任一参数
export const validateFlinkParams = [ // flink
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
    'flinkConf.flinkJobHistory',
    'flinkConf.flinkPrincipal',
    'flinkConf.flinkKeytabPath',
    'flinkConf.flinkKrb5ConfPath',
    'flinkConf.zkPrincipal',
    'flinkConf.zkKeytabPath',
    'flinkConf.zkLoginName'
]
export const validateHiveParams = [ // hive <=> Spark Thrift Server
    'hiveConf.jdbcUrl'
]
export const validateCarbonDataParams = [ // carbonData
    'carbonConf.jdbcUrl'
]
export const validateSparkParams = [ // spark
    'sparkConf.typeName',
    'sparkConf.sparkYarnArchive',
    'sparkConf.sparkSqlProxyPath',
    'sparkConf.sparkPythonExtLibPath',
    'sparkConf.sparkPrincipal',
    'sparkConf.sparkKeytabPath',
    'sparkConf.sparkKrb5ConfPath',
    'sparkConf.zkPrincipal',
    'sparkConf.zkKeytabPath',
    'sparkConf.zkLoginName'
]
export const validateDtYarnShellParams = [
    'dtyarnshellConf.jlogstashRoot',
    'dtyarnshellConf.javaHome',
    'dtyarnshellConf.hadoopHomeDir',
    'dtyarnshellConf.hdfsPrincipal',
    'dtyarnshellConf.hdfsKeytabPath',
    'dtyarnshellConf.hdfsKrb5ConfPath'
]

export const validateLearningParams = [
    'learningConf.learningPython3Path'
]
export const validateLibraParams = [
    'libraSqlConf.jdbcUrl'
]
// key_map
export const SPARK_KEY_MAP = {
    'spark.yarn.appMasterEnv.PYSPARK_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_PYTHON',
    'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON'
}
export const DTYARNSHELL_KEY_MAP = {
    'jlogstash.root': 'jlogstashRoot',
    'java.home': 'javaHome',
    'hadoop.home.dir': 'hadoopHomeDir',
    'python2.path': 'python2Path',
    'python3.path': 'python3Path'
}
// 非用户自定义参数
export const notExtKeysFlink = [
    'typeName', 'flinkZkAddress',
    'flinkHighAvailabilityStorageDir',
    'flinkZkNamespace', 'reporterClass',
    'gatewayHost', 'gatewayPort',
    'gatewayJobName', 'deleteOnShutdown',
    'randomJobNameSuffix', 'jarTmpDir',
    'flinkPluginRoot', 'remotePluginRootDir',
    'clusterMode', 'flinkJarPath',
    'flinkJobHistory', 'flinkPrincipal', 'flinkKeytabPath', 'flinkKrb5ConfPath',
    'zkPrincipal', 'zkKeytabPath', 'zkLoginName'
];
export const notExtKeysSpark = [
    'typeName', 'sparkYarnArchive',
    'sparkSqlProxyPath', 'sparkPythonExtLibPath', 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
    'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', 'sparkPrincipal', 'sparkKeytabPath',
    'sparkKrb5ConfPath', 'zkPrincipal', 'zkKeytabPath', 'zkLoginName'
];
export const notExtKeysLearning = [
    'typeName', 'learning.python3.path',
    'learning.python2.path',
    'learning.history.address', 'learning.history.webapp.address',
    'learning.history.webapp.https.address'
];
export const notExtKeysDtyarnShell = [
    'typeName', 'jlogstash.root',
    'java.home', 'hadoop.home.dir', 'python2.path',
    'python3.path', 'hdfsPrincipal', 'hdfsKeytabPath', 'hdfsKrb5ConfPath'
]
export const notExtKeysSparkThrift = [
    'jdbcUrl', 'username', 'password'
]
export const notExtKeysLibraSql = [
    'jdbcUrl', 'username', 'password'
]

export const formItemLayout = { // 表单常用布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};
export const longLabelFormLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

export const tailFormItemLayout = { // 表单末尾布局
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

export const rowFormItemLayout = { // 单行末尾布局
    labelCol: { span: 0 },
    wrapperCol: { span: 24 }
}

export const lineAreaChartOptions = {// 堆叠折现图默认选项
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

export const doubleLineAreaChartOptions = {// 堆叠折现图默认选项

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

export const pieOption = {
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
