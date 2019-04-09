import { combineReducers } from 'redux';
import { cloneDeep, assign } from 'lodash';
import localDb from 'utils/localDb';

import editorAction from '../../../consts/editorActionType';
import { experimentTabType, notebookTabType } from '../../../consts/actionType/tabType';
import { siderBarType } from '../../../consts/index'
const KEY_EDITOR_OPTIONS = 'editor_options';

const { experiment, notebook } = siderBarType;

// Console Reducers
const console = (state = { [experiment]: {}, [notebook]: {} }, action) => {
    const { type, payload = {} } = action;
    const { siderType } = payload;
    switch (type) {
        case experimentTabType.ADD_TAB: {
            const oldData = cloneDeep(state[experiment]);
            oldData[payload.tabId] = { data: [] };
            return {
                ...state,
                [experiment]: oldData
            }
        }
        case notebookTabType.ADD_TAB: {
            const oldData = cloneDeep(state[notebook]);
            oldData[payload.tabId] = { data: [] };
            return {
                ...state,
                [notebook]: oldData
            }
        }
        case editorAction.RESET_CONSOLE: {
            // reset console
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            origin[payload.tabId] = { data: [] };
            return newState;
        }
        case editorAction.SET_TAB: {
            // 设置Tab
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            if (payload) {
                origin[payload.key] = payload.data;
            }
            return newState;
        }
        case editorAction.APPEND_CONSOLE_LOG: {
            // 追加日志
            const { key, tabId, data } = payload;
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            const items = origin[tabId].data;
            for (let i = 0; i < items.length; i++) {
                const item = items[i];
                if (item.id == key) {
                    items[i].log = items[i].log
                        ? `${items[i].log} \n${data}`
                        : `${data}`;
                }
            }
            return newState;
        }
        case editorAction.SET_CONSOLE_LOG: {
            const { key, tabId, data } = payload;
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            const items = origin[tabId].data;
            for (let i = 0; i < items.length; i++) {
                const item = items[i];
                if (item.id == key) {
                    items[i].log = data;
                }
            }
            return newState;
        }
        case editorAction.UPDATE_RESULTS: {
            // 更新结果
            const { key, tabId, data } = payload;
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            const items = origin[tabId].data;
            for (let i = 0; i < items.length; i++) {
                const item = items[i];
                if (item.id == key) {
                    items[i].data = items[i].data || [];
                    items[i].data.push(data);
                }
            }
            return newState;
        }
        case editorAction.DELETE_RESULT: {
            // 删除结果
            const { key, tabId } = payload;
            const newState = cloneDeep(state);
            const origin = newState[siderType];
            const items = origin[tabId].data;
            for (let i = 0; i < items.length; i++) {
                const item = items[i];
                if (item.id == key) {
                    items.splice(i, 1);
                }
            }
            return newState;
        }
        default:
            return state;
    }
};

// 剪贴板
export const selection = (state = '', action) => {
    switch (action.type) {
        case editorAction.SET_SELECTION_CONTENT: {
            if (action.data) {
                return action.data;
            } else if (state !== '') {
                return '';
            }
            return '';
        }
        default:
            return state;
    }
};

/** running**/
// 运行中的任务
export const running = (state = [], action) => {
    let result = [];
    switch (action.type) {
        case editorAction.ADD_LOADING_TAB: {
            const list = cloneDeep(state);
            list.push(action.data.id);
            result = list;
            break;
        }
        case editorAction.REMOVE_LOADING_TAB: {
            let list = state.filter(function (value) {
                return value != action.data.id;
            });
            result = list;
            break;
        }
        case editorAction.REMOVE_ALL_LOAING_TAB: {
            result = [];
            break;
        }
        default:
            return state;
    }
    return result;
};
/** running**/

/**
 * 编辑器选项
 */
const initialEditorOptions = function () {
    return { theme: 'vs' };
};

export const options = (state = initialEditorOptions(), action) => {
    switch (action.type) {
        case editorAction.UPDATE_OPTIONS: {
            const nextOptions = assign({}, state, action.data);
            localDb.set(KEY_EDITOR_OPTIONS, nextOptions);
            return nextOptions;
        }
        default:
            return state;
    }
};

export const editor = combineReducers({
    console,
    selection,
    running,
    options
});
