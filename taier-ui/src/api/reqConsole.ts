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

export const CONSOLE_BASE_URL = '/taier';
export const CONSOLE_BASE_UPLOAD_URL = '/taier';
export const UIC_BASE_URL = '/uic/api';

export default {
	// ===== 用户相关 ===== //
	LOGIN: `${CONSOLE_BASE_URL}/user/login`, // 登陆
	DA_GET_USER_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
	DA_GET_USER_AUTH_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户权限
	GET_TENANT_LIST: `${CONSOLE_BASE_URL}/tenant/listTenant`, // 租户列表
	GET_META_COMPONENT: `${CONSOLE_BASE_URL}/cluster/getMetaComponent`,
	GET_RESOURCE_USER_LIST: `${CONSOLE_BASE_URL}/tenant/listByQueueId`, // 获取资源已绑定的租户
	BIND_USER_TO_RESOURCE: `${CONSOLE_BASE_URL}/tenant/addToQueue`, // 绑定用户到资源队列
	CONFIRM_SWITCH_QUEUE: `${CONSOLE_BASE_URL}/tenant/updateQueueId`, // 确认切换队列

	ADD_TENANT: `${CONSOLE_BASE_URL}/tenant/addTenant`, // 新增租户
	SWITCH_TENANT: `${CONSOLE_BASE_URL}/user/switchTenant`, // 切换租户
	// 集群
	GET_RESOURCE_LIST: `${CONSOLE_BASE_URL}/cluster/pageQueue`, // 查看资源列表
	GET_CLUSTER_LIST: `${CONSOLE_BASE_URL}/cluster/pageQuery`, // 查看集群列表
	UPDATE_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/update`, // 更新集群信息
	NEW_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/add`, // 新建集群
	TEST_CLUSTER_CONNECT: `${CONSOLE_BASE_URL}/cluster/testConnect`, // 测试集群联通性
	UPLOAD_CLUSTER_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/upload/cluster/config`, // 上传集群资源配置包
	GET_CLUSTER: `${CONSOLE_BASE_URL}/cluster/getOne`, // 获取集群信息
	GET_QUEUE_LISTS: `${CONSOLE_BASE_URL}/cluster/listQueues`, // 集群下队列列表
	// 任务管理
	GET_CLUSTER_DETAIL: `${CONSOLE_BASE_URL}/console/overview`, // 概览-获取集群
	GET_CLUSTER_SELECT: `${CONSOLE_BASE_URL}/cluster/clusters`, // 概览-集群下拉列表
	GET_NODEADDRESS_SELECT: `${CONSOLE_BASE_URL}/console/nodeAddress`, // 获取节点下拉
	// 根据节点搜索

	SEARCH_TASKNAME_LIST: `${CONSOLE_BASE_URL}/console/searchJob`, // 明细-根据任务名搜索任务
	SEARCH_TASKNAME_FUZZY: `${CONSOLE_BASE_URL}/console/listNames`, // 明细-模糊查询任务名
	KILL_TASK: `${CONSOLE_BASE_URL}/console/stopJob`, // 明细-杀任务
	KILL_TASKS: `${CONSOLE_BASE_URL}/console/stopJobList`, // 明细-杀全部任务或选中任务
	KILL_ALL_TASK: `${CONSOLE_BASE_URL}/console/stopAll`, // 杀全部任务
	JOB_STICK: `${CONSOLE_BASE_URL}/console/jobStick`, // 置顶任务

	GET_ENGINE_LIST: `${CONSOLE_BASE_URL}/console/engineTypes`, // 引擎列表
	GET_GROUP_LIST: `${CONSOLE_BASE_URL}/console/groups`, // group列表
	GET_VIEW_DETAIL: `${CONSOLE_BASE_URL}/console/groupDetail`, // 查看明细 和搜索条件
	CHANGE_JOB_PRIORITY: `${CONSOLE_BASE_URL}/console/jobPriority`, // 顺序调整调整优先级
	GET_CLUSTER_RESOURCES: `${CONSOLE_BASE_URL}/console/clusterResources`, // 查看剩余资源

	// 4.0版本
	GET_CLUSTER_INFO: `${CONSOLE_BASE_URL}/cluster/getCluster`,
	UPLOAD_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/upload/component/config`, // 上传配置文件
	DOWNLOAD_RESOURCE: `${CONSOLE_BASE_UPLOAD_URL}/download/component/downloadFile`, // 下载配置文件
	DELETE_CLUSTER: `${CONSOLE_BASE_UPLOAD_URL}/cluster/deleteCluster`, // 删除集群
	DELETE_COMPONENT: `${CONSOLE_BASE_URL}/component/delete`,
	GET_COMPONENT_VERSION: `${CONSOLE_BASE_URL}/component/getComponentVersion`,
	TEST_CONNECT: `${CONSOLE_BASE_URL}/component/testConnect`, // 测试单个组件连通性
	TEST_CONNECTS: `${CONSOLE_BASE_URL}/component/testConnects`, // 测试所有组件连通性
	SAVE_COMPONENT: `${CONSOLE_BASE_URL}/upload/component/addOrUpdateComponent`,
	CLOSE_KERBEROS: `${CONSOLE_BASE_URL}/component/closeKerberos`,
	GET_VERSION: `${CONSOLE_BASE_URL}/component/getComponentVersion`,
	ADD_CLUSTER: `${CONSOLE_BASE_URL}/cluster/addCluster`, // 新增集群
	GET_LOADTEMPLATE: `${CONSOLE_BASE_UPLOAD_URL}/component/loadTemplate`, // 获取上传模板
	UPLOAD_KERBEROSFILE: `${CONSOLE_BASE_UPLOAD_URL}/upload/component/hadoopKerberosConfig`, // 上传kerberos认证文件
	GET_KERBEROSFILE: `${CONSOLE_BASE_URL}/component/getHadoopKerberosFile`, // 获取上传过的kerberos认证文件的信息内容
	TEST_COMPONENT_CONNECT: `${CONSOLE_BASE_URL}/component/testConnections`,
	TEST_COMPONENT_CONNECT_KERBEROS: `${CONSOLE_BASE_UPLOAD_URL}/upload/service/component/testConnections`, // 测试连通性只要有组件开启kerberos认证就掉该接口
	ADD_COMPONENT: `${CONSOLE_BASE_URL}/component/addComponent`,
	SAVE_COMPONENT_KERBEROS: `${CONSOLE_BASE_UPLOAD_URL}/upload/service/component/updateWithKerberos`, // 开启kerberos认证的保存接口
	DELETE_KERBEROS: `${CONSOLE_BASE_URL}/component/rmKerberosConfig`, // 删除Haddop Kerberos认证文件
	ADD_ENGINE: `${CONSOLE_BASE_URL}/engine/addEngine`,
	ADD_ENGINS: `${CONSOLE_BASE_URL}/engine/addEngines`,
	UPDATE_CLUSTER_VERSION: `${CONSOLE_BASE_URL}/cluster/updateGlobalConfig`, // 更新hadoop版本

	// 4.1版本
	GET_COMPONENTSTORE: `${CONSOLE_BASE_URL}/component/getComponentStore`, // 获取存储组件列表
	PARSE_KERBEROS: `${CONSOLE_BASE_URL}/upload/component/parseKerberos`,
	UPLOAD_KERBEROS: `${CONSOLE_BASE_URL}/upload/component/uploadKerberos`, // 上传kerberos文件
	UPDATE_KRB5CONF: `${CONSOLE_BASE_URL}/component/updateKrb5Conf`, // 更新krb5文件

	// 4.2版本
	GET_DTSCRIPT_AGENT_LABEL: `${CONSOLE_BASE_URL}/component/getDtScriptAgentLabel`, // 获取 DtScript Agent 组件节点信息
	GET_CLUSTER_COMPONENT_USER: `${CONSOLE_BASE_URL}/component/getClusterComponentUser`, // 获取 DtScript Agent 组件节点信息
	ADD_OR_UPDATE_COMPONENT_USER: `${CONSOLE_BASE_URL}/component/addOrUpdateComponentUser`, // 更新 DtScript Agent 组件节点信息

	// 资源管理
	GET_ALL_CLUSTER: `${CONSOLE_BASE_URL}/cluster/getAllCluster`,
	GET_ENGINES_BY_CLUSTER: `${CONSOLE_BASE_URL}/cluster/getClusterEngine`,
	SEARCH_TENANT: `${CONSOLE_BASE_URL}/tenant/pageQuery`,
	GET_QUEUE: `${CONSOLE_BASE_URL}/engine/getQueue`,
	BIND_TENANT: `${CONSOLE_BASE_URL}/tenant/bindingTenant`,
	LDAP_ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/bindAccountList`,
	SWITCH_QUEUE: `${CONSOLE_BASE_URL}/tenant/bindingQueue`,
	BIND_NAME_SPACE: `${CONSOLE_BASE_URL}/tenant/bindNamespace`,
	GET_TASKLIMITS: `${CONSOLE_BASE_URL}/tenant/queryTaskResourceLimits`,
	REFRESH_QUEUE: `${CONSOLE_BASE_URL}/component/refresh`,
	TASK_RESOURCE: `${CONSOLE_BASE_URL}/console/getTaskResourceTemplate`,
	// TiDB 引擎账号绑定
	ACCOUNT_UNBIND_LIST: `${CONSOLE_BASE_URL}/account/getTenantUnBandList`,
	ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/bindAccount`,
	UPDATE_ACCOUNT_BIND: `${CONSOLE_BASE_URL}/account/updateBindAccount`,
	ACCOUNT_BIND_LIST: `${CONSOLE_BASE_URL}/account/pageQuery`,
	ACCOUNT_UNBIND: `${CONSOLE_BASE_URL}/account/unbindAccount`,
	GET_FULL_TENANT: `${UIC_BASE_URL}/v2/account/user/get-full-tenants-by-name?tenantName=`, // 获取所有租户
	GET_TENANTS_LIST: `${CONSOLE_BASE_URL}/tenant/dtToken`, // 获取所有租户

	// 告警通道
	ADD_OR_UPDATE_ALARM: `${CONSOLE_BASE_URL}/alert/edit`,
	GET_ALARM_RULE_LIST: `${CONSOLE_BASE_URL}/alert/page`,
	DEL_ALARM_RULE_LIST: `${CONSOLE_BASE_URL}/alert/delete`,
	GET_ALARM_BY_ID: `${CONSOLE_BASE_URL}/alert/getByAlertId`,
	SET_DEFAULT_ALERT: `${CONSOLE_BASE_URL}/alert/setDefaultAlert`,
	TEST_ALERT: `${CONSOLE_BASE_URL}/alert/testAlert`,
	GET_ALARM_CONFIG: `${CONSOLE_BASE_URL}/alert/sftp/get`,
	UPDATE_ALARM_CONFIG: `${CONSOLE_BASE_URL}/alert/sftp/update`,
	TEST_ALARM_CONFIG: `${CONSOLE_BASE_URL}/alert/sftp/testConnect`,

	// 任务运维
	QUERY_TASKS: `${CONSOLE_BASE_URL}/scheduleTaskShade/queryTasks`, // 任务管理 - 搜索-x
	GET_TASK_CHILDREN: `${CONSOLE_BASE_URL}/scheduleTaskTaskShade/displayOffSpring`, // 获取任务自己节点-x
	FIND_TASK_RULE_JOB: `${CONSOLE_BASE_URL}/scheduleJob/findTaskRuleJob`, // 获取补数据、周期 hover 信息-x
	FIND_TASK_RULE_TASK: `${CONSOLE_BASE_URL}/scheduleTaskShade/findTaskRuleTask`, // 获取任务hover信息-x
	GET_TASK_LOG: `${CONSOLE_BASE_URL}/action/log/unite`, // 获取任务告警日志-x
	GET_TASK_TYPESX: `${CONSOLE_BASE_URL}/component/getSupportJobTypes`, // 获取任务类型-x新
	FROZEN_TASK: `${CONSOLE_BASE_URL}/scheduleTaskShade/frozenTask`, // 冻结/解冻任务-x
	QUERY_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/queryJobs`, // 任务运维 - 补数据搜索
	GET_JOB_GRAPH: `${CONSOLE_BASE_URL}/scheduleJob/getJobGraph`, // 今天、昨天、月平均折线图数据-x
	GET_JOB_TOP_TIME: `${CONSOLE_BASE_URL}/scheduleJob/runTimeTopOrder`, // 离线任务运行时长top排序-x
	GET_JOB_TOP_ERROR: `${CONSOLE_BASE_URL}/scheduleJob/errorTopOrder`, // 离线任务错误top排序-x
	PATCH_TASK_DATA: `${CONSOLE_BASE_URL}/scheduleJob/fillTaskData`, // 补数据-x
	STOP_JOB: `${CONSOLE_BASE_URL}/scheduleJob/stopJob`, // 停止任务-x
	BATCH_STOP_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/batchStopJobs`, // 停止任务-x
	BATCH_STOP_JOBS_BY_DATE: `${CONSOLE_BASE_URL}/scheduleJob/stopJobByCondition`, // 按照业务日期杀任务-x
	RESTART_AND_RESUME_JOB: `${CONSOLE_BASE_URL}/scheduleJob/syncRestartJob`, // 重启并恢复任务-x
	BATCH_RESTART_AND_RESUME_JOB: `${CONSOLE_BASE_URL}/scheduleJob/restartJobAndResume`, // 批量重启-x
	GET_FILL_DATA: `${CONSOLE_BASE_URL}/scheduleJob/getFillDataJobInfoPreview`, // 获取补数据-x
	GET_FILL_DATA_DETAIL: `${CONSOLE_BASE_URL}/scheduleJob/getJobGetFillDataDetailInfo`, // 获取补数据详情-x
	GET_JOB_CHILDREN: `${CONSOLE_BASE_URL}/scheduleJobJob/displayOffSpring`, // 获取子job-x
	GET_TASK_PERIODS: `${CONSOLE_BASE_URL}/scheduleJob/displayPeriods`, // 转到前后周期实例-x
	GET_JOB_PARENT: `${CONSOLE_BASE_URL}/scheduleJobJob/displayForefathers`, // 获取父节点-x
	QUERY_JOB_STATISTICS: `${CONSOLE_BASE_URL}/scheduleJob/queryJobsStatusStatistics`, // 查询Job统计-x
	STOP_FILL_DATA_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/stopFillDataJobs`, // 停止补数据任务-x
	GET_RESTART_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/getRestartChildJob`, // 获取restart job列表-x
	GET_APPTYPE: `${CONSOLE_BASE_URL}/action/appType`,
	GET_WORKFLOW_RELATED_TASKS: `${CONSOLE_BASE_URL}/scheduleTaskShade/dealFlowWorkTask`, // 获取工作流的子任务-x
	GET_WORKFLOW_RELATED_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/getRelatedJobs`, // 获取工作流实例的子任务-x
	GET_PROJECT_LIST: `${CONSOLE_BASE_URL}/project/findFuzzyProjectByProjectAlias`, // 根据别名模糊查询项目名称
	USER_QUERYUSER: `${CONSOLE_BASE_URL}/user/queryUser`, // 获取负责人
	GET_TASK_JOB_WORKFLOW_NODES: `${CONSOLE_BASE_URL}/scheduleJobJob/displayOffSpringWorkFlow`, // 刷新任务实例获取工作流节点
	GET_WORKFLOW_FILLDATA_RELATED_JOBS: `${CONSOLE_BASE_URL}/scheduleJob/getRelatedJobsForFillData`, // 补数据工作流子节点
	GET_TASK_WORKFLOW_NODES: `${CONSOLE_BASE_URL}/scheduleTaskTaskShade/getAllFlowSubTasks`, // 查看获取工作流节点
};
