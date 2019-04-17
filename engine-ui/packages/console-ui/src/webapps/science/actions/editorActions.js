import {
    message
} from 'antd';

import { createLog } from 'widgets/code-editor/utils'

import API from '../api';
import editorAction from '../consts/editorActionType';

// const INTERVALS = 1500;

// 储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore = {}
// 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign = {}
// 正在运行中的sql key，调用stop接口的时候需要使用
const runningSql = {}
// 停止sql
export function stopSql (currentTab, currentTabData, isSilent) {
    return async (dispatch, getState) => {
        // 静默关闭，不通知任何人（服务器，用户）
        if (isSilent) {
            const running = getState().editor.running;
            if (running.indexOf(currentTab) > -1) {
                stopSign[currentTab] = true;
                dispatch(output(currentTab, createLog('执行停止', 'warning')))
                dispatch(removeLoadingTab(currentTab))
                if (intervalsStore[currentTab]) {
                    clearTimeout(intervalsStore[currentTab])
                    intervalsStore[currentTab] = null;
                }
                return;
            }
            return;
        }

        const jobId = runningSql[currentTab];
        if (!jobId) return;

        const res = await API.stopExecSQL({
            taskId: currentTabData.id,
            jobId: jobId
        });

        if (res.code === 1) {
            dispatch(output(currentTab, createLog('执行停止', 'warning')));
            // 消除轮询定时器
            if (intervalsStore[currentTab]) {
                clearTimeout(intervalsStore[currentTab])
                intervalsStore[currentTab] = null;
            }

            stopSign[currentTab] = true;
            dispatch(removeLoadingTab(currentTab));
            message.success('停止执行成功！');
        } else {
            message.success('停止执行失败！');
        }
    }
}

// Actions
export function output (tabId, log, key, type) {
    return {
        payload: {
            key,
            tabId,
            siderType: type,
            data: log
        },
        type: editorAction.APPEND_CONSOLE_LOG
    }
}

export function setOutput (tabId, log, key, type, extData) {
    return {
        payload: {
            key,
            tabId,
            extData,
            siderType: type,
            data: createLog(log, 'info')
        },
        type: editorAction.SET_CONSOLE_LOG
    }
}

export function outputRes (tabId, data, key, type, extData) {
    return {
        payload: {
            key,
            tabId,
            extData,
            siderType: type,
            data
        },
        type: editorAction.UPDATE_RESULTS
    }
}

export function removeRes (tabId, key, type) {
    return {
        payload: {
            key,
            tabId,
            siderType: type
        },
        type: editorAction.DELETE_RESULT
    }
}

export function resetConsole (tabId, type) {
    return {
        payload: {
            siderType: type,
            tabId
        },
        type: editorAction.RESET_CONSOLE
    }
}

export function setSelectionContent (data) {
    return {
        type: editorAction.SET_SELECTION_CONTENT,
        data
    }
}

// Loading actions
export function addLoadingTab (id) {
    return {
        type: editorAction.ADD_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeLoadingTab (id) {
    return {
        type: editorAction.REMOVE_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeAllLoadingTab () {
    return {
        type: editorAction.REMOVE_ALL_LOAING_TAB
    }
}

export function updateEditorOptions (data) {
    return {
        type: editorAction.UPDATE_OPTIONS,
        data
    }
}

export function getEditorThemeClassName (editorTheme) {
    // 如果是dark类的编辑器，则切换ide的theme为dark风格
    return editorTheme === 'vs-dark' || editorTheme === 'hc-black'
        ? 'theme-dark' : 'theme-white';
}
