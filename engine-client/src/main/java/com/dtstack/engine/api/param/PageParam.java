package com.dtstack.engine.api.param;


/**
 * Date: 2020/8/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class PageParam {
    private Integer currentPage;
    private Integer pageSize;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
