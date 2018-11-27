import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {
    allApplyList (params) {
        return http.post(req.GET_ALL_APPLY_LIST, params);
    },
    handleApply (params) {
        return http.post(req.HANDLE_APPLY, params);
    }

}
