import { apiMarketActionType as ACTION_TYPE } from '../../consts/apiMarketActionType';
import API from '../../api/apiMarket';

export const apiMarketActions = {

    // 获取市场分离信息
    getCatalogue (pid: any) {
        return (dispatch: any) => {
            return API.getCatalogue({ pid: pid, lvNum: 10 }).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_CATALOGUE,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    // 获取市场api列表
    getApiMarketList (params: any) {
        return (dispatch: any) => {
            return API.listByCondition(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_API_MARKET_LIST,
                        payload: res.data.data
                    });
                    return res;
                }
            });
        }
    },
    // 获取api详情
    getApiDetail (params: any) {
        return (dispatch: any) => {
            return API.getApiDetail(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_MARKET_API_DETAIL,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    // 获取api调用情况等
    getApiExtInfo (params: any) {
        let callFunc = 'getApiExtInfoForNormal'
        if (params.useAdmin) {
            callFunc = 'getApiExtInfoForManager'
        }
        return (dispatch: any) => {
            return (API as any)[callFunc](params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_API_EXT_INFO,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    // 申请api
    apiApply (params: any) {
        return (dispatch: any) => {
            return API.apiApply(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }
}
