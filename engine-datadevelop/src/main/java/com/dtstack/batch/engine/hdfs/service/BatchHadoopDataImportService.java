package com.dtstack.batch.engine.hdfs.service;

import com.dtstack.batch.bo.ImportDataParam;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTableColumn;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.core.domain.ImportColum;
import com.dtstack.batch.engine.hdfs.writer.HdfsOrcWriter;
import com.dtstack.batch.engine.hdfs.writer.HdfsParquetWriter;
import com.dtstack.batch.engine.hdfs.writer.HdfsTextWriter;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.engine.rdbms.common.enums.StoredType;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.enums.EImportDataMatchType;
import com.dtstack.batch.enums.LifeStatus;
import com.dtstack.batch.enums.RedisKey;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.ProjectService;
import com.dtstack.batch.service.table.IDataImportService;
import com.dtstack.batch.utils.TableOperateUtils;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * modify: xuchao
 * create: 2017/7/19.
 */
@Service
public class BatchHadoopDataImportService implements IDataImportService {

    private final static Logger logger = LoggerFactory.getLogger(BatchHadoopDataImportService.class);

    private final static String HIVE_ADD_PARTITION_FORMAT = "alter table ${projectName}.${tableName} add partition (${partitionInfo})";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private BatchTablePartitionService batchTablePartitionService;

    @Autowired
    private BatchTableInfoService hiveTableInfoService;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Resource
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;


