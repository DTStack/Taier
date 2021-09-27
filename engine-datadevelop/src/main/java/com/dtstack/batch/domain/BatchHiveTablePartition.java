package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * hive 分区 对应的实体类
 */
@Data
public class BatchHiveTablePartition extends TenantProjectEntity {

    /**
     * hiveTableInfo表的id
     */
    private Long tableId;

    /**
     * 分区名称
     */
    private String partitionName;

    /**
     * 分区location
     */
    private String partition;

    /**
     * 分区大小
     */
    private Long storeSize;

    /**
     * 文件数量
     */
    private Long fileCount;

    /**
     * 最后更新时间
     */
    private Timestamp lastDDLTime;

}
