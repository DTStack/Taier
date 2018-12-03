import { mineActionType as ACTION_TYPE } from '../../consts/mineActionType';
import API from '../../api/mine';

export const mineActions = {

    // 获取正在审批的个人api
    getApplyingList (params) {
        params = params || {};
        params.status = [0];
        return (dispatch) => {
            return API.getApplyList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_APPLYING_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    // 获取已审批个人api
    getAppliedList (params) {
        params = params || {};
        if (!params.status || params.status.length === 0) {
            params.status = [1, 2, 3, 4];
        }

        return (dispatch) => {
            return API.getApplyList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_APPLYED_LIST,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    // 停用，启用，禁用api
    updateApplyStatus (params) {
        let callFunc = 'updateApplyStatusForNormal'
        if (params.useAdmin) {
            callFunc = 'updateApplyStatusForManager'
        }
        return (dispatch) => {
            return API[callFunc](params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看调用情况
    getApiCallInfo (params) {
        let callFunc = 'getApiCallInfoForNormal'
        if (params.useAdmin) {
            callFunc = 'getApiCallInfoForManager'
        }
        return (dispatch) => {
            return API[callFunc](params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误信息
    getApiCallErrorInfo (params) {
        return (dispatch) => {
            return API.getApiCallErrorInfoForNormal(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api调用方式
    getApiCallUrl (params) {
        return (dispatch) => {
            return API.getApiCallUrl(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误日志
    queryApiCallLog (params) {
        let callFunc = 'queryApiCallLogForNormal'
        if (params.useAdmin) {
            callFunc = 'queryApiCallLogForManager'
        }
        return (dispatch) => {
            return API[callFunc](params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api联系人
    getApiCreatorInfo (params) {
        return (dispatch) => {
            return API.getApiCreatorInfo(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }

}
