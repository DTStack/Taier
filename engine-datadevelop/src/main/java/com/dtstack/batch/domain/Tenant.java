package com.dtstack.batch.domain;


import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class Tenant extends BaseEntity {

    private String tenantName;

    private Long dtuicTenantId;

    private Long createUserId;

    private String tenantDesc;

    private Integer status;
}
