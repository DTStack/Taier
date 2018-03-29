import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from './modules/user'
import { apps } from './modules/apps'
import { msgList } from './modules/message'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    msgList,
})

export default rootReducer
