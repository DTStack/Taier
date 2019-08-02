import { approvalActionType } from '../../../consts/approvalActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {

    approvalList: []

}

export default function apiMarket (state = initialState, action: any) {
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
