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
        className: 'icon_dropdown_homepa'
    }, {
        id: 'rdos',
        name: '离线开发',
        filename: 'batch.html',
        link: '/batch.html#',
        target: '_self',
        enable: true,
        hasProject: true, // 是否拥有项目选择
        default: true, // 是否为默认应用选项
        apiBase: '/rdos',
        icon: '/public/main/img/icon_1.png',
        newIcon: '/public/main/img/icon_new1.png', // 新版的图标，因为旧版本的阿里云还要用所以不能删除
        description: '一站式大数据开发平台，帮助企业快速完成数据中台搭建',
        className: 'icon_dropdown_offlin'
    }, {
        id: 'stream',
        name: '实时开发',
        filename: 'stream.html',
        link: '/stream.html#',
        target: '_self',
        enable: true,
        hasProject: true, // 是否拥有项目选择
        default: true, // 是否为默认应用选项
        apiBase: '/streamapp',
        icon: '/public/main/img/icon_2.png',
        newIcon: '/public/main/img/icon_new2.png',
        description: '基于SQL的流计算开发平台，帮助企业向实时化、智能化升级转型',
        className: 'icon_dropdown_flow'
    }, {
        id: 'analyticsEngine',
        name: '分析引擎',
        link: 'analytics.html#/',
        filename: 'analytics.html',
        target: '_self',
        enable: true,
        disableMessage: true, // 禁用消息
        apiBase: '/analytics',
        icon: '/public/main/img/icon_3.png',
        newIcon: '/public/main/img/icon_new3.png',
        description: '海量数据秒级查询，极速响应能力，帮助企业自由的数据探索',
        className: 'icon_dropdown_engine'
    }, {
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html#/',
        filename: 'dataQuality.html',
        target: '_self',
        enable: true,
        apiBase: '/dq',
        icon: '/public/main/img/icon_4.png',
        newIcon: '/public/main/img/icon_new4.png',
        description: '对过程和结果数据进行校验，帮助企业及时发现数据质量问题',
        className: 'icon_dropdown_qualit'
    }, {
        id: 'dataApi',
        name: '数据API',
        link: 'dataApi.html#/',
        filename: 'dataApi.html',
        target: '_self',
        enable: true,
        apiBase: '/dataApi',
        icon: '/public/main/img/icon_5.png',
        newIcon: '/public/main/img/icon_new5.png',
        description: '数据API快速生成与注册，帮助企业提高数据开放与共享效率',
        className: 'icon_dropdown_api'
    }, {
        id: 'dataLabel',
        name: '标签工厂',
        link: 'dataLabel.html#/',
        filename: 'dataLabel.html',
        target: '_self',
        enable: false,
        apiBase: '/dataLabel',
        icon: '/public/main/img/icon_4.png',
        newIcon: '/public/main/img/icon_new4.png',
        description: '快速生成规则标签，提供便捷、统一标签服务管理',
        className: ''
    }, {
        id: 'science',
        name: '数据科学',
        link: 'science.html#/',
        filename: 'science.html',
        target: '_self',
        enable: true,
        apiBase: '/science',
        icon: '/public/main/img/icon_suanfa.png',
        newIcon: '/public/main/img/icon_newsuanfa.png',
        description: '可视化机器学习作业平台，帮助企业构建与提升算法服务能力',
        className: 'science'
    }, {
        id: 'console',
        name: '控制台',
        link: 'console.html#/',
        filename: 'console.html',
        target: '_self',
        enable: true,
        apiBase: '/console',
        disableSetting: true, // 禁用设置菜单的内容
        icon: '/public/main/img/icon_6.png',
        newIcon: '/public/main/img/icon_new6.jpg',
        description: '多集群管理与计算资源分配，帮助企业构建安全稳定的集群环境',
        needRoot: true,
        disableExt: true, // 是否禁用消息，橘色管理模块
        className: 'icon_dropdown_contro'
    }, {
        id: 'map',
        name: '数据地图',
        link: 'map.html#/',
        filename: 'map.html',
        target: '_self',
        enable: false,
        apiBase: '/map',
        description: '可视化的数据资产中心，帮助企业全盘掌控数据资产情况和数据的来源去向',
        className: ''
    }
];
