import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from 'main/reducers/modules/user'
import { apps, app, licenseApps } from 'main/reducers/modules/apps'

import experiment from './modules/experiment';
import notebook from './modules/notebook';
import component from './modules/component';
import model from './modules/model';
import { editor } from './modules/editor';
import modal from './modules/modal';
import common from './modules/common';
import project from './modules/project'

// 全局State
const rootReducer = combineReducers({
    project,
    routing,
    user,
    app,
    apps,
    licenseApps,
    common,
    modal,
    editor,
    experiment,
    notebook,
    component,
    model
})

export default rootReducer
