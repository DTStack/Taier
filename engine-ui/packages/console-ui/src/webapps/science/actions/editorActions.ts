
import { createLog } from 'widgets/code-editor/utils'

import editorAction from '../consts/editorActionType';

// Actions
export function output (tabId: any, log: any, key: any, type: any) {
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

export function setOutput (tabId: any, log: any, key: any, type: any, extData: any) {
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

export function outputRes (tabId: any, data: any, key: any, type: any, extData: any) {
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

export function removeRes (tabId: any, key: any, type: any) {
    return {
        payload: {
            key,
            tabId,
            siderType: type
        },
        type: editorAction.DELETE_RESULT
    }
}

export function resetConsole (tabId: any, type: any) {
    return {
        payload: {
            siderType: type,
            tabId
        },
        type: editorAction.RESET_CONSOLE
    }
}

export function setSelectionContent (data: any) {
    return {
        type: editorAction.SET_SELECTION_CONTENT,
        data
    }
}

// Loading actions
export function addLoadingTab (id: any) {
    return {
        type: editorAction.ADD_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeLoadingTab (id: any) {
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

export function updateEditorOptions (data: any) {
    return {
        type: editorAction.UPDATE_OPTIONS,
        data
    }
}

export function getEditorThemeClassName (editorTheme: any) {
    // 如果是dark类的编辑器，则切换ide的theme为dark风格
    return editorTheme === 'vs-dark' || editorTheme === 'hc-black'
        ? 'theme-dark' : 'theme-white';
}
