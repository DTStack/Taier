package com.dtstack.engine.master.send;

import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.UrlUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class HttpSendClient {

    public static void masterTriggerNode(String target) {
        PoolHttpClient.post(UrlUtil.getHttpUrl(target,Urls.MASTER_TRIGGER_NODE), null);
    }

    /**
     * 返回数据格式{"send":true}
     * TODO 是否需要做两阶段提交---确保数据已经提交了。只是返回的时候网络异常
     *
     * @param address
     * @param paramAction
     * @return
     * @throws IOException
     */
    public static boolean actionSubmit(String address, ParamAction paramAction) throws IOException {
        String dataJson = PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.SUBMIT), PublicUtil.ObjectToMap(paramAction));
        if (dataJson == null) {
            return false;
        }

        Map<String, Object> resultMap = PublicUtil.jsonStrToObject(dataJson, Map.class);
        if (!resultMap.containsKey("data")) {
            return false;
        }

        Map<String, Object> sendData = (Map<String, Object>) resultMap.get("data");

        if (!sendData.containsKey("send")) {
            return false;
        }

        return MathUtil.getBoolean(sendData.get("send"));
    }

    public static Boolean actionStopJobToWorker(String address, ParamAction paramMap) throws IOException {
        String dataJson = PoolHttpClient.post(UrlUtil.getHttpUrl(address, Urls.WORK_SEND_STOP), PublicUtil.ObjectToMap(paramMap));
        if (dataJson == null) {
            return null;
        }

        Map<String, Object> resultMap = PublicUtil.jsonStrToObject(dataJson, Map.class);
        if (!resultMap.containsKey("data")) {
            return null;
        }
        return MathUtil.getBoolean(resultMap.get("data"), false);
    }
}
