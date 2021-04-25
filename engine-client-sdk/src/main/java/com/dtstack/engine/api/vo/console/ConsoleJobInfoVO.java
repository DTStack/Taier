package com.dtstack.engine.api.vo.console;

import com.dtstack.engine.api.pojo.ParamAction;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 10:06 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleJobInfoVO {

    private ParamAction paramAction;

    private Integer status;

    private Timestamp execStartTime;

    private Date generateTime;

    private String waitTime;

    private String tenantName;

    public ParamAction getParamAction() {
        return paramAction;
    }

    public void setParamAction(ParamAction paramAction) {
        this.paramAction = paramAction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Date getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(Date generateTime) {
        this.generateTime = generateTime;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
