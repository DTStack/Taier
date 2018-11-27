import commonActionType from '../../consts/commonActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    userList: [],
    menuList: [],
    periodType: [],
    notifyType: []
}

export default function common (state = initialState, action) {
    const { type, payload } = action;
    switch (type) {
    case commonActionType.GET_USER_LIST: {
        const clone = cloneDeep(state);
        const { userList } = clone;
        clone.userList = payload;
        return clone;
    }

    case commonActionType.GET_ALL_MENU_LIST: {
        const clone = cloneDeep(state);
        const { menuList } = clone;
        clone.menuList = payload;
        return clone;
    }

    case commonActionType.GET_PERIOD_TYPE: {
        const clone = cloneDeep(state);
        const { periodType } = clone;
        clone.periodType = payload;
        return clone;
    }

    case commonActionType.GET_NOTIFY_TYPE: {
        const clone = cloneDeep(state);
        const { notifyType } = clone;
        clone.notifyType = payload;
        return clone;
    }

    default:
        return state;
    }
}
