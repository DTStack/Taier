package com.dtstack.taier.datasource.api.dto.tsdb;

import lombok.Builder;
import lombok.Data;

/**
 * 过滤器
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
@Data
@Builder
public class Filter {

    private FilterType type;

    private String tagk;

    private String filter;

    @Builder.Default
    private Boolean groupBy = false;
}
