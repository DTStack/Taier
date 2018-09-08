package com.dtstack.rdos.engine.service.send;

import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.common.util.UrlUtil;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class HttpSendClient {

    public static void actionStart(String address, ParamAction paramAction) throws Exception{
        PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.START), PublicUtil.ObjectToMap(paramAction));
    }

    /**
     * 返回数据格式{"send":true}
     * TODO 是否需要做两阶段提交---确保数据已经提交了。只是返回的时候网络异常
     * @param address
     * @param paramAction
     * @return
     * @throws IOException
     */
    public static boolean actionSubmit(String address, ParamAction paramAction) throws IOException {
        String dataJson = PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.SUBMIT), PublicUtil.ObjectToMap(paramAction));
        if(dataJson == null){
            return false;
        }

        Map<String, Object> resultMap = PublicUtil.jsonStrToObject(dataJson, Map.class);
        if(!resultMap.containsKey("data")){
            return false;
        }

        Map<String, Object> sendData = (Map<String, Object>) resultMap.get("data");

        if(!sendData.containsKey("send")){
            return false;
        }

        return MathUtil.getBoolean(sendData.get("send"));
    }

    public static void actionStopJob(String address, Map<String, Object> params) throws IOException {
        PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.STOP), params);
    }

    public static void actionStopJobToWorker(String address, ParamAction paramMap) throws IOException {
        PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.MASTER_SEND_STOP), PublicUtil.ObjectToMap(paramMap));
    }

    public static void migrationShard(String target,Map<String, Object> params) {
        PoolHttpClient.post(UrlUtil.getHttpUrl(target,Urls.MIGRATE_SHARD),params);
    }
}
