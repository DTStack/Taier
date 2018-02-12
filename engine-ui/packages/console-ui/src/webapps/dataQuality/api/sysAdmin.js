import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    queryRole(params) {
        return http.post(req.DQ_ROLE_QUERY, params)
    },
    updateRole(params) {
        return http.post(req.DQ_ROLE_UPDATE, params)
    },
    delRole(params) {
        return http.post(req.DQ_ROLE_DELETE, params)
    },
    getRolePerissions(params) {
        return http.post(req.DQ_ROLE_PERMISSION_TREE, params)
    },
    getRolePerission(params) {
        return http.post(req.DQ_ROLE_PERMISSION, params)
    },

    getUserById(params) {
        return http.post(req.DQ_GET_USER_BY_ID, params)
    },
    addUserRole(params) {
        return http.post(req.DQ_USER_ROLE_ADD, params)
    },
    delUserRole(params) {
        return http.post(req.DQ_USER_ROLE_DELETE, params)
    },
    updateUserRole(params) {
        return http.post(req.DQ_USER_ROLE_UPDATE, params)
    },
    

}