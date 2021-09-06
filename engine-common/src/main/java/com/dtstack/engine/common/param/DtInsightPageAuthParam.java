package com.dtstack.engine.common.param;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

/**
 * @Author: 尘二(chener@dtstack.com)
 * @Date: 2018/12/21 16:07
 * @Description: 分页鉴权基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class DtInsightPageAuthParam extends DtInsightAuthParam {
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

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 分页字段
     */
    private String sortColumn;


    public DtInsightPageAuthParam turn() {
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
