package com.dtstack.engine.master.dto.fill;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:45 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataChooseProjectDTO {

    /**
     * 工程id
     * 必填
     */
    private Long projectId;


    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FillDataChooseProjectDTO that = (FillDataChooseProjectDTO) o;
        return Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
