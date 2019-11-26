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


package com.dtstack.engine.kylin;

import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.kylin.constraint.ConfigConstraint;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public class KylinHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(KylinHttpClient.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Charset charset = Charset.forName("UTF-8");

    private static Header CONTENT_TYPE_HEADER = new BasicHeader("Content-Type", "application/json");

    private static final String API_AUTHENTICATION = "%s/kylin/api/user/authentication";
    private static final String API_BUILD_CUBE = "%s/kylin/api/cubes/%s/build";
    private static final String API_CANCEL_JOB = "%s/kylin/api/jobs/%s/cancel";
    private static final String API_GET_JOB_STATUS = "%s/kylin/api/jobs/%s";
    private static final String API_GET_STEP_OUTPUT = "%s/kylin/api/jobs/%s/steps/%s/output";
    private static final String API_GET_JOB_LIST = "%s/kylin/api/jobs";
    private static final String API_DISCARD_JOB = "%s/kylin/api/jobs/%s/cancel";
    private static final String API_RESUME_JOB = "%s/kylin/api/jobs/%s/resume";

    private CloseableHttpClient httpClient;

    private HttpClientContext context;

    private KylinConfig kylinConfig;

    private RequestConfig requestConfig;

    public void init(KylinConfig config){
        this.kylinConfig = config;

        initRequestConfig();
        initHttpClient();
        authentication();
    }

    private void initRequestConfig(){
        Map<String, Object> connectParams = kylinConfig.getConnectParams();

        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setSocketTimeout(MapUtils.getIntValue(connectParams, ConfigConstraint.KEY_SOCKET_TIMEOUT, ConfigConstraint.DEFAULT_SOCKET_TIMEOUT));
        builder.setConnectTimeout(MapUtils.getIntValue(connectParams, ConfigConstraint.KEY_CONNECT_TIMEOUT, ConfigConstraint.DEFAULT_CONNECT_TIMEOUT));
        builder.setConnectionRequestTimeout(MapUtils.getIntValue(connectParams, ConfigConstraint.KEY_CONNECTION_REQUEST_TIMEOUT, ConfigConstraint.DEFAULT_CONNECTION_REQUEST_TIMEOUT));
        requestConfig = builder.build();
    }

    private void initHttpClient(){
        CookieStore cookieStore =  new BasicCookieStore();
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        httpClient = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private void authentication(){
        String userPass = String.format("%s:%s", kylinConfig.getUsername(), kylinConfig.getPassword());
        String encodeStr = Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));
        String authorization = String.format("Basic %s", encodeStr);
        Map<String, String> headerParams = new HashMap<>(1);
        headerParams.put("Authorization", authorization);

        RequestResult requestResult = get(String.format(API_AUTHENTICATION, kylinConfig.getHostPort()), headerParams);
        if(requestResult.getStatusCode() != HttpStatus.SC_OK){
            throw new RdosException("Login failed, please check if the account information is correct.Error info:" + requestResult.getMsg());
        }
    }

    public RequestResult buildCube(KylinConfig kylinConfig){
        String bodyEntity;
        try {
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put(ConfigConstraint.KEY_BUILD_TYPE, kylinConfig.getBuildType());

            if(kylinConfig.getStartTime() != null){
                bodyMap.put(ConfigConstraint.KEY_START_TIME, kylinConfig.getStartTime());
            }

            if(kylinConfig.getEndTime() != null){
                bodyMap.put(ConfigConstraint.KEY_END_TIME, kylinConfig.getEndTime());
            }

            bodyEntity = objectMapper.writeValueAsString(bodyMap);
        } catch (Exception e){
            logger.error("Failed to build body map:", e);
            throw new RdosException("Failed to build body map");
        }

        return put(String.format(API_BUILD_CUBE, kylinConfig.getHostPort(), kylinConfig.getCubeName()), bodyEntity);
    }

    public RequestResult cancelJob(String jobId){
        return put(String.format(API_CANCEL_JOB, kylinConfig.getHostPort(), jobId), null);
    }

    public RequestResult getJobStatus(String jobId){
        return get(String.format(API_GET_JOB_STATUS, kylinConfig.getHostPort(), jobId), null);
    }

    public RequestResult getStepOutput(String jobId, String stepId){
        return get(String.format(API_GET_STEP_OUTPUT, kylinConfig.getHostPort(), jobId, stepId), null);
    }

    public RequestResult getJobList(String cubeName, Integer limit){
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("cubeName", cubeName);

        if(limit != null){
            headerParams.put("limit", limit.toString());
        }

        return get(String.format(API_GET_JOB_LIST, kylinConfig.getHostPort()), headerParams);
    }

    public RequestResult discardJob(String jobId){
        return put(String.format(API_DISCARD_JOB, kylinConfig.getHostPort(), jobId), null);
    }

    public RequestResult resumeJob(String jobId){
        return put(String.format(API_RESUME_JOB, kylinConfig.getHostPort(), jobId), null);
    }

    private RequestResult get(String url, Map<String, String> headerParams){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        if(MapUtils.isNotEmpty(headerParams)){
            headerParams.forEach((name, value) -> httpGet.setHeader(name, value));
        }

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            return new RequestResult(response.getStatusLine().getStatusCode(), null, EntityUtils.toString(response.getEntity(), charset));
        } catch (Exception e){
            logger.error("Http get request error,url:{}, error info: ", url, e);
            return new RequestResult(-1, e.getMessage(), null);
        }
    }

    private RequestResult put(String url, String bodyEntity){
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(requestConfig);
        httpPut.setHeader(CONTENT_TYPE_HEADER);

        if(StringUtils.isNotEmpty(bodyEntity)){
            try {
                httpPut.setEntity(new StringEntity(bodyEntity));
            } catch (UnsupportedEncodingException e){
                logger.error("Failed to build put request body:", e);
                throw new RdosException("Failed to build put request body");
            }
        }

        try {
            CloseableHttpResponse response = httpClient.execute(httpPut, context);
            return new RequestResult(response.getStatusLine().getStatusCode(), null, EntityUtils.toString(response.getEntity(), charset));
        } catch (Exception e){
            logger.error("Http put request error,url:{}, error info: ", url, e);
            return new RequestResult(-1, e.getMessage(), null);
        }
    }

    class RequestResult{
        private int statusCode;
        private String msg;
        private String body;

        public RequestResult(int statusCode, String msg, String body) {
            this.statusCode = statusCode;
            this.msg = msg;
            this.body = body;
        }

        public String getMsg() {
            return msg;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }
    }
}
