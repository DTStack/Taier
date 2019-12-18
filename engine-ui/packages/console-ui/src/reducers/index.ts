import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import consoleUser from './modules/consoleUser'
import { user } from 'dt-common/src/reducers/modules/user'
import { apps, app, licenseApps } from 'dt-common/src/reducers/modules/apps'
import { msgList } from 'dt-common/src/reducers/modules/message'

import { testStatus, showRequireStatus } from './modules/cluster';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    app,
    msgList,
    licenseApps,
    consoleUser,
    testStatus,
    showRequireStatus
})

export default rootReducer
