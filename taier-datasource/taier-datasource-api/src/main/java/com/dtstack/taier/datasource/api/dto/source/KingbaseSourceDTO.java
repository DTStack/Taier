package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * kingbase 数据源信息
 *
 * @author ：wangchuan
 * date：Created in 下午7:16 2020/9/1
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class KingbaseSourceDTO extends RdbmsSourceDTO {


    @Override
    public Integer getSourceType() {
        return DataSourceType.KINGBASE8.getVal();
    }

}
