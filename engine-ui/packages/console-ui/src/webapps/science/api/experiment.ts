import http from './http'
import req from '../consts/reqUrls'
import { taskType } from '../consts'

export default {
    loadTreeData (params: any) {
        return http.post(req.GET_EXPERIMENT_CATALOGUES, params);
    },
    addExperiment (params: any) {
        return http.post(req.ADD_EXPERIMENT, {
            ...params,
            taskType: taskType.EXPERIMENT
        });
    },
    searchGlobal (params: any) {
        return http.post(req.SEARCH_EXPERIMENT, {
            ...params,
            taskType: taskType.EXPERIMENT
        });
    },
    openExperiment (params: any) {
        return http.post(req.GET_TASK_BY_ID, params);
    },
    submitExperiment (params: any) {
        return http.post(req.SUBMIT_TASK, params);
    },
    submitExperimentModel (params: any) {
        return http.post(req.SAVE_MODEL, params);
    },
    deleteExperiment (params: any) {
        return http.post(req.DELETE_EXPERIMENT, params);
    },
    getTableByName (params: any) {
        return http.post(req.GET_TABLENAME_BY_NAME, params);
    },
    getExperimentTask (params: any) {
        return http.post(req.GET_EXPERIMENT_TASK_BY_ID, params);
    },
    isPartitionTable (params: any) {
        return http.post(req.IS_PARTITION_TABLE, params);
    },
    getColumnsByTableName (params: any) {
        return http.post(req.GET_COLUMNS_BY_NAMES, params);
    },
    getInputTableColumns (params: any) {
        return http.post(req.GET_INPUT_TABLE_COLUMNS, params)
    },
    addOrUpdateTask (params: any) {
        return http.post(req.UPDATE_TASK, params);
    },
    formatSql (params: any) {
        return http.post(req.COMPONENT_FORMAT_SQL, params);
    },
    getComponentRunningLog (params: any) {
        return http.post(req.GET_COMPONENT_RUNNING_LOG, params);
    },
    cloneComponent (params: any) {
        return http.post(req.CLONE_COMPONENT, params);
    },
    getTaskJobId (params: any) {
        return http.post(req.GET_JOB_ID_BY_TASK, params);
    },
    stopJobList (params: any) {
        return http.post(req.STOP_TASK_BY_JOB_ID, params);
    },
    getRunTaskStatus (params: any) {
        return http.post(req.GET_TASK_STATUS_BY_JOB_ID, params);
    },
    getEvaluateReportChartData (params: any) {
        return http.post(req.GET_EVALUATE_REPORT_CHART_DATA, params);
    },
    getEvaluateReportTableData (params: any) {
        return http.post(req.GET_EVALUATE_REPORT_TABLE_DATA, params);
    }
}
