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

package com.dtstack.taiga.scheduler.vo.engine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 5:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class EngineSupportVO {

    private Integer engineType;

    private List<Integer> supportComponent;

    /**
     * 元数据组件
     */
    private Integer metadataComponent;

    public Integer getMetadataComponent() {
        return metadataComponent;
    }

    public void setMetadataComponent(Integer metadataComponent) {
        this.metadataComponent = metadataComponent;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public List<Integer> getSupportComponent() {
        return supportComponent;
    }

    public void setSupportComponent(List<Integer> supportComponent) {
        this.supportComponent = supportComponent;
    }
}
