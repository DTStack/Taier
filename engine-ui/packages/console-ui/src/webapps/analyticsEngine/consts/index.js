
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
