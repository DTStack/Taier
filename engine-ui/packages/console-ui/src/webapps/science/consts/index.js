/**
 * 项目类型
 */
export const PROJECT_TYPE = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}

export const PROJECT_STATUS = {
    CREATING: 0,
    SUCCESS: 1,
    FAILED: 2,
    CANCEL: 3
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

/**
 * 评估报告图表类型
 */
export const EVALUATE_REPORT_CHART_TYPE = {
    ROC: 1,
    K_S: 2,
    LIFT: 3,
    GAIN: 4,
    PRECISION_RECALL: 5
}

/**
 * 评估报告表数据类型
 */
export const EVALUATION_INDEX_TYPE = {
    OVERALL: 3, // 输出综合指标表
    FREQUENCY: 4, // 输出等频详细数据表
    WIDTH_DATA: 5 // 输出等宽详细数据表
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
    NOT_RUN: {
        value: 3,
        text: '未运行',
        className: 'state-not_run'
    },
    LOADING: {
        value: 5,
        text: '加载中',
        className: 'state-loading'
    }
}
export const TASK_STATUS = {
    WAIT_SUBMIT: {
        value: 0,
        text: '等待提交'
    },
    SUBMITTING: {
        value: 10,
        text: '提交中'
    },
    WAIT_RUN: {
        value: 16,
        text: '等待运行'
    },
    RUNNING: {
        value: 4,
        text: '运行中'
    },
    FINISHED: {
        value: 5,
        text: '成功'
    },
    STOPED: {
        value: 7,
        text: '取消'
    },
    SUBMIT_FAILED: {
        value: 9,
        text: '提交失败'
    },
    RUN_FAILED: {
        value: 8,
        text: '运行失败'
    },
    PARENT_FAILD: {
        value: 21,
        text: '上游失败'
    },
    FROZEN: {
        value: 18,
        text: '冻结'
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

/**
 * 输出类型
 */
export const INPUT_TYPE = {
    NORMAL: 0,
    // 拆分表
    DATA_SPLIT_LEFT: 1,
    DATA_SPLIT_RIGHT: 2,
    // 二分类评估
    EVALUATION_OVERALL_DATA: 3,
    EVALUATION_FREQUENCY_DATA: 4,
    EVALUATION_WIDTH_DATA: 5,
    // 归一化
    NORMALIZATION_INPUT_DATA: 6,
    NORMALIZATION_INPUT_PARAM: 7,
    NORMALIZATION_OUTPUT_DATA: 8,
    NORMALIZATION_OUTPUT_PARAM: 9,
    // 预测
    PREDICT_INPUT_MODAL: 10,
    PREDICT_INPUT_DATA: 11,
    // 逻辑回归
    MODEL: 12,
    // 读数据源
    SOURCE_READ: 13,
    // SQL
    SQL_1: 14,
    SQL_2: 15,
    SQL_3: 16,
    SQL_4: 17,
    SQL_OUT: 18,
    SOURCE_WRITE: 19 // 写数据表的输出，但是读数据表暂时没有输出
}
export const CONSTRAINT_TEXT = {
    [COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE]: {
        input: [],
        output: [{ key: INPUT_TYPE.SOURCE_READ, value: 'HDFS数据源输出' }]
    },
    [COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: []
    },
    [COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT]: {
        input: [
            { key: INPUT_TYPE.SQL_1, value: 'SQL结果输入1' },
            { key: INPUT_TYPE.SQL_2, value: 'SQL结果输入2' },
            { key: INPUT_TYPE.SQL_3, value: 'SQL结果输入3' },
            { key: INPUT_TYPE.SQL_4, value: 'SQL结果输入4' }
        ],
        output: [{ key: INPUT_TYPE.SQL_OUT, value: 'SQL结果输出' }]
    },
    [COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.NORMAL, value: '转化结果输出' }
        ]
    },
    [COMPONENT_TYPE.DATA_MERGE.NORMALIZE]: {
        input: [
            { key: INPUT_TYPE.NORMALIZATION_INPUT_PARAM, value: '归一化输入参数表' },
            { key: INPUT_TYPE.NORMALIZATION_INPUT_DATA, value: '归一化输入结果表' }
        ],
        output: [
            { key: INPUT_TYPE.NORMALIZATION_OUTPUT_PARAM, value: '输出参数表' },
            { key: INPUT_TYPE.NORMALIZATION_OUTPUT_DATA, value: '输出结果表' }
        ]
    },
    [COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.DATA_SPLIT_LEFT, value: '输出1' },
            { key: INPUT_TYPE.DATA_SPLIT_RIGHT, value: '输出2' }
        ]
    },
    [COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.MODEL, value: '模型输出' }
        ]
    },
    [COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT]: {
        input: [
            { key: INPUT_TYPE.PREDICT_INPUT_MODAL, value: '模型数据输入' },
            { key: INPUT_TYPE.PREDICT_INPUT_DATA, value: '预测数据输入' }
        ],
        output: [
            { key: INPUT_TYPE.NORMAL, value: '输出预测结果' }
        ]
    },
    [COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.EVALUATION_OVERALL_DATA, value: '输出综合指标表' },
            { key: INPUT_TYPE.EVALUATION_FREQUENCY_DATA, value: '输出等频详细数据表' },
            { key: INPUT_TYPE.EVALUATION_WIDTH_DATA, value: '输出等宽详细数据表' }
        ]
    }
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
