import { apiManageActionType } from '../../../consts/apiManageActionType';
import { cloneDeep } from 'lodash';

const initialState = {

    apiList: []

}

export default function apiManage (state = initialState, action) {
    const { type, payload } = action;
    switch (type) {
    case apiManageActionType.GET_ALL_API_LIST: {
        const clone = cloneDeep(state);
        clone.apiList = payload
        return clone;
    }

    default:
        return state;
    }
}
