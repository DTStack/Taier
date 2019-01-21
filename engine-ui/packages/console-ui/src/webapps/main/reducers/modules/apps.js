import { assign } from 'lodash';
import defaultApps from 'config/defaultApps';
import appActions from 'main/consts/appActions';
export function apps (state = defaultApps, action) {
    switch (action.type) {
        default:
            return state
    }
}
const licenseDefaultState = [
    { // 0
        'id': 'rdos',
        'name': '离线计算',
        'is_Show': true,
        'children': [
            {
                'name': '数据开发',
                'is_Show': true,
                'children': null
            },
            {
                'name': '运维中心',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据源',
                'is_Show': true,
                'children': null
            },
            {
                'name': '项目管理',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据地图',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据模型',
                'is_Show': true,
                'children': null
            }
        ]
    },
    { // 1
        'id': 'stream',
        'name': '流计算',
        'is_Show': true,
        'children': [
            {
                'name': '数据源',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据开发',
                'is_Show': true,
                'children': null
            },
            {
                'name': '项目管理',
                'is_Show': true,
                'children': null
            },
            {
                'name': '运维中心',
                'is_Show': true,
                'children': null
            }
        ]
    },
    { // 2
        'id': 'analyticsEngine',
        'name': '分析引擎',
        'is_Show': true,
        'children': [
            {
                'name': '数据库管理',
                'is_Show': true,
                'children': null
            },
            {
                'name': '表管理',
                'is_Show': true,
                'children': null
            }
        ]
    },
    { // 3
        'id': 'dataQuality',
        'name': '数据质量',
        'is_Show': true,
        'children': [
            {
                'name': '概览',
                'is_Show': true,
                'children': null
            },
            {
                'name': '任务查询',
                'is_Show': true,
                'children': null
            },
            {
                'name': '规则配置',
                'is_Show': true,
                'children': null
            },
            {
                'name': '逐行校验',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据源管理',
                'is_Show': true,
                'children': null
            }
        ]
    },
    { // 4
        'id': 'dataApi',
        'name': '数据Api',
        'is_Show': true,
        'children': [
            {
                'name': '概览',
                'is_Show': true,
                'children': null
            },
            {
                'name': 'API市场',
                'is_Show': true,
                'children': null
            },
            {
                'name': '我是API',
                'is_Show': true,
                'children': null
            },
            {
                'name': 'API管理',
                'is_Show': true,
                'children': null
            },
            {
                'name': '授权与安全',
                'is_Show': true,
                'children': null
            },
            {
                'name': '数据源管理',
                'is_Show': true,
                'children': null
            }
        ]
    }
]
export function licenseApps (state = licenseDefaultState, action) {
    switch (action.type) {
        case appActions.GET_LICENSE_APP: {
            if (action.data != null) {
                action.data.splice(0, 1);
                return assign({}, state, action.data)
            }
            return state;
        }
        default:
            return state
    }
}

export function app (state = {}, action) {
    switch (action.type) {
        case appActions.UPDATE_APP: {
            if (action.data !== null) {
                return assign({}, state, action.data)
            }

            return state;
        }
        default:
            return state
    }
}
