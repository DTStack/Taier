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
        icon: '/public/main/img/icon_1.png',
        description: '一站式数据开发管理平台，加速释放数据价值',
    },{
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html#/',
        filename: 'dataQuality.html',
        target: '_blank',
        enable: true,
        apiBase: '/dq',
        icon: '/public/main/img/icon_2.png',
        description: '支持多种异构数据源的质量校验、通知与管理',
    },{
        id: 'dataApi',
        name: '数据API',
        link: 'dataApi.html#/',
        filename: 'dataApi.html',
        target: '_blank',
        enable: true,
        apiBase: '/dataApi',
        icon: '/public/main/img/icon_3.png',
        description: '易上手、低成本、稳定的数据开放共享服务',
    }, {
        id: 'dataLabel',
        name: '标签工厂',
        link: 'dataLabel.html#/',
        filename: 'dataLabel.html',
        target: '_blank',
        enable: true,
        apiBase: '/dataLabel',
        icon: '/public/main/img/icon_4.png',
        description: '快速生成规则标签，提供便捷、统一标签服务管理',
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

