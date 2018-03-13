import { ruleConfigActions as ACTION_TYPE } from '../../../consts/ruleConfigActions';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    ruleLists: [],
}

export default function ruleConfig(state = initialState, action) {
    const { type, payload } = action;

    switch (type) {  
        case ACTION_TYPE.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case ACTION_TYPE.GET_RULE_LIST: {
            const clone = cloneDeep(state);
            const { ruleLists } = clone;
            clone.ruleLists = payload;
            return clone;
        }

        default:
            return state;
    }
}