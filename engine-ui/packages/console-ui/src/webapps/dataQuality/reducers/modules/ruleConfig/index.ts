import { ruleConfigActionType } from '../../../consts/ruleConfigActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    loading: false,
    monitorList: [],
    monitorFunction: [],
    tableColumn: [],
    monitorRules: [],
    monitorDetail: {},
    triggerList: []
};

export default function ruleConfig (state = initialState, action: any) {
    const { type, payload } = action;

    switch (type) {
        case ruleConfigActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case ruleConfigActionType.GET_MONITOR_LIST: {
            const clone = cloneDeep(state);
            // const { monitorList } = clone;
            clone.monitorList = payload;
            return clone;
        }

        case ruleConfigActionType.GET_MONITOR_TABLE_COLUMN: {
            const clone = cloneDeep(state);
            // const { tableColumn } = clone;
            clone.tableColumn = payload;
            return clone;
        }

        case ruleConfigActionType.GET_RULE_FUNCTION: {
            const clone = cloneDeep(state);
            // const { monitorFunction } = clone;
            clone.monitorFunction = payload;
            return clone;
        }

        case ruleConfigActionType.GET_MONITOR_RULE: {
            const clone = cloneDeep(state);
            // const { monitorRules } = clone;
            clone.monitorRules = payload;
            return clone;
        }

        case ruleConfigActionType.GET_MONITOR_DETAIL: {
            const clone = cloneDeep(state);
            // const { monitorDetail } = clone;
            clone.monitorDetail = payload;
            return clone;
        }

        case ruleConfigActionType.GET_REMOTE_TRIGGER: {
            const clone = cloneDeep(state);
            // const { triggerList } = clone;
            clone.triggerList = payload;
            return clone;
        }

        default:
            return state;
    }
}
