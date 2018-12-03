// import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    queryRole (params) {
        return http.post(req.DQ_ROLE_QUERY, params)
    },
    updateRole (params) {
        return http.post(req.DQ_ROLE_UPDATE, params)
    },
    deleteRole (params) {
        return http.post(req.DQ_ROLE_DELETE, params)
    },
    getRolePerissions (params) {
        return http.post(req.DQ_ROLE_PERMISSION_TREE, params)
    },
    getRolePerission (params) {
        return http.post(req.DQ_ROLE_PERMISSION, params)
    },

    getUserById (params) {
        return http.post(req.DQ_GET_USER_BY_ID, params)
    },
    addUser (params) {
        return http.post(req.DQ_USER_ROLE_ADD, params)
    },
    delUser (params) {
        return http.post(req.DQ_USER_ROLE_DELETE, params)
    },
    updateUser (params) {
        return http.post(req.DQ_USER_ROLE_UPDATE, params)
    },
    getUsers (params) {
        return http.post(req.DQ_GET_USER_LIST, params)
    },
    getUserPages (params) {
        return http.post(req.DQ_GET_USER_PAGES, params)
    },
    getUsersNotInProject (params) {
        return http.post(req.DQ_GET_USER_NOT_IN_PROJECT, params)
    }
}
