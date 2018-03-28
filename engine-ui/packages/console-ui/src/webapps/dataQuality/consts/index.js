// 任务类型
export const TASK_STATUS = {
    WAIT_RUN: 0,
    RUNNING: 1,
    FAIL: 2,
    PASS: 3,
    UNPASS: 4,
}

/**
 * 校验状态
 */
export const CHECK_STATUS = { // 2，4, 5, 6 可查看报告
    INITIAL: 0,
    RUNNING: 1,
    SUCCESS: 2,
    FAIL: 3,
    PASS: 4,
    UNPASS: 5,
    EXPIRED: 6,
}

// 数据源类型
export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10,
}

export const dataSourceTypes = [ // 数据源类型
    '未知类型', 
    'MySql', 
    'Oracle', 
    'SQLServer', 
    'PostgreSQL', 
    'RDBMS', 
    'HDFS', 
    'Spark',
    'HBase',
    'FTP',
    'MaxCompute'
]

export const periodType = [ // 调度类型
    '未知类型', 
    '小时', 
    '天', 
    '周', 
    '月', 
    '手动触发'
]

/**
 * 告警日期过滤
 */
export const alarmDateFilter = [{
    text: '今日告警数',
    value: '1',
}, {
    text: '最近7天告警数',
    value: '7',
}, {
    text: '最近30天告警数',
    value: '30',
}]

export const taskStatusFilter = [{
    text: '运行中',
    value: TASK_STATUS.RUNNING,
}, {
    text: '运行失败',
    value: TASK_STATUS.FAIL,
}, {
    text: '校验通过',
    value: TASK_STATUS.PASS,
}, {
    text: '校验未通过',
    value: TASK_STATUS.UNPASS,
}]

export const formItemLayout = { // 表单常用布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};

export const tailFormItemLayout = { // 表单末尾布局
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0,
        },
        sm: {
            span: 14,
            offset: 6,
        },
    },
}

export const rowFormItemLayout = { // 单行末尾布局
    labelCol: { span: 0 },
    wrapperCol: { span: 24 },
}

export const lineAreaChartOptions = {// 堆叠折现图默认选项
    title: {
        text: '堆叠区域图',
        textStyle: {
            fontSize: 12,
        },
        textAlign: 'left',
    },
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
    legend: {
        data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎']
    },
    toolbox: {
        feature: {
            saveAsImage: {
                show: false,
            }
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            boundaryGap : false,
            data : [],
            axisTick: {
                show: true,
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD'
                }
            },
            axisLabel: {
                textStyle: {
                    color: '#666666',
                },
            },
            nameTextStyle: {
                color: '#666666',
            },
            splitLine: {
                color: '#666666',
            }
        }
    ],
    yAxis : [
        {
            type : 'value',
            axisLabel: {
                formatter: '{value} 个',
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom',
                },
            },
            nameTextStyle: {
                color: '#666666',
            },
            nameLocation: 'end',
            nameGap: 20,
            axisLine: {
                show: false
            },
            axisTick: {
                show: false,
            },
            splitLine: {
                lineStyle: {
                    color: '#DDDDDD',
                    type: 'dashed'
                }
            }
        }
    ],
    series : []
};