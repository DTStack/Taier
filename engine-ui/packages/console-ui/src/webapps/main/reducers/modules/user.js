import { assign } from 'lodash';

import userActions from 'main/consts/userActions'

export function user(state = {id: 0, userName: '小威'}, action) {
    switch (action.type) {
    case userActions.GET_USER:
        return action.data
    case userActions.UPDATE_USER: {
        if (action.data !== null) {
            return assign(state, action.data)
        }
        return state;
    }
    default:
        return state
    }
}