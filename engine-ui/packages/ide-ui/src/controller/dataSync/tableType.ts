import mc from 'mirror-creator';
import Api from '../../api'

const tableTypeAction = mc([
    'GET_PROJECT_TABLE_TYPES', // 获取项目表类型
    'GET_TENANT_TABLE_TYPES' // 获取租户下的表类型
], { prefix: 'tableType/' })

const initialState: any = {
    teantTableTypes: [],
    projectTableTypes: []
}
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
export function getProjectTableTypes (projectId: any) {
    return async (dispatch: any) => {
        const res = await Api.getProjectTableTypes({
            projectId: projectId
        });
        if (res.code === 1) {
            return dispatch({
                type: tableTypeAction.GET_PROJECT_TABLE_TYPES,
                data: res.data || []
            })
        }
    }
}
export function getTenantTableTypes (params: any) {
    return async (dispatch: any) => {
        const res = await Api.getTenantTableTypes(params);
        if (res.code === 1) {
            return dispatch({
                type: tableTypeAction.GET_TENANT_TABLE_TYPES,
                data: res.data || []
            })
        }
    }
}
// reducer
export function tableTypes (state = initialState, action: any) {
    switch (action.type) {
        case tableTypeAction.GET_PROJECT_TABLE_TYPES: {
            return Object.assign({}, state, {
                projectTableTypes: action.data
            })
        }
        case tableTypeAction.GET_TENANT_TABLE_TYPES: {
            return Object.assign({}, state, {
                teantTableTypes: action.data
            })
        }
        default:
            return state
    }
}
