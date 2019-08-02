import http from './http'
import req from '../consts/reqUrls'

export default {

    getLoginedUser (params?: any) {
        return http.post(req.DA_GET_USER_BY_ID, params)
    },

    getUserList (params?: any) {
        return http.post((req as any).DATA_API_GET_USER_LIST, params)
    }

}
