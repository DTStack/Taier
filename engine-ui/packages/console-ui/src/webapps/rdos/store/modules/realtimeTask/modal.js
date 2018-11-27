import { combineReducers } from 'redux';

import { modalAction } from './actionTypes'

export const updateModal = (value) => {
    return { type: modalAction.UPDATE_MODAL_ACTION, data: value }
}

export const visibleReducer = (state = '', action) => {
    switch (action.type) {
    case modalAction.UPDATE_MODAL_ACTION:
        return action.data;
    default:
        return state;
    }
}
