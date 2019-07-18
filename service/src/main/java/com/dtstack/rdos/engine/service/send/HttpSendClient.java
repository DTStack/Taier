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

    public static boolean actionStopJobToWorker(String address, ParamAction paramMap) throws IOException {
        String dataJson = PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.WORK_SEND_STOP), PublicUtil.ObjectToMap(paramMap));
        if(dataJson == null){
            return false;
        }

        Map<String, Object> resultMap = PublicUtil.jsonStrToObject(dataJson, Map.class);
        if(!resultMap.containsKey("data")){
            return false;
        }
        Map<String, Object> result = (Map<String, Object>) resultMap.get("data");
        return MathUtil.getBoolean(result.get("send"), false);
    }

    public static void masterSendJobs(String target,Map<String, Object> params) {
        PoolHttpClient.post(UrlUtil.getHttpUrl(target,Urls.MASTER_SEND_JOBS),params);
    }
}
