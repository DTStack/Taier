package com.dtstack.rdos.engine.db.dataobject;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class RdosEngineJobCache extends DataObject{

    private String jobId;

    private String jobInfo;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }
}
