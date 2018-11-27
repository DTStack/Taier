import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

import consoleUser from './modules/consoleUser'
import { user } from 'main/reducers/modules/user'
import { apps, app } from 'main/reducers/modules/apps'

// 全局State
const rootReducer = combineReducers({
    routing,
    user,
    apps,
    app,
    consoleUser
})

export default rootReducer
