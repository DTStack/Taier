package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sanyue
 * @date 2018/11/20
 */
@Data
public class UserColumnPermission extends TenantProjectEntity {

    private Long userId;

    private String columnName;

    /**
     * 全部字段有权限
     */
    private Boolean fullColumn;

    private Long tableId;

    private Long applyId;

    private Long createUserId;

    private Long modifyUserId;
}
