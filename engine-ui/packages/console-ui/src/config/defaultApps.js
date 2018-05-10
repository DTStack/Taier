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
        description: '一站式开发管理的界面，帮助企业快速搭建数据中心，帮助开发人员专注于数据价值的挖掘和探索',
    },{
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html#/',
        filename: 'dataQuality.html',
        target: '_blank',
        enable: true,
        apiBase: '/dq',
        icon: '/public/main/img/icon_2.png',
        description: '支持多种异构数据源的质量校验、通知、管理服务的一站式平台',
    },{
        id: 'dataApi',
        name: '数据API',
        link: 'dataApi.html#/',
        filename: 'dataApi.html',
        target: '_blank',
        enable: true,
        apiBase: '/dataApi',
        icon: '/public/main/img/icon_3.png',
        description: '快速将数据表生成数据API，在为企业搭建统一的对内、对外数据服务管理中心，提供易上手、低成本、稳定的数据开放共享服务',
    }, {
        id: 'dataLabel',
        name: '标签管理',
        link: 'dataLabel.html#/',
        filename: 'dataLabel.html',
        target: '_blank',
        enable: false,
        apiBase: '/dataLabel',
        icon: '/public/main/img/icon_4.png',
        description: '为企业提供快速生成规则标签的工具，提供便捷、统一标签服务管理',
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

