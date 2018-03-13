import commonActionType from '../../consts/commonActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    userList: []
}

export default function dataCheck(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  

        case commonActionType.GET_USER_LIST: {
            const clone = cloneDeep(state);
            const { userList } = clone;
            clone.userList = payload;
            return clone;
        }

        default:
            return state;
    }
}