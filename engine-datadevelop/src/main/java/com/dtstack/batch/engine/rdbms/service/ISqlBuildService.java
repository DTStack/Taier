package com.dtstack.batch.engine.rdbms.service;

import com.dtstack.batch.engine.rdbms.common.dto.TableDTO;

/**
 * @author jiangbo
 * @date 2019/6/27
 */
public interface ISqlBuildService {
    String buildCreateSql(TableDTO createTableDTO);

    String buildRenameTableSql(String oldTable, String newTable);

    String buildAlterTableSql(String tableName, String comment, Integer lifecycle, Long catalogueId);

    String buildAddFuncSql(String funcName, String className, String resource);

    String buildDropFuncSql(String funcName);

    String quote(String values);
}
