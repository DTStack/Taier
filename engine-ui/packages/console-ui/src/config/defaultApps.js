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
        name: '离线计算',
        filename: 'batch.html',
        link: '/batch.html#',
        target: '_self',
        enable: true,
        hasProject: true, // 是否拥有项目选择
        default: true, // 是否为默认应用选项
        apiBase: '/rdos',
        icon: '/public/main/img/icon_1.png',
        description: '一站式大数据开发平台，帮助企业快速完全数据中台搭建',
    },{
        id: 'stream',
        name: '流计算',
        filename: 'stream.html',
        link: '/stream.html#',
        target: '_self',
        enable: true,
        hasProject: true, // 是否拥有项目选择
        default: true, // 是否为默认应用选项
        apiBase: '/streamapp',
        icon: '/public/main/img/icon_2.png',
        description: '基于FlinkSQL的流计算开发平台，快速对接数据源，帮助企业向实时化、智能化大数据计算升级转型',
    },{
        id: 'analyticsEngine',
        name: '分析引擎',
        link: 'analytics.html#/',
        filename: 'analytics.html',
        target: '_self',
        enable: true,
        apiBase: '/analytics',
        icon: '/public/main/img/icon_3.png',
        description: '海量数据秒级查询，极速响应能力，帮助企业自由的数据探索',
    },{
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html#/',
        filename: 'dataQuality.html',
        target: '_self',
        enable: true,
        apiBase: '/dq',
        icon: '/public/main/img/icon_4.png',
        description: '对过程数据和结果数据进行质量校验，帮助企业及时发现数据质量问题',
    },{
        id: 'dataApi',
        name: '数据API',
        link: 'dataApi.html#/',
        filename: 'dataApi.html',
        target: '_self',
        enable: true,
        apiBase: '/dataApi',
        icon: '/public/main/img/icon_5.png',
        description: '快速生成数据API、统一管理API服务，帮助企业提高数据开放效率',
    }, {
        id: 'dataLabel',
        name: '标签工厂',
        link: 'dataLabel.html#/',
        filename: 'dataLabel.html',
        target: '_self',
        enable: false,
        apiBase: '/dataLabel',
        icon: '/public/main/img/icon_4.png',
        description: '快速生成规则标签，提供便捷、统一标签服务管理',
    }, {
        id: 'console',
        name: '控制台',
        link: 'console.html#/',
        filename: 'console.html',
        target: '_self',
        enable: true,
        apiBase: '/console',
        icon: '/public/main/img/icon_6.png',
        description: '计算资源分配、多集群管理',
        needRoot: true,
        disableExt: true // 是否禁用消息，橘色管理模块
    }, {
        id: 'map',
        name: '数据地图',
        link: 'map.html#/',
        filename: 'map.html',
        target: '_self',
        enable: false,
        apiBase: '/map',
        description: '可视化的数据资产中心，帮助企业全盘掌控数据资产情况和数据的来源去向',
    },
];