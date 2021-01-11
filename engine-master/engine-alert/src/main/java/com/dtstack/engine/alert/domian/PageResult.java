package com.dtstack.engine.alert.domian;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/21 20:13
 * @Description:
 */
public class PageResult<T> {
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPage;
    private List<T> data;
    private Map<String, Object> attachment;

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
