package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：WangChuan
 * @Date ：Created in 17:59 2020/5/22
 * @Description：Postgresql数据源信息
 */
@Data
@ToString
@SuperBuilder
public class PostgresqlSourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.PostgreSQL.getVal();
    }
}
