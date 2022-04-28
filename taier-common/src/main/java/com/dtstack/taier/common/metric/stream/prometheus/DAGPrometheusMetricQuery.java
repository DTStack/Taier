package com.dtstack.taier.common.metric.stream.prometheus;

import com.dtstack.taier.common.http.PoolHttpClient;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.HttpQueryParamBuilder;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 14:51
 * @Description:
 */
public class DAGPrometheusMetricQuery {

    private static Logger logger = LoggerFactory.getLogger(DAGPrometheusMetricQuery.class);

    private PrometheusMetricQuery prometheusMetricQuery;

    private String dagQuery;

    public DAGPrometheusMetricQuery(PrometheusMetricQuery prometheusMetricQuery) {
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    public String queryResult(String metricName, Long time, QueryInfo queryInfo, String tagName) {
        String urlParam;
        try {
            urlParam = HttpQueryParamBuilder.builder(metricName, time, queryInfo);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("", e);
        }
        if(StringUtils.isEmpty(dagQuery)){
            Class clazz = prometheusMetricQuery.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                if("query".equals(field.getName())){
                    field.setAccessible(true);
                    try {
                        dagQuery = String.valueOf(field.get(this.prometheusMetricQuery));
                        field.setAccessible(false);
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage(),"获取url失败");
                    }
                    break;
                }
            }
        }
        String reqUrl = dagQuery+ urlParam;
        String result = PoolHttpClient.get(reqUrl, null);
        if (result == null) {
            return null;
        }
        return result;
    }
}
