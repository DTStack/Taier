package com.dtstack.engine.api.vo;


import com.dtstack.engine.api.domain.BaseEntity;

/**
 * @author yuebai
 * @date 2020-02-17
 */
public class AccountTenantVo extends BaseEntity {

    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
