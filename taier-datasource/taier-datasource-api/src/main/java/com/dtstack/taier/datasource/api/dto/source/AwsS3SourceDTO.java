package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * aws s3 sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:51 2021/5/6
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwsS3SourceDTO extends AbstractSourceDTO {

    /**
     * aws s3 文件访问密钥
     */
    private String accessKey;

    /**
     * aws s3 密钥
     */
    private String secretKey;

    /**
     * 桶所在区
     */
    private String region;

    /**
     * endPoint
     */
    private String endPoint;

    @Override
    public Integer getSourceType() {
        return DataSourceType.AWS_S3.getVal();
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
