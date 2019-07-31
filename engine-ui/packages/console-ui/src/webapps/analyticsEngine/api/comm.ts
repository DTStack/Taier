import http from './http'
import req from '../consts/reqUrls'

export default {

    execSQL (params: any) {
        return http.post(req.EXEC_SQL, params);
    },
    stopExecSQL (params: any) {
        return http.post(req.STOP_EXEC_SQL, params);
    },
    formatSQL (params: any) {
        return http.post(req.FORMAT_SQL, params);
    },
    getSQLResultData (params: any) {
        return http.post(req.GET_SQL_RESULT, params);
    }
}
