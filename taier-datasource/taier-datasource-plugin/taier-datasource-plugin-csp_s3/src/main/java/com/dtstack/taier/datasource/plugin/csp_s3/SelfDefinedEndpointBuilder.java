package com.dtstack.taier.datasource.plugin.csp_s3;

import com.qcloud.cos.endpoint.EndpointBuilder;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义 Endpoint
 *
 * @author ：wangchuan
 * date：Created in 下午5:07 2021/12/6
 * company: www.dtstack.com
 */
public class SelfDefinedEndpointBuilder implements EndpointBuilder {

    private String region;
    private String domain;
    private String endPoint;

    public SelfDefinedEndpointBuilder(String region, String domain) {
        super();
        // 格式化 Region
        this.region = Region.formatRegion(new Region(region));
        this.domain = domain;
    }

    public SelfDefinedEndpointBuilder(String endpoint) {
        super();
        this.endPoint = endpoint.replaceAll("http://", "").replaceAll("https://", "");
    }

    @Override
    public String buildGeneralApiEndpoint(String bucketName) {
        // 构造 Endpoint
        String endpoint = StringUtils.isNotBlank(this.endPoint) ? this.endPoint : String.format("%s.%s", this.region, this.domain);
        // 构造 Bucket 访问域名
        return String.format("%s.%s", bucketName, endpoint);
    }

    @Override
    public String buildGetServiceApiEndpoint() {
        if (StringUtils.isNotBlank(this.endPoint)) {
            return this.endPoint;
        }
        return String.format("%s.%s", this.region, this.domain);
    }
}
