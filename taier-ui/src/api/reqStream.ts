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

// 从config文件全局读取

export const STREAM_BASE_URL = '/taier';
export const STREAM_BASE_UPLOAD_URL = '/taier';
export const UIC_BASE_URL = '/uic/api';

export default {
    GET_SUPPORT_BINLOG_DATA_TYPES: `${STREAM_BASE_URL}/supportDataSources/getSupportDaTypes`, // 获取支持实时采集的数据源类型
    GET_TYPE_ORIGIN_DATA: `${STREAM_BASE_URL}/streamDataSource/listDataSourceBaseInfo`, // 获取类型数据源
    GET_STREAM_TABLELIST: `${STREAM_BASE_URL}/streamDataSource/tablelist`,
    IS_OPEN_CDB: `${STREAM_BASE_URL}/streamDataSource/isOpenCdb`,
    GET_PDB_LIST: `${STREAM_BASE_URL}/streamDataSource/listOraclePdb`,
    LIST_TABLE_BY_SCHEMA: `${STREAM_BASE_URL}/streamDataSource/listTablesBySchema`,
    LIST_SCHEMAS: `${STREAM_BASE_URL}/streamDataSource/listSchemas`,
    GET_SCHEMA_TABLE_COLUMN: `${STREAM_BASE_URL}/streamDataSource/listPollTableColumn`,
    POLL_PREVIEW: `${STREAM_BASE_URL}/streamDataSource/pollPreview`,
    GET_DATA_PREVIEW: `${STREAM_BASE_URL}/streamDataSource/getTopicData`, // 获取kafka topic预览数据
    ASSET_PREVIEW_DATA: `${STREAM_BASE_URL}/metaDataSource/preview`, // oracle 数据预览
    ASSET_PREVIEW_KAFKA: `${STREAM_BASE_URL}/metaDataSource/getTopicData`, // kafka 数据预览
    GET_TASK: `${STREAM_BASE_URL}/streamTask/getTaskById`, // 通过ID获取任务
    SAVE_TASK: `${STREAM_BASE_URL}/streamTask/addOrUpdateTask`, // 添加或者更新任务
    FORCE_UPDATE_TASK: `${STREAM_BASE_URL}/streamTask/forceUpdate`, // 强制更新
    UPDATE_TASK_RES: `${STREAM_BASE_URL}/streamTask/updateTaskResource`, // 任务添加资源
    GET_TOPIC_TYPE: `${STREAM_BASE_URL}/streamDataSource/getKafkaTopics`, // 获取Topic
    GET_RESTFUL_DATA_PREVIEW: `${STREAM_BASE_URL}/streamTask/pollPreviewByRestfulAPI`, // 数据预览
    GET_BINLOG_LIST_BY_SOURCE: `${STREAM_BASE_URL}/streamDataSource/getBinLogListBySource`, // 获取binlog列表
    GET_SLOT_LIST: `${STREAM_BASE_URL}/streamDataSource/listSlot`, // 获取slot列表
    GET_REALTIME_GUIDE_TARGET_LIST: `${STREAM_BASE_URL}/supportDataSources/getRealtimeGuidedModeTargetList`, // 获取支持实时采集选择目标的数据源类型
    GET_STREAM_TABLECOLUMN: `${STREAM_BASE_URL}/streamDataSource/tablecolumn`, // 输出tablecolumn
	GET_TOPIC_PARTITION_NUM: `${STREAM_BASE_URL}/streamDataSource/getTopicPartitionNum`, // 获取最大读取并发数

    GET_RES_LIST: `${STREAM_BASE_URL}/streamResource/getResources`, // 资源列表
    GET_ALL_STRATEGY: `${STREAM_BASE_URL}/streamStrategy/getStrategyByProjectId`, // 获取所有策略
    STREAM_QUERY_DATA_SOURCE: `${STREAM_BASE_URL}/streamDataSource/pageQuery`, // 查询数据源接口
    GET_FLINK_VERSION: `${STREAM_BASE_URL}/tenant/getFlinkVersion`, // 获取flink版本
    GET_CREATE_TYPE: `${STREAM_BASE_URL}/metaDataSource/checkAssets`, // 获取表来源
    GET_TABLE_LIST: `${STREAM_BASE_URL}/metaDataSource/getTableList`, // 获取表集合
    GET_TABLE_DETAIL: `${STREAM_BASE_URL}/metaDataSource/getTableDetail`, // 获取表详情
    GET_TIMEZONE_LIST: `${STREAM_BASE_URL}/streamTask/getAllTimeZone`, // 获取源表中的时区列表
    GET_DB_LIST: `${STREAM_BASE_URL}/metaDataSource/getDbList`, // 获取数据库集合
    GET_TABLE_TYPE: `${STREAM_BASE_URL}/streamDataSource/tableLocation`, // hive or kudu
    GET_HIVE_PARTITIONS: `${STREAM_BASE_URL}/streamDataSource/getHivePartitions`, // 获取hive表分区
    GET_INCEPTOR_PARTITIONS: `${STREAM_BASE_URL}/streamDataSource/getInceptorPartitions`, // 获取 inceptor 表分区

    GET_SOURCE_TABLE_TYPES: `${STREAM_BASE_URL}/supportDataSources/getFlinkSqlSourceTableTypes`, // 获取源表数据源类型
    GET_RESULT_TABLE_TYPES: `${STREAM_BASE_URL}/supportDataSources/getFlinkSqlResultTableTypes`, // 获取结果表数据源类型
    GET_DIMENSION_TABLE_TYPES: `${STREAM_BASE_URL}/supportDataSources/getFlinkSqlDimensionTableTypes`, // 获取维表数据源类型
};
