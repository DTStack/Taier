package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * open TSDB 数据源连接信息
 *
 * @author ：wangchuan
 * date：Created in 上午10:43 2021/6/17
 * company: www.dtstack.com
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenTSDBSourceDTO extends AbstractSourceDTO {

    /**
     * url
     */
    private String url;

    @Override
    public Integer getSourceType() {
        return DataSourceType.OPENTSDB.getVal();
    }

    @Override
    public String getUsername() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public String getPassword() {
        throw new SourceException("The method is not supported");
    }
}
