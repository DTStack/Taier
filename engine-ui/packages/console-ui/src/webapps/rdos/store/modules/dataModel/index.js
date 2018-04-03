import { combineReducers } from 'redux';
import assign from 'object-assign';
import { cloneDeep, isEqual } from 'lodash';
import dataModelActions from './actionTypes';

const configures = function( state = {} , action) {
    return state;
}

export const dataModel = combineReducers({
    configures
});