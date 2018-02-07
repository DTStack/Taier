import { combineReducers } from 'redux';

import { visibleReducer } from './modal'
import { realtimeTree } from './tree'
import { resources } from './res'
import { pages, currentPage } from './browser'

export const realtimeTask = combineReducers({
    modal: visibleReducer,
    realtimeTree,
    resources,
    pages,
    currentPage,
})