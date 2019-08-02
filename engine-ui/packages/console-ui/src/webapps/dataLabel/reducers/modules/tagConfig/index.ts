import { tagConfigActionType } from '../../../consts/tagConfigActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    loading: false,
    ruleTagList: [],
    registeredTagList: [],
    identifyColumn: []
}

export default function tagConfig (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case tagConfigActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case tagConfigActionType.GET_RULE_TAG_LIST: {
            const clone = cloneDeep(state);
            clone.ruleTagList = payload;
            return clone;
        }

        case tagConfigActionType.GET_REGISTERED_TAG_LIST: {
            const clone = cloneDeep(state);
            clone.registeredTagList = payload;
            return clone;
        }

        case tagConfigActionType.GET_ALL_IDENTIFY_COLUMN: {
            const clone = cloneDeep(state);
            clone.identifyColumn = payload;
            return clone;
        }

        default:
            return state;
    }
}
