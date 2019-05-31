import mc from 'mirror-creator';

import Api from '../../api'

const tableTypeAction = mc([
    'GET_TABLE_TYPES' // 获取表类型
], { prefix: 'tableType/' })

const initialState = [
    {
        name: 'Hadoop',
        value: 1
    },
    {
        name: 'LibrA',
        value: 2
    }
];

// Actions
export function getTableTypes (params) {
    return async (dispatch) => {
        const res = await Api.getTableTypes(params);
        if (res.code === 1) {
            return dispatch({
                type: tableTypeAction.GET_TABLE_TYPES,
                data: res.data
            })
        }
    }
}

export function tableTypes (state = initialState, action) {
    switch (action.type) {
        case tableTypeAction.GET_USER: {
            return action.data
        }
        default:
            return state
    }
}
