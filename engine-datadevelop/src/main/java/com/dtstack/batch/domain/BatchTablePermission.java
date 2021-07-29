package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author jiangbo
 * @time 2018/5/19
 */
@Data
public class BatchTablePermission extends TenantProjectEntity {

    private Long applyId;

    private Long userId;

    private Long tableId;

    private Integer permissionType;

}
