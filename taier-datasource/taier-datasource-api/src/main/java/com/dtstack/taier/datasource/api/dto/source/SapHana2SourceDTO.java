package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@ToString
@SuperBuilder
public class SapHana2SourceDTO extends SapHanaSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.SAP_HANA2.getVal();
    }

}
