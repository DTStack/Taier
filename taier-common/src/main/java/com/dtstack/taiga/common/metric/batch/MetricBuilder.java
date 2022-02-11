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

package com.dtstack.taiga.common.metric.batch;


import com.dtstack.taiga.common.metric.prometheus.PrometheusMetricQuery;

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
