package com.dtstack.taier.datasource.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertColumnMetaDTO {
    /**
     * 库
     */
    private String schema;

    /**
     * table Name
     */
    private String tableName;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * 列注释
     */
    private String columnComment;

    /**
     * 列别名
     */
    private String columnAliasName;

    /**
     * 原列名，用于修改时需要修改原列名
     */
    private String originColumnName;
}
