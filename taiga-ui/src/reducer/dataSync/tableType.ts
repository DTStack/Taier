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
import Api from '../../api';

const tableTypeAction = mc(
    [
        'GET_PROJECT_TABLE_TYPES', // 获取项目表类型
        'GET_TENANT_TABLE_TYPES', // 获取租户下的表类型
    ],
    { prefix: 'tableType/' }
);

const initialState: any = {
    teantTableTypes: [],
    projectTableTypes: [],
};
// 不做处理
// const exChangeData = (data = []) => {
//     let params: any = [];
//     data.forEach((obj: any, index: any) => {
//         Object.keys(obj).forEach((key: any) => {
//             params.push({
//                 name: data[index][key],
//                 value: key
//             })
//         })
//     })
//     return params
// }
// Actions
export function getProjectTableTypes(projectId: any) {
    return async (dispatch: any) => {
        const res = await Api.getProjectTableTypes({
            projectId: projectId,
        });
        if (res.code === 1) {
            return dispatch({
                type: tableTypeAction.GET_PROJECT_TABLE_TYPES,
                data: res.data || [],
            });
        }
    };
}
export function getTenantTableTypes(params: any) {
    return async (dispatch: any) => {
        const res = await Api.getTenantTableTypes(params);
        if (res.code === 1) {
            return dispatch({
                type: tableTypeAction.GET_TENANT_TABLE_TYPES,
                data: res.data || [],
            });
        }
    };
}
// reducer
export function tableTypes(state = initialState, action: any) {
    switch (action.type) {
        case tableTypeAction.GET_PROJECT_TABLE_TYPES: {
            return Object.assign({}, state, {
                projectTableTypes: action.data,
            });
        }
        case tableTypeAction.GET_TENANT_TABLE_TYPES: {
            return Object.assign({}, state, {
                teantTableTypes: action.data,
            });
        }
        default:
            return state;
    }
}
