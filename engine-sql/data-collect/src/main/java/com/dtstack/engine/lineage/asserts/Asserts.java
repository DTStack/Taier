package com.dtstack.engine.lineage.asserts;

import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.enums.DataSourceType;
import com.dtstack.engine.api.service.DataSourceService;
import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableParam;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.engine.api.vo.lineage.param.DataSourceParam;
import com.dtstack.engine.lineage.AppType;
import com.dtstack.engine.lineage.CollectAppType;
import com.dtstack.engine.lineage.DataCollection;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chener
 * @Classname Asserts
 * @Description TODO
 * @Date 2020/11/28 11:00
 * @Created chener@dtstack.com
 */
public class Asserts extends DataCollection {

    private static Logger logger = LoggerFactory.getLogger(Asserts.class);

    private static final String asset_tenant_sql = "select id,dtuic_tenant_id,tenant_name from assets_tenant";

    private static final String table_lineage_sql = "select is_manual,lineage_table_id,input_table_id,tenant_id from assets_table_lineage where tenant_id = ? and is_manual = 1 and is_deleted = 0";

    private static final String lineage_table_sql = "select id,is_manual,table_id,table_name,db_name,data_source_name,tenant_id from assets_lineage_table where id in (%s)";

    private static final String asset_table_sql = "select id,table_name,data_source_type,data_source_id,";

    private static final String column_lineage_sql = "select is_manual,lineage_column_id,input_column_id,tenant_id from assets_column_lineage where tenant_id = ? and is_deleted = 0";

    private static final String lineage_column_sql = "select id,lineage_table_id,is_manual,column_name,table_name,db_name,data_source_name,tenant_id from assets_lineage_column where id in (%s)";

    private static String PAGE_QUERY_DATASOURCE_BY_TENANT = "select data_source_name,data_source_json,data_source_type,tenant_id from assets_data_source where tenant_id = ? and is_deleted = 0 limit ?,?";

    private static String QUERY_DATASOURCE_COUNT_BY_TENANT = "select count(1) from assets_data_source where tenant_id = ? and is_deleted = 0";

    private static final Integer PAGE_SIZE = 200;

    public Asserts(DataSource dataSource, DtInsightApi dtInsightApi) {
        super(dataSource, dtInsightApi);
    }

    @Override
    public CollectAppType getAppType() {
        return CollectAppType.ASSERTS;
    }

