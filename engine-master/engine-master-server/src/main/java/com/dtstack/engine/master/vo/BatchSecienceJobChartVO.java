package com.dtstack.engine.master.vo;

import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.ChartMetaDataVO;
import org.apache.commons.collections.MapUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class BatchSecienceJobChartVO extends ChartDataVO {

    private String totalTitle = "总实例数";
    private String successTitle = "成功实例数";
    private String failTitle = "失败实例数";
    private String prepareRunTitle = "待运行实例数";
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd");

    public BatchSecienceJobChartVO format(List<Map<String, Object>> totalResult, List<Map<String, Object>> successResult,
                                          List<Map<String, Object>> failResult, List<Map<String, Object>> deployResult) {
        List<Object> typeData = Arrays.asList(totalTitle, successTitle, failTitle, prepareRunTitle);
        this.type = new ChartMetaDataVO("type", typeData);
        this.x = new ChartMetaDataVO("day", getXValue());
        List<Object> totalValues = formatValue(totalResult);
        List<Object> successValues = formatValue(successResult);
        List<Object> failValues = formatValue(failResult);
        List<Object> deployValues = formatValue(deployResult);
        this.y = new ArrayList<>();
        this.y.add(new ChartMetaDataVO(totalTitle, totalValues));
        this.y.add(new ChartMetaDataVO(successTitle, successValues));
        this.y.add(new ChartMetaDataVO(failTitle, failValues));
        this.y.add(new ChartMetaDataVO(prepareRunTitle, deployValues));
        return this;
    }

    private List<Object> formatValue(List<Map<String, Object>> metadata) {
        Map<String, Long> dataMap = new HashMap<>();
        List<Object> dataList = new ArrayList<>();

        for (Map<String, Object> data : metadata) {
            if (dataMap.get("day") != null) {

                dataMap.put(MapUtils.getString(data, "day"), dataMap.get(MapUtils.getString(data, "day")) + MapUtils.getLong(data,"day"));
            }
            dataMap.put(MapUtils.getString(data, "day"), MapUtils.getLong(data,"cnt"));
        }
        List<Object> xValue = getXValue();
        for (Object x : xValue) {
            if (dataMap.containsKey(x)) {
                dataList.add(Optional.ofNullable(dataMap.get(x)).orElse(0L));
            } else {
                dataList.add(0L);
            }
        }
        return dataList;
    }

    private List<Object> getXValue() {
        List<Object> xValue = new ArrayList<>();
        LocalDateTime start = LocalDateTime.now().minusDays(29);
        for (int i = 0; i < 30; i++) {
            xValue.add(start.plusDays(i).format(DF));
        }
        return xValue;
    }

}
