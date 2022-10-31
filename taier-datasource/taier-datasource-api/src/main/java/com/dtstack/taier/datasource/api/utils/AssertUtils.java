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

package com.dtstack.taier.datasource.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * 断言工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:25 2021/6/23
 * company: www.dtstack.com
 */
public class AssertUtils {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new SourceException(message);
        }
    }

    public static void isOverLength(String content, Integer limit, String message) {
        if (StringUtils.isNotBlank(content) && content.length() > limit) {
            throw new SourceException(message);
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new SourceException(message);
        }
    }

    public static void isNull(Object obj, String message) {
        if (obj != null) {
            throw new SourceException(message);
        }
    }

    public static void notBlank(String obj, String message) {
        if (StringUtils.isBlank(obj)) {
            throw new SourceException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new SourceException(message);
        }
    }

    public static void notNull(Collection collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new SourceException(message);
        }
    }

    public static void isTrue(Response response, Integer code) {
        if (response == null || response.getContent() == null) {
            throw new SourceException("response is null");
        }
        JSONObject jsonObject = JSONObject.parseObject(response.getContent());
        Integer statusCode = jsonObject.getInteger("code");
        AssertUtils.isTrue(200 == response.getStatusCode() && code.equals(statusCode), response.toString());
    }

    public static void httpSuccess(Response response) {
        httpSuccess(response, 200);
    }

    /**
     * 校验restful请求是否成功，失败则抛出异常信息
     *
     * @param response http请求response
     * @param code     http返回code码与之比较，默认为200
     */
    public static void httpSuccess(Response response, int code) {
        AssertUtils.notNull(response, "response is null");

        if (response.getStatusCode() != code) {
            if (response.getErrorMsg() != null) {
                throw new SourceException(response.getErrorMsg());
            } else {
                throw new SourceException("request error ,please check response : " + JSON.toJSONString(response));
            }
        }
    }
}
