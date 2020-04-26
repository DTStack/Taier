package com.dtstack.schedule.common.metric.batch;


import com.dtstack.schedule.common.metric.prometheus.PrometheusMetricQuery;

/**
 * @author toutian
 */
public class MetricBuilder {

    public static IMetric buildMetric(String metricName, String jobId, long startTime, long endTime, PrometheusMetricQuery prometheusMetricQuery) {
        BaseMetric metric = null;
        switch (metricName) {
            case "conversionErrors":
            	metric = new SyncJobMetricWithSum();
            	break;
            case "duplicateErrors":
            	metric = new SyncJobMetricWithSum();
            	break;
            case "nErrors":
            	metric = new SyncJobMetricWithCountMaxSum();
            	break;
            case "nullErrors":
            	metric = new SyncJobMetricWithSum();
            	break;
            case "numRead":
            	metric = new SyncJobMetricWithCountMaxSum();
            	break;
            case "numWrite":
            	metric = new SyncJobMetricWithCountMaxSum();
            	break;
            case "otherErrors":
            	metric = new SyncJobMetricWithSum();
            	break;
            case "byteRead":
            	metric = new SyncJobMetricWithCountMaxSum();
            	break;
            case "byteWrite":
            	 metric = new SyncJobMetricWithCountMaxSum();
            	 break;
            case "readDuration":
            	metric = new SyncJobMetricWithCountMaxSum();
            	break; 
            case "writeDuration":
                metric = new SyncJobMetricWithCountMaxSum();
                break;
            case "endLocation":
                metric = new SyncJobMetricWithCountMaxSum();
                break;
            case "startLocation":
                metric = new SyncJobMetricWithCountMaxSum();
                break;
            default:
                break;
        }

        if (metric != null) {
            metric.setStartTime(startTime);
            metric.setEndTime(endTime);
            metric.setGranularity("3s");
            metric.setMetricName(metricName);
            metric.setJobId(jobId);
            metric.setPrometheusMetricQuery(prometheusMetricQuery);
        }

        return metric;
    }
}
