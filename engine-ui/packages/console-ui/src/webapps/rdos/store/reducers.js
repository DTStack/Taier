// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

// 全局State
import { user, projectUsers, notProjectUsers } from './modules/user'
import { project, projects } from './modules/project'
// 实时任务
import { realtimeTask } from './modules/realtimeTask'
// 离线任务
import { offlineTask } from './modules/offlineTask';
import { sqlEditor } from './modules/offlineTask/sqlEditor';
// 数据源
import { dataSource } from './modules/dataSource';
// 数据管理
import { dataManageReducer } from './modules/dataManage';
// 运维中心
import { operation } from './modules/operation';

import { visibleSearchTask } from './modules/comm';

const rootReducer = combineReducers({
    routing,
    user,
    projectUsers, // 项目用户列表
    notProjectUsers, // 非项目用户列表
    project,
    projects,
    realtimeTask, // 实时任务
    offlineTask, // 离线任务
    sqlEditor,
    operation, // 运维中心
    dataManage: dataManageReducer,
    dataSource,
    visibleSearchTask,
})

export default rootReducer
