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
