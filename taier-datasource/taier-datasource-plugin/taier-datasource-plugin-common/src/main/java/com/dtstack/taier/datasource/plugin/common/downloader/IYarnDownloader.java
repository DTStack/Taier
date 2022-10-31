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

package com.dtstack.taier.datasource.plugin.common.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;

import java.util.List;

/**
 * @author ：wangchuan
 * date：Created in 下午1:23 2021/12/27
 * company: www.dtstack.com
 */
public interface IYarnDownloader extends IDownloader {

    /**
     * 获取 yarn tm 集合
     *
     * @return tm 集合
     */
    List<String> getTaskManagerList();
}
