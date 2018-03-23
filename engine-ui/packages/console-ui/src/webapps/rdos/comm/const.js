// 常量
export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
}

// 锁类型
export const LOCK_TYPE = {
    OFFLINE_TASK: 'BATCH_TASK',
    OFFLINE_SCRIPT: 'BATCH_SCRIPT',
    STREAM_TASK: 'STREAM_TASK',
}

// 资源类型
export const RESOURCE_TYPE = {
    JAR: 1,
    PY: 2,
}

// 调度状态
export const SCHEDULE_STATUS = {
    NORMAL: 1,
    STOPPED: 2,
}

export const MENU_TYPE = {
    TASK: 'TaskManager',
    TASK_DEV: 'TaskDevelop',
    SCRIPT: 'ScriptManager',
    RESOURCE: 'ResourceManager',
    FUNCTION: 'FunctionManager',
    COSTOMFUC: 'CustomFunction',
    SYSFUC: 'SystemFunction',
    TABLE: 'TableQuery',
}

export const PROJECT_ROLE = { // 项目角色
    PROJECT_OWNER: 2, // 项目所有者
    TENANT_OWVER: 1, // 租户所有者
    VISITOR: 4, // 访客
}

export const DATA_TYPE_ARRAY = [ // sql/oracle/sqlserver
    DATA_SOURCE.MYSQL,
    DATA_SOURCE.ORACLE,
    DATA_SOURCE.SQLSERVER,
]

export const TASK_TYPE = { // 任务类型
    SQL: 0,
    MR: 1,
    SYNC: 2,
    PYTHON: 3,
    R: 4,
    VIRTUAL_NODE: 5,
}

export const SCRIPT_TYPE = { // 脚本类型
    SQL: 0,
}

export const TASK_TYPE_ARRAY = [ //
    TASK_TYPE.SQL,
    TASK_TYPE.MR,
    TASK_TYPE.SYNC,
    TASK_TYPE.PYTHON,
    TASK_TYPE.VIRTUAL_NODE,
]

export const TASK_STATUS = { // 任务状态
    WAIT_SUBMIT: 0,
    CREATED: 1,
    INVOKED: 2,
    DEPLOYING: 3,
    RUNNING: 4,
    FINISHED: 5,
    STOPING: 6,
    STOPED: 7,
    RUN_FAILED: 8,  
    SUBMIT_FAILED: 9,
    SUBMITTING: 10,
    RESTARTING: 11,
    SET_SUCCESS: 12,
    WAIT_RUN: 16,
    FROZEN: 18,
}

export const hdfsFieldTypes = [ // hdfs 类型
    'STRING',
    'VARCHAR',
    'CHAR',
    'TINYINT',
    'SMALLINT',
    'INT',
    'BIGINT',
    'FLOAT',
    'DOUBLE',
    'TIMESTAMP',
    'DATE',
]

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
]

// 实时任务状态过滤选项
// 16,17 等待运行
export const taskStatusFilter = [{
    text: '等待提交',
    value: 0,
}, {
    text: '等待运行',
    value: 16,
}, {
    text: '运行中',
    value: 4,
}, {
    text: '停止',
    value: 7,
}, {
    text: '失败',
    value: 8,
}]

// 离线任务状态过滤选项
// UNSUBMIT 0;
// RUNNING  4;
// FINISHED 5;
// FAILED :8;
// WAITENGINE : 16:
// SUBMITTING: 10;
// CANCELED: 7;
// FROZEN: 18;
export const offlineTaskStatusFilter = [{
    id: 1,
    text: '等待提交',
    value: 0,
}, {
    id: 2,
    text: '提交中',
    value: 10,
}, {
    id: 3,
    text: '等待运行',
    value: 16,
}, {
    id: 4,
    text: '运行中',
    value: 4,
}, {
    id: 5,
    text: '完成',
    value: 5,
}, {
    id: 6,
    text: '取消',
    value: 7,
}, {
    id: 7,
    text: '失败',
    value: 8,
}, {
    id: 8,
    text: '冻结',
    value: 18,
}]

