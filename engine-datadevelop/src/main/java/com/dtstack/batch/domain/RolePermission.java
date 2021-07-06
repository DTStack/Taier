package com.dtstack.batch.domain;


import lombok.Data;

/**
 * @author toutian
 */
@Data
public class RolePermission extends BaseEntity {

    private Long roleId;

    private Long permissionId;

    private Long tenantId;

    private Long projectId;

    private Long createUserId;

    private Long modifyUserId;
}
