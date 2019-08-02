import { dashBoardActionType } from '../../../consts/dashBoardActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    loading: false,
    topRecords: [],
    alarmTrend: [],
    alarmSum: {},
    usage: {}
};

export default function dashBoard (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case dashBoardActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case dashBoardActionType.GET_TOP_RECORD: {
            const clone = cloneDeep(state);
            // const { topRecords } = clone;
            clone.topRecords = payload;
            return clone;
        }

        case dashBoardActionType.GET_ALARM_SUM: {
            const clone = cloneDeep(state);
            // const { alarmSum } = clone;
            clone.alarmSum = payload;
            return clone;
        }

        case dashBoardActionType.GET_ALARM_TREND: {
            const clone = cloneDeep(state);
            // const { alarmTrend } = clone;
            clone.alarmTrend = payload;
            return clone;
        }

        case dashBoardActionType.GET_USAGE: {
            const clone = cloneDeep(state);
            // const { usage } = clone;
            clone.usage = payload;
            return clone;
        }

        default:
            return state;
    }
}
