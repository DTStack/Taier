import { mineActionType } from '../../../consts/mineActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {

    apiList: {
        applyingList: [],
        appliedList: []
    }

}

export default function mine (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case mineActionType.GET_APPLYING_LIST: {
            const clone = cloneDeep(state);
            clone.apiList.applyingList = payload
            return clone;
        }
        case mineActionType.GET_APPLYED_LIST: {
            const clone = cloneDeep(state);
            clone.apiList.appliedList = payload
            return clone;
        }

        default:
            return state;
    }
}
