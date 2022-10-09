package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:35 2020/12/8
 * @Description：Vertica 数据源信息
 */
@Data
@ToString
@SuperBuilder
public class VerticaSourceDTO extends RdbmsSourceDTO {


    @Override
    public Integer getSourceType() {
        return DataSourceType.VERTICA.getVal();
    }
}
