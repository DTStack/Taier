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

package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.io.File;
import java.util.Map;

/**
 * <p>提供 Restful 相关操作方法</p>
 *
 * @author ：wangchuan
 * date：Created in 上午10:06 2021/8/9
 * company: www.dtstack.com
 */
public interface IRestful extends Client {

    /**
     * get 请求
     *
     * @param source  数据源信息
     * @param params  请求参数
     * @param cookies cookie 信息
     * @param headers header 信息
     * @return 相应
     */
    Response get(ISourceDTO source, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers);

    /**
     * post 请求
     *
     * @param source   数据源信息
     * @param bodyData 请求参数
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response post(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * delete 请求
     *
     * @param source   数据源信息
     * @param bodyData 请求参数
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response delete(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * put 请求
     *
     * @param source   数据源信息
     * @param bodyData body 信息
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response put(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * put Multipart
     *
     * @param source  数据源信息
     * @param params  请求参数
     * @param cookies cookie 信息
     * @param headers header 信息
     * @param files   文件信息
     * @return 相应
     */
    Response postMultipart(ISourceDTO source, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, Map<String, File> files);
}
