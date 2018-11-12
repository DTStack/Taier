import { hashHistory } from 'react-router'
import { cloneDeep } from "lodash";

import localDb from 'utils/localDb'
import Api from '../../../api'
import { browserAction } from './actionTypes'

export function publishTask(params) {
    const succCallback = (res) => {
        if (res.code === 1) {
            return res
        }
    }
    return Api.publishRealtimeTask(params)
        .then(succCallback);
}

// Action
/* eslint-disable */
export function closePage(id, pages, currentPage) {
    return dispatch => {
        const index = pages.findIndex((item) => {
            return id === item.id
        })
        if (index > -1) {
            if (currentPage.id === id && pages.length > 1) {
                const newNode = pages[index + 1] || pages[index - 1]
                dispatch(setCurrentPage(newNode))
            }
            return dispatch({
                type: browserAction.CLOSE_PAGE,
                data: index,
            })
        }
    }
}
/* eslint-disable */

export function openPage(params) {
    return (dispatch, getState) => {
        const { id } = params;
        const state = getState();
        const { realtimeTask } = state;
        const { pages = [] } = realtimeTask;
        const existPage = pages.find((page) => {
            return page.id == id
        })
        if (existPage) {
            if (location.pathname !== '/realtime/task') {
                hashHistory.push('/realtime/task')
            }
            return dispatch(setCurrentPage(existPage))
        }

        Api.getTask(params).then((res) => {
            if (res.code === 1 && res.data) {
                dispatch(newPage(res.data));
                if (location.pathname !== '/realtime/task') {
                    hashHistory.push('/realtime/task')
                }
            }
        })
    }
}

export function newPage(page) {
    return dispatch => {
        dispatch(setCurrentPage(page))
        return dispatch({
            type: browserAction.NEW_PAGE,
            data: page
        })
    }
}

export function findPage(page, pageArr) {
    return pageArr.findIndex((item) => { return item.id === page.id })
}

export function getPages() {
    return { type: browserAction.GET_PAGES }
}

export function getCurrentPage() {
    return { type: browserAction.GET_CURRENT_PAGE }
}

export function setCurrentPage(value) {
    return dispatch => {
        /**
         * 将value数据存入pages
         */
        dispatch(updatePage(value))
        /**
         * 将value存入currentValue
         */
        return dispatch({
            type: browserAction.SET_CURRENT_PAGE,
            data: value
        })
    }
}

export function closeOtherPages(current_page) {
    return {
        type: browserAction.CLOSE_OTHERS,
        data: current_page
    }
}

export function clearPages() {
    return {
        type: browserAction.CLEAR_PAGES,
    }
}

export function updatePage(page) {
    return {
        type: browserAction.UPDATE_PAGE,
        data: page,
    }
}

export function setInputData(value) {
    return {
        type: browserAction.SET_INPUT_DATA,
        data: value,
    }
}

export function getInputData(id) {
    return {
        type: browserAction.GET_INPUT_DATA,
        data: id,
    }
}

export function closeCurrentInputData(id) {
    return {
        type: browserAction.CLEAR_CURRENT_INPUT_DATA,
        data: id,
    }
}


export function closeOtherInputData(id) {
    return {
        type: browserAction.CLEAR_OTHER_INPUT_DATA,
        data: id,
    }
}

export function closeAllInputData() {
    return {
        type: browserAction.CLEAR_ALL_INPUT_DATA,
    }
}


export function setOutputData(value) {
    return {
        type: browserAction.SET_OUTPUT_DATA,
        data: value,
    }
}

export function getOutputData(id) {
    return {
        type: browserAction.GET_OUTPUT_DATA,
        data: id,
    }
}

export function closeCurrentOutputData(id) {
    return {
        type: browserAction.CLEAR_CURRENT_OUTPUT_DATA,
        data: id,
    }
}


export function closeOtherOutputData(id) {
    return {
        type: browserAction.CLEAR_OTHER_OUTPUT_DATA,
        data: id,
    }
}

export function closeAllOutputData() {
    return {
        type: browserAction.CLEAR_ALL_OUTPUT_DATA,
    }
}


export function setDimensionData(value) {
    return {
        type: browserAction.SET_DIMESION_DATA,
        data: value,
    }
}

export function getDimensionData(id) {
    return {
        type: browserAction.GET_DIMESION_DATA,
        data: id,
    }
}

export function closeCurrentDimensionData(id) {
    return {
        type: browserAction.CLEAR_CURRENT_DIMESION_DATA,
        data: id,
    }
}


export function closeOtherDimensionData(id) {
    return {
        type: browserAction.CLEAR_OTHER_DIMESION_DATA,
        data: id,
    }
}

export function closeAllDimensionData() {
    return {
        type: browserAction.CLEAR_ALL_DIMESION_DATA,
    }
}

