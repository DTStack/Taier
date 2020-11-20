// 从config文件全局读取

export const CONSOLE_BASE_URL = '/node';
export const CONSOLE_BASE_UPLOAD_URL = '/node';
export const CONSOLE_PERFORM_URL = '/api/console'
export default {

    // ===== 用户相关 ===== //
    DA_GET_USER_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DA_GET_USER_AUTH_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户权限
    GET_TENANT_LIST: `${CONSOLE_BASE_URL}/tenant/listTenant`, // 租户列表
    GET_RESOURCE_USER_LIST: `${CONSOLE_BASE_URL}/tenant/listByQueueId`, // 获取资源已绑定的租户
    BIND_USER_TO_RESOURCE: `${CONSOLE_BASE_URL}/tenant/addToQueue`, // 绑定用户到资源队列
    CONFIRM_SWITCH_QUEUE: `${CONSOLE_BASE_URL}/tenant/updateQueueId`, // 确认切换队列
    // 集群
    GET_RESOURCE_LIST: `${CONSOLE_BASE_URL}/cluster/pageQueue`, // 查看资源列表
    GET_CLUSTER_LIST: `${CONSOLE_BASE_URL}/cluster/pageQuery`, // 查看集群列表
    UPDATE_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/update`, // 更新集群信息
    NEW_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/add`, // 新建集群
    TEST_CLUSTER_CONNECT: `${CONSOLE_BASE_URL}/cluster/testConnect`, // 测试集群联通性
    UPLOAD_CLUSTER_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/config`, // 上传集群资源配置包
    GET_CLUSTER: `${CONSOLE_BASE_URL}/cluster/getOne`, // 获取集群信息
    GET_QUEUE_LISTS: `${CONSOLE_BASE_URL}/cluster/listQueues`, // 集群下队列列表
    // 任务管理
    GET_CLUSTER_DETAIL: `${CONSOLE_BASE_URL}/console/overview`, // 概览-获取集群
    GET_CLUSTER_SELECT: `${CONSOLE_BASE_URL}/cluster/clusters`, // 概览-集群下拉列表
    GET_NODEADDRESS_SELECT: `${CONSOLE_BASE_URL}/console/nodeAddress`, // 获取节点下拉
    // 根据节点搜索

    SEARCH_TASKNAME_LIST: `${CONSOLE_BASE_URL}/console/searchJob`, // 明细-根据任务名搜索任务
    SEARCH_TASKNAME_FUZZY: `${CONSOLE_BASE_URL}/console/listNames`, // 明细-模糊查询任务名
    KILL_TASK: `${CONSOLE_BASE_URL}/console/stopJob`, // 明细-杀任务
    KILL_TASKS: `${CONSOLE_BASE_URL}/console/stopJobList`, // 明细-杀全部任务或选中任务
    KILL_ALL_TASK: `${CONSOLE_BASE_URL}/console/stopAll`, // 杀全部任务
    JOB_STICK: `${CONSOLE_BASE_URL}/console/jobStick`, // 置顶任务

    GET_ENGINE_LIST: `${CONSOLE_BASE_URL}/console/engineTypes`, // 引擎列表
    GET_GROUP_LIST: `${CONSOLE_BASE_URL}/console/groups`, // group列表
    GET_VIEW_DETAIL: `${CONSOLE_BASE_URL}/console/groupDetail`, // 查看明细 和搜索条件
    CHANGE_JOB_PRIORITY: `${CONSOLE_BASE_URL}/console/jobPriority`, // 顺序调整调整优先级
    GET_CLUSTER_RESOURCES: `${CONSOLE_BASE_URL}/console/clusterResources`, // 查看剩余资源

    // 4.0版本
    GET_CLUSTER_INFO: `${CONSOLE_BASE_URL}/component/cluster/getCluster`,
    UPLOAD_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/upload/component/config`, // 上传配置文件
    DOWNLOAD_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/download/component/downloadFile`, // 下载配置文件
    DELETE_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/cluster/deleteCluster`, // 删除集群
    DELETE_COMPONENT: `${CONSOLE_BASE_URL}/component/delete`,
    GET_COMPONENT_VERSION: `${CONSOLE_BASE_URL}/component/getComponentVersion`,
    TEST_CONNECTS: `${CONSOLE_BASE_URL}/component/testConnects`,
    SAVE_COMPONENT: `${CONSOLE_BASE_URL}/upload/component/addOrUpdateComponent`,
    CLOSE_KERBEROS: `${CONSOLE_BASE_URL}/component/closeKerberos`,
    GET_VERSION: `${CONSOLE_BASE_URL}/component/getComponentVersion`,
    ADD_CLUSTER: `${CONSOLE_BASE_URL}/component/addOrCheckClusterWithName`, // 新增集群
    GET_LOADTEMPLATE: `${CONSOLE_BASE_UPLOAD_URL}/component/loadTemplate`, // 获取上传模板
    UPLOAD_KERBEROSFILE: `${CONSOLE_BASE_UPLOAD_URL}/upload/component/hadoopKerberosConfig`, // 上传kerberos认证文件
    GET_KERBEROSFILE: `${CONSOLE_BASE_URL}/component/getHadoopKerberosFile`, // 获取上传过的kerberos认证文件的信息内容
    TEST_COMPONENT_CONNECT: `${CONSOLE_BASE_URL}/component/testConnections`,
    TEST_COMPONENT_CONNECT_KERBEROS: `${CONSOLE_BASE_UPLOAD_URL}/upload/service/component/testConnections`, // 测试连通性只要有组件开启kerberos认证就掉该接口
    ADD_COMPONENT: `${CONSOLE_BASE_URL}/component/addComponent`,
    SAVE_COMPONENT_KERBEROS: `${CONSOLE_BASE_UPLOAD_URL}/upload/service/component/updateWithKerberos`, // 开启kerberos认证的保存接口
    DELETE_KERBEROS: `${CONSOLE_BASE_URL}/component/rmKerberosConfig`, // 删除Haddop Kerberos认证文件
    ADD_ENGINE: `${CONSOLE_BASE_URL}/engine/addEngine`,
    ADD_ENGINS: `${CONSOLE_BASE_URL}/engine/addEngines`,
    UPDATE_CLUSTER_VERSION: `${CONSOLE_BASE_URL}/cluster/updateGlobalConfig`, // 更新hadoop版本

    // 4.1版本
    GET_COMPONENTSTORE: `${CONSOLE_BASE_URL}/component/getComponentStore`, // 获取存储组件列表
    PARSE_KERBEROS: `${CONSOLE_BASE_URL}/upload/component/parseKerberos`,

    // 资源管理
    GET_ALL_CLUSTER: `${CONSOLE_BASE_URL}/cluster/getAllCluster`,
    SEARCH_TENANT: `${CONSOLE_BASE_URL}/tenant/pageQuery`,
    GET_QUEUE: `${CONSOLE_BASE_URL}/engine/getQueue`,
    BIND_TENANT: `${CONSOLE_BASE_URL}/tenant/bindingTenant`,
    LDAP_ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/bindAccountList`,
    SWITCH_QUEUE: `${CONSOLE_BASE_URL}/tenant/bindingQueue`,
    BIND_NAME_SPACE: `${CONSOLE_BASE_URL}/tenant/bindNamespace`,
    GET_TASKLIMITS: `${CONSOLE_BASE_URL}/tenant/queryTaskResourceLimits`,
    REFRESH_QUEUE: `${CONSOLE_BASE_URL}/component/refresh`,
    TASK_RESOURCE: `${CONSOLE_BASE_URL}/console/getTaskResourceTemplate`,
    // TiDB 引擎账号绑定
    ACCOUNT_UNBIND_LIST: `${CONSOLE_BASE_URL}/account/getTenantUnBandList`,
    ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/bindAccount`,
    UPDATE_ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/updateBindAccount`,
    ACCOUNT_BIND_LIST: `${CONSOLE_BASE_URL}/account/pageQuery`,
    ACCOUNT_UNBIND: `${CONSOLE_BASE_URL}/account/unbindAccount`,

    // 告警通道
    ADD_OR_UPDATE_ALARM: `${CONSOLE_PERFORM_URL}/service/alert/edit`,
    GET_ALARM_RULE_LIST: `${CONSOLE_PERFORM_URL}/service/alert/page`,
    DEL_ALARM_RULE_LIST: `${CONSOLE_PERFORM_URL}/service/alert/delete`,
    GET_ALARM_BY_ID: `${CONSOLE_PERFORM_URL}/service/alert/getByAlertId`,
    SET_DEFAULT_ALERT: `${CONSOLE_PERFORM_URL}/service/alert/setDefaultAlert`,
    TEST_ALERT: `${CONSOLE_PERFORM_URL}/service/alert/testAlert`
}
