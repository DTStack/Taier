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
export const RDOS_BASE_URL = '/api/rdos';

const req = {

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
    PUBLISH_OFFLINE_PACKAGE: `${RDOS_BASE_URL}/batch/batchPackage/publishPackage`,
    GET_OFFLINE_FUNCTION: `${RDOS_BASE_URL}/batch/batchFunction/pageQuery`, // 函数分页
    GET_OFFLINE_RESOURCE: `${RDOS_BASE_URL}/batch/batchResource/pageQuery`, // 资源分页
    GET_OFFLINE_TASKS: `${RDOS_BASE_URL}/batch/batchTaskShade/pageQuery`, // 已提交任务分页查询
    GET_OFFLINE_LINK_SOURCE: `${RDOS_BASE_URL}/batch/batchDataSource/getDataSourceInBingProject`, // 获取当前项目关联的项目下的数据源列表
    GET_OFFLINE_TASK_LINK_ITEMS: `${RDOS_BASE_URL}/batch/batchPackage/getRelationByTask`, // 获取任务关联的函数资源表
    GET_TABLES: `${RDOS_BASE_URL}/batch/batchTableInfo/simplePageQuery`, // 获取表
    GET_OFFLINE_PACKAGE_NAME: `${RDOS_BASE_URL}/batch/batchPackage/getPackageName`, // 获取包名
    OFFLINE_CREATE_PACKAGE: `${RDOS_BASE_URL}/batch/batchPackage/createPackage`, // 创建包
    OFFLINE_DELETE_PACKAGE: `${RDOS_BASE_URL}/batch/batchPackage/deletePackage`, // 删除包
    GET_OFFLINE_PACKAGE_LIST: `${RDOS_BASE_URL}/batch/batchPackage/pageQuery`, // 包列表
    GET_OFFLINE_UPLOADPACKAGE_LIST: `${RDOS_BASE_URL}/upload/batch/batchPackage/uploadPackage`, // 导入发布包
    GET_PACKAGE_ITEM_LIST: `${RDOS_BASE_URL}/batch/batchPackageItem/getPackageItemList`, // 查看发布包列表
    GET_PACKAGE_ITEM_STATUS: `${RDOS_BASE_URL}/batch/batchPackageItem/getPackageItemStatus`, // 查看发布包列表
    INIT_UPLOAD_PACKAGE: `${RDOS_BASE_URL}/batch/batchPackage/initUploadPackage`, // 跳过检查继续导入
    GET_PACKAGE_ISHASFTP: `${RDOS_BASE_URL}/batch/batchPackage/isHasFtp`, // 是否开启sftp
    GET_PROJECTS_BY_APP_TYPE: `${RDOS_BASE_URL}/common/project/getProjectsByAppType`, // 获取模糊查询项目名称
    GET_TASKS_BY_APP_TYPE: `${RDOS_BASE_URL}/batch/batchTask/getTasksByAppType`, // 根据任务名称查询任务信息
    ALL_PRODUCT_GLOBAL_SEARCH: `${RDOS_BASE_URL}/batch/batchTask/allProductGlobalSearch`, // 所有产品的已提交任务查询
};

export default req;
