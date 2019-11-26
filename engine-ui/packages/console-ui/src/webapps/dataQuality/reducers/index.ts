import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from 'main/reducers/modules/user'
import { apps, app, licenseApps } from 'main/reducers/modules/apps'
import { project, projects, projectList, panelLoading, allProjects } from '../actions/project'
import dashBoard from './modules/dashBoard';
import taskQuery from './modules/taskQuery';
import dataCheck from './modules/dataCheck';
import dataSource from './modules/dataSource';
import ruleConfig from './modules/ruleConfig';
import keymap from './modules/dataCheck/keymap';
import common from './modules/common';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    project,
    projects,
    projectList,
    panelLoading,
    allProjects,
    licenseApps,
    dashBoard,
    taskQuery,
    dataCheck,
    dataSource,
    ruleConfig,
    common,
    keymap
})

export default rootReducer
