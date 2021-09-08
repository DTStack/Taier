package com.dtstack.batch.domain;

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/9
 */
@Data
public class ReadWriteLock extends BaseEntity {
    /**
     * 锁名称
     */
    private String lockName;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 修改的用户
     */
    private Long modifyUserId;

    /**
     * 锁版本号
     */
    private Integer version;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目Id
     */
    private Long projectId;

    /**
     * 任务Id
     */
    private Long relationId;

    /**
     * 任务类型
     */
    private String type;

}
