package com.dtstack.taier.common.param;


import com.dtstack.taier.common.metric.MetricResult;

import java.util.List;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 16:38
 * @Description:
 */
public class StreamMetricData {
    private String resultType;

    private List<MetricResult> result;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<MetricResult> getResult() {
        return result;
    }

    public void setResult(List<MetricResult> result) {
        this.result = result;
    }
}
