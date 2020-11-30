package com.dtstack.engine.lineage.asserts;

import com.dtstack.engine.lineage.CollectAppType;
import com.dtstack.engine.lineage.DataCollection;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String table_lineage_sql = "select is_manual,lineage_table_id,input_table_id from assets_table_lineage where tenant_id = ?";

    private static final String lineage_table_sql = "select id,is_manual,table_id,table_name,db_name,data_source_name,tenant_id from assets_lineage_table where id in (%s)";

    private static final String asset_table_sql = "select id,table_name,data_source_type,data_source_id,";

    private static final String column_lineage_sql = "select is_manual,lineage_column_id,input_column_id,tenant_id from assets_column_lineage";

    private static final String lineage_column_sql = "select lineage_table_id,column_id,column_name,table_name,db_name,data_source_name,tenant_id from assets_lineage_column";

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
            Set<AssetTableLineageDTO> tableLineagesByTenant = getTableLineagesByTenant(tenantDTO);
            Set<Long> lineageTableIdSet = new HashSet<>();
            tableLineagesByTenant.forEach(l -> {
                lineageTableIdSet.add(l.getInputTableId());
                lineageTableIdSet.add(l.getLineageTableId());
            });
            Map<Long, AssetLineageTableDTO> assertLineageTableMap = getAssertLineageTableMap(lineageTableIdSet);
            Set<Long> assetTableSet = new HashSet<>(20);
            assertLineageTableMap.forEach((k, v) -> {
                if (Objects.nonNull(v.getTableId())) {
                    assetTableSet.add(v.getTableId());
                }
            });
            Map<Long, AssetTableDTO> assertTableMap = getAssertTableMap(assetTableSet);
        }
        //1.查询表级血缘
        //2.根据血缘表id查询血缘表信息
        //3.根据血缘表信息查询真实表信息
        //4.根据表信息查询engine表信息
        //5.处理数据插入
    }

    private Map<Long, AssetTableDTO> getAssertTableMap(Set<Long> tableIds) {
        Map<Long, AssetTableDTO> tableMap = new HashMap<>(20);
        String inSqlPart = Joiner.on(",").join(tableIds);
        DataSource dataSource = getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            //TODO
        } catch (SQLException e) {
            logger.error("",e);
        }
        return tableMap;
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
            sqls.add(Joiner.on(",").join(ids));
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
}
