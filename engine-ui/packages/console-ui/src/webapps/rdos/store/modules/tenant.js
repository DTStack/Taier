import mc from 'mirror-creator';
import { combineReducers } from 'redux';

import Api from '../../api'

// 公共actionTypes
const actions = mc([
    'SET_TENANT_LIST',
    'SET_CURRENT_TENANT'
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
            const currentTenant = res.data.find((tenant) => {
                return tenant.current;
            });
            currentTenant && dispatch({
                type: actions.SET_CURRENT_TENANT,
                payload: currentTenant
            })
        }
    }
}

export function tenantList (state = [], action) {
    switch (action.type) {
        case actions.SET_TENANT_LIST:
            return action.payload;
        default:
            return state;
    }
}
export function currentTenant (state = {}, action) {
    switch (action.type) {
        case actions.SET_CURRENT_TENANT:
            return action.payload;
        default:
            return state;
    }
}

export default combineReducers({
    tenantList,
    currentTenant
})
