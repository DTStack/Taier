package com.dtstack.batch.domain.po;

import lombok.Data;

/**
 * @author jiangbo
 * @time 2018/1/11
 */
@Data
public class BatchDirtyDataTopPO {

    private String taskName;

    private String tableName;

    private long totalNum;

    private long maxNum;

    private long recentNum;

}
