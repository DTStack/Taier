import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList() {
        return http.post(req.LB_GET_USER_LIST)
    },

}