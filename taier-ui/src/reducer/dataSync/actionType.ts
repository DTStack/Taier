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

export const commAction = mc(
    [
        'GET_TASK_TYPES',
        'GET_TASK_TYPE_FILTER',
        'SET_TABLE_LIST',
        'GET_SCRIPT_TYPES',
        'GET_COMPONENT_TYPES',
        'SET_PROJECT_TABLE_LIST',
    ],
    { prefix: 'offline/comm/' }
);

export const modalAction = mc(
    [
        'SET_ENGINE_TYPE',
        'TOGGLE_CREATE_TASK',
        'TOGGLE_EDIT_TASK',
        'TOGGLE_CLONE_TASK',
        'TOGGLE_CLONE_TO_WORKFLOW',
        'TOGGLE_UPLOAD',
        'TOGGLE_CREATE_FOLDER',
        'TOGGLE_CREATE_FN',
        'TOGGLE_CREATE_PROD',
        'TOGGLE_MOVE_FN',
        'TOGGLE_MOVE_PROD',
        'SET_MODAL_DEFAULT',
        'EMPTY_MODAL_DEFAULT',
        'SET_MODAL_KEY',
        'SHOW_FNVIEW_MODAL',
        'HIDE_FNVIEW_MODAL',
        'SHOW_RESVIEW_MODAL',
        'HIDE_RESVIEW_MODAL',
        'TOGGLE_CREATE_SCRIPT',
        'TOGGLE_CREATE_COMPONENT',
        'TOGGLE_CHANGE_PROD',
        'SET_MODAL_CREATE_ORIGIN',
        'GET_WORKFLOW_LIST',
        'TOGGLE_CHANGE_FUNC',
        'TOGGLE_SAVE_MODAL',
        'TOGGLE_PUBLISH_MODAL',
        'IS_SAVE_FINISH',
    ],
    { prefix: 'offline/modal/' }
);

export const workflowAction = mc(['CLONE', 'UPDATE', 'RESET'], {
    prefix: 'offline/workflow/',
});

export const workbenchAction = mc(
    [
        'LOAD_TASK_DETAIL',
        'OPEN_TASK_TAB',
        'CLOSE_TASK_TAB',
        'UPDATE_TASK_TAB',
        'CLOSE_ALL_TABS',
        'CLOSE_OTHER_TABS',
        'CHANGE_SCHEDULE_CONF',
        'CHANGE_SCHEDULE_STATUS',
        'CHANGE_TASK_SUBMITSTATUS',
        'ADD_VOS',
        'DEL_VOS',
        'SET_TASK_FIELDS_VALUE',
        'SET_TASK_FIELDS_VALUE_SILENT',
        'SET_CURRENT_TAB_NEW',
        'SET_CURRENT_TAB_SAVED',
        'MAKE_TAB_DIRTY',
        'MAKE_TAB_CLEAN',
        'LOAD_TASK_CUSTOM_PARAMS',
        'SAVE_DATASYNC_TO_TAB',
        'INIT_WORKBENCH',
    ],
    { prefix: 'offline/workbench/' }
);

export const dataSourceListAction = mc(
    ['LOAD_DATASOURCE', 'RESET_DATASOURCE'],
    { prefix: 'offline/dataSync/dataSourceList/' }
);

export const sourceMapAction = mc(
    [
        'DATA_SOURCEMAP_UPDATE',
        'DATA_SOURCE_CHANGE',
        'DATA_SOURCEMAP_CHANGE',
        'SOURCE_TABLE_COLUMN_CHANGE',
        'SOURCE_TABLE_COPATE_CHANGE',
        'ADD_SOURCE_KEYROW',
        'REMOVE_SOURCE_KEYROW',
        'ADD_BATCH_SOURCE_KEYROW',
        'REPLACE_BATCH_SOURCE_KEYROW',
        'DATA_SOURCE_ADD',
        'DATA_SOURCE_DELETE',
        'EDIT_SOURCE_KEYROW',
    ],
    { prefix: 'offline/dataSync/sourceMap/' }
);

export const targetMapAction = mc(
    [
        'DATA_SOURCE_TARGET_CHANGE',
        'DATA_TARGETMAP_CHANGE',
        'TARGET_TABLE_COLUMN_CHANGE',
        'ADD_TARGET_KEYROW',
        'ADD_BATCH_TARGET_KEYROW',
        'REPLACE_BATCH_TARGET_KEYROW',
        'EDIT_TARGET_KEYROW',
        'REMOVE_TARGET_KEYROW',
        'COPY_TARGET_ROWS_TO_SOURCE',
        'CHANGE_NATIVE_HIVE',
    ],
    { prefix: 'offline/dataSync/targetMap/' }
);

export const keyMapAction = mc(
    [
        'ADD_LINKED_KEYS',
        'DEL_LINKED_KEYS',
        'SET_ROW_MAP',
        'SET_NAME_MAP',
        'RESET_LINKED_KEYS',
        'EDIT_KEYMAP_TARGET',
        'EDIT_KEYMAP_SOURCE',
        'REMOVE_KEYMAP',
    ],
    { prefix: 'offline/dataSync/keyMap/' }
);

export const settingAction = mc(
    ['CHANGE_CHANNEL_SETTING', 'CHANGE_CHANNEL_FIELDS', 'INIT_CHANNEL_SETTING'],
    { prefix: 'offline/dataSync/setting/' }
);

export const dataSyncAction = mc(
    [
        'INIT_JOBDATA',
        'RESET_KEYMAP',
        'SET_CURRENT_STEP',
        'INIT_CURRENT_STEP',
        'RESET_SOURCE_MAP',
        'RESET_TARGET_MAP',
        'SET_TABID',
        'RESET_TABID',
        'GET_DATASYNC_SAVED',
    ],
    { prefix: 'offline/dataSync/' }
);
