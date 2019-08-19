import http from './http'
import req from '../consts/reqUrls'

export default {
    getModelList (params: any) {
        return http.post(req.GET_MODEL_LIST, params);
    },
    getModelVersions (params: any) {
        return http.post(req.GET_MODEL_VERSIONS, params);
    },
    getModelParamsList (params: any) {
        return http.post(req.GET_MODEL_PARAMS_LIST, params);
    },
    loadModel (params: any) {
        return http.post(req.LOAD_MODEL, params);
    },
    getTaskModels () {
        return http.post(req.LIST_TASK_ALL_MODEL_AND_VERSION);
    },
    getModelListFromLab (params: any) {
        return http.post(req.LIST_MODEL_TASK_FROM_LAB, params);
    },
    saveModel (params: any) {
        return http.post(req.SAVE_MODEL, params);
    },
    getModelComopnentsList (params?: any) {
        return http.post(req.GET_MODEL_COMPONENTS_LIST, params);
    },
    switchVersion (params: any) {
        return http.post(req.SWITCH_MODEL_VERSION, params);
    },
    deleteModel (params: any) {
        return http.post(req.DELETE_MODEL, params);
    },
    openModel (params: any) {
        return http.post(req.OPEN_MODEL, params);
    },
    disableModel (params: any) {
        return http.post(req.DISABLE_MODEL, params);
    }
}
