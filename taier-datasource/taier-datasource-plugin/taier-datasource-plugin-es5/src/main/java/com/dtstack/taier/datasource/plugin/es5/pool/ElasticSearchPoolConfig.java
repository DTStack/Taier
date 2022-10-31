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

package com.dtstack.taier.datasource.plugin.es5.pool;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Set;

/**
 * 连接吃配置类
 *
 * @author ：wangchuan
 * date：Created in 下午3:04 2021/12/8
 * company: www.dtstack.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ElasticSearchPoolConfig extends GenericObjectPoolConfig {

    private String clusterName;

    Set<String> nodes = Sets.newHashSet();

    private String username;

    private String password;

}
