import commApi from '../api/comm';
import taskActions from '../consts/actionType/taskType';

// Action
export function getSupportTaskTypes () {
    return (dispatch: any) => {
        commApi.getSupportTaskTypes().then((res: any) => {
            if (res.code === 1) {
                return dispatch({
                    type: taskActions.GET_SUPPORT_TASK_TYPES,
                    payload: res.data || []
                })
            }
        })
    }
}
