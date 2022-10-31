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

package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 10:49 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillGeneratStatusEnum {

    /**
     * 补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败
     */
    DEFAULT_VALUE(0,"默认值，按照原来的接口逻辑走"),
    REALLY_GENERATED(1,"实例正在生成中..."),
    FILL_FINISH(2,"完成生成补数据实例"),
    FILL_FAIL(3,"生成补数据失败"),
    ;

    private final Integer type;

    private final String name;

    FillGeneratStatusEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
