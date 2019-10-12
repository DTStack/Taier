import { apiManageActionType } from '../../../consts/apiManageActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    apiList: [],
    disAbleTip: window.localStorage.getItem('disAbleTip'),
    isClickCode: false,
    securityList: []
}

export default function apiManage (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case apiManageActionType.GET_ALL_API_LIST: {
            const clone = cloneDeep(state);
            clone.apiList = payload
            return clone;
        }
        case apiManageActionType.GET_SECURITY_LIST: {
            const clone = cloneDeep(state);
            clone.securityList = payload
            return clone;
        }
        case apiManageActionType.CHANGE_DISABLE_TIP: {
            const clone = cloneDeep(state);
            clone.disAbleTip = !clone.disAbleTip
            window.localStorage.setItem('disAbleTip', clone.disAbleTip);
            return clone;
        }
        case apiManageActionType.CHNAGE_CODE_CLICK: {
            const clone = cloneDeep(state);
            clone.isClickCode = payload
            return clone;
        }

        default:
            return state;
    }
}
