package com.dtstack.batch.domain;


import com.dtstack.engine.api.domain.TenantProjectEntity;
import com.dtstack.engine.api.domain.User;
import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class RoleUser extends TenantProjectEntity {

    private Role role;

    private Long roleId;

    private User user;

    private Long userId;

    private Long createUserId;

    private Long modifyUserId;
}
