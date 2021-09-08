package com.dtstack.engine.master.controller.param;


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
        if (currentPage ==null || currentPage<=0) {
            currentPage = 1;
        }

        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        if (pageSize ==null || pageSize<=0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
