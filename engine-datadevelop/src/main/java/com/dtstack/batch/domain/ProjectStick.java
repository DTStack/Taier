package com.dtstack.batch.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 *
 * 用于首页项目置顶
 * @author sanyue
 */
@Data
public class ProjectStick extends TenantProjectEntity {

    private Timestamp stick;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 修改人id
     */
    private Long modifyUserId;
}
