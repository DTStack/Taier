package com.dtstack.batch.service.impl;

import com.csvreader.CsvWriter;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.engine.hdfs.service.BatchHadoopSelectSqlService;
import com.dtstack.batch.engine.hdfs.service.SyncDownload;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.enums.DownloadType;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.table.IDataDownloadService;
import com.dtstack.dtcenter.common.enums.ComputeType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.master.vo.action.ActionLogVO;
import com.dtstack.engine.master.vo.action.ActionRetryLogVO;
import com.dtstack.engine.master.impl.ActionService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 下载功能
 * Date: 2018/5/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(BatchDownloadService.class);

    public static final Integer DEFAULT_LOG_PREVIEW_BYTES = 16383;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Resource
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>(\\*|[a-zA-Z0-9_,\\s]*?))\\s+from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    private static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    public IDownload downloadSqlExeResult(String jobId, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot) {

        BatchHiveSelectSql batchHiveSelectSql = batchHiveSelectSqlDao.getByJobId(jobId, tenantId, Deleted.NORMAL.getStatus());
        Preconditions.checkNotNull(batchHiveSelectSql, "不存在该临时查询");

        if (MultiEngineType.HADOOP.getType() != batchHiveSelectSql.getEngineType() &&
                MultiEngineType.LIBRA.getType() != batchHiveSelectSql.getEngineType() &&
                MultiEngineType.TIDB.getType() != batchHiveSelectSql.getEngineType() &&
                MultiEngineType.ORACLE.getType() != batchHiveSelectSql.getEngineType() &&
                MultiEngineType.GREENPLUM.getType() != batchHiveSelectSql.getEngineType()) {
            throw new RdosDefineException("临时表查询仅支持Hadoop、LibrA、TiDB、Oracle、Greenplum 引擎类型的任务");
        }

        MultiEngineType engineType = MultiEngineType.getByType(batchHiveSelectSql.getEngineType());

        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(engineType.getType(), batchHiveSelectSql.getOtherType());
        Preconditions.checkNotNull(dataDownloadService, String.format("暂时不支持引擎类型 %d", engineType.getType()));

        boolean needMask = !roleUserService.isAdmin(userId, batchHiveSelectSql.getProjectId(), isRoot);
        return dataDownloadService.downloadSqlExeResult(jobId, tenantId, projectId, dtuicTenantId, needMask);
    }

    /**
     * @param dtuicTenantId
     * @param tableName
     * @param db
     * @param num
     * @param fieldNameList
     * @param permissionStyle 显示全部字段（包括fieldNameList），无权限字段显示 noPemrission， 需要和fieldNameList配合使用
     * @return
     * @throws Exception
     */
    public List<Object> queryDataFromTable(Long dtuicTenantId, Long projectId, String tableName, String db, Integer num,
                                           List<String> fieldNameList, Boolean permissionStyle,
                                           boolean needMask, Integer engineType) throws Exception {

        Integer otherTypes = null;
        // 特殊处理Hadoop引擎
        if(MultiEngineType.HADOOP.getType() == engineType){
            DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
            if(DataSourceType.IMPALA.getVal().equals(dataSourceType.getVal())){
                // 1 代表Impala
                otherTypes = 1;
            }
        }
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(engineType, otherTypes);
        Preconditions.checkNotNull(dataDownloadService, String.format("暂时不支持引擎类型 %d", engineType));

        return dataDownloadService.queryDataFromTable(dtuicTenantId, projectId, tableName, db, num,
                fieldNameList, permissionStyle, needMask);
    }


    /**
     * 按行数获取job的log
     *
     * @param dtuicTenantId
     * @param taskType      除数据同步和虚节点都可以导出jobLog
     * @param jobId
     * @param byteNum
     * @return
     * @throws Exception
     */
    public String loadJobLog(Long dtuicTenantId, Integer taskType, String jobId, Integer byteNum) {
        logger.info("获取job日志下载器-->jobId:{}", jobId);
        IDownload downloader = null;
        downloader = buildIDownLoad(jobId, taskType, dtuicTenantId, byteNum == null ? DEFAULT_LOG_PREVIEW_BYTES : byteNum);
        logger.info("获取job日志下载器完成-->jobId:{}", jobId);

        if (downloader == null) {
            logger.error("-----日志文件导出失败-----");
            return "";
        }

        StringBuilder result = new StringBuilder();
        while (!downloader.reachedEnd()) {
            Object row = downloader.readNext();
            result.append(row);
        }

        return result.toString();
    }

    /**
     * 返回下载jobLog的downloader
     *
     * @param jobId
     * @param taskType      除数据同步、虚节点和工作流都可以导出jobLog
     * @param dtuicTenantId
     * @return
     * @throws Exception
     */
    public IDownload downloadJobLog(String jobId, Integer taskType, Long dtuicTenantId) {

        return buildIDownLoad(jobId, taskType, dtuicTenantId, Integer.MAX_VALUE);
    }

    private IDownload buildIDownLoad(String jobId, Integer taskType, Long dtuicTenantId, Integer limitNum) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("engineJobId 不能为空");
        }

        if (EJobType.VIRTUAL.getVal().equals(taskType)
                || EJobType.WORK_FLOW.getVal().equals(taskType)
                || EJobType.LIBRA_SQL.getVal().equals(taskType)
                || EJobType.TIDB_SQL.getVal().equals(taskType)
                || EJobType.GREENPLUM_SQL.getVal().equals(taskType)) {
            throw new RdosDefineException("(虚节点、工作流、LIBRA_SQL、greenplum SQL)的任务日志不支持下载");
        }
        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(taskType);
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(multiEngineType.getType());
        Preconditions.checkNotNull(dataDownloadService, String.format("not support engineType %d", multiEngineType.getType()));

        return dataDownloadService.buildIDownLoad(jobId, taskType, dtuicTenantId, limitNum);
    }


    public String downloadAppTypeLog(Long dtuicTenantId, String jobId, Integer limitNum, String logType, Integer taskType) {
        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(taskType);
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(multiEngineType.getType());

        IDownload downloader = dataDownloadService.typeLogDownloader(dtuicTenantId, jobId, limitNum == null ? Integer.MAX_VALUE : limitNum, logType);

        if (downloader == null) {
            logger.error("-----日志文件导出失败-----");
            return "-----日志文件不存在-----";
        }

        StringBuilder result = new StringBuilder();
        while (!downloader.reachedEnd()) {
            Object row = downloader.readNext();
            result.append(row);
        }

        return result.toString();
    }

    /**
     * 根据类型生成下载的文件名
     * @param downloadType 文件下载类型
     * @return
     */
    private String getDownloadFileName(DownloadType downloadType) {
        String downFileNameSuf;
        if (downloadType == DownloadType.TABLE) {
            downFileNameSuf = ".csv";
        } else if (downloadType == DownloadType.LOG) {
            downFileNameSuf = ".log";
        } else if (downloadType == DownloadType.XML) {
            downFileNameSuf = ".xml";
        } else {
            throw new RdosDefineException("未知的文件下载类型");
        }
        return String.format("dtstack_ide_%s%s", UUID.randomUUID().toString(), downFileNameSuf);
    }

    /**
     * 文件下载处理
     * @param response
     * @param iDownload
     * @param downloadType
     * @param jobId
     * @param dtuicTenantId
     * @param tenantId
     * @param userId
     * @param isRoot
     */
    public void handleDownload(HttpServletResponse response, IDownload iDownload, DownloadType downloadType, String jobId,
                               Long dtuicTenantId, Long tenantId, Long userId, Boolean isRoot) {

        String downFileName = getDownloadFileName(downloadType);
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s", downFileName));
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        try {
            if (iDownload == null) {
                writeFileWithEngineLog(response, jobId);
            } else {
                if (downloadType == DownloadType.TABLE) {
                    BatchHiveSelectSql batchHiveSelectSql = batchHiveSelectSqlDao.getByJobId(jobId, tenantId, Deleted.NORMAL.getStatus());
                    if (batchHiveSelectSql == null) {
                        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                            bos.write(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8));
                        }
                        return;
                    }

                    // 非简单查询
                    if (batchHiveSelectSql.getIsSelectSql() == TempJobType.SELECT.getType()) {
                        downloadForQueryTaskTempTable(iDownload, response);
                        // 简单查询
                    } else if (batchHiveSelectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
                        Integer limitNum = null;
                        try {
                            //简单sql中如果包含limit  需要下载对应条数
                            Matcher matcher = BatchHadoopSelectSqlService.SIMPLE_QUERY_PATTERN.matcher(batchHiveSelectSql.getSqlText());
                            if (matcher.find()) {
                                String limitStr = matcher.group("num");
                                if (StringUtils.isNotEmpty(limitStr)) {
                                    limitNum = Integer.parseInt(limitStr);
                                }
                            }
                        } catch (NumberFormatException e) {
                            logger.error("download simple select {} error", batchHiveSelectSql.getSqlText(), e);
                        }
                        downloadForSimpleQueryOriginTable(iDownload, response, limitNum);
                    }
                } else {
                    if (iDownload instanceof SyncDownload) {
                        writeFileWithSyncLog(response, iDownload);
                    } else {
                        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                            while (!iDownload.reachedEnd()) {
                                Object row = iDownload.readNext();
                                bos.write(row.toString().getBytes());
                            }
                        } catch (Exception e) {
                            logger.error("下载日志异常，{}", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("",e);
            if (e instanceof FileNotFoundException) {
                writeFileWithEngineLog(response, jobId);
            } else {
                try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                    bos.write(String.format("下载文件异常:", e.getMessage()).getBytes());
                } catch (Exception e1) {
                    logger.error("", e1);
                }
            }
        } finally {
            if (iDownload != null) {
                try {
                    iDownload.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 下载临时运行表数据（非简单查询）
     * @param downloadInvoke
     * @param response
     * @param aliaRules
     */
    private void downloadForQueryTaskTempTable(IDownload downloadInvoke, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            List<String> alias = downloadInvoke.getMetaInfo();
            for (int i = 0; i < alias.size(); i++) {
                String alia = alias.get(i);
            }
            //下载表数据-添加metaInfo表列名
            bos.write((StringUtils.join(alias, ",") + "\n").getBytes());

            StringWriter writer = new StringWriter();
            int bufferSize = 0;
            while (!downloadInvoke.reachedEnd()) {
                Object rowDataObject =  downloadInvoke.readNext();
                List<Object> rowDataTypeJudge = (List<Object>) rowDataObject;
                if (CollectionUtils.isEmpty(rowDataTypeJudge)) {
                    continue;
                }
                if (rowDataTypeJudge.get(0) instanceof List) {
                    List<List<String>> rowDataList = (List<List<String>>) rowDataObject;
                    for (List<String> row : rowDataList) {
                        writeRowData(row, writer);
                        bufferSize++;
                    }
                } else {
                    List<String> row = (List<String>) rowDataObject;
                    writeRowData(row, writer);
                    bufferSize++;
                }

                if (bufferSize > 1000) {
                    bos.write(writer.toString().getBytes());
                    bufferSize = 0;
                    writer = new StringWriter();
                }
            }
            if (bufferSize > 0) {
                bos.write(writer.toString().getBytes());
            }

        } catch (Exception e) {
            logger.error("downloadForQueryTaskTempTable end with error", e);
        }
    }

    /**
     * 写入行数据
     *
     * @param row
     * @param writer
     * @throws IOException
     */
    private void writeRowData(List<String> row, StringWriter writer) throws IOException {
        CsvWriter csvWriter = new CsvWriter(writer, ',');
        csvWriter.writeRecord(row.toArray(new String[0]));
    }

    /**
     * 下载临时运行表数据（简单查询）
     * @param downloadInvoke
     * @param response
     * @param limit
     */
    private void downloadForSimpleQueryOriginTable(IDownload downloadInvoke, HttpServletResponse response, Integer limit) {
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            //下载表数据-添加metaInfo表列名
            bos.write((StringUtils.join(downloadInvoke.getMetaInfo(), ",") + "\n").getBytes(StandardCharsets.UTF_8));
            StringWriter writer = new StringWriter();
            int bufferSize = 0;
            int readCounter = 0;
            while (!downloadInvoke.reachedEnd()) {
                if ((Objects.nonNull(limit) && readCounter >= limit)) {
                    break;
                }
                List<String> row = (List<String>) downloadInvoke.readNext();
                CsvWriter csvWriter = new CsvWriter(writer, ',');
                csvWriter.writeRecord(row.toArray(new String[0]));
                bufferSize++;
                readCounter++;
                if (bufferSize > 1000) {
                    bos.write(writer.toString().getBytes(StandardCharsets.UTF_8));
                    bufferSize = 0;
                    writer = new StringWriter();
                }
            }
            if (bufferSize > 0) {
                bos.write(writer.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            logger.error("downloadForQueryTaskTempTable end with error", e);
        }
    }

    /**
     * 输出engine提供的日志
     * @param response
     * @param jobId
     */
    private void writeFileWithEngineLog(HttpServletResponse response, String jobId) {
        //hdfs没有日志就下载engine里的日志
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            String log = getLog(jobId);
            if (StringUtils.isNotBlank(log)) {
                bos.write(log.getBytes());
            }
        }catch (Exception e) {
            logger.error("下载engineLog异常，{}", e);
        }
    }

    /**
     * 获取log
     *
     * @param jobId
     */
    private String getLog(String jobId) {
        StringBuilder log = new StringBuilder();
        //hdfs没有日志就下载engine里的日志
        if (StringUtils.isNotBlank(jobId)) {
            ActionLogVO actionLogVO = actionService.log(jobId, ComputeType.BATCH.getType());
            log.append("=====================提交日志========================\n");
            if (StringUtils.isNotBlank(actionLogVO.getLogInfo())) {
                log.append(actionLogVO.getLogInfo().replace("\\n", "\n").replace("\\t", " "));
            }
            log.append("\n\n\n");
            if (StringUtils.isNotBlank(actionLogVO.getEngineLog())) {
                log.append("=====================运行日志========================\n");
                log.append(actionLogVO.getEngineLog().replace("\\n", "\n").replace("\\t", " "));
                log.append("\n\n\n");
            }
            // 添加重试日志
            Map<String, Object> retryParamsMap = Maps.newHashMap();
            retryParamsMap.put("jobId", jobId);
            retryParamsMap.put("computeType", ComputeType.BATCH.getType());
            //先获取engine的日志总数信息
            List<ActionRetryLogVO> actionRetryLogVOs = actionService.retryLog(jobId);
            if (CollectionUtils.isNotEmpty(actionRetryLogVOs)){
                int size = actionRetryLogVOs.size();
                for (int i = size - 1; i >= 0; i--) {
                    log.append(buildRetryLog(actionRetryLogVOs.get(i), size - i));
                }
            }
        }
        return log.toString();
    }

    /**
     * 构建重试日志
     *
     * @param retryLogContent
     * @param pageInfo
     * @return
     */
    private String buildRetryLog(ActionRetryLogVO retryLogContent, Integer pageInfo){
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(retryLogContent)) {
            return "";
        }
        String logInfo = retryLogContent.getLogInfo();
        String engineInfo = retryLogContent.getEngineLog();
        String retryTaskParams = retryLogContent.getRetryTaskParams();
        builder.append("====================第 ").append(pageInfo).append("次重试====================").append("\n");

        if (!Strings.isNullOrEmpty(logInfo)) {
            builder.append("====================LogInfo start====================").append("\n");
            builder.append(logInfo).append("\n");
            builder.append("=====================LogInfo end=====================").append("\n");
        }
        if (!Strings.isNullOrEmpty(engineInfo)) {
            builder.append("==================EngineInfo  start==================").append("\n");
            builder.append(engineInfo).append("\n");
            builder.append("===================EngineInfo  end===================").append("\n");
        }
        if (!Strings.isNullOrEmpty(retryTaskParams)) {
            builder.append("==================RetryTaskParams  start==================").append("\n");
            builder.append(retryTaskParams).append("\n");
            builder.append("===================RetryTaskParams  end===================").append("\n");
        }
        builder.append("==================第").append(pageInfo).append("次重试结束==================").append("\n\n\n");
        return builder.toString();
    }

    /**
     * 输出数据同步任务日志
     * @param response
     * @param downloadInvoke
     */
    private void writeFileWithSyncLog(HttpServletResponse response, IDownload downloadInvoke) {
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            String logInfo = ((SyncDownload) downloadInvoke).getLogInfo()
                    .replace("\\n\"","\n").replace("\\n\\t","\n");
            bos.write(logInfo.getBytes());
        } catch (Exception e) {
            logger.error("下载数据同步任务运行日志异常，{}", e);
        }
    }
}
