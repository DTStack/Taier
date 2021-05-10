package com.dtstack.engine.api.param;

/**
 * @author yuebai
 * @date 2019-05-17
 */
public class NotifyPageParam extends NotifyParam {
    private Integer currentPage;

    private Integer pageSize;


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
