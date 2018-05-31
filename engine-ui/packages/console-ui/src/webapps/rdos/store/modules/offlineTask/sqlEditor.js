import { combineReducers } from 'redux';
import { cloneDeep } from 'lodash';
import moment from "moment";
import { editorAction } from './actionType'

// Actions
export function output(tab, log) {
    return {
        type: editorAction.APPEND_CONSOLE_LOG,
        data: `【${moment().format("HH:mm:ss")}】 ${log}`,
        key: tab,
    }
}

export function setOutput(tab, log) {
    return {
        type: editorAction.SET_CONSOLE_LOG,
        data: `【${moment().format("HH:mm:ss")}】 ${log}`,
        key: tab,
    }
}

export function outputRes(tab, item, jobId) {
    return {
        type: editorAction.UPDATE_RESULTS,
        data: {jobId:jobId,data:item},
        key: tab,
    }
}

export function removeRes(tab, index) {
    return {
        type: editorAction.DELETE_RESULT,
        data: index,
        key: tab,
    }
}


export function resetConsole(tab) {
    return {
        type: editorAction.RESET_CONSOLE,
        key: tab,
    }
}


export function getTab(key) {
    return {
        type: editorAction.GET_TAB,
        key
    }
}


export function setSelectionContent(data) {
    return {
        type: editorAction.SET_SELECTION_CONTENT,
        data
    }
}


// Console Reducers 
const console = (state = {}, action) => {
    switch (action.type) {
    case editorAction.GET_TAB: {// 获取Tab
        const origin = cloneDeep(state)
        if (action.key) {
            const tab = origin[action.key]
            if (!tab) {
                origin[action.key] = { log: '', results: [] }
            }
        }
        return origin
    }
    case editorAction.RESET_CONSOLE: { // reset console
        const origin = cloneDeep(state)
        origin[action.key] = { log: '', results: [] }
        return origin
    }
    case editorAction.SET_TAB: { // 设置Tab
        const obj = cloneDeep(state)
        const map = action.data
        if (map) {
            obj[map.key] = map.data
        }
        return obj;
    }
    case editorAction.APPEND_CONSOLE_LOG: {// 追加日志
        const { key, data } = action
        const newLog = cloneDeep(state)
        newLog[key].log = newLog[key] ? `${newLog[key].log} \n${data}` : `${data}`
        return newLog
    }
    case editorAction.SET_CONSOLE_LOG: {
        const { key, data } = action
        const newLog = cloneDeep(state)
        newLog[key].log = action.data
        newLog[key].showRes = false
        return newLog;
    }
    case editorAction.UPDATE_RESULTS: {// 更新结果
        const updatedKey = action.key
        const jobId = action.jobId;
        let updated = cloneDeep(state);
        const update_arr = [...updated[updatedKey].results]
        if (updated[updatedKey] && action.data) {
            update_arr.push(action.data)
            updated[updatedKey].results = update_arr
            updated[updatedKey].showRes = true
        } else {
            updated[updatedKey].showRes = false
        }
        
        return updated;
    }
    case editorAction.DELETE_RESULT: {// 删除结果
        const key = action.key
        const index = action.data
        const origin = cloneDeep(state)
        const arr = origin[key].results
        if (arr.length > 0 && index !== undefined) {
            arr.splice(index, 1);
            origin[key].results = arr;
        }
        return origin;
    }
    default:
        return state;
    }
}

// 剪贴板
export const selection = (state = '', action) => {
    switch(action.type) {
    case editorAction.SET_SELECTION_CONTENT: {
        return action.data
    }
    default:
        return state
    }
}

/**running**/
export function addLoadingTab(id) {
    return {
        type: editorAction.ADD_LOADING_TAB,
        data:{
            id:id
        }
    }
}
export function removeLoadingTab(id) {
    return {
        type: editorAction.REMOVE_LOADING_TAB,
        data:{
            id:id
        }
    }
}
export function removeAllLoadingTab() {
    return {
        type: editorAction.REMOVE_ALL_LOAING_TAB
    }
}
//运行中的任务
export const running = (state = [], action) => {
    switch(action.type) {
        case editorAction.ADD_LOADING_TAB: {
            const list=cloneDeep(state);
            list.push(action.data.id);
            return list
        }
        case editorAction.REMOVE_LOADING_TAB: {
            
            let list=state.filter(function(value){
                return value!=action.data.id
            })
            return list
        }
        case editorAction.REMOVE_ALL_LOAING_TAB: {
            return []
        }
    default:
        return state
    }
}
/**running**/

export const sqlEditor = combineReducers({
    console,
    selection,
    running
})
