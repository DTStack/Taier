/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { combineReducers } from 'redux';
import { cloneDeep, assign } from 'lodash';
import { editorAction } from './actionTypes';

export const KEY_EDITOR_OPTIONS = 'editor_options';

// Console Reducers
export const console = (state: any = {}, action: any) => {
    switch (action.type) {
        case editorAction.GET_TAB: {
            // 初始化console
            const origin = cloneDeep(state);
            if (action.key || action.key === 0) {
                const tab = origin[action.key];
                if (!tab) {
                    origin[action.key] = { log: '', results: [] };
                }
            }
            return origin;
        }
        case editorAction.RESET_CONSOLE: {
            // reset console
            const origin = cloneDeep(state);
            if (action.key || action.key === 0) {
                origin[action.key] = { log: '', results: [] };
            }
            return origin;
        }
        case editorAction.SET_TAB: {
            // 设置Tab
            const obj = cloneDeep(state);
            const map = action.data;
            if (map && (map.key || map.key === 0)) {
                obj[map.key] = map.data;
            }
            return obj;
        }
        case editorAction.APPEND_CONSOLE_LOG: {
            // 追加日志
            const { key, data } = action;
            const newLog = cloneDeep(state);
            newLog[key].log = newLog[key]
                ? `${newLog[key].log} \n${data}`
                : `${data}`;
            return newLog;
        }
        case editorAction.SET_CONSOLE_LOG: {
            const { key } = action;
            const newLog = cloneDeep(state);
            newLog[key].log = action.data;
            newLog[key].showRes = false;
            return newLog;
        }
        case editorAction.UPDATE_RESULTS: {
            // 更新结果
            const updatedKey = action.key;
            const updated = cloneDeep(state);
            const updateArr: any = [...updated[updatedKey].results];
            if (updated[updatedKey] && action.data) {
                const lastResult = updateArr[updateArr.length - 1];
                let index = 1;
                // 根据最后一个结果的id序号来递增序号
                if (lastResult) {
                    index = lastResult.id
                        ? lastResult.id + 1
                        : updateArr.length + 1;
                }
                updateArr.push({ ...action.data, id: index });
                updated[updatedKey].results = updateArr;
                updated[updatedKey].showRes = true;
            } else {
                updated[updatedKey].showRes = false;
            }

            return updated;
        }
        case editorAction.DELETE_RESULT: {
            // 删除结果
            const key = action.key;
            const index = action.data;
            const origin = cloneDeep(state);
            const arr = origin[key].results;
            if (arr.length > 0 && index !== undefined) {
                arr.splice(index, 1);
                origin[key].results = arr;
            }
            return origin;
        }
        default:
            return state;
    }
};

// 剪贴板
export const selection = (state = '', action: any) => {
    switch (action.type) {
        case editorAction.SET_SELECTION_CONTENT: {
            if (action.data) {
                return action.data;
            }
            return '';
        }
        default:
            return state;
    }
};

/** running**/
// 运行中的任务
export const running = (state: any = [], action: any) => {
    switch (action.type) {
        case editorAction.ADD_LOADING_TAB: {
            const list = cloneDeep(state);
            list.push(action.data.id);
            return list;
        }
        case editorAction.REMOVE_LOADING_TAB: {
            const list = state.filter(function (value: any) {
                return value != action.data.id;
            });
            return list;
        }
        case editorAction.REMOVE_ALL_LOAING_TAB: {
            return [];
        }
        default:
            return state;
    }
};
/** running**/

/**
 * 编辑器选项
 */
const initialEditorOptions = function () {
    return { theme: 'vs' };
};

export const options = (state = initialEditorOptions(), action: any) => {
    switch (action.type) {
        case editorAction.UPDATE_OPTIONS: {
            const nextOptions = assign({}, state, action.data);
            return nextOptions;
        }
        default:
            return state;
    }
};

/**
 * 是否展示右侧面板
 */
export const showRightExtraPane = (state = '', action: any) => {
    switch (action.type) {
        case editorAction.SHOW_RIGHT_PANE: {
            return action.data;
        }
        default:
            return state;
    }
};

/**
 * 语法面板数据
 * @param {*} state
 * @param {*} action
 */
const initialSyntaxPaneState = function () {
    return { selected: undefined, html: '' };
};
export const syntaxPane = (state = initialSyntaxPaneState(), action: any) => {
    const { type, data } = action;
    let nextState = state;
    switch (type) {
        case editorAction.UPDATE_SYNTAX_PANE: {
            nextState = Object.assign({}, state, data);
            break;
        }
    }
    return nextState;
};

export const editor = combineReducers({
    console,
    selection,
    running,
    options,
    showRightExtraPane,
    syntaxPane,
});
