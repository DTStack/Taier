import { assign, cloneDeep } from 'lodash';
import userActions from '../../consts/userActions'

const initialState = {
    id: 0,
    userName: '测试',
    userList: [],
}

export function user(state = initialState, action) {
    const { type, data } = action;
    switch (type) {

        case userActions.GET_USER:
            return data

        case userActions.UPDATE_USER: {
            if (data !== null) {
                return assign(state, data)
            }
            return state;
        }

        case userActions.GET_USER_LIST: {
            const clone = cloneDeep(state);
            const { userList } = clone;
            clone.userList = data;
            return clone;
        }

        default:
            return state;

    }
}