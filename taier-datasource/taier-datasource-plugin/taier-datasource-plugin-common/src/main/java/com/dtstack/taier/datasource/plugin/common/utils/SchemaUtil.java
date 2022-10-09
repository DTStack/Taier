package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.plugin.common.base.InsideTable;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * schema 工具
 *
 * @author ：wangchuan
 * date：Created in 下午3:31 2022/1/3
 * company: www.dtstack.com
 */
public class SchemaUtil {

    /**
     * 获取 schema，优先从 SqlQueryDTO 中获取
     *
     * @param sourceDTO   数据源连接信息
     * @param sqlQueryDTO 查询条件
     * @return schema 信息
     */
    public static String getSchema(ISourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        AssertUtils.notNull(sourceDTO, "sourceDTO can't be null.");
        if (sourceDTO instanceof RdbmsSourceDTO) {
            if (Objects.isNull(sqlQueryDTO)) {
                return ((RdbmsSourceDTO) sourceDTO).getSchema();
            } else {
                return StringUtils.isNotBlank(sqlQueryDTO.getSchema()) ?
                        sqlQueryDTO.getSchema() : ((RdbmsSourceDTO) sourceDTO).getSchema();
            }
        }
        return Objects.isNull(sqlQueryDTO) ? null : sqlQueryDTO.getSchema();
    }

    /**
     * 获取表名和 schema, 如果只有一个说明只有 tableName, 如果 list 两个值则第一个是 tableName, 第二个是 schema
     *
     * @param sourceDTO    数据源信息
     * @param queryDTO     查询信息
     * @param specialSign  数据源特殊处理字符
     * @param transferFunc schema、table 转化函数
     * @return table info
     */
    public static InsideTable getTableInfo(ISourceDTO sourceDTO, SqlQueryDTO queryDTO,
                                           Pair<Character, Character> specialSign, BiFunction<String, String, String> transferFunc) {
        String schema = SchemaUtil.getSchema(sourceDTO, queryDTO);
        return getTableInfo(schema, queryDTO.getTableName(), specialSign, transferFunc);
    }

    /**
     * 获取表名和 schema, 如果只有一个说明只有 tableName, 如果 list 两个值则第一个是 tableName, 第二个是 schema
     *
     * @param schema       schema 信息
     * @param tableName    表名
     * @param specialSign  数据源特殊处理字符
     * @param transferFunc schema、table 转化函数
     * @return table info
     */
    public static InsideTable getTableInfo(String schema, String tableName, Pair<Character, Character> specialSign,
                                          BiFunction<String, String, String> transferFunc) {
        AssertUtils.notNull(specialSign, "special sign can't be null.");
        AssertUtils.notNull(transferFunc, "transferFunc can't be null.");

        String transferST = transferFunc.apply(schema, tableName);
        List<String> result = StringUtil.splitWithOutQuota(transferST, '.', specialSign);

        InsideTable insideTable = InsideTable.builder().build();
        // 如果返回值只有一个说明不含 schema
        if (result.size() == 1) {
            insideTable.setTable(result.get(0));
        } else if (result.size() == 2) {
            insideTable.setSchema(result.get(0));
            insideTable.setTable(result.get(1));
        } else {
            throw new SourceException(String.format("tableName:[%s] does not conform to the rule", transferST));
        }
        return insideTable;
    }
}
