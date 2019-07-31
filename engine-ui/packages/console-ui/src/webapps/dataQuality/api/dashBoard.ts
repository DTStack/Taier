// import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getTopRecord (params: any) {
        return http.post(req.GET_TOP_RECORD, params);
    },
    getAlarmSum (params: any) {
        return http.post(req.GET_ALARM_SUM, params);
    },
    getAlarmTrend (params: any) {
        return http.post(req.GET_ALARM_TREND, params);
    },
    getUsage (params: any) {
        return http.post(req.GET_DASHBOARD_USAGE, params);
    }

}
