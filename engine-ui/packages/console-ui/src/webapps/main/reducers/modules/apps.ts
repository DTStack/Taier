import { assign, cloneDeep } from 'lodash';
import defaultApps from 'config/defaultApps';
import appActions from 'main/consts/appActions';
export function apps (state = defaultApps, action) {
    switch (action.type) {
        default:
            return state
    }
}
export function licenseApps (state = [], action) {
    switch (action.type) {
        case appActions.GET_LICENSE_APP: {
            if (action.data != null) {
                const nextState = cloneDeep(action.data);
                nextState.splice(0, 1);
                return nextState;
            }
            return state;
        }
        default:
            return state
    }
}

export function app (state = {}, action) {
    switch (action.type) {
        case appActions.UPDATE_APP: {
            if (action.data !== null) {
                return assign({}, state, action.data)
            }

            return state;
        }
        default:
            return state
    }
}

export function isLicenseLoaded (state = false, action) {
    switch (action.type) {
        case appActions.SET_LICENSE_LOADED: {
            return true;
        }
        default:
            return state
    }
}
