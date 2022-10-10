package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * inceptor 数据源信息
 *
 * @author ：wangchuan
 * date：Created in 下午2:19 2021/5/6
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InceptorSourceDTO extends RdbmsSourceDTO {

    /**
     * Hadoop defaultFS
     */
    @Builder.Default
    private String defaultFS = "";

    /**
     * Hadoop 配置信息
     */
    private String config;

    /**
     * hive metaStore 连接地址
     */
    private String metaStoreUris;

    @Override
    public Integer getSourceType() {
        return DataSourceType.INCEPTOR.getVal();
    }
}
