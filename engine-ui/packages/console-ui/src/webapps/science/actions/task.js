import commApi from '../api/comm';
import taskActions from '../consts/actionType/taskType';

// Action
export function getSupportTaskTypes () {
    return (dispatch) => {
        commApi.getSupportTaskTypes().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: taskActions.GET_SUPPORT_TASK_TYPES,
                    payload: res.data || []
                })
            }
        })
    }
}
