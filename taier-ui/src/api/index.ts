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

import req from './req';
import http from './http';
import offlineReq from './reqOffline';
import dataManageReq from './reqDataManage';
import { TASK_TYPE_ENUM } from '@/constant';

const api = {
	sqlFormat(params: any) {
		// SQL格式化
		return http.post(offlineReq.SQL_FORMAT, params);
	},

	unlockFile(params: any) {
		// 解锁文件
		return http.post(offlineReq.UNLOCK_FILE, params);
	},

	getIsStandeAlone(params?: any) {
		return http.post(req.GET_IS_STANDE_ALONE, params);
	},
	addRoleUser(user: any) {
		return http.post(req.ADD_ROLE_USRE, user);
	},
	getLoginedUser() {
		return http.post(req.GET_USER_BY_ID);
	},
	updateUserRole(user: any) {
		return http.post(req.UPDATE_USER_ROLE, user);
	},
	getNotProjectUsers(params: any) {
		return http.post(req.GET_NOT_PROJECT_USERS, params);
	},

	searchUICUsers(params: any) {
		return http.post(req.SEARCH_UIC_USERS, params);
	},
	getTenantList(params?: any) {
		return http.post(req.GET_TENANT_LIST, params);
	},

	/**
	 * 跟踪采集用户行为
	 * @param {string} target
	 * @param {object} params
	 */
	//   async trackUserActions(target: any, params: any) {
	//     const trackTarget = `track_${target}`;
	//     const tracked = utils.getCookie(trackTarget);
	//     if (!tracked) {
	//       const res = await http.post(req.TRACK_USER_ACTIONS, params);
	//       if (res.code === 1) {
	//         utils.setCookie(trackTarget, true);
	//       }
	//     }
	//   },
	// ========== Project ========== //
	compareIntrinsicTable(params: any) {
		// hive同步页面中获取新增或删除的表名称
		return http.post(req.COMPAREINTRINSICTABLE, params);
	},
	dealIntrinsicTable(params: any) {
		// hive同步页面中同步新增或删除的表
		return http.post(req.DEALINTRINSICTABLE, params);
	},
	checkDealStatus(params?: any) {
		// hive同步页面中获取同步状态的接口
		return http.post(req.CHECKDEALSTATUS, params);
	},
	queryProjects(params: any) {
		return http.post(req.QUERY_PROJECT_LIST, params);
	},
	getProjectCatalogue(params: { isGetFile: true }) {
		return http.post(req.GET_PROJECT_CATALOGUE, params);
	},
	getProjectByTenant(params: any) {
		return http.post(req.GET_PROJECT_BY_TENANT, params);
	},
	getProjects(params: any) {
		// 获取项目
		return http.post(req.GET_PROJECT_LIST, params);
	},
	getAllProjects(params: any) {
		// 获取所有项目
		return http.post(req.GET_ALL_PROJECTS, params);
	},
	getProjectSummary() {
		return http.post(req.GET_PROJECT_SUMMARY);
	},
	getTenantProjects(params: any) {
		// 获取租户下所有项目
		return http.post(req.GET_ALL_TENANT_PROJECTS, params);
	},
	getProjectSupportEngines(params: any) {
		return http.post(req.GET_PROJ_SUPPORT_ENGINE, params);
	},
	removeProjectUser(params: any) {
		return http.post(req.REMOVE_USER_FROM_PROJECT, params);
	},
	addProjectUser(params: any) {
		return http.post(req.ADD_PROJECT_USER, params);
	},
	getUserProjects(params: any) {
		return http.post(req.GET_USRE_PROJECTS, params);
	},
	getProjectUsers(params: any) {
		return http.post(req.GET_PROJECT_USERS, params);
	},
	getProjectByID(params: any) {
		return http.post(req.GET_PROJECT_BY_ID, params);
	},
	upateProjectInfo(params: any) {
		return http.post(req.UPDATE_PROJECT_INFO, params);
	},
	createProject(params: any) {
		return http.post(req.CREATE_PROJECT, params);
	},
	getProjectInfo(params: any) {
		return http.post(req.GET_PROJECT_INFO, params);
	},
	getProjectListInfo(params: any) {
		return http.post(req.GET_PROJECT_LIST_INFO, params);
	},
	setSticky(params: any) {
		return http.post(req.SET_STICKY, params);
	},
	deleteProject(params: any) {
		return http.post(req.DELETE_PROJECT, params);
	},
	deleteProProject(params: any) {
		return http.post(req.DELETE_PROJECT_PRO, params);
	},
	updateProjectSchedule(params: any) {
		return http.post(req.UPDATE_PROJECT_SCHEDULE, params);
	},
	updateProjectAllowDownLoad(params: any) {
		return http.post(req.UPDATE_PROJECT_ALLOW_DOWNLOAD, params);
	},
	updateProjectAlarm(params: any) {
		return http.post(req.UPDATE_PROJECT_ALARM, params);
	},
	updateProjectAlarmStatus(params: any) {
		return http.post(req.UPDATE_PROJECT_ALARM_STATUS, params);
	},
	getProjectAlarm() {
		return http.post(req.GET_PROJECT_ALARM, null);
	},
	bindProductionProject(params: any) {
		return http.post(req.BIND_PRODUCTION_PROJECT, params);
	},
	getBindingProjectList(params: any) {
		return http.post(req.GET_COULD_BINDING_PROJECT_LIST, params);
	},
	getTableListFromDataBase(params: any) {
		return http.post(req.GET_TABLE_LIST_FROM_DATABASE, params);
	},
	getRetainDBList(params?: any) {
		return http.post(req.GET_RETAINDB_LIST, params);
	},
	getSupportEngineType(params?: any) {
		// 项目支持的引擎类型
		return http.post(req.GET_SUPPORT_ENGINE_TYPE, params);
	},
	getProjectTableTypes(params: any) {
		// 项目支持的表类型
		return http.post(req.GET_SUPPORT_TABLE_TYPE, params);
	},
	getTenantTableTypes(params: any) {
		// 租户支持的表类型
		return http.post(req.GET_TENANT_TABLE_TYPE, params);
	},
	getProjectUsedEngineInfo(params?: any) {
		// 获取项目在用引擎信息
		return http.post(req.GET_PRO_USE_ENGINE, params);
	},
	getProjectUnUsedEngine(params?: any) {
		// 获取项目未接入的引擎信息
		return http.post(req.GET_PRO_UNUSE_ENGINE, params);
	},
	addNewEngine(params: any) {
		return http.post(req.ADD_NEW_ENGINE, params);
	},
	getDBTableList(params: any) {
		return http.post(req.GET_DB_TABLE_LIST, params);
	},
	checkAddEngineStatus(parmas: any) {
		return http.post(req.CHECK_ADD_ENGINE_STATUS, parmas);
	},

	// ========== Role ========== //
	getRoleList(params: any) {
		return http.post(req.GET_ROLE_LIST, params);
	},
	updateRole(params: any) {
		return http.post(req.UPDATE_ROLE, params);
	},
	deleteRole(params: any) {
		return http.post(req.DELETE_ROLE, params);
	},
	getRoleTree(params?: any) {
		return http.post(req.GET_ROLE_TREE, params);
	},
	getRoleInfo(params: any) {
		return http.post(req.GET_ROLE_INFO, params);
	},

	// ========== Task ========== //
	cloneTask(params: any) {
		return http.post(req.CLONE_TASK, params);
	},
	cloneTaskToWorkflow(params: any) {
		return http.post(req.CLONE_TASK_TO_WORKFLOW, params);
	},
	getWorkflowList(params: any) {
		return http.post(req.GET_WORKFLOW_LISTS, params);
	},
	taskVersionScheduleConf(params: any) {
		return http.post(offlineReq.TASK_VERSION_SCHEDULE_CONF, params);
	},
	updateTaskOwner(params: any) {
		return http.post(offlineReq.UPDATE_TASK_OWNER, params);
	},
	convertToHiveColumns(params: any) {
		return http.post(req.CONVERT_TO_HIVE_COLUMNS, params);
	},

	// =========== 以下为离线模块 ==================//
	saveOfflineTask(task: any) {
		return http.post(offlineReq.SAVE_TASK, task);
	},

	convertDataSyncToScriptMode(params: any) {
		return http.post(offlineReq.CONVERT_SYNC_T0_SCRIPT_MODE, params);
	},

	renameTask(task: any) {
		return http.post(offlineReq.RENAME_TASK, task);
	},

	forceUpdateOfflineTask(task: any) {
		return http.post(offlineReq.FORCE_UPDATE_TASK, task);
	},

	getOfflineTaskByID(params: any) {
		return http.post(offlineReq.GET_TASK, params);
	},
	queryOfflineCataTask(params: any) {
		return http.post(offlineReq.QUERY_CATA_TASK, params);
	},
	getOfflineTasksByProject(params: any) {
		return http.post(offlineReq.GET_TASKS_BY_PROJECT_ID, params);
	},
	getOfflineTasksByName(params: any) {
		return http.post(offlineReq.GET_TASKS_BY_NAME, params);
	},
	queryOfflineTasks(params: any) {
		return http.post(offlineReq.QUERY_TASKS, params);
	},
	getOfflineTaskLog(params: any) {
		// 获取离线任务日志
		return http.post(offlineReq.GET_TASK_LOG, params);
	},
	getOfflineTaskPeriods(params: any) {
		// 转到前后周期
		return http.post(offlineReq.GET_TASK_PERIODS, params);
	},
	searchOfflineTask(params: any) {
		return http.post(offlineReq.GLOBAL_SEARCH_TASK, params);
	},
	getCustomParams(params?: any) {
		return http.post(offlineReq.GET_CUSTOM_TASK_PARAMS, params);
	},
	getSyncTemplate(params: any) {
		return http.post(offlineReq.GET_SYNC_SCRIPT_TEMPLATE, params);
	},
	getCreateTargetTable(params: any) {
		return http.post(req.GET_CREATE_TARGET_TABLE, params);
	},
	getRelatedTasks(params: any) {
		return http.post(offlineReq.GET_WORKFLOW_RELATED_TASKS, params);
	},
	getRelatedJobs(params: any) {
		return http.post(offlineReq.GET_WORKFLOW_RELATED_JOBS, params);
	},
	getFillDataRelatedJobs(params: any) {
		return http.post(offlineReq.GET_WORKFLOW_FILLDATA_RELATED_JOBS, params);
	},
	// =========== 脚本模块 ==================//
	saveScript(params: any) {
		return http.post(offlineReq.SAVE_SCRIPT, params);
	},
	forceUpdateOfflineScript(task: any) {
		return http.post(offlineReq.FORCE_UPDATE_SCRIPT, task);
	},
	getScriptById(params: any) {
		return http.post(offlineReq.GET_SCRIPT_BY_ID, params);
	},
	execScript(params: any) {
		return http.post(offlineReq.EXEC_SCRIPT, params);
	},
	stopScript(params: any) {
		// 获取离线任务日志
		return http.post(offlineReq.STOP_SCRIPT, params);
	},
	deleteScript(params: any) {
		return http.post(offlineReq.DELETE_SCRIPT, params);
	},
	getScriptTypes(params?: any) {
		return http.post(offlineReq.GET_SCRIPT_TYPES, params);
	},

	// =========== 离线Job模块 ==================//
	queryJobs(params: any) {
		return http.post(offlineReq.QUERY_JOBS, params);
	},
	publishOfflineTask(params: any) {
		return http.post(offlineReq.PUBLISH_TASK, params);
	},
	getTaskTypes(params?: any) {
		return http.post(offlineReq.GET_TASK_TYPES, params);
	},
	getAnalyDataSourceLists(params?: any) {
		return http.post(offlineReq.GET_ANALY_DTATSOURCE_LISTS, params);
	},
	getJobById(params: any) {
		return http.post(offlineReq.GET_JOB_BY_ID, params);
	},
	getJobGraph(params?: any) {
		return http.post(offlineReq.GET_JOB_GRAPH, params);
	},
	getJobStatistics(params?: any) {
		return http.post(offlineReq.GET_JOB_STATISTICS, params);
	},
	getJobTopTime(params: any) {
		return http.post(offlineReq.GET_JOB_TOP_TIME, params);
	},
	getTableInfoByDataSource(params: any) {
		return http.post(offlineReq.GET_TABLE_INFO_BY_DATASOURCE, params);
	},
	getJobTopError(params?: any) {
		return http.post(offlineReq.GET_JOB_TOP_ERROR, params);
	},
	patchTaskData(params: any) {
		// 补数据
		return http.post(offlineReq.PATCH_TASK_DATA, params);
	},
	operaRecordData(params: any) {
		// 操作记录
		return http.post(offlineReq.OPERA_RECORD_DATA, params);
	},
	getTaskChildren(params: any) {
		// 获取任务子节点
		return http.post(offlineReq.GET_TASK_CHILDREN, params);
	},
	getTaskParents(params: any) {
		// 获取任务父节点
		return http.post(offlineReq.GET_TASK_PARENTS, params);
	},
	getFillData(params: any) {
		// 补数据搜索
		return http.post(offlineReq.GET_FILL_DATA, params);
	},
	getFillDate(params: any) {
		// 补数据日期列表
		return http.post(offlineReq.GET_FILL_DATE, params);
	},
	getFillDataDetail(params: any) {
		// 补数据详情
		return http.post(offlineReq.GET_FILL_DATA_DETAIL, params);
	},
	startJob(params: any) {
		// 启动任务
		return http.post(offlineReq.START_JOB, params);
	},
	stopJob(params: any) {
		// 停止任务
		return http.post(offlineReq.STOP_JOB, params);
	},
	restartAndResume(params: any) {
		// 重启并恢复任务
		return http.post(offlineReq.RESTART_AND_RESUME_JOB, params);
	},
	batchStopJob(params: any) {
		// 批量停止任务
		return http.post(offlineReq.BATCH_STOP_JOBS, params);
	},
	batchStopJobByDate(params: any) {
		// 按业务日期批量杀任务
		return http.post(offlineReq.BATCH_STOP_JOBS_BY_DATE, params);
	},
	batchRestartAndResume(params: any) {
		// 重启并恢复任务
		return http.post(offlineReq.BATCH_RESTART_AND_RESUME_JOB, params);
	},
	getJobChildren(params: any) {
		// 获取任务子Job
		return http.post(offlineReq.GET_JOB_CHILDREN, params);
	},
	getJobParents(params: any) {
		// 获取任务父Job
		return http.post(offlineReq.GET_JOB_PARENT, params);
	},
	/**
	 * 获取工作流任务的子节点
	 */
	getTaskWorkflowNodes(params: any) {
		return http.post(offlineReq.GET_TASK_WORKFLOW_NODES, params);
	},
	/**
	 * 获取工作流任务节点实例的子节点
	 */
	getTaskJobWorkflowNodes(params: any) {
		return http.post(offlineReq.GET_TASK_JOB_WORKFLOW_NODES, params);
	},
	execSQLImmediately(params: any) {
		// 立即执行SQL
		return http.post(offlineReq.EXEC_SQL_IMMEDIATELY, params);
	},
	/**
	 * sparkSql高级运行模式
	 * @param params 执行参数
	 */
	execSparkSQLAdvancedMode(params: {
		projectId: string;
		isCheckDDL: number;
		taskVariables: any[];
		sqlList: string[];
		taskId: number;
	}) {
		// 执行 SparkSQL 高级模式
		return http.post(offlineReq.EXEC_SPARK_SQL_ADVANCED_MODE, params);
	},
	stopSQLImmediately(params: any) {
		// 停止执行数据同步
		return http.post(offlineReq.STOP_SQL_IMMEDIATELY, params);
	},
	execDataSyncImmediately(params: any) {
		// 立即执行数据同步
		return http.post(offlineReq.EXEC_DATA_SYNC_IMMEDIATELY, params);
	},
	stopDataSyncImmediately(params: any) {
		// 停止执行SQL
		return http.post(offlineReq.STOP_DATA_SYNC_IMMEDIATELY, params);
	},
	getIncrementColumns(params: any) {
		// 获取增量字段
		return http.post(offlineReq.GET_INCREMENT_COLUMNS, params);
	},
	checkSyncMode(params: any) {
		// 检测是否满足增量数据同步
		return http.post(offlineReq.CHECK_SYNC_MODE, params);
	},
	getHivePartitions(params: any) {
		// 获取Hive分区
		return http.post(offlineReq.CHECK_HIVE_PARTITIONS, params);
	},
	getPartitionType(params: any) {
		return http.post(offlineReq.GET_PARTITION_TYPE, params);
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
			taskType && taskType === TASK_TYPE_ENUM.SYNC
				? offlineReq.SELECT_DATA_SYNC_RESULT
				: offlineReq.SELECT_SQL_RESULT_DATA;
		return http.post(url, params);
	},
	checkIsLoop(prams: any) {
		return http.post(offlineReq.CHECK_IS_LOOP, prams);
	},
	getJobRuntimeInfo(prams: any) {
		return http.post(offlineReq.GET_JOB_RUNTIME_INFO, prams);
	},
	queryJobStatics(params: any) {
		return http.post(offlineReq.QUERY_JOB_STATISTICS, params);
	},
	queryJobSubNodes(params: any) {
		return http.post(offlineReq.QUERY_JOB_SUB_NODES, params);
	},
	forzenTask(params: any) {
		return http.post(offlineReq.FROZEN_TASK, params);
	},
	statisticsTaskRunTime(params: any) {
		return http.post(offlineReq.STATISTICS_TASK_RUNTIME, params);
	},
	stopFillDataJobs(params: any) {
		return http.post(offlineReq.STOP_FILL_DATA_JOBS, params);
	},
	getRestartJobs(params: any) {
		return http.post(offlineReq.GET_RESTART_JOBS, params);
	},

	// =========== 离线catalogue目录模块 ==================//
	getOfflineCatalogue(params: any) {
		return http.post(offlineReq.GET_OFFLINE_CATALOGUE, params);
	},
	addOfflineCatalogue(params: any) {
		return http.post(offlineReq.ADD_OFFLINE_CATALOGUE, params);
	},
	editOfflineCatalogue(params: any) {
		return http.post(offlineReq.EDIT_OFFLINE_CATALOGUE, params);
	},
	locateCataPosition(params: any) {
		return http.post(offlineReq.GET_OFFLINE_CATALOGUE_BY_LOCATION, params);
	},

	addOfflineResource(params: any) {
		return http.postAsFormData(offlineReq.ADD_OFFLINE_RESOURCE, params);
	},
	replaceOfflineResource(params: any) {
		return http.postAsFormData(offlineReq.REPLACE_OFFLINE_RESOURCE, params);
	},
	addOfflineTask(params: any) {
		return http.post(offlineReq.ADD_OFFLINE_TASK, params);
	},
	getOfflineTaskDetail(params: any) {
		return http.post(offlineReq.GET_OFFLINE_TASK, params);
	},
	getOfflineTaskByName(params: any) {
		return http.post(offlineReq.GET_OFFLINE_TASK_BY_NAME, params);
	},
	getOfflineDataSource(params?: any) {
		return http.post(offlineReq.GET_OFFLINE_DATASOURCE, params);
	},
	getOfflineTableList(params: any) {
		return http.post(offlineReq.GET_OFFLINE_TABLELIST, params);
	},
	getOfflineTableListPage(params: any) {
		return http.post(offlineReq.GET_OFFLINE_TABLELISTPAGE, params);
	},
	getAllSchemas(params: any) {
		return http.post(offlineReq.GET_OFFLINE_ALLSCHEMAS, params);
	},
	getOfflineCubeKylinInfo(params: any) {
		return http.post(offlineReq.GET_OFFLINE_CUBEKYLININFO, params);
	},
	getOfflineTableColumn(params: {
		sourceId: string;
		schema?: any;
		tableName: string;
		isIncludePart?: boolean;
	}) {
		return http.post(offlineReq.GET_OFFLINE_TABLECOLUMN, params);
	},
	isNativeHive(params: any) {
		return http.post(offlineReq.IS_NATIVE_HIVE, params);
	},
	getOfflineColumnForSyncopate(params: any) {
		return http.post(offlineReq.GET_OFFLINE_COLUMNFORSYNCOPATE, params);
	},
	getOfflineJobData(params: any) {
		return http.post(offlineReq.GET_OFFLINE_JOBDATA, params);
	},
	saveOfflineJobData(params: any) {
		return http.post(offlineReq.SAVE_OFFLINE_JOBDATA, params);
	},
	addOfflineFunction(params: any) {
		return http.post(offlineReq.ADD_OFFLINE_FUNCTION, params);
	},
	addOfflineProcedure(params: any) {
		return http.post(offlineReq.ADD_OFFLINE_PROCEDURE, params);
	},
	getschemaName(params: any) {
		return http.post(offlineReq.GET_SCHEMA_NAME, params);
	},
	getTableListByName(params: any) {
		return http.post(req.GET_TABLE_LIST_BY_NAME, params);
	},
	getRecommentTask(params: any) {
		return http.post(req.GET_RECOMMEND_TASK, params);
	},
	getColumnsOfTable(params: any) {
		return http.post(req.GET_COLUMNS_OF_TABLE, params);
	},
	getAllFunction(params: any) {
		return http.post(req.GET_ALL_FUNCTION_NAME, params);
	},

	// =========== 离线文件操作 ==================//
	delOfflineTask(params: any) {
		return http.post(offlineReq.DEL_OFFLINE_TASK, params);
	},
	delOfflineFolder(params: any) {
		return http.post(offlineReq.DEL_OFFLINE_FOLDER, params);
	},
	delOfflineRes(params: any) {
		return http.post(offlineReq.DEL_OFFLINE_RES, params);
	},
	delOfflineFn(params: any) {
		return http.post(offlineReq.DEL_OFFLINE_FN, params);
	},
	delOfflineProd(params: any) {
		return http.post(offlineReq.DEL_OFFLINE_PROD, params);
	},
	moveOfflineFn(params: any) {
		return http.post(offlineReq.MOVE_OFFLINE_FN, params);
	},
	getOfflineFn(params: any) {
		return http.post(offlineReq.GET_FN_DETAIL, params);
	},
	getOfflineRes(params: any) {
		return http.post(offlineReq.GET_RES_DETAIL, params);
	},
	getDataPreview(params: any) {
		return http.post(offlineReq.DATA_PREVIEW, params);
	},
	getHBaseColumnFamily(params: any) {
		return http.post(offlineReq.GET_HBASE_COLUMN_FAMILY, params);
	},

	// =========== 离线alarm告警模块 ==================//
	getOfflineAlarmList(params: any) {
		return http.post(offlineReq.GET_ALARM_LIST, params);
	},
	addOfflineAlarm(params: any) {
		return http.post(offlineReq.ADD_ALARM, params);
	},
	updateOfflineAlarm(params: any) {
		return http.post(offlineReq.UPDATE_ALARM, params);
	},
	closeOfflineAlarm(params: any) {
		return http.post(offlineReq.CLOSE_ALARM, params);
	},
	openOfflineAlarm(params: any) {
		return http.post(offlineReq.OEPN_ALARM, params);
	},
	deleteOfflineAlarm(params: any) {
		return http.post(offlineReq.DELETE_ALARM, params);
	},
	getOfflineAlarmRecords(params: any) {
		return http.post(offlineReq.GET_ALARM_RECORDS, params);
	},
	getOfflineAlarmStatistics(params?: any) {
		return http.post(offlineReq.ALARM_STATISTICS, params);
	},
	getOfflineAlarmTypes() {
		return http.post(offlineReq.ALARM_TYPES);
	},

	// =========== datasource数据源模块 ==================//
	addOrUpdateSource(source: any) {
		return http.post(offlineReq.SAVE_DATA_SOURCE, source);
	},
	addOrUpdateSourceKerberos(source: any) {
		return http.postAsFormData(offlineReq.SAVE_DATA_SOURCE_KERBEROS, source);
	},
	deleteDataSource(params: any) {
		return http.post(offlineReq.DELETE_DATA_SOURCE, params);
	},
	queryDataSource(params: any) {
		return http.post(offlineReq.QUERY_DATA_SOURCE, params);
	},
	getDataSourceById(params: any) {
		return http.post(offlineReq.GET_DATA_SOURCE_BY_ID, params);
	},
	testDSConnection(params: any) {
		return http.post(offlineReq.TEST_DATA_SOURCE_CONNECTION, params);
	},
	testDSConnectionKerberos(params: any) {
		return http.postAsFormData(offlineReq.TEST_DATA_SOURCE_CONNECTION_KERBEROS, params);
	},
	getDataSourceTypes(params?: any) {
		return http.post(offlineReq.GET_DATA_SOURCE_TYPES, params);
	},
	checkIsPermission(params?: any) {
		return http.post(offlineReq.CHECK_IS_PERMISSION, params);
	},

	// ============== dataSource 整库同步 ==================
	saveSyncConfig(params: any) {
		return http.post(offlineReq.SAVE_SYNC_CONFIG, params);
	},
	getSyncHistoryList(params: any) {
		return http.post(offlineReq.GET_SYNC_HISTORY, params);
	},
	getSyncDetail(params: any) {
		return http.post(offlineReq.GET_SYNC_DETAIL, params);
	},
	publishSyncTask(params: any) {
		return http.post(offlineReq.PUBLISH_SYNC_TASK, params);
	},
	taskProgress(params: any) {
		return http.post(offlineReq.TASK_PROGESS, params);
	},
	checkSyncConfig(params: any) {
		return http.post(offlineReq.CHECK_SYNC_CONFIG, params);
	},
	getTaskOfOfflineSource(params: any) {
		return http.post(offlineReq.GET_TASK_LIST_OF_OFFLINE_SOURCE, params);
	},
	checkSyncPermission(params?: any) {
		return http.post(offlineReq.CHECK_SYNC_PERMISSION, params);
	},

	// ============== dataManage 数据管理 ==================
	createTable(params: any) {
		return http.post(dataManageReq.CREATE_TABLE, params);
	},
	searchTable(params: any) {
		return http.post(dataManageReq.SEARCH_TABLE, params);
	},
	getTable(params: any) {
		return http.post(dataManageReq.GET_TABLE, params);
	},
	getTableByName(params: any) {
		return http.post(dataManageReq.GET_TABLE_BY_NAME, params);
	},
	getTablesByName(params: any) {
		return http.post(dataManageReq.GET_TABLES_BY_NAME, params);
	},
	previewTable(params: any) {
		return http.post(dataManageReq.PREVIEW_TABLE, params);
	},
	saveTable(params: any) {
		return http.post(dataManageReq.SAVE_TABLE, params);
	},
	searchLog(params: any) {
		return http.post(dataManageReq.SEARCH_LOG, params);
	},
	getProjectUsersData(params: any) {
		return http.post(req.GET_PROJECT_USERS, params);
	},
	getCreateTableCode(params: any) {
		return http.post(dataManageReq.GET_CREATE_CODE, params);
	},
	dropTable(params: any) {
		return http.post(dataManageReq.DROP_TABLE, params);
	},
	createDdlTable(params: any) {
		return http.post(dataManageReq.DDL_CREATE_TABLE, params);
	},
	uploadTableData(params: any) {
		return http.post(dataManageReq.UPLOAD_TABLE_DATA, params);
	},
	checkTableExist(params: any) {
		return http.post(dataManageReq.CHECK_TABLE_EXIST, params);
	},
	checkHdfsLocExist(params: any) {
		return http.post(dataManageReq.CHECK_HDFSLOC_EXIST, params);
	},
	getTablePartition(params: any) {
		return http.post(dataManageReq.GET_TABLE_PARTITION, params);
	},
	checkTablePartition(params: any) {
		return http.post(dataManageReq.CHECK_TABLE_PARTITION, params);
	},
	importLocalData(params: any) {
		// 导入本地数据
		return http.postAsFormData(dataManageReq.UPLOAD_TABLE_DATA, params);
	},
	getUploadStatus(params: any) {
		return http.post(dataManageReq.GET_UPLOAD_STATUS, params);
	},
	getTableRelTree(params: any) {
		return http.post(dataManageReq.GET_REL_TABLE_TREE, params);
	},
	getRelTableInfo(params: any) {
		return http.post(dataManageReq.GET_REL_TABLE_INFO, params);
	},
	getParentRelTable(params: any) {
		return http.post(dataManageReq.GET_PARENT_REL_TABLES, params);
	},
	getChildRelTables(params: any) {
		return http.post(dataManageReq.GET_CHILD_REL_TABLES, params);
	},
	getRelTableTasks(params: any) {
		return http.post(dataManageReq.GET_REL_TABLE_TASKS, params);
	},

	// =========== 脏数据 ==================//
	getDirtyDataTrend(params: any) {
		return http.post(dataManageReq.GET_DIRTY_DATA_TREND, params);
	},
	top30DirtyData(params: any) {
		return http.post(dataManageReq.TOP30_DIRTY_DATA, params);
	},
	getDirtyDataTables(params: any) {
		return http.post(dataManageReq.GET_DIRTY_DATA_TABLES, params);
	},
	getPubSyncTask(params: any) {
		// 导入本地数据
		return http.post(dataManageReq.GET_PUB_SYNC_TASK, params);
	},
	getDirtyDataTableInfo(params: any) {
		return http.post(dataManageReq.GET_DIRTY_DATA_TABLE_INFO, params);
	},
	getDirtyDataTableOverview(params: any) {
		return http.post(dataManageReq.GET_DIRTY_TABLE_OVERVIEW, params);
	},
	countDirtyData(params: any) {
		return http.post(dataManageReq.COUNT_DIRTY_DATA, params);
	},
	getDirtyDataAnalytics(params: any) {
		return http.post(dataManageReq.GET_DIRTY_DATA_ANALYTICS, params);
	},

	// =========== 项目统计 ==================//
	countProjectTable(params: any) {
		return http.post(offlineReq.PROJECT_TABLE_COUNT, params);
	},
	countProjectStore(params: any) {
		return http.post(offlineReq.PROJECT_STORE_COUNT, params);
	},
	getProjectStoreTop(params: any) {
		return http.post(offlineReq.PROJECT_STORE_TOP, params);
	},
	getProjectTableStoreTop(params: any) {
		return http.post(offlineReq.PROJECT_TABLE_STORE_TOP, params);
	},
	getProjectDataOverview(params: any) {
		return http.post(offlineReq.PROJECT_DATA_OVERVIEW, params);
	},
	//= =============== 离线合并接口 ===============/
	linkSource(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(offlineReq.LINK_SOURCE, params);
		}
	},
	getLinkSourceList(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_OFFLINE_LINK_SOURCE, params);
		}
	},
	getPackageName(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_OFFLINE_PACKAGE_NAME, params);
		}
	},
	createPackage(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.OFFLINE_CREATE_PACKAGE, params);
		}
	},
	publishPackage(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.PUBLISH_OFFLINE_PACKAGE, params);
		}
	},
	getPackageList(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_OFFLINE_PACKAGE_LIST, params);
		}
	},
	deletePackage(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.OFFLINE_DELETE_PACKAGE, params);
		}
	},
	getTaskLinkItems(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_OFFLINE_TASK_LINK_ITEMS, params);
		}
	},
	getPackageItemList(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_PACKAGE_ITEM_LIST, params);
		}
	},
	getPackageItemStatus(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_PACKAGE_ITEM_STATUS, params);
		}
	},
	initUploadPackage(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.INIT_UPLOAD_PACKAGE, params);
		}
	},
	getIsHasFtp(params: any, type = 'offline') {
		if (type === 'offline') {
			return http.post(req.GET_PACKAGE_ISHASFTP, params);
		}
	},
	// =========== 组件模块 ==================//
	saveComponent(params: any) {
		return http.post(offlineReq.SAVE_COMPONENT, params);
	},
	updateComponentOwner(params: any) {
		return http.post(offlineReq.CHANGE_COMPONENT_OWNER_USER, params);
	},
	cloneComponent(params: any) {
		return http.post(offlineReq.CLONE_COMPONENT, params);
	},
	deleteComponent(params: any) {
		return http.post(offlineReq.DELETE_COMPONENT_BY_ID, params);
	},
	getComponentById(params: any) {
		return http.post(offlineReq.GET_COMPONENT_BY_ID, params);
	},
	getComponentByVersionId(params: any) {
		return http.post(offlineReq.GET_COMPONENT_BY_VERSIONID, params);
	},

	publishComponent(params: any) {
		return http.post(offlineReq.PUBLISH_COMPONENT, params);
	},
	execComponent(params: any) {
		return http.post(offlineReq.EXECUTE_COMPONENT, params);
	},
	getComponentTypes(params?: any) {
		return http.post(offlineReq.GET_SUPPORT_COMPONENT_TYPE, params);
	},
	pageQueryByProjectIdInComponent(params: any) {
		return http.post(offlineReq.PAGE_QUERY_BY_PROJECT, params);
	},
	selectStatus(params: any) {
		// 非数据同步接口轮训状态
		return http.post(offlineReq.SELECT_SQL_STATUS, params);
	},
	selectRunLog(params: any) {
		// 非数据同步接口获取日志
		return http.post(offlineReq.SELECT_SQL_LOG, params);
	},
	selectExecResultDataSync(params: any) {
		// 数据同步接口获取结果表
		return http.post(offlineReq.SELECT_DATA_SYNC_RESULT, params);
	},
	ftpRegexPre(params: any) {
		return http.post(offlineReq.GET_OFFLINE_FTP_REG, params);
	},
	/**
	 * - 查询数据同步任务，SQL 执行结果
	 * - 需要补充增量同步
	 * @param {Object} params 请求参数
	 * @param {Number} taskType 任务类型
	 */
	getDataSourceVersion(params: any) {
		return http.post(offlineReq.GET_DATA_SOURCE_VERSION, params);
	},
	getProjectsByAppType(params: any) {
		return http.post(req.GET_PROJECTS_BY_APP_TYPE, params);
	},
	getTasksByAppType(params: any) {
		return http.post(req.GET_TASKS_BY_APP_TYPE, params);
	},
	allProductGlobalSearch(params: any) {
		return http.post(req.ALL_PRODUCT_GLOBAL_SEARCH, params);
	},
	// =========== 离线文件操作 ==================//
	getChildTasks(params: any) {
		return http.post(offlineReq.GET_CHILD_TASKS, params);
	},

	submittedComponentQuery(params: any) {
		return http.post(offlineReq.SUBMITTED_COMPONENT_QUERY, params);
	},
};
export default api;
