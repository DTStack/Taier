import { combineReducers } from 'redux';

import { taskFlow, graphStatus } from './taskflow'

export const operation = combineReducers({
    taskFlow,
    graphStatus
})
