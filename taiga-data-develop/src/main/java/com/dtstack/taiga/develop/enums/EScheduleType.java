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

package com.dtstack.taiga.develop.enums;

/**
 * Reason:
 * Date: 2017/6/2
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EScheduleType {

    //正常调度(0), 补数据(1)
    NORMAL_SCHEDULE(0), FILL_DATA(1);

    private int type;

    EScheduleType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String getTypeName(int type){
        if(type == 0){
            return "正常调度";
        }else if(type == 1){
            return "补数据";
        }else{
            return "未知调度类型";
        }
    }
}
