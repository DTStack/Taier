import { TASK_TYPE_ENUM } from '@/constant';
import http from './http';
import req from './request';

export default {
    // 获取节点下拉
    getNodeAddressSelect(params?: any) {
        return http.post(req.GET_NODEADDRESS_SELECT, params);
    },
    // 获取类型数据源
    getTypeOriginData(params: any) {
        return http.post(req.GET_TYPE_ORIGIN_DATA, params);
    },
    listTablesBySchema(params: any) {
        return http.post(req.LIST_TABLE_BY_SCHEMA, params);
    },
    // 获取kafka topic预览数据
    getDataPreview(params: any) {
        return http.post(req.GET_DATA_PREVIEW, params);
    },
    pollPreview(params: any) {
        return http.post(req.POLL_PREVIEW, params);
    },
    // 添加或更新任务
    saveTask(params: any) {
        return http.post(req.SAVE_TASK, params);
    },
    // 获取Topic
    getTopicType(params: any) {
        return http.post(req.GET_TOPIC_TYPE, params);
    },
    getStreamTableColumn(params: { schema: string; sourceId: number; tableName: string; flinkVersion: string }) {
        return http.post(req.GET_STREAM_TABLECOLUMN, params);
    },
    // 获取源表中的时区列表
    getTimeZoneList(params?: any) {
        return http.post(req.GET_TIMEZONE_LIST, params);
    },
    // 转换向导到脚本模式
    convertToScriptMode(params: any) {
        return http.post(req.CONVERT_TO_SCRIPT_MODE, params);
    },
    checkSyntax(params: any) {
        return http.post(req.GRAMMAR_CHECK, params);
    },
    sqlFormat(params: any) {
        return http.post(req.SQL_FORMAT, params);
    },
    getTaskList(params: any) {
        return http.post(req.GET_TASK_LIST, params);
    },
    getStatusCount(params: any) {
        return http.post(req.GET_STATUS_COUNT, params);
    },
    startTask(params: any) {
        return http.post(req.START_TASK, params);
    },
    startCollectionTask(params: any) {
        return http.post(req.START_COLLECTION_TASK, params);
    },
    getTaskManagerLog(params: any) {
        return http.post(req.GET_TASK_MANAGER_LOG, params);
    },
    getJobManagerLog(params: any) {
        return http.post(req.GET_JOB_MANAGER_LOG, params);
    },
    listTaskManager(params: any) {
        return http.post(req.LIST_TASK_MANAGER, params);
    },
    getTaskLogs(params: any) {
        return http.post(req.GET_TASK_LOGS, params);
    },
    // failover 日志
    getFailoverLogsByTaskId(params: any) {
        return http.post(req.GET_TASK_FAILOVER_LOG, params);
    },
    getHistoryLog(params: any) {
        return http.post(req.GET_HISTORY_LOG, params);
    },
    isOpenCdb(params: { dataInfoId: number }) {
        return http.post(req.IS_OPEN_CDB, params);
    },
    getPDBList(params: { dataInfoId: number; searchKey?: string }) {
        return http.post(req.GET_PDB_LIST, params);
    },
    // 数据开发 - 获取启停策略列表
    getAllStrategy() {
        return http.post(req.GET_ALL_STRATEGY);
    },
    getTopicPartitionNum(params: any) {
        return http.post(req.GET_TOPIC_PARTITION_NUM, params);
    },
    getSchemaTableColumn(params: any) {
        return http.post(req.GET_SCHEMA_TABLE_COLUMN, params);
    },
    getSlotList(params: any) {
        return http.post(req.GET_SLOT_LIST, params);
    },
    getBinlogListBySource(params: any) {
        return http.post(req.GET_BINLOG_LIST_BY_SOURCE, params);
    },
    // 获取指标
    getTaskMetrics(params: { taskId: number; timespan: string; end: number; chartNames: string[] }) {
        return http.post(req.GET_TASK_METRICS, params);
    },
    getMetricValues(params: { taskId: number }) {
        // 获取所有指标
        return http.get(req.GET_METRIC_VALUES, params);
    },
    checkSourceStatus(params: { taskId: number }) {
        // 获取任务的异常数据源
        return http.post(req.CHECK_SOURCE_STATUS, params);
    },
    queryTaskMetrics(params: { taskId: number; chartName: string; timespan: string; end: number }) {
        // 查询指标数据
        return http.post(req.QUERY_TASK_METRICES, params);
    },
    getListHistory(params: any) {
        return http.post(req.GET_LIST_HISTORY, params);
    },
    listCheckPoint(params: any) {
        return http.post(req.LIST_CHECK_POINT, params);
    },
    stopTask(params: any) {
        return http.post(req.STOP_TASK, params);
    },
    getTaskJson(params: any) {
        return http.post(req.GET_TASK_JSON, params);
    },
    getTaskSqlText(params: any) {
        return http.post(req.GET_TASK_SQL_TEXT, params);
    },
    addTenant(params: any) {
        return http.post(req.ADD_TENANT, params);
    },
    switchTenant(params: { tenantId: number }) {
        return http.post(req.SWITCH_TENANT, params);
    },
    login(params: { username: string; password: string }) {
        return http.postAsFormData(req.LOGIN, params);
    },
    addCluster(params: { clusterName: string }) {
        return http.post(req.ADD_CLUSTER, params); // 新增集群
    },
    getClusterInfo(params: { clusterId: number | string }) {
        return http.get(req.GET_CLUSTER_INFO, params);
    },
    uploadResource(params: { fileName: any; componentType: any }) {
        return http.postAsFormData(req.UPLOAD_RESOURCE, params);
    },
    deleteComponent(params: { componentId: number }) {
        return http.post(req.DELETE_COMPONENT, params); // 删除组件
    },
    deleteCluster(params: { clusterId: number }) {
        return http.post(req.DELETE_CLUSTER, params);
    },
    testConnect(params: { clusterId: number; componentType: number; versionName: string }) {
        return http.post(req.TEST_CONNECT, params);
    },
    testConnects<T>(params: { clusterId: number }) {
        return http.post<T>(req.TEST_CONNECTS, params);
    },
    closeKerberos(params: { componentId: number }) {
        return http.post(req.CLOSE_KERBEROS, params);
    },
    getVersionData(params?: any) {
        return http.get(req.GET_VERSION, params);
    },
    saveComponent(params: any) {
        return http.postAsFormData(req.SAVE_COMPONENT, params);
    },
    parseKerberos(params: any) {
        return http.postAsFormData(req.PARSE_KERBEROS, params);
    },
    getClusterList(params: { currentPage: number; pageSize: number }) {
        return http.post(req.GET_CLUSTER_LIST, params);
    },
    getTenantList(params?: any) {
        return http.get(req.GET_TENANT_LIST, params);
    },
    getMetaComponent(params?: any) {
        return http.get(req.GET_META_COMPONENT, params);
    },
    // 获取存储组件列表
    getComponentStore(params: any) {
        return http.post(req.GET_COMPONENTSTORE, params);
    },
    // 上传kerberos文件
    uploadKerberos(params: any) {
        return http.postAsFormData(req.UPLOAD_KERBEROS, params);
    },
    // 更新krb5.conf文件
    updateKrb5Conf(params: { krb5Content: string }) {
        return http.post(req.UPDATE_KRB5CONF, params);
    },
    // 概览-获取集群
    getClusterDetail(params: any) {
        return http.post(req.GET_CLUSTER_DETAIL, params);
    },
    // 明细-杀死选中或者杀死全部任务
    killTasks(params: { jobResource: string; nodeAddress: string; stage: number; jobIdList?: any[] }) {
        return http.post(req.KILL_TASKS, params);
    },
    killAllTask(params: { jobResource: string; nodeAddress: string }) {
        return http.post(req.KILL_ALL_TASK, params);
    },
    stickJob(params: { jobId: string; jobResource: string }) {
        return http.post(req.JOB_STICK, params);
    },
    // 查看明细 和搜索条件
    getViewDetail(params: {
        stage: number;
        jobResource: string;
        nodeAddress?: string;
        currentPage: number;
        pageSize: number;
    }) {
        return http.post(req.GET_VIEW_DETAIL, params);
    },
    getClusterResources(params: any) {
        return http.post(req.GET_CLUSTER_RESOURCES, params);
    },
    getLoadTemplate<T>(params: any) {
        return http.post<T>(req.GET_LOADTEMPLATE, params);
    },
    getAllCluster(params?: any) {
        return http.get(req.GET_ALL_CLUSTER, params);
    },
    getEnginesByCluster(params?: any) {
        return http.get(req.GET_ENGINES_BY_CLUSTER, params);
    },
    searchTenant(params: any) {
        return http.post(req.SEARCH_TENANT, params);
    },
    bindTenant(params: any) {
        return http.post(req.BIND_TENANT, params);
    },
    switchQueue(params: any) {
        return http.post(req.SWITCH_QUEUE, params);
    },
    refreshQueue(params: { clusterName: string }) {
        return http.post(req.REFRESH_QUEUE, params);
    },
    getRetainDBList(params?: any) {
        return http.post(req.GET_RETAINDB_LIST, params);
    },
    convertToHiveColumns(params: any) {
        return http.post(req.CONVERT_TO_HIVE_COLUMNS, params);
    },
    convertDataSyncToScriptMode(params: any) {
        return http.post(req.CONVERT_SYNC_T0_SCRIPT_MODE, params);
    },
    getOfflineTaskByID<T = any>(params: any) {
        return http.post<T>(req.GET_TASK, params);
    },
    getOfflineSubTaskById<T = any>(params: any) {
        return http.post<T>(req.GET_SUB_TASK, params);
    },
    getCustomParams(params?: any) {
        return http.post(req.GET_CUSTOM_TASK_PARAMS, params);
    },
    getSyncTemplate(params: any) {
        return http.post(req.GET_SYNC_SCRIPT_TEMPLATE, params);
    },
    publishOfflineTask(params: any) {
        return http.post(req.PUBLISH_TASK, params);
    },
    getTaskTypes(params?: any) {
        return http.post(req.GET_TASK_TYPES, params);
    },
    getTableInfoByDataSource(params: any) {
        return http.post(req.GET_TABLE_INFO_BY_DATASOURCE, params);
    },
    execSQLImmediately<T>(params: any) {
        // 立即执行SQL
        return http.post<T>(req.EXEC_SQL_IMMEDIATELY, params);
    },
    stopSQLImmediately(params: any) {
        // 停止执行数据同步
        return http.post(req.STOP_SQL_IMMEDIATELY, params);
    },
    execDataSyncImmediately<T>(params: any) {
        // 立即执行数据同步
        return http.post<T>(req.EXEC_DATA_SYNC_IMMEDIATELY, params);
    },
    stopDataSyncImmediately(params: any) {
        // 停止执行SQL
        return http.post(req.STOP_DATA_SYNC_IMMEDIATELY, params);
    },
    getIncrementColumns(params: any, config?: any) {
        // 获取增量字段
        return http.post(req.GET_INCREMENT_COLUMNS, params, config);
    },
    getHivePartitions(params: any) {
        // 获取Hive分区
        return http.post(req.CHECK_HIVE_PARTITIONS, params);
    },
    selectRunLog<T>(params: any) {
        // 非数据同步接口获取日志
        return http.post<T>(req.SELECT_SQL_LOG, params);
    },
    /**
     * - 查询数据同步任务，SQL 执行结果
     * - 需要补充增量同步
     * @param {Object} params 请求参数
     * @param {Number} taskType 任务类型
     */
    selectExecResultData(params: any, taskType: any) {
        //
        const url =
            taskType && taskType === TASK_TYPE_ENUM.SYNC ? req.SELECT_DATA_SYNC_RESULT : req.SELECT_SQL_RESULT_DATA;
        return http.post(url, params);
    },
    selectExecResultDataSync(params: any) {
        // 数据同步接口获取结果表
        return http.post(req.SELECT_DATA_SYNC_RESULT, params);
    },
    selectStatus(params: any) {
        // 非数据同步接口轮训状态
        return http.post(req.SELECT_SQL_STATUS, params);
    },
    forzenTask(params: any) {
        return http.post(req.FROZEN_TASK, params);
    },
    getOfflineCatalogue(params: any) {
        return http.post(req.GET_OFFLINE_CATALOGUE, params);
    },
    addOfflineCatalogue(params: any) {
        return http.post(req.ADD_OFFLINE_CATALOGUE, params);
    },
    editOfflineCatalogue(params: any) {
        return http.post(req.EDIT_OFFLINE_CATALOGUE, params);
    },
    addOfflineResource(params: any) {
        return http.postAsFormData(req.ADD_OFFLINE_RESOURCE, params);
    },
    replaceOfflineResource(params: any) {
        return http.postAsFormData(req.REPLACE_OFFLINE_RESOURCE, params);
    },
    addOfflineTask(params: any) {
        return http.post(req.ADD_OFFLINE_TASK, params);
    },
    saveOfflineJobData(params: any) {
        return http.post(req.SAVE_OFFLINE_JOBDATA, params);
    },
    editTask(params: any) {
        return http.post(req.EDIT_TASK, params);
    },
    addOfflineFunction(params: any) {
        return http.post(req.ADD_OFFLINE_FUNCTION, params);
    },
    delOfflineTask(params: any) {
        return http.post(req.DEL_OFFLINE_TASK, params);
    },
    delOfflineFolder(params: any) {
        return http.post(req.DEL_OFFLINE_FOLDER, params);
    },
    delOfflineRes(params: any) {
        return http.post(req.DEL_OFFLINE_RES, params);
    },
    delOfflineFn(params: any) {
        return http.post(req.DEL_OFFLINE_FN, params);
    },
    getOfflineFn(params: any) {
        return http.post(req.GET_FN_DETAIL, params);
    },
    getOfflineRes(params: any) {
        return http.post(req.GET_RES_DETAIL, params);
    },
    getHBaseColumnFamily(params: any) {
        return http.post(req.GET_HBASE_COLUMN_FAMILY, params);
    },
    queryOfflineTasks(params: any) {
        return http.post(req.QUERY_TASKS, params);
    },
    getOfflineTaskLog(params: any) {
        // 获取离线任务日志
        return http.post(req.GET_TASK_LOG, params);
    },
    getOfflineTaskPeriods(params: any) {
        // 转到前后周期
        return http.post(req.GET_TASK_PERIODS, params);
    },
    queryJobs<T = any>(params: any) {
        return http.post<T>(req.QUERY_JOBS, params);
    },
    getSubJobs<T = any>(params: any) {
        return http.post<T>(req.GET_SUB_JOBS, params);
    },
    patchTaskData(params: any) {
        // 补数据
        return http.post(req.PATCH_TASK_DATA, params);
    },
    getTaskChildren<T = any>(params: any) {
        // 获取任务子节点
        return http.post<T>(req.GET_TASK_CHILDREN, params);
    },
    getRootWorkflowTask<T = any>(params: any) {
        return http.post<T>(req.GET_ROOT_WORKFLOW_TASK, params);
    },
    getFillData(params: any) {
        // 补数据搜索
        return http.post(req.GET_FILL_DATA, params);
    },
    getFillDataDetail<T = any>(params: any) {
        // 补数据详情
        return http.post<T>(req.GET_FILL_DATA_DETAIL, params);
    },

    batchStopJob(params: any) {
        // 批量停止任务
        return http.post(req.BATCH_STOP_JOBS, params);
    },
    batchRestartAndResume(params: any) {
        // 重启并恢复任务
        return http.post(req.BATCH_RESTART_AND_RESUME_JOB, params);
    },
    getJobChildren<T = any>(params: any) {
        // 获取任务子Job
        return http.post<T>(req.GET_JOB_CHILDREN, params);
    },
    getRootWorkflowJob<T = any>(params: any) {
        return http.post<T>(req.GET_ROOT_WORKFLOW_JOB, params);
    },
    queryJobStatics(params: any) {
        return http.post(req.QUERY_JOB_STATISTICS, params);
    },

    stopFillDataJobs(params: any) {
        return http.post(req.STOP_FILL_DATA_JOBS, params);
    },

    getPersonInCharge() {
        return http.post(req.USER_QUERYUSER);
    },
    /**
     * 获取工作流任务节点实例的子节点
     */
    getTaskJobWorkflowNodes(params: any) {
        return http.post(req.GET_TASK_JOB_WORKFLOW_NODES, params);
    },
    getOfflineTableList(params: any, config?: any) {
        return http.post(req.TABLE_LIST, params, config);
    },
    getOfflineTableColumn(params: any, config?: any) {
        return http.post(req.GET_TABLE_COLUMN, params, config);
    },
    getOfflineColumnForSyncopate(params: any, config?: any) {
        return http.post(req.GET_COLUMN_FOR_SYNCOPATE, params, config);
    },
    getHivePartitionsForDataSource(params: any, config?: any) {
        return http.post(req.GET_HIVE_PARTITIONS, params, config);
    },
    getDataSourcePreview(params: any) {
        return http.post(req.GET_DATA_SOURCE_PREVIEW, params);
    },
    getAllSchemas(params: any, config?: any) {
        return http.post(req.GET_ALL_SCHEMAS, params, config);
    },
    dataSourcepage(params: any) {
        return http.post(req.GET_DATA_SOURCE_PAGE, params);
    },
    typeList(params: any) {
        return http.post(req.GET_TYPE_LIST, params);
    },
    dataSourceDelete(params: any) {
        return http.post(req.DELETE_SOURCE, params);
    },
    queryDsClassifyList(params: any) {
        return http.post(req.QUERY_DATA_SOURCE_CLASSIFY_LIST, params);
    },
    queryDsTypeByClassify(params: any) {
        return http.post(req.QUERY_LIST_BY_CLASSIFY, params);
    },
    queryDsVersionByType(params: any) {
        return http.post(req.QUERY_VERSION_BY_CLASSIFY, params);
    },
    findTemplateByTypeVersion(params: any) {
        return http.post(req.QUERY_TEMPLATE_BY_VERSION, params);
    },
    addDatasource(params: any) {
        return http.post(req.ADD_DATA_SOURCE, params);
    },
    addOrUpdateSourceWithKerberos(params: any) {
        return http.postForm(req.UPLOAD_DATA_SOURCE_WITH_KERBEROS, params);
    },
    testCon(params: any) {
        return http.post(req.TEST_CONNECT_IN_DATA_SOURCE, params);
    },
    testConWithKerberos(params: any) {
        return http.postForm(req.TEST_KERBEROS_IN_DATA_SOURCE, params);
    },
    detail(params: any) {
        return http.post(req.GET_DATA_SOURCE_DETAIL, params);
    },
    uploadCode(params: any) {
        return http.postForm(req.UPLOAD_CODE, params);
    },
    getCreateTargetTable(params: any) {
        return http.post(req.GET_CREATE_TARGET_TABLE, params);
    },
    createDdlTable(params: any) {
        return http.post(req.CREATE_DDL_TABLE, params);
    },
    batchStopJobByDate(params: any) {
        // 按业务日期批量杀任务
        return http.post(req.BATCH_STOP_JOBS_BY_DATE, params);
    },
    allProductGlobalSearch(params: any) {
        return http.post(req.ALL_PRODUCT_GLOBAL_SEARCH, params);
    },
    getComponentModels() {
        return http.get(req.GET_COMPONENT_MODELS, {});
    },
    getComponentInfo(params: any) {
        return http.get(req.GET_COMPONENT_INFO, params);
    },
    getComponentSchemaConfig<T>(params: any) {
        return http.post<T>(req.GET_TENANT_COMPONENT_LIST, params);
    },
    saveComponentSchemaConfig(params: any) {
        return http.post(req.SAVE_TENANT_COMPONENT_INFO, params);
    },
    getSchemaListByComponent<T>(params: any) {
        return http.post<T>(req.GET_SCHEMA_LIST_BY_COMPONENT, params);
    },
    getResourceLocation<T = any>(params: any) {
        return http.post<T>(req.GET_RESOUCE_LOCATION, params);
    },
    validateRepeatTaskName<T = any>(params: any) {
        return http.post<T>(req.VALIDATE_REPEAT_TASK_NAME, params);
    },
    getSyncProperties<T = any>(params: any) {
        return http.post<T>(req.QUERY_SYNC_DATA_SOURCE, params);
    },
    getSupportSource<T = any>(params: any) {
        return http.post<T>(req.QUERY_SUPPORT_SOURCE, params);
    },
    getResourceByTenant(params: any) {
        return http.post(req.GET_RESOURCES_BY_TENANT, params);
    },
    getAllDataSource(params: any) {
        return http.post(req.GET_ALL_DATA_SOURCE, params);
    },
    getComponentVersionByTaskType<T = any>(params: any) {
        return http.post<T>(req.GET_COMPONENT_VERSION, params);
    },
    getFTPColumns<T = any>(params: any) {
        return http.post<T>(req.GET_FTP_COLUMNS, params);
    },
};
