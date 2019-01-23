// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { apps, app, licenseApps } from 'main/reducers/modules/apps'

// 全局State
import { user, projectUsers, notProjectUsers } from './modules/user'
import { project, projects, allProjects } from './modules/project'

// 离线任务
import { offlineTask } from './modules/offlineTask';
import { editor } from './modules/editor';
// 数据源
import { dataSource } from './modules/dataSource';
// 数据管理
import { dataManageReducer } from './modules/dataManage';
// 数据模型
import { dataModel } from './modules/dataModel';
// 运维中心
import { operation } from './modules/operation';

import { visibleSearchTask } from './modules/comm';

import { uploader } from './modules/uploader';

const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    licenseApps,
    projectUsers, // 项目用户列表
    notProjectUsers, // 非项目用户列表
    project,
    projects, // 用户有权限的项目
    allProjects, // 全局所有项目
    offlineTask, // 离线任务
    editor, // 编辑器
    operation, // 运维中心
    dataManage: dataManageReducer,
    dataSource, // 数据源
    visibleSearchTask,
    dataModel, // 数据模型
    uploader
})

export default rootReducer
