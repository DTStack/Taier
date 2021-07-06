package com.dtstack.batch.web.pager;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:42 2021/1/5
 * @Description：分页查询条件
 */
@Data
@NoArgsConstructor
public class PageQuery<T> {
    /**
     * 查询条件具体信息
     */
    private T model;

    /**
     * 开始
     */
    private int start;

    /**
     * 当前页
     */
    private int page = 1;

    /**
     * 分页大小
     */
    private int pageSize = 10;

    /**
     * 排序 -- 正序或者逆序
     */
    private String sort;

    /**
     * 排序规则
     */
    private String orderBy;

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

    /**
     * 获取起始位置
     *
     * @return
     */
    public int getStart() {
        start = (this.page - 1) * this.pageSize;
        return start;
    }

    /**
     * 设置排序规则
     *
     * @param sort
     */
    public void setSort(String sort) {
        if (Sort.DESC.getValue().equalsIgnoreCase(sort)) {
            this.sort = Sort.DESC.getValue();
        } else {
            this.sort = Sort.ACS.getValue();
        }
    }
}
