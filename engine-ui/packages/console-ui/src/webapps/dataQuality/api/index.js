/**
 * 系统管理
 */
import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import req from '../consts/reqUrls'

export default {

    /**
     * 消息
     * @param { Intager } type  2:unread, 3:allread
     * @param { Object } params 分页 { currentPage, pageSize }
     */
    getMessage(params) {
        return http.post(req.MASSAGE_QUERY, params)
    },

    markAsRead(params) {
        return http.post(req.MASSAGE_MARK_AS_READ, params)
    },

    markAsAllRead(params) {
        return http.post(req.MASSAGE_MARK_AS_ALL_READ, params)
    },

    deleteMsgs(params) {
        return http.post(req.MASSAGE_DELETE, params)
    }

}