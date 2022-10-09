package com.dtstack.taier.datasource.plugin.common.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 内部使用 table
 *
 * @author ：wangchuan
 * date：Created in 10:24 2022/8/4
 * company: www.dtstack.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsideTable {

    /**
     * schema 信息
     */
    private String schema;

    /**
     * table 信息
     */
    private String table;
}
