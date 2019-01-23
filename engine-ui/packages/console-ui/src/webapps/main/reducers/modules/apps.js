import { assign } from 'lodash';
import defaultApps from 'config/defaultApps';
import appActions from 'main/consts/appActions';
export function apps (state = defaultApps, action) {
    switch (action.type) {
        default:
            return state
    }
}
export function licenseApps (state = [{}], action) {
    switch (action.type) {
        case appActions.GET_LICENSE_APP: {
            if (action.data != null) {
                action.data.splice(0, 1);
                // return assign({}, state, action.data)
                return action.data
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
