package com.dtstack.taier.datasource.api.dto.source;


import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：WangChuan
 * @Date ：Created in 18:04 2020/5/22
 * @Description：Sqlserver数据源信息
 */
@Data
@ToString
@SuperBuilder
public class Sqlserver2017SourceDTO extends RdbmsSourceDTO {

    @Override
    public Integer getSourceType() {
        return DataSourceType.SQLSERVER_2017_LATER.getVal();
    }
}
