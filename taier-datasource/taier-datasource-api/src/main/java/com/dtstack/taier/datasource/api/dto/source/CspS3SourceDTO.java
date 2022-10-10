package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * csp s3 sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:51 2021/12/6
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CspS3SourceDTO extends AbstractSourceDTO {

    /**
     * csp s3 文件访问密钥
     */
    private String accessKey;

    /**
     * csp s3 密钥
     */
    private String secretKey;

    /**
     * 桶所在区
     */
    private String region;

    /**
     * 域名
     */
    private String domain;

    /**
     * endPoint
     */
    private String endPoint;

    @Override
    public Integer getSourceType() {
        return DataSourceType.CSP_S3.getVal();
    }

    @Override
    public String getUsername() {
        throw new SourceException("This method is not supported");
    }

    @Override
    public String getPassword() {
        throw new SourceException("This method is not supported");
    }
}
