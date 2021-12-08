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

export const taskTreeAction = mc(
    [
        'RESET_TASK_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_TASK',
        'DEL_OFFLINE_FOLDER',
        'EDIT_FOLDER_CHILD',
        'EDIT_FOLDER_CHILD_FIELDS',
        'MERGE_FOLDER_CONTENT',
    ],
    { prefix: 'catalogue/taskTree/' }
);

export const componentTreeAction = mc(
    [
        'RESET_COMPONENT_TREE',
        'DELETE_COMPONENT',
        'LOAD_FOLDER_CONTENT',
        'EDIT_FOLDER_CHILD',
        'MERGE_FOLDER_CONTENT',
    ],
    { prefix: 'catalogue/componentTree/' }
);

export const resTreeAction = mc(
    [
        'RESET_RES_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_RES',
        'DEL_OFFLINE_FOLDER',
        'EDIT_FOLDER_CHILD',
    ],
    { prefix: 'catalogue/resTree/' }
);

export const functionTreeAction = mc(
    ['RESET_FUNCTION_TREE', 'LOAD_FOLDER_CONTENT'],
    {
        prefix: 'catalogue/functionTree/',
    }
);

export const sparkFnTreeAction = mc(['GET_SPARK_ROOT', 'LOAD_FOLDER_CONTENT'], {
    prefix: 'catalogue/sparkTree/',
});

export const greenPlumTreeAction = mc(
    ['GET_SPARK_ROOT', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/greenPlumTree/' }
);

export const libraFnTreeAction = mc(['GET_LIBRA_ROOT', 'LOAD_FOLDER_CONTENT'], {
    prefix: 'catalogue/libraTree/',
});

export const libraSysFnTreeActon = mc(
    ['RESET_SYSFUC_TREE', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/libraSysTree/' }
);

export const tiDBFnTreeAction = mc(['GET_TIDB_ROOT', 'LOAD_FOLDER_CONTENT'], {
    prefix: 'catalogue/tiDBTree/',
});

export const tiDBSysFnTreeActon = mc(
    ['RESET_SYSFUC_TREE', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/tiDBSysTree/' }
);

export const oracleFnTreeAction = mc(
    ['GET_ORACLE_ROOT', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/oracleTree/' }
);

export const oracleSysFnTreeActon = mc(
    ['RESET_SYSFUC_TREE', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/oracleSysTree/' }
);

export const greenPlumFnTreeAction = mc(
    ['GET_GREEN_PLUM_ROOT', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/greenPlumTree/' }
);

export const greenPlumSysFnTreeActon = mc(
    ['RESET_SYSFUC_TREE', 'LOAD_FOLDER_CONTENT'],
    { prefix: 'catalogue/greenPlumSysTree/' }
);

export const greenPlumFnTreeActon = mc(
    [
        'RESET_FUC_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_FOLDER',
        'DEL_OFFLINE_FN',
        'EDIT_FOLDER_CHILD',
    ],
    { prefix: 'catalogue/greenPlumFnTree/' }
);

export const greenPlumProdTreeActon = mc(
    [
        'RESET_FUC_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_FOLDER',
        'DEL_OFFLINE_FN',
        'EDIT_FOLDER_CHILD',
    ],
    { prefix: 'catalogue/greenPlumProdTree/' }
);

export const sparkCustomFnTreeAction = mc(
    [
        'RESET_FUC_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_FOLDER',
        'DEL_OFFLINE_FN',
        'EDIT_FOLDER_CHILD',
    ],
    { prefix: 'catalogue/sparkCustomFnTree/' }
);

export const sparkSysFnTreeActon = mc(
    [
        'RESET_SYSFUC_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_OFFLINE_FOLDER',
    ],
    { prefix: 'catalogue/sparkSysFnTree/' }
);

export const scriptTreeAction = mc(
    [
        'RESET_SCRIPT_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_SCRIPT',
        'DEL_OFFLINE_FOLDER',
        'EDIT_FOLDER_CHILD',
        'EDIT_FOLDER_CHILD_FIELDS',
        'MERGE_FOLDER_CONTENT',
    ],
    { prefix: 'catalogue/scriptTree/' }
);

export const tableTreeAction = mc(
    [
        'RESET_TABLE_TREE',
        'LOAD_FOLDER_CONTENT',
        'ADD_FOLDER_CHILD',
        'DEL_TABLE',
        'DEL_OFFLINE_FOLDER',
        'EDIT_FOLDER_CHILD',
    ],
    { prefix: 'catalogue/tableTree/' }
);
