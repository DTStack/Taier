import http from './http'
import req from '../consts/reqUrls'

export default {
    allApplyList (params: any) {
        return http.post(req.GET_ALL_APPLY_LIST, params);
    },
    handleApply (params: any) {
        return http.post(req.HANDLE_APPLY, params);
    },
    editHandleApply (params: any) {
        return http.post(req.EDIT_HANDLE_APPLY, params);
    },
    getSecurityList (params: any) {
        return http.post(req.GET_SECURITY_LIST, params);
    },
    deleteSecurity (params: any) {
        return http.post(req.DELETE_SECURITY, params);
    },
    updateSecurity (params: any) {
        return http.post(req.UPDATE_SECURITY, params);
    },
    addSecurity (params: any) {
        return http.post(req.NEW_SECURITY, params);
    },
    listSecurityApiInfo (params: any) {
        return http.post(req.LIST_SECURITY_API_INFO, params);
    }
}
