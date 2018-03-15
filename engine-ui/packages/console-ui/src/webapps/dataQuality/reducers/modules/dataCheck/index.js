import { dataCheckActionType } from '../../../consts/dataCheckActions';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    lists: [],
    originPart: [],
    targetPart: [],
}

export default function dataCheck(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        case dataCheckActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case dataCheckActionType.GET_LIST: {
            const clone = cloneDeep(state);
            const { lists } = clone;
            clone.lists = payload;
            return clone;
        }

        case dataCheckActionType.GET_SOURCE_PART: {
            const clone = cloneDeep(state);
            const { originPart, targetPart } = clone;
            if (payload.type === 'origin') {
                clone.originPart = payload.data.children;
            } else {
                clone.targetPart = payload.data.children;
            }

            return clone;
        }

        case dataCheckActionType.RESET_SOURCE_PART: {
            const clone = cloneDeep(state);
            const { originPart, targetPart } = clone;
            if (payload === 'origin') {
                clone.originPart = [];
            } else {
                clone.targetPart = [];
            }

            return clone;
        }

        default:
            return state;
    }
}