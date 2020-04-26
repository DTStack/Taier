package com.dtstack.schedule.common.metric.prometheus;



import com.dtstack.schedule.common.metric.QueryInfo;

import java.io.UnsupportedEncodingException;

/**
 * 根据参数构建 prometheus query 查询的url
 * Date: 2018/10/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class HttpQueryParamBuilder extends AbsHttpQueryParamBuilder{

    private static final String QUERY_RANGE_TPL = "query=${query}&time=${time}";


    public static String builder(String metricName, Long time, QueryInfo queryInfo) throws UnsupportedEncodingException {

        String reqParam = "";
        if(time != null && time > 0){
            long timeSec = time/1000;
            reqParam = QUERY_RANGE_TPL.replace("${time}",  timeSec + "");
        }else{
            reqParam = QUERY_RANGE_TPL.replace("&time=${time}", "");
        }

        String queryParam = buildQuery(metricName, queryInfo);

        return reqParam.replace("${query}", queryParam);
    }


}
