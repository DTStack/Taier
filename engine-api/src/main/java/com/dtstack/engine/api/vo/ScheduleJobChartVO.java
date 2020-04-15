package com.dtstack.engine.api.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
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
