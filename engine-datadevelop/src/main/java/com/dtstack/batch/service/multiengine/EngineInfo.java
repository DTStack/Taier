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

package com.dtstack.batch.service.multiengine;


import com.dtstack.engine.common.enums.MultiEngineType;

import java.util.Map;

/**
 * Reason:
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class EngineInfo {

    private MultiEngineType engineTypeEnum;

    private String engineType;

    public EngineInfo(MultiEngineType engineTypeEnum){
        this.engineTypeEnum = engineTypeEnum;
        this.engineType = engineTypeEnum.getName();
    }

    public void init(Map<String, String> conf){
    }

    public MultiEngineType getEngineTypeEnum(){
        return engineTypeEnum;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getEngineType() {
        return engineType;
    }

}
