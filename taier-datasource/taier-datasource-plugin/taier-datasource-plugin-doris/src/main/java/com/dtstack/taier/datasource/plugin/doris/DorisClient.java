package com.dtstack.taier.datasource.plugin.doris;

import com.dtstack.taier.datasource.plugin.mysql5.MysqlClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author ：qianyi
 * date：Created in 下午1:46 2021/07/09
 * company: www.dtstack.com
 */
public class DorisClient extends MysqlClient {

    private static final String DONT_EXIST = "doesn't exist";

    /**
     * 查询的格式: default_cluster:dbName
     *
     * @param source
     * @return
     */
    @Override
    public String getCurrentDatabase(ISourceDTO source) {
        String currentDb = super.getCurrentDatabase(source);
        return currentDb.substring(currentDb.lastIndexOf(":") + 1);
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new DorisConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.DORIS;
    }


    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        return String.format("%s.%s", schema, tableName);
    }

    @Override
    protected Pair<Character, Character> getSpecialSign() {
        return Pair.of('`', '`');
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columns = Lists.newArrayList();

        try (Connection connection = getCon(sourceDTO, queryDTO);
             ResultSet rs = connection
                     .getMetaData()
                     .getColumns(null, null, queryDTO.getTableName(), null)) {

            while (rs.next()) {
                ColumnMetaDTO cmDTO = new ColumnMetaDTO();
                cmDTO.setKey(rs.getString(4));
                cmDTO.setType(rs.getString(6));
                // 获取字段精度
                if (cmDTO.getType().equalsIgnoreCase("decimal")
                        || cmDTO.getType().equalsIgnoreCase("float")
                        || cmDTO.getType().equalsIgnoreCase("double")) {
                    cmDTO.setScale(rs.getInt(7));
                    cmDTO.setPrecision(rs.getInt(9));
                }
                cmDTO.setComment(rs.getString(12));
                cmDTO.setPart(false);
                columns.add(cmDTO);
            }
        } catch (Exception e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(
                        queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format(
                        "Failed to get the meta information of the fields of the table: %s. " +
                                "Please contact the DBA to check the database and table information: %s",
                        queryDTO.getTableName(), e.getMessage()), e);
            }
        }

        return columns;
    }
}
