package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * doris source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@SuperBuilder
public class DorisSourceDTO extends Mysql5SourceDTO {

    private DorisRestfulSourceDTO restfulSource;

    @Override
    public Integer getSourceType() {
        return DataSourceType.DORIS.getVal();
    }
}
