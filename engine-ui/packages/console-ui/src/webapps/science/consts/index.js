/**
 * 项目类型
 */
export const PROJECT_TYPE = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}
/**
 * 目录类型
 */
export const CATALOGUE_TYPE = {
    DATA_BASE: 'database',
    DATA_MAP: 'datamap',
    TABLE: 'table',
    FOLDER: 'folder',
    SEARCH_TABLE: 'searchTable'
}

export const siderBarType = {
    notebook: 'notebook',
    experiment: 'laboratory',
    component: 'component',
    model: 'model'
}
export const modelComponentType = {
    NOTEBOOK: {
        value: 0,
        text: 'notebook'
    }
}
export const taskType = {
    NOTEBOOK: 13,
    EXPERIMENT: 14
}
export const MODEL_STATUS = {
    RUNNING: {
        value: 1,
        text: '运行中',
        className: 'state-running'
    },
    FAILED: {
        value: 4,
        text: '运行失败',
        className: 'state-failed'
    },
    DISABLED: {
        value: 2,
        text: '已禁用',
        className: 'state-disabled'
    },
    LOADING: {
        value: 5,
        text: '已禁用',
        className: 'state-disabled'
    }
}

export const taskStatus = {
    ALL: null,
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
    WAIT_RUN: 16,
    WAIT_COMPUTE: 17,
    FROZEN: 18,
    FAILING: 22 // 失败中，中间状态
}

/**
 * 组件类型
 */
export const COMPONENT_TYPE = {
    DATA_SOURCE: {
        READ_DATABASE: 1, // 读数据库
        WRITE_DATABASE: 2 // 写数据库
    },
    DATA_TOOLS: {
        SQL_SCRIPT: 3 // sql脚本
    },
    DATA_MERGE: {
        TYPE_CHANGE: 4, // 类型转换
        NORMALIZE: 5 // 归一化
    },
    DATA_PRE_HAND: {
        DATA_SPLIT: 6 // 拆分
    },
    MACHINE_LEARNING: {
        LOGISTIC_REGRESSION: 7 // 逻辑二分类
    },
    DATA_PREDICT: {
        DATA_PREDICT: 8 // 数据预测
    },
    DATA_EVALUATE: {
        BINARY_CLASSIFICATION: 9 // 二分类评估
    }
}

// 组件对应的key值
export const TASK_ENUM = {
    [COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE]: 'readTableComponent',
    [COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE]: 'writeTableComponent',
    [COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT]: 'sqlComponent',
    [COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE]: 'transTypeComponent',
    [COMPONENT_TYPE.DATA_MERGE.NORMALIZE]: 'normalizationComponent',
    [COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT]: 'dataSplitComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION]: 'logisticComponent',
    [COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT]: 'predictComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION]: 'eveluationComponent'
}
/* 输入输出的类型 */
export const INPUT_TYPE_ENUM = {
    'HDFS数据源输出': 13,
    'sql脚本表1': 14,
    'sql脚本表2': 15,
    'sql脚本表3': 16,
    'sql脚本表4': 17,
    '输出参数表': 9,
    '输出结果表': 10,
    '归一化输入参数表': 7,
    '归一化输入结果表': 6,
    '输出表1': 1,
    '输出表2': 2,
    '模型输出': 12,
    '模型': 10,
    '预测数据': 11,
    '综合指标表': 3,
    '等频详细数据表': 4,
    '等宽详细数据表': 5
}
/**
 * SQL执行状态
 */
export const sqlExecStatus = {
    FINISHED: 5,
    FAILED: 8,
    CANCELED: 7
}

// dataMap创建状态
export const dataMapStatus = {
    INITIALIZE: 0,
    NORMAL: 1,
    FAIL: 2
}
export const modalType = {
    newNotebook: 'NEW_NOTEBOOK',
    newExperiment: 'NEW_EXPERIMENT'
}
export const consoleKey = 'console-log';
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

export const halfFormItemLayout = { // 表单中间布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
};

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

// 不显示label
export const rowFormItemLayout = {
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
// mxGraph的长方形框的长宽
export const VertexSize = {
    width: 188,
    height: 32
}
