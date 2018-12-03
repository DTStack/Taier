// import utils from 'utils'
import http from './http';
import req from '../consts/reqUrls';

export default {
    getLists (params) {
        return http.post(req.GET_CHECK_LIST, params);
    },
    getCheckDetail (params) {
        return http.post(req.GET_CHECK_DETAIL, params);
    },
    addCheck (params) {
        return http.post(req.ADD_CHECK, params);
    },
    updateCheck (params) {
        return http.post(req.UPDATE_CHECK, params);
    },
    deleteCheck (params) {
        return http.post(req.DELETE_CHECK, params);
    },
    getCheckReport (params) {
        return http.post(req.GET_CHECK_REPORT, params);
    },
    getCheckReportTable (params) {
        return http.post(req.GET_CHECK_REPORT_TABLE, params);
    },
    updateTaskParams (params) {
        return http.post(req.UPDATE_CHECK_ENV_PARAMS, params);
    }
};
