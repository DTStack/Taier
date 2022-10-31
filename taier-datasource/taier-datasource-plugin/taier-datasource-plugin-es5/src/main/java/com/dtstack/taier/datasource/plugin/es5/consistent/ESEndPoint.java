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

package com.dtstack.taier.datasource.plugin.es5.consistent;

/**
 * es endPoint
 *
 * @author ：wangchuan
 * date：Created in 下午10:18 2021/12/8
 * company: www.dtstack.com
 */
public interface ESEndPoint {

    /**
     * 健康检查
     */
    String ENDPOINT_HEALTH_CHECK = "/_cluster/health";

    /**
     * 索引下数据查看
     */
    String ENDPOINT_SEARCH_FORMAT = "/%s/_search";

    /**
     * 指定索引、type数据查看
     */
    String ENDPOINT_SEARCH_TYPE_FORMAT = "/%s/%s/_search";

    /**
     * 获取所有的索引
     */
    String ENDPOINT_INDEX_GET = "/_cat/indices?format=json";

    /**
     * 获取指定索引的 mapping
     */
    String ENDPOINT_MAPPING_FORMAT = "/%s/_mapping";

    /**
     * 获取指定索引、type的 mapping
     */
    String ENDPOINT_MAPPING_TYPE_FORMAT = "/%s/%s/_mapping";

}
