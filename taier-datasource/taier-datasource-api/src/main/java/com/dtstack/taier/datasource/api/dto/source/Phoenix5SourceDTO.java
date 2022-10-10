package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:56 2020/7/16
 * @Description：Phoenix5 数据源信息
 */
@Data
@ToString
@SuperBuilder
public class Phoenix5SourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.PHOENIX5.getVal();
    }
}
