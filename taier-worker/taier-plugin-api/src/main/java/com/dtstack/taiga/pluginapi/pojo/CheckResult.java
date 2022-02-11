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

package com.dtstack.taiga.pluginapi.pojo;

import java.io.Serializable;

/**
 * @author haier
 * @Description 语法检测结果
 * @date 2021/3/9 11:33 上午
 */
public class CheckResult implements Serializable {
    private boolean result;
    private String errorMsg;

    public static CheckResult success() {
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(true);
        return checkResult;
    }

    public static CheckResult exception(String msg) {
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(false);
        checkResult.setErrorMsg(msg);
        return checkResult;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
