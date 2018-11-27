import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from 'main/reducers/modules/user'
import { apps, app } from 'main/reducers/modules/apps'

import workbench from './modules/workbench';
import { editor } from './modules/editor';
import modal from './modules/modal';
import common from './modules/common';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    common,
    modal,
    editor,
    workbench
})

export default rootReducer
