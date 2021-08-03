// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux'

// task
import { editor } from './editor';
import { dataSync } from './dataSync';

const rootReducer = combineReducers({
    routing,
    editor,
    dataSync
})

export default rootReducer
