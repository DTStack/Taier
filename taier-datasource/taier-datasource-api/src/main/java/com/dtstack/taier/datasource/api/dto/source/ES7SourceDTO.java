package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * es7 source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@ToString
@SuperBuilder
@NoArgsConstructor
public class ES7SourceDTO extends ESSourceDTO {
    @Override
    public Integer getSourceType() {
        return DataSourceType.ES7.getVal();
    }
}
