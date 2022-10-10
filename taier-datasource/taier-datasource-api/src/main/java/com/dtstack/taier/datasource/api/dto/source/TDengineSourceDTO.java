package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author luming
 * @date 2022/3/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@SuperBuilder
public class TDengineSourceDTO extends RdbmsSourceDTO {
    @Override
    public Integer getSourceType() {
        return DataSourceType.TDENGINE.getVal();
    }
}
