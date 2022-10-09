package com.dtstack.taier.datasource.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * table Info
 *
 * @author ：wangchuan
 * date：Created in 下午12:25 2022/3/21
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    /**
     * db 信息
     */
    private String database;

    /**
     * schema 信息
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;
}
