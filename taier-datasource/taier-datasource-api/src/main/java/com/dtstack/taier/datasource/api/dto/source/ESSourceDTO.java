package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * es source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ESSourceDTO extends RdbmsSourceDTO {
    /**
     * 其他配置信息
     */
    private String others;

    /**
     * es文档id
     */
    private String id;

    @Override
    public Integer getSourceType() {
        return DataSourceType.ES.getVal();
    }
}
