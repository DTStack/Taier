
import http from './http'
import req from '../consts/reqUrls'

export default {
    getApplyList: function (params: any) {
        return http.post(req.GET_APPLY_LIST, params);
    },
    updateApplyStatusForNormal: function (params: any) {
        return http.post(req.UPDATE_APPLY_STATUS, params);
    },
    updateApplyStatusForManager: function (params: any) {
        return http.post(req.UPDATE_APPLY_STATUS_ADMIN, params);
    },
    getApiCallInfoForNormal (params: any) {
        return http.post(req.GET_API_CALL_INFO, params);
    },
    getApiCallInfoForManager (params: any) {
        return http.post(req.GET_USER_API_CALL_INFO_ADMIN, params);
    },
    getApiCallErrorInfoForNormal: function (params: any) {
        return http.post(req.GET_API_CALL_ERROR_INFO, params);
    },
    getApiCallErrorInfoForManager: function (params: any) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    },
    getApiCallUrl: function (params: any) {
        return http.post(req.GET_API_CALL_URL, params);
    },
    queryApiCallLogForNormal: function (params: any) {
        return http.post(req.GET_API_CALL_ERROR_LOG, params);
    },
    queryApiCallLogForManager: function (params: any) {
        return http.post(req.GET_API_CALL_ERROR_LOG_ADMIN, params);
    },
    getApiCreatorInfo: function (params: any) {
        return http.post(req.GET_API_CREATOR_INFO, params);
    }

}
