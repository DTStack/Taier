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
    },
    getClusterInfo(params){
        return http.post(req.GET_CLUSTER,params);
    },
    updateCluster(params){
        return http.postAsFormData(req.UPDATE_CLUSTER,params);
    },

    // 任务管理模块
    // 概览-获取集群
    getClusterDetail(params) {
        return http.post(req.GET_CLUSTER_DETAIL,params);
    },
    // 概览-集群下拉列表
    getClusterSelect(params) {
        return http.post(req.GET_CLUSTER_SELECT,params);
    },
    // 明细-根据任务名搜索任务
    searchTaskList(params) {
        return http.post(req.SEARCH_TASKNAME_LIST,params);
    },
    // 明细-模糊查询任务名
    searchTaskFuzzy(params) {
        return http.post(req.SEARCH_TASKNAME_FUZZY,params);
    },
    // 明细-杀任务
    killTask(params) {
        return http.post(req.KILL_TASK,params);
    },
}