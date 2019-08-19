import { taskQueryActionType } from '../../../consts/taskQueryActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    loading: false,
    taskList: [],
    taskDetail: [],
    tableReport: {},
    alarmNum: []
};

export default function taskQuery (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case taskQueryActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case taskQueryActionType.GET_TASK_LIST: {
            const clone = cloneDeep(state);
            // const { taskList } = clone;
            clone.taskList = payload;
            return clone;
        }

        case taskQueryActionType.GET_TASK_DETAIL: {
            const clone = cloneDeep(state);
            // const { taskDetail } = clone;
            clone.taskDetail = payload;
            return clone;
        }

        case taskQueryActionType.GET_TASK_TABLE_REPORT: {
            const clone = cloneDeep(state);
            // const { tableReport } = clone;
            clone.tableReport = payload;
            return clone;
        }

        case taskQueryActionType.GET_TASK_ALARM_NUM: {
            const clone = cloneDeep(state);
            // const { alarmNum } = clone;
            clone.alarmNum = payload;
            return clone;
        }

        default:
            return state;
    }
}
