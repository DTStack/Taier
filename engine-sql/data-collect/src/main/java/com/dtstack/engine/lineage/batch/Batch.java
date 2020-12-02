package com.dtstack.engine.lineage.batch;

import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.engine.lineage.CollectAppType;
import com.dtstack.engine.lineage.DataCollection;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chener
 * @Classname Batch
 * @Description TODO
 * @Date 2020/11/23 11:11
 * @Created chener@dtstack.com
 */
public class Batch extends DataCollection {

    private static Logger logger = LoggerFactory.getLogger(Batch.class);

    private static String QUERY_TENANT = "select id,dtuic_tenant_id from rdos_tenant where is_deleted=0";

    private static String PAGE_QUERY_LINEAGE_BY_TENANT = "select tenant_id,task_id,data_source_id,table_name,col,input_data_source_id,input_table_name,input_col from rdos_batch_table_table where tenant_id = ? and is_deleted =0 limit ?,?";

    private static String QUERY_LINEAGE_COUNT_BY_TENANT = "select count(*) from rdos_batch_table_table where tenant_id = ? and is_deleted =0";

    private static String PAGE_QUERY_DATASOURCE_BY_TENANT = "select data_name,data_json,type,tenant_id from rdos_batch_data_source where tenant_id = ? and is_deleted = 0 limit ?,?";

    private static String QUERY_DATASOURCE_COUNT_BY_TENANT = "select count(1) from rdos_batch_data_source where tenant_id = ? and is_deleted = 0";

    private static final String QUERY_DATA_SOURCE_INFO = "select ds.id,ds.data_name,ds.type,ds.tenant_id,pe.engine_identity from ide.rdos_batch_data_source ds join ide.rdos_project_engine pe on ds.project_id = pe.project_id where ds.is_default = 1 and ds.id = ?";

    private static final Integer PAGE_SIZE = 200;

    public Batch(DataSource dataSource,DtInsightApi dtInsightApi){
        super(dataSource,dtInsightApi);
    }

    @Override
    public CollectAppType getAppType() {
        return CollectAppType.BATCH;
    }

    @Override
    public void collect() {
        doBatchJob();
    }

