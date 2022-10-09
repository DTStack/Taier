package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * yarn2 client
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class Yarn2SourceDTO extends YarnSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.YARN2.getVal();
    }
}
