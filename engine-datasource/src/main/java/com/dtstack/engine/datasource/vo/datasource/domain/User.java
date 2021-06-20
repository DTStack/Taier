package com.dtstack.engine.datasource.vo.datasource.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * DtUIC 用户 ID
     */
    private Long dtuicUserId;

    /**
     * 邮件
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 默认项目 ID
     */
    private Long defaultProjectId;


}
