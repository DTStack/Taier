import { dashBoardActionType } from '../../consts/dashBoardActionType';
// import { message } from 'antd';
import API from '../../api/dashBoard';

export const dashBoardActions: any = {
    getTopRecord (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: dashBoardActionType.CHANGE_LOADING
            });
            API.getTopRecord(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_TOP_RECORD,
                        payload: res.data
                    });
                }
                dispatch({
                    type: dashBoardActionType.CHANGE_LOADING
                });
            });
        }
    },
    getAlarmSum (params: any) {
        return (dispatch: any) => {
            API.getAlarmSum(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_ALARM_SUM,
                        payload: res.data
                    });
                }
            });
        }
    },
    getAlarmTrend (params: any) {
        return (dispatch: any) => {
            API.getAlarmTrend(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_ALARM_TREND,
                        payload: res.data
                    });
                }
            });
        }
    },
    getUsage (params: any) {
        return (dispatch: any) => {
            API.getUsage(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_USAGE,
                        payload: res.data
                    });
                }
            });
        }
    }
}
