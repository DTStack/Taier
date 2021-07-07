package com.dtstack.batch.engine.hdfs.service;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.dao.BatchDataCatalogueDao;
import com.dtstack.batch.dao.BatchTableInfoDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.table.ITablePublishService;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reason:
 * Date: 2019/6/14
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@Service
public class BathHadoopTablePublishService implements ITablePublishService {

    @Autowired
    private ITableService tableServiceImpl;

    @Autowired
    private BatchTableInfoDao batchTableInfoDao;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private BatchDataCatalogueDao batchDataCatalogueDao;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Override
    public Integer publish(BatchTableInfo sourceTableInfo, Project sourceProject, Long sourceDtUicTenantId, Tenant produceTenant, Long userId) throws Exception {

        ProjectEngine projectEngine = projectEngineService.getProjectDb(sourceProject.getId(), MultiEngineType.HADOOP.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support type %s", sourceProject.getId(), MultiEngineType.HADOOP.getType()));

        DataSourceType metaDataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(sourceProject.getId());
        String createSql = tableServiceImpl.showCreateTable(sourceDtUicTenantId, null, projectEngine.getEngineIdentity(), ETableType.getTableType(sourceTableInfo.getTableType()), sourceTableInfo.getTableName());
        createSql = createSql.replaceAll("(?i)create table", "create table if not exists");
        createSql = SqlFormatUtil.getStandardSql(createSql);

        BatchTableInfo produceTable = batchTableInfoDao.getByTableName(sourceTableInfo.getTableName(),
                produceTenant.getId(), sourceProject.getProduceProjectId(), ETableType.HIVE.getType());
        if (produceTable == null) {
            ProjectEngine produceProjectEngine = projectEngineService.getProjectDb(sourceProject.getProduceProjectId(), MultiEngineType.HADOOP.getType());
            Preconditions.checkNotNull(produceProjectEngine, String.format("project %d not support type %s", sourceProject.getProduceProjectId(), MultiEngineType.HADOOP.getType()));
            jdbcServiceImpl.executeQueryWithoutResult(produceTenant.getDtuicTenantId(), null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), produceProjectEngine.getEngineIdentity(), createSql);
            return 1;
        } else {
            //如果表已存在 要同步类目信息
            Long productCatalogueId = batchTableInfoService.getProductCatalogueId(produceTenant.getId(), sourceTableInfo.getCatalogueId(), userId);
            BatchTableInfo catalogueTable = new BatchTableInfo();
            catalogueTable.setCatalogueId(productCatalogueId);
            catalogueTable.setId(produceTable.getId());
            BatchDataCatalogue batchDataCatalogue = batchDataCatalogueDao.getOne(productCatalogueId);
            String path = batchDataCatalogue.getPath() + "/" + produceTable.getId();
            catalogueTable.setPath(path);
            batchTableInfoService.updateTable(catalogueTable);
            return 0;
        }
    }
}
