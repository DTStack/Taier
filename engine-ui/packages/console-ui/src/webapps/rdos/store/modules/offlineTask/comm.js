/**
 * 离线任务功能Redux模块
 */
import { combineReducers } from 'redux';
import assign from 'object-assign';

import {
    commAction
} from './actionType';
import { TASK_TYPE } from '../../../comm/const';

import Api from '../../../api';
import manageApi from '../../../api/dataManage';

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
const tables = (state = {}, action) => {
    const { type, key, payload } = action;
    const newState = assign({}, state);
    switch (type) {
        case commAction.SET_TABLE_LIST: {
            newState[key] = payload
            return newState;
        }

        default: return newState;
    }
}
const scriptTypes = (state = [], action) => {
    switch (action.type) {
        case commAction.GET_SCRIPT_TYPES: {
            return action.payload;
        }
        default: return state;
    }
}
const projectTables = (state = {}, action) => {
    const { type, payload } = action;
    const { projectIdentifier, tableList } = payload || {};
    const newState = assign({}, state);
    switch (type) {
        case commAction.SET_PROJECT_TABLE_LIST: {
            newState[projectIdentifier] = tableList;
            return newState;
        }

        default: return newState;
    }
}
export const commReducer = combineReducers({
    taskTypes,
    taskTypeFilter,
    tables,
    scriptTypes,
    projectTables
});

/**
 *  Actions
 */
/**
 * @param type 任务类型/脚本任务，获取spark,libra任务/脚本不同表
 */
export const getTableList = (projectId, type) => {
    return (dispatch, getState) => {
        Api.getTableListByName({
            appointProjectId: projectId,
            ...type
        }).then((res) => {
            if (res.code == 1) {
                let { data } = res;
                dispatch({
                    type: commAction.SET_TABLE_LIST,
                    payload: data.children,
                    key: projectId
                })
            }
        })
    }
}
export const getTableListByProject = (projectIdentifier, type) => {
    return (dispatch, getState) => {
        return manageApi.getTableListByProjectList({
            projectIdentifier
        }).then((res) => {
            if (res.code == 1) {
                let { data } = res;
                dispatch({
                    type: commAction.SET_PROJECT_TABLE_LIST,
                    payload: {
                        projectIdentifier,
                        tableList: data.children
                    }
                });
                return [projectIdentifier, data.children];
            }
        })
    }
}
export const getTaskTypes = () => {
    return (dispatch, getState) => {
        Api.getTaskTypes().then(res => {
            if (res.code === 1) {
                const taskTypes = res.data;

                const offlineTaskTypeFilter = taskTypes && taskTypes.map(type => {
                    return {
                        value: type.key,
                        id: type.key,
                        text: type.value
                    }
                });

                dispatch({
                    type: commAction.GET_TASK_TYPES,
                    payload: taskTypes || []
                })

                dispatch({
                    type: commAction.GET_TASK_TYPE_FILTER,
                    payload: (offlineTaskTypeFilter || []).concat({ id: TASK_TYPE.NOTEBOOK, value: TASK_TYPE.NOTEBOOK, text: 'Notebook' }, { id: TASK_TYPE.EXPERIMENT, value: TASK_TYPE.EXPERIMENT, text: '算法实验' })
                })
            }
        })
    }
}
export const getScriptTypes = () => {
    return (dispatch, getState) => {
        Api.getScriptTypes().then(res => {
            if (res.code === 1) {
                const scriptTypes = res.data;
                dispatch({
                    type: commAction.GET_SCRIPT_TYPES,
                    payload: scriptTypes || []
                })
            }
        })
    }
}
