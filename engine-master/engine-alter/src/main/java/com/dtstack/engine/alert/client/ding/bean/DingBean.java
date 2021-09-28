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

package com.dtstack.engine.alert.client.ding.bean;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 10:33 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DingBean {

    private String msgtype;

    private DingText text;

    private DingMarkdown dingMarkdown;

    private DingAt at;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public DingText getText() {
        return text;
    }

    public void setText(DingText text) {
        this.text = text;
    }

    public DingMarkdown getDingMarkdown() {
        return dingMarkdown;
    }

    public void setDingMarkdown(DingMarkdown dingMarkdown) {
        this.dingMarkdown = dingMarkdown;
    }

    public DingAt getAt() {
        return at;
    }

    public void setAt(DingAt at) {
        this.at = at;
    }
}
