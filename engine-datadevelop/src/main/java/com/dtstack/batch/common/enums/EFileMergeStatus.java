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

package com.dtstack.batch.common.enums;

/**
 * <p>小文件合并状态
 *
 * @author ：wangchuan
 * date：Created in 2:24 下午 2020/12/14
 * company: www.dtstack.com
 */
public enum EFileMergeStatus {

    /**
     * 未开始 - 等待开始的状态
     */
    NOT_START(0, "未开始"),

    /**
     * 正在合并
     */
    MERGING(1, "正在合并"),

    /**
     * 合并成功
     */
    SUCCESS(2, "合并成功"),

    /**
     * 合并失败
     */
    FAIL(3, "合并失败"),

    /**
     * 合并取消
     */
    CANCEL(4, "合并取消"),

    /**
     * 合并取消中
     */
    CANCELING(5, "正在停止"),

    /**
     * 等待资源
     */
    WAIT_RESOURCE(8, "等待资源");

    /**
     * 对应的key
     */
    private final Integer val;

    /**
     * 描述
     */
    private String desc;

    public Integer getVal() {
        return val;
    }

    EFileMergeStatus(Integer val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
