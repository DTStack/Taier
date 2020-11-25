package com.dtstack.engine.lineage;

import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.service.DataSourceService;
import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.engine.api.vo.lineage.param.DataSourceParam;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author chener
 * @Classname Batch
 * @Description TODO
 * @Date 2020/11/23 11:11
 * @Created chener@dtstack.com
 */
public class Batch {

    private static Logger logger = LoggerFactory.getLogger(Batch.class);

    public static void main(String[] args) {
        initLog4jProperties();
        initApi();
        try {
            DataSource dataSource = getDataSource();
            doBatchJob(dataSource);
        } catch (PropertyVetoException e) {
            logger.error("",e);
        }
    }

    private static String QUERY_TENANT = "select id,dtuic_tenant_id from rdos_tenant where is_deleted=0";

    private static String PAGE_QUERY_LINEAGE_BY_TENANT = "select tenant_id,task_id,data_source_id,table_name,col,input_data_source_id,input_table_name,input_col from rdos_batch_table_table where tenant_id = ? and is_deleted =0 limit ?,?";

    private static String QUERY_LINEAGE_COUNT_BY_TENANT = "select count(*) from rdos_batch_table_table where tenant_id = ? and is_deleted =0";

    private static String PAGE_QUERY_DATASOURCE_BY_TENANT = "select data_name,data_json,type,tenant_id from rdos_batch_data_source where tenant_id = ? and is_deleted = 0 limit ?,?";

    private static String QUERY_DATASOURCE_COUNT_BY_TENANT = "select count(1) from rdos_batch_data_source where tenant_id = ? and is_deleted = 0";

    private static final String QUERY_DATA_SOURCE_INFO = "select ds.id,ds.data_name,ds.type,ds.tenant_id,pe.engine_identity from ide.rdos_batch_data_source ds join ide.rdos_project_engine pe on ds.project_id = pe.project_id where ds.is_default = 1 and ds.id = ?";

    private static final Integer PAGE_SIZE = 200;

    private static DtInsightApi dtInsightApi;

    private static void initApi(){
        DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                .setEndpoint(Conf.getConf(Conf.SERVER))
                .setServerUrls(Conf.getConf(Conf.NODES).split(","))
                .setSlb(true)
                .setToken(Conf.getConf(Conf.TOKEN));
        dtInsightApi = builder.buildApi();
    }

