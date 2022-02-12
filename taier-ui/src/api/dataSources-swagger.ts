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

//每个模块定义的相关接口
// const api = '/api/publicService';
const api = '/taier/datasource';

export default {
	getOfflineTableList: {
		method: 'post',
		url: `${api}/addDs/tablelist`,
	},
	getOfflineTableColumn: {
		method: 'post',
		url: `${api}/addDs/tablecolumn`,
	},
	getOfflineColumnForSyncopate: {
		method: 'post',
		url: `${api}/addDs/columnForSyncopate`,
	},
	getHivePartitions: {
		method: 'post',
		url: `${api}/addDs/getHivePartitions`,
	},
	getDataPreview: {
		method: 'post',
		url: `${api}/addDs/preview`,
	},
	getAllSchemas: {
		method: 'post',
		url: `${api}/addDs/getAllSchemas`,
	},
	queryByTenantId: {
		method: 'get',
		url: `${api}/dataSource/queryByTenantId`,
	},
	//数据源列表分页信息
	dataSourcepage: {
		method: 'post',
		url: `${api}/dataSource/page`,
	},
	//数据源类型下拉列表
	typeList: {
		method: 'post',
		url: `${api}/dataSource/type/list`,
	},
	// 授权产品下拉列表
	productList: {
		method: 'post',
		url: `${api}/dataSource/product/list`,
	},
	//删除一条数据源实例
	dataSourceDelete: {
		method: 'post',
		url: `${api}/dataSource/delete`,
	},
	//产品授权界面
	authProductList: {
		method: 'post',
		url: `${api}/dataSource/auth/product/list`,
	},
	// 产品授权
	dataSoProAuth: {
		method: 'post',
		url: `${api}/dataSource/product/auth`,
	},
	//获取数据源分类类目列表
	queryDsClassifyList: {
		method: 'post',
		url: `${api}/addDs/queryDsClassifyList`,
	},
	//根据分类获取数据源类型
	queryDsTypeByClassify: {
		method: 'post',
		url: `${api}/addDs/queryDsTypeByClassify`,
	},
	//根据数据源类型获取版本列表
	queryDsVersionByType: {
		method: 'post',
		url: `${api}/addDs/queryDsVersionByType`,
	},
	//根据数据库类型和版本查找表单模版
	findTemplateByTypeVersion: {
		method: 'post',
		url: `${api}/dsForm/findFormByTypeVersion`,
	},
	// 获取数据源与租户交集的产品列表
	queryProductList: {
		method: 'post',
		url: `${api}/addDs/queryAppList`,
	},
	// 添加数据源
	addDatasource: {
		method: 'post',
		url: `${api}/addDs/addOrUpdateSource`,
	},
	//上传Kerberos添加数据源
	addOrUpdateSourceWithKerberos: {
		method: 'postForm',
		url: `${api}/addDs/addOrUpdateSourceWithKerberos`,
	},
	// 测试联通性
	testCon: {
		method: 'post',
		url: `${api}/addDs/testCon`,
	},
	// 上传Kerberos测试联通性
	testConWithKerberos: {
		method: 'postForm',
		url: `${api}/addDs/testConWithKerberos`,
	},
	// 获取数据源基本详情
	detail: {
		method: 'post',
		url: `${api}/dataSource/detail`,
	},
	//解析kerberos文件获取principal列表
	uploadCode: {
		method: 'postForm',
		url: `${api}/addDs/getPrincipalsWithConf`,
	},
	getCreateTargetTable: {
		method: 'post',
		url: `${api}/addDs/getCreateTargetTableSql`,
	},
	createDdlTable: {
		method: 'post',
		url: `${api}/addDs/ddlCreateTable`,
	},
};
