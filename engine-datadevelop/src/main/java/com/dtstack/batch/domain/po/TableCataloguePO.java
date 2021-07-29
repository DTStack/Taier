package com.dtstack.batch.domain.po;


import lombok.Data;

/**
 * @author jiangbo
 * @time 2018/1/8
 */

@Data
public class TableCataloguePO {

    private Long id;

    private Long catalogueId;

    private String path;

    private Long tableId;

    private Long tenantId;

    private String tableName;

    private Integer level;

}
