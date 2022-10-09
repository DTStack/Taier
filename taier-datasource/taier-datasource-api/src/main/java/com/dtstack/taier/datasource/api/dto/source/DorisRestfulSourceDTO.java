package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * doris restful source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class DorisRestfulSourceDTO extends RestfulSourceDTO implements Cloneable {

    /**
     * 集群名称
     */
    private String cluster;

    /**
     * 库
     */
    private String schema;

    private String userName;

    private String password;

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Integer getSourceType() {
        return DataSourceType.DorisRestful.getVal();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new SourceException("clone DorisRestfulSourceDTO error", e);
        }
    }
}
