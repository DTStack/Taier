/**
 * 离线任务功能Redux模块
 */
import { combineReducers } from 'redux';
import assign from 'object-assign';
import { get}  from 'lodash';

import {
    commAction
} from './actionType';
import { TASK_TYPE } from '../../../comm/const';

import Api from '../../../api';
import manageApi from '../../../api/dataManage';

const taskTypes = (state: any = [], action: any) => {
    switch (action.type) {
        case commAction.GET_TASK_TYPES: {
            return action.payload;
        }
        default: return state;
    }
}

const taskTypeFilter = (state: any = [], action: any) => {
    switch (action.type) {
        case commAction.GET_TASK_TYPE_FILTER: {
            return action.payload;
        }

        default: return state;
    }
}
const tables = (state: any = {}, action: any) => {
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
const scriptTypes = (state: any = [], action: any) => {
    switch (action.type) {
        case commAction.GET_SCRIPT_TYPES: {
            return action.payload;
        }
        default: return state;
    }
}
const projectTables = (state: any = {}, action: any) => {
    const { type, payload } = action;
    const { projectIdentifier, tableList }: any = payload || {};
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
export const getTableList = (projectId?: any, type?: any) => {
    return (dispatch: any, getState: any) => {
        Api.getTableListByName({
            appointProjectId: projectId,
            ...type
        }).then((res: any) => {
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
export const getTableListByProject = (projectIdentifier?: any, type?: any) => {
    return (dispatch: any, getState: any) => {
        return manageApi.getTableListByProjectList({
            projectIdentifier
        }).then((res: any) => {
            if (res.code == 1) {
                let { data } = res;
                const children = get(data, 'children', [])
                dispatch({
                    type: commAction.SET_PROJECT_TABLE_LIST,
                    payload: {
                        projectIdentifier,
                        tableList: children
                    }
                });
                return [projectIdentifier, children];
            }
        })
    }
}
export const getTaskTypes = () => {
    return (dispatch: any, getState: any) => {
        Api.getTaskTypes().then((res: any) => {
            if (res.code === 1) {
                const taskTypes = res.data;

                const offlineTaskTypeFilter = taskTypes && taskTypes.map((type: any) => {
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
    return (dispatch: any, getState: any) => {
        Api.getScriptTypes().then((res: any) => {
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
