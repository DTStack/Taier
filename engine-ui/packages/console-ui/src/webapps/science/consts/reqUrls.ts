// 从config文件全局读取
import { SCIENCE_BASE_URL } from 'config/base';

export default {

    // ================== Common ================== //
    GET_USER_BY_ID: `${SCIENCE_BASE_URL}/common/user/getUserById`, // 根据用户ID获取用户
    DOWNLOAD_PMML: `${SCIENCE_BASE_URL}/download/scienceDownload/downloadPmml`, // 下载模型 （导出pmml文件）
    GET_ALL_PROJECTS: `${SCIENCE_BASE_URL}/service/project/getProjects`, // 获取所有的项目列表
    GET_PROJECT_LIST: `${SCIENCE_BASE_URL}/common/project/listProject`, // 获取项目列表
    GET_PROJECT_DETAIL: `${SCIENCE_BASE_URL}/common/project/getProject`, // 项目详情
    CREATE_PROJECT: `${SCIENCE_BASE_URL}/common/project/createProject`, // 创建项目
    GET_TOP_PROJECT: `${SCIENCE_BASE_URL}/common/projectHit/listProjectTop5`, // 项目TOP5列表
    UPDATE_PROJECT: `${SCIENCE_BASE_URL}/common/project/updateProject`, // 更新项目
    GET_ALL_JOB_STATUS: `${SCIENCE_BASE_URL}/service/maintain/queryAllJobStatusStatistics`, // 所有项目指标统计
    GET_JOB_STATUS: `${SCIENCE_BASE_URL}/service/maintain/queryJobStatusStatistics`, // 获取项目任务执行情况
    GET_JOB_GRAPH: `${SCIENCE_BASE_URL}/service/maintain/getJobGraph`, // 获取项目实例运行情况
    QUERY_TASK: `${SCIENCE_BASE_URL}/service/maintain/queryTask`, // 查询任务接口
    FROZEN_TASK: `${SCIENCE_BASE_URL}/service/maintain/frozenTask`, // 冻结/解冻实验或者作业
    EXEC_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceJob/startSqlImmediately`, // 开始执行nb
    POLL_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceJob/selectData`, // 开始轮询执行状态
    STOP_EXEC_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceJob/stopJob`, // 停止执行nb
    FORMAT_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/formatSql`, // 格式化SQL
    DOWNLOAD_SQL_RESULT: `${SCIENCE_BASE_URL}/download/dataBaseManagement/downloadSqlExeResult`, // 下载执行结果
    GET_SQL_RESULT: `${SCIENCE_BASE_URL}/dataBaseManagement/selectData`, // 获取SQL结果
    GET_SYS_PARAMS: `${SCIENCE_BASE_URL}/service/scienceTask/getSysParams`, // 获取系统参数

    GET_CATALOGUES: `${SCIENCE_BASE_URL}/service/scienceCatalogue/getCatalogue`, // 获取目录
    ADD_FOLDER: `${SCIENCE_BASE_URL}/service/scienceCatalogue/addCatalogue`, // 添加文件夹
    UPDATE_FOLDER: `${SCIENCE_BASE_URL}/service/scienceCatalogue/updateCatalogue`, // 更新文件夹
    DELETE_FOLDER: `${SCIENCE_BASE_URL}/service/scienceCatalogue/deleteCatalogue`, // 删除文件夹

    GET_SUPPORT_TASK_TYPES: `${SCIENCE_BASE_URL}/service/scienceTask/getSupportJobTypes`, // 获取项目任务类型
    GET_TASK_BY_ID: `${SCIENCE_BASE_URL}/service/scienceTask/getTaskById`, // 获取Task
    ADD_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceTask/addOrUpdateTask`, // 添加notebook
    OPEN_NOTEBOOK: `${SCIENCE_BASE_URL}/openNotebook`, // 打开notebook
    SEARCH_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceTask/globalSearch`, // 搜索notebook
    DELETE_NOTEBOOK: `${SCIENCE_BASE_URL}/service/scienceTask/deleteTask`, // 删除notebook

    ADD_EXPERIMENT: `${SCIENCE_BASE_URL}/service/scienceTask/addOrUpdateTask`, // 添加实验
    SEARCH_EXPERIMENT: `${SCIENCE_BASE_URL}/service/scienceTask/globalSearch`, // 搜索实验
    DELETE_EXPERIMENT: `${SCIENCE_BASE_URL}/service/scienceTask/deleteTask`, // 删除实验
    SUBMIT_TASK: `${SCIENCE_BASE_URL}/service/scienceTask/publishTask`, // 提交任务
    GET_EVALUATE_REPORT_TABLE_DATA: `${SCIENCE_BASE_URL}/service/scienceTask/getData`, // 获取临时表数据

    GET_MODEL_PARAMS_LIST: `${SCIENCE_BASE_URL}/service/scienceModel/listModel`, // 获取model 参数列表
    LOAD_MODEL: `${SCIENCE_BASE_URL}/service/scienceModel/load`, // load Model

    GET_EXPERIMENT_TASK_BY_ID: `${SCIENCE_BASE_URL}/service/scienceTask/getTaskById`, // 获取组件数据
    UPDATE_TASK: `${SCIENCE_BASE_URL}/service/scienceTask/addOrUpdateTask`, // 更新组件参数
    GET_COMPONENT_RUNNING_LOG: `${SCIENCE_BASE_URL}/service/scienceJob/getJobLog`, // 获取组件运行日志
    GET_INPUT_TABLE_COLUMNS: `${SCIENCE_BASE_URL}/service/scienceTask/getInputTableColumns`, // 获取组件相关的表数据
    CLONE_COMPONENT: `${SCIENCE_BASE_URL}/service/scienceTask/cloneComponentTask`, // 复制组件
    GET_TABLENAME_BY_NAME: `${SCIENCE_BASE_URL}/service/dataManager/getTableNameList`, // 获取表名列表(模糊查询)
    IS_PARTITION_TABLE: `${SCIENCE_BASE_URL}/service/dataManager/isPartitionTable`, // 校验是否是分区表
    GET_COLUMNS_BY_NAMES: `${SCIENCE_BASE_URL}/service/dataManager/getColumnsByName`, // 获取表的列名
    GET_JOB_ID_BY_TASK: `${SCIENCE_BASE_URL}/service/scienceJob/startAlgorithm`, // 获取执行的任务的jobid
    STOP_TASK_BY_JOB_ID: `${SCIENCE_BASE_URL}/service/scienceJob/stopJobList`, // 停止执行的任务的jobid
    GET_TASK_STATUS_BY_JOB_ID: `${SCIENCE_BASE_URL}/service/scienceJob/checkAlgorithmRunState`, // 获取执行的任务的状态
    GET_EVALUATE_REPORT_CHART_DATA: `${SCIENCE_BASE_URL}/service/dataManager/graph`, // 评估报告图表数据
    COMPONENT_FORMAT_SQL: `${SCIENCE_BASE_URL}/service/dataManager/sqlFormat`, // 评估报告图表数据
    GET_REGRESSION_EVALUATION_GRAPH: `${SCIENCE_BASE_URL}/service/dataManager/regressionEvaluationGraph`, // 回归模型评估 直方图
    GET_CLUSTER_REGRESSTION_GRAPH: `${SCIENCE_BASE_URL}/service/dataManager/clusterRegressionGraph`, // 聚类评估 饼图
    GET_MISSVALUE_GRAPH: `${SCIENCE_BASE_URL}/service/dataManager/confusionMatrixGraph`, // 缺失值矩阵

    // === 模型 === //
    SAVE_MODEL: `${SCIENCE_BASE_URL}/service/scienceModel/saveModel`, // 新增保存模型
    GET_MODEL_VERSIONS: `${SCIENCE_BASE_URL}/service/scienceModel/listVersion`, // 查询模型的全部版本
    LIST_MODEL_TASK_FROM_LAB: `${SCIENCE_BASE_URL}/service/scienceModel/listModelTaskFromLab`, // 返回算法实验任务中的模型任务
    GET_MODEL_LIST: `${SCIENCE_BASE_URL}/service/scienceModel/pageQuery`, // pageQuery模型
    LIST_TASK_ALL_MODEL_AND_VERSION: `${SCIENCE_BASE_URL}/service/scienceModel/listWithTopVersion`, // 查询全部model（包含最高的version）
    GET_MODEL_COMPONENTS_LIST: `${SCIENCE_BASE_URL}/service/scienceTask/getModelComponentList`, // 模型组件列表
    SWITCH_MODEL_VERSION: `${SCIENCE_BASE_URL}/service/scienceModel/switchVersion`, // 切换mode版本
    DELETE_MODEL: `${SCIENCE_BASE_URL}/service/scienceModel/deleteModel`, // 删除model
    OPEN_MODEL: `${SCIENCE_BASE_URL}/service/scienceModel/openModel`, // 开启model
    DISABLE_MODEL: `${SCIENCE_BASE_URL}/service/scienceModel/disableModel`, // 禁用model
    // ================== table ================== //
    GET_TABLE: `${SCIENCE_BASE_URL}/service/dataManager/getTable`,
    CHECK_TABLE_PARTITION: `${SCIENCE_BASE_URL}/service/dataManager/checkPartitionExists`, // 检查表分区
    TABLE_CREATE_BY_DDL: `${SCIENCE_BASE_URL}/service/dataManager/ddlCreateTable`, // ddl建表
    GET_TABLES_BY_NAME: `${SCIENCE_BASE_URL}/service/dataManager/getTableList`, // 查询指定名称表信息(支持模糊查询)
    // ==== 数据源 ==== //
    LIST_DATA_SOURCE: `${SCIENCE_BASE_URL}/service/dataManager/listDataSource`, // 数据源列表
    UPLOAD_TABLE_DATA: `${SCIENCE_BASE_URL}/upload/dataManager/importData`, // 创建数据源(上传文件)
    GET_DATA_SOURCE_DETAIL: `${SCIENCE_BASE_URL}/service/dataManager/getDataSourceDetail`, // 数据源详细信息
    UPDATE_DATA_SOURCE: `${SCIENCE_BASE_URL}/service/dataManager/updateDataSource`, // 更新数据源

    // 资源
    GTE_RESOURCE_BY_ID: `${SCIENCE_BASE_URL}/service/scienceResource/getResourceById`,
    ADD_RESOURCE: `${SCIENCE_BASE_URL}/upload/scienceResource/addResource`,
    RENAME_RESOURCE: `${SCIENCE_BASE_URL}/service/scienceResource/renameResource`,
    DELETE_RESOURCE: `${SCIENCE_BASE_URL}/service/scienceResource/deleteResource`
}
