package com.dtstack.batch.sync.util;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.pluginapi.pojo.Column;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2019/12/20
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ImpalaUtils {
    public static String getTableFileType(ISourceDTO iSourceDTO, String tableName) {
        IClient iClient = ClientCache.getClient(DataSourceType.IMPALA.getVal());
        com.dtstack.dtcenter.loader.dto.Table iClientTable = iClient.getTable(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        return  iClientTable.getStoreType();
    }

    public static Map<String, String> getImpalaKuduTableParams(ISourceDTO iSourceDTO, String tableName) {
        String sql = String.format("DESCRIBE FORMATTED %s", tableName);
        List<Map<String, Object>> list = ClientCache.getClient(DataSourceType.IMPALA.getVal()).executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).build());
        Boolean isHasName = false;
        Boolean isKudu = false;
        Map<String, String> tableParams = new HashMap<>();
        for (Map<String, Object> resultSet : list) {
            isHasName = resultSet.containsKey("name");
            if (isHasName) {
                String name = resultSet.get("name").toString();
                if (StringUtils.isNotBlank(name) && name.contains("Table Parameters:")) {
                    isKudu = true;
                }
            }
            if (isKudu) {
                String type = resultSet.getOrDefault("type", "").toString();
                String comment = resultSet.getOrDefault("comment", "").toString();
                if (StringUtils.isBlank(type) || StringUtils.isBlank(comment)) {
                    break;
                }
                tableParams.put(type.trim(), comment.trim());
            }
        }
        return tableParams;
    }

    public static Map<String, Object> getImpalaHiveTableDetailInfo(ISourceDTO iSourceDTO, String tableName) {
        IClient client = ClientCache.getClient(DataSourceType.IMPALA.getVal());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();

        com.dtstack.dtcenter.loader.dto.Table tableInfo = client.getTable(iSourceDTO, sqlQueryDTO);
        List<ColumnMetaDTO> columnMetaDTOList = tableInfo.getColumns();

        List<Column> columns = new ArrayList<>();
        List<Column> partitionColumns =  new ArrayList<>();
        ColumnMetaDTO columnMetaDTO = null;
        for (int i = 0; i < columnMetaDTOList.size(); i++) {
            columnMetaDTO = columnMetaDTOList.get(i);
            Column column = new Column();
            column.setName(columnMetaDTO.getKey());
            column.setType(columnMetaDTO.getType());
            column.setComment(columnMetaDTO.getComment());
            column.setIndex(i);

            columns.add(column);
            if(columnMetaDTO.getPart()){
                partitionColumns.add(column);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("allColumns", columns);
        map.put("partitionColumns", partitionColumns);
        map.put("path", tableInfo.getPath());
        map.put("fieldDelimiter", tableInfo.getDelim());
        return map;
    }
}
