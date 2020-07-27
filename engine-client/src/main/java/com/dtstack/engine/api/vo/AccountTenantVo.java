package com.dtstack.engine.api.vo;


import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

/**
 * @author yuebai
 * @date 2020-02-17
 */
@ApiModel
public class AccountTenantVo extends BaseEntity {

    private String name;

    private String password;

    private Integer engineType;

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

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