export const offlineTaskTypeFilter = [{
    id: 1,
    text: 'SQL',
    value: TASK_TYPE.SQL,
}, {
    id: 2,
    text: 'MR',
    value: TASK_TYPE.MR,
}, {
    id: 3,
    text: '数据同步',
    value: TASK_TYPE.SYNC,
}, {
    id: 4,
    text: 'Virtual',
    value: TASK_TYPE.VIRTUAL_NODE,
}, {
    id: 5,
    text: 'Python',
    value: TASK_TYPE.PYTHON,
}, {
    id: 6,
    text: 'R',
    value: TASK_TYPE.R,
}]

export const offlineTaskPeriodFilter = [{
    id: 1,
    text: '分钟任务',
    value: 0,
}, {
    id: 2,
    text: '小时任务',
    value: 1,
}, {
    id: 3,
    text: '天任务',
    value: 2,
}, {
    id: 4,
    text: '周任务',
    value: 4,
}, {
    id: 5,
    text: '月任务',
    value: 5,
}]

export const ScheduleTypeFilter = [{ // 调度过滤
    text: '周期调度',
    value: 0,
}, {
    text: '补数据',
    value: 1,
}]

export const AlarmStatusFilter = [{ // 告警状态过滤选项
    text: '正常',
    value: 0,
}, {
    text: '关闭',
    value: 1,
}]

export const jobTypes = [{ // 调度类型 0-周期调度 ， 1-补数据类型
    text: '全部',
    value: '',
}, {
    text: '周期调度',
    value: 0,
}, {
    text: '补数据',
    value: 1,
}]

export const DataSourceTypeFilter = [{ // 数据源类型过滤选项
    text: 'MySQL',
    value: 1,
}, {
    text: 'Oracle',
    value: 2,
}, {
    text: 'SQLServer',
    value: 3,
}, {
    text: 'HDFS',
    value: 6,
}, {
    text: 'Hive',
    value: 7,
}, {
    text: 'HBase',
    value: 8,
}]

export const propEditorOptions = { // 编辑器选项
    mode: 'text/x-properties',
    lint: true,
    indentWithTabs: true,
    smartIndent: true,
    lineNumbers: true,
    autofocus: false,
}

export const defaultEditorOptions = { // 编辑器选项
    mode: 'text/x-sql',
    lint: true,
    indentWithTabs: true,
    smartIndent: true,
    lineNumbers: true,
    autofocus: false,
    // extraKeys: { 'Ctrl-Space': 'autocomplete' },
}

export const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
}

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
        data:['邮件营销','联盟广告','视频广告']
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

export const pieChartOptions ={
    title : {
        text: '某站点用户访问来源',
        subtext: '纯属虚构',
        textAlign: 'left',
        textBaseline: 'top',
        textStyle: {
            fontSize: 14,
            fontWeight: 'bold',
        },
        x: 'left'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    legend: {
        orient: 'vertical',
        right: 'right',
        top: 'middle',
        data: []
    },
    color: ['#f25d5d', '#9a64fb', '#5d99f2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
    series : [
       {
            name:'访问来源',
            type:'pie',
            radius: ['50%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            cursor: 'initial',
            label: {
                normal: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    show: true,
                    textStyle: {
                        fontSize: '30',
                        fontWeight: 'bold'
                    }
                }
            },
            labelLine: {
                normal: {
                    show: false
                }
            },
            data:[]
        }
    ]
};

export const defaultBarOption = {
    title: {
        text: '世界人口总量',
        textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
        },
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    color: ['#5dd1f2', '#5d99f2', '#9a64fb', '#5df2c3', '#eeeeee'],
    legend: {
        data: ['2011年', '2012年']
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
        type: 'value',
        show: false,
        boundaryGap: [0, 0.01]
    },
    yAxis: {
        type: 'category',
        data: ['巴西', '美国','印度','中国','世界人口(万)'],
        axisLine: {
            lineStyle: {
                color: '#dddddd',
                width: 2,
            }
        },
        position: 'top',
        axisLabel: {
            textStyle: {
                color: '#666666'
            },
        },
        axisTick: {
            show: false,
        },
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
            center: [-10 , '0%'],
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

