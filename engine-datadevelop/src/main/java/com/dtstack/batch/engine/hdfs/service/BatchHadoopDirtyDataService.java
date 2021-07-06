package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.RelationResultType;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTableInfoDao;
import com.dtstack.batch.dao.BatchTableRelationDao;
import com.dtstack.batch.dao.ProjectDao;
import com.dtstack.batch.datamask.domain.DataMaskRule;
import com.dtstack.batch.datamask.util.DataMaskUtil;
import com.dtstack.batch.domain.BatchDataSource;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.datamask.impl.DataMaskColumnInfoService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.table.IDirtyDataService;
import com.dtstack.batch.service.table.impl.BatchActionRecordService;
import com.dtstack.batch.service.table.impl.BatchTableInfoService;
import com.dtstack.batch.service.table.impl.BatchTableRelationService;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.Base64Util;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.TableOperateEnum;
import com.dtstack.engine.api.service.ScheduleTaskShadeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Hadoop 引擎的脏数据相关
 * Date: 2019/5/27
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHadoopDirtyDataService implements IDirtyDataService {

    @Autowired
    private HadoopDataDownloadService hadoopDataDownloadService;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private BatchTableRelationDao batchTableRelationDao;

    @Autowired
    private DataMaskColumnInfoService dataMaskColumnInfoService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private BatchTableInfoDao hiveTableInfoDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BatchActionRecordService batchActionRecordService;

    @Autowired
    private BatchTableRelationService tableRelationService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private HadoopProjectService hiveProjectService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    private static final Logger logger = LoggerFactory.getLogger(BatchHadoopDirtyDataService.class);

    private static final String DIRTY_TABLE_PREFIX = "dirty_";

    private static final String CREATE_SQL_TEMP = "create table if not exists %s(record string COMMENT '错误记录',category string COMMENT '错误分类',msg string COMMENT '错误信息',ts timestamp COMMENT '时间戳') COMMENT '脏数据表' partitioned by(task_name string COMMENT '任务名称',`time` string COMMENT '任务执行时间') row format delimited fields terminated by '%s' stored as textfile";

    private static final String ADD_PART_TEMP = "alter table %s add partition(task_name='%s',`time`='%s')";

    private static final String TABLE_NAME_TEMP = "%s.%s";

    private static final String FIELD_DELIMITER = "\u0001";

    /**
     * 创建脏数据表
     */
    @Forbidden
    @Override
    public String createDirtyTable(String tableName, Integer lifyDay, Long userId, Long taskId, String taskName,
                                   Long tenantId, Long projectId) {
        // 默认表名
        if (StringUtils.isEmpty(tableName)) {
            tableName = DIRTY_TABLE_PREFIX + taskName;
        }

        BatchTableInfo tableInfo = hiveTableInfoDao.getByTableName(tableName, tenantId, projectId, null);
        if (tableInfo != null) {
            if (tableInfo.getIsDirtyDataTable() == 0) {
                throw new RdosDefineException("此表为非脏数据表,请重新指定脏数据表名");
            }
        } else {
            Project project = projectDao.getOne(projectId);
            String dbName = this.getDataBase(projectId);
            lifyDay = lifyDay == null ? 90 : lifyDay;
            Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);

            String exeSql;
            try {
                exeSql = createDirtyTable(dbName, tableName, dtuicTenantId, projectId);
            } catch (Exception e) {
                throw new RdosDefineException(ErrorCode.CREATE_TABLE_ERR, e);
            }

            MultiEngineType engineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(MultiEngineType.HADOOP.getType());
            tableInfo = batchTableInfoService.addTableFromSql(dtuicTenantId, tenantId, project.getId(), tableName,
                    lifyDay, null, userId, 1, false, engineType.getType(), dbName);

            batchActionRecordService.addRecord(tableInfo, userId, exeSql, TableOperateEnum.CREATE);
        }

        tableRelationService.addRelation(tenantId, projectId, batchDataSourceService.getDefaultDataSourceByTableType(tableInfo.getTableType(), projectId), tableInfo.getTableName(), tableInfo.getId(), taskId, TableRelationType.TASK.getType(), EJobType.SYNC.getVal(), RelationResultType.IS_QUERY.getVal());

        return tableName;
    }

    /**
     * 获取dbName
     *
     * @param projectId
     * @return
     */
    private String getDataBase(Long projectId) {
        ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
        if (projectDb == null) {
            throw new RdosDefineException("当前项目未配置Hadoop引擎，无法开启脏数据");
        }

        return projectDb.getEngineIdentity();
    }

    /**
     * 创建脏数据表
     *
     * @param dbName
     * @param tableName
     * @param dtuicTenantId
     * @param projectId
     * @return
     */
    @Override
    public String createDirtyTable(String dbName, String tableName, Long dtuicTenantId, Long projectId) {
        String sql = String.format(CREATE_SQL_TEMP, String.format(TABLE_NAME_TEMP, dbName, tableName), FIELD_DELIMITER);
        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), dbName, sql);
        return sql;
    }

    /**
     * 获取脏数据表
     *
     * @param tableInfo
     * @param projectId
     * @param dbName
     * @param partId
     * @param tenantId
     * @param dtuicTenantId
     * @param needMask
     * @param limit
     * @param errorType
     * @param dtToken
     * @return
     */
    @Override
    public List<List<String>> getDirtyData(BatchTableInfo tableInfo, Long projectId, String dbName,
                                           Long partId, Long tenantId, Long dtuicTenantId,
                                           Boolean needMask, Integer limit, String errorType, String dtToken) {

        String partition = null;
        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(tableInfo.getProjectId());
        List<String> partitionVOS = iTableServiceImpl.showPartitions(dtuicTenantId, null, dbName, ETableType.getTableType(tableInfo.getTableType()), tableInfo.getTableName());
        if (CollectionUtils.isNotEmpty(partitionVOS)) {

            if (partId != null && partId < partitionVOS.size()) {
                partitionVOS.get(partId.intValue());
            } else {
                partition = partitionVOS.get(0);
            }
        }

        try {
            // 获取数据
            List<String> fieldNames = new ArrayList<>();
            fieldNames.add("record");
            fieldNames.add("category");
            fieldNames.add("msg");
            fieldNames.add("ts");

            IDownload download = hadoopDataDownloadService.getDownloader(dtuicTenantId,tableInfo.getTableName(),dbName,
                    fieldNames,partition, dataSourceType.getVal());
            if (download == null) {
                throw new RdosDefineException("数据源连接失败");
            }
            List<List<String>> result = new ArrayList<>();
            result.add(download.getMetaInfo());
            Map<String, List<DataMaskRule>> rules = Maps.newHashMap();
            if (needMask) {
                rules = getRelatedColumnRules(tenantId, projectId, tableInfo.getTableName(),dtToken);
            }
            int num = 0;
            while (!download.reachedEnd() && num < limit) {
                List<String> row = (List<String>) download.readNext();
                if (row.get(1).equals(errorType)) {
                    JSONObject dirtyRecord = JSONObject.parseObject(row.get(0));
                    rules.entrySet().forEach(entry -> {
                        String col = entry.getKey();
                        List<DataMaskRule> crule = entry.getValue();
                        if (dirtyRecord.containsKey(col)) {
                            String source = dirtyRecord.getString(col);
                            dirtyRecord.put(col, DataMaskUtil.mask(source, DataMaskColumnInfoService.generateMaskRule(crule, source)));
                        }
                    });
                    row.set(0, JSONObject.toJSONString(dirtyRecord));
                    result.add(row);
                    num++;
                }
            }

            return result;
        } catch (Exception e) {
            logger.error(ErrorCode.GET_DIRTY_ERROR.getDescription(), e);
            throw new RdosDefineException(e.getMessage());
        }
    }

    private Map<String, List<DataMaskRule>> getRelatedColumnRules(Long tenantId, Long projectId, String tableName,String dtToken) {
        List<Long> relationIds = batchTableRelationDao.listRelationIdByDirtyTableNameAndIsTask(tableName, tenantId, projectId, TableRelationType.TASK.getType());
        List<String> tables = Lists.newArrayList();
        relationIds.forEach(taskId -> {

            ScheduleTaskShade taskShade = scheduleTaskShadeService.findTaskId(taskId, Deleted.NORMAL.getStatus(), AppType.RDOS.getType()).getData();
            if (taskShade != null && taskShade.getTaskType() == EJobType.SYNC.getVal()) {
                JSONObject syncJob = JSONObject.parseObject(Base64Util.baseDecode(taskShade.getSqlText()))
                        .getJSONObject("job").getJSONObject("job");
                JSONArray content = syncJob.getJSONArray("content");
                for (int i = 0; i < content.size(); i++) {
                    JSONObject ob = content.getJSONObject(i);
                    if (!ob.containsKey("reader")) {
                        continue;
                    }
                    if (PluginName.Hive_R.equals(ob.getJSONObject("reader").getString("name"))) {
                        JSONObject reader = ob.getJSONObject("reader");
                        String path = reader.getJSONObject("parameter").getString("path");
                        String name = path.split("\\.db/")[1].split("/")[0];
                        tables.add(name);
                        break;
                    }
                }

            }
        });
        Map<String, List<DataMaskRule>> result = Maps.newHashMap();
        tables.forEach(tb -> {
            BatchTableInfo info = batchTableInfoService.getTableInfoByTableName(tb, tenantId, projectId, ETableType.HIVE.getType());
            if (info != null) {
                result.putAll(dataMaskColumnInfoService.getRelatedRulesByTableId(info.getId()));
            }
        });
        return result;
    }

    /**
     * 替换同步job中表的存储路径
     */
    @Forbidden
    public String replaceTablePath(boolean saveDirty, String sqlText, Long taskId, String taskName, Long userId, Long tenantId, Long projectId, Boolean isRoot, Map<String, Object> actionParam) throws Exception {
        JSONObject sqlObject = JSONObject.parseObject(sqlText);
        JSONObject job = sqlObject.getJSONObject("job");
        JSONObject setting = job.getJSONObject("setting");
        if (setting.containsKey("dirty") && tenantService.isStandAlone(tenantId)) {
            throw new RdosDefineException("standAlone模式的数据同步任务不允许配置脏数据配置!");
        }
        if (setting.containsKey("dirty")) {
            //插入脏数据表 数据源类型
            BatchDataSource defaultDataSource = batchDataSourceService.getDefaultDataSourceByEngineType(MultiEngineType.HADOOP.getType(), projectId);
            if (defaultDataSource != null) {
                actionParam.put("dirtyDataSourceType", defaultDataSource.getType());
            }

            if (!saveDirty) {
                setting.remove("dirty");
                return sqlObject.toJSONString();
            }

            JSONObject dirty = setting.getJSONObject("dirty");
            String tableName = dirty.getString("path");
            Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);

            JSONObject jsonObject = hiveProjectService.createHadoopConfigObject(dtuicTenantId);
            dirty.put("hadoopConfig", jsonObject);
            String path = null;

            if (StringUtils.isNotEmpty(tableName)) {

                BatchTableInfo tableInfo = batchTableInfoService.getTableInfoByTableName(tableName, tenantId, projectId, ETableType.HIVE.getType());
                if (tableInfo == null) {
                    tableName = createDirtyTable(tableName, 90, userId, taskId, taskName, tenantId, projectId);
                    tableInfo = batchTableInfoService.getByName(tableName, tenantId, projectId, ETableType.HIVE.getType());
                }
                tableInfo = batchTableInfoService.getTableInfoNewest(dtuicTenantId, tableInfo);
                String dbName = this.getDataBase(projectId);
                tableName = String.format(TABLE_NAME_TEMP, dbName, tableName);
                Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
                String alterSql = String.format(ADD_PART_TEMP, tableName, taskName, time);

                ExecuteContent content = new ExecuteContent();
                content.setTenantId(tenantId)
                        .setProjectId(projectId)
                        .setUserId(userId)
                        .setRootUser(isRoot)
                        .setCheckSyntax(false)
                        .setRelationId(taskId)
                        .setRelationType(1)
                        .setDetailType(EJobType.SYNC.getType())
                        .setSql(alterSql)
                        .setTableType(ETableType.HIVE.getType())
                        .setEngineType(MultiEngineType.HADOOP.getType());
                batchSqlExeService.executeSql(content);

                String partName = String.format("task_name=%s/time=%s", taskName, time);
                path = tableInfo.getLocation() + "/" + partName;

                dirty.put("path", path);
                dirty.put("tableName", tableName);
                setting.put("dirty", dirty);
                job.put("setting", setting);
                sqlObject.put("job", job);
            }
        }
        return sqlObject.toJSONString();
    }

    @Override
    public Map<String, Object> readyForSaveDirtyData(String tableName, Integer lifeDay, Long userId, Long taskId, String taskName,
                                                     Long tenantId, Long dtuicTenantId, Long projectId) {

        Map<String, Object> settingMap = Maps.newHashMap();
        tableName = createDirtyTable(tableName, lifeDay, userId, taskId, taskName, tenantId, projectId);

        // 这里path存表名，运行job时再替换成带分区的path
        settingMap.put("path", tableName);

        // 添加hadoop配置
        JSONObject jsonObject = hiveProjectService.createHadoopConfigObject(dtuicTenantId);
        settingMap.put("hadoopConfig", jsonObject.toJSONString());
        settingMap.put("isSaveDirty", 1);

        return settingMap;
    }
}