// Reducer
const pagesKey = 'pages';
const defaultPages = localDb.get(pagesKey) || []
export function pages(state = defaultPages, action) {
    switch (action.type) {
        case browserAction.GET_PAGES: {
            const data = localDb.get(pagesKey)
            return data;
        }
        case browserAction.NEW_PAGE: {
            const index = state.findIndex((item) => {
                return action.data.id === item.id
            })
            if (index < 0) {
                const newState = [...state, action.data]
                localDb.set(pagesKey, newState)
                return newState;
            }
            return state
        }
        /**
         * 更新pages里面的page
         */
        case browserAction.UPDATE_PAGE: {
            const newState = [...state]
            const newPage = action.data
            const index = state.findIndex((item) => {
                return newPage.id === item.id
            })
            if (index > -1) {
                newState[index] = Object.assign({}, state[index], newPage)
            }
            return newState
        }
        case browserAction.SET_INPUT_DATA: {
            const { taskId, source } = action.data;
            const newPageIndex = state.findIndex((page) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState = [...state];
                const {panelColumn} = source||{};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { source:panelColumn })
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.SET_OUTPUT_DATA: {
            const { taskId, sink } = action.data;
            const newPageIndex = state.findIndex((page) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState = [...state];
                const {panelColumn} = source||{};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { sink:panelColumn })
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.SET_DIMESION_DATA: {
            const { taskId, side } = action.data;
            const newPageIndex = state.findIndex((page) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState = [...state];
                const {panelColumn} = source||{};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { side:panelColumn })
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.CLOSE_PAGE: {
            const arr = [...state]
            if (action.data > -1) {
                arr.splice(action.data, 1)
                localDb.set(pagesKey, arr)
            }
            return arr;
        }
        case browserAction.CLEAR_PAGES: {
            localDb.set(pagesKey, []);
            return [];
        }
        case browserAction.CLOSE_OTHERS: {
            let arr = cloneDeep([...state]);
            const current_page = action.data;

            arr = arr.filter(
                (item) => {
                    return item.id == current_page.id;
                }
            )
            localDb.set(pagesKey, arr);
            return arr;
        }
        default:
            return state;
    }
}

const key = 'current_page'
const defaultCurPage = localDb.get(key) || {}
export function currentPage(state = defaultCurPage, action) {
    switch (action.type) {
        case browserAction.GET_CURRENT_PAGE: {
            const data = localDb.get(key)
            return data;
        }
        case browserAction.SET_CURRENT_PAGE: {
            const current = action.data || defaultCurPage
            localDb.set(key, current)
            return current
        }
        case browserAction.SET_INPUT_DATA: {
            const { source, taskId } = action.data || {};
            if (taskId == state.id) {
                const {panelColumn} = source||{};
                const newPage = Object.assign({}, state, { source:panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        case browserAction.SET_OUTPUT_DATA: {
            const { sink, taskId } = action.data || {};
            if (taskId == state.id) {
                const {panelColumn} = source||{};
                const newPage = Object.assign({}, state, { sink:panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        case browserAction.SET_DIMESION_DATA: {
            const { side, taskId } = action.data || {};
            if (taskId == state.id) {
                const {panelColumn} = source||{};
                const newPage = Object.assign({}, state, { side:panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        default:
            return state;
    }
}

export function inputData(state = {}, action) {
    switch (action.type) {
        case browserAction.SET_INPUT_DATA:
            const data = { ...state, ...{ [action.data.taskId]: action.data.source } }
            return data;
        case browserAction.GET_INPUT_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_INPUT_DATA:
            const newState = { ...state };
            console.log('inputData-CLEAR_CURRENT_INPUT_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_INPUT_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_INPUT_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_INPUT_DATA:
            return {};
        default:
            return state;
    }
}

export function outputData(state = {}, action) {
    switch (action.type) {
        case browserAction.SET_OUTPUT_DATA:
            const data = { ...state, ...{ [action.data.taskId]: action.data.sink } }
            return data;
        case browserAction.GET_OUTPUT_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_OUTPUT_DATA:
            const newState = { ...state };
            console.log('inputData-CLEAR_CURRENT_OUT_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_OUT_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_OUTPUT_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_OUTPUT_DATA:
            return {};
        default:
            return state;
    }
}

export function dimensionData(state = {}, action) {
    switch (action.type) {
        case browserAction.SET_DIMESION_DATA:
            const data = { ...state, ...{ [action.data.taskId]: action.data.side } }
            return data;
        case browserAction.GET_DIMESION_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_DIMESION_DATA:
            const newState = { ...state };
            console.log('inputData-CLEAR_CURRENT_DIMESION_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_DIMESION_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_DIMESION_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_DIMESION_DATA:
            return {};
        default:
            return state;
    }
}
