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

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DingAt {

    private List<String> atMobiles;

    private Boolean isAtAll;

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    public Boolean getAtAll() {
        return isAtAll;
    }

    public void setAtAll(Boolean atAll) {
        isAtAll = atAll;
    }
}
