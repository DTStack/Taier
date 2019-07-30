
import http from './http'
import req from '../consts/reqUrls'

export default {

    queryRole(params: any) {
        return http.post(req.DL_ROLE_QUERY, params)
    },
    updateRole(params: any) {
        return http.post(req.DL_ROLE_UPDATE, params)
    },
    deleteRole(params: any) {
        return http.post(req.DL_ROLE_DELETE, params)
    },
    getRolePerissions(params: any) {
        return http.post(req.DL_ROLE_PERMISSION_TREE, params)
    },
    getRolePerission(params: any) {
        return http.post(req.DL_ROLE_PERMISSION, params)
    },
    getUserById(params: any) {
        return http.post(req.DL_GET_USER_BY_ID, params)
    },
    addUser(params: any) {
        return http.post(req.DL_USER_ROLE_ADD, params)
    },
    delUser(params: any) {
        return http.post(req.DL_USER_ROLE_DELETE, params)
    },
    updateUser(params: any) {
        return http.post(req.DL_USER_ROLE_UPDATE, params)
    },
    getUsers(params: any) {
        return http.post(req.DL_GET_USER_LIST, params)
    },
    getUserPages(params: any) {
        return http.post(req.DL_GET_USER_PAGES, params)
    },
    getUsersNotInProject(params: any) {
        return http.post(req.DL_GET_USER_NOT_IN_PROJECT, params)
    }
}
