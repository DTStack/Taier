package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class UserRolePermissionVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 是否为访客
     */
    private Boolean isCustomer;

}
