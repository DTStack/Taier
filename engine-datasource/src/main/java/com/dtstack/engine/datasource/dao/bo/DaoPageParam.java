package com.dtstack.engine.datasource.dao.bo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DaoPageParam {

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 分页db查询，起始偏移量，limit A,B 中的A
     */
    private Integer start;
    /**
     * 分页db查询，结束偏移量，limit A,B 中的B
     */
    private Integer end;

    private Long tenantId;

    private Integer pageSize;
    private Integer currentPage;
    private String sort;
    private String sortColumn;


    /**
     * 生成mybatis-plus能访问的分页对象
     */
    public <T> Page<T> page() {
        return new Page<>(getCurrentPage() == null ? DEFAULT_PAGE_NO : getCurrentPage(), getPageSize() == null ? DEFAULT_PAGE_SIZE : getPageSize());
    }

    /**
     * turn to page param for `db-query` and `es`
     */
    public static <T extends DaoPageParam> T turn(T t) {
        if (Objects.isNull(t.getCurrentPage())) {
            t.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(t.getPageSize())) {
            t.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int start = ((t.getCurrentPage() == 0 ?
                1 : t.getCurrentPage()) - 1) * t.getPageSize();
        t.setStart(start);
        t.setEnd(t.getPageSize());
        return t;
    }

    public DaoPageParam turn() {
        if (Objects.isNull(this.getCurrentPage())) {
            this.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(this.getPageSize())) {
            this.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int start = ((this.getCurrentPage() == 0 ? 1 : this.getCurrentPage()) - 1) * this.getPageSize();
        this.setStart(start);
        this.setEnd(this.getPageSize());
        return this;
    }

}
