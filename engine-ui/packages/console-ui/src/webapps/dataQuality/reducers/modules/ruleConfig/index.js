import { ruleConfigActionType } from '../../../consts/ruleConfigActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    ruleLists: [],
    monitorFunction: []
}

export default function ruleConfig(state = initialState, action) {
    const { type, payload } = action;

    switch (type) {  
        case ruleConfigActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case ruleConfigActionType.GET_RULE_LIST: {
            const clone = cloneDeep(state);
            const { ruleLists } = clone;
            clone.ruleLists = payload;
            return clone;
        }

        case ruleConfigActionType.GET_RULE_FUNCTION: {
            const clone = cloneDeep(state);
            const { monitorFunction } = clone;
            clone.monitorFunction = payload;
            return clone;
        }

        default:
            return state;
    }
}