/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.kylinRestful.http.response;

import com.dtstack.taier.datasource.plugin.kylinRestful.http.HttpClient;
import com.dtstack.taier.datasource.api.exception.SourceException;
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
            if(statusCode == 204){
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