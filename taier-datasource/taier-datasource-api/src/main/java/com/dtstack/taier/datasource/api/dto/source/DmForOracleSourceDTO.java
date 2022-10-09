package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * dm for oracle source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class DmForOracleSourceDTO extends DmSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.DMDB_For_Oracle.getVal();
    }
}
