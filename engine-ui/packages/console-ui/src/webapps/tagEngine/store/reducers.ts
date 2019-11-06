// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { apps, app, licenseApps } from 'main/reducers/modules/apps'

// 全局State
import { user, projectUsers, notProjectUsers } from './modules/user'
import { project, projects, allProjects } from './modules/project'
// 实时任务
import { realtimeTask } from './modules/realtimeTask'
import { editor } from './modules/editor';
// 数据源
import { dataSource } from './modules/dataSource';
// 运维中心
import { operation } from './modules/operation';

import { visibleSearchTask } from './modules/comm';

const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    licenseApps, // licenseApps权限
    projectUsers, // 项目用户列表
    notProjectUsers, // 非项目用户列表
    project,
    projects, // 用户有权限的项目
    allProjects, // 全局所有项目
    realtimeTask, // 实时任务
    editor, // 编辑器
    operation, // 运维中心
    dataSource,
    visibleSearchTask
})

export default rootReducer
