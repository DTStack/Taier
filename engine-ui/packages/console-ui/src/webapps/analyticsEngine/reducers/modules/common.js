import { cloneDeep } from 'lodash';

import commonActionType from '../../consts/commonActionType';

const initialState = {
    userList: [],
    allDict: {},
}

export default function comm(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  

        case commonActionType.GET_USER_LIST: {
            const clone = cloneDeep(state);
            const { userList } = clone;
            clone.userList = payload;
            return clone;
        }

        case commonActionType.GET_ALL_DICT: {
            const clone = cloneDeep(state);
            const { allDict } = clone;
            clone.allDict = payload;
            return clone;
        }

        default:
            return state;
    }
}