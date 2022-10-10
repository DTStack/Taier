package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * presto sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:50 2021/3/23
 * company: www.dtstack.com
 */
@ToString
@SuperBuilder
public class PrestoSourceDTO extends RdbmsSourceDTO {
    @Deprecated
    private String catalog;

    @Override
    public Integer getSourceType() {
        return DataSourceType.Presto.getVal();
    }
}
