import { assign } from 'lodash';

const defaultApps = [ // 应用数
    {
        id: 'index',
        name: '首页',
        link: 'index.html',
        target: '_self',
        enable: true,
    }, {
        id: 'rdos',
        name: '开发套件',
        link: 'rdos.html#/',
        target: '_blank',
        enable: true,
    },{
        id: 'dataQuality',
        name: '数据质量',
        link: 'dataQuality.html',
        target: '_blank',
        enable: true,
    },{
        id: 'api',
        name: 'API管理',
        link: 'api.html',
        target: '_blank',
        enable: true,
    }, {
        id: 'label',
        name: '标签管理',
        link: 'label.html',
        target: '_blank',
        enable: false,
    }, {
        id: 'map',
        name: '数据地图',
        link: 'map.html',
        target: '_blank',
        enable: false,
    }, {
        id: 'metaData',
        name: '元数据管理',
        link: 'metaData.html',
        target: '_blank',
        enable: false,
    },
]

export function apps(state = defaultApps, action) {
    switch (action.type) {
    default:
        return state
    }
}