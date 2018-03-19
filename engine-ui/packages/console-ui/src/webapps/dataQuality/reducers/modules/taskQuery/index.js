import { taskQueryActionType } from '../../../consts/taskQueryActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    taskList: [],
    taskDetail: [],
}

export default function taskQuery(state = initialState, action) {
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
            const { taskList } = clone;
            clone.taskList = payload;
            return clone;
        }

        case taskQueryActionType.GET_TASK_DETAIL: {
            const clone = cloneDeep(state);
            const { taskDetail } = clone;
            clone.taskDetail = payload;
            return clone;
        }

        default:
            return state;
    }
}