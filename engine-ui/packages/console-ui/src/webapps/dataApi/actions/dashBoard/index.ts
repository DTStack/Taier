import { dashBoardActionType as ACTION_TYPE } from '../../consts/dashBoardActionType';
import API from '../../api/dashBoard';

export const dashBoardActions: any = {
    chooseUserDate (date: any, topn: any) {
        return (dispatch: any) => {
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
    chooseAdminDate (date: any, topn: any) {
        return (dispatch: any) => {
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
    getApiCallInfo (params: any, isAdmin: any, date: any) {
        let callFunc = 'getApiCallInfoForNormal';
        let actionType = ACTION_TYPE.GET_USER_CALL_INFO;
        if (isAdmin) {
            callFunc = 'getApiCallInfoForManager'
            params.useAdmin = true
            actionType = ACTION_TYPE.GET_MARKET_CALL_INFO;
        }

        return (dispatch: any) => {
            API[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: actionType,
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
        return (dispatch: any) => {
            API.getApplyCount({ status: 0 }).then((res: any) => {
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
    getUserCallTopN (params: any, isAdmin: any, date: any) {
        params.useAdmin = true
        let actionType = ACTION_TYPE.GET_MARKET_API_CALL_RANK;

        return (dispatch: any) => {
            API.getUserCallTopN(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: actionType,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    getApiCallFailRateTopN (params: any, isAdmin: any, date: any) {
        let callFunc = 'listApiCallFailRateTopNForNormal';
        let actionType = ACTION_TYPE.GET_API_FAIL_RANK;
        if (isAdmin) {
            callFunc = 'listApiCallFailRateTopNForManager';
            params.useAdmin = true
            actionType = ACTION_TYPE.GET_MARKET_API_FAIL_RANK;
        }

        return (dispatch: any) => {
            API[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: actionType,
                        payload: res.data,
                        date: date
                    });
                }
            });
        }
    },
    // 获取用户申请api情况
    getApiSubscribe (date: any) {
        return (dispatch: any) => {
            API.getApiSubscribe().then((res: any) => {
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
    getApiCallNumTopN (params: any, date: any) {
        return (dispatch: any) => {
            API.getApiCallNumTopN(params).then((res: any) => {
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
    getApiCallErrorInfo (date: any) {
        return (dispatch: any) => {
            API.getApiCallErrorInfoForManager({ time: date }).then((res: any) => {
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
    listApiCallNumTopNForManager (params: any) {
        return (dispatch: any) => {
            API.listApiCallNumTopNForManager(params).then((res: any) => {
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
