package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2018/1/11
 */
@Data
public class BatchDirtyDataCount extends TenantProjectEntity {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 脏数据总数
     */
    private long dirtyDataNum;

    /**
     * 运行时间戳
     */
    private Timestamp runTime;

    /**
     * 各类型错误数量
     */
    private long errorNum1;

    private long errorNum2;

    private long errorNum3;

    private long errorNum4;

}
