export const BASE_URI = '/taier/api';

export default {
    GET_RETAINDB_LIST: `${BASE_URI}/batchComponent/getAllDatabases`, // 获取可以对接项目的数据库表
    CONVERT_TO_HIVE_COLUMNS: `${BASE_URI}/batch/batchDataSource/convertToHiveColumns`, // 转换成hive类型的数据类型
    LOGIN: `${BASE_URI}/user/login`, // 登陆
    GET_TENANT_LIST: `${BASE_URI}/tenant/listTenant`, // 获取租户列表
    GET_META_COMPONENT: `${BASE_URI}/cluster/getMetaComponent`, // 获取集群的元数据组件类型
    ADD_TENANT: `${BASE_URI}/tenant/addTenant`, // 新增租户
    SWITCH_TENANT: `${BASE_URI}/user/switchTenant`, // 切换租户
    GET_CLUSTER_LIST: `${BASE_URI}/cluster/pageQuery`, // 查看集群列表
    GET_CLUSTER_DETAIL: `${BASE_URI}/console/overview`, // 获取集群详情
    KILL_TASKS: `${BASE_URI}/console/stopJobList`, // 杀全部任务或选中任务
    KILL_ALL_TASK: `${BASE_URI}/console/stopAll`, // 杀全部任务
    DOWNLOAD_RESOURCE: `${BASE_URI}/download/component/downloadFile`, // 下载配置文件
    JOB_STICK: `${BASE_URI}/console/jobStick`, // 置顶任务
    GET_VIEW_DETAIL: `${BASE_URI}/console/groupDetail`, // 查看明细 和搜索条件
    GET_CLUSTER_RESOURCES: `${BASE_URI}/console/clusterResources`, // 查看剩余资源
    GET_CLUSTER_INFO: `${BASE_URI}/cluster/getCluster`, // 获取集群详情
    UPLOAD_RESOURCE: `${BASE_URI}/upload/component/config`, // 上传配置文件
    DELETE_CLUSTER: `${BASE_URI}/cluster/deleteCluster`, // 删除集群
    DELETE_COMPONENT: `${BASE_URI}/component/delete`, // 删除组件
    TEST_CONNECT: `${BASE_URI}/component/testConnect`, // 测试单个组件连通性
    TEST_CONNECTS: `${BASE_URI}/component/testConnects`, // 测试所有组件连通性
    SAVE_COMPONENT: `${BASE_URI}/upload/component/addOrUpdateComponent`, // 保存集群配置的组件信息
    GET_NODEADDRESS_SELECT: `${BASE_URI}/console/nodeAddress`, // 获取节点下拉
    CLOSE_KERBEROS: `${BASE_URI}/component/closeKerberos`, // 删除 Kerberos
    GET_VERSION: `${BASE_URI}/component/getComponentVersion`, // 获取支持的组件版本信息
    ADD_CLUSTER: `${BASE_URI}/cluster/addCluster`, // 新增集群
    GET_LOADTEMPLATE: `${BASE_URI}/component/loadTemplate`, // 获取上传模板
    GET_COMPONENTSTORE: `${BASE_URI}/component/getComponentStore`, // 获取存储组件列表
    PARSE_KERBEROS: `${BASE_URI}/upload/component/parseKerberos`, // 解析 Kerberos 文件
    UPLOAD_KERBEROS: `${BASE_URI}/upload/component/uploadKerberos`, // 上传 kerberos 文件
    UPDATE_KRB5CONF: `${BASE_URI}/component/updateKrb5Conf`, // 更新 krb5 文件
    GET_ALL_CLUSTER: `${BASE_URI}/cluster/getAllCluster`, // 获取所有集群信息
    GET_ENGINES_BY_CLUSTER: `${BASE_URI}/cluster/getClusterEngine`, // 获取集群下的引擎信息
    SEARCH_TENANT: `${BASE_URI}/tenant/pageQuery`, // 获取当前集群绑定的租户信息
    BIND_TENANT: `${BASE_URI}/tenant/bindingTenant`, // 绑定集群和租户
    SWITCH_QUEUE: `${BASE_URI}/tenant/bindingQueue`, // 修改集群和租户绑定信息
    REFRESH_QUEUE: `${BASE_URI}/component/refresh`, // 集群配置刷新队列
    GET_TASK: `${BASE_URI}/task/getTaskById`, // 获取任务通过任务ID
    GET_SUB_TASK: `${BASE_URI}/task/getFlowWorkSubTasks`, // 获取工作流的子任务
    GET_TASK_TYPES: `${BASE_URI}/task/getSupportJobTypes`, // 获取项目支持的任务类型
    PUBLISH_TASK: `${BASE_URI}/task/publishTask`, // 发布任务至调度
    GET_CUSTOM_TASK_PARAMS: `${BASE_URI}/task/getSysParams`, // 获取全局的系统参数
    FROZEN_TASK: `${BASE_URI}/task/frozenTask`, // 冻结/解冻任务
    CONVERT_SYNC_T0_SCRIPT_MODE: `${BASE_URI}/task/guideToTemplate `, // 转换数据同步从向导到脚本模式
    GET_SYNC_SCRIPT_TEMPLATE: `${BASE_URI}/task/getJsonTemplate`, // 获取数据同步脚本模式的模版
    EXEC_SQL_IMMEDIATELY: `${BASE_URI}/batchJob/startSqlImmediately`, // 立即执行 SQL
    STOP_SQL_IMMEDIATELY: `${BASE_URI}/batchJob/stopSqlImmediately`, // 停止执行 SQL
    SELECT_SQL_RESULT_DATA: `${BASE_URI}/batchSelectSql/selectData`, // 轮询调度查询sql结果
    EXEC_DATA_SYNC_IMMEDIATELY: `${BASE_URI}/batchJob/startSyncImmediately`, // 立即执行数据同步
    STOP_DATA_SYNC_IMMEDIATELY: `${BASE_URI}/batchJob/stopSyncJob`, // 停止执行数据同步
    SELECT_DATA_SYNC_RESULT: `${BASE_URI}/batchJob/getSyncTaskStatus`, // 获取数据同步执行状态
    SELECT_SQL_LOG: `${BASE_URI}/batchSelectSql/selectRunLog`, // 轮询调度查询sql状态
    SELECT_SQL_STATUS: `${BASE_URI}/batchSelectSql/selectStatus`, // 轮询调度查询sql状态
    GET_INCREMENT_COLUMNS: `${BASE_URI}/task/getIncreColumn`, // 获取增量字段
    CHECK_HIVE_PARTITIONS: `${BASE_URI}/batchDataSource/getHivePartitions`, // 获取 hive 表分区值
    GET_OFFLINE_CATALOGUE: `${BASE_URI}/batchCatalogue/getCatalogue`, // 获取目录结构
    ADD_OFFLINE_CATALOGUE: `${BASE_URI}/batchCatalogue/addCatalogue`, // 添加目录
    DEL_OFFLINE_FOLDER: `${BASE_URI}/batchCatalogue/deleteCatalogue`, // 删除目录
    EDIT_OFFLINE_CATALOGUE: `${BASE_URI}/batchCatalogue/updateCatalogue`, // 更新目录
    ADD_OFFLINE_RESOURCE: `${BASE_URI}/resource/addResource`, // 添加资源
    REPLACE_OFFLINE_RESOURCE: `${BASE_URI}/resource/replaceResource`, // 替换资源
    ADD_OFFLINE_TASK: `${BASE_URI}/task/addOrUpdateTask`, // 添加任务
    EDIT_TASK: `${BASE_URI}/task/editTask`, // 更新任务中不涉及内容的部分
    SAVE_OFFLINE_JOBDATA: `${BASE_URI}/task/addOrUpdateTask`, // 更新任务
    ADD_OFFLINE_FUNCTION: `${BASE_URI}/batchFunction/addOrUpdateFunction`, // 添加函数
    GET_TABLE_INFO_BY_DATASOURCE: `${BASE_URI}/batchDataSource/getTableInfoByDataSource`, // 从目标表位置获取表格信息
    DEL_OFFLINE_TASK: `${BASE_URI}/task/deleteTask`, // 删除任务
    DEL_OFFLINE_RES: `${BASE_URI}/resource/deleteResource`, // 删除资源
    DEL_OFFLINE_FN: `${BASE_URI}/batchFunction/deleteFunction`, // 删除函数
    GET_FN_DETAIL: `${BASE_URI}/batchFunction/getFunction`, // 获取函数详情
    GET_RES_DETAIL: `${BASE_URI}/resource/getResourceById`, // 获取资源详情
    GET_HBASE_COLUMN_FAMILY: `${BASE_URI}/batch/batchDataSource/columnfamily`, // 获取Hbase数据表列族
    QUERY_TASKS: `${BASE_URI}/scheduleTaskShade/queryTasks`, // 获取离线任务管理列表
    GET_TASK_CHILDREN: `${BASE_URI}/scheduleTaskTaskShade/displayOffSpring`, // 获取任务子节点
    GET_ROOT_WORKFLOW_TASK: `${BASE_URI}/scheduleTaskTaskShade/getWorkFlowTopTask`, // 获取 task 的根节点
    GET_TASK_LOG: `${BASE_URI}/action/queryJobLog`, // 获取离线任务日志
    QUERY_JOBS: `${BASE_URI}/scheduleJob/queryJobs`, // 获取周期实例列表
    GET_SUB_JOBS: `${BASE_URI}/scheduleJob/getRelatedJobs`, // 获取工作流实例的子实例
    PATCH_TASK_DATA: `${BASE_URI}/fill/fillData`, // 补数据
    BATCH_STOP_JOBS: `${BASE_URI}/action/batchStopJobs`, // 批量停止任务
    BATCH_RESTART_AND_RESUME_JOB: `${BASE_URI}/action/restartJob`, // 批量重启并恢复任务
    GET_FILL_DATA: `${BASE_URI}/fill/queryFillDataList`, // 获取补数据实例列表
    GET_FILL_DATA_DETAIL: `${BASE_URI}/fill/queryFillDataJobList`, // 获取补数据实例详情
    GET_JOB_CHILDREN: `${BASE_URI}/scheduleJobJob/displayOffSpring`, // 获取任务上下游关系
    GET_ROOT_WORKFLOW_JOB: `${BASE_URI}/scheduleJobJob/getWorkFlowTopJob`, // 获取 job 的根节点
    GET_TASK_PERIODS: `${BASE_URI}/scheduleJob/queryDisplayPeriods`, // 转到前后周期实例
    QUERY_JOB_STATISTICS: `${BASE_URI}/scheduleJob/queryJobsStatusStatistics`, // 查询Job统计
    STOP_FILL_DATA_JOBS: `${BASE_URI}/action/stopFillDataJobs`, // 停止补数据任务
    USER_QUERYUSER: `${BASE_URI}/user/queryUser`, // 获取负责人
    GET_TASK_JOB_WORKFLOW_NODES: `${BASE_URI}/scheduleJobJob/displayOffSpringWorkFlow`, // 刷新任务实例获取工作流节点
    GET_TYPE_ORIGIN_DATA: `${BASE_URI}/dataSource/manager/listDataSourceBaseInfo`, // 获取类型数据源
    LIST_TABLE_BY_SCHEMA: `${BASE_URI}/dataSource/manager/listTablesBySchema`,
    POLL_PREVIEW: `${BASE_URI}/dataSource/manager/pollPreview`,
    GET_DATA_PREVIEW: `${BASE_URI}/dataSource/manager/getTopicData`, // 获取kafka topic预览数据
    SAVE_TASK: `${BASE_URI}/task/addOrUpdateTask`, // 添加或者更新任务
    GET_TOPIC_TYPE: `${BASE_URI}/dataSource/manager/getKafkaTopics`, // 获取Topic
    GET_STREAM_TABLECOLUMN: `${BASE_URI}/dataSource/addDs/tablecolumn`, // 输出tablecolumn
    GET_TIMEZONE_LIST: `${BASE_URI}/flink/getAllTimeZone`, // 获取源表中的时区列表
    CONVERT_TO_SCRIPT_MODE: `${BASE_URI}/task/guideToTemplate `, // 转换向导到脚本模式
    IS_OPEN_CDB: `${BASE_URI}/dataSource/manager/isOpenCdb`,
    GET_PDB_LIST: `${BASE_URI}/dataSource/manager/listOraclePdb`,
    GET_TOPIC_PARTITION_NUM: `${BASE_URI}/dataSource/manager/getTopicPartitionNum`, // 获取最大读取并发数
    GET_ALL_STRATEGY: `${BASE_URI}/streamStrategy/getStrategyByProjectId`, // 获取所有策略
    GET_SCHEMA_TABLE_COLUMN: `${BASE_URI}/dataSource/manager/listPollTableColumn`,
    GET_SLOT_LIST: `${BASE_URI}/dataSource/manager/listSlot`, // 获取slot列表
    GET_BINLOG_LIST_BY_SOURCE: `${BASE_URI}/dataSource/manager/getBinLogListBySource`, // 获取binlog列表
    SQL_FORMAT: `${BASE_URI}/flink/sqlFormat`, // 格式化 sql
    GRAMMAR_CHECK: `${BASE_URI}/flink/grammarCheck`, // 语法检查
    GET_TASK_LIST: `${BASE_URI}/flink/getTaskList`, // 获取实时任务管理列表
    GET_STATUS_COUNT: `${BASE_URI}/flink/getStatusCount`, // 获取实时任务管理状态统计
    START_TASK: `${BASE_URI}/flink/start`, // 续跑 FlinkSQL 任务
    START_COLLECTION_TASK: `${BASE_URI}/task/startTask`, // 续跑实时采集任务
    GET_TASK_MANAGER_LOG: `${BASE_URI}/flink/getTaskManagerLog`, // 获取 Task Manager日志
    GET_JOB_MANAGER_LOG: `${BASE_URI}/flink/getJobManagerLog`, // 获取运行中的实时任务日志
    LIST_TASK_MANAGER: `${BASE_URI}/flink/listTaskManagerByTaskId`, // 获取 Task Manager 列表
    GET_TASK_LOGS: `${BASE_URI}/flink/getLogsByTaskId`, // 获取非运行中的实时任务日志
    GET_TASK_FAILOVER_LOG: `${BASE_URI}/flink/getFailoverLogsByTaskId`, // 获取任务告警日志
    GET_HISTORY_LOG: `${BASE_URI}/flink/getJobHistoryList`, // 获取历史日志
    DOWNLOAD_HISTORY_LOG: `${BASE_URI}/download/streamDownload/downloadHistoryLog`, // 下载历史日志
    GET_TASK_METRICS: `${BASE_URI}/streamJobMetric/getTaskMetrics`, // 获取指标
    GET_METRIC_VALUES: `${BASE_URI}/streamJobMetric/values`, // 获取所有指标
    CHECK_SOURCE_STATUS: `${BASE_URI}/dataSource/manager/getDataSourceLinkStatus`, // 获取任务的异常数据源
    QUERY_TASK_METRICES: `${BASE_URI}/streamJobMetric/queryTaskMetrics`, // 查询指标数据
    GET_LIST_HISTORY: `${BASE_URI}/scheduleJob/listHistory`, // 获取 CheckPoint 下拉列表
    LIST_CHECK_POINT: `${BASE_URI}/checkpoint/listCheckPoint`, // 获取 CheckPointPath 下拉列表
    STOP_TASK: `${BASE_URI}/flink/stopTask`, // 停止任务
    GET_TASK_JSON: `${BASE_URI}/flink/getTaskJson`, // 拓扑图
    GET_TASK_SQL_TEXT: `${BASE_URI}/flink/getTaskSqlText`, // 获取当前任务的 Sql 内容
    TABLE_LIST: `${BASE_URI}/dataSource/addDs/tablelist`, // 获取数据源列表
    GET_TABLE_COLUMN: `${BASE_URI}/dataSource/addDs/tablecolumn`, // 获取数据库表字段信息
    GET_COLUMN_FOR_SYNCOPATE: `${BASE_URI}/dataSource/addDs/columnForSyncopate`, // 获取表切分键信息
    GET_HIVE_PARTITIONS: `${BASE_URI}/dataSource/addDs/getHivePartitions`, // 获取分区信息
    GET_DATA_SOURCE_PREVIEW: `${BASE_URI}/dataSource/addDs/preview`, // 数据同步任务预览数据
    GET_ALL_SCHEMAS: `${BASE_URI}/dataSource/addDs/getAllSchemas`, // 数据同步任务获取 schema
    GET_DATA_SOURCE_PAGE: `${BASE_URI}/dataSource/manager/page`, // 获取数据源表格列表
    GET_TYPE_LIST: `${BASE_URI}/dataSource/manager/type/list`, // 获取数据源分类列表
    DELETE_SOURCE: `${BASE_URI}/dataSource/manager/delete`, // 删除数据源
    QUERY_DATA_SOURCE_CLASSIFY_LIST: `${BASE_URI}/dataSource/addDs/queryDsClassifyList`, // 获取数据源分类类目列表
    QUERY_LIST_BY_CLASSIFY: `${BASE_URI}/dataSource/addDs/queryDsTypeByClassify`, // 根据数据源分类获取数据源
    QUERY_VERSION_BY_CLASSIFY: `${BASE_URI}/dataSource/addDs/queryDsVersionByType`, // 获取数据源版本列表
    QUERY_TEMPLATE_BY_VERSION: `${BASE_URI}/dataSource/dsForm/findFormByTypeVersion`, // 获取 template 渲染模板
    ADD_DATA_SOURCE: `${BASE_URI}/dataSource/addDs/addOrUpdateSource`, // 保存数据源
    UPLOAD_DATA_SOURCE_WITH_KERBEROS: `${BASE_URI}/dataSource/addDs/addOrUpdateSourceWithKerberos`, // 保存带有 kerberos 的数据源
    TEST_CONNECT_IN_DATA_SOURCE: `${BASE_URI}/dataSource/addDs/testCon`, // 测试连通性
    TEST_KERBEROS_IN_DATA_SOURCE: `${BASE_URI}/dataSource/addDs/testConWithKerberos`, // 测试带有 kerberos 的连通性
    GET_DATA_SOURCE_DETAIL: `${BASE_URI}/dataSource/manager/detail`, // 获取数据源详情
    UPLOAD_CODE: `${BASE_URI}/dataSource/addDs/getPrincipalsWithConf`, // 上传 kerberos 文件
    GET_CREATE_TARGET_TABLE: `${BASE_URI}/dataSource/addDs/getCreateTargetTableSql`, // 获取创建表的默认语句
    CREATE_DDL_TABLE: `${BASE_URI}/dataSource/addDs/ddlCreateTable`, // 创建 ddl 的表
    BATCH_STOP_JOBS_BY_DATE: `${BASE_URI}/action/stopJobByCondition`, // 按照业务日期杀任务
    ALL_PRODUCT_GLOBAL_SEARCH: `${BASE_URI}/task/allProductGlobalSearch`, // 所有产品的已提交任务查询
    GET_COMPONENT_MODELS: `${BASE_URI}/component/componentModels`, // 获取能配置的组件信息
    GET_COMPONENT_INFO: `${BASE_URI}/component/getComponentInfo`, // 获取组件配置信息
    GET_TENANT_COMPONENT_LIST: `${BASE_URI}/tenantComponent/selectTenantComponentList`, // 获取当前租户配置的任务组件运行信息
    SAVE_TENANT_COMPONENT_INFO: `${BASE_URI}/tenantComponent/saveTenantComponentInfo`, // 保存组件运行schema信息
    GET_SCHEMA_LIST_BY_COMPONENT: `${BASE_URI}/tenantComponent/getByTenantAndTaskType`, // 获取任务类型可配置的shema
    GET_RESOUCE_LOCATION: `${BASE_URI}/batchCatalogue/getCatalogueIds`, // 获取当前目录的位置
    VALIDATE_REPEAT_TASK_NAME: `${BASE_URI}/task/checkTaskNameRepeat`, // 校验当前任务名称是否重名
    QUERY_SYNC_DATA_SOURCE: `${BASE_URI}/task/getSyncProperties`, // 获取当前数据同步任务的数据
    QUERY_SUPPORT_SOURCE: `${BASE_URI}/dataSource/manager/support`, // 获取当前各组件支持的数据源
    GET_RESOURCES_BY_TENANT: `${BASE_URI}/console/clusterResourcesByTenantId`, // 基于租户 ID 获取集群信息
    GET_ALL_DATA_SOURCE: `${BASE_URI}/dataSource/manager/total`, // 获取全部数据源
    GET_COMPONENT_VERSION: `${BASE_URI}/task/getComponentVersionByTaskType`, // 获取当前任务支持的版本
    GET_FTP_COLUMNS: `${BASE_URI}/task/parsing_ftp_columns`, // 获取 FTP 的列
};
