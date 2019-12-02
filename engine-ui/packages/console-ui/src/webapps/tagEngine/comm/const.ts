
export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    POSTGRESQL: 4,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
    MAXCOMPUTE: 10,
    ES: 11,
    REDIS: 12,
    MONGODB: 13,
    KAFKA: 14, // KAFKA_11
    ADS: 15,
    BEATS: 16,
    KAFKA_10: 17,
    KAFKA_09: 18,
    DB2: 19
}

export const REDIS_TYPE = {
    SINGLE: 1,
    CLUSTER: 3,
    SENTINEL: 2
}

export const PROJECT_TYPE = {
    COMMON: 0, // 普通
    TEST: 1, // 测试
    PRO: 2// 生产
}

export const PROJECT_STATUS = {
    INITIALIZE: 0, // 创建中
    NORMAL: 1, // 正常
    DISABLE: 2, // 禁用
    FAIL: 3// 创建失败
}

export const PROJECT_ROLE = { // 项目角色
    PROJECT_OWNER: 2, // 项目所有者
    TENANT_OWVER: 1, // 租户所有者
    VISITOR: 4 // 访客
}

export const TASK_STATUS: any = { // 任务状态
    ALL: null,
    WAIT_SUBMIT: 0, // 等待提交
    CREATED: 1, // 已创建
    INVOKED: 2,
    DEPLOYING: 3,
    RUNNING: 4, // 运行中
    FINISHED: 5, // 已完成
    STOPING: 6, // 停止中
    STOPED: 7, // 已取消
    RUN_FAILED: 8, // 运行失败
    SUBMIT_FAILED: 9, // 提交失败
    SUBMITTING: 10, // 提交中
    RESTARTING: 11, // 重试中
    SET_SUCCESS: 12,
    KILLED: 13, // 已停止
    WAIT_RUN: 16, // 等待运行
    WAIT_COMPUTE: 17,
    FROZEN: 18, // 冻结
    PARENT_FAILD: 21 // 上游失败
}

// 常量
export const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

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
    color: ['#2491F7', '#1BD7F7', '#9a64fb', '#5df2c3', '#eeeeee'],
    legend: {
        data: ['2011年', '2012年'],
        show: true
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
        type: 'category',
        data: ['巴西', '美国', '印度', '中国', '世界人口(万)'],
        boundaryGap: [0, 0.01]
    },
    yAxis: {
        type: 'value',
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
