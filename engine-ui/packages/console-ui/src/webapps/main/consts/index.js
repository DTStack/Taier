// 常量

/** 
 * 数据源类型
*/
export const DATA_SOURCE = {
    MYSQL: 1,
    ORACLE: 2,
    SQLSERVER: 3,
    HDFS: 6,
    HIVE: 7,
    HBASE: 8,
    FTP: 9,
}

export const currentApps = [{
    id: 'dataQuality',
    name: '数据质量',
    link: 'dataQuality.html#/',
    filename: 'dataQuality.html',
    target: '_blank',
    enable: true,
    apiBase: '/dataQuality',
}]