package com.dtstack.taier.dao.domain.po;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.taier.common.param.DtInsightPageAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;


/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
@Data
public class BasePageParam extends DtInsightPageAuthParam {

    public static final int DEFAULT_PAGE_NO = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 分页db查询，起始偏移量，limit A,B 中的A
     */
    @ApiModelProperty(hidden = true)
    private Integer start;
    /**
     * 分页db查询，结束偏移量，limit A,B 中的B
     */
    @ApiModelProperty(hidden = true)
    private Integer end;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    /**
     * 生成mybatis-plus能访问的分页对象
     */
    public <T> Page<T> page() {
        if (Objects.isNull(this.getCurrentPage())) {
            super.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(this.getPageSize())) {
            super.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return new Page<>(super.getCurrentPage(), super.getPageSize());
    }


    public BasePageParam turn() {
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
