package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：WangChuan
 * @Date ：Created in 17:54 2020/5/22
 * @Description：Oracle数据源信息
 */
@Data
@ToString
@SuperBuilder
public class OracleSourceDTO extends RdbmsSourceDTO {

    // oracle 可拔插数据库
    private String pdb;

    @Override
    public Integer getSourceType() {
        return DataSourceType.Oracle.getVal();
    }

}
