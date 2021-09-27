package com.dtstack.batch.domain;


import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class Role extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String roleName;

    private Integer roleType;

    private Integer roleValue;

    private String roleDesc;

    private Long modifyUserId;

    /**
     * 创建人id
     */
    private Long createUserId;

}
