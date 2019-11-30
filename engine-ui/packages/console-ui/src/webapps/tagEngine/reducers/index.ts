// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { apps, app, licenseApps } from 'main/reducers/modules/apps'

// 全局State
import { user } from './modules/user'
import { project, projects } from './modules/project'

const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    licenseApps, // licenseApps权限
    project,
    projects, // 获取所有的项目
})

export default rootReducer
