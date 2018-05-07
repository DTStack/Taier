import { tagConfigActionType } from '../../../consts/tagConfigActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    ruleTagList: [],
    registeredTagList: []
}

export default function tagConfig(state = initialState, action) {
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
            const { ruleTagList } = clone;
            clone.ruleTagList = payload;
            return clone;
        }

        case tagConfigActionType.GET_REGISTERED_TAG_LIST: {
            const clone = cloneDeep(state);
            const { registeredTagList } = clone;
            clone.registeredTagList = payload;
            return clone;
        }


        default:
            return state;
    }
}