package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * great db sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午3:48 2022/9/13
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class GreatDbSourceDTO extends Mysql5SourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.GREAT_DB.getVal();
    }
}
