/**
 * 基本配置
 */
export const UIC_BASE_URL = '/uic/api';
export const RDOS_BASE_URL = '/api/rdos';
export const STREAM_BASE_URL = '/api/streamapp/service';
export const STREAM_BASE_URL_NOT_SERVICE = '/api/streamapp';
export const DQ_BASE_URL = '/api/dq/service';
export const DQ_BASE_URL_NOT_SERVICE = '/api/dq';
export const DATA_API_BASE_URL = '/api/da/service';
export const TAG_ENGINE_URL = '/api/tag/service';
export const CONSOLE_BASE_URL = '/api/console/service';
export const CONSOLE_BASE_UPLOAD_URL = '/api/console';
export const ANALYTICS_ENGINE_BASE_URL = '/api/analysis/service';
export const SCIENCE_BASE_URL = '/api/dataScience';

export const rdosApp = {
    id: 'rdos',
    name: '离线计算',
    filename: 'batch.html',
    link: '/batch.html#',
    target: '_blank',
    enable: true,
    hasProject: true, // 是否拥有项目选择
    default: true, // 是否为默认应用选项
    apiBase: '/rdos'
}

export const streamApp = {
    id: 'stream',
    name: '流计算',
    filename: 'stream.html',
    link: '/stream.html#',
    target: '_blank',
    enable: true,
    hasProject: true, // 是否拥有项目选择
    default: true, // 是否为默认应用选项
    apiBase: '/streamapp'
}

export const dqApp = {
    id: 'dataQuality',
    name: '数据质量',
    link: 'dataQuality.html#/',
    filename: 'dataQuality.html',
    target: '_blank',
    enable: true,
    apiBase: '/dq'
}

export const daApp = {
    id: 'dataApi',
    name: 'API管理',
    link: 'dataApi.html#/',
    filename: 'dataApi.html',
    target: '_blank',
    enable: true,
    apiBase: '/dataApi'
}

export const dlApp = {
    id: 'dataLabel',
    name: '标签工厂',
    link: 'dataLabel.html#/',
    filename: 'dataLabel.html',
    target: '_blank',
    enable: true,
    apiBase: '/tag'
}

export const consoleApp = {
    id: 'console',
    name: '控制台',
    link: 'console.html#/',
    filename: 'console.html',
    target: '_blank',
    enable: false,
    apiBase: '/console',
    needRoot: true,
    disableExt: true
}

export const aeApp = {
    id: 'analyticsEngine',
    name: '分析引擎',
    link: 'analytics.html#/',
    filename: 'analytics.html',
    target: '_blank',
    enable: true,
    apiBase: '/analytics',
    disableMessage: true // 禁用消息
}

export const scienceApp = {
    id: 'science',
    name: '分析引擎',
    link: 'science.html#/',
    filename: 'science.html',
    target: '_blank',
    enable: true,
    apiBase: '/science'
}
