package com.dtstack.taier.datasource.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多条 sql 一起执行封装类
 *
 * @author ：wangchuan
 * date：Created in 下午4:13 2022/1/7
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlMultiDTO {

    /**
     * 标识当前 sql 唯一值的 key
     */
    private String uniqueKey;

    /**
     * 需要执行的 sql
     */
    private String sql;

}
