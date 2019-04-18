import componentActionType from '../../consts/componentActionType';
import { isEqual } from 'lodash'
const mockData = require('./mocks/data.json');
const graphData = mockData.data ? JSON.parse(mockData.data.sqlText) : [];
export function getTaskData () {
    return (dispatch) => {
        // TODO 请求接口
        dispatch({
            type: componentActionType.GET_TASK_DATA,
            payload: graphData
        })
    }
}
export function updateTaskData (graphData) {
    return {
        type: componentActionType.UPDATE_TASK_DATA,
        payload: graphData
    }
}
export function saveGraph (payload) {
    return {
        type: componentActionType.SAVE_GRAPH,
        payload
    }
}
export function saveSelectedCell (payload) {
    return (dispatch, getState) => {
        const cell = getState().component.selectedCell;
        if (!isEqual(cell, payload)) {
            dispatch({
                type: componentActionType.SAVE_SELECTED_CELL,
                payload
            })
        }
    }
}

export function getRunTaskList (taskId, type) {
    return (dispatch) => {
        // TODO 请求接口
        const response = {
            code: 1,
            data: {
                jobIds: { 2510: 2510, 2511: 2511, 2512: 2512, 2513: 2513 },
                result: null
            }
        }
        const jobs = [];
        for (const key in response.data.jobIds) {
            if (response.data.jobIds.hasOwnProperty(key)) {
                const element = response.data.jobIds[key];
                jobs.push(element);
            }
        }
        dispatch({
            type: componentActionType.ADD_JOBID,
            payload: response.data.jobIds
        })
        dispatch(getRunningTaskStatus(jobs));
    }
}

export function getRunningTaskStatus (jobIds) {
    return (dispatch) => {
        // TODO 请求接口
        const response = {
            code: 1,
            data: {
                2510: 1,
                2511: 0,
                2512: 0,
                2513: 0
            }
        }
        dispatch({
            type: componentActionType.CHANGE_TASK_STATUS,
            payload: response.data
        })
        // 模拟1秒钟后的轮训
        setTimeout(() => {
            const response = {
                code: 1,
                data: {
                    2510: 2,
                    2511: 1,
                    2512: 0,
                    2513: 0
                }
            }
            dispatch({
                type: componentActionType.CHANGE_TASK_STATUS,
                payload: response.data
            })
        }, 1000)
    }
}
