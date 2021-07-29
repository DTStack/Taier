package com.dtstack.batch.domain;


import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class BatchTaskResourceShade extends TenantProjectEntity {

    private Long taskId;

    private Long resourceId;

    private int resourceType;

}
