package com.dtstack.engine.api.pager;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/4/27
 */
public class PageResult<T> {

    public final static PageResult EMPTY_PAGE_RESULT = new PageResult<>();

    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private T data;
    private Object attachment;

    private PageResult() {
    }

    /**
     * 分页查询通用方法
     */
    public PageResult(T data, int totalCount, PageQuery pageQuery) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentPage = pageQuery.getPage();
        this.pageSize = pageQuery.getPageSize();
        int totalPage = totalCount / pageSize;
        this.totalPage = (totalCount % pageSize == 0 ? totalPage : totalPage + 1);
    }

    /**
     * 分页查询可用方法
     */
    public PageResult(int currentPage, int pageSize, int totalCount, int totalPage, T data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public PageResult<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageResult<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public PageResult<T> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public PageResult<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public T getData() {
        return data;
    }

    public PageResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Object getAttachment() {
        return attachment;
    }

    public PageResult<T> setAttachment(Object attachment) {
        this.attachment = attachment;
        return this;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", data=" + data +
                ", attachment=" + attachment +
                '}';
    }
}
