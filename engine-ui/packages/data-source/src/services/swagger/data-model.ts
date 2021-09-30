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

const prefix = '/api/publicService/model';
// 数据模型
export default {
	// 获取模型列表
	getModelList: {
		method: 'post',
		url: `${prefix}/list`,
	},
	// 获取模型详情
	getModelDetail: {
		method: 'get',
		url: `${prefix}/detail`,
	},
	// TODO: 修改接口参数，临时mock
	// 判断模型是否被下游引用
	isModelReferenced: {
		method: 'post',
		url: `${prefix}/checkRef`,
	},
	// 删除模型
	deleteModel: {
		method: 'post',
		url: `${prefix}/delete`,
	},
	// 发布模型
	releaseModel: {
		method: 'post',
		url: `${prefix}/release`,
	},
	// 模型下线
	unreleaseModel: {
		method: 'post',
		url: `${prefix}/offline`,
	},
	// 获取所有可用数据园
	getAllDataSourceList: {
		method: 'get',
		url: `${prefix}/data/sources`,
	},
	// 获取数据源类型，用于列表筛选
	getDataSourceTypeList: {
		method: 'get',
		url: `${prefix}/data/sourceType`,
	},
	// 获取更新方式枚举值
	getDataModelUpdateTypeList: {
		method: 'get',
		url: `${prefix}/data/updateType`,
	},
	// 根据数据源获取schema下拉列表
	getDataModelSchemaList: {
		method: 'get',
		url: `${prefix}/data/schemas`,
	},
	getDataModelTableList: {
		method: 'get',
		url: `${prefix}/data/tables`,
	},
	// SQL预览
	previewSql: {
		method: 'post',
		url: `${prefix}/sqlPreview`,
	},
	getDataModelColumns: {
		method: 'post',
		url: `${prefix}/data/columns`,
	},
	repeatValidate: {
		method: 'post',
		url: `/api/publicService/common/name/repeat`,
	},
	isPartition: {
		method: 'post',
		url: `${prefix}/data/isPart`,
	},
	saveDataModel: {
		method: 'post',
		url: `${prefix}/save`,
	},
	// 版本历史记录
	getVersionHistoryList: {
		method: 'post',
		url: `${prefix}/versionHistory`,
	},
	// 获取版本详情
	getVersionDetail: {
		method: 'get',
		url: `${prefix}/versionDetail`,
	},
	// 版本恢复
	recoverVersion: {
		method: 'post',
		url: `${prefix}/versionRecovery`,
	},
};
