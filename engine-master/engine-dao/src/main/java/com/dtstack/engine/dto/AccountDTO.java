package com.dtstack.engine.dto;

import com.dtstack.engine.domain.Account;
import io.swagger.annotations.ApiModel;

/**
 * @author yuebai
 * @date 2020-02-17
 */
@ApiModel
public class AccountDTO extends Account {

    private Long tenantId;


    private Long dtuicUserId;

    private String username;

    private String modifyUserName;

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getDtuicUserId() {
        return dtuicUserId;
    }

    public void setDtuicUserId(Long dtuicUserId) {
        this.dtuicUserId = dtuicUserId;
    }


    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
