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

export const STREAM_BASE_URL = '/taier';

export default {
	GET_TYPE_ORIGIN_DATA: `${STREAM_BASE_URL}/dataSource/manager/listDataSourceBaseInfo`, // 获取类型数据源
	LIST_TABLE_BY_SCHEMA: `${STREAM_BASE_URL}/dataSource/manager/listTablesBySchema`,
	POLL_PREVIEW: `${STREAM_BASE_URL}/dataSource/manager/pollPreview`,
	GET_DATA_PREVIEW: `${STREAM_BASE_URL}/dataSource/manager/getTopicData`, // 获取kafka topic预览数据
	SAVE_TASK: `${STREAM_BASE_URL}/task/addOrUpdateTask`, // 添加或者更新任务
	GET_TOPIC_TYPE: `${STREAM_BASE_URL}/dataSource/manager/getKafkaTopics`, // 获取Topic
	GET_STREAM_TABLECOLUMN: `${STREAM_BASE_URL}/dataSource/addDs/tablecolumn`, // 输出tablecolumn
	GET_TIMEZONE_LIST: `${STREAM_BASE_URL}/flinkSql/getAllTimeZone`, // 获取源表中的时区列表
	CONVERT_TO_SCRIPT_MODE: `${STREAM_BASE_URL}/task/guideToTemplate `, // 转换向导到脚本模式
	IS_OPEN_CDB: `${STREAM_BASE_URL}/dataSource/manager/isOpenCdb`,
	GET_PDB_LIST: `${STREAM_BASE_URL}/dataSource/manager/listOraclePdb`,
	GET_TOPIC_PARTITION_NUM: `${STREAM_BASE_URL}/dataSource/manager/getTopicPartitionNum`, // 获取最大读取并发数
	GET_ALL_STRATEGY: `${STREAM_BASE_URL}/streamStrategy/getStrategyByProjectId`, // 获取所有策略
	GET_SCHEMA_TABLE_COLUMN: `${STREAM_BASE_URL}/dataSource/manager/listPollTableColumn`,
	GET_SLOT_LIST: `${STREAM_BASE_URL}/dataSource/manager/listSlot`, // 获取slot列表
	GET_BINLOG_LIST_BY_SOURCE: `${STREAM_BASE_URL}/dataSource/manager/getBinLogListBySource`, // 获取binlog列表
	SQL_FORMAT: `${STREAM_BASE_URL}/flinkSql/sqlFormat`,
	GRAMMAR_CHECK: `${STREAM_BASE_URL}/flinkSql/grammarCheck`, // 语法检查
	GET_TASK_LIST: `${STREAM_BASE_URL}/flinkSql/getTaskList`,
	GET_STATUS_COUNT: `${STREAM_BASE_URL}/flinkSql/getStatusCount`,
	START_TASK: `${STREAM_BASE_URL}/flinkSql/start`,
	START_COLLECTION_TASK: `${STREAM_BASE_URL}/task/startTask`,
	GET_TASK_MANAGER_LOG: `${STREAM_BASE_URL}//flinkSql/getTaskManagerLog`,
	GET_JOB_MANAGER_LOG: `${STREAM_BASE_URL}/flinkSql/getJobManagerLog`,
	LIST_TASK_MANAGER: `${STREAM_BASE_URL}/flinkSql/listTaskManagerByTaskId`,
	GET_TASK_LOGS: `${STREAM_BASE_URL}/flinkSql/getLogsByTaskId`,
	GET_TASK_FAILOVER_LOG: `${STREAM_BASE_URL}/flinkSql/getFailoverLogsByTaskId`, // 获取任务告警日志
	GET_HISTORY_LOG: `${STREAM_BASE_URL}/flinkSql/getJobHistoryList`,
	DOWNLOAD_HISTORY_LOG: `${STREAM_BASE_URL}/download/streamDownload/downloadHistoryLog`,
	GET_TASK_METRICS: `${STREAM_BASE_URL}/streamJobMetric/getTaskMetrics`, // 获取指标
	GET_METRIC_VALUES: `${STREAM_BASE_URL}/streamJobMetric/values`, // 获取所有指标
	CHECK_SOURCE_STATUS: `${STREAM_BASE_URL}/dataSource/manager/getDataSourceLinkStatus`, // 获取任务的异常数据源
	QUERY_TASK_METRICES: `${STREAM_BASE_URL}/streamJobMetric/queryTaskMetrics`, // 查询指标数据
};
