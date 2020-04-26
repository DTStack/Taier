package com.dtstack.schedule.common.metric;

import java.util.List;

/**
 * Reason:
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MetricData<T, V> {

    private String tagName;

    private List<Tuple<T, V>> dps;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<Tuple<T, V>> getDps() {
        return dps;
    }

    public void setDps(List<Tuple<T, V>> dps) {
        this.dps = dps;
    }
}
