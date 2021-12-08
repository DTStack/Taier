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

import { RDOS_BASE_URL } from './req';

const dataManageReq = {
    CREATE_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/createTable`,
    SEARCH_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/showTables`,
    GET_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/getTable`,
    GET_TABLE_BY_NAME: `${RDOS_BASE_URL}/batch/batchTableInfo/getTableByName`,
    GET_TABLE_LIST_BY_PROJECT: `${RDOS_BASE_URL}/batch/batchCatalogue/getProjectTableList`,
    PREVIEW_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/getData`,
    SAVE_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/alterTable`,
    SEARCH_LOG: `${RDOS_BASE_URL}/batch/batchActionRecord/getRecords`,
    GET_CREATE_CODE: `${RDOS_BASE_URL}/batch/batchTableInfo/createSql`,
    DROP_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/dropTable`,
    DDL_CREATE_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/ddlCreateTable`,
    DROP_CREATE_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/dropAndCreateTable`,
    IS_PARTITION_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/isPartitionTable`,
    ADD_TABLE_META: `${RDOS_BASE_URL}/batch/batchTableInfo/addTableMeta`,
    COPY_TABLE_STRUCTURE: `${RDOS_BASE_URL}/batch/batchTableInfo/copyTableStructure`,
    CHECK_TABLE_EXIST: `${RDOS_BASE_URL}/batch/batchTableInfo/isTableExist`,
    GET_TABLES_BY_NAME: `${RDOS_BASE_URL}/batch/batchTableInfo/getTableList`, // 查询指定名称表信息(支持模糊查询)
    CHECK_HDFSLOC_EXIST: `${RDOS_BASE_URL}/batch/batchTableInfo/isHdfsDirExist`,
    GET_TABLE_PARTITION: `${RDOS_BASE_URL}/batch/batchTablePartition/getPartitions`, // 获取表分区信息

    UPLOAD_TABLE_DATA: `${RDOS_BASE_URL}/upload/batch/batchDataImport/importData`, //
    GET_UPLOAD_STATUS: `${RDOS_BASE_URL}/batch/batchDataImport/getImportLocalDataStatus`, // 获取文件上传状态
    CHECK_TABLE_PARTITION: `${RDOS_BASE_URL}/batch/batchTablePartition/checkPartitionExists`, // 检查表分区
    DELETE_BY_IDS: `${RDOS_BASE_URL}/batch/batchApply/deleteByIds`, // 删除表申请&审批记录
    NEW_SEARCH_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/pageQuery`, // 新的查询表的详细信息
    QUERY_TABLE: `${RDOS_BASE_URL}/batch/batchTableInfo/tableQuery`, // 表查询

    GET_USERS_IN_TENANT: `${RDOS_BASE_URL}/common/user/getUsersInTenant`, // 用户列表

    // ========= 数据类目 =========
    GET_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchDataCatalogue/getCatalogue`, // 获取表目录
    ADD_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchDataCatalogue/addCatalogue`, // 增加目录
    DEL_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchDataCatalogue/deleteCatalogue`, // 删除目录
    UPDATE_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/batchDataCatalogue/updateCatalogue`, // 更新目录
    ADD_TABLE_TO_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/updateCatalogue`, // 添加表到数据类目
    DEL_TABLE_IN_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/deleteTableCatalogue`, // 删除数据类目中的表
    APPLY_TABLE: `${RDOS_BASE_URL}/batch/batchApply/apply`, // 申请资源

    // ========= 血缘关系 =========
    GET_REL_TABLE_TREE: `${RDOS_BASE_URL}/batch/batchTableBlood/getTree`, // 获取血缘关系树
    GET_REL_TABLE_INFO: `${RDOS_BASE_URL}/batch/batchTableBlood/getTableInfo`, // 获取关系表详情
    GET_PARENT_REL_TABLES: `${RDOS_BASE_URL}/batch/batchTableBlood/getUpstream`, // 获取父级血缘关系
    GET_CHILD_REL_TABLES: `${RDOS_BASE_URL}/batch/batchTableBlood/getDownstream`, // 获取子集血缘关系
    GET_REL_TABLE_TASKS: `${RDOS_BASE_URL}/batch/batchTableBlood/getRelateTask`, // 获取血缘表关系任务
    GET_REL_TABLE_COLUMNS: `${RDOS_BASE_URL}/batch/batchTableBlood/getColumns`, // 获取表字段
    GET_REL_TABLE_UP_DOWN_COLUMNS: `${RDOS_BASE_URL}/batch/batchTableBlood/getUpAndDownStreamColumns`, // 获取表上下游字段

    // ========= 脏数据管理 =========
    GET_DIRTY_DATA_TREND: `${RDOS_BASE_URL}/batch/batchDirtyData/dataTrend`, // 脏数据产生趋势
    TOP30_DIRTY_DATA: `${RDOS_BASE_URL}/batch/batchDirtyData/dataTop`, // 脏数据产生TOP30任务
    GET_DIRTY_DATA_TABLES: `${RDOS_BASE_URL}/batch/batchDirtyData/getTables`, // 脏数据表列表
    GET_PUB_SYNC_TASK: `${RDOS_BASE_URL}/batch/batchDirtyData/getPubSyncTask`, // 已发布的数据同步任务
    GET_DIRTY_DATA_TABLE_INFO: `${RDOS_BASE_URL}/batch/batchDirtyData/tableInfo`, // 脏数据表详情
    GET_DIRTY_TABLE_OVERVIEW: `${RDOS_BASE_URL}/batch/batchDirtyData/dataOverview`, // 脏数据概览
    COUNT_DIRTY_DATA: `${RDOS_BASE_URL}/batch/batchDirtyData/countInfo`, // 脏数据统计信息
    GET_DIRTY_DATA_ANALYTICS: `${RDOS_BASE_URL}/batch/batchDirtyData/reason`, // 原因分析

    // ========= 表权限管理 =========
    APPLY_LIST: `${RDOS_BASE_URL}/batch/batchApply/pageQuery`, // 申请列表
    APPLY_SOURCE: `${RDOS_BASE_URL}/batch/batchApply/apply`, // 申请资源
    APPLY_CANCEL: `${RDOS_BASE_URL}/batch/batchApply/cancel`, // 取消申请
    REVOKE_PERMISSION: `${RDOS_BASE_URL}/batch/batchApply/revoke`, // 撤回权限
    APPLY_HANDLE: `${RDOS_BASE_URL}/batch/batchApply/getRelateTask`, // 申请处理
    ADD_MARK: `${RDOS_BASE_URL}/batch/batchTableCollect/collect`, // 取消收藏
    CANCEL_MARK: `${RDOS_BASE_URL}/batch/batchTableCollect/cancelCollect`, // 取消收藏
    APPLY_REPLY: `${RDOS_BASE_URL}/batch/batchApply/reply`, // 申请处理
    GET_DDL_LIST: `${RDOS_BASE_URL}/batch/batchTableInfo/getDDLOperators`, // 获取DDL
    GET_DML_LIST: `${RDOS_BASE_URL}/batch/batchTableInfo/getDMLOperators`, // 获取DML
    GET_TABLE_PERMISSION: `${RDOS_BASE_URL}/batch/batchTableInfo/getTablePermission`, // 获取表列表
    GET_SIMPLE_COLUMNS: `${RDOS_BASE_URL}/batch/batchTableInfo/getSimpleColumns`, // 获取字段名
    GET_APPLY_DETAIL: `${RDOS_BASE_URL}/batch/batchApply/getApplyDetail`, // 获取字段权限详情

    // ===== 项目表数据统计 ===== //
    PROJECT_TABLE_COUNT: `${RDOS_BASE_URL}/batch/batchTableCount/tableCount`, // 表总量
    PROJECT_STORE_COUNT: `${RDOS_BASE_URL}/batch/batchTableCount/totalSize`, // 表总存储量
    PROJECT_STORE_TOP: `${RDOS_BASE_URL}/batch/batchTableCount/projectSizeTopOrder`, // 项目占用排行
    PROJECT_TABLE_STORE_TOP: `${RDOS_BASE_URL}/batch/batchTableCount/tableSizeTopOrder`, // 表占用排行
    PROJECT_DATA_OVERVIEW: `${RDOS_BASE_URL}/batch/batchTableCount/dataHistory`, // 数据趋势概览

    // ===== 数据脱敏 ===== //
    // 脱敏管理
    GET_DESENSITIZATION_LIST: `${RDOS_BASE_URL}/batch/dataMaskConfig/listConfigs`, // 获取数据脱敏列表
    ADD_DESENSITIZATION: `${RDOS_BASE_URL}/batch/dataMaskConfig/createDataMaskConfig`, // 添加脱敏
    CHECK_PERMISSION: `${RDOS_BASE_URL}/batch/dataMaskConfig/voidCheckPermission`, // 检查是否有权限
    DEL_DESENSITIZATION: `${RDOS_BASE_URL}/batch/dataMaskConfig/deleteConfig`, // 删除脱敏
    GET_TABLE_LIST: `${RDOS_BASE_URL}/batch/dataMaskConfig/getTableListByProjectId`, // 获取表列表
    GET_COLUMNS_LIST: `${RDOS_BASE_URL}/batch/dataMaskConfig/getTableColumns`, // 获取表字段
    GET_DESRULES_LIST: `${RDOS_BASE_URL}/batch/dataMaskRule/getRulesByTenantId`, // 获取脱敏规则列表
    CHECK_UPWARD_COLUMNS: `${RDOS_BASE_URL}/batch/dataMaskConfig/checkUpwardColumns`, // 检查是否上游字段
    VIEW_TABLE_RELATION: `${RDOS_BASE_URL}/batch/dataMaskConfig/getRelatedTables`, // 根据脱敏名称查看关系明细
    GET_RELATED_PROJECTS: `${RDOS_BASE_URL}/batch/dataMaskConfig/getRelatedPorjects`, // 获取关联项目
    UPDATE_OPEN_STATUS: `${RDOS_BASE_URL}/batch/dataMaskConfig/enable`, // 切换开关状态
    // 血缘
    GET_TREE: `${RDOS_BASE_URL}/batch/dataMaskConfig/getTree`, // 表查看血缘
    GET_UPCOLUMNS: `${RDOS_BASE_URL}/batch/dataMaskConfig/getUpwardColumns`, // 获取上游字段
    GET_DOWNCOLUMNS: `${RDOS_BASE_URL}/batch/dataMaskConfig/getDownwardColumns`, // 获取下游字段
    UPDATA_LINEAGE_STATUS: `${RDOS_BASE_URL}/batch/dataMaskConfig/enableLineageChain`, // 链路脱敏启用/禁用
    // 规则管理
    GET_RULE_LIST: `${RDOS_BASE_URL}/batch/dataMaskRule/listRules`, // 获取规则列表
    ADD_RULE: `${RDOS_BASE_URL}/batch/dataMaskRule/createRule`, // 添加规则
    EDIT_RULE: `${RDOS_BASE_URL}/batch/dataMaskRule/getRuleById`, // 编辑规则
    UPDATE_RULE: `${RDOS_BASE_URL}/batch/dataMaskRule/updateRule`, // 确定更新规则
    DEL_RULE: `${RDOS_BASE_URL}/batch/dataMaskRule/deleteRule`, // 删除规则
};

export default dataManageReq;
