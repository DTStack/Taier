package com.dtstack.engine.api.domain;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 10:39 上午 2020/10/15
 */
public class TenantResource extends BaseEntity{


    /**租户id**/
    private Integer tenantId;

    /**uic租户id**/
    private Integer dtUicTenantId;

    /**任务类型**/
    private Integer taskType;


    /**任务类型名称**/
    private String engineType;

    /**资源限制**/
    private String resourceLimit;

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Integer dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getResourceLimit() {
        return resourceLimit;
    }

    public void setResourceLimit(String resourceLimit) {
        this.resourceLimit = resourceLimit;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }
}
