import { dashBoardActionType as ACTION_TYPE } from '../../consts/dashBoardActionType';
import { message } from 'antd';
import API from '../../api/dashBoard';

export const dashBoardActions = {
    chooseUserDate (date, topn) {
        return dispatch => {
            dispatch({
                type: ACTION_TYPE.CHOOSE_USER_DATE,
                payload: date
            });
            dispatch(dashBoardActions.getApiCallInfo({ time: date }, false, date))
            dispatch(dashBoardActions.getApiCallFailRateTopN({ time: date, topn: topn }, false, date))
            dispatch(dashBoardActions.getApiCallNumTopN({ time: date, topn: topn }, date))
            dispatch(dashBoardActions.getApiSubscribe(date));
        }
    },
    chooseAdminDate (date, topn) {
        return dispatch => {
            dispatch({
                type: ACTION_TYPE.CHOOSE_ADMIN_DATE,
                payload: date
            });
            dispatch(dashBoardActions.getApiCallErrorInfo(date))
            dispatch(dashBoardActions.getApiCallInfo({ time: date }, true, date))
            dispatch(dashBoardActions.getApprovedMsgCount());
            dispatch(dashBoardActions.getUserCallTopN({ time: date, topn: topn }, true, date))
            dispatch(dashBoardActions.getApiCallFailRateTopN({ time: date, topn: topn }, true, date))
            dispatch(dashBoardActions.listApiCallNumTopNForManager({ time: date, topn: topn }))
        }
    },
    // 获取调用情况
    getApiCallInfo (params, isAdmin, date) {
        let callFunc = 'getApiCallInfoForNormal';
        let action_type = ACTION_TYPE.GET_USER_CALL_INFO;
        if (isAdmin) {
            callFunc = 'getApiCallInfoForManager'
            params.useAdmin = true
            action_type = ACTION_TYPE.GET_MARKET_CALL_INFO;
        }

        return (dispatch) => {
            API[callFunc](params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: action_type,
                        payload: {
                            callCount: res.data.callCount, // 调用总数
                            failPercent: res.data.failRate, // 失败率
                            apiNum: res.data.apiNum, // 调用最多api
                            infoList: res.data.infoList
                        },
                        date: date
                    });
                }
            });
        }
    },
    // 获取待审批的数量
    getApprovedMsgCount () {
        return (dispatch) => {
            API.getApplyCount({ status: 0 }).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_MARKET_API_APPLY_INFO,
                        payload: res.data
                    });
                }
            });
        }
    },
    // 获取用户调用排行
    getUserCallTopN (params, isAdmin, date) {
        params.useAdmin = true
        let action_type = ACTION_TYPE.GET_MARKET_API_CALL_RANK;

        return (dispatch) => {
            API.getUserCallTopN(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: action_type,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    getApiCallFailRateTopN (params, isAdmin, date) {
        let callFunc = 'listApiCallFailRateTopNForNormal';
        let action_type = ACTION_TYPE.GET_API_FAIL_RANK;
        if (isAdmin) {
            callFunc = 'listApiCallFailRateTopNForManager';
            params.useAdmin = true
            action_type = ACTION_TYPE.GET_MARKET_API_FAIL_RANK;
        }

        return (dispatch) => {
            API[callFunc](params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: action_type,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    // 获取用户申请api情况
    getApiSubscribe (date) {
        return (dispatch) => {
            API.getApiSubscribe().then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_USER_API_SUB_INFO,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    // 获取用户自己的api调用排行
    getApiCallNumTopN (params, date) {
        return (dispatch) => {
            API.getApiCallNumTopN(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_USER_API_CALL_RANK,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    // 获取api的错误分布
    getApiCallErrorInfo (date) {
        return (dispatch) => {
            API.getApiCallErrorInfoForManager({ time: date }).then((res) => {
                if (res.code === 1) {
                    const data = (res.data && res.data.recordInfoList) || [];
                    dispatch({
                        type: ACTION_TYPE.GET_MARKET_API_ERROR_INFO,
                        payload: data,
                        date: date
                    });
                }
            });
        }
    },
    // 管理员获取api调用次数topN
    listApiCallNumTopNForManager (params) {
        return (dispatch) => {
            API.listApiCallNumTopNForManager(params).then((res) => {
                if (res.code === 1) {
                    const data = res.data || [];
                    dispatch({
                        type: ACTION_TYPE.GET_MARKET_TOP_CALL_FUNC,
                        payload: data,
                        date: params.time
                    });
                }
            });
        }
    }
}
