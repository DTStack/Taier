package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Project;
import com.dtstack.engine.api.domain.User;
import lombok.Data;

import java.util.List;

/**
 * 项目列表展示实体
 * Date: 2017/4/25
 * Company: www.dtstack.com
 * author: toutian
 */
@Data
public class ProjectVO extends Project {

    private User createUser;

    private List<User> adminUsers;

    private List<User> memberUsers;

    private String produceProject;

    private String testProject;

    private Long testProjectId;

    private List<ProjectEngineVO> projectEngineList;


    public ProjectVO setAdminUsers(List<User> adminUsers) {
        this.adminUsers = adminUsers;
        return this;
    }

    public ProjectVO setMemberUsers(List<User> memberUsers) {
        this.memberUsers = memberUsers;
        return this;
    }

}
