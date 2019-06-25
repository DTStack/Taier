import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import consoleUser from './modules/consoleUser'
import { user } from 'main/reducers/modules/user'
import { apps, app, licenseApps } from 'main/reducers/modules/apps'
import { testStatus, showRequireStatus } from './modules/cluster';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    app,
    licenseApps,
    consoleUser,
    testStatus,
    showRequireStatus
})

export default rootReducer
