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
    uploadClusterResource(params){
        return http.postAsFormData(req.UPLOAD_CLUSTER_RESOURCE,params);
    },
    getTenantList(params){
        return http.post(req.GET_USER_LIST,params);
    },
    testCluster(params){
        return http.post(req.TEST_CLUSTER_CONNECT,params);
    },
    createCluster(params){
        return http.postAsFormData(req.NEW_CLUSTER,params);
    },
    bindUserToQuere(params){
        return http.post(req.BIND_USER_TO_RESOURCE,params);
    }
}