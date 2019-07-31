import { combineReducers } from 'redux';
import actionType from '../../../consts/actionType/taskType';

function taskType (state = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case actionType.GET_SUPPORT_TASK_TYPES: {
            return payload || [];
        }
        default: {
            return state;
        }
    }
}
export default combineReducers({
    taskType
});
