package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * restful 数据源信息
 *
 * @author ：wangchuan
 * date：Created in 下午2:00 2021/8/9
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class RestfulSourceDTO extends AbstractSourceDTO {

    /**
     * 请求地址
     */
    private String url;

    /**
     * 协议 仅支持 HTTP/HTTPS
     */
    private String protocol;

    /**
     * 请求头信息
     */
    private Map<String, String> headers;

    /**
     * 连接超时时间，单位：秒
     */
    private Integer connectTimeout;

    /**
     * socket 超时时间，单位：秒
     */
    private Integer socketTimeout;

    /**
     * 是否开启本地缓存
     */
    private Boolean useCache;

    @Override
    public String getUsername() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public String getPassword() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Integer getSourceType() {
        return DataSourceType.RESTFUL.getVal();
    }
}
