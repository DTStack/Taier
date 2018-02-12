const baseUrl = '/api';//APP_CONF.API_BASE_URL || ''; // 从config文件全局读取

export default {

    UNLOCK_FILE: `${baseUrl}/common/readWriteLock/getLock`, // 解锁文件

    // ========================= 离线任务请求 ========================= //
    SQL_FORMAT: `${baseUrl}/batch/sqlFormat/sqlFormat`, // SQL格式化服务

    // ===== task模块 ===== //
    SAVE_TASK: `${baseUrl}/batch/batchTask/addOrUpdateTask`, // 添加或者更新任务
    FORCE_UPDATE_TASK: `${baseUrl}/batch/batchTask/forceUpdate`, // 强制更新
    GET_TASK: `${baseUrl}/batch/batchTask/getTaskById`, // 获取任务通过任务ID
    DELETE_TASK: `${baseUrl}/batch/batchTask/deleteTask`, // 删除任务
    QUERY_CATA_TASK: `${baseUrl}/batch/batchTask/getLogsByTaskId`, // 任务,目录关键字搜索
    GET_TASKS_BY_PROJECT_ID: `${baseUrl}/batch/batchTask/getTasksByProjectId`, // 根据项目id获取任务列表
    GET_TASKS_BY_NAME: `${baseUrl}/batch/batchTask/getTasksByName`, // 根据项目id，任务名 获取任务列表
    QUERY_TASKS: `${baseUrl}/batch/batchTask/queryTasks`, // 任务管理 - 搜索
    GET_TASK_CHILDREN: `${baseUrl}/batch/batchTaskTask/displayOffSpring`, // 获取任务自己节点
    GET_TASK_PARENTS: `${baseUrl}/batch/batchTaskTask/displayForefathers`, // 获取任务父节点
    GET_TASK_LOG: `${baseUrl}/batch/batchServerLog/getLogsByJobId`, // 获取任务告警日志
    GLOBAL_SEARCH_TASK: `${baseUrl}/batch/batchTask/globalSearch`, // 全局搜索任务
    GET_TASK_TYPES: `${baseUrl}/batch/batchTask/getSupportJobTypes`, // 获取任务类型
    PUBLISH_TASK: `${baseUrl}/batch/batchTask/publishTask`, // 发布任务
    GET_CUSTOM_TASK_PARAMS: `${baseUrl}/batch/batchTask/getSysParams`, // 获取任务自定义参数

    // ===== 脚本管理 ===== //
    SAVE_SCRIPT: `${baseUrl}/batch/batchScript/addOrUpdateScript`, // 保存脚本
    FORCE_UPDATE_SCRIPT: `${baseUrl}/batch/batchScript/forceUpdate`, // 强制更新
    EXEC_SCRIPT: `${baseUrl}/batch/batchScript/startSqlImmediately`, // 执行脚本
    STOP_SCRIPT: `${baseUrl}/batch/batchScript/stopSqlImmediately`, // 停止执行
    DELETE_SCRIPT: `${baseUrl}/batch/batchScript/deleteScript`, // 删除脚本
    GET_SCRIPT_BY_ID: `${baseUrl}/batch/batchScript/getScriptById`, // 根据脚本获取ID
    GET_SCRIPT_TYPES: `${baseUrl}/batch/batchScript/getTypes`, // 脚本类型

    // ===== Job调度模块 ===== //
    QUERY_JOBS: `${baseUrl}/batch/batchJob/queryJobs`, // 任务运维 - 补数据搜索
    GET_JOB_BY_ID: `${baseUrl}/batch/batchJob/getJobById`, // 任务运维 - 调度任务详情
    GET_JOB_GRAPH: `${baseUrl}/batch/batchJob/getJobGraph`, // 今天、昨天、月平均折线图数据
    GET_JOB_STATISTICS: `${baseUrl}/batch/batchJob/getStatusCount`, // 实时任务个状态数量统计
    GET_JOB_TOP_TIME: `${baseUrl}/batch/batchJob/runTimeTopOrder`, // 离线任务运行时长top排序
    GET_JOB_TOP_ERROR: `${baseUrl}/batch/batchJob/errorTopOrder`, // 离线任务错误top排序
    PATCH_TASK_DATA: `${baseUrl}/batch/batchJob/fillTaskData`, // 补数据
    QUERY_PATCH_TASK_DATA: `${baseUrl}/batch/batchJob/queryBugJobs`, // 补数据搜索
    START_JOB: `${baseUrl}/batch/batchJob/loadDataJob`, // 启动任务
    STOP_JOB: `${baseUrl}/batch/batchJob/stopJob`, // 停止任务
    BATCH_STOP_JOBS: `${baseUrl}/batch/batchJob/batchStopJobs`, // 停止任务
    RESTART_AND_RESUME_JOB: `${baseUrl}/batch/batchJob/restartJobAndResume`, // 重启并恢复任务
    BATCH_RESTART_AND_RESUME_JOB: `${baseUrl}/batch/batchJob/batchRestartJobAndResume`, // 批量重启
    GET_FILL_DATA: `${baseUrl}/batch/batchJob/getFillDataJobInfoPreview`, // 获取补数据
    GET_FILL_DATE: `${baseUrl}/batch/batchJob/getFillDataBizDay`, // 补数据指定名称下的日期列表
    GET_FILL_DATA_DETAIL: `${baseUrl}/batch/batchJob/getFillDataDetailInfo`, // 获取补数据详情
    GET_JOB_CHILDREN: `${baseUrl}/batch/batchJobJob/displayOffSpring`, // 获取子job
    GET_JOB_PARENT: `${baseUrl}/batch/batchJobJob/displayForefathers`, // 获取父节点
    EXEC_SQL_IMMEDIATELY: `${baseUrl}/batch/batchJob/startSqlImmediately`, // 立即执行SQL
    STOP_SQL_IMMEDIATELY: `${baseUrl}/batch/batchJob/stopSqlImmediately`, // 停止执行SQL
    CHECK_IS_LOOP: `${baseUrl}/batch/batchTask/checkIsLoop`,
    GET_JOB_RUNTIME_INFO: `${baseUrl}/batch/batchJob/jobDetail`, // 获取任务调度详情
    QUERY_JOB_STATISTICS: `${baseUrl}/batch/batchJob/queryJobsStatusStatistics`, // 查询Job统计
    QUERY_JOB_SUB_NODES: `${baseUrl}/batch/batchJob/getAllChildJobWithSameDay`, // 查询子job子节点

    // ===== catalogue目录模块 ===== //
    // GET_OFFLINE_CATELOGUE: `${baseUrl}/batch/streamCatalogue/getCatalogue`, // 离线报警记录数量统计
    GET_OFFLINE_CATELOGUE: `${baseUrl}/batch/batchCatalogue/getCatalogue`,
    ADD_OFFLINE_CATELOGUE: `${baseUrl}/batch/batchCatalogue/addCatalogue`,
    EDIT_OFFLINE_CATELOGUE: `${baseUrl}/batch/batchCatalogue/updateCatalogue`,
    ADD_OFFLINE_RESOURCE: `${baseUrl}/upload/batch/batchResource/addResource`,
    ADD_OFFLINE_TASK: `${baseUrl}/batch/batchTask/addOrUpdateTask`,
    GET_OFFLINE_TASK: `${baseUrl}/batch/batchTask/getTaskById`,
    GET_OFFLINE_TASK_BY_NAME: `${baseUrl}/batch/batchTask/getDependencyTask`,
    GET_OFFLINE_DATASOURCE: `${baseUrl}/batch/batchDataSource/list`,
    GET_OFFLINE_TABLELIST: `${baseUrl}/batch/batchDataSource/tablelist`,
    GET_OFFLINE_TABLECOLUMN: `${baseUrl}/batch/batchDataSource/tablecolumn`,
    GET_OFFLINE_JOBDATA: `${baseUrl}/batch/batchDataSource/trace`,
    SAVE_OFFLINE_JOBDATA: `${baseUrl}/batch/batchTask/addOrUpdateTask`,
    ADD_OFFLINE_FUNCTION: `${baseUrl}/batch/batchFunction/addFunction`,

    // 离线文件操作
    DEL_OFFLINE_TASK: `${baseUrl}/batch/batchTask/deleteTask`,
    DEL_OFFLINE_FOLDER: `${baseUrl}/batch/batchCatalogue/deleteCatalogue`,
    DEL_OFFLINE_RES: `${baseUrl}/batch/batchResource/deleteResource`,
    DEL_OFFLINE_FN: `${baseUrl}/batch/batchFunction/deleteFunction`,
    MOVE_OFFLINE_FN: `${baseUrl}/batch/batchFunction/moveFunction`,
    GET_FN_DETAIL: `${baseUrl}/batch/batchFunction/getFunction`,
    GET_RES_DETAIL: `${baseUrl}/batch/batchResource/getResourceById`,
    DATA_PREVIEW: `${baseUrl}/batch/batchDataSource/preview`,

    // ===== alarm告警模块 ===== //
    GET_ALARM_LIST: `${baseUrl}/batch/batchAlarm/getAlarmList`, // 获取报警规则
    ADD_ALARM: `${baseUrl}/batch/batchAlarm/createAlarm`, // 创建报警
    UPDATE_ALARM: `${baseUrl}/batch/batchAlarm/updateAlarm`, // 更新报警
    CLOSE_ALARM: `${baseUrl}/batch/batchAlarm/closeAlarm`, // 关闭报警
    OEPN_ALARM: `${baseUrl}/batch/batchAlarm/startAlarm`, // 开启报警
    DELETE_ALARM: `${baseUrl}/batch/batchAlarm/deleteAlarm`, // 删除报警
    GET_ALARM_RECORDS: `${baseUrl}/batch/batchAlarmRecord/getAlarmRecordList`, // 获取报警记录
    ALARM_STATISTICS: `${baseUrl}/batch/batchAlarmRecord/countAlarm`, // 删除报警

    // ===== 数据源管理 ===== //
    SAVE_DATA_SOURCE: `${baseUrl}/batch/batchDataSource/addOrUpdateSource`, // 添加或者更新数据源
    DELETE_DATA_SOURCE: `${baseUrl}/batch/batchDataSource/deleteSource`, // 删除数据源
    QUERY_DATA_SOURCE: `${baseUrl}/batch/batchDataSource/pageQuery`, // 查询数据源接口
    GET_DATA_SOURCE_BY_ID: `${baseUrl}/batch/batchDataSource/getBySourceId`, // 根据ID查询数据源接口
    TEST_DATA_SOURCE_CONNECTION: `${baseUrl}/batch/batchDataSource/checkConnection`, // 测试数据源连通性
    GET_DATA_SOURCE_TYPES: `${baseUrl}/batch/batchDataSource/getTypes`, // 获取数据源类型列表
    GET_HBASE_COLUMN_FAMILY: `${baseUrl}/batch/batchDataSource/columnfamily`, // 获取Hbase数据表列族
    GET_TABLE_CATALOGUE: `${baseUrl}/batch/hiveCatalogue/getHiveCatalogue`, // 获取表目录
    ADD_TABLE_CATALOGUE: `${baseUrl}/batch/hiveCatalogue/addCatalogue`, // 增加目录
    DEL_TABLE_CATALOGUE: `${baseUrl}/batch/hiveCatalogue/deleteCatalogue`, // 删除目录
    UPDATE_TABLE_CATALOGUE: `${baseUrl}/batch/hiveCatalogue/updateHiveCatalogue`, // 更新目录
    ADD_TABLE_TO_CATALOGUE: `${baseUrl}/batch/hiveTableCatalogue/updateHiveCatalogue`, // 添加表到数据类目
    DEL_TABLE_IN_CATALOGUE: `${baseUrl}/batch/hiveTableCatalogue/deleteTableCatalogue`, // 删除数据类目中的表

    // ===== 项目统计 ===== //
    PROJECT_TABLE_COUNT: `${baseUrl}/batch/hiveTableCount/tableCount`, // 表总量
    PROJECT_STORE_COUNT: `${baseUrl}/batch/hiveTableCount/totalSize`, // 表总存储量
    PROJECT_STORE_TOP: `${baseUrl}/batch/hiveTableCount/projectSizeTopOrder`, // 项目占用排行
    PROJECT_TABLE_STORE_TOP: `${baseUrl}/batch/hiveTableCount/tableSizeTopOrder`, // 表占用排行
    PROJECT_DATA_OVERVIEW: `${baseUrl}/batch/hiveTableCount/dataHistory`, // 数据趋势概览
}
