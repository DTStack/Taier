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
};

export default req;
