package com.dtstack.batch.vo.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/24 1:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnJobStatusStatisticsVO {

    private String statusKey;

    private Long count;

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
