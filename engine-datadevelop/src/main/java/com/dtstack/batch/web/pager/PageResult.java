package com.dtstack.batch.web.pager;

import com.dtstack.batch.web.pager.PageQuery;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:31 2021/1/5
 * @Description：分页结果
 */
@Data
@NoArgsConstructor
public class PageResult<T> {
    /**
     * 空分页结果
     */
    public final static com.dtstack.batch.web.pager.PageResult EMPTY_PAGE_RESULT = new com.dtstack.batch.web.pager.PageResult<>();

    /**
     * 当前页
     */
    private Integer currentPage = 0;

    /**
     * 分页大小
     */
    private Integer pageSize = 0;

    /**
     * 总数
     */
    private int totalCount;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 数据信息
     */
    private T data;

    /**
     * 附件
     */
    private Object attachment;

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
}
