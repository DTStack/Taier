package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * es6 source dto
 *
 * @author ：luming
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
public class ES6SourceDTO extends ESSourceDTO {
    @Override
    public Integer getSourceType() {
        return DataSourceType.ES6.getVal();
    }
}
