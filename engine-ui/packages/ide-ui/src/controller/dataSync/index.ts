import { combineReducers } from 'redux';

import { workbenchReducer } from './workbench'

export const dataSync = combineReducers({
    workbench: workbenchReducer,
});
