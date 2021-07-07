package com.dtstack.batch.engine.adbpg.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.ProjectCreateModel;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.project.IProjectService;
import com.dtstack.batch.vo.ProjectEngineVO;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.thread.RdosThreadFactory;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ADB For PG 引擎项目service
 * date: 2021/6/7 2:02 下午
 * author: zhaiyue
 */
@Service
public class BatchADBPGProjectService implements IProjectService {

    public static Logger LOG = LoggerFactory.getLogger(BatchADBPGProjectService.class);

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    public static String SCHEMA_PARAM_TEPLATE = "currentSchema=%s";

    private final static ExecutorService TIDB_CREATE_PROJECT = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000), new RdosThreadFactory("adb_for_pg_create_project"), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId, Long dtuicTenantId, ProjectEngineVO projectEngineVO) throws Exception {
        String dbName = projectName;
        User user = userService.getOne(userId);
        if (ProjectCreateModel.intrinsic.getType().equals(projectEngineVO.getCreateModel())) {
            addIntrinsicTable(dtuicTenantId, tenantId, projectId, userId, projectEngineVO);
            dbName = projectEngineVO.getDatabase();
        } else {
            iTableServiceImpl.createDatabase(dtuicTenantId, Objects.isNull(user) ? null : user.getDtuicUserId(), projectName.toLowerCase(), ETableType.ADB_FOR_PG, projectDesc);
        }

        //初始化项目默认adb_for_pg数据源
        initDefaultSource(dtuicTenantId, projectId, dbName, projectDesc, tenantId, userId);
        return 1;
    }

    /**
     * 导入已有项目： 将指定schema下的表信息映射到数栈内部的table_info上
     *
     * @param dtuicTenantId
     * @param tenantId
     * @param projectId
     * @param userId
     * @param projectEngineVO
     * @throws Exception
     */
    private void addIntrinsicTable(Long dtuicTenantId, Long tenantId, Long projectId, Long userId,
                                   ProjectEngineVO projectEngineVO) throws Exception {

        List<String> tableNameList = getDBTableList(dtuicTenantId,userId, projectEngineVO.getDatabase(), projectId);

        if (CollectionUtils.isNotEmpty(tableNameList)) {
            for (String tableName : tableNameList) {
                try {
                    TIDB_CREATE_PROJECT.execute(() ->
                            batchTableInfoService.addTableFromSql(dtuicTenantId, tenantId, projectId, tableName,
                                    projectEngineVO.getLifecycle(), projectEngineVO.getCatalogueId(), userId, NORMAL_TABLE,
                                    false, ETableType.ADB_FOR_PG.getType(), projectEngineVO.getDatabase()));
                } catch (Exception e) {
                    LOG.warn("Import table from tidb source error: ", e);
                }
            }
        }
    }

    /**
     * 获取数据源下对应的db信息
     *
     * @param dtuicTenantId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getRetainDB(Long dtuicTenantId,Long userId) throws Exception {
        User user = userService.getOne(userId);
        List<String> dbList = jdbcServiceImpl.getAllDataBases(dtuicTenantId, user.getDtuicUserId(), EJobType.ANALYTICDB_FOR_PG, "");
        return dbList;
    }

    /**
     * 获取DB下面的表列表
     *
     * @param dtuicTenantId
     * @param userId
     * @param dbName
     * @param projectId
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getDBTableList(Long dtuicTenantId, Long userId,String dbName, Long projectId) throws Exception {
        User user = userService.getOne(userId);
        List<String> tableList = jdbcServiceImpl.getTableList(dtuicTenantId,user.getDtuicUserId(), EJobType.ANALYTICDB_FOR_PG,  dbName);
        return tableList;
    }

    /**
     * 创建默认数据源信息
     *
     * @param dtuicTenantId
     * @param projectId
     * @param dbName
     * @param projectDesc
     * @param tenantId
     * @param userId
     */
    private void initDefaultSource(Long dtuicTenantId, Long projectId, String dbName, String projectDesc,
                                   Long tenantId, Long userId) {
        User user = userService.getOne(userId);
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, user.getDtuicUserId(), EJobType.ANALYTICDB_FOR_PG);
        JSONObject jsonObject = new JSONObject();

        String jdbcUrl = jdbcInfo.getJdbcUrl();
        jdbcUrl = buildJdbcURLWithParam(jdbcUrl, dbName);

        jsonObject.put("jdbcUrl", jdbcUrl);
        jsonObject.put("username", jdbcInfo.getUsername());
        jsonObject.put("password", jdbcInfo.getPassword());

        String dataSourceName = dbName + "_" + MultiEngineType.ANALYTICDB_FOR_PG.name();
        batchDataSourceService.createMateDataSource(dtuicTenantId, tenantId, projectId, userId, jsonObject.toJSONString(), dataSourceName, DataSourceType.ADB_FOR_PG.getVal());
    }

    /***
     * 构建jdbc url 的参数后缀
     * @param jdbcURL
     * @param schema
     * @return
     */
    private String buildJdbcURLWithParam(String jdbcURL, String schema) {
        String splicingSymbol = needSplicingSymbol(jdbcURL) ? "?" : "";
        String schemaStr = String.format(SCHEMA_PARAM_TEPLATE, schema);
        return String.format("%s%s%s", jdbcURL, splicingSymbol, schemaStr);
    }

    /**
     * 是否需要jdbc url 的参数拼接符 "?"
     *
     * @param jdbcURL
     * @return
     */
    private boolean needSplicingSymbol(String jdbcURL) {
        return !jdbcURL.contains("?");
    }

}
