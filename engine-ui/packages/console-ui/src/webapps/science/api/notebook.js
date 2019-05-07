import http from './http'
import req from '../consts/reqUrls'
import { taskType } from '../consts'

export default {
    getTaskById (params) {
        return http.post(req.GET_TASK_BY_ID, params);
    },
    addNotebook (params) {
        return http.post(req.ADD_NOTEBOOK, {
            ...params,
            taskType: taskType.NOTEBOOK
        });
    },
    submitNotebook (params) {
        return http.post(req.SUBMIT_TASK, params);
    },
    submitNotebookModel (params) {
        return http.post(req.SAVE_MODEL, params);
    },
    searchGlobal (params) {
        return http.post(req.SEARCH_NOTEBOOK, {
            ...params,
            taskType: taskType.NOTEBOOK
        });
    },
    deleteNotebook (params) {
        return http.post(req.DELETE_NOTEBOOK, params);
    },
    execTask (params) {
        return http.post(req.EXEC_NOTEBOOK, params);
    },
    pollTask (params) {
        return http.post(req.POLL_NOTEBOOK, params);
    },
    stopExecSQL (params) {
        return http.post(req.STOP_EXEC_NOTEBOOK, params);
    }
}
