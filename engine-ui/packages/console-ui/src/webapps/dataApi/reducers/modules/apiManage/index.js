import { apiManageActionType } from '../../../consts/apiManageActionType';
import { cloneDeep } from 'lodash';

const initialState = {
   
    apiList:[],
    disAbleTip:window.localStorage.getItem("disAbleTip")

}

export default function apiManage(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        case apiManageActionType.GET_ALL_API_LIST: {
            const clone = cloneDeep(state);
            clone.apiList=payload
            return clone;
        }
        case apiManageActionType.CHANGE_DISABLE_TIP:{
            const clone = cloneDeep(state);
            clone.disAbleTip=!clone.disAbleTip
            window.localStorage.setItem("disAbleTip",clone.disAbleTip);
            return clone;
        }


        default:
            return state;
    }
}