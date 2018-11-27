import { approvalActionType } from '../../../consts/approvalActionType';
import { cloneDeep } from 'lodash';

const initialState = {

    approvalList: []

}

export default function apiMarket (state = initialState, action) {
    const { type, payload } = action;
    switch (type) {
    case approvalActionType.GET_ALL_APPLY_LIST: {
        const clone = cloneDeep(state);
        clone.approvalList = payload
        return clone;
    }

    default:
        return state;
    }
}
