/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { RDOS_BASE_URL } from './req';

const offlineReq = {
	UNLOCK_FILE: `${RDOS_BASE_URL}/common/readWriteLock/getLock`, // 解锁文件

	// ========================= 离线任务请求 ========================= //
	SQL_FORMAT: `${RDOS_BASE_URL}/batch/batchTableInfo/sqlFormat`, // SQL格式化服务

	// ===== task模块 ===== //
	SAVE_TASK: `${RDOS_BASE_URL}/task/addOrUpdateTask`, // 添加或者更新任务
	RENAME_TASK: `${RDOS_BASE_URL}/task/renameTask`, // 任务重命名
	FORCE_UPDATE_TASK: `${RDOS_BASE_URL}/task/forceUpdate`, // 强制更新
	GET_TASK: `${RDOS_BASE_URL}/task/getTaskById`, // 获取任务通过任务ID
	CLONE_TASK: `${RDOS_BASE_URL}/task/cloneTask`, // 克隆任务
	QUERY_CATA_TASK: `${RDOS_BASE_URL}/task/getLogsByTaskId`, // 任务,目录关键字搜索
	GET_TASKS_BY_PROJECT_ID: `${RDOS_BASE_URL}/task/getTasksByProjectId`, // 根据项目id获取任务列表
	GET_TASKS_BY_NAME: `${RDOS_BASE_URL}/task/getTasksByName`, // 根据项目id，任务名 获取任务列表
	QUERY_TASKS: `${RDOS_BASE_URL}/task/queryTasks`, // 任务管理 - 搜索
	GET_TASK_CHILDREN: `${RDOS_BASE_URL}/batchTaskTask/displayOffSpring`, // 获取任务自己节点
	GET_TASK_PARENTS: `${RDOS_BASE_URL}/batchTaskTask/displayForefathers`, // 获取任务父节点
	GET_TASK_LOG: `${RDOS_BASE_URL}/batchServerLog/getLogsByJobId`, // 获取任务告警日志
	GLOBAL_SEARCH_TASK: `${RDOS_BASE_URL}/task/globalSearch`, // 全局搜索任务
	GET_TASK_TYPES: `${RDOS_BASE_URL}/task/getSupportJobTypes`, // 获取任务类型
	GET_ANALY_DTATSOURCE_LISTS: `${RDOS_BASE_URL}/batchDataSource/getAnalysisSource`, // 获取DTinsightAnalytics数据源下数据
	PUBLISH_TASK: `${RDOS_BASE_URL}/task/publishTask`, // 发布任务
	GET_CUSTOM_TASK_PARAMS: `${RDOS_BASE_URL}/task/getSysParams`, // 获取任务自定义参数
	FROZEN_TASK: `${RDOS_BASE_URL}/task/frozenTask`, // 冻结/解冻任务
	TASK_VERSION_SCHEDULE_CONF: `${RDOS_BASE_URL}/task/taskVersionScheduleConf `,
	UPDATE_TASK_OWNER: `${RDOS_BASE_URL}/task/setOwnerUser `,
	CONVERT_SYNC_T0_SCRIPT_MODE: `${RDOS_BASE_URL}/task/guideToTemplate `, // 转换数据同步从向导到脚本模式
	// ===== 脚本管理 ===== //
	SAVE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/addOrUpdateScript`, // 保存脚本
	FORCE_UPDATE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/forceUpdate`, // 强制更新
	EXEC_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/startSqlImmediately`, // 执行脚本
	STOP_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/stopSqlImmediately`, // 停止执行
	DELETE_SCRIPT: `${RDOS_BASE_URL}/batch/batchScript/deleteScript`, // 删除脚本
	GET_SCRIPT_BY_ID: `${RDOS_BASE_URL}/batch/batchScript/getScriptById`, // 根据脚本获取ID
	GET_SCRIPT_TYPES: `${RDOS_BASE_URL}/batch/batchScript/getTypes`, // 脚本类型

	// ===== Job调度模块 ===== //
	QUERY_JOBS: `${RDOS_BASE_URL}/batchJob/queryJobs`, // 任务运维 - 补数据搜索
	GET_JOB_BY_ID: `${RDOS_BASE_URL}/batchJob/getJobById`, // 任务运维 - 调度任务详情
	GET_JOB_GRAPH: `${RDOS_BASE_URL}/batchJob/getJobGraph`, // 今天、昨天、月平均折线图数据
	GET_JOB_STATISTICS: `${RDOS_BASE_URL}/batchJob/getStatusCount`, // 实时任务个状态数量统计
	GET_JOB_TOP_TIME: `${RDOS_BASE_URL}/batchJob/runTimeTopOrder`, // 离线任务运行时长top排序
	GET_JOB_TOP_ERROR: `${RDOS_BASE_URL}/batchJob/errorTopOrder`, // 离线任务错误top排序
	PATCH_TASK_DATA: `${RDOS_BASE_URL}/batchJob/fillTaskData`, // 补数据
	OPERA_RECORD_DATA: `${RDOS_BASE_URL}/batchTaskRecord/queryRecords`, // 操作记录
	QUERY_PATCH_TASK_DATA: `${RDOS_BASE_URL}/batchJob/queryBugJobs`, // 补数据搜索
	START_JOB: `${RDOS_BASE_URL}/batchJob/loadDataJob`, // 启动任务
	STOP_JOB: `${RDOS_BASE_URL}/batchJob/stopJob`, // 停止任务
	BATCH_STOP_JOBS: `${RDOS_BASE_URL}/batchJob/batchStopJobs`, // 停止任务
	BATCH_STOP_JOBS_BY_DATE: `${RDOS_BASE_URL}/batchJob/stopJobByCondition`, // 按照业务日期杀任务
	RESTART_AND_RESUME_JOB: `${RDOS_BASE_URL}/batchJob/restartJobAndResume`, // 重启并恢复任务
	BATCH_RESTART_AND_RESUME_JOB: `${RDOS_BASE_URL}/batchJob/batchRestartJobAndResume`, // 批量重启
	GET_FILL_DATA: `${RDOS_BASE_URL}/batchJob/getFillDataJobInfoPreview`, // 获取补数据
	GET_FILL_DATE: `${RDOS_BASE_URL}/batchJob/getFillDataBizDay`, // 补数据指定名称下的日期列表
	GET_FILL_DATA_DETAIL: `${RDOS_BASE_URL}/batchJob/getFillDataDetailInfo`, // 获取补数据详情
	GET_JOB_CHILDREN: `${RDOS_BASE_URL}/batchJobJob/displayOffSpring`, // 获取子job
	GET_TASK_PERIODS: `${RDOS_BASE_URL}/batchJob/displayPeriods`, // 转到前后周期实例
	GET_JOB_PARENT: `${RDOS_BASE_URL}/batchJobJob/displayForefathers`, // 获取父节点
	GET_TASK_WORKFLOW_NODES: `${RDOS_BASE_URL}/batchTaskTaskShade/getAllFlowSubTasks`, // 获取工作流节点
	GET_TASK_JOB_WORKFLOW_NODES: `${RDOS_BASE_URL}/batchJobJob/displayOffSpringWorkFlow`, // 获取工作流节点
	CHECK_IS_LOOP: `${RDOS_BASE_URL}/task/checkIsLoop`,
	GET_JOB_RUNTIME_INFO: `${RDOS_BASE_URL}/batchJob/jobDetail`, // 获取任务调度详情
	QUERY_JOB_STATISTICS: `${RDOS_BASE_URL}/batchJob/queryJobsStatusStatistics`, // 查询Job统计
	QUERY_JOB_SUB_NODES: `${RDOS_BASE_URL}/batchJob/getAllChildJobWithSameDay`, // 查询子job子节点
	STATISTICS_TASK_RUNTIME: `${RDOS_BASE_URL}/batchJob/statisticsTaskRecentInfo`, // 统计任务运行信息
	STOP_FILL_DATA_JOBS: `${RDOS_BASE_URL}/batchJob/stopFillDataJobs`, // 停止补数据任务
	GET_SYNC_SCRIPT_TEMPLATE: `${RDOS_BASE_URL}/task/getJsonTemplate`, // 获取数据同步脚本模式的模版
	GET_RESTART_JOBS: `${RDOS_BASE_URL}/batchJob/getRestartChildJob`, // 获取restart job列表
	DOWNLOAD_SQL_RESULT: `${RDOS_BASE_URL}/batch/batchDownload/downloadSqlExeResult`, // 下载运行结果
	EXEC_SQL_IMMEDIATELY: `${RDOS_BASE_URL}/batchJob/startSqlImmediately`, // 立即执行SQL
	EXEC_SPARK_SQL_ADVANCED_MODE: `${RDOS_BASE_URL}/batchJob/startSqlSophisticated`, // 执行sparkSQL高级模式
	STOP_SQL_IMMEDIATELY: `${RDOS_BASE_URL}/batchJob/stopSqlImmediately`, // 停止执行SQL
	SELECT_SQL_RESULT_DATA: `${RDOS_BASE_URL}/batchSelectSql/selectData`, // 轮询调度查询sql结果
	SELECT_SQL_STATUS: `${RDOS_BASE_URL}/batchSelectSql/selectStatus`, // 轮询调度查询sql状态
	SELECT_SQL_LOG: `${RDOS_BASE_URL}/batchSelectSql/selectRunLog`, // 轮询调度查询sql状态
	EXEC_DATA_SYNC_IMMEDIATELY: `${RDOS_BASE_URL}/batchJob/startSyncImmediately`, // 立即执行数据同步
	STOP_DATA_SYNC_IMMEDIATELY: `${RDOS_BASE_URL}/batchJob/stopSyncJob`, // 停止执行数据同步
	SELECT_DATA_SYNC_RESULT: `${RDOS_BASE_URL}/batchJob/getSyncTaskStatus`, // 获取数据同步执行状态
	GET_INCREMENT_COLUMNS: `${RDOS_BASE_URL}/task/getIncreColumn`, // 获取增量字段
	CHECK_SYNC_MODE: `${RDOS_BASE_URL}/batchDataSource/canSetIncreConf`, // 检测是否满足增量
	CHECK_HIVE_PARTITIONS: `${RDOS_BASE_URL}/batchDataSource/getHivePartitions`, // 获取hive表分区值
	GET_PARTITION_TYPE: `${RDOS_BASE_URL}/batchDataSource/tableLocation`, // 检测当前impala数据源表类型 hive or kudu

	// ===== catalogue目录模块 ===== //
	GET_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batchCatalogue/getCatalogue`,
	GET_OFFLINE_CATALOGUE_BY_LOCATION: `${RDOS_BASE_URL}/batchCatalogue/getLocation`,
	ADD_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batchCatalogue/addCatalogue`,
	DEL_OFFLINE_FOLDER: `${RDOS_BASE_URL}/batchCatalogue/deleteCatalogue`,
	EDIT_OFFLINE_CATALOGUE: `${RDOS_BASE_URL}/batchCatalogue/updateCatalogue`,

	ADD_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/batchResource/addResource`,
	REPLACE_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/batchResource/replaceResource`,
	ADD_OFFLINE_TASK: `${RDOS_BASE_URL}/task/addOrUpdateTask`,
	GET_OFFLINE_TASK: `${RDOS_BASE_URL}/task/getTaskById`,
	GET_OFFLINE_TASK_BY_NAME: `${RDOS_BASE_URL}/task/getDependencyTask`,
	GET_OFFLINE_DATASOURCE: `${RDOS_BASE_URL}/batchDataSource/list`,
	GET_OFFLINE_TABLELIST: `${RDOS_BASE_URL}/batchDataSource/tablelist`,
	GET_OFFLINE_TABLELISTPAGE: `${RDOS_BASE_URL}/batchDataSourceMigration/tableList`,
	GET_OFFLINE_ALLSCHEMAS: `${RDOS_BASE_URL}/batchDataSource/getAllSchemas`,
	GET_OFFLINE_CUBEKYLININFO: `${RDOS_BASE_URL}/batchDataSource/getKylinCubeinfo`,
	GET_OFFLINE_FTP_REG: `${RDOS_BASE_URL}/batchDataSource/ftpRegexPre`,
	GET_DATA_SOURCE_VERSION: `${RDOS_BASE_URL}/batchDataSource/getDataSourceVersion`,
	GET_OFFLINE_TABLECOLUMN: `${RDOS_BASE_URL}/batchDataSource/tablecolumn`,
	GET_OFFLINE_COLUMNFORSYNCOPATE: `${RDOS_BASE_URL}/batchDataSource/columnForSyncopate`,
	GET_OFFLINE_JOBDATA: `${RDOS_BASE_URL}/task/trace`,
	SAVE_OFFLINE_JOBDATA: `${RDOS_BASE_URL}/task/addOrUpdateTask`,
	ADD_OFFLINE_FUNCTION: `${RDOS_BASE_URL}/batchFunction/addOrUpdateFunction`,
	ADD_OFFLINE_PROCEDURE: `${RDOS_BASE_URL}/batchFunction/addGpProcedureOrFunction`, // 临时
	GET_SCHEMA_NAME: `${RDOS_BASE_URL}/batchFunction/getEngineIdentity`,
	LINK_SOURCE: `${RDOS_BASE_URL}/batchDataSource/linkDataSource`, // 关联映射数据源
	GET_WORKFLOW_RELATED_TASKS: `${RDOS_BASE_URL}/task/dealFlowWorkTask`, // 获取工作流的子任务
	GET_WORKFLOW_RELATED_JOBS: `${RDOS_BASE_URL}/batchJob/getRelatedJobs`, // 获取工作流实例的子任务
	GET_WORKFLOW_FILLDATA_RELATED_JOBS: `${RDOS_BASE_URL}/batchJob/getRelatedJobsForFillData`, // 补数据工作流子节点
	GET_TABLE_INFO_BY_DATASOURCE: `${RDOS_BASE_URL}/batchDataSource/getTableInfoByDataSource`, // 从目标表位置获取表格信息
	IS_NATIVE_HIVE: `${RDOS_BASE_URL}/batchDataSource/isNativeHive`, // 校验是不是标准分区

	// 离线文件操作
	DEL_OFFLINE_TASK: `${RDOS_BASE_URL}/task/deleteTask`,
	DEL_OFFLINE_RES: `${RDOS_BASE_URL}/batchResource/deleteResource`,
	DEL_OFFLINE_FN: `${RDOS_BASE_URL}/batchFunction/deleteFunction`,
	DEL_OFFLINE_PROD: `${RDOS_BASE_URL}/batchFunction/deleteProcedure`,
	MOVE_OFFLINE_FN: `${RDOS_BASE_URL}/batchFunction/moveFunction`,
	GET_FN_DETAIL: `${RDOS_BASE_URL}/batchFunction/getFunction`,
	GET_RES_DETAIL: `${RDOS_BASE_URL}/batchResource/getResourceById`,
	DATA_PREVIEW: `${RDOS_BASE_URL}/batchDataSource/preview`,

	// ===== alarm告警模块 ===== //
	GET_ALARM_LIST: `${RDOS_BASE_URL}/batch/batchAlarm/getAlarmList`, // 获取报警规则
	ADD_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/createAlarm`, // 创建报警
	UPDATE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/updateAlarm`, // 更新报警
	CLOSE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/closeAlarm`, // 关闭报警
	OEPN_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/startAlarm`, // 开启报警
	DELETE_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/deleteAlarm`, // 删除报警
	GET_ALARM_RECORDS: `${RDOS_BASE_URL}/batch/batchAlarmRecord/getAlarmRecordList`, // 获取报警记录
	ALARM_STATISTICS: `${RDOS_BASE_URL}/batch/batchAlarmRecord/countAlarm`, // 删除报警
	ALARM_TYPES: `${RDOS_BASE_URL}/batch/batchAlarm/getSendTypeList`, // 告警类型

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
	TASK_PROGESS: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/taskProgress`, // 获取任务同步进度
	CHECK_SYNC_CONFIG: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/checkTransformConfig`, // 检查高级配置
	CHECK_SYNC_PERMISSION: `${RDOS_BASE_URL}/batch/batchDataSourceMigration/checkPermission`, // 检查同步历史是否有权限

	// ===== 项目统计 ===== //
	PROJECT_TABLE_COUNT: `${RDOS_BASE_URL}/batch/hiveTableCount/tableCount`, // 表总量
	PROJECT_STORE_COUNT: `${RDOS_BASE_URL}/batch/hiveTableCount/totalSize`, // 表总存储量
	PROJECT_STORE_TOP: `${RDOS_BASE_URL}/batch/hiveTableCount/projectSizeTopOrder`, // 项目占用排行
	PROJECT_TABLE_STORE_TOP: `${RDOS_BASE_URL}/batch/hiveTableCount/tableSizeTopOrder`, // 表占用排行
	PROJECT_DATA_OVERVIEW: `${RDOS_BASE_URL}/batch/hiveTableCount/dataHistory`, // 数据趋势概览

	// ===== 组件模块 ===== //
	SAVE_COMPONENT: `${RDOS_BASE_URL}/batch/batchComponent/addOrUpdateComponent`, // 新建、更新组件
	GET_COMPONENT_BY_ID: `${RDOS_BASE_URL}/batch/batchComponent/getComponentById`, // 获取组件信息
	GET_COMPONENT_BY_VERSIONID: `${RDOS_BASE_URL}/batch/batchComponent/getComponentByVersionId`, // 获取组件信息
	PUBLISH_COMPONENT: `${RDOS_BASE_URL}/batch/batchComponent/publishComponent`, // 提交组件
	CLONE_COMPONENT: `${RDOS_BASE_URL}/batch/batchComponent/cloneComponent`, // 克隆组件
	DELETE_COMPONENT_BY_ID: `${RDOS_BASE_URL}/batch/batchComponent/deleteComponentById`, // 删除组件
	EXECUTE_COMPONENT: `${RDOS_BASE_URL}/batch/batchComponent/executeComponent`, // 运行组件
	GET_SUPPORT_COMPONENT_TYPE: `${RDOS_BASE_URL}/batch/batchComponent/getSupportComponentType`, // 获取组件类型
	PAGE_QUERY_BY_PROJECT: `${RDOS_BASE_URL}/batch/batchTableInfo/pageQueryByProjectId`, // 获取表
	CHANGE_COMPONENT_OWNER_USER: `${RDOS_BASE_URL}/batch/batchComponent/changeComponentOwnerUser`, // 修改任务组件责任人
	GET_CHILD_TASKS: `${RDOS_BASE_URL}/batch/task/getChildTasks`,

	SUBMITTED_COMPONENT_QUERY: `${RDOS_BASE_URL}/batch/batchComponent/listSubmitComponentsByTaskType`, // 搜索当前项目提交过的任务组件列表
};
export default offlineReq;
