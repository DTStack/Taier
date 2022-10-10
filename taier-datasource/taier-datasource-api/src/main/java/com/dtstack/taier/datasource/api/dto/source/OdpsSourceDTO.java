package com.dtstack.taier.datasource.api.dto.source;


import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：WangChuan
 * @Date ：Created in 18:36 2020/5/22
 * @Description：Odps数据源信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OdpsSourceDTO extends RdbmsSourceDTO {
    /**
     * 配置信息
     */
    private String config;


    @Override
    public Integer getSourceType() {
        return DataSourceType.MAXCOMPUTE.getVal();
    }


}
