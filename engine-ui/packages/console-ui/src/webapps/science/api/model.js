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
    },
    getTaskModels (params) {
        return http.post(req.LIST_TASK_ALL_MODEL_AND_VERSION, params);
    },
    getModelListFromLab (params) {
        return http.post(req.LIST_MODEL_TASK_FROM_LAB, params);
    },
    saveModel (params) {
        return http.post(req.SAVE_MODEL, params);
    },
    getModelComopnentsList (params) {
        return http.post(req.GET_MODEL_COMPONENTS_LIST, params);
    },
    switchVersion (params) {
        return http.post(req.SWITCH_MODEL_VERSION, params);
    },
    deleteModel (params) {
        return http.post(req.DELETE_MODEL, params);
    },
    openModel (params) {
        return http.post(req.OPEN_MODEL, params);
    },
    disableModel (params) {
        return http.post(req.DISABLE_MODEL, params);
    }
}
