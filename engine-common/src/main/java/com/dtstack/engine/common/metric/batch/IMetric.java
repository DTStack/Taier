package com.dtstack.engine.common.metric.batch;

import java.text.DecimalFormat;

/**
 * @author toutian
 */
public interface IMetric {

    Object getMetric();

    String getChartName();

    DecimalFormat df = new DecimalFormat("#.##");
}
