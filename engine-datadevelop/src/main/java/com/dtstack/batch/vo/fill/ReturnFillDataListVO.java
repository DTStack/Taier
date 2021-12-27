package com.dtstack.batch.vo.fill;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 1:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnFillDataListVO {

    /**
     * 补数据标识
     */
    private Long id;

    /**
     * 补数据名称
     */
    private String fillDataName;

    /**
     * 成功job数量
     */
    private Long finishedJobSum;

    /**
     * 所有job数量
     */
    private Long allJobSum;

    /**
     * 完成的job数量
     */
    private Long doneJobSum;

    /**
     * 补数据开始时间
     */
    private String fromDay;

    /**
     * 补数据结束时间
     */
    private String toDay;

    /**
     * 补数据生成时间
     */
    private String runDay;

    /**
     * 操作人名称
     */
    private String UserName;

    /**
     * 操作人id
     */
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFillDataName() {
        return fillDataName;
    }

    public void setFillDataName(String fillDataName) {
        this.fillDataName = fillDataName;
    }

    public Long getFinishedJobSum() {
        return finishedJobSum;
    }

    public void setFinishedJobSum(Long finishedJobSum) {
        this.finishedJobSum = finishedJobSum;
    }

    public Long getAllJobSum() {
        return allJobSum;
    }

    public void setAllJobSum(Long allJobSum) {
        this.allJobSum = allJobSum;
    }

    public Long getDoneJobSum() {
        return doneJobSum;
    }

    public void setDoneJobSum(Long doneJobSum) {
        this.doneJobSum = doneJobSum;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
