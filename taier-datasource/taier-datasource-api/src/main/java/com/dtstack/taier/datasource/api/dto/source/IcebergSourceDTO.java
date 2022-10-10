package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Iceberg 数据源连接信息
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IcebergSourceDTO extends RdbmsSourceDTO {

    /**
     * iceberg hive warehouse
     */
    private String warehouse;

    /**
     * iceberg hive uri
     */
    private String uri;

    /**
     * hdfs、hive 等配置的路径
     */
    private String confDir;

    /**
     * hive metaStore 客户端池大小
     */
    private Integer clients;

    @Override
    public Integer getSourceType() {
        return DataSourceType.ICEBERG.getVal();
    }
}
