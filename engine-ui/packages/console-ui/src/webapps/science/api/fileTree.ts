import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params: any) {
        return http.post(req.GET_CATALOGUES, params);
    },
    addFolder (params: any) {
        return http.post(req.ADD_FOLDER, params);
    },
    updateFolder (params: any) {
        return http.post(req.UPDATE_FOLDER, params);
    },
    deleteFolder (params: any) {
        return http.post(req.DELETE_FOLDER, params);
    }
}
