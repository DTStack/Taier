import http from './http'
import req from '../consts/reqUrls'

export default {

    getMonitorLists (params) {
        return http.post(req.GET_MONITOR_LIST, params);
    },
    getRuleFunction (params) {
        return http.post(req.GET_RULE_FUNCTION, params);
    },
    getTableColumn (params) {
        return http.post(req.GET_MONITOR_TABLE_COLUMN, params);
    },
    getMonitorRule (params) {
        return http.post(req.GET_MONITOR_RULE, params);
    },
    saveMonitorRule (params) {
        return http.post(req.SAVE_MONITOR_RULE, params);
    },
    deleteMonitorRule (params) {
        return http.post(req.DELETE_MONITOR_RULE, params);
    },
    addMonitor (params) {
        return http.post(req.ADD_MONITOR, params);
    },
    updateMonitor (params) {
        return http.post(req.UPDATE_MONITOR, params);
    },
    subscribeTable (params) {
        return http.post(req.SUBSCRIBE_TABLE, params);
    },
    unSubscribeTable (params) {
        return http.post(req.UNSUBSCRIBE_TABLE, params);
    },
    checkSubscribe (params) {
        return http.post(req.CHECK_SUBSCRIBE, params);
    },
    changeMonitorStatus (params) {
        return http.post(req.CHANGE_MONITOR_STATUS, params);
    },
    executeMonitor (params) {
        return http.post(req.EXECUTE_MONITOR, params);
    },
    getMonitorDetail (params) {
        return http.post(req.GET_MONITOR_DETAIL, params);
    },
    getRemoteTrigger (params) {
        return http.post(req.GET_REMOTE_TRIGGER, params);
    },
    addRemoteTrigger (params) {
        return http.post(req.ADD_REMOTE_TRIGGER, params);
    },
    delRemoteTrigger (params) {
        return http.post(req.DEL_REMOTE_TRIGGER, params);
    },
    checkMonitor (params) {
        return http.post(req.CHECK_MONITOR, params);
    },
    updateTaskParams (params) {
        return http.post(req.UPDATE_TASK_PARAMS, params);
    }

}
