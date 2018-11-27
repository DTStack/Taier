import { assign } from 'lodash';

import userActions from 'main/consts/userActions'

const initalUser = {
    id: 0,
    dtuicUserId: 0,
    userName: '未登录',
    isRoot: false
}

export function user (state = initalUser, action) {
    switch (action.type) {
    case userActions.GET_USER:
        return action.data
    case userActions.UPDATE_USER: {
        if (action.data !== null) {
            return { ...state, ...action.data }
        }
        return state;
    }
    default:
        return state
    }
}
