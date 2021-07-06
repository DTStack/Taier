package com.dtstack.batch.engine.oracle.service;

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
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author shixi
 * @date 2020-02-18
 */
@Service
public class OracleProjectService implements IProjectService {


    public static Logger LOG = LoggerFactory.getLogger(OracleProjectService.class);

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
            iTableServiceImpl.createDatabase(dtuicTenantId, Objects.isNull(user) ? null : user.getDtuicUserId(), projectName.toLowerCase(), ETableType.ORACLE, projectDesc);
        }

        //初始化项目默认oracle数据源
        initDefaultSource(dtuicTenantId, projectId, dbName, projectDesc, tenantId, userId);

        return 1;
    }

    @Override
    public List<String> getRetainDB(Long dtuicTenantId,Long userId) {
        User user = userDao.getOne(userId);
        List<String> allDataBases = jdbcServiceImpl.getAllDataBases(dtuicTenantId, user.getDtuicUserId(), EJobType.ORACLE_SQL, null);
        return allDataBases;
    }

    @Override
    public List<String> getDBTableList(Long dtuicTenantId, Long userId,String dbName, Long projectId) {
        User user = userDao.getOne(userId);
        List<List<Object>> tableResult = jdbcServiceImpl.executeQuery(dtuicTenantId,user.getDtuicUserId(), EJobType.ORACLE_SQL, dbName, String.format("select TABLE_NAME from all_tables where owner = upper('%s')",dbName));
        List<String> tableList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tableResult)) {
            for (int i = 1; i < tableResult.size(); i++) {
                List<Object> record = tableResult.get(i);
                tableList.add((String)record.get(0));
            }
        }

        return tableList;
    }


    // 创建默认数据源
    private void initDefaultSource(Long dtuicTenantId, Long projectId, String dbName, String projectDesc,
                                   Long tenantId, Long userId) {

        User user = userDao.getOne(userId);

        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, user.getDtuicUserId(), EJobType.ORACLE_SQL);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jdbcUrl", jdbcInfo.getJdbcUrl());
        jsonObject.put("username", jdbcInfo.getUsername());
        jsonObject.put("password", jdbcInfo.getPassword());

        String dataSourceName = dbName + "_" + MultiEngineType.ORACLE.name();
        batchDataSourceService.createMateDataSource(dtuicTenantId, tenantId, projectId, userId, jsonObject.toJSONString(), dataSourceName, DataSourceType.Oracle.getVal());
    }
}

