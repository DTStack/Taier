import { cloneDeep } from 'lodash';

import commonActionType from '../../consts/commonActionType';
import { siderBarType } from '../../consts/index'

const initialState = {
    userList: [],
    allDict: {},
    siderBarKey: siderBarType.notebook
}

export default function comm (state = initialState, action) {
    const { type, payload } = action;
    switch (type) {
        case commonActionType.GET_USER_LIST: {
            const clone = cloneDeep(state);
            clone.userList = payload;
            return clone;
        }

        case commonActionType.GET_ALL_DICT: {
            const clone = cloneDeep(state);
            clone.allDict = payload;
            return clone;
        }
        case commonActionType.CHANGE_SIDERBAR_KEY: {
            return {
                ...state,
                siderBarKey: payload
            };
        }
        default:
            return state;
    }
}
