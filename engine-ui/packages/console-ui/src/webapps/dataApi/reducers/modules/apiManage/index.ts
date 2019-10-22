import { apiManageActionType } from '../../../consts/apiManageActionType';
import { cloneDeep } from 'lodash';

let disAbleTip = window.localStorage.getItem('disAbleTip');
const initialState: any = {
    apiList: [],
    disAbleTip: disAbleTip == 'true',
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
