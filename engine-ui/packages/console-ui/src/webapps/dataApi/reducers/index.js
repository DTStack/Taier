import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import { user } from 'main/reducers/modules/user'
import { apps, app } from 'main/reducers/modules/apps'

import common from './modules/common';
import dashBoard from './modules/dashBoard';
import apiMarket from './modules/apiMarket';
import mine from './modules/mine';
import apiManage from './modules/apiManage';
import approval from './modules/approval';
import dataSource from './modules/dataSource';

// 全局State
const rootReducer = combineReducers({
    routing,
    dataSource,
    user,
    apps,
    app,
    common,
    dashBoard,
    apiMarket,
    mine,
    apiManage,
    approval
})

export default rootReducer
