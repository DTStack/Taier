import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getTaskList(params) {
        return http.post(req.GET_TASK_LIST, params);
    },
    getTaskDetail(params) {
        return http.post(req.GET_TASK_DETAIL, params);
    },
	
}