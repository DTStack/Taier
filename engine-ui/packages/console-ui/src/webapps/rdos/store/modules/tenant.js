import mc from 'mirror-creator';
import { combineReducers } from 'redux';

import Api from '../../api'

// 公共actionTypes
const actions = mc([
    'SET_TENANT_LIST'
], { prefix: 'comm/' })

// Actions
export function getTenantList () {
    return async dispatch => {
        let res = await Api.getTenantList();
        if (res && res.code == 1) {
            dispatch({
                type: actions.SET_TENANT_LIST,
                payload: res.data
            })
        }
    }
}

// 请状态
export function tenantList (state = [], action) {
    switch (action.type) {
        case actions.SET_TENANT_LIST:
            return action.payload;
        default:
            return state;
    }
}

export default combineReducers({
    tenantList
})
