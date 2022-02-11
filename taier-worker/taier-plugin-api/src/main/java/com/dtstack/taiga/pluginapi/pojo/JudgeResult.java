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

public class JudgeResult implements Serializable {

    private JudgeType result;
    private String reason;

    public static JudgeResult ok() {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.OK);
        return judgeResult;
    }

    public static JudgeResult notOk(String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.NOT_OK);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public static JudgeResult exception(String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.EXCEPTION);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public static JudgeResult limitError(String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.LIMIT_ERROR);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public boolean available() {
        return result != null && result == JudgeType.OK;
    }

    public JudgeType getResult() {
        return result;
    }

    public void setResult(JudgeType result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public enum JudgeType {
        OK,
        NOT_OK,
        LIMIT_ERROR,
        EXCEPTION
    }
}