    public void doBatchJob(){
        try (Connection connection = getDataSource().getConnection()) {
            Statement statement = connection.prepareStatement(QUERY_TENANT);
            ResultSet resultSet = statement.executeQuery(QUERY_TENANT);
            List<BatchTenant> tenants = new ArrayList<>();
            while (resultSet.next()){
                BatchTenant tenant = new BatchTenant();
                Long id = resultSet.getLong(1);
                tenant.setId(id);
                Long dtUicTenantId = resultSet.getLong(2);
                tenant.setTenantId(dtUicTenantId);
                tenants.add(tenant);
            }

            for (BatchTenant tenant:tenants){

                //查询血缘关系，并批量导入
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_LINEAGE_COUNT_BY_TENANT);
                preparedStatement.setLong(1,tenant.getId());
                preparedStatement.execute();
                ResultSet resultSet2 = preparedStatement.getResultSet();
                int totalCount = 0;
                if (resultSet2.next()){
                    totalCount = resultSet2.getInt(1);
                }
                if (totalCount>0){
                    double val = totalCount * 1.0d / PAGE_SIZE;
                    int pageCount = (int) Math.ceil(val);
                    for (int i = 0; i < pageCount; i++) {
                        PreparedStatement prepareStatement = connection.prepareStatement(PAGE_QUERY_LINEAGE_BY_TENANT);
                        prepareStatement.setLong(1,tenant.getId());
                        prepareStatement.setInt(2,PAGE_SIZE*i);
                        prepareStatement.setInt(3,PAGE_SIZE*(i+1));
                        prepareStatement.execute();
                        ResultSet resultSet3 = prepareStatement.getResultSet();
                        List<BatchLineage> batchLineages = new ArrayList<>();
                        while (resultSet3.next()){
                            BatchLineage lineage = new BatchLineage();
                            lineage.setTenantId(resultSet3.getLong(1));
                            lineage.setTaskId(resultSet3.getLong(2));
                            lineage.setDataSourceId(resultSet3.getLong(3));
                            lineage.setTableNam(resultSet3.getString(4));
                            lineage.setCol(resultSet3.getString(5));
                            lineage.setInputDataSourceId(resultSet3.getLong(6));
                            lineage.setInputTableName(resultSet3.getString(7));
                            lineage.setInputCol(resultSet3.getString(8));
                            batchLineages.add(lineage);
                        }
                        if (batchLineages.size()>0){
                            sendToEngine(batchLineages,tenant);
                        }
                    }
                }
                resultSet2.close();
                preparedStatement.close();
            }

        }catch (Exception e){
            logger.error("",e);
        }
    }

    private void sendToEngine(List<BatchLineage> batchLineages, BatchTenant tenant) throws PropertyVetoException {
        Set<Long> dataSourceIdSet = new HashSet<>();
        for (BatchLineage batchLineage:batchLineages){
            dataSourceIdSet.add(batchLineage.getDataSourceId());
            dataSourceIdSet.add(batchLineage.getInputDataSourceId());
        }
        Map<Long, BatchDataSource> dataSourceMap = new HashMap<>();
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(QUERY_DATA_SOURCE_INFO);
            for (Long dataSourceId:dataSourceIdSet){
                prepareStatement.setLong(1,dataSourceId);
                prepareStatement.execute();
                ResultSet resultSet = prepareStatement.getResultSet();
                while (resultSet.next()){
                    BatchDataSource batchDataSource = new BatchDataSource();
                    batchDataSource.setSourceId(resultSet.getLong(1));
                    batchDataSource.setSourceName(resultSet.getString(2));
                    batchDataSource.setSourceType(resultSet.getInt(3));
                    batchDataSource.setTenantId(resultSet.getLong(4));
                    batchDataSource.setDbName(resultSet.getString(5));
                    dataSourceMap.put(batchDataSource.getSourceId(),batchDataSource);
                }
            }
        } catch (SQLException e) {
            logger.error("",e);
        }
        List<LineageColumnColumnVO> columnColumnVOS = new ArrayList<>();
        for (BatchLineage lineage:batchLineages){
            LineageColumnColumnVO vo = new LineageColumnColumnVO();
            vo.setDtUicTenantId(tenant.getTenantId());
            vo.setAppType(AppType.RDOS.getType());
            vo.setInputColumnName(lineage.getInputCol());
            vo.setResultColumnName(lineage.getCol());
            LineageTableVO resultTableVo = getLineageTableVO(lineage.getTableNam(),dataSourceMap.get(lineage.getDataSourceId()));
            vo.setResultTableInfo(resultTableVo);
            LineageTableVO inputTableVo = getLineageTableVO(lineage.getInputTableName(),dataSourceMap.get(lineage.getInputDataSourceId()));
            vo.setInputTableInfo(inputTableVo);
            columnColumnVOS.add(vo);
        }
        sendColumnLineageToEngine(columnColumnVOS);
    }

    private void sendColumnLineageToEngine(List<LineageColumnColumnVO> columnColumnVOS){
        if(CollectionUtils.isEmpty(columnColumnVOS)){
            return;
        }
        List<List<LineageColumnColumnVO>> partition = Lists.partition(columnColumnVOS, 200);
        LineageService lineageService = getDtInsightApi().getSlbApiClient(LineageService.class);
        for (List<LineageColumnColumnVO> part : partition){
            LineageColumnColumnParam param = new LineageColumnColumnParam();
            param.setLineageTableTableVOs(part);
            lineageService.acquireOldColumnColumn(param);
        }
    }

    private static LineageTableVO getLineageTableVO(String tableNam, BatchDataSource batchDataSource) {
        LineageTableVO tableVO = new LineageTableVO();
        tableVO.setSchemaName(batchDataSource.getDbName());
        tableVO.setDbName(batchDataSource.getDbName());
        tableVO.setTableName(tableNam);
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setSourceType(BatchDataSourceTypeConvert.getEngineSourceTypeByBatchType(batchDataSource.getSourceType()));
        dataSourceVO.setSourceName(batchDataSource.getSourceName());
        dataSourceVO.setAppType(AppType.RDOS.getType());
        tableVO.setDataSourceVO(dataSourceVO);
        return tableVO;
    }

}
