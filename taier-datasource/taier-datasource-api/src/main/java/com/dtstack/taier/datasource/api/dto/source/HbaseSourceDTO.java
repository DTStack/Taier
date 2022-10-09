package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:44 2020/5/22
 * @Description：Hbase 数据源信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HbaseSourceDTO extends RdbmsSourceDTO {
    /**
     * 目录
     * Hbase 根目录
     */
    private String path;

    /**
     * Hbase 配置信息
     */
    private String config;

    /**
     * 其他配置信息
     */
    private String others;


    @Override
    public Integer getSourceType() {
        return DataSourceType.HBASE.getVal();
    }
}
