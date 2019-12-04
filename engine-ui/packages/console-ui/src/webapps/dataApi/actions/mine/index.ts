import { mineActionType as ACTION_TYPE } from '../../consts/mineActionType';
import API from '../../api/mine';

export const mineActions: any = {

    // 获取正在审批的个人api
    getApplyingList (params: any) {
        params = params || {};
        params.status = [0];
        return (dispatch: any) => {
            return API.getApplyList(params).then((res: any) => {
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
    getAppliedList (params: any) {
        params = params || {};
        if (!params.status || params.status.length === 0) {
            params.status = [1, 2, 3, 4, 5];
        }

        return (dispatch: any) => {
            return API.getApplyList(params).then((res: any) => {
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
    updateApplyStatus (params: any) {
        let callFunc = 'updateApplyStatusForNormal'
        if (params.useAdmin) {
            callFunc = 'updateApplyStatusForManager'
        }
        return (dispatch: any) => {
            return (API as any)[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看调用情况
    getApiCallInfo (params: any) {
        let callFunc = 'getApiCallInfoForNormal'
        if (params.useAdmin) {
            callFunc = 'getApiCallInfoForManager'
        }
        return (dispatch: any) => {
            return (API as any)[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误信息
    getApiCallErrorInfo (params: any) {
        return (dispatch: any) => {
            return API.getApiCallErrorInfoForNormal(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api调用方式
    getApiCallUrl (params: any) {
        return (dispatch: any) => {
            return API.getApiCallUrl(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 重置token
    resetToken (params: number) {
        return (dispatch: any) => {
            return API.resetToken(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误日志
    queryApiCallLog (params: any) {
        let callFunc = 'queryApiCallLogForNormal'
        if (params.useAdmin) {
            callFunc = 'queryApiCallLogForManager'
        }
        return (dispatch: any) => {
            return (API as any)[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api联系人
    getApiCreatorInfo (params: any) {
        return (dispatch: any) => {
            return API.getApiCreatorInfo(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }

}
