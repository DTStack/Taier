package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Wangchuan
 * @Date ：Created in 19:24 2020/5/22
 * @Description：Mongo 数据源信息
 */
@Data
@ToString
@SuperBuilder
public class KylinSourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.Kylin.getVal();
    }

}
