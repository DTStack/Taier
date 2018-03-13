import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from './modules/user'
import { apps } from 'main/reducers/modules/apps'

import dataCheck from './modules/dataCheck';
import dataSource from './modules/dataSource';
import ruleConfig from './modules/ruleConfig';
import keymap from './modules/dataCheck/keymap'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    dataCheck,
    dataSource,
    ruleConfig,
    keymap
})

export default rootReducer
