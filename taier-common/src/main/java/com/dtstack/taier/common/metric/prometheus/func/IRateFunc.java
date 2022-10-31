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

package com.dtstack.taier.common.metric.prometheus.func;

import com.dtstack.taier.common.metric.IFunction;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author zhiChen
 * @date 2022/4/27 20:07
 */
public class IRateFunc extends CommonFunc {
    private static final String NAME = "irate";
    private static final String TMP = "irate(${content}[${rangeVector}])";
    private String rangeVector;

    public IRateFunc() {
        super("irate");
    }

    public String getRangeVector() {
        return this.rangeVector;
    }

    public void setRangeVector(String rangeVector) {
        this.rangeVector = rangeVector;
    }

    public boolean checkParam() {
        return !StringUtils.isBlank(this.rangeVector);
    }

    public String build(String content) throws UnsupportedEncodingException {
        String queryStr = "irate(${content}[${rangeVector}])".replace("${content}", content).replace("${rangeVector}", this.rangeVector);
        return this.dealLabelFilter(queryStr);
    }

    public String toString() {
        return "IRateFunc{functionName='" + this.functionName + '\'' + ", byLabel=" + this.byLabel + ", withoutLabel=" + this.withoutLabel + '\'' + ", rangeVector='" + this.rangeVector + '\'' + '}';
    }
}
