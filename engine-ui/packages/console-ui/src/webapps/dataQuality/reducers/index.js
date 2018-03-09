import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from 'main/reducers/modules/user'
import { apps } from 'main/reducers/modules/apps'

import dataCheck from './modules/dataCheck';
import dataSource from './modules/dataSource';
import keymap from './modules/dataCheck/keymap'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    dataCheck,
    dataSource,
    keymap
})

export default rootReducer
