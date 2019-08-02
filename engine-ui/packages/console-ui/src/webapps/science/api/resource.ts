import http from './http'
import req from '../consts/reqUrls'
// import { taskType } from '../consts'

export default {
    getResourceById (params: any) {
        return http.post(req.GTE_RESOURCE_BY_ID, params);
    },
    addResource (params: any) {
        return http.postAsFormData(req.ADD_RESOURCE, params);
    },
    renameResource (params: any) {
        return http.post(req.RENAME_RESOURCE, params);
    },
    deleteResource (params: any) {
        return http.post(req.DELETE_RESOURCE, params);
    }
}
