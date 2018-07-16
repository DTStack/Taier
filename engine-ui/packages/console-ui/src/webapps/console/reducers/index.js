import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'


import { user } from 'main/reducers/modules/user'
import { apps,app } from 'main/reducers/modules/apps'


// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    app,
})

export default rootReducer
