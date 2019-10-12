import { RDOS_BASE_URL } from 'config/base';

export default {

    UNLOCK_FILE: `${RDOS_BASE_URL}/common/readWriteLock/getLock`, // 解锁文件

    // ========================= 离线任务请求 ========================= //
    SQL_FORMAT: `${RDOS_BASE_URL}/batch/batchTableInfo/sqlFormat`, // SQL格式化服务

    // ===== task模块 ===== //
    SAVE_TASK: `${RDOS_BASE_URL}/batch/batchTask/addOrUpdateTask`, // 添加或者更新任务
    RENAME_TASK: `${RDOS_BASE_URL}/batch/batchTask/renameTask`, // 任务重命名
    FORCE_UPDATE_TASK: `${RDOS_BASE_URL}/batch/batchTask/forceUpdate`, // 强制更新
    GET_TASK: `${RDOS_BASE_URL}/batch/batchTask/getTaskById`, // 获取任务通过任务ID
    DELETE_TASK: `${RDOS_BASE_URL}/batch/batchTask/deleteTask`, // 删除任务
    CLONE_TASK: `${RDOS_BASE_URL}/batch/batchTask/cloneTask`, // 克隆任务
    QUERY_CATA_TASK: `${RDOS_BASE_URL}/batch/batchTask/getLogsByTaskId`, // 任务,目录关键字搜索
    GET_TASKS_BY_PROJECT_ID: `${RDOS_BASE_URL}/batch/batchTask/getTasksByProjectId`, // 根据项目id获取任务列表
    GET_TASKS_BY_NAME: `${RDOS_BASE_URL}/batch/batchTask/getTasksByName`, // 根据项目id，任务名 获取任务列表
    QUERY_TASKS: `${RDOS_BASE_URL}/batch/batchTask/queryTasks`, // 任务管理 - 搜索
    GET_TASK_CHILDREN: `${RDOS_BASE_URL}/batch/batchTaskTask/displayOffSpring`, // 获取任务自己节点
    GET_TASK_PARENTS: `${RDOS_BASE_URL}/batch/batchTaskTask/displayForefathers`, // 获取任务父节点
    GET_TASK_LOG: `${RDOS_BASE_URL}/batch/batchServerLog/getLogsByJobId`, // 获取任务告警日志
    GLOBAL_SEARCH_TASK: `${RDOS_BASE_URL}/batch/batchTask/globalSearch`, // 全局搜索任务
    GET_TASK_TYPES: `${RDOS_BASE_URL}/batch/batchTask/getSupportJobTypes`, // 获取任务类型
    GET_ANALY_DTATSOURCE_LISTS: `${RDOS_BASE_URL}/batch/batchDataSource/getAnalysisSource`, // 获取DTinsightAnalytics数据源下数据
    PUBLISH_TASK: `${RDOS_BASE_URL}/batch/batchTask/publishTask`, // 发布任务
    GET_CUSTOM_TASK_PARAMS: `${RDOS_BASE_URL}/batch/batchTask/getSysParams`, // 获取任务自定义参数
    FROZEN_TASK: `${RDOS_BASE_URL}/batch/batchTask/frozenTask`, // 冻结/解冻任务
    TASK_VERSION_SCHEDULE_CONF: `${RDOS_BASE_URL}/batch/batchTask/taskVersionScheduleConf `,
    UPDATE_TASK_OWNER: `${RDOS_BASE_URL}/batch/batchTask/setOwnerUser `,
    CONVERT_SYNC_T0_SCRIPT_MODE: `${RDOS_BASE_URL}/batch/batchTask/guideToTemplate `, // 转换数据同步从向导到脚本模式
    // ===== 脚本管理 ===== //
    SAVE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/addOrUpdateScript`, // 保存脚本
    FORCE_UPDATE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/forceUpdate`, // 强制更新
    EXEC_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/startSqlImmediately`, // 执行脚本
    STOP_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/stopSqlImmediately`, // 停止执行
    DELETE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/deleteScript`, // 删除脚本
    GET_SCRIPT_BY_ID: `${RDOS_BASE_URL}/batch/batchScript/getScriptById`, // 根据脚本获取ID
    GET_SCRIPT_TYPES: `${RDOS_BASE_URL}/batch/batchScript/getTypes`, // 脚本类型

    // ===== Job调度模块 ===== //
    QUERY_JOBS: `${RDOS_BASE_URL}/batch/batchJob/queryJobs`, // 任务运维 - 补数据搜索
    GET_JOB_BY_ID: `${RDOS_BASE_URL}/batch/batchJob/getJobById`, // 任务运维 - 调度任务详情
    GET_JOB_GRAPH: `${RDOS_BASE_URL}/batch/batchJob/getJobGraph`, // 今天、昨天、月平均折线图数据
    GET_JOB_STATISTICS: `${RDOS_BASE_URL}/batch/batchJob/getStatusCount`, // 实时任务个状态数量统计
    GET_JOB_TOP_TIME: `${RDOS_BASE_URL}/batch/batchJob/runTimeTopOrder`, // 离线任务运行时长top排序
    GET_JOB_TOP_ERROR: `${RDOS_BASE_URL}/batch/batchJob/errorTopOrder`, // 离线任务错误top排序
    PATCH_TASK_DATA: `${RDOS_BASE_URL}/batch/batchJob/fillTaskData`, // 补数据
    OPERA_RECORD_DATA: `${RDOS_BASE_URL}/batch/batchTaskRecord/queryRecords`, // 操作记录
    QUERY_PATCH_TASK_DATA: `${RDOS_BASE_URL}/batch/batchJob/queryBugJobs`, // 补数据搜索
    START_JOB: `${RDOS_BASE_URL}/batch/batchJob/loadDataJob`, // 启动任务
    STOP_JOB: `${RDOS_BASE_URL}/batch/batchJob/stopJob`, // 停止任务
    BATCH_STOP_JOBS: `${RDOS_BASE_URL}/batch/batchJob/batchStopJobs`, // 停止任务
    BATCH_STOP_JOBS_BY_DATE: `${RDOS_BASE_URL}/batch/batchJob/stopJobByCondition`, // 按照业务日期杀任务
    RESTART_AND_RESUME_JOB: `${RDOS_BASE_URL}/batch/batchJob/restartJobAndResume`, // 重启并恢复任务
    BATCH_RESTART_AND_RESUME_JOB: `${RDOS_BASE_URL}/batch/batchJob/batchRestartJobAndResume`, // 批量重启
    GET_FILL_DATA: `${RDOS_BASE_URL}/batch/batchJob/getFillDataJobInfoPreview`, // 获取补数据
    GET_FILL_DATE: `${RDOS_BASE_URL}/batch/batchJob/getFillDataBizDay`, // 补数据指定名称下的日期列表
    GET_FILL_DATA_DETAIL: `${RDOS_BASE_URL}/batch/batchJob/getFillDataDetailInfo`, // 获取补数据详情
    GET_JOB_CHILDREN: `${RDOS_BASE_URL}/batch/batchJobJob/displayOffSpring`, // 获取子job
    GET_TASK_PERIODS: `${RDOS_BASE_URL}/batch/batchJob/displayPeriods`, // 转到前后周期实例
    GET_JOB_PARENT: `${RDOS_BASE_URL}/batch/batchJobJob/displayForefathers`, // 获取父节点
    GET_TASK_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchTaskTaskShade/getAllFlowSubTasks`, // 获取工作流节点
    GET_TASK_JOB_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchJobJob/displayOffSpringWorkFlow`, // 获取工作流节点
    CHECK_IS_LOOP: `${RDOS_BASE_URL}/batch/batchTask/checkIsLoop`,
    GET_JOB_RUNTIME_INFO: `${RDOS_BASE_URL}/batch/batchJob/jobDetail`, // 获取任务调度详情
    QUERY_JOB_STATISTICS: `${RDOS_BASE_URL}/batch/batchJob/queryJobsStatusStatistics`, // 查询Job统计
    QUERY_JOB_SUB_NODES: `${RDOS_BASE_URL}/batch/batchJob/getAllChildJobWithSameDay`, // 查询子job子节点
    STATISTICS_TASK_RUNTIME: `${RDOS_BASE_URL}/batch/batchJob/statisticsTaskRecentInfo`, // 统计任务运行信息
    STOP_FILL_DATA_JOBS: `${RDOS_BASE_URL}/batch/batchJob/stopFillDataJobs`, // 停止补数据任务
    GET_SYNC_SCRIPT_TEMPLATE: `${RDOS_BASE_URL}/batch/batchTask/getJsonTemplate`, // 获取数据同步脚本模式的模版
    GET_RESTART_JOBS: `${RDOS_BASE_URL}/batch/batchJob/getRestartChildJob`, // 获取restart job列表
    DOWNLOAD_SQL_RESULT: `${RDOS_BASE_URL}/download/batch/batchDownload/downloadSqlExeResult`, // 下载运行结果
    EXEC_SQL_IMMEDIATELY: `${RDOS_BASE_URL}/batch/batchJob/startSqlImmediately`, // 立即执行SQL
    STOP_SQL_IMMEDIATELY: `${RDOS_BASE_URL}/batch/batchJob/stopSqlImmediately`, // 停止执行SQL
    SELECT_SQL_RESULT_DATA: `${RDOS_BASE_URL}/batch/batchSelectSql/selectData`, // 轮询调度查询sql结果
    EXEC_DATA_SYNC_IMMEDIATELY: `${RDOS_BASE_URL}/batch/batchJob/startSyncImmediately`, // 立即执行数据同步
    STOP_DATA_SYNC_IMMEDIATELY: `${RDOS_BASE_URL}/batch/batchJob/stopSyncJob`, // 停止执行数据同步
    SELECT_DATA_SYNC_RESULT: `${RDOS_BASE_URL}/batch/batchJob/getSyncTaskStatus`, // 获取数据同步执行状态
    GET_INCREMENT_COLUMNS: `${RDOS_BASE_URL}/batch/batchDataSource/getIncreColumn`, // 获取增量字段
    CHECK_SYNC_MODE: `${RDOS_BASE_URL}/batch/batchDataSource/canSetIncreConf`, // 检测是否满足增量
    CHECK_HIVE_PARTITIONS: `${RDOS_BASE_URL}/batch/batchDataSource/getHivePartitions`, // 获取hive表分区值

    // ===== catalogue目录模块 ===== //
    // GET_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batch/streamCatalogue/getCatalogue`, // 离线报警记录数量统计
    GET_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchCatalogue/getCatalogue`,
    GET_OFFLINE_CATALOGUE_BY_LOCATION: `${RDOS_BASE_URL}/batch/batchCatalogue/getLocation`,
    ADD_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchCatalogue/addCatalogue`,
    EDIT_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchCatalogue/updateCatalogue`,
    ADD_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/upload/batch/batchResource/addResource`,
    REPLACE_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/upload/batch/batchResource/replaceResource`,
    ADD_OFFLINE_TASK: `${RDOS_BASE_URL}/batch/batchTask/addOrUpdateTask`,
    GET_OFFLINE_TASK: `${RDOS_BASE_URL}/batch/batchTask/getTaskById`,
    GET_OFFLINE_TASK_BY_NAME: `${RDOS_BASE_URL}/batch/batchTask/getDependencyTask`,
    GET_OFFLINE_DATASOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/list`,
    GET_OFFLINE_TABLELIST: `${RDOS_BASE_URL}/batch/batchDataSource/tablelist`,
    GET_OFFLINE_CUBEKYLININFO: `${RDOS_BASE_URL}/batch/batchDataSource/getKylinCubeinfo`,
    GET_OFFLINE_TABLECOLUMN: `${RDOS_BASE_URL}/batch/batchDataSource/tablecolumn`,
    GET_OFFLINE_COLUMNFORSYNCOPATE: `${RDOS_BASE_URL}/batch/batchDataSource/columnForSyncopate`,
    GET_OFFLINE_JOBDATA: `${RDOS_BASE_URL}/batch/batchDataSource/trace`,
    SAVE_OFFLINE_JOBDATA: `${RDOS_BASE_URL}/batch/batchTask/addOrUpdateTask`,
    ADD_OFFLINE_FUNCTION: `${RDOS_BASE_URL}/batch/batchFunction/addFunction`,
    LINK_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/linkDataSource`, // 关联映射数据源
    GET_WORKFLOW_RELATED_TASKS: `${RDOS_BASE_URL}/batch/batchTask/dealFlowWorkTask`, // 获取工作流的子任务
    GET_WORKFLOW_RELATED_JOBS: `${RDOS_BASE_URL}/batch/batchJob/getRelatedJobs`, // 获取工作流实例的子任务
    GET_WORKFLOW_FILLDATA_RELATED_JOBS: `${RDOS_BASE_URL}/batch/batchJob/getRelatedJobsForFillData`, // 补数据工作流子节点
    IS_NATIVE_HIVE: `${RDOS_BASE_URL}/batch/batchDataSource/isNativeHive`, // 校验是不是标准分区

    // 离线文件操作
    DEL_OFFLINE_TASK: `${RDOS_BASE_URL}/batch/batchTask/deleteTask`,
    DEL_OFFLINE_FOLDER: `${RDOS_BASE_URL}/batch/batchCatalogue/deleteCatalogue`,
    DEL_OFFLINE_RES: `${RDOS_BASE_URL}/batch/batchResource/deleteResource`,
    DEL_OFFLINE_FN: `${RDOS_BASE_URL}/batch/batchFunction/deleteFunction`,
    MOVE_OFFLINE_FN: `${RDOS_BASE_URL}/batch/batchFunction/moveFunction`,
    GET_FN_DETAIL: `${RDOS_BASE_URL}/batch/batchFunction/getFunction`,
    GET_RES_DETAIL: `${RDOS_BASE_URL}/batch/batchResource/getResourceById`,
    DATA_PREVIEW: `${RDOS_BASE_URL}/batch/batchDataSource/preview`,

    // ===== alarm告警模块 ===== //
    GET_ALARM_LIST: `${RDOS_BASE_URL}/batch/batchAlarm/getAlarmList`, // 获取报警规则
    ADD_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/createAlarm`, // 创建报警
    UPDATE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/updateAlarm`, // 更新报警
    CLOSE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/closeAlarm`, // 关闭报警
    OEPN_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/startAlarm`, // 开启报警
    DELETE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/deleteAlarm`, // 删除报警
    GET_ALARM_RECORDS: `${RDOS_BASE_URL}/batch/batchAlarmRecord/getAlarmRecordList`, // 获取报警记录
    ALARM_STATISTICS: `${RDOS_BASE_URL}/batch/batchAlarmRecord/countAlarm`, // 删除报警

    // ===== 数据源管理 ===== //
    SAVE_DATA_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/addOrUpdateSource`, // 添加或者更新数据源
    SAVE_DATA_SOURCE_KERBEROS: `${RDOS_BASE_URL}/upload/batch/batchDataSource/addOrUpdateSourceWithKerberos`, // 添加或者更新数据源当开启kerberos时启用该接口
    DELETE_DATA_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/deleteSource`, // 删除数据源
    QUERY_DATA_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/pageQuery`, // 查询数据源接口
    GET_DATA_SOURCE_BY_ID: `${RDOS_BASE_URL}/batch/batchDataSource/getBySourceId`, // 根据ID查询数据源接口
    TEST_DATA_SOURCE_CONNECTION: `${RDOS_BASE_URL}/batch/batchDataSource/checkConnection`, // 测试数据源连通性
    TEST_DATA_SOURCE_CONNECTION_KERBEROS: `${RDOS_BASE_URL}/upload/batch/batchDataSource/checkConnectionWithKerberos`, // 测试数据源连通性当开启kerberos时启用该接口
    GET_DATA_SOURCE_TYPES: `${RDOS_BASE_URL}/batch/batchDataSource/getTypes`, // 获取数据源类型列表
    GET_HBASE_COLUMN_FAMILY: `${RDOS_BASE_URL}/batch/batchDataSource/columnfamily`, // 获取Hbase数据表列族
    GET_TASK_LIST_OF_OFFLINE_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/getSourceTaskRef`, // 获取离线数据源的任务
    CHECK_IS_PERMISSION: `${RDOS_BASE_URL}/batch/batchDataSource/checkPermission`, // 检查是否有权限

    GET_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/getHiveCatalogue`, // 获取表目录
    ADD_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/addCatalogue`, // 增加目录
    DEL_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/deleteCatalogue`, // 删除目录
    UPDATE_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/updateCatalogue`, // 更新目录
    ADD_TABLE_TO_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/updateCatalogue`, // 添加表到数据类目
    DEL_TABLE_IN_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/deleteTableCatalogue`, // 删除数据类目中的表

    SAVE_SYNC_CONFIG: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/saveConfig`, // 保存整库同步配置
    GET_SYNC_HISTORY: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/list`, // 获取整库同步历史
    GET_SYNC_DETAIL: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/getDetail`, // 获取整库同步详情
    PUBLISH_SYNC_TASK: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/task`, // 发布单表
    CHECK_SYNC_CONFIG: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/checkTransformConfig`, // 检查高级配置
    CHECK_SYNC_PERMISSION: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/checkPermission`, // 检查同步历史是否有权限

    // ===== 项目统计 ===== //
    PROJECT_TABLE_COUNT: `${RDOS_BASE_URL}/batch/hiveTableCount/tableCount`, // 表总量
    PROJECT_STORE_COUNT: `${RDOS_BASE_URL}/batch/hiveTableCount/totalSize`, // 表总存储量
    PROJECT_STORE_TOP: `${RDOS_BASE_URL}/batch/hiveTableCount/projectSizeTopOrder`, // 项目占用排行
    PROJECT_TABLE_STORE_TOP: `${RDOS_BASE_URL}/batch/hiveTableCount/tableSizeTopOrder`, // 表占用排行
    PROJECT_DATA_OVERVIEW: `${RDOS_BASE_URL}/batch/hiveTableCount/dataHistory` // 数据趋势概览

}
