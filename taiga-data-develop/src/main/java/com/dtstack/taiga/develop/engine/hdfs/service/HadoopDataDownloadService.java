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

package com.dtstack.taiga.develop.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.annotation.Forbidden;
import com.dtstack.taiga.common.engine.JdbcInfo;
import com.dtstack.taiga.common.enums.*;
import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.BatchSelectSql;
import com.dtstack.taiga.dao.domain.TenantComponent;
import com.dtstack.taiga.develop.engine.rdbms.common.IDownload;
import com.dtstack.taiga.develop.engine.rdbms.hive.service.LogPluginDownload;
import com.dtstack.taiga.develop.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.taiga.develop.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.taiga.develop.mapping.JobTypeDataSourceTypeMapping;
import com.dtstack.taiga.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taiga.develop.service.impl.TenantComponentService;
import com.dtstack.taiga.develop.service.job.impl.BatchJobService;
import com.dtstack.taiga.develop.service.table.IDataDownloadService;
import com.dtstack.taiga.develop.service.table.impl.BatchSelectSqlService;
import com.dtstack.taiga.pluginapi.util.RetryUtil;
import com.dtstack.taiga.scheduler.service.ClusterService;
import com.dtstack.taiga.scheduler.service.ScheduleActionService;
import com.dtstack.taiga.scheduler.service.ScheduleJobService;
import com.dtstack.taiga.scheduler.vo.action.ActionLogVO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private TenantComponentService tenantEngineService;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    /**
     * 下载sql执行结果
     *
     * @param jobId
     * @param tenantId
     * @return
     */
    @Override
    public IDownload downloadSqlExeResult(String jobId, Long tenantId) {

        String tableName;
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException("当前下载只支持通过临时表查询的下载");
        }
        DataSourceType dataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        Integer jobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType.getVal());

        BatchSelectSql batchHiveSelectSql = batchSelectSqlService.getByJobId(jobId, tenantId, Deleted.NORMAL.getStatus());
        tableName = batchHiveSelectSql.getTempTableName();

        TenantComponent tenantEngine = tenantEngineService.getByTenantAndEngineType(tenantId, EScheduleJobType.SPARK_SQL.getType());
        Preconditions.checkNotNull(tenantEngine, String.format("tenant %d not support hadoop engine.", tenantId));
        // 简单查询逻辑更改
        if (batchHiveSelectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            return getSimpleSelectDownLoader(tenantId, batchHiveSelectSql.getSqlText(), jobType);
        }
        return getDownloader(tenantId, tableName, tenantEngine.getComponentIdentity(), dataSourceType.getVal());
    }

    /**
     * 查询表数据
     *
     * @param tenantId 租户id
     * @param tableName 查询表名
     * @param db 查询数据库
     * @param num 查询限制条数
     * @param fieldNameList 查询指定的字段列表，为空、为null或者包含*则默认查询全部字段
     * @param permissionStyle 是否显示全部字段（包括fieldNameList），如果为true则无权限字段显示 NO PERMISSION， 需要和fieldNameList配合使用
     * @return
     * @throws Exception
     */
    @Forbidden
    @Override
    public List<Object> queryDataFromTable(Long tenantId, String tableName, String db, Integer num, List<String> fieldNameList, Boolean permissionStyle) throws Exception {
        DataSourceType dataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        Integer EScheduleJobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType.getVal());
        IDownload selectDownLoader = getSelectDownLoader(tenantId, fieldNameList.contains("*") ? null : fieldNameList, Lists.newArrayList(fieldNameList), permissionStyle, db, tableName, null, EScheduleJobType);
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()));
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


    /**
     * 从临时表获取表数据
     *
     * @param tenantId
     * @param tableName
     * @param db
     * @return
     * @throws Exception
     */
    public List<Object> queryDataFromTempTable(Long tenantId, String tableName, String db) throws Exception {
        DataSourceType dataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        IDownload downloader = getDownloader(tenantId, tableName, db, dataSourceType.getVal());
        List<Object> result = new ArrayList<>();
        List<String> alias = downloader.getMetaInfo();
        result.add(alias);

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId,null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()));
        int readCounter = 0;
        while (!downloader.reachedEnd() && readCounter < jdbcInfo.getMaxRows()) {
            List<String> row = (List<String>) downloader.readNext();
            result.add(row);
            readCounter++;
        }
        return result;
    }

    /**
     * 获取简单查询下载器
     *
     * @param tenantId 租户id
     * @param sql 查询sql
     * @param taskType 任务类型 仅支持sparkSql和hiveSql
     * @return 简单查询结果下载器
     */
    public IDownload getSimpleSelectDownLoader(Long tenantId, String sql, Integer taskType){
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
            TenantComponent tenantEngine = tenantEngineService.getByTenantAndEngineType(tenantId, taskType);
            Preconditions.checkNotNull(tenantEngine, String.format("项目:%d 不支持引擎:HADOOP", tenantId));
            db = tenantEngine.getComponentIdentity();
        }
        String tableName = matcher.group("name");
        return getSelectDownLoader(tenantId, queryFieldNames, fieldNamesShow, false, db, tableName, null, taskType);
    }

    /**
     * 获取表查询结果下载器（支持Spark、Hive） Impala数据源不会在此处理，会在ImpalaDownloadService中处理
     *
     * @param tenantId 租户id
     * @param queryFieldNames 查询字段
     * @param fieldNamesShow 展示字段
     * @param permissionStyle 是否展示全部字段
     * @param partition 查询分区
     * @param taskType 任务类型
     * @return 表查询结果下载器
     */
    public IDownload getSelectDownLoader(Long tenantId, List<String> queryFieldNames,
                                         List<String> fieldNamesShow, boolean permissionStyle,
                                         String db, String tableName, String partition, Integer taskType){
        Map<String, Object> hadoop = Engine2DTOService.getHdfs(tenantId);
        JdbcInfo jdbcInfo = null;
        if (EScheduleJobType.SPARK_SQL.getVal().equals(taskType)) {
            // sparkSql查询，获取sparkThrift jdbc配置
            jdbcInfo = Engine2DTOService.getSparkThrift(tenantId);
            try {
                return new HiveSelectDownload(hadoop, jdbcInfo, queryFieldNames, fieldNamesShow, permissionStyle, tenantId,
                        db, tableName, partition, JobTypeDataSourceTypeMapping.getDataSourceTypeByJobType(taskType, jdbcInfo.getVersion()));
            } catch (final Exception e) {
                HadoopDataDownloadService.logger.error("", e);
                return null;
            }
        }
        throw new RdosDefineException("不支持该类型的任务查询结果下载！");
    }

    public IDownload getDownloader(Long tenantId, String tableName, String db, Integer dataSourceType) {
        return getDownloader(tenantId,tableName,db, null, null, dataSourceType);
    }

    public IDownload getDownloader(Long tenantId, String tableName, String db, List<String> columns,
                                   String partition, Integer dataSourceType){
        Map<String, Object> hadoop = Engine2DTOService.getHdfs(tenantId);
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
        try {
            return new HiveSelectDownload(hadoop, jdbcInfo, null, null, true,
                    tenantId, db, tableName, partition, dataSourceType);
        } catch (final Exception e) {
            HadoopDataDownloadService.logger.error("",e);
            return null;
        }
    }



    @Override
    public IDownload buildIDownLoad(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId 不能为空");
        }

        if (EScheduleJobType.SYNC.getVal().equals(taskType)) {
            //standalone模式的不支持日志下载直接返回null
            Boolean isStandalone = clusterService.hasStandalone(tenantId, EComponentType.FLINK.getTypeCode());
            if (isStandalone){
                return null;
            }
            //同步任务
            StringBuilder syncLog = new StringBuilder();
            SyncDownload syncDownload = new SyncDownload();
            ActionLogVO log = actionService.log(jobId, ComputeType.BATCH.getType());
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
                final Map<String, Object> hadoopConf = Engine2DTOService.getHdfs(tenantId);
                final JSONObject yarnConf = Engine2DTOService.getYarnConf(tenantId);
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
    public IDownload typeLogDownloader(Long tenantId, String jobId, Integer limitNum, String logType) {
        String applicationId = batchJobService.getEngineJobId(jobId);

        if (StringUtils.isBlank(applicationId)) {
            throw new RdosDefineException("任务尚未执行完成或提交失败，请稍后再试");
        }

        IDownload iDownload = null;
        try {
            iDownload = RetryUtil.executeWithRetry(() -> {
                final Map<String, Object> hadoopConf = Engine2DTOService.getHdfs(tenantId);
                JSONObject yarnConf = Engine2DTOService.getYarnConf(tenantId);
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
     * @return userName
     */
    private String getSubmitUserNameByJobId(String jobId) {
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        String submitUserName = scheduleJob.getSubmitUserName();
        if (StringUtils.isEmpty(submitUserName)) {
            submitUserName = environmentContext.getHadoopUserName();
        }
        return submitUserName;
    }
}
