package com.dtstack.batch.engine.rdbms.libra.service;

import com.dtstack.batch.engine.rdbms.common.dto.ColumnDTO;
import com.dtstack.batch.engine.rdbms.common.dto.TableDTO;
import com.dtstack.batch.engine.rdbms.service.ISqlBuildService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangbo
 * @date 2019/6/27
 */
@Service
public class LibraSqlBuildService implements ISqlBuildService {

    @Override
    public String buildCreateSql(TableDTO createTableDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("create table if not exists %s (", quote(createTableDTO.getTableName())));

        List<String> columnExprs = new ArrayList<>();
        for (ColumnDTO column : createTableDTO.getColumns()) {
            String scale = "";
            if (null != column.getPrecision()) {
                if (null != column.getScale()) {
                    scale = String.format("(%s,%s)", column.getPrecision(), column.getScale());
                } else {
                    scale = String.format("(%s)", column.getPrecision());
                }
            }
            if (null != column.getCharLen()) {
                scale = String.format("(%s)", column.getCharLen());
            }
            columnExprs.add(String.format("%s %s", quote(column.getColumnName()), column.getColumnType() + scale));
        }

        sb.append(StringUtils.join(columnExprs, ","));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String buildRenameTableSql(String oldTable, String newTable) {
        return String.format("alter table %s rename to %s", quote(oldTable), quote(newTable));
    }

    @Override
    public String buildAlterTableSql(String tableName, String comment, Integer lifecycle, Long catalogueId) {
        return String.format("COMMENT ON TABLE %s IS '%s'", quote(tableName), comment);
    }

    @Override
    public String buildAddFuncSql(String funcName, String className, String resource) {
        return StringUtils.EMPTY;
    }

    @Override
    public String buildDropFuncSql(String funcName) {
        return String.format("drop function %s()", quote(funcName));
    }

    @Override
    public String quote(String values) {
        return String.format("\"%s\"", values);
    }
}
