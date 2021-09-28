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

package com.dtstack.engine.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public enum LearningFrameType {

    TensorFlow(0,"tensorflow"),MXNet(1,"mxnet");

    private int type;

    private String name;

    LearningFrameType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static LearningFrameType getByType(int type){
        for (LearningFrameType frameType : LearningFrameType.values()) {
            if(frameType.getType() == type){
                return frameType;
            }
        }

        return null;
    }

    public static LearningFrameType getByName(String name){
        for (LearningFrameType frameType : LearningFrameType.values()) {
            if(frameType.getName().equals(name)){
                return frameType;
            }
        }

        return TensorFlow;
    }
}
