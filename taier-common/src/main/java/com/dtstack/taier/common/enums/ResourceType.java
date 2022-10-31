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

package com.dtstack.taier.common.enums;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-15 23:41
 **/
public enum ResourceType {

    /**
     * 其他
     */
    OTHER(0),
    /**
     * 资源类型jar
     */
    JAR(1),
    /**
     * 资源类型python
     */
    PYTHON(2),

    /**
     * zip
     */
    ZIP(3),

    /**
     * egg
     */
    EGG(4);

    private int type;

    ResourceType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
