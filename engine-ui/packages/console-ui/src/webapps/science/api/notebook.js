import http from './http'
import req from '../consts/reqUrls'

export default {
    getTaskById (params) {
        return http.post(req.GET_NOTEBOOK_TASK_BY_ID, params);
    },
    addNotebook (params) {
        return http.post(req.ADD_NOTEBOOK, params);
    },
    openNotebook (params) {
        return http.post(req.OPEN_NOTEBOOK, params);
    },
    submitNotebook (params) {
        return http.post(req.SUBMIT_NOTEBOOK, params);
    },
    submitNotebookModel (params) {
        return http.post(req.SUBMIT_NOTEBOOK_MODEL, params);
    },
    searchGlobal (params) {
        return http.post(req.SEARCH_NOTEBOOK, params);
    },
    deleteNotebook (params) {
        return http.post(req.DELETE_NOTEBOOK, params);
    }
}
