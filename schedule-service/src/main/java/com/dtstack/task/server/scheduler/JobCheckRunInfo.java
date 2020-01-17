package com.dtstack.task.server.scheduler;


import com.dtstack.engine.common.enums.JobCheckStatus;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public class JobCheckRunInfo {

    private JobCheckStatus status;

    private String extInfo;

    public JobCheckStatus getStatus() {
        return status;
    }

    public void setStatus(JobCheckStatus status) {
        this.status = status;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public static JobCheckRunInfo createCheckInfo(JobCheckStatus status) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(status);
        jobCheckRunInfo.setExtInfo("");
        return jobCheckRunInfo;
    }

    public static JobCheckRunInfo createCheckInfo(JobCheckStatus status, String extInfo) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(status);
        jobCheckRunInfo.setExtInfo(extInfo);
        return jobCheckRunInfo;
    }

    public String getErrMsg() {
        extInfo = extInfo == null ? "" : extInfo;
        return status.getMsg() + extInfo;
    }

}
