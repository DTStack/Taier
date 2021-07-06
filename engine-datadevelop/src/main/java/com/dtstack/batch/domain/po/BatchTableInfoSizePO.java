package com.dtstack.batch.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:10 2020/4/10
 * @Description：表大小持久化对象
 */
@Data
public class BatchTableInfoSizePO implements Serializable {
    /**
     * 表 ID
     */
    private Long id;

    /**
     * 表大小
     */
    private Long tableSize;


    /**
     * 表下文件数量
     */
    private Long tableFileCount;

    /**
     * 表大小更新时间
     */
    private Timestamp sizeUpdateTime;

    /**
     * 生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常
     */
    private Integer lifeStatus;

    /**
     * 是否分区表
     */
    private Integer isPartition;

    public BatchTableInfoSizePO(Long id, Long tableSize, Long tableFileCount, Timestamp sizeUpdateTime, Integer lifeStatus) {
        this.id = id;
        this.tableSize = tableSize;
        this.tableFileCount = tableFileCount;
        this.sizeUpdateTime = sizeUpdateTime;
        this.lifeStatus = lifeStatus;
    }

    public BatchTableInfoSizePO(Long id, Long tableSize, Long tableFileCount, Timestamp sizeUpdateTime, Integer lifeStatus, Integer isPartition) {
        this.id = id;
        this.tableSize = tableSize;
        this.tableFileCount = tableFileCount;
        this.sizeUpdateTime = sizeUpdateTime;
        this.lifeStatus = lifeStatus;
        this.isPartition = isPartition;
    }
}
