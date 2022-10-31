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

package com.dtstack.taier.common.lang.web;

import com.dtstack.taier.common.exception.ErrorCode;

public class R<T> {
    private static String DATA_VERSION;

    /**
     * 状态码
     */
    public int code;

    /**
     * 异常信息
     */
    public String message;

    /**
     * 返回值
     */
    private T data;

    /**
     * 请求时间
     */
    private long space;

    public final static <T> R<T> ok(T data) {
        return new R()
                .code(ErrorCode.SUCCESS.getCode())
                .data(data);
    }

    public final static R<Void> empty() {
        return new R()
                .code(ErrorCode.SUCCESS.getCode());
    }

    public final static <T> R<T> fail(int code, String message) {
        return new R()
                .code(code)
                .message(message);
    }

    public final static <T> R<T> fail(ErrorCode errorCode) {
        return new R()
                .code(errorCode.getCode())
                .message(errorCode.getMsg());
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> space(long space) {
        this.space = space;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    /**
     * 是否成功标志位,便于前端进行返回判断
     * @return
     */
    public boolean isSuccess() {
        return getCode() == ErrorCode.SUCCESS.getCode();
    }

    public String getVersion() {
        return R.DATA_VERSION;
    }



    /**
     * 设置接口版本
     * @param version
     */
    public static void setVersion(String version) {
        R.DATA_VERSION = version;
    }
}
