package com.dtstack.batch.engine.greenplum.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.ProjectCreateModel;
import com.dtstack.batch.dao.UserDao;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.project.IProjectService;
import com.dtstack.batch.vo.ProjectEngineVO;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chener
 * @Classname GreenplumProjectService
 * @Description TODO
 * @Date 2020/5/19 21:48
 * @Created chener@dtstack.com
 */
@Service
public class GreenplumProjectService implements IProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(GreenplumProjectService.class);

    private static final String SCHEMA_TABLE_LIST_SQL_TMPL = "SELECT tablename FROM pg_catalog.pg_tables where SCHEMANAME = '%s'";

    /**
     * pg 获取schema 列表 FIXME 是否兼容libra
     */
    private static final String DB_SCHEMA_LIST_SQL = "SELECT n.nspname AS \"Name\" "
            + " FROM pg_catalog.pg_namespace n " + " WHERE n.nspname !~ '^pg_' AND n.nspname <> 'information_schema'"
            + " ORDER BY 1";
    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Override
    public int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId, Long dtuicTenantId, ProjectEngineVO projectEngineVO) {
        String dbName = projectName;
        User user = userDao.getOne(userId);
        if (ProjectCreateModel.intrinsic.getType().equals(projectEngineVO.getCreateModel())) {
            dbName = projectEngineVO.getDatabase();
        } else {
            iTableServiceImpl.createDatabase(dtuicTenantId, Objects.isNull(user) ? null :user.getDtuicUserId(), projectName.toLowerCase(), ETableType.GREENPLUM, projectDesc);
        }

        //初始化项目默认greenplum数据源
        initDefaultSource(dtuicTenantId, projectId, dbName, projectDesc, tenantId, userId);

        return 1;
    }

    private void initDefaultSource(Long dtuicTenantId, Long projectId, String dbName, String projectDesc, Long tenantId, Long userId) {
        User user = userDao.getOne(userId);

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, user.getDtuicUserId(), EJobType.GREENPLUM_SQL);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jdbcUrl", jdbcInfo.getJdbcUrl());
        jsonObject.put("username", jdbcInfo.getUsername());
        jsonObject.put("password", jdbcInfo.getPassword());

        String dataSourceName = dbName + "_" + MultiEngineType.GREENPLUM.name();
        batchDataSourceService.createMateDataSource(dtuicTenantId, tenantId, projectId, userId, jsonObject.toJSONString(), dataSourceName, DataSourceType.GREENPLUM6.getVal());
    }

    @Override
    public List<String> getRetainDB(Long dtuicTenantId, Long userId) {
        List<List<Object>> retainDBResult = jdbcServiceImpl.executeQuery(dtuicTenantId,userId, EJobType.GREENPLUM_SQL,null, DB_SCHEMA_LIST_SQL);
        List<String> retainList = Lists.newArrayList();
        for (List<Object> objects : retainDBResult) {
            if ("name".equalsIgnoreCase(MathUtil.getString(objects.get(0)))) {
                continue;
            }
            retainList.add(MathUtil.getString(objects.get(0)));
        }
        return retainList;
    }

    @Override
    public List<String> getDBTableList(Long dtuicTenantId, Long userId, String dbName, Long projectId) {
        User user = userDao.getOne(userId);
        List<List<Object>> tableResult = jdbcServiceImpl.executeQuery(dtuicTenantId, user.getDtuicUserId(), EJobType.GREENPLUM_SQL, dbName, String.format(SCHEMA_TABLE_LIST_SQL_TMPL, dbName));
        List<String> tableList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tableResult)) {
            for (int i = 1; i < tableResult.size(); i++) {
                List<Object> record = tableResult.get(i);
                tableList.add((String)record.get(0));
            }
        }

        return tableList;
    }
}
