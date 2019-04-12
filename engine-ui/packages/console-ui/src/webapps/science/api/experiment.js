import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params) {
        return http.post(req.GET_EXPERIMENT_CATALOGUES, params);
    },
    addExperiment (params) {
        return http.post(req.ADD_EXPERIMENT, params);
    }
}
