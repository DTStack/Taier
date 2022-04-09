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
	saveTask(task: any) {
		let params = cloneDeep(task);
		if (params.taskType === TASK_TYPE_ENUM.DATA_ACQUISITION) {
			delete params.sourceParams;
			delete params.sinkParams;
			delete params.sideParams;
		} else {
			params.sourceParams = params?.sourceParams;
			params.sinkParams = params?.sinkParams;
			params.sideParams = params?.sideParams;
		}
		params.sqlText = params?.sqlText;

		delete params.dataSourceList;
		// 1.12 时时间特征勾选了 ProcTime，ProcTime 名称字段未填写时需补上 proc_time
		if (params.componentVersion === '1.12' && Array.isArray(params.source)) {
			for (const form of params.source) {
				if (form.timeTypeArr?.includes(1)) {
					form.procTime = form.procTime || 'proc_time';
				}
			}
		}

		params = convertRequestParams(taskParams, params);
		return http.post(req.SAVE_TASK, params).then((res) => {
			res.data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
			return Promise.resolve(res);
		});
	},
	getTask(params: any) {
		return http.post(req.GET_TASK, params).then((res) => {
			res.data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
			if (res.data.taskVersionsStr) {
				res.data.taskVersions = JSON.parse(res.data.taskVersionsStr);
			}
			if (res.data.sideStr) {
				res.data.side = JSON.parse(res.data.sideStr);
			}
			if (res.data.sinkStr) {
				res.data.sink = JSON.parse(res.data.sinkStr);
			}
			if (res.data.sourceStr) {
				res.data.source = JSON.parse(res.data.sourceStr);
			}
			return Promise.resolve(res);
		});
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
};
