package com.dtstack.taier.dao.domain.po;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 3:51 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CountFillDataJobStatusPO {

    private Long fillId;

    private Integer status;

    private Integer count;

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
