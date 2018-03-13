import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getRuleLists(params) {
        return http.post(req.GET_RULE_LIST, params);
    },

}