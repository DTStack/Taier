// 从config文件全局读取
import { SCIENCE_BASE_URL } from 'config/base';

export default {

    // ================== Common ================== //
    EXEC_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/startSqlImmediately`, // 开始执行SQL
    POLL_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/selectData`, // 开始轮询执行状态
    STOP_EXEC_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/stopSql`, // 停止执行SQL
    FORMAT_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/formatSql`, // 格式化SQL
    DOWNLOAD_SQL_RESULT: `/api/analysis/download/dataBaseManagement/downloadSqlExeResult`, // 下载执行结果
    GET_SQL_RESULT: `${SCIENCE_BASE_URL}/dataBaseManagement/selectData`, // 获取SQL结果
    GET_SYS_PARAMS: `${SCIENCE_BASE_URL}/batchTask/getSysParams`, // 获取系统参数

    GET_CATALOGUES: `${SCIENCE_BASE_URL}/getCatalogue`, // 获取目录
    ADD_FOLDER: `${SCIENCE_BASE_URL}/addCatalogue`, // 添加文件夹
    UPDATE_FOLDER: `${SCIENCE_BASE_URL}/updateCatalogue`, // 更新文件夹
    DELETE_FOLDER: `${SCIENCE_BASE_URL}/deleteCatalogue`, // 删除文件夹

    GET_NOTEBOOK_TASK_BY_ID: `${SCIENCE_BASE_URL}/notebook/getTaskById`, // 获取notebookTask
    ADD_NOTEBOOK: `${SCIENCE_BASE_URL}/addOrUpdateNotebook`, // 添加notebook
    OPEN_NOTEBOOK: `${SCIENCE_BASE_URL}/openNotebook`, // 打开notebook
    SUBMIT_NOTEBOOK: `${SCIENCE_BASE_URL}/publishNotebookTask`, // 提交notebook
    SUBMIT_NOTEBOOK_MODEL: `${SCIENCE_BASE_URL}/publishNotebookModel`, // 提交notebook
    SEARCH_NOTEBOOK: `${SCIENCE_BASE_URL}/notebook/globalSearch`, // 搜索notebook
    DELETE_NOTEBOOK: `${SCIENCE_BASE_URL}/notebook/deleteTask`, // 删除notebook

    ADD_EXPERIMENT: `${SCIENCE_BASE_URL}/addOrUpdateExperiment`, // 添加实验
    SEARCH_EXPERIMENT: `${SCIENCE_BASE_URL}/experiment/globalSearch`, // 搜索实验
    DELETE_EXPERIMENT: `${SCIENCE_BASE_URL}/experiment/deleteTask`, // 删除实验

    GET_MODEL_LIST: `${SCIENCE_BASE_URL}/model/list`, // 获取mode列表
    GET_MODEL_VERSIONS: `${SCIENCE_BASE_URL}/model/versions`, // 获取model版本列表
    GET_MODEL_PARAMS_LIST: `${SCIENCE_BASE_URL}/model/getParamsList`, // 获取model 参数列表
    LOAD_MODEL: `${SCIENCE_BASE_URL}/model/load`, // load Model

    GET_EXPERIMENT_TASK_BY_ID: `${SCIENCE_BASE_URL}/service/scienceTask/getTaskById`, // 获取组件数据
    UPDATE_TASK: `${SCIENCE_BASE_URL}/service/scienceTask/addOrUpdateTask`, // 更新组件参数
    GET_INPUT_TABLE_COLUMNS: `${SCIENCE_BASE_URL}/service/scienceTask/getInputTableColumns`, // 获取组件相关的表数据
    GET_TABLENAME_BY_NAME: `${SCIENCE_BASE_URL}/service/dataManager/getTableNameList`, // 获取表名列表(模糊查询)
    IS_PARTITION_TABLE: `${SCIENCE_BASE_URL}/service/dataManager/isPartitionTable`, // 校验是否是分区表
    GET_COLUMNS_BY_NAMES: `${SCIENCE_BASE_URL}/service/dataManager/getColumnsByName`, // 获取表的列名

    // ================== table ================== //
    GET_TABLE: `${SCIENCE_BASE_URL}/batch/batchHiveTableInfo/getTable`,
    CHECK_TABLE_PARTITION: `${SCIENCE_BASE_URL}/batch/batchHiveTablePartition/checkPartitionExists`, // 检查表分区
    UPLOAD_TABLE_DATA: `${SCIENCE_BASE_URL}/upload/batch/batchHiveDataImport/importData`, //
    TABLE_CREATE_BY_DDL: `${SCIENCE_BASE_URL}/batch/batchHiveTableInfo/ddlCreateTable`, // ddl建表
    GET_TABLES_BY_NAME: `${SCIENCE_BASE_URL}/batch/batchHiveTableInfo/getTableList` // 查询指定名称表信息(支持模糊查询)
}
