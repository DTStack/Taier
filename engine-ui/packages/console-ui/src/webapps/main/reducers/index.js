import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from './modules/user'
import { apps, app, licenseApps, isLicenseLoaded } from './modules/apps'
import { msgList } from './modules/message'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    licenseApps,
    isLicenseLoaded,
    msgList
})

export default rootReducer