    /**
     * 经过文件上传接口处理的数据都是string类型
     * FIXME 由于异步执行写hadoop文件操作，可考虑控制用户多次重复提交相同数据。
     *
     * @return 返回任务查询redis key
     * @throws Exception
     */
    public String importData(ImportDataParam importDataParam) throws Exception {

        Long projectId = importDataParam.getProjectId();
        Long tenantId = importDataParam.getTenantId();
        Long userId = importDataParam.getUserId();
        Long dtuicTenantId = importDataParam.getDtuicTenantId();

        String separator = importDataParam.getSeparator();
        String oriCharSet = importDataParam.getOriCharSet();
        Integer startLine = importDataParam.getStartLine();
        Boolean topLineIsTitle = importDataParam.getTopLineIsTitl();
        Integer matchType = importDataParam.getMatchType();
        Long tableId = importDataParam.getTableId();
        String partitionStr = importDataParam.getPartitionStr();
        String keyRefStr = importDataParam.getKeyRefStr();
        Integer overwriteFlag = importDataParam.getOverwriteFlag();

        String tmpFilePath = importDataParam.getTmpFilePath();

        boolean overwriteData = overwriteFlag > 0;

        List<Object> partitionInfo = PublicUtil.objectToList(partitionStr);
        List<ImportColum> keyList = getKeyList(keyRefStr, matchType);

        BatchTableInfo tableInfo = batchTableInfoService.getTableInfo(tableId, tenantId);
        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support hadoop engine.", projectId));

        DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        Table baseInfo = iTableServiceImpl.getTableInfo(dtuicTenantId,null, projectEngine.getEngineIdentity(), ETableType.getTableType(tableInfo.getTableType()), tableInfo.getTableName());

        String tablePartHdfsPath = getTablePartitionHdfsPath(baseInfo.getPath(), partitionInfo);
        if (overwriteData) {
            overWriteHdfsPath(dtuicTenantId, tablePartHdfsPath);
        }

        if (!createIfNotExistsPartition(dtuicTenantId, tableId, partitionInfo, tenantId, projectId)) {
            throw new RdosDefineException("create partition failure.", ErrorCode.SERVER_EXCEPTION);
        }


        List<BatchTableColumn> tableColumn = TableOperateUtils.getTableColumns(baseInfo);

        String inputFormat = baseInfo.getStoreType().toLowerCase();
        String queryKey = UUID.randomUUID().toString();
        String finalSeparator = separator;
        String key = RedisKey.IDE_DATA_DEV_IMP_LOCAL_STATUS.getKey(tenantId, userId, projectId, queryKey);
        redisTemplate.execute((RedisCallback<String>) redisConnection -> {
            redisConnection.set(key.getBytes(), "0".getBytes());
            return "init";
        });
        executor.execute(() -> {
            try {
                if (StoredType.TEXTFILE.getValue().equalsIgnoreCase(inputFormat)) {
                    importTextFile(dtuicTenantId, baseInfo.getDelim(), tmpFilePath, finalSeparator, oriCharSet, startLine, topLineIsTitle,
                            matchType, tablePartHdfsPath, tableColumn, keyList, key);
                } else if (StoredType.ORC.getValue().equalsIgnoreCase(inputFormat)) {
                    importOrcFile(dtuicTenantId, tmpFilePath, finalSeparator, oriCharSet, startLine, topLineIsTitle, matchType,
                            tablePartHdfsPath, tableColumn, keyList, key);
                } else if (StoredType.PARQUET.getValue().equalsIgnoreCase(inputFormat)) {
                    importParquetFile(dtuicTenantId, tmpFilePath, finalSeparator, oriCharSet, startLine, topLineIsTitle, matchType,
                            tablePartHdfsPath, tableColumn, keyList, key);
                } else {
                    throw new RdosDefineException("not support for hive table input format:" + inputFormat, ErrorCode.SERVER_EXCEPTION);
                }
            } catch (IOException e) {
                setImportLocalFileRedisStatus(key, "-1");
                String errorKey = key + "_ERROR";
                String errorMsg = "参数 matchType 非法";
                setImportLocalFileRedisFailedStack(errorKey, errorMsg);
                logger.error(ExceptionUtils.getStackTrace(e));
            } catch (RdosDefineException e) {
                setImportLocalFileRedisStatus(key, "-1");
                String errorKey = key + "_ERROR";
                String errorMsg = e.getErrorMsg();
                setImportLocalFileRedisFailedStack(errorKey, errorMsg);
                logger.error(ExceptionUtils.getStackTrace(e));
            } finally {
                File tmpFile = new File(tmpFilePath);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
        });
        // 更新tableInfo
        BatchTableInfo tableInfoUpdate = hiveTableInfoService.getOne(tableId, tenantId);
        if (tableInfoUpdate != null) {
            tableInfoUpdate.setLifeStatus(LifeStatus.NORMAL.getValue());
            tableInfoUpdate.setLastDmlTime(new Timestamp(System.currentTimeMillis()));
            hiveTableInfoService.updateBaseInfo(tableInfoUpdate);
        }

        return queryKey;
    }


    /**
     * 向hdfs 导入 text 格式文件
     *
     * @param fieldDelim
     * @param localFileName
     * @param separator
     * @param oriCharSet
     * @param startLine
     * @param topLineIsTitle
     * @param matchType
     * @param hdfsPath
     * @param tableColumns   导入目标表的列信息
     * @return
     * @throws IOException
     */
    @Forbidden
    private int importTextFile(Long dtuicTenantId, String fieldDelim, String localFileName, String separator, String oriCharSet,
                               int startLine, boolean topLineIsTitle, int matchType, String hdfsPath,
                               List<BatchTableColumn> tableColumns, List<ImportColum> keyList, String redisKey) {
        int totalLine = 0;
        File dataFile = new File(localFileName);
        if (!dataFile.exists()) {
            setImportLocalFileRedisStatus(redisKey, "-1");
            String errorKey = redisKey + "_ERROR";
            String errorMsg = "can't find file of name " + localFileName;
            setImportLocalFileRedisFailedStack(errorKey, errorMsg);
            throw new RdosDefineException(errorMsg, ErrorCode.SERVER_EXCEPTION);
        }

        if (EImportDataMatchType.BY_POS.getType().equals(matchType)) {
            totalLine = HdfsTextWriter.writeByPos(dtuicTenantId, hdfsPath, separator, fieldDelim, localFileName, oriCharSet,
                    startLine, topLineIsTitle, tableColumns, keyList);
        } else if (EImportDataMatchType.BY_NAME.getType().equals(matchType)) {
            totalLine = HdfsTextWriter.writeByName(dtuicTenantId, hdfsPath, separator, fieldDelim, localFileName, oriCharSet, startLine,
                    keyList, tableColumns);
        } else {
            setImportLocalFileRedisStatus(redisKey, "-1");
            String errorKey = redisKey + "_ERROR";
            String errorMsg = "参数 matchType 非法";
            setImportLocalFileRedisFailedStack(errorKey, errorMsg);
            throw new RdosDefineException(errorMsg, ErrorCode.INVALID_PARAMETERS);
        }
        setImportLocalFileRedisStatus(redisKey, "1");
        return totalLine;
    }

    private void setImportLocalFileRedisStatus(String key, String status) {
        redisTemplate.execute((RedisCallback<String>) redisConnection -> {
            redisConnection.setEx(key.getBytes(), 18400L, status.getBytes());
            return null;
        });
    }

    private void setImportLocalFileRedisFailedStack(String key, String errorStr) {
        redisTemplate.opsForValue().set(key, errorStr, 1, TimeUnit.DAYS);
    }

    private int importOrcFile(Long dtuicTenantId, String localFileName, String separator, String oriCharSet, int startLine, boolean topLineIsTitle,
                              int matchType, String hdfsPath, List<BatchTableColumn> columnsList, List<ImportColum> keyList, String redisKey) throws IOException {
        int totalLine = 0;
        if (EImportDataMatchType.BY_POS.getType().equals(matchType)) {
            totalLine = HdfsOrcWriter.writeByPos(dtuicTenantId, hdfsPath, separator, localFileName, oriCharSet, startLine, topLineIsTitle,
                    columnsList, keyList);
        } else if (EImportDataMatchType.BY_NAME.getType().equals(matchType)) {
            totalLine = HdfsOrcWriter.writeByName(dtuicTenantId, hdfsPath, separator, localFileName, oriCharSet, startLine, topLineIsTitle,
                    columnsList, keyList);
        } else {
            setImportLocalFileRedisStatus(redisKey, "-1");
            String errorKey = redisKey + "_ERROR";
            String errorMsg = "参数 matchType 非法";
            setImportLocalFileRedisFailedStack(errorKey, errorMsg);
            throw new RdosDefineException(errorMsg, ErrorCode.INVALID_PARAMETERS);
        }
        setImportLocalFileRedisStatus(redisKey, "1");
        return totalLine;
    }

    private int importParquetFile(Long dtuicTenantId, String localFileName, String separator, String oriCharSet, int startLine, boolean topLineIsTitle,
                                  int matchType, String hdfsPath, List<BatchTableColumn> columnsList, List<ImportColum> keyList, String redisKey) throws IOException {
        int totalLine = 0;
        if (EImportDataMatchType.BY_POS.getType().equals(matchType)) {
            totalLine = HdfsParquetWriter.writeByPos(dtuicTenantId, hdfsPath, separator, localFileName, oriCharSet, startLine, topLineIsTitle,
                    columnsList, keyList);
        } else if (EImportDataMatchType.BY_NAME.getType().equals(matchType)) {
            totalLine = HdfsParquetWriter.writeByName(dtuicTenantId, hdfsPath, separator, localFileName, oriCharSet, startLine, topLineIsTitle,
                    columnsList, keyList);
        } else {
            setImportLocalFileRedisStatus(redisKey, "-1");
            String errorKey = redisKey + "_ERROR";
            String errorMsg = "参数 matchType 非法";
            setImportLocalFileRedisFailedStack(errorKey, errorMsg);
            throw new RdosDefineException(errorMsg, ErrorCode.INVALID_PARAMETERS);
        }
        setImportLocalFileRedisStatus(redisKey, "1");
        return totalLine;
    }

    /**
     * 检查分区是否存在，不存在则新建
     *
     * @param tableId
     * @param partitionList
     */
    private boolean createIfNotExistsPartition(long dtuicTenantId, long tableId, List<Object> partitionList, long tenantId, long projectId){

        //检查tableId是否存在partition
        if (!hiveTableInfoService.isPartition(tableId, tenantId)) {
            return true;
        }

        // 判断分区是否存在
        boolean isExists = batchTablePartitionService.checkPartitionExists(tenantId, tableId, partitionList, dtuicTenantId);
        if (isExists) {
            return true;
        }

        Project project = projectService.getProjectById(projectId);
        if (project == null) {//正常应该是不可能的
            logger.error("project {} is not exists.", projectId);
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }

        BatchTableInfo batchHiveTable = batchTableInfoService.getTableInfo(tableId, tenantId);

        ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
        Preconditions.checkNotNull(projectDb, "引擎不能为空");
        String dbName = projectDb.getEngineIdentity();
        String tableName = batchHiveTable.getTableName();

        String partitionInfo = "";
        for (Object tmp : partitionList) {
            Map<String, String> pairMap = (Map<String, String>) tmp;
            if (pairMap.size() != 1) {
                throw new RdosDefineException("(分区信息不能为空)", ErrorCode.INVALID_PARAMETERS);
            }

            for (Map.Entry<String, String> pair : pairMap.entrySet()) {
                partitionInfo += pair.getKey() + "='" + pair.getValue() + "',";
            }
        }

        if (partitionInfo.endsWith(",")) {
            partitionInfo = partitionInfo.substring(0, partitionInfo.length() - 1);
        }

        String addPartitionSql = HIVE_ADD_PARTITION_FORMAT.replace("${projectName}", dbName).replace("${tableName}", tableName)
                .replace("${partitionInfo}", partitionInfo);

        try {
            DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
            jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), dbName, addPartitionSql);
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Forbidden
    private String getTablePartitionHdfsPath(String tablePath, List<Object> partitionInfo) {

        if (CollectionUtils.isEmpty(partitionInfo)) {
            return tablePath;
        }

        String hdfsUrl = tablePath + "/";

        for (Object tmp : partitionInfo) {
            Map<String, String> pairMap = (Map<String, String>) tmp;

            if (pairMap.size() != 1) {
                throw new RdosDefineException("(partitionInfo 参数不正确)", ErrorCode.INVALID_PARAMETERS);
            }

            for (Map.Entry<String, String> pair : pairMap.entrySet()) {
                hdfsUrl += pair.getKey() + "=" + pair.getValue() + "/";
            }
        }


        if (hdfsUrl.endsWith("/")) {
            hdfsUrl = hdfsUrl.substring(0, hdfsUrl.length() - 1);
        }

        return hdfsUrl;
    }

    /**
     * 递归删除
     * @param dtuicTenantId
     * @param hdfsPath
     * @throws Exception
     */
    private void overWriteHdfsPath(Long dtuicTenantId, String hdfsPath) {
        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);
        HdfsOperator.checkAndDele(conf,kerberosConf,hdfsPath);
    }

    private List<ImportColum> getKeyList(String keyRefStr, Integer matchType) throws IOException {
        List<ImportColum> keyList = new ArrayList<>();
        if (keyRefStr != null) {
            List<Object> keyRefList = PublicUtil.objectToList(keyRefStr);
            for (Object o : keyRefList) {
                if (o instanceof String) {
                    keyList.add(new ImportColum());
                } else {
                    keyList.add(PublicUtil.objectToObject(o, ImportColum.class));
                }
            }

            boolean fieldNullCheck = false;
            for (ImportColum importColum : keyList) {
                if (!StringUtils.isEmpty(importColum.getFormat())) {
                    importColum.setDateFormat(new SimpleDateFormat(importColum.getFormat()));
                }
            }

            if (matchType.equals(EImportDataMatchType.BY_NAME.getType())) {
                for (ImportColum importColum : keyList) {
                    if (!StringUtils.isEmpty(importColum.getKey())) {
                        importColum.setKey(importColum.getKey().trim());
                        fieldNullCheck = true;
                    }
                }

                if (!fieldNullCheck) {
                    throw new RdosDefineException(ErrorCode.IMPORT_DATA_NULL_ERROR);
                }
            }
        }

        return keyList;
    }

}
