package com.dtstack.batch.domain;


import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class User extends BaseEntity {

    private String userName;

    private String phoneNumber;

    private Long dtuicUserId;

    private String email;

    private Integer status;

    private Long defaultProjectId;

}
