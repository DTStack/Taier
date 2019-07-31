import http from './http'
import req from '../consts/reqUrls'
import { taskType } from '../consts'

export default {
    getTaskById (params: any) {
        return http.post(req.GET_TASK_BY_ID, params);
    },
    addNotebook (params: any) {
        return http.post(req.ADD_NOTEBOOK, params)
    },
    submitNotebook (params: any) {
        return http.post(req.SUBMIT_TASK, params);
    },
    submitNotebookModel (params: any) {
        return http.post(req.SAVE_MODEL, params);
    },
    searchGlobal (params: any) {
        return http.post(req.SEARCH_NOTEBOOK, {
            ...params,
            taskType: taskType.NOTEBOOK
        });
    },
    deleteNotebook (params: any) {
        return http.post(req.DELETE_NOTEBOOK, params);
    },
    execTask (params: any) {
        return http.post(req.EXEC_NOTEBOOK, params);
    },
    pollTask (params: any) {
        return http.post(req.POLL_NOTEBOOK, params);
    },
    stopExecSQL (params: any) {
        return http.post(req.STOP_EXEC_NOTEBOOK, params);
    }
}
