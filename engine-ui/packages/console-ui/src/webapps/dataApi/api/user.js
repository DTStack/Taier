import http from './http'
import req from '../consts/reqUrls'

export default {

    // ========== User ========== //

    getLoginedUser () {
        return http.post(req.DA_GET_USER_BY_ID)
    },

    getUserList () {
        return http.post(req.DATA_API_GET_USER_LIST)
    }

}
