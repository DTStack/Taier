/**
 * 基本配置
 */
export const UIC_BASE_URL = '/uic/api';
export const RDOS_BASE_URL = '/api/rdos';
export const DQ_BASE_URL = '/api/dq/service';
export const DATA_API_BASE_URL = '/api/dataApi/service';

export const rdosApp = {
    id: 'rdos',
    name: '开发套件',
    filename: 'rdos.html',
    link: '/rdos.html#/',
    target: '_blank',
    enable: true,
    hasProject: true, // 是否拥有项目选择
    default: true, // 是否为默认应用选项
    apiBase: '/rdos',
}

export const dqApp = {
    id: 'dataQuality',
    name: '数据质量',
    link: 'dataQuality.html#/',
    filename: 'dataQuality.html',
    target: '_blank',
    enable: true,
    apiBase: '/dq',
}