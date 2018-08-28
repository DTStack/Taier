/**
 * 离线任务功能Redux模块
 */
import { combineReducers } from 'redux';
import assign from 'object-assign';

import {
    commAction,
} from './actionType';

import Api from '../../../api';

const taskTypes = (state = [], action) => {
    switch (action.type) {
        case commAction.GET_TASK_TYPES: {
            return action.payload;
        }

        default: return state;
    }
}

export const commReducer = combineReducers({
    taskTypes,
});

/**
 *  Actions
 */
export const getTaskTypes = () => {
    return dispatch => {
        Api.getTaskTypes().then(res => {
            if (res.code === 1) {
                dispatch({
                    type: commAction.GET_TASK_TYPES,
                    payload: res.data || [],
                })
            }
        })
    }
}