    @Override
    public void collect() {
        Set<AssetTenantDTO> tenants = getTenants();
        for (AssetTenantDTO tenantDTO : tenants) {

            //数据源的查询和推送
            //查询数据源信息，并批量导入
            try {
                int countDataSource = getCountDataSource(tenantDTO);
                if (countDataSource > 0) {
                    double val = countDataSource * 1.0d / PAGE_SIZE;
                    int pageCount = (int) Math.ceil(val);
                    for (int i = 0; i < pageCount; i++) {
                        int start = i * PAGE_SIZE;
                        int end = (i + 1) * PAGE_SIZE;
                        List<AssertDataSource> batchDataSources = getAssertDataSources(tenantDTO.getId(), start, end);
                        if (batchDataSources.size() > 0) {
                            sendDataSourceListToEngine(batchDataSources, tenantDTO);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("数据源数据推送异常,e:{}", e);
            }


            //血缘关系的查询和推送
            Set<AssetTableLineageDTO> tableLineagesByTenant = getTableLineagesByTenant(tenantDTO);
            Set<Long> lineageTableIdSet = new HashSet<>();
            tableLineagesByTenant.forEach(l -> {
                lineageTableIdSet.add(l.getInputTableId());
                lineageTableIdSet.add(l.getLineageTableId());
            });
            Map<Long, AssetLineageTableDTO> assertLineageTableMap = getAssertLineageTableMap(lineageTableIdSet);
            List<LineageTableTableVO> tableTableVOS = new ArrayList<>();
            for (AssetTableLineageDTO tableLineageDTO:tableLineagesByTenant){
                LineageTableTableVO tableTableVO = new LineageTableTableVO();
                tableTableVO.setAppType(AppType.MAP.getType());
                AssetLineageTableDTO lineageTableDTO = assertLineageTableMap.get(tableLineageDTO.getLineageTableId());
                if (Objects.isNull(lineageTableDTO)){
                    continue;
                }
                LineageTableVO resultTableVo = getLineageTableVoByLineageTable(lineageTableDTO);
                tableTableVO.setResultTableInfo(resultTableVo);
                AssetLineageTableDTO lineageTableDTO1 = assertLineageTableMap.get(tableLineageDTO.getInputTableId());
                if (Objects.isNull(lineageTableDTO1)){
                    continue;
                }
                LineageTableVO inputTableVo = getLineageTableVoByLineageTable(lineageTableDTO1);
                tableTableVO.setInputTableInfo(inputTableVo);
                tableTableVO.setDtUicTenantId(tenantDTO.getDtuicTenantId());
                if (tableLineageDTO.getIsManual() == 1){
                    tableTableVO.setManual(true);
                }
                tableTableVOS.add(tableTableVO);
            }
            sendTableLineageToEngine(tableTableVOS);
            Set<AssetColumnLineageDTO> columnLineageByTenant = getColumnLineageByTenant(tenantDTO);
            Set<Long> columnIdSet = new HashSet<>(60);
            columnLineageByTenant.forEach(cl->{
                columnIdSet.add(cl.getInputColumnId());
                columnIdSet.add(cl.getLineageColumnId());
            });

            Map<Long, AssetLineageColumnDTO> lineageColumnMap = getLineageColumnMap(columnIdSet);
            List<LineageColumnColumnVO> columnColumnVOS = new ArrayList<>();
            for (AssetColumnLineageDTO columnLineageDTO:columnLineageByTenant){
                LineageColumnColumnVO vo = new LineageColumnColumnVO();
                vo.setDtUicTenantId(tenantDTO.getDtuicTenantId());
                vo.setAppType(AppType.MAP.getType());
                AssetLineageColumnDTO inputColumnDTO = lineageColumnMap.get(columnLineageDTO.getInputColumnId());
                if (Objects.isNull(inputColumnDTO)){
                    continue;
                }
                vo.setInputColumnName(inputColumnDTO.getColumnName());
                AssetLineageColumnDTO lineageColumnDTO = lineageColumnMap.get(columnLineageDTO.getLineageColumnId());
                if (Objects.isNull(lineageColumnDTO)){
                    continue;
                }
                vo.setResultColumnName(lineageColumnDTO.getColumnName());
                LineageTableVO resultTableVo = getLineageTableVO(lineageColumnMap.get(columnLineageDTO.getLineageColumnId()));
                vo.setResultTableInfo(resultTableVo);
                LineageTableVO inputTableVo = getLineageTableVO(lineageColumnMap.get(columnLineageDTO.getInputColumnId()));
                vo.setInputTableInfo(inputTableVo);
                if (columnLineageDTO.getIsManual() == 1){
                    vo.setManual(true);
                }
                columnColumnVOS.add(vo);
            }
            sendColumnLineageToEngine(columnColumnVOS);
        }
    }

    private void sendColumnLineageToEngine(List<LineageColumnColumnVO> columnColumnVOS) {
        if (CollectionUtils.isEmpty(columnColumnVOS)){
            return;
        }
        List<List<LineageColumnColumnVO>> partitions = Lists.partition(columnColumnVOS, 200);
        LineageService lineageService = getDtInsightApi().getSlbApiClient(LineageService.class);
        for (List<LineageColumnColumnVO> part : partitions){
            LineageColumnColumnParam param = new LineageColumnColumnParam();
            param.setLineageTableTableVOs(part);
            lineageService.acquireOldColumnColumn(param);
        }
    }

    private void sendTableLineageToEngine(List<LineageTableTableVO> tableTableVOS){
        if (CollectionUtils.isEmpty(tableTableVOS)){
            return;
        }
        List<List<LineageTableTableVO>> partition = Lists.partition(tableTableVOS, 200);
        LineageService lineageService = getDtInsightApi().getSlbApiClient(LineageService.class);
        for (List<LineageTableTableVO> part : partition){
            LineageTableTableParam param = new LineageTableTableParam();
            param.setLineageTableTableVOs(part);
            lineageService.acquireOldTableTable(param);
        }
    }

    private LineageTableVO getLineageTableVoByLineageTable(AssetLineageTableDTO assetLineageTableDTO){
        LineageTableVO tableVO = new LineageTableVO();
        tableVO.setSchemaName(assetLineageTableDTO.getDbName());
        tableVO.setDbName(assetLineageTableDTO.getDbName());
        tableVO.setTableName(assetLineageTableDTO.getTableName());
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setSourceName(assetLineageTableDTO.getDataSourceName());
        dataSourceVO.setAppType(AppType.MAP.getType());
        tableVO.setDataSourceVO(dataSourceVO);
        return tableVO;
    }

    private LineageTableVO getLineageTableVO(AssetLineageColumnDTO assetLineageColumnDTO) {
        LineageTableVO tableVO = new LineageTableVO();
        tableVO.setSchemaName(assetLineageColumnDTO.getDbName());
        tableVO.setDbName(assetLineageColumnDTO.getDbName());
        tableVO.setTableName(assetLineageColumnDTO.getTableName());
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setSourceName(assetLineageColumnDTO.getDataSourceName());
        dataSourceVO.setAppType(AppType.MAP.getType());
        tableVO.setDataSourceVO(dataSourceVO);
        return tableVO;
    }

    private Map<Long, AssetLineageColumnDTO> getLineageColumnMap(Set<Long> columnIdSet) {
        Map<Long,AssetLineageColumnDTO> resMap = new HashMap<>(60);
        DataSource dataSource = getDataSource();
        List<String> sqls = new ArrayList<>();
        List<Long> idList = Lists.newArrayList(columnIdSet);
        //超过100条，分批查询
        List<List<Long>> partitions = Lists.partition(idList, batch_size);
        for (List<Long> ids : partitions) {
            List<String> holders = ids.stream().map(id -> "?").collect(Collectors.toList());
            sqls.add(Joiner.on(",").join(holders));
        }
        try (Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < partitions.size(); i++) {
                String sql = sqls.get(i);
                List<Long> params = partitions.get(i);
                String preparedSql = String.format(lineage_column_sql, sql);
                PreparedStatement prepareStatement = connection.prepareStatement(preparedSql);
                for (int j = 0; j < params.size(); j++) {
                    prepareStatement.setLong(j + 1, params.get(j));
                }
                ResultSet resultSet = prepareStatement.executeQuery();
                while (resultSet.next()) {
                    //id,lineage_table_id,is_manual,column_name,table_name,db_name,data_source_name,tenant_id
                    AssetLineageColumnDTO columnDTO = new AssetLineageColumnDTO();
                    columnDTO.setId(resultSet.getLong(1));
                    columnDTO.setLineageTableId(resultSet.getLong(2));
                    columnDTO.setIsManual(resultSet.getInt(3));
                    columnDTO.setColumnName(resultSet.getString(4));
                    columnDTO.setTableName(resultSet.getString(5));
                    columnDTO.setDbName(resultSet.getString(6));
                    columnDTO.setDataSourceName(resultSet.getString(7));
                    columnDTO.setTenantId(resultSet.getLong(8));
                    resMap.put(columnDTO.getId(), columnDTO);
                }
                resultSet.close();
                prepareStatement.close();
            }
        } catch (SQLException e) {
            logger.error("",e);
        }
        return resMap;
    }

    /**
     * @param tenantId:
     * @author zyd
     * @Description 获取资产数据源列表
     * @Date 2020/12/1 11:54 上午
     * @return: java.util.List<BatchDataSource>
     **/
    private List<AssertDataSource> getAssertDataSources(Long tenantId, int start, int end) throws Exception {

        List<AssertDataSource> assertDataSources = new ArrayList<>();
        DataSource dataSource = getDataSource();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(PAGE_QUERY_DATASOURCE_BY_TENANT);
        ) {
            statement.setLong(1, tenantId);
            statement.setLong(2, start);
            statement.setLong(3, end);
            try (
                    ResultSet resultSet = statement.executeQuery();
            ) {
                while (resultSet.next()) {
                    AssertDataSource dataSource1 = new AssertDataSource();
                    dataSource1.setSourceName(resultSet.getString(1));
                    dataSource1.setDataJson(resultSet.getString(2));
                    dataSource1.setSourceType(resultSet.getInt(3));
                    dataSource1.setTenantId(resultSet.getLong(4));
                    assertDataSources.add(dataSource1);
                }
            }
        } catch (Exception e) {
            logger.error(this.getClass() + ":getAssertDataSources" + "分页查询数据源异常，tenantId:{},e:{}",
                    tenantId, tenantId,e);
        }
        return assertDataSources;
    }

    /**
     * @author zyd
     * @Description 查询数据源数量
     * @Date 2020/12/1 11:38 上午
     * @return: int
     **/
    private int getCountDataSource(AssetTenantDTO tenant) throws SQLException {

        DataSource dataSource = getDataSource();
        int countDataSource = 0;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(QUERY_DATASOURCE_COUNT_BY_TENANT);
        ) {
            statement.setLong(1, tenant.getId());
            try (ResultSet resultSet = statement.executeQuery();) {
                if (resultSet.next()) {
                    countDataSource = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("查询数据源个数异常,e:{}", e);
        }
        return countDataSource;
    }

    private static final int batch_size = 100;

    private Map<Long, AssetLineageTableDTO> getAssertLineageTableMap(Set<Long> lineageTableSet) {
        Map<Long, AssetLineageTableDTO> resMap = new HashMap<>(20);
        if (CollectionUtils.isEmpty(lineageTableSet)) {
            return resMap;
        }
        DataSource dataSource = getDataSource();
        List<String> sqls = new ArrayList<>();
        List<Long> idList = Lists.newArrayList(lineageTableSet);
        //超过100条，分批查询
        List<List<Long>> partitions = Lists.partition(idList, batch_size);
        for (List<Long> ids : partitions) {
            List<String> holders = ids.stream().map(id -> "?").collect(Collectors.toList());
            sqls.add(Joiner.on(",").join(holders));
        }
        try (Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < partitions.size(); i++) {
                String sql = sqls.get(i);
                List<Long> params = partitions.get(i);
                String preparedSql = String.format(lineage_table_sql, sql);
                PreparedStatement prepareStatement = connection.prepareStatement(preparedSql);
                for (int j = 0; j < params.size(); j++) {
                    prepareStatement.setLong(j + 1, params.get(j));
                }
                ResultSet resultSet = prepareStatement.executeQuery();
                while (resultSet.next()) {
                    AssetLineageTableDTO lineageTableDTO = new AssetLineageTableDTO();
                    lineageTableDTO.setId(resultSet.getLong(1));
                    lineageTableDTO.setDataSourceName(resultSet.getString(6));
                    lineageTableDTO.setDbName(resultSet.getString(5));
                    lineageTableDTO.setIsManual(resultSet.getInt(2));
                    lineageTableDTO.setTableId(resultSet.getLong(3));
                    lineageTableDTO.setTableName(resultSet.getString(4));
                    lineageTableDTO.setTenantId(resultSet.getLong(7));
                    resMap.put(lineageTableDTO.getId(), lineageTableDTO);
                }
                resultSet.close();
                prepareStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resMap;
    }

    /**
     * 表级血缘只抓取手动维护的。因为字段级血缘包含了表级血缘
     *
     * @param tenantDTO
     * @return
     */
    private Set<AssetTableLineageDTO> getTableLineagesByTenant(AssetTenantDTO tenantDTO) {
        DataSource dataSource = getDataSource();
        Set<AssetTableLineageDTO> res = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(table_lineage_sql);) {
            prepareStatement.setLong(1, tenantDTO.getId());
            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    AssetTableLineageDTO tableLineageDTO = new AssetTableLineageDTO();
                    tableLineageDTO.setInputTableId(resultSet.getLong(3));
                    tableLineageDTO.setIsManual(resultSet.getInt(1));
                    tableLineageDTO.setLineageTableId(resultSet.getLong(2));
                    tableLineageDTO.setTenantId(resultSet.getLong(4));
                    res.add(tableLineageDTO);
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return res;
    }

    private Set<AssetColumnLineageDTO> getColumnLineageByTenant(AssetTenantDTO tenantDTO) {
        Set<AssetColumnLineageDTO> res = new HashSet<>();
        DataSource dataSource = getDataSource();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(column_lineage_sql)) {
            prepareStatement.setLong(1, tenantDTO.getId());
            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    AssetColumnLineageDTO columnLineageDTO = new AssetColumnLineageDTO();
                    columnLineageDTO.setIsManual(resultSet.getInt(1));
                    columnLineageDTO.setLineageColumnId(resultSet.getLong(2));
                    columnLineageDTO.setInputColumnId(resultSet.getLong(3));
                    res.add(columnLineageDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    private Set<AssetTenantDTO> getTenants() {
        DataSource dataSource = getDataSource();
        Set<AssetTenantDTO> res = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(asset_tenant_sql);) {
            while (resultSet.next()) {
                AssetTenantDTO tenantDTO = new AssetTenantDTO();
                tenantDTO.setId(resultSet.getLong(1));
                tenantDTO.setDtuicTenantId(resultSet.getLong(2));
                tenantDTO.setTenantName(resultSet.getString(3));
                res.add(tenantDTO);
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return res;
    }

    private void sendDataSourceListToEngine(List<AssertDataSource> assertDataSourceList, AssetTenantDTO tenant) {

        DataSourceService dataSourceService = getDtInsightApi().getSlbApiClient(DataSourceService.class);
        DataSourceParam dataSourceParam = new DataSourceParam();
        List<DataSourceDTO> dataSourceDtos = new ArrayList<>();
        for (AssertDataSource assertDataSource : assertDataSourceList) {
            DataSourceDTO dataSourceDTO = new DataSourceDTO();
            if(StringUtils.isBlank(assertDataSource.getDataJson())){
                assertDataSource.setSourceType(1000);
            }
            //数据源类型进行转换
            String nameByTypeCode = AssertDataSourceTypeEnum.getNameByTypeCode(assertDataSource.getSourceType());
            DataSourceType byName = DataSourceType.getByName(nameByTypeCode);
            if (byName == null) {
                logger.error("数据源类型不支持,tenantId:{},sourceTypeName:{},sourceType:{}", tenant.getDtuicTenantId(),
                        nameByTypeCode, assertDataSource.getSourceType());
                continue;
            }
            dataSourceDTO.setSourceType(byName.getType());
            dataSourceDTO.setSourceName(assertDataSource.getSourceName());
            dataSourceDTO.setDataJson(assertDataSource.getDataJson());
            dataSourceDTO.setDtUicTenantId(tenant.getDtuicTenantId());
            //资产平台
            dataSourceDTO.setAppType(AppType.MAP.getType());
            dataSourceDtos.add(dataSourceDTO);
        }
        dataSourceParam.setDataSourceDTOList(dataSourceDtos);
        dataSourceService.acquireOldDataSourceList(dataSourceParam);
    }

}
