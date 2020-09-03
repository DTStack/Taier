package com.dtstack.engine.master.enums;

import org.apache.commons.lang3.StringUtils;

public enum PlatformEventType {
    LOG_OUT("登出"),
    MODIFY_INFO("修改用户信息"),
    DELETE_USER("删除用户"),
    CHANGE_TENANT_OWNER("切换租户所有者"),
    DELETE_TENANT("删除租户"),
    ADD_TENANT("新增租户"),
    EDIT_TENANT("编辑租户"),
    ADD_USER("新增用户"),
    GRANT_USER("用户赋予产品权限"),
    TENANT_ADD_USER("租户添加用户"),
    TENANT_REMOVE_USER("租户移除用户"),
    GRANT_ADMIN("用户置为租户管理员");

    private String comment;

    private PlatformEventType(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public static PlatformEventType getByCode(String code){
        if (StringUtils.isEmpty(code)){
            return null;
        }
        for (PlatformEventType et:values()){
            if (et.name().equalsIgnoreCase(code)){
                return et;
            }
        }
        return null;
    }
}