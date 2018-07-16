/**
 * 系统管理
 */
import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import req from '../consts/reqUrls'

export default {
    getResourceList:function(params){
        return http.post(req.GET_RESOURCE_LIST,params);
    },
    getClusterList:function(params){
        return http.post(req.GET_CLUSTER_LIST,params);
    },
}