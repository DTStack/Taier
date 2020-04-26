package com.dtstack.schedule.common.metric.prometheus;


import com.dtstack.schedule.common.metric.QueryInfo;

import java.io.UnsupportedEncodingException;

/**
 * 根据参数构建 prometheus rangeQuery 查询的url
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class HttpQueryRangeParamBuilder extends AbsHttpQueryParamBuilder {

    private static final String QUERY_RANGE_TPL = "query=${query}&start=${start}&end=${end}&step=${step}";

    /**
     * queryRange=flink_taskmanager_job_task_operator_KafkaConsumer_current_offsets_nbTest1_0{job_name='job10'}&start=2018-09-29T20:10:30.781Z&end=2018-09-30T20:11:00.781Z&step=30s
     */
    public static String builder(String metricName, long startTime, long endTime, QueryInfo queryInfo) throws UnsupportedEncodingException {

        long startSec = startTime/1000;
        endTime = endTime < System.currentTimeMillis() ? endTime : System.currentTimeMillis();
        long endSec = endTime/1000;
        String reqParam = QUERY_RANGE_TPL.replace("${start}", startSec + "").replace("${end}", endSec + "").replace("${step}", queryInfo.getGranularity());
        String queryParam = buildQuery(metricName, queryInfo);

        return reqParam.replace("${query}", queryParam);
    }
}
