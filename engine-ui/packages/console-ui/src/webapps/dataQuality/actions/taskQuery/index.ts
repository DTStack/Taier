import { taskQueryActionType } from '../../consts/taskQueryActionType';
// import { message } from 'antd';
import API from '../../api/taskQuery';

export const taskQueryActions: any = {
    getTaskList (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: taskQueryActionType.CHANGE_LOADING
            });
            API.getTaskList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: taskQueryActionType.GET_TASK_LIST,
                        payload: res.data
                    });
                }
                dispatch({
                    type: taskQueryActionType.CHANGE_LOADING
                });
            });
        }
    },
    getTaskDetail (params: any) {
        return (dispatch: any) => {
            API.getTaskDetail(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: taskQueryActionType.GET_TASK_DETAIL,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTaskTableReport (params: any) {
        return (dispatch: any) => {
            API.getTaskTableReport(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: taskQueryActionType.GET_TASK_TABLE_REPORT,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTaskAlarmNum (params: any) {
        return (dispatch: any) => {
            API.getTaskAlarmNum(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: taskQueryActionType.GET_TASK_ALARM_NUM,
                        payload: res.data
                    });
                }
            });
        }
    }
}
