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
import { DEFAULT_COMP_TEST, DEFAULT_COMP_REQUIRED } from '../../consts/index'
const clusterActions = mc([
    'UPDATE_TEST_RESULT',
    'UPDATE_REQUIRED_STATUS'
])

// actions
export const updateTestStatus = (data: any) => {
    return {
        type: clusterActions.UPDATE_TEST_RESULT,
        data: data
    }
}
export const updateRequiredStatus = (data: any) => {
    return {
        type: clusterActions.UPDATE_REQUIRED_STATUS,
        data: data
    }
}

// reducer
export function testStatus (state = DEFAULT_COMP_TEST, action: any) {
    switch (action.type) {
        case clusterActions.UPDATE_TEST_RESULT: {
            const data = action.data;
            return Object.assign({}, state, data)
        }
        default:
            return state
    }
}

export function showRequireStatus (state = DEFAULT_COMP_REQUIRED, action: any) {
    switch (action.type) {
        case clusterActions.UPDATE_REQUIRED_STATUS: {
            const data = action.data;
            return Object.assign({}, state, data)
        }
        default:
            return state
    }
}
