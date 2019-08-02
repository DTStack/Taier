import commonActionType from '../../consts/commonActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    userList: [],
    allDict: {},
    menuList: null
}

export default function dataCheck (state = initialState, action: any) {
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
        case commonActionType.GET_ALL_MENU_LIST: {
            const clone = cloneDeep(state);
            clone.menuList = payload;
            return clone;
        }

        default:
            return state;
    }
}
