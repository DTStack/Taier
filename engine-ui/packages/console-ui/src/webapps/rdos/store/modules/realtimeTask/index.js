import { combineReducers } from 'redux';

import { visibleReducer } from './modal'
import { realtimeTree } from './tree'
import { resources } from './res'
import { commReducer } from './comm'
import { pages, currentPage, inputData, outputData, dimensionData } from './browser'

export const realtimeTask = combineReducers({
    modal: visibleReducer,
    realtimeTree,
    resources,
    pages,
    currentPage,
    inputData,
    outputData,
    dimensionData,
    comm: commReducer
})
