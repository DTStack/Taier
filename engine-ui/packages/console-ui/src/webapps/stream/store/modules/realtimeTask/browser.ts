import { hashHistory } from 'react-router'
import { cloneDeep } from 'lodash';
import { message } from 'antd';
import localDb from 'utils/localDb'
import Api from '../../../api'
import { browserAction, commAction } from './actionTypes'
export function publishTask (params: any) {
    const succCallback = (res: any) => {
        if (res.code === 1) {
            return res
        }
    }
    return Api.publishRealtimeTask(params)
        .then(succCallback);
}

export function convertToScriptMode (task: any) {
    return (dispatch: any) => {
        const reqParams: any = {
            id: task.id,
            createModel: task.createModel,
            lockVersion: task.lockVersion,
            version: task.version,
            // preSave: task.preSave,
            readWriteLockVO: task.readWriteLockVO
        }
        const reloadCurrentPage = (id: any) => {
            Api.getTask({ id }).then((res: any) => {
                if (res.code === 1 && res.data) {
                    dispatch(setCurrentPage(res.data))
                }
            })
        }
        dispatch({ type: commAction.CLOSE_RIGHT_PANEL, payload: false })
        Api.convertToScriptMode(reqParams).then((res: any) => {
            if (res.code === 1) {
                message.success('转换成功！');
                dispatch({ type: commAction.CLOSE_RIGHT_PANEL, payload: true }) // 关闭右侧面板
                reloadCurrentPage(task.id);
            }
        });
    }
}
// Action
/* eslint-disable */
export function closePage (id: any, pages: any, currentPage: any) {
    return (dispatch: any) => {
        const index = pages.findIndex((item: any) => {
            return id === item.id
        })
        if (index > -1) {
            if (currentPage.id === id) {
                if (pages.length > 1) {
                    const newNode = pages[index + 1] || pages[index - 1]
                    dispatch(setCurrentPage(newNode))
                } else {
                    dispatch({
                        type: browserAction.SET_CURRENT_PAGE,
                        data: {}
                    })
                }
            }
            return dispatch({
                type: browserAction.CLOSE_PAGE,
                data: index,
            })
        }
    }
}
function getExistPage (id: any, pages: any) {
    return pages.find((page: any) => {
        return page.id == id
    })
}
/* eslint-enable */
let _singleTask: any;
export function openPage (params: any) {
    return (dispatch: any, getState: any) => {
        if (_singleTask) {
            return;
        }
        const { id } = params;
        const state = getState();
        const { realtimeTask } = state;
        const { pages = [] } = realtimeTask;
        const existPage = getExistPage(id, pages);
        if (existPage) {
            if (location.pathname !== '/realtime/task') {
                hashHistory.push('/realtime/task')
            }
            return dispatch(setCurrentPage(existPage))
        }

        _singleTask = Api.getTask(params).then((res: any) => {
            if (res.code === 1 && res.data) {
                dispatch(newPage(res.data));
                if (location.pathname !== '/realtime/task') {
                    hashHistory.push('/realtime/task')
                }
            }
        }).finally(() => {
            _singleTask = null;
        })
    }
}

export function newPage (page: any) {
    return (dispatch: any) => {
        dispatch({
            type: browserAction.NEW_PAGE,
            data: page
        })
        dispatch(setCurrentPage(page))
    }
}

export function findPage (page: any, pageArr: any) {
    return pageArr.findIndex((item: any) => { return item.id === page.id })
}

export function getPages () {
    return { type: browserAction.GET_PAGES }
}

export function getCurrentPage () {
    return { type: browserAction.GET_CURRENT_PAGE }
}

export function updateCurrentPage (data: any) {
    return { type: browserAction.UPDATE_CURRENT_PAGE, data }
}

