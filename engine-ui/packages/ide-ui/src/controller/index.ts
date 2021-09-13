// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux';

// task
import { editor } from './editor';
import { dataSync } from './dataSync';
import { workbenchReducer } from './workbench';
import { catalogueReducer } from './catalogue'

const rootReducer = combineReducers({
    routing,
    editor,
    dataSync,
    workbenchReducer,
    catalogue: catalogueReducer
});

export default rootReducer;
