import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList (params?: any) {
        return http.post((req as any).DQ_GET_USER_LIST, params)
    },
    getMenuList (params?: any) {
        return http.post(req.GET_ALL_MENU_LIST, params)
    }
}
