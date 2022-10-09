package com.dtstack.taier.datasource.plugin.restful.core.http.request;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * Http put
 *
 * @author ：wangchuan
 * date：Created in 上午10:33 2021/8/10
 * company: www.dtstack.com
 */
public class HttpPutWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "PUT";

    public HttpPutWithEntity() {
        super();
    }

    public HttpPutWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpPutWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
