// 常量
declare var window: any;
/**
 * 所有应用的唯一ID
*/
export const MY_APPS = {
    MAIN: 'main',
    RDOS: 'rdos',
    STREAM: 'stream',
    DATA_QUALITY: 'dataQuality',
    API: 'dataApi',
    DATA_MAP: 'map',
    META_DATA: 'metaData',
    ANALYTICS_ENGINE: 'analyticsEngine',
    CONSOLE: 'console',
    SCIENCE: 'science',
    TAG: 'tagEngine'
}

export const mainApp = {
    id: 'main',
    name: '首页',
    link: 'index.html',
    filename: 'index.html',
    target: '_self',
    enable: true,
    apiBase: '/main'
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
    FTP: 9
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
    CUSTOM: 7 // 自定义
}

export const ANALYTICS_ENGINE_ROLE = { // 项目角色
    TENANT_OWVER: 1, // 租户所有者
    PROJECT_ADMIN: 3, // 项目管理员
    VISITOR: 4, // 访客
    DEVELOPER: 6 // 开发者
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
    PROOWNER: 6 // 项目所有者
}

export const QUALITY_PRO_ROLES = { // 项目角色
    TENANT_OWVER: 1, // 租户所有者
    PRO_ADMIN: 2, // 项目管理员
    VISITOR: 3, // 访客
    DATA_DEVELOP: 4, // 数据开发
    PRO_OWNER: 5 // 项目所有者
}

export const API_PRO_ROLES: any = {
    TENANT_OWVER: 1, // 租户所有者
    API_MANAGER: 2, // API管理员
    VISITOR: 3, // 访客
    APP_DEVE: 4, // 应用开发
    DATA_DEVE: 5, // 数据开发
    PRO_OWNER: 6, // 项目所有者
    PRO_MANAGER: 7 // 项目管理员
}
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
/**
 * 获取Header的logo
 * @param {string} app 应用id
 * @param {string} theme 主题
 */
export function getHeaderLogo (app?: any, theme?: any) {
    theme = theme || window.APP_CONF.theme || 'default';
    app = app || MY_APPS.MAIN;
    const logo = window.APP_CONF.logo;
    const defaultLogo = 'public/main/img/logo.svg';
    const aliyunDefaultLogo = 'public/main/img/aliyun-logo.svg';

    const themeLogoMap: any = {
        default: logo || defaultLogo,
        aliyun: aliyunDefaultLogo
    }

    return themeLogoMap[theme] || defaultLogo;
}

export function getThemeBanner (theme?: string) {
    theme = theme || window.APP_CONF.theme || 'default';
    switch (theme) {
        case 'aliyun': {
            return 'public/main/img/aliyun-banner.png';
        }
        default: {
            return 'public/main/img/bg1.jpg';
        }
    }
}

export const appUriDict = {
    RDOS: {
        OPERATION: 'operation',
        OPERATION_MANAGER: 'operation_manager',
        DEVELOP: 'offline/task'
    }
}
