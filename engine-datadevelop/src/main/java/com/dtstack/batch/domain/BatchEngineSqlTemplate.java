package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author yuebai
 * @date 2019-06-10
 */
@Data
public class BatchEngineSqlTemplate extends BaseEntity {


    /**
     * '执行引擎类型
     */
    private Integer engineType;

    /**
     * 'sql 文本'
     */
    private String params;

    /**
     * 表类型 1 hive表 2 libra表
     */
    private Integer tableType;

}
