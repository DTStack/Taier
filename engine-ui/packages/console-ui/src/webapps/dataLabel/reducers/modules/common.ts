import commonActionType from '../../consts/commonActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    userList: [],
    menuList: [],
    periodType: [],
    notifyType: []
}

export default function common (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case commonActionType.GET_USER_LIST: {
            const clone = cloneDeep(state);
            clone.userList = payload;
            return clone;
        }

        case commonActionType.GET_ALL_MENU_LIST: {
            const clone = cloneDeep(state);
            clone.menuList = payload;
            return clone;
        }

        case commonActionType.GET_PERIOD_TYPE: {
            const clone = cloneDeep(state);
            clone.periodType = payload;
            return clone;
        }

        case commonActionType.GET_NOTIFY_TYPE: {
            const clone = cloneDeep(state);
            clone.notifyType = payload;
            return clone;
        }

        default:
            return state;
    }
}
