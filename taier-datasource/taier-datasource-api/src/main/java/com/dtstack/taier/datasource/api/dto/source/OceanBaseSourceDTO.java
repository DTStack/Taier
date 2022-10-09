package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 12:01 2021/4/21
 * @Description：oceanbase数据源信息
 */
@Data
@ToString
@SuperBuilder
public class OceanBaseSourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.OceanBase.getVal();
    }

}
