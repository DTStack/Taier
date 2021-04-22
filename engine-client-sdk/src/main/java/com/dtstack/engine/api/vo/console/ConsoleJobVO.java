package com.dtstack.engine.api.vo.console;


/**
 * @Auther: dazhi
 * @Date: 2020/7/29 8:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleJobVO {
    private ConsoleJobInfoVO theJob;
    private Integer theJobIdx;
    private String nodeAddress;

    public ConsoleJobInfoVO getTheJob() {
        return theJob;
    }

    public void setTheJob(ConsoleJobInfoVO theJob) {
        this.theJob = theJob;
    }

    public Integer getTheJobIdx() {
        return theJobIdx;
    }

    public void setTheJobIdx(Integer theJobIdx) {
        this.theJobIdx = theJobIdx;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
