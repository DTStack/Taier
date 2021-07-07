
package com.dtstack.batch.engine.adbpg.service;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.table.ITablePublishService;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * ADB For PG SQl 表发布
 * date: 2021/6/7 2:02 下午
 * author: zhaiyue
 */
@Service
public class BatchADBPGTablePublishService implements ITablePublishService {

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private ITableService tableServiceimpl;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private UserService userService;

    @Override
    public Integer publish(BatchTableInfo sourceTableInfo, Project sourceProject, Long sourceDtUicTenantId, Tenant produceTenant, Long userId) throws Exception {
        ProjectEngine projectEngine = projectEngineService.getProjectDb(sourceProject.getId(), MultiEngineType.ANALYTICDB_FOR_PG.getType());
        if (Objects.isNull(projectEngine)){
            throw new RdosDefineException(String.format("project %d not support type %s", sourceProject.getId(), MultiEngineType.ANALYTICDB_FOR_PG.getType()));
        }
        User user = userService.getUser(userId);
        Long dtuicUserId = Objects.isNull(user) ? null : user.getDtuicUserId();

        String createSql = tableServiceimpl.showCreateTable(sourceDtUicTenantId, dtuicUserId, projectEngine.getEngineIdentity(), ETableType.getTableType(sourceTableInfo.getTableType()), sourceTableInfo.getTableName());
        createSql = createSql.replaceAll("(?i)create table", "create table if not exists");
        createSql = SqlFormatUtil.getStandardSql(createSql);

        BatchTableInfo produceTable = batchTableInfoService.getByTableName(sourceTableInfo.getTableName(),
                sourceProject.getTenantId(), sourceProject.getProduceProjectId(), ETableType.ADB_FOR_PG.getType());
        if (Objects.isNull(produceTable)) {
            ProjectEngine produceProjectEngine = projectEngineService.getProjectDb(sourceProject.getProduceProjectId(), MultiEngineType.ANALYTICDB_FOR_PG.getType());
            if (Objects.isNull(produceProjectEngine)){
                throw new RdosDefineException(String.format("project %d not support type %s", sourceProject.getId(), MultiEngineType.ANALYTICDB_FOR_PG.getType()));
            }
            jdbcServiceImpl.executeQueryWithoutResult(produceTenant.getDtuicTenantId(), dtuicUserId, EJobType.ANALYTICDB_FOR_PG, produceProjectEngine.getEngineIdentity(), createSql);
            return 1;
        } else {
            return 0;
        }
    }
}
