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

import { TASK_TYPE_ENUM } from '@/constant';
import { Utils } from '@dtinsight/dt-utils/lib';
import { cloneDeep } from 'lodash';
import moment from 'moment';
import { convertRequestParams, taskParams } from './defaultParams';
import http from './http';
import req from './reqStream';

export default {
    getTargetList (params: {
        dataSourceType: number;
        rdbmsDaType: number;
    }) {
        return http.post(req.GET_REALTIME_GUIDE_TARGET_LIST, params)
    },
    getSupportDaTypes (params?: any) {
        return http.post(req.GET_SUPPORT_BINLOG_DATA_TYPES, params)
    },
    // 获取类型数据源
    getTypeOriginData (params: any) {
        return http.post(req.GET_TYPE_ORIGIN_DATA, params)
    },
    isOpenCdb (params: { sourceId: number }) {
        return http.post(req.IS_OPEN_CDB, params)
    },
    getPDBList (params: { sourceId: number; searchKey?: string }) {
        return http.post(req.GET_PDB_LIST, params)
    },
    getStreamTablelist (params: any) {
        return http.post(req.GET_STREAM_TABLELIST, params)
    },
    getSchemaTableColumn (params: any) {
        return http.post(req.GET_SCHEMA_TABLE_COLUMN, params)
    },
    listSchemas (params: any) {
        return http.post(req.LIST_SCHEMAS, params)
    },
    listTablesBySchema (params: any) {
        return http.post(req.LIST_TABLE_BY_SCHEMA, params)
    },
    // oracle 数据预览
    previewAssetData (params: any) {
        return http.post(req.ASSET_PREVIEW_DATA, params)
    },
    // kafka 数据预览
    previewAssetKafkaData (params: any) {
        return http.post(req.ASSET_PREVIEW_KAFKA, params)
    },
    // 获取kafka topic预览数据
    getDataPreview (params: any) {
        return http.post(req.GET_DATA_PREVIEW, params)
    },
    pollPreview (params: any) {
        return http.post(req.POLL_PREVIEW, params)
    },
    getRestfulDataPreview (params: any) {
        return http.post(req.GET_RESTFUL_DATA_PREVIEW, params)
    },
    getBinlogListBySource (params: any) {
        return http.post(req.GET_BINLOG_LIST_BY_SOURCE, params)
    },
    getSlotList (params: any) {
        return http.post(req.GET_SLOT_LIST, params)
    },
    // 添加或更新任务
    saveTask (task: any) {
        let params = cloneDeep(task);
        if (params.taskType === TASK_TYPE_ENUM.DATA_COLLECTION) {
            delete params.sourceParams;
            delete params.sinkParams;
            delete params.sideParams;
        } else {
            params.sourceParams = Utils.base64Encode(params?.sourceParams);
            params.sinkParams = Utils.base64Encode(params?.sinkParams);
            params.sideParams = Utils.base64Encode(params?.sideParams);
        }
        params.sqlText = Utils.base64Encode(params?.sqlText);

        delete params.dataSourceList;
        // 1.12 时时间特征勾选了 ProcTime，ProcTime 名称字段未填写时需补上 proc_time
        if (params.componentVersion === '1.12' && Array.isArray(params.source)) {
            for (const form of params.source) {
                if (form.timeTypeArr?.includes(1)) {
                    form.procTime = form.procTime || 'proc_time'
                }
            }
        }

        params.targetMapStr = JSON.stringify(params.targetMap)
        params.sourceMapStr = JSON.stringify(params.sourceMap)
        params.settingMapStr = JSON.stringify(params.settingMap)
        if (params.readWriteLockVO) {
            params.readWriteLockVO.gmtCreate = moment(params?.readWriteLockVO?.gmtCreate).unix() * 1000
            params.readWriteLockVO.gmtModified = moment(params?.readWriteLockVO?.gmtModified).unix() * 1000
        }
        params.sideStr = params.side ? JSON.stringify(params.side) : undefined;
        params.sinkStr = params.sink ? JSON.stringify(params.sink) : undefined;
        params.sourceStr = params.source ? JSON.stringify(params.source) : undefined;

        params = convertRequestParams(taskParams, params);
        return http.post(req.SAVE_TASK, params).then(res => {
            res.data = (typeof res.data === 'string' ? JSON.parse(res.data) : res.data);
            return Promise.resolve(res)
        })
    },
    // 强制更新
    forceUpdateTask (task: any) {
        let params = cloneDeep(task);
        params.sideStr = params.side ? JSON.stringify(params.side) : undefined;
        params.sinkStr = params.sink ? JSON.stringify(params.sink) : undefined;
        params.sourceStr = params.source ? JSON.stringify(params.source) : undefined;
        if (params.readWriteLockVO) {
            params.readWriteLockVO.gmtCreate = moment(params?.readWriteLockVO?.gmtCreate).unix() * 1000
            params.readWriteLockVO.gmtModified = moment(params?.readWriteLockVO?.gmtModified).unix() * 1000
        }
        params.targetMapStr = JSON.stringify(params.targetMap)
        params.sourceMapStr = JSON.stringify(params.sourceMap)
        params.settingMapStr = JSON.stringify(params.settingMap)

        params = convertRequestParams(taskParams, params)
        return http.post(req.FORCE_UPDATE_TASK, params).then(res => {
            res.data = (typeof res.data === 'string' ? JSON.parse(res.data) : res.data);
            return Promise.resolve(res)
        })
    },
    getTask (params: any) {
        return http.post(req.GET_TASK, params).then(res => {
            res.data = (typeof res.data === 'string' ? JSON.parse(res.data) : res.data);
            if (res.data.taskVersionsStr) {
                res.data.taskVersions = JSON.parse(res.data.taskVersionsStr)
            }
            if (res.data.sideStr) {
                res.data.side = JSON.parse(res.data.sideStr)
            }
            if (res.data.sinkStr) {
                res.data.sink = JSON.parse(res.data.sinkStr)
            }
            if (res.data.sourceStr) {
                res.data.source = JSON.parse(res.data.sourceStr)
            }
            return Promise.resolve(res)
        })
    },
    getResList (params?: any) {
        return http.post(req.GET_RES_LIST, params)
    },
    // 任务添加资源
    updateTaskRes (params: any) {
        return http.post(req.UPDATE_TASK_RES, params)
    },
    // 数据开发 - 获取启停策略列表
    getAllStrategy () {
        return http.post(req.GET_ALL_STRATEGY)
    },
    // 获取Topic
    getTopicType (params: any) {
        return http.post(req.GET_TOPIC_TYPE, params)
    },
    getStreamTableColumn (params: { schema: string; sourceId: number; tableName: string; flinkVersion: string }) {
        return http.post(req.GET_STREAM_TABLECOLUMN, params)
    },
    getTopicPartitionNum (params: any) {
        return http.post(req.GET_TOPIC_PARTITION_NUM, params)
    },
    getStreamDataSourceList (params: { currentPage: number; pageSize: number; groupTags: string[]; name: string }) {
        return http.post(req.STREAM_QUERY_DATA_SOURCE, params).then(res => {
            res.data.data = res.data?.data ? res.data.data.map((d: any) => {
                d.dataJson = d.dataJson || JSON.parse(d.dataJsonStr);
                return d;
            }) : [];
            return Promise.resolve(res)
        })
    },
    getFlinkVersion () {
        return http.post(req.GET_FLINK_VERSION)
    },
    // 获取表来源
    getCreateTypes () {
        return http.post(req.GET_CREATE_TYPE)
    },
    // 获取表集合
    getAssetTableList (params: any) {
        return http.post(req.GET_TABLE_LIST, params)
    },
    // 获取表详情
    getAssetTableDetail (params: any) {
        return http.post(req.GET_TABLE_DETAIL, params)
    },
    // 获取源表中的时区列表
    getTimeZoneList (params?: any) {
        return http.post(req.GET_TIMEZONE_LIST, params)
    },
    // 获取数据库集合
    getDBList () {
        return http.post(req.GET_DB_LIST)
    },
    // 获取源表数据源类型
    getSourceTableTypes () {
        return http.post(req.GET_SOURCE_TABLE_TYPES)
    },
    // 获取结果表数据源类型
    getResultTableTypes () {
        return http.post(req.GET_RESULT_TABLE_TYPES)
    },
    // 获取维表数据源类型
    getDimensionTableTypes () {
        return http.post(req.GET_DIMENSION_TABLE_TYPES)
    },
    getTableType (params: any) {
        return http.post(req.GET_TABLE_TYPE, params)
    },
    getHivePartitions (params: any) {
        return http.post(req.GET_HIVE_PARTITIONS, params)
    },
    getInceptorPartitions (params: any) {
        return http.post(req.GET_INCEPTOR_PARTITIONS, params)
    },
}