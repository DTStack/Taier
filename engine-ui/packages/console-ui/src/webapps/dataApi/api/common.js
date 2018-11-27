import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList () {
        return http.post(req.DQ_GET_USER_LIST)
    },
    getMenuList () {
        return http.post(req.GET_ALL_MENU_LIST)
    }
}
