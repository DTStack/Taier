package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * yarn client sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class YarnSourceDTO extends AbstractSourceDTO {

    /**
     * hadoop 配置
     */
    private Map<String, Object> hadoopConf;

    /**
     * yarn 配置
     */
    private Map<String, Object> yarnConf;

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Integer getSourceType() {
        return DataSourceType.YARN2.getVal();
    }
}
