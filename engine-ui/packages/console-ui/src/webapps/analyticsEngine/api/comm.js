import http from './http'
import req from '../consts/reqUrls'

export default {

    execSQL (params) {
        return http.post(req.EXEC_SQL, params);
    },
    stopExecSQL (params) {
        return http.post(req.STOP_EXEC_SQL, params);
    },
    formatSQL (params) {
        return http.post(req.FORMAT_SQL, params);
    },
    getSQLResultData (params) {
        return http.post(req.GET_SQL_RESULT, params);
    }
}
