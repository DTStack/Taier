
import http from './http'
import req from '../consts/reqUrls'

export default {
    allApplyList(params: any) {
        return http.post(req.GET_ALL_APPLY_LIST, params);
    },
    handleApply(params: any) {
        return http.post(req.HANDLE_APPLY, params);
    }

}
