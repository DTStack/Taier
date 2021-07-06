package com.dtstack.batch.engine.tidb.service;

import com.dtstack.batch.bo.ImportDataParam;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.core.domain.ImportColum;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.engine.tidb.writer.TiDBWriter;
import com.dtstack.batch.enums.EImportDataMatchType;
import com.dtstack.batch.enums.RedisKey;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.ProjectService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.table.IDataImportService;
import com.dtstack.batch.service.table.impl.BatchTableInfoService;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.engine.api.pojo.lineage.Column;
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
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2019-06-05
 */
@Service
public class BatchTiDBDataImportService implements IDataImportService {

    private final static Logger logger = LoggerFactory.getLogger(BatchTiDBDataImportService.class);

    @Resource
    private ITableService iTableServiceImpl;

    @Resource
    private IJdbcService jdbcServiceImpl;

    @Resource
    private BatchTableInfoService batchTableInfoService;

    @Resource
    private ProjectService projectService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProjectEngineService projectEngineService;

    @Autowired
    private UserService userService;

    @Override
    public String importData(ImportDataParam importDataParam) throws Exception {

        Predicate<ImportDataParam> checkMustParam =
                param -> null != param
                        && null != param.getDtuicTenantId()
                        && null != param.getUserId()
                        && null != param.getProjectId()
                        && null != param.getTableId()
                        && null != param.getTenantId();
        if (!checkMustParam.test(importDataParam)) {
            throw new DtCenterDefException("导入数据缺少必要参数");
        }

        BatchTableInfo tableInfo = batchTableInfoService.getTableInfo(importDataParam.getTableId(), importDataParam.getTenantId());
        if (null == tableInfo) {
            throw new DtCenterDefException("导入数据对应表不存在");
        }

        Project project = projectService.getProjectById(tableInfo.getProjectId());
        if (null == project) {
            throw new DtCenterDefException("导入数据对应项目不存在");
        }

        User user = userService.getUser(importDataParam.getUserId());
        final Long dtuicUserId = Objects.isNull(user) ? null : user.getDtuicUserId();
        if (null != importDataParam.getOverwriteFlag() && importDataParam.getOverwriteFlag() > 0) {
            //清空原有数据

            this.truncateTableData(importDataParam.getDtuicTenantId(), dtuicUserId,tableInfo, project);
        }


        //获取db数据库字段
        ProjectEngine projectEngine = projectEngineService.getProjectDb(project.getId(), MultiEngineType.TIDB.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support libra engine.", project.getId()));
        List<BatchTableColumn> tableColumn = this.getTableColumn(importDataParam.getDtuicTenantId(), tableInfo.getTableName(), projectEngine.getEngineIdentity());

        //设置redis
        String queryKey = UUID.randomUUID().toString();

        String key = RedisKey.IDE_DATA_DEV_IMP_LOCAL_STATUS.getKey(importDataParam.getTenantId(), importDataParam.getUserId(), importDataParam.getProjectId(), queryKey);
        redisTemplate.execute((RedisCallback<String>) redisConnection -> {
            redisConnection.set(key.getBytes(), "0".getBytes());
            return "init";
        });

        //执行导入文件
        executor.execute(() -> {
            try {
                importFile(importDataParam.getDtuicTenantId(), importDataParam.getProjectId(), importDataParam.getTmpFilePath(), importDataParam.getSeparator(),
                        importDataParam.getOriCharSet(), importDataParam.getStartLine(), importDataParam.getTopLineIsTitl(),
                        importDataParam.getMatchType(), tableInfo.getTableName(), tableColumn, this.getKeyList(importDataParam.getKeyRefStr(), importDataParam.getMatchType()), key,dtuicUserId);
            } catch (IOException e) {
                setImportLocalFileRedisStatus(key, "-1", "参数 matchType 非法");
                logger.error("用户 {} 导入文件{} 异常 {}", importDataParam.getUserId(), importDataParam.getTmpFilePath(), ExceptionUtils.getStackTrace(e));
            } catch (Exception e) {
                setImportLocalFileRedisStatus(key, "-1", "数据库连接异常");
                logger.error(ExceptionUtils.getStackTrace(e));
            } finally {
                File tmpFile = new File(importDataParam.getTmpFilePath());
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
        });

        //更新操作时间
        tableInfo.setLastDmlTime(new Timestamp(System.currentTimeMillis()));
        batchTableInfoService.updateTable(tableInfo);

        return queryKey;
    }

    /**
     * 处理导入的文件 转换为jdbc数据插入数据库
     *
     * @param dtuicTenantId
     * @param tmpFilePath
     * @param finalSeparator
     * @param oriCharSet
     * @param startLine
     * @param topLineIsTitle
     * @param matchType
     * @param tableName
     * @param tableColumn
     * @param keyList
     * @param key
     */
    private int importFile(Long dtuicTenantId, Long projectId, String tmpFilePath, String finalSeparator,
                           String oriCharSet, Integer startLine, Boolean topLineIsTitle, Integer matchType,
                           String tableName, List<BatchTableColumn> tableColumn, List<ImportColum> keyList, String key, Long dtuicUserId) {
        int totalLine = 0;
        Connection connection = null;
        Statement statement = null;
        try {
            File file = new File(tmpFilePath);
            if (!file.exists()) {
                setImportLocalFileRedisStatus(key, "-1", "can't find file of name " + tmpFilePath);
                throw new RdosDefineException("can't find file of name " + tmpFilePath, ErrorCode.SERVER_EXCEPTION);
            }
            if (EImportDataMatchType.BY_NAME.getType().equals(matchType) || EImportDataMatchType.BY_POS.getType().equals(matchType)) {

                ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, MultiEngineType.TIDB.getType());
                Preconditions.checkNotNull(projectEngine, "不支持TiDB引擎");

                connection = jdbcServiceImpl.getConnection(dtuicTenantId, dtuicUserId, EJobType.TIDB_SQL, projectEngine.getEngineIdentity());
                statement = connection.createStatement();
                statement.execute("use " + projectEngine.getEngineIdentity());

            }
            if (EImportDataMatchType.BY_POS.getType().equals(matchType)) {
                //根据位置匹配
                TiDBWriter.writeByPos(tmpFilePath, finalSeparator, oriCharSet, startLine, topLineIsTitle, tableColumn, connection, tableName, keyList.size());
            } else if (EImportDataMatchType.BY_NAME.getType().equals(matchType)) {
                //根据名称匹配
                TiDBWriter.writeByName(tmpFilePath, finalSeparator, oriCharSet, startLine, topLineIsTitle, tableColumn, connection, tableName, keyList.size(), keyList);
            } else {
                setImportLocalFileRedisStatus(key, "-1", "参数 matchType 非法");
                throw new RdosDefineException("参数 matchType 非法", ErrorCode.INVALID_PARAMETERS);
            }
            setImportLocalFileRedisStatus(key, "1", "");
        }catch (Exception e){
            logger.error(String.format("导入失败,原因是 %s",e.getMessage()));
        }finally {
            DBUtil.closeDBResources(null,statement,connection);
        }
        return totalLine;
    }

    private void setImportLocalFileRedisStatus(String key, String status, String errorStr) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(status)) {
            redisTemplate.execute((RedisCallback<String>) redisConnection -> {
                redisConnection.setEx(key.getBytes(), 18400L, status.getBytes());
                return null;
            });
        }

        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(errorStr)) {
            redisTemplate.opsForValue().set(key + "_ERROR", errorStr, 1, TimeUnit.DAYS);
        }

    }


    /**
     * 清除原表 原有数据
     *
     * @param dtuicTenantId
     * @param tableInfo
     * @param project
     * @throws Exception
     */
    private void truncateTableData(Long dtuicTenantId, Long dtuicUserId, BatchTableInfo tableInfo, Project project) {
        //根据project获取对应schema信息
        if (null != project) {
            ProjectEngine engine = projectEngineService.getProjectDb(project.getId(), MultiEngineType.TIDB.getType());
            Preconditions.checkNotNull(engine, "引擎不能为空");
            String schema = engine.getEngineIdentity();
            jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, dtuicUserId, EJobType.TIDB_SQL, schema, String.format("truncate table `%s`;", tableInfo.getTableName()));
        }
    }

    @Forbidden
    private List<BatchTableColumn> getTableColumn(Long dtuicTenantId, String tableName, String db){
        List<Column> columns = iTableServiceImpl.getColumns(dtuicTenantId, null, db, ETableType.TIDB, tableName);
        if (CollectionUtils.isNotEmpty(columns)) {
            return columns.stream().map(column -> {
                BatchTableColumn col = new BatchTableColumn();
                col.setColumnName(column.getName());
                col.setColumnType(column.getType().toLowerCase());
                col.setColumnIndex(column.getIndex());
                return col;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<ImportColum> getKeyList(String keyRefStr, Integer matchType) throws Exception {
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

            if (EImportDataMatchType.BY_NAME.getType().equals(matchType)) {
                for (ImportColum importColum : keyList) {
                    if (!StringUtils.isEmpty(importColum.getKey())) {
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
