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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Objects;
import java.util.UUID;

/**
 * 通用方法工具类
 *
 * @author luming
 * @date 2022/1/10
 */
public class CommonUtil {

    private static final String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 判断网络请求返回response与期望code是否相等
     *
     * @param response
     * @param code
     * @return
     */
    public static Boolean codeIsEqual(Response response, Integer code) {
        if (response == null) {
            throw new SourceException("response is null");
        }
        if (!Objects.equals(response.getStatusCode(), 200)) {
            throw new SourceException("response error, " + response.getErrorMsg());
        }
        Integer statusCode = JSONObject.parseObject(response.getContent()).getInteger("code");
        return Objects.equals(statusCode, code);
    }

    /**
     * 生成uuid，不带横线
     *
     * @return
     */
    public static String createUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成短8位uuid，不带横线
     *
     * @return
     */
    public static String createShortUuid() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            sb.append(chars[x % 0x3E]);
        }
        return sb.toString();
    }
}
