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

package com.dtstack.taiga.develop.utils.develop.service.impl;

import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.scheduler.service.ClusterService;
import com.dtstack.taiga.scheduler.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 枚举类中不能autowire注入属性，用于初始化枚举类中的service
 *
 * @author ：wangchuan
 * date：Created in 下午3:33 2020/11/9
 * company: www.dtstack.com
 */
@Service
public class EnumServiceInit {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @PostConstruct
    public void init() {
        Engine2DTOService.init(componentService, clusterService, environmentContext);
    }
}
