// 从config文件全局读取
import { SCIENCE_BASE_URL } from 'config/base';

export default {

    // ================== Common ================== //
    EXEC_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/startSqlImmediately`, // 开始执行SQL
    STOP_EXEC_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/stopSql`, // 停止执行SQL
    FORMAT_SQL: `${SCIENCE_BASE_URL}/dataBaseManagement/formatSql`, // 格式化SQL
    DOWNLOAD_SQL_RESULT: `/api/analysis/download/dataBaseManagement/downloadSqlExeResult`, // 下载执行结果
    GET_SQL_RESULT: `${SCIENCE_BASE_URL}/dataBaseManagement/selectData`, // 获取SQL结果

    GET_NOTEBOOK_CATALOGUES: `${SCIENCE_BASE_URL}/getCatalogue`, // 获取目录
    GET_EXPERIMENT_CATALOGUES: `${SCIENCE_BASE_URL}/getCatalogue`, // 获取目录
    GET_COMPONENT_CATALOGUES: `${SCIENCE_BASE_URL}/getCatalogue` // 获取目录

}
