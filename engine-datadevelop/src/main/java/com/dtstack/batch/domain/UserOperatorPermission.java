package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 * @date 2018/11/20
 */
@Data
public class UserOperatorPermission extends TenantProjectEntity {

    private Long userId;

    /**
     * 操作类型id
     */
    private Long operatorId;

    /**
     * ddl/dml
     */
    private Integer type;

    private Long tableId;

    private Long applyId;

    private Long createUserId;

    private Long modifyUserId;

}
