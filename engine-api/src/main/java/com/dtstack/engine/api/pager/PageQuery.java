package com.dtstack.engine.api.pager;

import io.swagger.annotations.ApiModel;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/12/29
 */
@ApiModel
public class PageQuery<T> {

    private T model;

    private int start;

    private int page = 1;

    private int pageSize = 10;

    private String sort;

    private String orderBy;

    public PageQuery() {
    }

    public PageQuery(Integer page, Integer pageSize) {
        if (page != null && page != 0) {
            this.page = page;
        }
        if (pageSize != null && pageSize != 0) {
            this.pageSize = pageSize;
        }
        this.start = getStart();
    }

    public PageQuery(Integer page, Integer pageSize, String orderBy, String sort) {
        this(page, pageSize);
        this.orderBy = orderBy;
        setSort(sort);
    }

    public PageQuery(T model) {
        this(1, 1000);
        this.model = model;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        start = (this.page - 1) * this.pageSize;
        return start;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(String sort) {
        if (sort.equals("desc") || sort.equals("DESC")) {
            this.sort = sort;
        } else {
            this.sort = "ASC";
        }
    }

    public String getSort() {
        return sort;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}

