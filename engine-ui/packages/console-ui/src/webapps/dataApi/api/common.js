import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList() {
        return http.post(req.DQ_GET_USER_LIST)
    },
}