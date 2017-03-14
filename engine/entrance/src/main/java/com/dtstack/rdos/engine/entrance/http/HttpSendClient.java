package com.dtstack.rdos.engine.entrance.http;

import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.common.util.UrlUtil;
import com.dtstack.rdos.engine.entrance.service.paramObject.ParamAction;
import java.util.HashMap;
import java.util.Map;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class HttpSendClient {

    private static ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    public static void actionStart(String address,ParamAction paramAction) throws Exception{
        PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.START), PublicUtil.ObjectToMap(paramAction));

    }

    public static void migration(final String node)throws Exception{
        Map<String,Object> params = new HashMap<String,Object>(){
            {
                put("node",node);
            }
        };
        PoolHttpClient.post(UrlUtil.getHttpUrl(zkDistributed.isHaveMaster(), Urls.MIGRATE),params);
    }
}
