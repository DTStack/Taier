package com.dtstack.sdk.core.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/21 16:07
 * @Description: 分页鉴权基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class DtInsightPageAuthParam extends DtInsightAuthParam {
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
}
