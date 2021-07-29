export const TASK_TYPE = { // 任务类型
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
    GREEN_PLUM_SQL: 21
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
    DO_FAIL: 22,
    AUTO_CANCEL: 24 // 自动取消
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
    text: '手动取消',
    value: 7
}, {
    id: 11,
    text: '自动取消',
    value: 24
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

export const TASK_ALL_TYPE = [
    {
        type: -1,
        text: '虚节点'
    },
    {
        type: 0,
        text: 'SparkSQL'
    },
    {
        type: 1,
        text: 'Spark'
    },
    {
        type: 2,
        text: '数据同步'
    },
    {
        type: 3,
        text: 'PySpark'
    },
    {
        type: 4,
        text: 'R'
    },
    {
        type: 5,
        text: '深度学习'
    },
    {
        type: 6,
        text: 'Python'
    },
    {
        type: 7,
        text: 'Shell'
    },
    {
        type: 8,
        text: '机器学习'
    },
    {
        type: 9,
        text: 'HadoopMR'
    },
    {
        type: 10,
        text: '工作流'
    },
    {
        type: 12,
        text: 'CarbonSQL'
    },
    {
        type: 13,
        text: 'Notebook'
    },
    {
        type: 14,
        text: '算法实验'
    },
    {
        type: 15,
        text: 'LibrA SQL'
    },
    {
        type: 16,
        text: 'Kylin'
    },
    {
        type: 17,
        text: 'HiveSQL'
    },
    {
        type: 18,
        text: 'ImpalaSQL'
    },
    {
        type: 19,
        text: 'TiDB SQL'
    },
    {
        type: 20,
        text: 'Oracle SQL'
    },
    {
        type: 21,
        text: 'greenplum SQL'
    },
    {
        type: 22,
        text: 'TensorFlow 1.x'
    },
    {
        type: 23,
        text: 'Keras'
    },
    {
        type: 24,
        text: 'Presto'
    },
    {
        type: 25,
        text: 'PyTorch'
    }
]

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
    KINGBASE: 40
}

// 调度状态
export const SCHEDULE_STATUS = {
    NORMAL: 1,
    STOPPED: 2
}

// 脚本类型
export const SCRIPT_TYPE = {
    SQL: 0,
    PYTHON2: 1,
    PYTHON3: 2,
    SHELL: 3,
    LIBRASQL: 4,
    IMPALA_SQL: 5,
    TI_DB_SQL: 6
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

export const PROJECT_TYPE = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}

export const offlineTaskPeriodFilter: any = [ {
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

// 表单正常布局
export const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

// 堆叠折现图默认选项
export const lineAreaChartOptions: any = {
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
