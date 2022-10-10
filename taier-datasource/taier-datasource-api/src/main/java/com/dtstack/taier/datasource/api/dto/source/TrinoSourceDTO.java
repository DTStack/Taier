package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * trino sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午2:23 2021/9/7
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class TrinoSourceDTO extends RdbmsSourceDTO {

    /**
     * 位于schema上层，不为空时会在获取连接就切换到该catalog下
     */
    private String catalog;

    @Override
    public Integer getSourceType() {
        return DataSourceType.TRINO.getVal();
    }
}
