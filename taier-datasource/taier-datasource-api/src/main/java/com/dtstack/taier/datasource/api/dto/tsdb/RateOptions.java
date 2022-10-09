package com.dtstack.taier.datasource.api.dto.tsdb;

import lombok.Builder;
import lombok.Data;

/**
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
@Data
@Builder
public class RateOptions {

    private Boolean counter;

    private Boolean dropResets;

    private Long counterMax;

    private Long resetValue;
}
