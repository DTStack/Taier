package com.dtstack.engine.master.send;

import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.UrlUtil;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class HttpSendClient {

    public static void masterSendJobs(String target,Map<String, Object> params) {
        PoolHttpClient.post(UrlUtil.getHttpUrl(target,Urls.MASTER_SEND_JOBS),params);
    }
}
