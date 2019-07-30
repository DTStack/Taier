/**
 * 系统管理
 */
import http from './http'
import req from '../consts/reqUrls'

export default {

    /**
     * 消息
     * @param { Intager } type  2:unread, 3:allread
     * @param { Object } params 分页 { currentPage, pageSize }
     */
    getMessage(params: any) {
        return http.post(req.MASSAGE_QUERY, params)
    },

    getMsgById(params: any) {
        return http.post(req.GET_MASSAGE_BY_ID, params)
    },

    markAsRead(params: any) {
        return http.post(req.MASSAGE_MARK_AS_READ, params)
    },

    markAsAllRead(params: any) {
        return http.post(req.MASSAGE_MARK_AS_ALL_READ, params)
    },

    deleteMsgs(params: any) {
        return http.post(req.MASSAGE_DELETE, params)
    }

}
