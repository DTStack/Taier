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

package com.dtstack.taier.datasource.api.downloader;

import java.util.List;

/**
 * 下载器
 *
 * @author ：wangchuan
 * date：Created in 下午5:05 2022/9/23
 * company: www.dtstack.com
 */
public interface IDownloader {

    /**
     * 配置下载器
     *
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean configure() throws Exception;

    /**
     * 获取元数据信息
     *
     * @return 元数据信息
     */
    List<String> getMetaInfo();

    /**
     * 读取下一行
     *
     * @return 下一行数据
     */
    Object readNext();

    /**
     * 是否读取到最后一行
     *
     * @return 是否是最后一行
     */
    boolean reachedEnd();

    /**
     * 关闭流
     *
     * @return 是否关闭成功
     * @throws Exception 异常信息
     */
    boolean close() throws Exception;
}
