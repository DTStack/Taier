import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params) {
        return http.post(req.GET_EXPERIMENT_CATALOGUES, params);
    },
    addExperiment (params) {
        return http.post(req.ADD_EXPERIMENT, params);
    },
    searchGlobal (params) {
        return http.post(req.SEARCH_EXPERIMENT, params);
    },
    openExperiment (params) {
        return http.post(req.OPEN_NOTEBOOK, params);
    },
    submitExperiment (params) {
        return http.post(req.SUBMIT_NOTEBOOK, params);
    },
    submitExperimentModel (params) {
        return http.post(req.SUBMIT_NOTEBOOK_MODEL, params);
    },
    deleteExperiment (params) {
        return http.post(req.DELETE_EXPERIMENT, params);
    }
}
