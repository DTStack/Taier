package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * ADB_PostgreSQL sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午5:04 2021/5/28
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class AdbForPgSourceDTO extends PostgresqlSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.ADB_FOR_PG.getVal();
    }
}
