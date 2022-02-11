package com.dtstack.taiga.dao.domain.po;

/**
 * @Author: newman
 * Date: 2020-12-18 16:02
 * Description: 状态，数量类
 * @since 1.0.0
 */
public class StatusCountPO {

    /**任务状态**/
    private Integer status;

    /**对应的数量**/
    private Integer count;

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
