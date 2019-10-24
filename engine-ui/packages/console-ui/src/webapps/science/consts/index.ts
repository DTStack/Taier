/**
 * 项目类型
 */
export const PROJECT_TYPE: any = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}

export const PROJECT_STATUS: any = {
    CREATING: 0,
    SUCCESS: 1,
    FAILED: 2,
    CANCEL: 3
}
// 资源类型
export const RESOURCE_TYPE: any = {
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
/**
 * 目录类型
 */
export const CATALOGUE_TYPE: any = {
    DATA_BASE: 'database',
    DATA_MAP: 'datamap',
    TABLE: 'table',
    FOLDER: 'folder',
    SEARCH_TABLE: 'searchTable'
}

/**
 * 评估报告图表类型
 */
export const EVALUATE_REPORT_CHART_TYPE: any = {
    ROC: 1,
    K_S: 2,
    LIFT: 3,
    GAIN: 4,
    PRECISION_RECALL: 5
}

/**
 * 评估报告表数据类型
 */
export const EVALUATION_INDEX_TYPE: any = {
    OVERALL: 3, // 输出综合指标表
    FREQUENCY: 4, // 输出等频详细数据表
    WIDTH_DATA: 5 // 输出等宽详细数据表
}

export const siderBarType: any = {
    notebook: 'notebook',
    experiment: 'laboratory',
    component: 'component',
    model: 'model',
    resource: 'resource'
}
export const modelComponentType: any = {
    NOTEBOOK: {
        value: 0,
        text: 'notebook'
    }
}
export const TASK_TYPE: any = { // 任务类型
    PYTHON: 6,
    PYSPARK: 3
}
export const TASK_TYPE_TEXT: any = [{
    value: TASK_TYPE.PYTHON,
    text: 'Python3'
}, {
    value: TASK_TYPE.PYSPARK,
    text: 'PySpark'
}]
export const PYTON_VERSION: any = {
    PYTHON2: 2,
    PYTHON3: 3
}
export const DEAL_MODEL_TYPE: any = {// python和pySpark操作类型
    RESOURCE: 0,
    EDIT: 1
}
export const taskType: any = {
    NOTEBOOK: 13,
    EXPERIMENT: 14
}
export const MODEL_STATUS: any = {
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
export const TASK_STATUS: any = {
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
export const taskStatus: any = {
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
        NORMALIZE: 5, // 归一化
        STANDARD: 15, // 标准化
        MISS_VALUE: 16 // 缺失值填充
    },
    DATA_PRE_HAND: {
        DATA_SPLIT: 6 // 拆分
    },
    MACHINE_LEARNING: {
        LOGISTIC_REGRESSION: 7, // 逻辑二分类
        GBDT_REGRESSION: 11, // GBDT回归
        KMEANS_UNION: 12, // kmeans聚类
        GBDT_CLASS: 17, // GBDT二分类
        SVM: 18 // SVM
    },
    DATA_PREDICT: {
        DATA_PREDICT: 8 // 数据预测
    },
    DATA_EVALUATE: {
        BINARY_CLASSIFICATION: 9, // 二分类评估
        REGRESSION_CLASSIFICATION: 13, // 回归模型评估
        UNION_CLASSIFICATION: 14, // 聚类模型评估
        CONFUSION_MATRIX: 19 // 混淆矩阵
    },
    FEATURE_ENGINEER: { // 特征工程
        ONE_HOT: 20 // 特征生成
    }
}

// 组件对应的key值
export const TASK_ENUM = {
    [COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE]: 'readTableComponent',
    [COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE]: 'writeTableComponent',
    [COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT]: 'sqlComponent',
    [COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE]: 'transTypeComponent',
    [COMPONENT_TYPE.DATA_MERGE.NORMALIZE]: 'normalizationComponent',
    [COMPONENT_TYPE.DATA_MERGE.STANDARD]: 'standardizationComponent',
    [COMPONENT_TYPE.DATA_MERGE.MISS_VALUE]: 'fillMissingValueComponent',
    [COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT]: 'dataSplitComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION]: 'logisticComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION]: 'gbdtRegressionComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION]: 'kmeansComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS]: 'gbdtClassifierComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.SVM]: 'svmComponent',
    [COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT]: 'predictComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION]: 'eveluationComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION]: 'regressionEvaluateComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION]: 'clusterEvaluateComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX]: 'confusionMatrixComponent',
    [COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT]: 'oneHotEncoderComponent'
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
    SOURCE_WRITE: 19, // 写数据表的输出，但是读数据表暂时没有输出
    // GBDT
    GBDT_IMPORTANT: 24,
    // kmeans
    KMEANS_CENTER_DATA: 20,
    KMEANS_RESULT_CLUSTER: 21,
    KMEANS_CENTER_CLUSTER: 22,
    KMEANS_STATISTIC_CLUSTER: 23,
    // 回归模型评估
    REGRESSION_OUTPUT_1: 25,
    REGRESSION_OUTPUT_2: 26,
    // 聚类
    UNION_INPUT_MODEL: 27,
    UNION_INPUT_DATA: 28,
    // 标准化
    STANDARD_INPUT_PARAM: 29,
    STANDARD_OUTPUT_DATA: 30,
    STANDARD_OUTPUT_PARAM: 31,
    // one-hot
    ONE_HOT_INPUT_MODAL: 32,
    ONE_HOT_OUTPUT_DATA: 33,
    ONE_HOT_OUTPUT_MODAL: 34,
    // 缺失值填充
    MISS_VALUE_INPUT_PARAM: 35,
    MISS_VALUE_OUTPUT_DATA: 36,
    MISS_VALUE_OUTPUT_PARAM: 37,
    // GDBT二分类
    GBDT_CLASS_IMPORTANT: 38,
    // SVM
    // 混淆矩阵
    CONFUSION_MATRIX_INPUT_DATA: 39,
    CONFUSION_MATRIX_OUTPUT_DATA: 40
}
export const CONSTRAINT_TEXT: any = {
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
    [COMPONENT_TYPE.DATA_MERGE.STANDARD]: { // 标准化
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入数据' },
            { key: INPUT_TYPE.NORMALIZATION_INPUT_PARAM, value: '输入参数' }
        ],
        output: [
            { key: INPUT_TYPE.NORMALIZATION_OUTPUT_PARAM, value: '输出参数' },
            { key: INPUT_TYPE.NORMALIZATION_OUTPUT_DATA, value: '输出结果' }
        ]
    },
    [COMPONENT_TYPE.DATA_MERGE.MISS_VALUE]: { // 缺失值
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入数据' },
            { key: INPUT_TYPE.MISS_VALUE_INPUT_PARAM, value: '输入参数' }
        ],
        output: [
            { key: INPUT_TYPE.MISS_VALUE_OUTPUT_PARAM, value: '输出参数' },
            { key: INPUT_TYPE.MISS_VALUE_OUTPUT_DATA, value: '输出结果' }
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
    [COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.MODEL, value: '模型输出' },
            { key: INPUT_TYPE.GBDT_IMPORTANT, value: '输出重要性' }
        ]
    },
    [COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' },
            { key: INPUT_TYPE.KMEANS_CENTER_DATA, value: '初始化质心表' }
        ],
        output: [
            { key: INPUT_TYPE.KMEANS_RESULT_CLUSTER, value: '输出聚类表' },
            { key: INPUT_TYPE.MODEL, value: '输出聚类中心点模型' },
            { key: INPUT_TYPE.KMEANS_STATISTIC_CLUSTER, value: '输出聚类统计表' },
            { key: INPUT_TYPE.KMEANS_CENTER_CLUSTER, value: '输出聚类中心表' }
        ]
    },
    [COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS]: { // GDBT二分类
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.MODEL, value: '模型输出' },
            { key: INPUT_TYPE.GBDT_CLASS_IMPORTANT, value: '输出模型特征重要性' }
        ]
    },
    [COMPONENT_TYPE.MACHINE_LEARNING.SVM]: { // SVM
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
    },
    [COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION]: {
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入' }
        ],
        output: [
            { key: INPUT_TYPE.REGRESSION_OUTPUT_1, value: '输出1' },
            { key: INPUT_TYPE.REGRESSION_OUTPUT_2, value: '输出2' }
        ]
    },
    [COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION]: {
        input: [
            { key: INPUT_TYPE.UNION_INPUT_MODEL, value: '模型输入' },
            { key: INPUT_TYPE.UNION_INPUT_DATA, value: '数据输入' }
        ],
        output: [
            { key: INPUT_TYPE.NORMAL, value: '输出' }
        ]
    },
    [COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX]: { // 混淆矩阵
        input: [
            { key: INPUT_TYPE.CONFUSION_MATRIX_INPUT_DATA, value: '输入预测结果' }
        ],
        output: [
            { key: INPUT_TYPE.CONFUSION_MATRIX_OUTPUT_DATA, value: '输出结果' }
        ]
    },
    [COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT]: { // one-hot
        input: [
            { key: INPUT_TYPE.NORMAL, value: '输入数据' },
            { key: INPUT_TYPE.ONE_HOT_INPUT_MODAL, value: '输入模型' }
        ],
        output: [
            { key: INPUT_TYPE.ONE_HOT_OUTPUT_DATA, value: '输出结果' },
            { key: INPUT_TYPE.ONE_HOT_OUTPUT_MODAL, value: '输出模型' }
        ]
    }
}
/**
 * SQL执行状态
 */
export const sqlExecStatus: any = {
    FINISHED: 5,
    FAILED: 8,
    CANCELED: 7
}

// dataMap创建状态
export const dataMapStatus: any = {
    INITIALIZE: 0,
    NORMAL: 1,
    FAIL: 2
}
export const modalType: any = {
    newNotebook: 'NEW_NOTEBOOK',
    newExperiment: 'NEW_EXPERIMENT'
}
export const consoleKey = 'console-log';
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
// mxGraph的长方形框的长宽
export const VertexSize: any = {
    width: 188,
    height: 32
}
