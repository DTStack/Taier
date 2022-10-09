package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 18:04 2020/5/22
 * @Description：Impala 数据源信息
 */
@Data
@ToString
@SuperBuilder
public class ImpalaSourceDTO extends RdbmsSourceDTO {
    /**
     * Hadoop defaultFS
     */
    @Builder.Default
    private String defaultFS = "";

    /**
     * Hadoop/ Hbase 配置信息
     */
    private String config;

    @Override
    public Integer getSourceType() {
        return DataSourceType.IMPALA.getVal();
    }
}
