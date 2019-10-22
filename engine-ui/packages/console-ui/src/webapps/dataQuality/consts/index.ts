// 任务类型
export const TASK_STATUS: any = {
    WAIT_RUN: 0,
    RUNNING: 1,
    FAIL: 2,
    PASS: 3,
    UNPASS: 4
}

// 告警类型
export const ALARM_TYPE: any = {
    EMAIL: '1',
    SMS: '2',
    DINGDING: '4'
}

// 触发方式
export const TRIG_MODE: any = {
    LOOP: 0,
    HAND: 1
}
export const TRIG_MODE_TEXT: any = {
    [TRIG_MODE.LOOP]: '周期',
    [TRIG_MODE.HAND]: '手动'
}

// 数据源类型
export const DATA_SOURCE: any = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10
}

/**
 * 校验状态
 */
export const CHECK_STATUS: any = { // 2，4, 5, 6 可查看报告
    INITIAL: 0,
    RUNNING: 1,
    SUCCESS: 2,
    FAIL: 3,
    PASS: 4,
    UNPASS: 5,
    EXPIRED: 6
}

export const HELP_DOC_URL: any = {
    REMOTE: '/public/helpSite/valid/v3.0/RuleManage/Remote.html'
}

/**
 * 校验状态--中文
 */
export const CHECK_STATUS_CN: any = [{
    text: '等待运行',
    value: '0'
}, {
    text: '运行中',
    value: '1'
}, {
    text: '运行成功',
    value: '2'
}, {
    text: '运行失败',
    value: '3'
}, {
    text: '校验通过',
    value: '4'
}, {
    text: '校验未通过',
    value: '5'
}, {
    text: '校验结果失效',
    value: '6'
}]

export const dataSourceTypes: any = [ // 数据源类型
    '未知类型',
    'MySQL',
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

export const dataSourceFilter: any = [{
    text: 'MySQL',
    value: 1
}, {
    text: 'Oracle',
    value: 2
}, {
    text: 'SQLServer',
    value: 3
}, {
    text: 'Spark',
    value: 7
}, {
    text: 'MaxCompute',
    value: 10
}]

export const periodType: any = [ // 调度类型
    '未知类型',
    '小时',
    '天',
    '周',
    '月',
    '手动触发'
]

/**
 * 统计函数
 */
export enum STATISTICS_FUNC {
    /**
     * 枚举
     */
    ENUM = 11,
    /**
     * 字符串最大长度
     */
    STRING_MAX_LEN = 16,
    /**
     * 字符串最小长度
     */
    STRING_MIN_LEN = 17
};

/**
 * operator--有!=
 */
export const operatorSelect: any = [{
    text: '>',
    value: '>'
}, {
    text: '>=',
    value: '>='
}, {
    text: '=',
    value: '='
}, {
    text: '<',
    value: '<'
}, {
    text: '<=',
    value: '<='
}, {
    text: '!=',
    value: '!='
}]

/**
 * operator1--无!=
 */
export const operatorSelect1: any = [{
    text: '>',
    value: '>'
}, {
    text: '>=',
    value: '>='
}, {
    text: '=',
    value: '='
}, {
    text: '<',
    value: '<'
}, {
    text: '<=',
    value: '<='
}]

/**
 * 所有数据源，字段规则，统计函数为枚举类型时，只有以下2种
 */
export const operatorForEnum: any = [{
    text: '=',
    value: '='
}, {
    text: '!=',
    value: '!='
}]

/**
 * 告警日期过滤
 */
export const alarmDateFilter: any = [{
    text: '今日告警数',
    value: '1'
}, {
    text: '最近7天告警数',
    value: '7'
}, {
    text: '最近30天告警数',
    value: '30'
}]

/**
 * 任务状态过滤
 */
export const taskStatusFilter: any = [{
    text: '等待运行',
    value: TASK_STATUS.WAIT_RUN
}, {
    text: '运行中',
    value: TASK_STATUS.RUNNING
}, {
    text: '运行失败',
    value: TASK_STATUS.FAIL
}, {
    text: '校验通过',
    value: TASK_STATUS.PASS
}, {
    text: '校验未通过',
    value: TASK_STATUS.UNPASS
}]

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

export const halfFormItemLayout: any = { // 表单中间布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
};

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

// 不显示label
export const rowFormItemLayout: any = {
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
