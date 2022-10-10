package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * tidb sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午3:48 2022/3/4
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class TiDBSourceDTO extends Mysql5SourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.TiDB.getVal();
    }
}
