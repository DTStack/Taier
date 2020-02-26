package com.dtstack.engine.master.send;

import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.UrlUtil;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class HttpSendClient {

    public static void masterTriggerNode(String target) {
        PoolHttpClient.post(UrlUtil.getHttpUrl(target,Urls.MASTER_TRIGGER_NODE), null);
    }
}
