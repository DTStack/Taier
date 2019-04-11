import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params) {
        return http.post(req.GET_CATALOGUES, params);
    },
    getTaskById (params) {
        return http.post(req.GET_NOTEBOOK_TASK_BY_ID, params);
    }
}
