import { numOrStr, Response } from 'typing';
import http from './http'
import req from '../consts/reqUrls'

export interface InvalidData {
    table?: string;
    total?: number;
    current?: number;
    result?: any[];
    lifeCycle?: string;
}

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
    },
    getInvalidData (params: {
        recordId: numOrStr;
        ruleId: numOrStr;
        current?: number;
        pageSize?: number;
    }) {
        return http.post<Response<InvalidData>>(req.GET_INVALID_TABLE_RESULT, params);
    },
    getDownInvalidDataURL (recordId: numOrStr, ruleId: numOrStr) {
        return `${req.DOWNLOAD_INVALID_TABLE_RESULT}?recordId=${recordId}&ruleId=${ruleId}`;
    }
}