    public static void doBatchJob(DataSource dataSource){
        try (Connection connection = dataSource.getConnection()) {
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

                //查询数据源信息，并批量导入
                PreparedStatement statement1 = connection.prepareStatement(QUERY_DATASOURCE_COUNT_BY_TENANT);
                statement1.setLong(1,tenant.getId());
                statement1.execute();
                ResultSet resultSet1 = statement1.getResultSet();
                int countDataSource = 0;
                if(resultSet1.next()){
                    countDataSource = resultSet1.getInt(1);
                }
                if(countDataSource>0){
                    double val = countDataSource * 1.0d / PAGE_SIZE;
                    int pageCount = (int) Math.ceil(val);
                    for (int i = 0; i < pageCount; i++) {
                        PreparedStatement prepareStatement = connection.prepareStatement(PAGE_QUERY_DATASOURCE_BY_TENANT);
                        prepareStatement.setLong(1,tenant.getId());
                        prepareStatement.setInt(2,PAGE_SIZE*i);
                        prepareStatement.setInt(3,PAGE_SIZE*(i+1));
                        prepareStatement.execute();
                        ResultSet resultSet2 = prepareStatement.getResultSet();
                        List<BatchDataSource> batchDataSources = new ArrayList<>();
                        while (resultSet2.next()){
                            BatchDataSource dataSource1 = new BatchDataSource();
                            dataSource1.setSourceName(resultSet2.getString(1));
                            dataSource1.setDataJson(resultSet2.getString(2));
                            dataSource1.setSourceType(resultSet2.getInt(3));
                            dataSource1.setTenantId(resultSet2.getLong(4));
                            batchDataSources.add(dataSource1);
                        }
                        if (batchDataSources.size()>0){
                            sendDataSourceListToEngine(batchDataSources,tenant);
                        }
                    }
                }
                resultSet.close();
                statement.close();
            }

        }catch (Exception e){
            logger.error("",e);
        }
    }

    private static void sendDataSourceListToEngine(List<BatchDataSource> batchDataSources, BatchTenant tenant) {

        DataSourceService dataSourceService = dtInsightApi.getSlbApiClient(DataSourceService.class);
        DataSourceParam dataSourceParam = new DataSourceParam();
        List<DataSourceDTO> dataSourceDTOs = new ArrayList<>();
        for (BatchDataSource batchDataSource : batchDataSources) {
            DataSourceDTO dataSourceDTO = new DataSourceDTO();
            dataSourceDTO.setSourceType(batchDataSource.getSourceType());
            dataSourceDTO.setSourceName(batchDataSource.getSourceName());
            dataSourceDTO.setDataJson(batchDataSource.getDataJson());
            dataSourceDTO.setDtUicTenantId(tenant.getTenantId());
            dataSourceDTOs.add(dataSourceDTO);
        }
        dataSourceParam.setDataSourceDTOList(dataSourceDTOs);
        dataSourceService.acquireOldDataSourceList(dataSourceParam);


    }

    private static void sendToEngine(List<BatchLineage> batchLineages, BatchTenant tenant) throws PropertyVetoException {
        Set<Long> dataSourceIdSet = new HashSet<>();
        for (BatchLineage batchLineage:batchLineages){
            dataSourceIdSet.add(batchLineage.getDataSourceId());
            dataSourceIdSet.add(batchLineage.getInputDataSourceId());
        }
        DataSource dataSource = getDataSource();
        Map<Long, BatchDataSource> dataSourceMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
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
        LineageService lineageService = dtInsightApi.getSlbApiClient(LineageService.class);
        LineageColumnColumnParam param = new LineageColumnColumnParam();
        param.setLineageTableTableVOs(columnColumnVOS);
        lineageService.acquireOldColumnColumn(param);
    }

    private static LineageTableVO getLineageTableVO(String tableNam, BatchDataSource batchDataSource) {
        LineageTableVO tableVO = new LineageTableVO();
        tableVO.setSchemaName(batchDataSource.getDbName());
        tableVO.setDbName(batchDataSource.getDbName());
        tableVO.setTableName(tableNam);
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setSourceType(batchDataSource.getSourceType());
        dataSourceVO.setSourceName(batchDataSource.getSourceName());
        dataSourceVO.setAppType(AppType.RDOS.getType());
        tableVO.setDataSourceVO(dataSourceVO);
        return tableVO;
    }

    private static void initLog4jProperties() {
        InputStream is = Batch.class.getClassLoader().getResourceAsStream("log4j.properties");
        Properties p = new Properties();
        try {
            p.load(is);
            if(p!=null){
                is.close();
            }
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Properties prop = new Properties();
            InputStream in = Batch.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Conf.confMap.put(key, prop.getProperty(key));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() throws PropertyVetoException {
        return DataSourceHolder.getDataSource();
    }

    private static class DataSourceHolder{
        private static DataSource mDataSource;
        static DataSource getDataSource() throws PropertyVetoException {
            if (mDataSource == null){
                ComboPooledDataSource dataSource = new ComboPooledDataSource();
                dataSource.setDriverClass(Conf.getConf(Conf.CLASS_NAME));
                dataSource.setJdbcUrl(Conf.getConf(Conf.URL));
                dataSource.setUser(Conf.getConf(Conf.USER));
                dataSource.setPassword(Conf.getConf(Conf.PASSWORD));
                dataSource.setMaxPoolSize(20);
                dataSource.setMinPoolSize(5);
                dataSource.setInitialPoolSize(5);
                dataSource.setCheckoutTimeout(10000);
                dataSource.setTestConnectionOnCheckin(true);
                dataSource.setTestConnectionOnCheckout(true);
                mDataSource = dataSource;
            }
            return mDataSource;
        }
    }


}
