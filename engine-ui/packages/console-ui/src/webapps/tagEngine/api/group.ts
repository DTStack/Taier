import req from '../consts/reqGroup';
import http from './http';

export default {
    getGroups (params?: any) {
        return http.post(req.GET_GROUPS, params);
    },
    getGroup (params?: any) {
        return http.post(req.GET_GROUP, params);
    },
    createGroup (params?: any) {
        return http.post(req.CREATE_GROUP, params);
    },
    uploadGroup (params?: any) {
        return http.post(req.UPLOAD_GROUP, params);
    },
    deleteGroup (params?: any) {
        return http.post(req.DELETE_GROUP, params);
    },
    updateGroup (params?: any) {
        return http.post(req.UPDATE_GROUP, params);
    },
    getGroupSpecimens (params?: any) {
        return http.post(req.GET_GROUP_SPECIMENS, params);
    }
}
