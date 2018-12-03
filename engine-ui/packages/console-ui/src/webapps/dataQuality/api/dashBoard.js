// import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getTopRecord (params) {
        return http.post(req.GET_TOP_RECORD, params);
    },
    getAlarmSum (params) {
        return http.post(req.GET_ALARM_SUM, params);
    },
    getAlarmTrend (params) {
        return http.post(req.GET_ALARM_TREND, params);
    },
    getUsage (params) {
        return http.post(req.GET_DASHBOARD_USAGE, params);
    }

}
