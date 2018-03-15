import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getRuleLists(params) {
        return http.post(req.GET_RULE_LIST, params);
    },
    getMonitorFunction(params) {
        return http.post(req.GET_MONITOR_FUNCTION, params);
    },
    getMonitorRule(params) {
        return http.post(req.GET_MONITOR_RULE, params);
    },
    saveMonitorRule(params) {
        return http.post(req.SAVE_MONITOR_RULE, params);
    },
    deleteMonitorRule(params) {
        return http.post(req.DELETE_MONITOR_RULE, params);
    },
    addRule(params) {
        return http.post(req.ADD_RULE, params);
    },

}