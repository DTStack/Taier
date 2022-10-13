package com.dtstack.taier.datasource.plugin.kylinRestful.http.response;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.kylinRestful.http.HttpClient;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.util.EntityUtils;

public class ResultResponse {
    private final int statusCode;
    private HttpStatus httpStatus;
    private String content;
    private HttpResponse httpResponse;

    public ResultResponse(int statusCode) {
        super();
        this.statusCode = statusCode;
        if (statusCode >= 200 && statusCode < 300) {
            if (statusCode == 204) {
                this.httpStatus = HttpStatus.ServerSuccessNoContent;
            } else {
                this.httpStatus = HttpStatus.ServerSuccess;
            }
        } else if (statusCode >= 400 && statusCode < 500) {
            if (statusCode == 401) {
                this.httpStatus = HttpStatus.ServerUnauthorized;
            } else {
                this.httpStatus = HttpStatus.ServerNotSupport;
            }
        } else if (statusCode >= 500 && statusCode < 600) {
            this.httpStatus = HttpStatus.ServerError;
        } else {
            this.httpStatus = HttpStatus.UnKnow;
        }
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResultResponse(int status, String content) {
        super();
        this.statusCode = status;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        if (this.content == null) {
            HttpEntity entity = this.httpResponse.getEntity();
            try {
                String content;
                Header[] headers = this.httpResponse.getHeaders("Content-Encoding");
                if (headers != null && headers.length > 0 && headers[0].getValue().equalsIgnoreCase("gzip")) {
                    GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(entity);
                    content = EntityUtils.toString(gzipEntity, HttpClient.DEFAULT_CHARSET);
                } else {
                    content = EntityUtils.toString(entity, HttpClient.DEFAULT_CHARSET);
                }

                this.content = content;
            } catch (Exception e) {
                throw new SourceException(e.getMessage(), e);
            }
        }
        return content;
    }

    public static ResultResponse simplify(HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        ResultResponse resultResponse = new ResultResponse(statusCode);
        resultResponse.httpResponse = httpResponse;
        return resultResponse;
    }

}