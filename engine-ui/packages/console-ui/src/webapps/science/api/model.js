import http from './http'
import req from '../consts/reqUrls'

export default {
    getModelList (params) {
        return http.post(req.GET_MODEL_LIST, params);
    },
    getModelVersions (params) {
        return http.post(req.GET_MODEL_VERSIONS, params);
    },
    getModelParamsList (params) {
        return http.post(req.GET_MODEL_PARAMS_LIST, params);
    },
    loadModel (params) {
        return http.post(req.LOAD_MODEL, params);
    }
}
