// 常量

/** 
 * 所有应用的唯一ID
*/
export const MY_APPS = {
    MAIN: 'main',
    RDOS: 'rdos',
    STREAM: 'stream',
    DATA_QUALITY: 'dataQuality',
    API: 'dataApi',
    LABEL: 'dataLabel',
    DATA_MAP: 'map',
    META_DATA: 'metaData',
    ANALYTICS_ENGINE: 'analyticsEngine',
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
 * 引用角色
 */
export const RDOS_ROLE = { // 项目角色
    TENANT_OWVER: 1, // 租户所有者
    PROJECT_OWNER: 2, // 项目所有者
    PROJECT_ADMIN: 3, // 项目管理员
    VISITOR: 4, // 访客
    OPERATION: 5, // 运维
    DEVELOPER: 6, // 开发者
    CUSTOM: 7, // 自定义
}

export const ANALYTICS_ENGINE_ROLE = { // 项目角色
    TENANT_OWVER: 1, // 租户所有者
    PROJECT_ADMIN: 3, // 项目管理员
    VISITOR: 4, // 访客
    DEVELOPER: 6, // 开发者
}
/**
 * 应用角色
 */
export const APP_ROLE = { // 项目角色
    TENANT_OWVER: 1, // 租户所有者
    ADMIN: 2, // 应用管理者
    VISITOR: 3, // 访客
    DEVELOPER: 4, // 开发者
    CUSTOM: 5, // 自定义
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