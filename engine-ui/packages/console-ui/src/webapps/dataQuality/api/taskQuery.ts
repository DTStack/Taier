// import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getTaskList (params: any) {
        return http.post(req.GET_TASK_LIST, params);
    },
    getTaskDetail (params: any) {
        return http.post(req.GET_TASK_DETAIL, params);
    },
    getTaskTableReport (params: any) {
        return http.post(req.GET_TABLE_REPORT, params);
    },
    getTaskAlarmNum (params: any) {
        return http.post(req.GET_TASK_ALARM_NUM, params);
    },
    getFormatTableResult (params: any) {
        return http.post(req.GET_FORMAT_TABLE_RESULT, params);
    }

}
