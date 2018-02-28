import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getLists(params) {
        return http.post(req.GET_CHECK_LIST, params);
    },
    getCheckDetail(params) {
    	return http.post(req.GET_CHECK_DETAIL, params);
    },
    editCheck(params) {
    	return http.post(req.EDIT_CHECK, params);
    },
    deleteCheck(params) {
    	return http.post(req.DELETE_CHECK, params);
    },
    getCheckReport(params) {
    	return http.post(req.GET_CHECK_REPORT, params);
    }

}