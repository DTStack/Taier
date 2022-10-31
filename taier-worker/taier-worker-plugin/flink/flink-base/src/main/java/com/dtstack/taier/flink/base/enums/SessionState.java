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

package com.dtstack.taier.flink.base.enums;

/**
 * @description: session state (healthy、unhealthy)
 * @program: engine-all
 * @author: lany
 * @create: 2021/07/10 14:24
 */
public enum SessionState {
    // session is ok
    HEALTHY(true),

    // session is not ok
    UNHEALTHY(false);

    private final boolean state;

    SessionState(boolean state){
        this.state = state;
    }

    public boolean getState(){
        return this.state;
    }
}
