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
    'ODPS'
]

export const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 12 },
    },
};