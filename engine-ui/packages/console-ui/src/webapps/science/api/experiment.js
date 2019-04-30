import http from './http'
import req from '../consts/reqUrls'
import { taskType } from '../consts'

export default {
    loadTreeData (params) {
        return http.post(req.GET_EXPERIMENT_CATALOGUES, params);
    },
    addExperiment (params) {
        return http.post(req.ADD_EXPERIMENT, {
            ...params,
            taskType: taskType.EXPERIMENT
        });
    },
    searchGlobal (params) {
        return http.post(req.SEARCH_EXPERIMENT, params);
    },
    openExperiment (params) {
        return http.post(req.GET_TASK_BY_ID, params);
    },
    submitExperiment (params) {
        return http.post(req.SUBMIT_TASK, params);
    },
    submitExperimentModel (params) {
        return http.post(req.SAVE_MODEL, params);
    },
    deleteExperiment (params) {
        return http.post(req.DELETE_EXPERIMENT, params);
    },
    getTableByName (params) {
        return http.post(req.GET_TABLENAME_BY_NAME, params);
    },
    getExperimentTask (params) {
        return http.post(req.GET_EXPERIMENT_TASK_BY_ID, params);
    },
    isPartitionTable (params) {
        return http.post(req.IS_PARTITION_TABLE, params);
    },
    getColumnsByTableName (params) {
        return http.post(req.GET_COLUMNS_BY_NAMES, params);
    },
    getInputTableColumns (params) {
        return http.post(req.GET_INPUT_TABLE_COLUMNS, params)
    },
    addOrUpdateTask (params) {
        return http.post(req.UPDATE_TASK, params);
    },
    cloneComponent (params) {
        return http.post(req.CLONE_COMPONENT, params);
    },
    getTaskJobId (params) {
        return http.post(req.GET_JOB_ID_BY_TASK, params);
    },
    stopJobList (params) {
        return http.post(req.STOP_TASK_BY_JOB_ID, params);
    },
    getRunTaskStatus (params) {
        return http.post(req.GET_TASK_STATUS_BY_JOB_ID, params);
    }
}
