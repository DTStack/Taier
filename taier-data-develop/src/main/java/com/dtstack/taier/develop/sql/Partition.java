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

package com.dtstack.taier.develop.sql;

import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * 分区描述类
 *
 * @author jaingbo
 */
public class Partition {

    /**
     * 分区字段
     */
    private List<Pair<String,String>> partKeyValues;

    /**
     * 分区路径
     */
    private String partLocalion;

    public List<Pair<String, String>> getPartKeyValues() {
        return partKeyValues;
    }

    public void setPartKeyValues(List<Pair<String, String>> partKeyValues) {
        this.partKeyValues = partKeyValues;
    }

    public String getPartLocalion() {
        return partLocalion;
    }

    public void setPartLocalion(String partLocalion) {
        this.partLocalion = partLocalion;
    }

    @Override
    public String toString() {
        return "Partition{" +
                "partKeyValues=" + partKeyValues +
                ", partLocalion='" + partLocalion + '\'' +
                '}';
    }
}
