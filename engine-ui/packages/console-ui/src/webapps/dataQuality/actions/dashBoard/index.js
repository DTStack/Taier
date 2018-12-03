import { dashBoardActionType } from '../../consts/dashBoardActionType';
// import { message } from 'antd';
import API from '../../api/dashBoard';

export const dashBoardActions = {
    getTopRecord (params) {
        return dispatch => {
            dispatch({
                type: dashBoardActionType.CHANGE_LOADING
            });
            API.getTopRecord(params).then((res) => {
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
    getAlarmSum (params) {
        return dispatch => {
            API.getAlarmSum(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_ALARM_SUM,
                        payload: res.data
                    });
                }
            });
        }
    },
    getAlarmTrend (params) {
        return dispatch => {
            API.getAlarmTrend(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dashBoardActionType.GET_ALARM_TREND,
                        payload: res.data
                    });
                }
            });
        }
    },
    getUsage (params) {
        return dispatch => {
            API.getUsage(params).then((res) => {
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
