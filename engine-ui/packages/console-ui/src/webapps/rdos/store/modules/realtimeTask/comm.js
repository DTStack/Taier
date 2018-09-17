/**
 * 离线任务功能Redux模块
 */
import { combineReducers } from 'redux';

import {
    commAction,
} from './actionTypes';

import Api from '../../../api';

const taskTypes = (state = [], action) => {
    switch (action.type) {
        case commAction.GET_TASK_TYPES: {
            return action.payload;
        }
        default: return state;
    }
}

const taskTypeFilter = (state = [], action) => {
    switch (action.type) {
        case commAction.GET_TASK_TYPE_FILTER: {
            return action.payload;
        }

        default: return state;
    }
}

export const commReducer = combineReducers({
    taskTypes,
    taskTypeFilter,
});

/**
 *  Actions
 */
export const getTaskTypes = () => {
    return (dispatch, getState) => {

        const currentState = getState();
        const { realtimeTask } = currentState;
        if (realtimeTask.comm.taskTypes && realtimeTask.comm.taskTypes.length > 0) {
            return {};
        }
        Api.getRealtimeTaskTypes().then(res => {
            if (res.code === 1) {
                const taskTypes = res.data;

                const realtimeTaskTypeFilter = taskTypes && taskTypes.map(type => {
                    return {
                        value: type.key,
                        id: type.key,
                        text: type.value,
                    }
                });

                dispatch({
                    type: commAction.GET_TASK_TYPES,
                    payload: taskTypes || [],
                })

                dispatch({
                    type: commAction.GET_TASK_TYPE_FILTER,
                    payload: realtimeTaskTypeFilter || [],
                })
            }
        })
    }
}
