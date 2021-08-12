package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Role;
import com.dtstack.engine.api.domain.User;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/10/26
 */
@Data
public class UserRoleVO {

    private Long userId = 0L;
    private User user;
    private List<Role> roles = new ArrayList<>();
    private Integer isSelf = 0;


    /**
     * 加入项目时间
     */
    private Timestamp gmtCreate;

    public void addRoles(Role role) {
        this.roles.add(role);
    }

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

}
