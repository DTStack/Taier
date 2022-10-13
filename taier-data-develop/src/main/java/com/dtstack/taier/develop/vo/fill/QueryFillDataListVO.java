package com.dtstack.taier.develop.vo.fill;

import com.dtstack.taier.develop.vo.base.PageVO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 1:40 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryFillDataListVO extends PageVO {

    /**
     * 补数据名称
     */
    @ApiModelProperty(value = "补数据名称")
    private String jobName;

    /**
     * 操作人用户id
     */
    @ApiModelProperty(value = "操作人用户id")
    private Long operatorId;

    /**
     * 补数据运行 格式yyyy-MM-dd
     */
    @ApiModelProperty(value = "补数据运行 格式yyyy-MM-dd")
    private String runDay;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
