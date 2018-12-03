import http from './http'
import req from '../consts/reqUrls'

export default {
    getApiCallInfoForNormal (params) {
        return http.post(req.GET_API_CALL_INFO, params);
    },
    getApiCallInfoForManager (params) {
        return http.post(req.GET_USER_API_CALL_INFO_ADMIN, params);
    },
    listApiCallFailRateTopNForNormal (params) {
        return http.post(req.GET_API_FAIL_RANK, params);
    },
    listApiCallFailRateTopNForManager (params) {
        return http.post(req.GET_API_FAIL_RANK_ADMIN, params);
    },
    getApiCallNumTopN (params) {
        return http.post(req.GET_USER_API_CALL_RANK, params);
    },
    getApiSubscribe (params) {
        return http.post(req.GET_USER_API_SUB_INFO, params);
    },
    getUserCallTopN (params) {
        return http.post(req.GET_MARKET_API_CALL_RANK, params);
    },
    getApiCallErrorInfoForNormal: function (params) {
        return http.post(req.GET_API_CALL_ERROR_INFO, params);
    },
    getApiCallErrorInfoForManager: function (params) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    },
    getApplyCount (params) {
        return http.post(req.GET_MARKET_API_APPLY_INFO, params);
    },
    listApiCallNumTopNForManager (params) {
        return http.post(req.GET_MARKET_TOP_CALL_FUNC, params)
    }
}
