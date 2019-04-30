
import { createLog } from 'widgets/code-editor/utils'

import editorAction from '../consts/editorActionType';

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
