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
import com.google.common.base.Charsets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Reason:
 * Date: 2018/10/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public class CommonFunc implements IFunction {

    private static final String BY_LABEL_TPL = " by (${label})";

    private static final String WITHOUT_LABEL_TPL = " without (${label})";

    protected String functionName;

    /**byLabel 和 withoutLabel 不能同时设置*/
    protected List<String> byLabel;

    protected List<String> withoutLabel;

    public CommonFunc(String functionName){
        this.functionName = functionName;
    }

    @Override
    public boolean checkParam() {
        return !StringUtils.isBlank(functionName);
    }

    @Override
    public String build(String content) throws UnsupportedEncodingException {
        String queryStr = functionName + "(" + content + ")";
        return dealLabelFilter(queryStr);
    }

    public String dealLabelFilter(String queryStr) throws UnsupportedEncodingException {

        if(CollectionUtils.isEmpty(byLabel) && CollectionUtils.isEmpty(withoutLabel)){
            return queryStr;
        }

        //by 和 without 必须配合函数一起使用
        if(CollectionUtils.isNotEmpty(byLabel) && CollectionUtils.isNotEmpty(withoutLabel)){
            throw new RuntimeException("'by label' and 'without label' can't set at the same time");
        }

        if(CollectionUtils.isNotEmpty(byLabel)){
            String labels = String.join(",", byLabel);
            String byLabels = URLEncoder.encode(BY_LABEL_TPL.replace("${label}", labels), Charsets.UTF_8.name());
            queryStr += byLabels;
        }

        if(CollectionUtils.isNotEmpty(withoutLabel)){
            String labels = String.join(",", withoutLabel);
            String withoutLabels = URLEncoder.encode(WITHOUT_LABEL_TPL.replace("${label}", labels), Charsets.UTF_8.name());
            queryStr += withoutLabels;
        }

        return queryStr;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<String> getByLabel() {
        return byLabel;
    }

    public void setByLabel(List<String> byLabel) {
        this.byLabel = byLabel;
    }

    public List<String> getWithoutLabel() {
        return withoutLabel;
    }

    public void setWithoutLabel(List<String> withoutLabel) {
        this.withoutLabel = withoutLabel;
    }

    @Override
    public String toString() {
        return "CommonFunc{" +
                "functionName='" + functionName + '\'' +
                ", byLabel=" + byLabel +
                ", withoutLabel=" + withoutLabel +
                '}';
    }
}
