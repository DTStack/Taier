// 常量

/** 
 * 所有应用的唯一ID
*/
export const MY_APPS = {
    MAIN: 'main',
    RDOS: 'rdos',
    DATA_QUALITY: 'dataQuality',
    API: 'api',
    LABEL: 'label',
    DATA_MAP: 'map',
    META_DATA: 'metaData',
}

export const mainApp = {
    id: 'main',
    name: '首页',
    link: 'index.html',
    filename: 'index.html',
    target: '_self',
    enable: true,
    apiBase: '/main',
}

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

/**
 * 项目角色
 */
export const RDOS_PROJECT_ROLE = { // 项目角色
    PROJECT_OWNER: 2, // 项目所有者
    TENANT_OWVER: 1, // 租户所有者
    VISITOR: 4, // 访客
}

/**
 * 项目角色
 */
export const DQ_PROJECT_ROLE = { // 项目角色
    ADMIN: 1, // 租户所有者
    DATA_DEVELOPER: 2, // 项目所有者
    VISITOR: 3, // 访客
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