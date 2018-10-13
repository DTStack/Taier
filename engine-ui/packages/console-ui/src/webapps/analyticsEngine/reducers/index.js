import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from 'main/reducers/modules/user'
import { apps, app } from 'main/reducers/modules/apps'

import workbench from './modules/workbench';
import { editor } from './modules/editor';
import common from './modules/common';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    common,
    editor,
    workbench,
})

export default rootReducer
