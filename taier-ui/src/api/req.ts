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
export const RDOS_BASE_URL = '/taier';

const req = {
	// ===== 用户模块 ===== //
	// LOGIN: `${RDOS_BASE_URL}/user/login`,
	ADD_ROLE_USRE: `${RDOS_BASE_URL}/common/roleUser/addRoleUser`, // 项目用户
	GET_NOT_PROJECT_USERS: `${RDOS_BASE_URL}/common/project/getUsersNotInProject`, // 获取所以用户
	LOGOUT: `/uic/api/v2/logout`,
	APP_LOGOUT: `${RDOS_BASE_URL}/login/out`,
	GET_IS_STANDE_ALONE: `${RDOS_BASE_URL}/batch/tenant/isStandeAlone`,
	UPDATE_USER_ROLE: `${RDOS_BASE_URL}/common/roleUser/updateUserRole`, // 更新用户角色
	GET_USER_BY_ID: `${RDOS_BASE_URL}/common/user/getUserById`, // 根据用户ID获取用户
	SEARCH_UIC_USERS: `${RDOS_BASE_URL}/common/project/getUicUsersNotInProject`,
	GET_TENANT_LIST: `${RDOS_BASE_URL}/batch/tenant/getUserTenants`,
	TRACK_USER_ACTIONS: `${RDOS_BASE_URL}/common/securityAudit/enterApp`, // 捕获用户行为

	// ===== 项目模块 ===== //
	CREATE_PROJECT: `${RDOS_BASE_URL}/common/project/createProject`, // 创建项目
	GET_PROJECT_CATALOGUE: `${RDOS_BASE_URL}/common/batchCatalogue/getProjectCatalogue`, // 获取项目目录
	GET_PROJECT_SUMMARY: `${RDOS_BASE_URL}/common/project/getHomePages`, // 项目概况
	REMOVE_USER_FROM_PROJECT: `${RDOS_BASE_URL}/common/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
	ADD_PROJECT_USER: `${RDOS_BASE_URL}/common/roleUser/addRoleUserNew`, // 添加项目用户接口
	QUERY_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/queryProjects`, // 查询项目列表
	GET_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/getProjects`, // 获取当前用户有权限的项目列表
	GET_ALL_PROJECTS: `${RDOS_BASE_URL}/common/project/getAllProjects`, // 获取所以项目列表
	GET_ALL_TENANT_PROJECTS: `${RDOS_BASE_URL}/common/project/getTenantProjects`, // 获取租户下所有项目
	GET_PROJ_SUPPORT_ENGINE: `${RDOS_BASE_URL}/common/project/getProjectSupportEngineType`, // 项目支持的引擎
	GET_PROJECT_BY_TENANT: `${RDOS_BASE_URL}/batch/project/getAllByTenantId`, // 查询租户下的项目列表
	GET_USRE_PROJECTS: `${RDOS_BASE_URL}/common/project/getProjectUserIn`, // 获取用户所在的所有项目
	GET_PROJECT_USERS: `${RDOS_BASE_URL}/common/project/getProjectUsers`, // 获取所在的项目所有用户
	GET_PROJECT_BY_ID: `${RDOS_BASE_URL}/common/project/getProjectByProjectId`, // 获取项目详情
	UPDATE_PROJECT_INFO: `${RDOS_BASE_URL}/common/project/updateProjectInfo`, // 修改项目描述
	GET_PROJECT_INFO: `${RDOS_BASE_URL}/common/project/getProjectInfo`, // 获取项目信息包括告警
	GET_PROJECT_LIST_INFO: `${RDOS_BASE_URL}/common/project/getProjectList`, // 首页project查询
	SET_STICKY: `${RDOS_BASE_URL}/common/project/setSticky`, // 首页project查询
	DELETE_PROJECT: `${RDOS_BASE_URL}/common/project/deleteProject`, // 删除项目接口
	DELETE_PROJECT_PRO: `${RDOS_BASE_URL}/common/project/preDeleteProject`, // 删除项目接口
	UPDATE_PROJECT_SCHEDULE: `${RDOS_BASE_URL}/common/project/closeOrOpenSchedule`, // 开启或关闭项目调度
	UPDATE_PROJECT_ALLOW_DOWNLOAD: `${RDOS_BASE_URL}/common/project/closeOrOpenDownloadSelect`, // 开启/关闭允许select结果的下载
	UPDATE_PROJECT_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/updateProjectAlarm`, // 更新发送任务运行报告数据
	UPDATE_PROJECT_ALARM_STATUS: `${RDOS_BASE_URL}/batch/batchAlarm/updateProjectAlarmStatus`, // 更新项目内任务运行情况报告是否发送
	GET_PROJECT_ALARM: `${RDOS_BASE_URL}/batch/batchAlarm/getProjectAlarm`, // 获取发送报告详情
	BIND_PRODUCTION_PROJECT: `${RDOS_BASE_URL}/common/project/bindingProject`, // 绑定生产项目
	GET_COULD_BINDING_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/getBindingProjects`, // 获取可以绑定的项目
	GET_TABLE_LIST_FROM_DATABASE: `${RDOS_BASE_URL}/common/project/getTableList`, // 从目标数据库获取表列表
	GET_RETAINDB_LIST: `${RDOS_BASE_URL}/batchComponent/getAllDatabases`, // 获取可以创建项目的数据库--v3.3.0
	GET_SUPPORT_ENGINE_TYPE: `${RDOS_BASE_URL}/common/project/getSupportEngineType`, // 项目支持的引擎
	GET_SUPPORT_TABLE_TYPE: `${RDOS_BASE_URL}/batch/project/getProjectSupportTableType`, // 项目支持表类型
	GET_TENANT_TABLE_TYPE: `${RDOS_BASE_URL}/batch/project/getSupportTableType`, // 租户支持表类型
	GET_PRO_USE_ENGINE: `${RDOS_BASE_URL}/common/project/getProjectUsedEngineInfo`, // 获取项目在用引擎信息
	GET_PRO_UNUSE_ENGINE: `${RDOS_BASE_URL}/common/project/getProjectUnusedEngineInfo`, // 获取项目未接入的引擎信息
	ADD_NEW_ENGINE: `${RDOS_BASE_URL}/common/project/addNewEngine`, // 添加引擎
	GET_DB_TABLE_LIST: `${RDOS_BASE_URL}/common/project/getDBTableList`, // 预览元数据
	CHECK_ADD_ENGINE_STATUS: `${RDOS_BASE_URL}/common/project/hasSuccessInitEngine`, // 检查新增引擎状态
	COMPAREINTRINSICTABLE: `${RDOS_BASE_URL}/common/project/compareIntrinsicTable`, // hive同步页面中获取新增或删除的表名称
	DEALINTRINSICTABLE: `${RDOS_BASE_URL}/common/project/dealIntrinsicTable`, // hive同步页面中同步新增或删除的表
	CHECKDEALSTATUS: `${RDOS_BASE_URL}/common/project/checkDealStatus`, // hive同步页面中获取同步状态的接口
	// ===== 角色管理 ===== //
	GET_ROLE_LIST: `${RDOS_BASE_URL}/common/role/pageQuery`, // 获取角色列表
	UPDATE_ROLE: `${RDOS_BASE_URL}/common/role/addOrUpdateRole`, // 新建/更新角色
	DELETE_ROLE: `${RDOS_BASE_URL}/common/role/deleteRole`, // 删除角色
	GET_ROLE_TREE: `${RDOS_BASE_URL}/common/permission/tree`, // 编辑角色
	GET_ROLE_INFO: `${RDOS_BASE_URL}/common/permission/getPermissionIdsByRoleId`, // 获取角色信息

	// ===== task模块 ===== //
	CLONE_TASK: `${RDOS_BASE_URL}/batch/batchTask/cloneTask`, // 克隆任务
	CLONE_TASK_TO_WORKFLOW: `${RDOS_BASE_URL}/batch/batchTask/cloneTaskToFlow`, // 克隆任务至工作流
	GET_WORKFLOW_LISTS: `${RDOS_BASE_URL}/batch/batchTask/queryTaskByType`, // 获取工作流任务列表
	GET_CREATE_TARGET_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/getCreateTargetTableSql`, // 获取目标表创建sql
	GET_COLUMNS_OF_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/simpleTableColumns`, // 获取表的列名
	GET_ALL_FUNCTION_NAME: `${RDOS_BASE_URL}/batch/batchFunction/getAllFunctionName`, // 获取系统函数

	GET_TABLE_LIST_BY_NAME: `${RDOS_BASE_URL}/batch/batchCatalogue/getTableList`, // 根据表名的表查询
	GET_RECOMMEND_TASK: `${RDOS_BASE_URL}/batch/batchTask/recommendDependencyTask`, // 获取推荐的依赖任务
	CONVERT_TO_HIVE_COLUMNS: `${RDOS_BASE_URL}/batch/batchDataSource/convertToHiveColumns`, // 转换成hive类型的数据类型

	// ==== 发布包 ===== //
	PUBLISH_OFFLINE_PACKAGE: `${RDOS_BASE_URL}/batchPackage/publishPackage`,
	GET_OFFLINE_FUNCTION: `${RDOS_BASE_URL}/batchFunction/pageQuery`, // 函数分页
	GET_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/batchResource/pageQuery`, // 资源分页
	GET_OFFLINE_TASKS: `${RDOS_BASE_URL}/batchTaskShade/pageQuery`, // 已提交任务分页查询
	GET_OFFLINE_LINK_SOURCE: `${RDOS_BASE_URL}/batchDataSource/getDataSourceInBingProject`, // 获取当前项目关联的项目下的数据源列表
	GET_OFFLINE_TASK_LINK_ITEMS: `${RDOS_BASE_URL}/batchPackage/getRelationByTask`, // 获取任务关联的函数资源表
	GET_TABLES: `${RDOS_BASE_URL}/batch/simplePageQuery`, // 获取表
	GET_OFFLINE_PACKAGE_NAME: `${RDOS_BASE_URL}/batchPackage/getPackageName`, // 获取包名
	OFFLINE_CREATE_PACKAGE: `${RDOS_BASE_URL}/batchPackage/createPackage`, // 创建包
	OFFLINE_DELETE_PACKAGE: `${RDOS_BASE_URL}/batchPackage/deletePackage`, // 删除包
	GET_OFFLINE_PACKAGE_LIST: `${RDOS_BASE_URL}/batchPackage/pageQuery`, // 包列表
	GET_OFFLINE_UPLOADPACKAGE_LIST: `${RDOS_BASE_URL}/batch/batchPackage/uploadPackage`, // 导入发布包
	GET_PACKAGE_ITEM_LIST: `${RDOS_BASE_URL}/batchPackageItem/getPackageItemList`, // 查看发布包列表
	GET_PACKAGE_ITEM_STATUS: `${RDOS_BASE_URL}/batchPackageItem/getPackageItemStatus`, // 查看发布包列表
	INIT_UPLOAD_PACKAGE: `${RDOS_BASE_URL}/batchPackage/initUploadPackage`, // 跳过检查继续导入
	GET_PACKAGE_ISHASFTP: `${RDOS_BASE_URL}/batchPackage/isHasFtp`, // 是否开启sftp
	GET_PROJECTS_BY_APP_TYPE: `${RDOS_BASE_URL}/project/getProjectsByAppType`, // 获取模糊查询项目名称
	GET_TASKS_BY_APP_TYPE: `${RDOS_BASE_URL}/batchTask/getTasksByAppType`, // 根据任务名称查询任务信息
	ALL_PRODUCT_GLOBAL_SEARCH: `${RDOS_BASE_URL}/batchTask/allProductGlobalSearch`, // 所有产品的已提交任务查询
};

export default req;
