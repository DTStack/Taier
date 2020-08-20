package com.dtstack.engine.master.enums;

import org.apache.commons.lang3.StringUtils;

public enum PlatformEventType {
    LOG_OUT("登出"),
    MODIFY_INFO("修改用户信息"),
    DELETE_USER("删除用户"),
    CHANGE_TENANT_OWNER("切换租户所有者"),
    DELETE_TENANT("删除租户"),
    GRANT_ADMIN("给用户授予产品权限");

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