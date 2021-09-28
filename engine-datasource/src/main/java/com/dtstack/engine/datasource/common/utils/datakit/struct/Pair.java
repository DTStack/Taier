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

package com.dtstack.engine.datasource.common.utils.datakit.struct;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * SLOGAN:让现在编程未来
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2019/10/24.
 * @description 键值对
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Pair<L, R> implements java.io.Serializable {
    private static final long serialVersionUID = 3295957496904951095L;
    /**
     * 左值（键）
     */
    private L leftValue;
    /**
     * 右值（值）
     */
    private R rightValue;

    public L leftValue() {
        return this.leftValue;
    }

    public R rightValue() {
        return this.rightValue;
    }

    static public <L, R> Pair<L, R> of(L lValue, R rValue) {
        return new Pair<>(lValue, rValue);
    }
}
