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

export const periodType = [ // 调度类型
    '未知类型', 
    '小时', 
    '天', 
    '周', 
    '月', 
    '手动触发'
]

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