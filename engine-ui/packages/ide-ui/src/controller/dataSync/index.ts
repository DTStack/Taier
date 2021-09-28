import { combineReducers } from 'redux';

import { workbenchReducer } from './workbench';
import { dataSyncReducer } from './dataSync';
import { tableTypes } from './tableType';

export const dataSync = combineReducers({
    workbench: workbenchReducer,
    dataSync: dataSyncReducer,
    tableTypes, // 表类型
});
