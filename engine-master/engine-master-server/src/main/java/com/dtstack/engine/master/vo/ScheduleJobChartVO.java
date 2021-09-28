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

package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class ScheduleJobChartVO extends ChartDataVO{
    public ScheduleJobChartVO() {
    }

    public ScheduleJobChartVO(List<Object> todayData, List<Object> yestdayData, List<Object> avgData){
        List<Object> typeData = Arrays.asList((Object)"今天","昨天","历史平均");
        this.type = new ChartMetaDataVO("type",typeData);
        List<Object> hourData = new ArrayList<>();
        for(int i = 0;i<24;i++){
            if(i<10){
                hourData.add("0"+i);
            }else{
                hourData.add(""+i);
            }
        }
        this.x = new ChartMetaDataVO("hours",hourData);
        this.y = new ArrayList<>();
        this.y.add(new ChartMetaDataVO("今天",todayData));
        this.y.add(new ChartMetaDataVO("昨天",yestdayData));
        this.y.add(new ChartMetaDataVO("历史平均",avgData));
    }
}
