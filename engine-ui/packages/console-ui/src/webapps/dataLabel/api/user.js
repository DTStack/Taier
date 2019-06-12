import http from './http'
import req from '../consts/reqUrls'

export default {

    // ========== User ========== //

    getLoginedUser () {
        return http.post(req.DL_GET_USER_BY_ID)
    },

    getUserList () {
        return http.post(req.DL_GET_USER_LIST)
    }

}
