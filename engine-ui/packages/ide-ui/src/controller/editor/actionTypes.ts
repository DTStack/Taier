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

import mc from 'mirror-creator';

export const editorAction = mc(
    [
        'RESET_CONSOLE',
        'APPEND_CONSOLE_LOG',
        'SET_CONSOLE_LOG',
        'UPDATE_RESULTS',
        'DELETE_RESULT',
        'GET_TAB',
        'SET_TAB',
        'SET_SELECTION_CONTENT',
        'ADD_LOADING_TAB', // 添加指定的的tab loading
        'REMOVE_LOADING_TAB', // 移除指定的tab loading
        'REMOVE_ALL_LOAING_TAB', // 移除所有tab loading
        'UPDATE_OPTIONS', // 更新编辑器选项
        'SHOW_RIGHT_PANE', // 显示右侧面板
        'SHOW_TABLE_TIP_PANE', // 打开表提示面板
        'SHOW_SYNTAX_HELP_PANE', // 打开语法提示面板
        'UPDATE_SYNTAX_PANE', // 更新帮助面板状态
    ],
    { prefix: 'editor/' }
);
