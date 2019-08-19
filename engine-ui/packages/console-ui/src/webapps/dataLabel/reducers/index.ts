import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from 'main/reducers/modules/user'
import { apps, app } from 'main/reducers/modules/apps'

import dataSource from './modules/dataSource';
import common from './modules/common';
import tagConfig from './modules/tagConfig';
import apiMarket from './modules/apiMarket';
import mine from './modules/mine';
import apiManage from './modules/apiManage';
import approval from './modules/approval';

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    app,
    apps,
    tagConfig,
    dataSource,
    common,
    apiMarket,
    mine,
    apiManage,
    approval
})

export default rootReducer
