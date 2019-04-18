import http from './http'
import req from '../consts/reqUrls'

export default {

    execTask (params) {
        return http.post(req.EXEC_SQL, params);
    },
    pollTask (params) {
        return http.post(req.POLL_SQL, params);
    },
    stopExecSQL (params) {
        return http.post(req.STOP_EXEC_SQL, params);
    },
    formatSQL (params) {
        return http.post(req.FORMAT_SQL, params);
    },
    getSQLResultData (params) {
        return http.post(req.GET_SQL_RESULT, params);
    },
    getSysParams (params) {
        return http.post(req.GET_SYS_PARAMS, params);
    }
}
