import http from './http'
import req from '../consts/reqUrls'

export default {
    getTaskById (params) {
        return http.post(req.GET_NOTEBOOK_TASK_BY_ID, params);
    }
}
