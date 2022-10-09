package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KylinRestfulSourceDTO extends AbstractSourceDTO {

    private String project;
    /**
     * url
     */
    private String url;

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
        return DataSourceType.KylinRestful.getVal();
    }
}
