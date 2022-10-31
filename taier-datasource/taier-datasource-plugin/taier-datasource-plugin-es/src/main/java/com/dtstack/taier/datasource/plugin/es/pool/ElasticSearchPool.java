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

package com.dtstack.taier.datasource.plugin.es.pool;

import com.dtstack.taier.datasource.plugin.common.Pool;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:14 2020/8/3
 * @Description：
 */
public class ElasticSearchPool extends Pool<RestHighLevelClient> {

    private ElasticSearchPoolConfig config;

    public ElasticSearchPool(ElasticSearchPoolConfig config){
        super(config, new ElasticSearchPoolFactory(config));
        this.config = config;
    }

    public ElasticSearchPoolConfig getConfig() {
        return config;
    }

    public void setConfig(ElasticSearchPoolConfig config) {
        this.config = config;
    }

}
