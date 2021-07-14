import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import consoleUser from './modules/consoleUser'

import { testStatus, showRequireStatus } from './modules/cluster';

// 全局State
const rootReducer = combineReducers({
    routing,
    consoleUser,
    testStatus,
    showRequireStatus
})

export default rootReducer
