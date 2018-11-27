import { assign } from 'lodash';
import defaultApps from 'config/defaultApps';
import appActions from 'main/consts/appActions';

export function apps (state = defaultApps, action) {
    switch (action.type) {
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
