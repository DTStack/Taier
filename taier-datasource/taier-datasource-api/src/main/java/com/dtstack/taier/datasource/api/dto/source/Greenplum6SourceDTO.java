package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:20 2020/5/22
 * @Description：Greenplum6 数据源信息
 */
@Data
@ToString
@SuperBuilder
public class Greenplum6SourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.GREENPLUM6.getVal();
    }

}
