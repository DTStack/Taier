import { combineReducers } from 'redux';

import {
    taskTreeReducer,
    resourceTreeReducer,
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
    functionTree: functionTreeReducer,
    tableTree: tableTreeReducer,
    sysFunctionTree: sysFunctionTreeReducer,
    scriptTree: scriptTreeReducer,
    workbench: workbenchReducer,
    dataSync: dataSyncReducer,
    workflow: workflowReducer
});
