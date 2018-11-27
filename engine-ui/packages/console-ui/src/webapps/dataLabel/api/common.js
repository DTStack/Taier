import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList () {
        return http.post(req.DL_GET_USER_LIST)
    },
    getAllMenuList () {
        return http.post(req.GET_ALL_MENU_LIST)
    },
    getPeriodType () {
    	return http.post(req.GET_PERIOD_TYPE);
    },
    getNotifyType () {
    	return http.post(req.GET_NOTIFY_TYPE);
    }

}
