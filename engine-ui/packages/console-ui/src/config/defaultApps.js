/**
 * 应用配置数据
 */
module.exports = [
    {
        id: 'main',
        name: '首页',
        link: 'index.html',
        filename: 'index.html',
        target: '_self',
        enable: true,
        apiBase: '/main',
    }, {
        id: 'rdos',
        name: '开发套件',
        filename: 'rdos.html',
        link: '/rdos.html#/',
        target: '_blank',
        enable: true,
        hasProject: true, // 是否拥有项目选择
        default: true, // 是否为默认应用选项
        apiBase: '/rdos',
    },{
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html#/',
        filename: 'dataQuality.html',
        target: '_blank',
        enable: true,
        apiBase: '/dq',
    },{
        id: 'dataApi',
        name: '数据API',
        link: 'dataApi.html#/',
        filename: 'dataApi.html',
        target: '_blank',
        enable: true,
        apiBase: '/dataApi',
    }, {
        id: 'dataLabel',
        name: '标签管理',
        link: 'dataLabel.html#/',
        filename: 'dataLabel.html',
        target: '_blank',
        enable: true,
        apiBase: '/dataLabel',
    }, {
        id: 'map',
        name: '数据地图',
        link: 'map.html#/',
        filename: 'map.html',
        target: '_blank',
        enable: false,
        apiBase: '/map',
    }, {
        id: 'metaData',
        name: '元数据管理',
        filename: 'metaData.html#/',
        link: 'metaData.html',
        target: '_blank',
        enable: false,
        apiBase: '/metaData',
    },
];

