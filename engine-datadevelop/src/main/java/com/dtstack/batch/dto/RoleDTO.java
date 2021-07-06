package com.dtstack.batch.dto;

import com.dtstack.batch.domain.Role;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/3
 */
@Data
public class RoleDTO extends Role {

    private String roleNameLike;
}
