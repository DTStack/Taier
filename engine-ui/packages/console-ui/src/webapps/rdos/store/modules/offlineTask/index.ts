import { combineReducers } from 'redux';

import {
    taskTreeReducer,
    resourceTreeReducer,
    sparkFnTreeReducer, // 存spark第一级数据
    libraFnTreeReducer, // 存libra第一级数据
    libraSysFnTreeReducer,
    functionTreeReducer,
    sysFunctionTreeReducer,
    scriptTreeReducer,
    tableTreeReducer
} from './folderTree'

import { modalShowReducer } from './modal'
import { dataSyncReducer } from './dataSync'
import { workbenchReducer } from './workbench'
import { commReducer } from './comm'
import { workflowReducer } from './workflow';

export const offlineTask = combineReducers({
    comm: commReducer,
    modalShow: modalShowReducer,
    taskTree: taskTreeReducer,
    resourceTree: resourceTreeReducer,
    sparkTree: sparkFnTreeReducer,
    libraTree: libraFnTreeReducer,
    libraSysFnTree: libraSysFnTreeReducer,
    functionTree: functionTreeReducer,
    tableTree: tableTreeReducer,
    sysFunctionTree: sysFunctionTreeReducer,
    scriptTree: scriptTreeReducer,
    workbench: workbenchReducer,
    dataSync: dataSyncReducer,
    workflow: workflowReducer
});
