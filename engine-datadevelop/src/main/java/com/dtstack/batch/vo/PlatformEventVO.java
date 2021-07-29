package com.dtstack.batch.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 14:51
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
public class PlatformEventVO {

    /**
     * 由于接口不需要登陆，添加加密验证
     */
    private String sign;

    /**
     * 触发的事件code
     */
    private String eventCode;

    /**
     * token
     */
    private String token;

    /**
     * 手机号码
     */
    private String phone;
    private String email;

    /**
     * uic 用户 Id
     */
    private Long dtUicUserId;

    /**
     * uic 租户Id
     */
    private Long dtUicTenantId;

    /**
     * uic 租户Id 因为不同回调接口 用的不一样 导致冗余的字段
     */
    private Long tenantId;

    /**
     * 旧-项目所有者 uic用户Id
     */
    private Long oldOwnerUicUserId;

    /**
     * 新-项目所有者 uic用户Id
     */
    private Long newOwnerUicUserId;

    /**
     * 置为管理员/取消管理员 true：置为管理员 false：取消管理员
     */
    private Boolean isAdmin;

}
