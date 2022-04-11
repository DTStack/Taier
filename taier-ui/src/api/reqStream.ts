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
	GET_TASK: `${STREAM_BASE_URL}/task/getTaskById`, // 通过ID获取任务
	SAVE_TASK: `${STREAM_BASE_URL}/task/addOrUpdateTask`, // 添加或者更新任务
	GET_TOPIC_TYPE: `${STREAM_BASE_URL}/dataSource/manager/getKafkaTopics`, // 获取Topic
	GET_STREAM_TABLECOLUMN: `${STREAM_BASE_URL}/dataSource/addDs/tablecolumn`, // 输出tablecolumn
	GET_TIMEZONE_LIST: `${STREAM_BASE_URL}/flinkSql/getAllTimeZone`, // 获取源表中的时区列表
	CONVERT_TO_SCRIPT_MODE: `${STREAM_BASE_URL}/task/guideToTemplate `, // 转换向导到脚本模式
};
