/**
 * 系统管理
 */
import http from './http'
import req from '../consts/reqUrls'

export default {
    getResourceList: function (params: any) {
        return http.post(req.GET_RESOURCE_LIST, params);
    },
    getClusterList: function (params: any) {
        return http.post(req.GET_CLUSTER_LIST, params);
    },
    uploadClusterResource (params: any) {
        return http.postAsFormData(req.UPLOAD_CLUSTER_RESOURCE, params);
    },
    getTenantList (params?: any) {
        return http.post(req.GET_TENANT_LIST, params);
    },
    testCluster (params: any) {
        return http.post(req.TEST_CLUSTER_CONNECT, params);
    },
    createCluster (params: any) {
        return http.postAsFormData(req.NEW_CLUSTER, params);
    },
    bindUserToQuere (params: any) {
        return http.post(req.BIND_USER_TO_RESOURCE, params);
    },
    // getClusterInfo(params: any) {
    //     return http.post(req.GET_CLUSTER, params);
    // },
    updateCluster (params: any) {
        return http.postAsFormData(req.UPDATE_CLUSTER, params);
    },
    getQueueLists (params: any) {
        return http.post(req.GET_QUEUE_LISTS, params);
    },
    confirmSwitchQueue (params: any) {
        return http.post(req.CONFIRM_SWITCH_QUEUE, params);
    },
    // 任务管理模块
    // 概览-获取集群
    getClusterDetail (params: any) {
        return http.post(req.GET_CLUSTER_DETAIL, params);
    },
    // 概览-集群下拉列表
    getClusterSelect (params: any) {
        return http.post(req.GET_CLUSTER_SELECT, params);
    },
    // 获取节点下拉
    getNodeAddressSelect (params?: any) {
        return http.post(req.GET_NODEADDRESS_SELECT, params);
    },
    // 根据节点搜索

    // 明细-根据任务名搜索任务
    searchTaskList (params: any) {
        return http.post(req.SEARCH_TASKNAME_LIST, params);
    },
    // 明细-模糊查询任务名
    searchTaskFuzzy (params: any) {
        return http.post(req.SEARCH_TASKNAME_FUZZY, params);
    },
    // 明细-杀任务
    killTask (params: any) {
        return http.post(req.KILL_TASK, params);
    },
    // 明细-杀死选中或者杀死全部任务
    killAllTask (params: any) {
        return http.post(req.KILL_ALL_TASK, params);
    },

    // 引擎列表
    getEngineList (params?: any) {
        return http.post(req.GET_ENGINE_LIST, params);
    },
    // group列表
    getGroupList (params: any) {
        return http.post(req.GET_GROUP_LIST, params);
    },
    // 查看明细 和搜索条件
    getViewDetail (params: any) {
        return http.post(req.GET_VIEW_DETAIL, params);
    },
    // 顺序调整调整优先级
    changeJobPriority (params: any) {
        return http.post(req.CHANGE_JOB_PRIORITY, params);
    },
    // 查看剩余资源
    getClusterResources (params: any) {
        return http.post(req.GET_CLUSTER_RESOURCES, params);
    },
    uploadResource (params: any) {
        return http.postAsFormData(req.UPLOAD_RESOURCE, params);
    },
    uploadKerberosFile (params: any) {
        return http.postAsFormData(req.UPLOAD_KERBEROSFILE, params);
    },
    getKerberosFile (params: any) {
        return http.post(req.GET_KERBEROSFILE, params);
    },
    testComponent (params: any) {
        return http.post(req.TEST_COMPONENT_CONNECT, params);
    },
    testComponentKerberos (params: any) {
        return http.postAsFormData(req.TEST_COMPONENT_CONNECT_KERBEROS, params);
    },
    addCluster (params: any) {
        return http.post(req.ADD_CLUSTER, params); // 新增集群
    },
    addComponent (params: any) {
        return http.post(req.ADD_COMPONENT, params);
    },
    saveComponent (params: any) {
        return http.post(req.SAVE_COMPONENT, params);
    },
    saveComponentWithKerberos (params: any) {
        return http.postAsFormData(req.SAVE_COMPONENT_KERBEROS, params);
    },
    deleteComponent (params: any) {
        return http.post(req.DELETE_COMPONENT, params); // 删除组件
    },
    deleteKerberos (params: any) {
        return http.post(req.DELETE_KERBEROS, params); // 删除Haddop Kerberos认证文件
    },
    addEngine (params: any) {
        return http.post(req.ADD_ENGINE, params);
    },
    getClusterInfo (params: any) {
        return http.post(req.GET_CLUSTER_INFO, params);
    },
    updateHadoopVersion (params: { clusterId: number; hadoopVersion: string }) {
        return http.post(req.UPDATE_HADOOP_VERSION, params);
    },
    // 资源管理
    getAllCluster (params?: any) {
        return http.post(req.GET_ALL_CLUSTER, params); // 返回数据包含集群下的engine，以及队列
    },
    searchTenant (params: any) {
        return http.post(req.SEARCH_TENANT, params);
    },
    getQueue (params: any) {
        return http.post(req.GET_QUEUE, params);
    },
    bindTenant (params: any) {
        return http.post(req.BIND_TENANT, params);
    },
    switchQueue (params: any) {
        return http.post(req.SWITCH_QUEUE, params);
    }
}
