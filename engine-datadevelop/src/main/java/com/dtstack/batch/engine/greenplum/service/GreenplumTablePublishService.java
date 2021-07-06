package com.dtstack.batch.engine.greenplum.service;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.dao.BatchTableInfoDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.table.ITablePublishService;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author chener
 * @Classname GreenplumTablePublishService
 * @Description TODO
 * @Date 2020/5/19 21:56
 * @Created chener@dtstack.com
 */
@Service
public class GreenplumTablePublishService implements ITablePublishService {
    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private BatchTableInfoDao batchTableInfoDao;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private UserService userService;

    @Override
    public Integer publish(BatchTableInfo sourceTableInfo, Project sourceProject, Long sourceDtUicTenantId, Tenant produceTenant, Long userId) throws Exception {

        ProjectEngine projectEngine = projectEngineService.getProjectDb(sourceProject.getId(), MultiEngineType.GREENPLUM.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support type %s", sourceProject.getId(), MultiEngineType.GREENPLUM.getType()));

        User user = userService.getUser(userId);

        Long dtuicUserId = Objects.isNull(user) ? null : user.getDtuicUserId();

        String createSql = iTableServiceImpl.showCreateTable(sourceDtUicTenantId, dtuicUserId, projectEngine.getEngineIdentity(), ETableType.getTableType(sourceTableInfo.getTableType()), sourceTableInfo.getTableName());
        createSql = SqlFormatUtil.getStandardSql(createSql);

        BatchTableInfo produceTable = batchTableInfoDao.getByTableName(sourceTableInfo.getTableName(),
                sourceProject.getTenantId(), sourceProject.getProduceProjectId(), ETableType.GREENPLUM.getType());
        if (produceTable == null) {
            ProjectEngine produceProjectEngine = projectEngineService.getProjectDb(sourceProject.getProduceProjectId(), MultiEngineType.GREENPLUM.getType());
            Preconditions.checkNotNull(produceProjectEngine, String.format("project %d not support type %s", sourceProject.getProduceProjectId(), MultiEngineType.GREENPLUM.getType()));
            jdbcServiceImpl.executeQueryWithoutResult(produceTenant.getDtuicTenantId(), dtuicUserId, EJobType.GREENPLUM_SQL, produceProjectEngine.getEngineIdentity(), createSql);
            return 1;
        } else {
            return 0;
        }
    }
}
