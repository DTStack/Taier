package com.dtstack.taier.common.metric.prometheus.func;

import com.dtstack.taier.common.metric.IFunction;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author zhiChen
 * @date 2022/4/27 20:07
 */
public class IRateFunc extends CommonFunc {
    private static final String NAME = "irate";
    private static final String TMP = "irate(${content}[${rangeVector}])";
    private String rangeVector;

    public IRateFunc() {
        super("irate");
    }

    public String getRangeVector() {
        return this.rangeVector;
    }

    public void setRangeVector(String rangeVector) {
        this.rangeVector = rangeVector;
    }

    public boolean checkParam() {
        return !StringUtils.isBlank(this.rangeVector);
    }

    public String build(String content) throws UnsupportedEncodingException {
        String queryStr = "irate(${content}[${rangeVector}])".replace("${content}", content).replace("${rangeVector}", this.rangeVector);
        return this.dealLabelFilter(queryStr);
    }

    public String toString() {
        return "IRateFunc{functionName='" + this.functionName + '\'' + ", byLabel=" + this.byLabel + ", withoutLabel=" + this.withoutLabel + '\'' + ", rangeVector='" + this.rangeVector + '\'' + '}';
    }
}
