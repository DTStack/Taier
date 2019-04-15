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

    GET_CATALOGUES: `${SCIENCE_BASE_URL}/getCatalogue`, // 获取目录
    ADD_FOLDER: `${SCIENCE_BASE_URL}/addCatalogue`, // 添加文件夹

    GET_NOTEBOOK_TASK_BY_ID: `${SCIENCE_BASE_URL}/notebook/getTaskById`, // 获取notebookTask
    ADD_NOTEBOOK: `${SCIENCE_BASE_URL}/addOrUpdateNotebook`, // 添加notebook
    OPEN_NOTEBOOK: `${SCIENCE_BASE_URL}/openNotebook`, // 打开notebook

    ADD_EXPERIMENT: `${SCIENCE_BASE_URL}/addOrUpdateExperiment` // 添加实验
}
