package com.dtstack.batch.domain.po;

import lombok.Data;

/**
 * @author jiangbo
 * @time 2018/1/2
 */
@Data
public class StorageSizePO {

    private Long projectId;

    private String projectname;

    private Long tableId;

    private String tableName;

    private long size;

}