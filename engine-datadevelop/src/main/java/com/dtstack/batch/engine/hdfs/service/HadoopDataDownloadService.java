package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.TenantDao;
import com.dtstack.batch.datamask.bo.MaskRule;
import com.dtstack.batch.datamask.domain.DataMaskRule;
import com.dtstack.batch.datamask.util.DataMaskUtil;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.domain.Tenant;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.hive.service.LogPluginDownload;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.mapping.JobTypeDataSourceTypeMapping;
import com.dtstack.batch.service.datamask.impl.DataMaskColumnInfoService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.job.impl.BatchJobService;
import com.dtstack.batch.service.table.IDataDownloadService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.engine.ConsoleSend;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.ComputeType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.JdbcUrlUtil;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.common.util.RetryUtil;
import com.dtstack.dtcenter.common.util.UrlInfo;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.api.service.ActionService;
import com.dtstack.engine.api.service.ScheduleJobService;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;


/**
 * Hadoop 平台下载数据内容
 * 问题：
 * 1：如果不是写入到临时表的--》无法根据字段排序，无法保证查询的结果和下载结果是一样的。
 * 2：大部分情况下是不需要下载的。不需要查询10w条数据到临时表,--->最好是在查询的时候填写了查询的数据信息
 * Date: 2018/5/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class HadoopDataDownloadService implements IDataDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(HadoopDataDownloadService.class);

    private static final String TEXT_STORE_NULL = "\\N";

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private DataMaskColumnInfoService dataMaskColumnInfoService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private ConsoleSend consoleSend;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private TenantService tenantService;

    /**
     * 下载sql执行结果
     *
     * @param jobId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param needMask
     * @return
     */
    @Override
    public IDownload downloadSqlExeResult(String jobId, Long tenantId, Long projectId, Long dtuicTenantId, boolean needMask) {

        String tableName;
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException("当前下载只支持通过临时表查询的下载");
        }
        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        Integer jobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType.getVal());

        BatchHiveSelectSql batchHiveSelectSql = batchSelectSqlService.getByJobId(jobId, tenantId, Deleted.NORMAL.getStatus());
        tableName = batchHiveSelectSql.getTempTableName();
        if (batchHiveSelectSql.getIsSelectSql() == TempJobType.CARBON_SQL.getType()) {
            JSONObject conf = JSON.parseObject(batchHiveSelectSql.getParsedColumns());
            Table baseInfo = batchDataSourceService.getOrcTableInfoForCarbonData(conf, tableName, conf.getJSONObject("kerberosConfig"));
            return getDownloader(dtuicTenantId,baseInfo.getName(),baseInfo.getDb(), null, null, dataSourceType.getVal());
        }

        ProjectEngine projectEngine = projectEngineService.getProjectDb(batchHiveSelectSql.getProjectId(), MultiEngineType.HADOOP.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support hadoop engine.", projectId));
        // 简单查询逻辑更改
        if (batchHiveSelectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            return getSimpleSelectDownLoader(dtuicTenantId, projectId, batchHiveSelectSql.getSqlText(), needMask, jobType);
        }
        return getDownloader(dtuicTenantId, tableName, projectEngine.getEngineIdentity(), dataSourceType.getVal());
    }

    /**
     * 查询表数据
     *
     * @param dtUicTenantId uic租户id
     * @param projectId  项目id
     * @param tableName 查询表名
     * @param db 查询数据库
     * @param num 查询限制条数
     * @param fieldNameList 查询指定的字段列表，为空、为null或者包含*则默认查询全部字段
     * @param permissionStyle 是否显示全部字段（包括fieldNameList），如果为true则无权限字段显示 NO PERMISSION， 需要和fieldNameList配合使用
     * @param needMask 是否需要脱敏
     * @return
     * @throws Exception
     */
    @Forbidden
    @Override
    public List<Object> queryDataFromTable(Long dtUicTenantId,
                                           Long projectId,
                                           String tableName,
                                           String db,
                                           Integer num,
                                           List<String> fieldNameList,
                                           Boolean permissionStyle,
                                           boolean needMask) throws Exception {
        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        Integer eJobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType.getVal());
        IDownload selectDownLoader = getSelectDownLoader(dtUicTenantId, projectId, fieldNameList.contains("*") ? null : fieldNameList, Lists.newArrayList(fieldNameList), permissionStyle, needMask, db, tableName, null, eJobType);
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtUicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()));
        if (num == null || num > jdbcInfo.getMaxRows()) {
            num = jdbcInfo.getMaxRows();
        }
        List<Object> queryResult = new ArrayList<>();
        // 添加查询字段信息
        queryResult.add(selectDownLoader.getMetaInfo());
        int readCounter = 0;
        while (!selectDownLoader.reachedEnd()) {
            if (readCounter >= num) {
                break;
            }
            queryResult.add(selectDownLoader.readNext());
            readCounter++;
        }
        return queryResult;
    }


    private String dealHiveTextNull(String source) {
        return TEXT_STORE_NULL.equals(source) ? null : source;
    }

    /**
     * 从临时表获取表数据
     *
     * @param dtuicTenantId
     * @param tableName
     * @param db
     * @param selectSql
     * @param needMask
     * @return
     * @throws Exception
     */
    public List<Object> queryDataFromTempTable(Long dtuicTenantId, String tableName, String db, BatchHiveSelectSql selectSql,
                                               Boolean needMask, Long projectId) throws Exception {
        List<Object> result = new ArrayList<>();
        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        IDownload downloader = getDownloader(dtuicTenantId, tableName, db, dataSourceType.getVal());
        Map<Integer, Map<String, String>> tbColumns = Maps.newHashMap();
        if (StringUtils.isNotBlank(selectSql.getParsedColumns())) {
            tbColumns = JSON.parseObject(selectSql.getParsedColumns(), Map.class);
        }
        Map<String, List<DataMaskRule>> aliaRules = dataMaskColumnInfoService.getRelatedMasksForColumnAlia(tbColumns);
        Map<Integer, List<DataMaskRule>> indexRules = Maps.newHashMap();
        List<String> alias = downloader.getMetaInfo();
        for (int i = 0; i < alias.size(); i++) {
            String alia = alias.get(i);
            if (aliaRules.containsKey(alia)) {
                alias.set(i, alia + DataMaskUtil.SIGN_FOR_COLUMNS_NEED_MASK);
                indexRules.put(i, aliaRules.get(alia));
            }
        }
        result.add(alias);
        if (!needMask) {
            indexRules = Maps.newHashMap();
        }

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId,null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()));
        int readCounter = 0;
        while (!downloader.reachedEnd() && readCounter < jdbcInfo.getMaxRows()) {
            List<String> row = (List<String>) downloader.readNext();
            for (Map.Entry<Integer, List<DataMaskRule>> indexRule : indexRules.entrySet()) {
                Integer index = indexRule.getKey();
                List<DataMaskRule> rules = indexRule.getValue();
                //脱敏
                String source = row.get(index);
                MaskRule maskRule = DataMaskColumnInfoService.generateMaskRule(rules, source);
                source = DataMaskUtil.mask(source, maskRule);
                row.set(index, dealHiveTextNull(source));
            }
            result.add(row);
            readCounter++;
        }
        return result;
    }

    /**
     * 通过HiveServer获取临时表数据
     *
     * @param dtuicTenantId
     * @param tableName
     * @param db
     * @return
     * @throws Exception
     */
    public List<Object> queryDataFromHiveServerTempTable(Long dtuicTenantId, String tableName, String db, Long projectId) throws Exception {
        List<Object> result = new ArrayList<>();
        IDownload downloader = getHiveServerDownloader(dtuicTenantId, tableName, db, projectId);
        List<String> alias = downloader.getMetaInfo();
        result.add(alias);

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId,null, ETableType.HIVE);
        int readCounter = 0;
        while (!downloader.reachedEnd() && readCounter < jdbcInfo.getMaxRows()) {
            List<String> row = (List<String>) downloader.readNext();
            result.add(row);
            readCounter++;
        }
        return result;
    }

    public Map<String, List<DataMaskRule>> getDataMaskRules(Long dtuicTenantId, String tableName, Long projectId) {
        Tenant tenant = tenantDao.getByDtUicTenantId(dtuicTenantId);
        BatchTableInfo tb = batchTableInfoService.getTableInfoByTableName(tableName, tenant.getId(), projectId, ETableType.HIVE.getType());
        if (tb == null) {
            return Maps.newHashMap();
        }
        return dataMaskColumnInfoService.getRelatedRulesByTableId(tb.getId());
    }

    public List<Object> queryDataFromCarbonOrcTable(Long dtuicTenantId, String tableName, String jdbcUrl, Long projectId) throws Exception {
        UrlInfo urlInfo = JdbcUrlUtil.getUrlInfo(jdbcUrl);
        IDownload downloader = getDownloader(dtuicTenantId,tableName,urlInfo.getDb(), null, null, DataSourceType.HIVE.getVal());
        int readCounter = 0;
        List<Object> result = new ArrayList<>();
        result.add(downloader.getMetaInfo());

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId,null, ETableType.HIVE);
        while (!downloader.reachedEnd() && readCounter < jdbcInfo.getMaxRows()) {
            List<String> row = (List<String>) downloader.readNext();
            result.add(row);
            readCounter++;
        }
        return result;
    }

    public IDownload getHiveServerDownloader(Long dtuicTenantId, String tableName, String db, Long projectId){
        try {
            return getSelectDownLoader(dtuicTenantId,  projectId, null, null, true, false,
                    db, tableName, null, EJobType.HIVE_SQL.getType());
        } catch (final Exception e) {
            HadoopDataDownloadService.logger.error("",e);
            return null;
        }
    }

    /**
     * 获取简单查询下载器
     *
     * @param dtUicTenantId uic租户id
     * @param projectId 项目id
     * @param sql 查询sql
     * @param needMask 是否需要脱敏
     * @param taskType 任务类型 仅支持sparkSql和hiveSql
     * @return 简单查询结果下载器
     */
    public IDownload getSimpleSelectDownLoader(Long dtUicTenantId, Long projectId, String sql, boolean needMask, Integer taskType){
        Matcher matcher = BatchHadoopSelectSqlService.SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            throw new RdosDefineException("该下载器仅支持简单查询结果下载");
        }
        // 查询字段集合
        List<String> queryFieldNames = BatchHadoopSelectSqlService.getSimpleQueryFieldNames(sql, false);
        // 字段别名集合
        List<String> fieldNamesShow = BatchHadoopSelectSqlService.getSimpleQueryFieldNames(sql, true);
        String db = matcher.group("db");
        if (StringUtils.isEmpty(db)) {
            ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
            Preconditions.checkNotNull(projectEngine, String.format("项目:%d 不支持引擎:HADOOP", projectId));
            db = projectEngine.getEngineIdentity();
        }
        String tableName = matcher.group("name");
        return getSelectDownLoader(dtUicTenantId, projectId, queryFieldNames, fieldNamesShow, false, needMask, db, tableName, null, taskType);
    }

    /**
     * 获取表查询结果下载器（支持Spark、Hive） Impala数据源不会在此处理，会在ImpalaDownloadService中处理
     *
     * @param dtUicTenantId uic租户id
     * @param projectId 项目id
     * @param queryFieldNames 查询字段
     * @param fieldNamesShow 展示字段
     * @param permissionStyle 是否展示全部字段
     * @param needMask 是否需要脱敏
     * @param partition 查询分区
     * @param taskType 任务类型
     * @return 表查询结果下载器
     */
    public IDownload getSelectDownLoader(Long dtUicTenantId, Long projectId, List<String> queryFieldNames,
                                         List<String> fieldNamesShow, boolean permissionStyle, boolean needMask,
                                         String db, String tableName, String partition, Integer taskType){
        Map<String, Object> hadoop = consoleSend.getHdfs(dtUicTenantId);
        JdbcInfo jdbcInfo = null;
        if (EJobType.HIVE_SQL.getVal().equals(taskType) || EJobType.SPARK_SQL.getVal().equals(taskType)) {
            if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
                // hiveSql查询，获取hiveServer jdbc配置
                jdbcInfo = this.consoleSend.getHiveServer(dtUicTenantId);
            } else {
                // sparkSql查询，获取sparkThrift jdbc配置
                jdbcInfo = this.consoleSend.getSparkThrift(dtUicTenantId);
            }
            // 获取需要的脱敏的字段
            Map<String, List<DataMaskRule>> columnRules = getDataMaskRules(dtUicTenantId, tableName, projectId);
            try {
                return new HiveSelectDownload(hadoop, jdbcInfo, queryFieldNames, fieldNamesShow, permissionStyle, needMask, dtUicTenantId,
                        db, tableName, partition, columnRules, JobTypeDataSourceTypeMapping.getDataSourceTypeByJobType(taskType, jdbcInfo.getVersion()));
            } catch (final Exception e) {
                HadoopDataDownloadService.logger.error("", e);
                return null;
            }
        }
        throw new RdosDefineException("不支持该类型的任务查询结果下载！");
    }

    public IDownload getDownloader(Long dtuicTenantId, String tableName, String db, Integer dataSourceType) {
        return getDownloader(dtuicTenantId,tableName,db, null, null, dataSourceType);
    }

    public IDownload getDownloader(Long dtuicTenantId, String tableName, String db,List<String> columns,
                                             String partition, Integer dataSourceType){
        Map<String, Object> hadoop = this.consoleSend.getHdfs(dtuicTenantId);
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
        try {
            return new HiveSelectDownload(hadoop, jdbcInfo, null, null, true, false,
                    dtuicTenantId, db, tableName, partition, null, dataSourceType);
        } catch (final Exception e) {
            HadoopDataDownloadService.logger.error("",e);
            return null;
        }
    }



    @Override
    public IDownload buildIDownLoad(String jobId, Integer taskType, Long dtuicTenantId, Integer limitNum) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId 不能为空");
        }

        if (EJobType.SYNC.getVal().equals(taskType)) {
            //standalone模式的不支持日志下载直接返回null
            Boolean isStandalone = tenantService.hasStandAlone(dtuicTenantId);
            if (isStandalone){
                return null;
            }
            //同步任务
            StringBuilder syncLog = new StringBuilder();
            SyncDownload syncDownload = new SyncDownload();
            ActionLogVO log = actionService.log(jobId, ComputeType.BATCH.getType()).getData();
            if (!Objects.isNull(log)) {
                String engineLogStr = log.getEngineLog();
                String logInfoStr = log.getLogInfo();
                if (StringUtils.isNotBlank(engineLogStr)) {
                    try {
                        JSONObject engineLogJson = JSON.parseObject(engineLogStr);
                        engineLogJson.remove("increConf");
                        engineLogStr = engineLogJson.toJSONString();
                    } catch (Exception e) {
                        logger.info("engineLog非json", e);
                    }
                    syncLog.append("engineLog:\n").append(engineLogStr).append("\n");
                }
                try {
                    JSONObject logInfo = JSON.parseObject(logInfoStr);
                    syncLog.append("logInfo:\n").append(logInfo.getString("msg_info"));
                } catch (Exception e) {
                    logger.error("同步任务日志下载失败", e);
                }
            }
            syncDownload.setLogInfo(syncLog.toString());
            return syncDownload;
        }

        String applicationId  = batchJobService.getEngineJobId(jobId);

        if (StringUtils.isBlank(applicationId)) {
            return null;
        }

        IDownload iDownload = null;
        try {
            iDownload = RetryUtil.executeWithRetry(() -> {
                 final Map<String, Object> hadoopConf = this.consoleSend.getHdfs(dtuicTenantId);
                final String clusterConfig = this.consoleSend.getCluster(dtuicTenantId);
                Map<String, Object> yarnConf = null;
                if (StringUtils.isNotBlank(clusterConfig)) {
                    final JSONObject cluster = JSON.parseObject(clusterConfig);
                    yarnConf = PublicUtil.strToMap(cluster.getString("yarnConf"));
                }
                String submitUserName = getSubmitUserNameByJobId(jobId);
                final LogPluginDownload downloader = new LogPluginDownload(applicationId, yarnConf, hadoopConf, submitUserName, limitNum);
                return downloader;
            }, 3, 1000L, false);

        } catch (Exception e) {
            logger.error("downloadJobLog {}  失败:{}", jobId, e);
            return null;
        }
        return iDownload;
    }

    @Override
    public IDownload typeLogDownloader(Long dtuicTenantId, String jobId, Integer limitNum, String logType) {
        String applicationId = batchJobService.getEngineJobId(jobId);

        if (StringUtils.isBlank(applicationId)) {
            throw new RdosDefineException("任务尚未执行完成或提交失败，请稍后再试");
        }

        IDownload iDownload = null;
        try {
            iDownload = RetryUtil.executeWithRetry(() -> {
                final Map<String, Object> hadoopConf = this.consoleSend.getHdfs(dtuicTenantId);
                final String clusterConfig = this.consoleSend.getCluster(dtuicTenantId);
                Map<String, Object> yarnConf = null;
                if (StringUtils.isNotBlank(clusterConfig)) {
                    final JSONObject cluster = JSON.parseObject(clusterConfig);
                    yarnConf = PublicUtil.strToMap(cluster.getString("yarnConf"));
                }
                String submitUserName = getSubmitUserNameByJobId(jobId);
                final LogPluginDownload downloader = new LogPluginDownload(applicationId,yarnConf,hadoopConf,submitUserName, limitNum);
                downloader.configure();
                return downloader;
            }, 3, 1000L, false);

        } catch (Exception e) {
            throw new RdosDefineException(String.format("typeLogDownloader 失败，原因是：%s", e.getMessage()), e);
        }

        return iDownload;
    }

    /**
     * 根据jobId查询任务提交用户
     * @param jobId jobId
     * @return dtuicUserId
     */
    private String getSubmitUserNameByJobId(String jobId) {
        ApiResponse<ScheduleJob> response = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (response.getData() != null) {
            String submitUserName = response.getData().getSubmitUserName();
            if (StringUtils.isEmpty(submitUserName)) {
                submitUserName = environmentContext.getHadoopUserName();
            }
            return submitUserName;
        } else {
            throw new RdosDefineException("engine接口返回信息错误，/node/scheduleJob/getByJobId，jobId:" + jobId);
        }
    }
}