export function setCurrentPage (value: any) {
    return (dispatch: any) => {
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

export function closeOtherPages (currentPage: any) {
    return {
        type: browserAction.CLOSE_OTHERS,
        data: currentPage
    }
}

export function clearPages () {
    return {
        type: browserAction.CLEAR_PAGES
    }
}

export function updatePage (page: any) {
    return {
        type: browserAction.UPDATE_PAGE,
        data: page
    }
}

export function setInputData (value: any) {
    return {
        type: browserAction.SET_INPUT_DATA,
        data: value
    }
}

export function getInputData (id?: any) {
    return {
        type: browserAction.GET_INPUT_DATA,
        data: id
    }
}

export function closeCurrentInputData (id: any) {
    return {
        type: browserAction.CLEAR_CURRENT_INPUT_DATA,
        data: id
    }
}

export function closeOtherInputData (id: any) {
    return {
        type: browserAction.CLEAR_OTHER_INPUT_DATA,
        data: id
    }
}

export function closeAllInputData () {
    return {
        type: browserAction.CLEAR_ALL_INPUT_DATA
    }
}

export function setOutputData (value: any) {
    return {
        type: browserAction.SET_OUTPUT_DATA,
        data: value
    }
}

export function getOutputData (id?: any) {
    return {
        type: browserAction.GET_OUTPUT_DATA,
        data: id
    }
}

export function closeCurrentOutputData (id: any) {
    return {
        type: browserAction.CLEAR_CURRENT_OUTPUT_DATA,
        data: id
    }
}

export function closeOtherOutputData (id: any) {
    return {
        type: browserAction.CLEAR_OTHER_OUTPUT_DATA,
        data: id
    }
}

export function closeAllOutputData () {
    return {
        type: browserAction.CLEAR_ALL_OUTPUT_DATA
    }
}

export function setDimensionData (value: any) {
    return {
        type: browserAction.SET_DIMESION_DATA,
        data: value
    }
}

export function getDimensionData (id?: any) {
    return {
        type: browserAction.GET_DIMESION_DATA,
        data: id
    }
}

export function closeCurrentDimensionData (id: any) {
    return {
        type: browserAction.CLEAR_CURRENT_DIMESION_DATA,
        data: id
    }
}

export function closeOtherDimensionData (id: any) {
    return {
        type: browserAction.CLEAR_OTHER_DIMESION_DATA,
        data: id
    }
}

export function closeAllDimensionData () {
    return {
        type: browserAction.CLEAR_ALL_DIMESION_DATA
    }
}

// Reducer
const pagesKey = 'pages';
const defaultPages = localDb.get(pagesKey) || []
export function pages (state = defaultPages, action: any) {
    switch (action.type) {
        case browserAction.GET_PAGES: {
            const data = localDb.get(pagesKey)
            return data;
        }
        case browserAction.NEW_PAGE: {
            const index = state.findIndex((item: any) => {
                return action.data.id === item.id
            })
            if (index < 0) {
                const newState: any = [...state, action.data]
                localDb.set(pagesKey, newState)
                return newState;
            }
            return state
        }
        /**
         * 更新pages里面的page
         */
        case browserAction.UPDATE_PAGE: {
            const newState: any = [...state]
            const newPage = action.data
            const index = state.findIndex((item: any) => {
                return newPage.id === item.id
            })
            if (index > -1) {
                newState[index] = Object.assign({}, state[index], newPage)
            }
            localDb.set(pagesKey, newState)
            return newState
        }
        case browserAction.SET_INPUT_DATA: {
            const { taskId, source } = action.data;
            const newPageIndex = state.findIndex((page: any) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState: any = [...state];
                const { panelColumn }: any = source || {};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { source: panelColumn })
                localDb.set(pagesKey, newState)
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.SET_OUTPUT_DATA: {
            const { taskId, sink } = action.data;
            const newPageIndex = state.findIndex((page: any) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState: any = [...state];
                const { panelColumn }: any = sink || {};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { sink: panelColumn })
                localDb.set(pagesKey, newState)
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.SET_DIMESION_DATA: {
            const { taskId, side } = action.data;
            const newPageIndex = state.findIndex((page: any) => {
                return page.id == taskId
            })
            if (newPage && newPageIndex > -1) {
                const newState: any = [...state];
                const { panelColumn }: any = side || {};
                newState[newPageIndex] = Object.assign({}, state[newPageIndex], { side: panelColumn })
                localDb.set(pagesKey, newState)
                return newState;
            } else {
                return state;
            }
        }
        case browserAction.CLOSE_PAGE: {
            const arr: any = [...state]
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
            const currentPage = action.data;

            arr = arr.filter(
                (item: any) => {
                    return item.id == currentPage.id;
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
export function currentPage (state = defaultCurPage, action: any) {
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
        case browserAction.UPDATE_CURRENT_PAGE: {
            let current = state;
            if (action.data) {
                current = Object.assign({}, state, action.data);
                localDb.set(key, current)
            }
            return current
        }
        case browserAction.SET_INPUT_DATA: {
            const { source, taskId }: any = action.data || {};
            if (taskId == state.id) {
                const { panelColumn }: any = source || {};
                const newPage = Object.assign({}, state, { source: panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        case browserAction.SET_OUTPUT_DATA: {
            const { sink, taskId }: any = action.data || {};
            if (taskId == state.id) {
                const { panelColumn }: any = sink || {};
                const newPage = Object.assign({}, state, { sink: panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        case browserAction.SET_DIMESION_DATA: {
            const { side, taskId }: any = action.data || {};
            if (taskId == state.id) {
                const { panelColumn }: any = side || {};
                const newPage = Object.assign({}, state, { side: panelColumn })
                localDb.set(key, newPage)
                return newPage;
            } else {
                return state;
            }
        }
        case browserAction.CLEAR_PAGES: {
            const newState: any = {};
            localDb.set(key, newState)
            return newState;
        }
        default:
            return state;
    }
}

export function inputData (state: any = {}, action: any) {
    switch (action.type) {
        case browserAction.SET_INPUT_DATA:
            const data: any = { ...state, ...{ [action.data.taskId]: action.data.source } }
            return data;
        case browserAction.GET_INPUT_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_INPUT_DATA:
            const newState: any = { ...state };
            console.log('inputData-CLEAR_CURRENT_INPUT_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_INPUT_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_INPUT_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_INPUT_DATA:
            return {};
        case browserAction.CLEAR_PAGES: {
            return {};
        }
        default:
            return state;
    }
}

export function outputData (state: any = {}, action: any) {
    switch (action.type) {
        case browserAction.SET_OUTPUT_DATA:
            const data: any = { ...state, ...{ [action.data.taskId]: action.data.sink } }
            return data;
        case browserAction.GET_OUTPUT_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_OUTPUT_DATA:
            const newState: any = { ...state };
            console.log('inputData-CLEAR_CURRENT_OUT_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_OUT_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_OUTPUT_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_OUTPUT_DATA:
            return {};
        case browserAction.CLEAR_PAGES: {
            return {};
        }
        default:
            return state;
    }
}

export function dimensionData (state: any = {}, action: any) {
    switch (action.type) {
        case browserAction.SET_DIMESION_DATA:
            const data: any = { ...state, ...{ [action.data.taskId]: action.data.side } }
            return data;
        case browserAction.GET_DIMESION_DATA:
            return state;
        case browserAction.CLEAR_CURRENT_DIMESION_DATA:
            const newState: any = { ...state };
            console.log('inputData-CLEAR_CURRENT_DIMESION_DATA', newState);
            delete newState[action.data]
            console.log('inputData-CLEAR_CURRENT_DIMESION_DATA', newState);
            return newState;
        case browserAction.CLEAR_OTHER_DIMESION_DATA:
            const taskId = action.data;
            return { [taskId]: state[taskId] };
        case browserAction.CLEAR_ALL_DIMESION_DATA:
            return {};
        case browserAction.CLEAR_PAGES: {
            return {};
        }
        default:
            return state;
    }
}
