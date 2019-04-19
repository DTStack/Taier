import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params) {
        return http.post(req.GET_CATALOGUES, params);
    },
    getTaskById (params) {
        return http.post(req.GET_NOTEBOOK_TASK_BY_ID, params);
    },
    addFolder (params) {
        return http.post(req.ADD_FOLDER, params);
    },
    updateFolder (params) {
        return http.post(req.UPDATE_FOLDER, params);
    },
    deleteFolder (params) {
        return http.post(req.DELETE_FOLDER, params);
    }
}
