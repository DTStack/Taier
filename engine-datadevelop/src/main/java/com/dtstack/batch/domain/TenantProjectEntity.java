package com.dtstack.batch.domain;


import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class TenantProjectEntity extends BaseEntity {

    private Long tenantTenantProjectEntityId;

    private Long projectId;

    private Long dtuicTenantId;

    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    private Integer appType;


}
