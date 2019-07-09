/**
 * 系统管理
 */
import http from './http'
import req from '../consts/reqUrls'

export default {
    getResourceList: function (params) {
        return http.post(req.GET_RESOURCE_LIST, params);
    },
    getClusterList: function (params) {
        return http.post(req.GET_CLUSTER_LIST, params);
    },
    uploadClusterResource (params) {
        return http.postAsFormData(req.UPLOAD_CLUSTER_RESOURCE, params);
    },
    getTenantList (params) {
        return http.post(req.GET_TENANT_LIST, params);
    },
    testCluster (params) {
        return http.post(req.TEST_CLUSTER_CONNECT, params);
    },
    createCluster (params) {
        return http.postAsFormData(req.NEW_CLUSTER, params);
    },
    bindUserToQuere (params) {
        return http.post(req.BIND_USER_TO_RESOURCE, params);
    },
    // getClusterInfo (params) {
    //     return http.post(req.GET_CLUSTER, params);
    // },
    updateCluster (params) {
        return http.postAsFormData(req.UPDATE_CLUSTER, params);
    },
    getQueueLists (params) {
        return http.post(req.GET_QUEUE_LISTS, params);
    },
    confirmSwitchQueue (params) {
        return http.post(req.CONFIRM_SWITCH_QUEUE, params);
    },
    // 任务管理模块
    // 概览-获取集群
    getClusterDetail (params) {
        return http.post(req.GET_CLUSTER_DETAIL, params);
    },
    // 概览-集群下拉列表
    getClusterSelect (params) {
        return http.post(req.GET_CLUSTER_SELECT, params);
    },
    // 获取节点下拉
    getNodeAddressSelect (params) {
        return http.post(req.GET_NODEADDRESS_SELECT, params);
    },
    // 根据节点搜索

    // 明细-根据任务名搜索任务
    searchTaskList (params) {
        return http.post(req.SEARCH_TASKNAME_LIST, params);
    },
    // 明细-模糊查询任务名
    searchTaskFuzzy (params) {
        return http.post(req.SEARCH_TASKNAME_FUZZY, params);
    },
    // 明细-杀任务
    killTask (params) {
        return http.post(req.KILL_TASK, params);
    },
    // 明细-杀死选中或者杀死全部任务
    killAllTask (params) {
        return http.post(req.KILL_ALL_TASK, params);
    },

    // 引擎列表
    getEngineList (params) {
        return http.post(req.GET_ENGINE_LIST, params);
    },
    // group列表
    getGroupList (params) {
        return http.post(req.GET_GROUP_LIST, params);
    },
    // 查看明细 和搜索条件
    getViewDetail (params) {
        return http.post(req.GET_VIEW_DETAIL, params);
    },
    // 顺序调整调整优先级
    changeJobPriority (params) {
        return http.post(req.CHANGE_JOB_PRIORITY, params);
    },
    // 查看剩余资源
    getClusterResources (params) {
        return http.post(req.GET_CLUSTER_RESOURCES, params);
    },
    uploadResource (params) {
        return http.postAsFormData(req.UPLOAD_RESOURCE, params);
    },
    testComponent (params) {
        return http.post(req.TEST_COMPONENT_CONNECT, params);
    },
    addCluster (params) {
        return http.post(req.ADD_CLUSTER, params); // 新增集群
    },
    addComponent (params) {
        return http.post(req.ADD_COMPONENT, params);
    },
    saveComponent (params) {
        return http.post(req.SAVE_COMPONENT, params);
    },
    deleteComponent (params) {
        return http.post(req.DELETE_COMPONENT, params); // 删除组件
    },
    addEngine (params) {
        return http.post(req.ADD_ENGINE, params);
    },
    getClusterInfo (params) {
        return http.post(req.GET_CLUSTER_INFO, params);
    },
    // 资源管理
    getAllCluster (params) {
        return http.post(req.GET_ALL_CLUSTER, params); // 返回数据包含集群下的engine，以及队列
    },
    searchTenant (params) {
        return http.post(req.SEARCH_TENANT, params);
    },
    getQueue (params) {
        return http.post(req.GET_QUEUE, params);
    },
    bindTenant (params) {
        return http.post(req.BIND_TENANT, params);
    },
    switchQueue (params) {
        return http.post(req.SWITCH_QUEUE, params);
    }
}
