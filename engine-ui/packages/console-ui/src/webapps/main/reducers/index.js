import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from './modules/user'
import { apps } from './modules/apps'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
})

export default rootReducer
