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

import http from './http';
import req from './reqStream';

export default {
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
	getStreamTableColumn(params: {
		schema: string;
		sourceId: number;
		tableName: string;
		flinkVersion: string;
	}) {
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
	getTaskMetrics(params: {
		taskId: number;
		timespan: string;
		end: number;
		chartNames: Array<string>;
	}) {
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
};
