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

package com.dtstack.engine.common.metric.prometheus.func;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * 计算 calculates the per-second instant rate of increase of the time series in the range vector.
 * This is based on the last two data points. Breaks in monotonicity (such as counter resets due to target restarts)
 * are automatically adjusted for
 * Date: 2018/10/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public class IRateFunc extends CommonFunc{

    private static final String NAME = "irate";

    private static final String TMP = "irate(${content}[${rangeVector}])";

    private String rangeVector;

    public IRateFunc(){
        super(NAME);
    }

    public String getRangeVector() {
        return rangeVector;
    }

    public void setRangeVector(String rangeVector) {
        this.rangeVector = rangeVector;
    }

    @Override
    public boolean checkParam() {
        return !StringUtils.isBlank(rangeVector);
    }

    @Override
    public String build(String content) throws UnsupportedEncodingException {
        String queryStr = TMP.replace("${content}", content).replace("${rangeVector}", rangeVector);
        return dealLabelFilter(queryStr);
    }

    @Override
    public String toString() {
        return "IRateFunc{" +
                "functionName='" + functionName + '\'' +
                ", byLabel=" + byLabel +
                ", withoutLabel=" + withoutLabel + '\'' +
                ", rangeVector='" + rangeVector + '\'' +
                '}';
    }
}
