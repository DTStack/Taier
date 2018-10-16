// 从config文件全局读取
import { CONSOLE_BASE_URL, DQ_BASE_URL,  CONSOLE_BASE_UPLOAD_URL } from 'config/base';

export default {

    // ===== 用户相关 ===== //
    DA_GET_USER_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DA_GET_USER_AUTH_BY_ID:`${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户权限
    GET_USER_LIST:`${CONSOLE_BASE_URL}/tenant/listTenant`,//租户列表
    GET_RESOURCE_USER_LIST:`${CONSOLE_BASE_URL}/tenant/listByQueueId`,//获取资源已绑定的租户
    BIND_USER_TO_RESOURCE:`${CONSOLE_BASE_URL}/tenant/addToQueue`,//绑定用户到资源队列
    
    //集群
    GET_RESOURCE_LIST:`${CONSOLE_BASE_URL}/cluster/pageQueue`,//查看资源列表
    GET_CLUSTER_LIST:`${CONSOLE_BASE_URL}/cluster/pageQuery`,//查看集群列表
    UPDATE_CLUSTER:`${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/update`,//更新集群信息
    NEW_CLUSTER:`${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/add`,//新建集群
    TEST_CLUSTER_CONNECT:`${CONSOLE_BASE_URL}/cluster/testConnect`,//测试集群联通性
    UPLOAD_CLUSTER_RESOURCE:`${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/config`,//上传集群资源配置包
    GET_CLUSTER:`${CONSOLE_BASE_URL}/cluster/getOne`,//获取集群信息

    // 任务管理
    GET_CLUSTER_DETAIL: `${CONSOLE_BASE_URL}/group/overview`,  //概览-获取集群
    GET_CLUSTER_SELECT: `${CONSOLE_BASE_URL}/cluster/clusters`, // 概览-集群下拉列表
    SEARCH_TASKNAME_LIST:  `${CONSOLE_BASE_URL}/group/searchJob`,  // 明细-根据任务名搜索任务
    SEARCH_TASKNAME_FUZZY: `${CONSOLE_BASE_URL}/group/listNames`,   // 明细-模糊查询任务名
    KILL_TASK: `${CONSOLE_BASE_URL}/group/stopJob`,    // 明细-杀任务
    
    GET_ENGINE_LIST: `${CONSOLE_BASE_URL}/group/engineTypes`,   // 引擎列表
    GET_GROUP_LIST: `${CONSOLE_BASE_URL}/group/groups`,   // group列表
    GET_VIEW_DETAIL: `${CONSOLE_BASE_URL}/group/groupDetail`,   // 查看明细 和搜索条件
    CHANGE_JOB_PRIORITY: `${CONSOLE_BASE_URL}/group/jobPriority`,  //顺序调整调整优先级
    GET_CLUSTER_RESOURCES: `${CONSOLE_BASE_URL}/group/clusterResources`, // 查看剩余资源
} 