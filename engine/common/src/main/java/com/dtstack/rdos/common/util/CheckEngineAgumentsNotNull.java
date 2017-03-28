package com.dtstack.rdos.common.util;

import com.dtstack.rdos.commom.exception.EngineAgumentsException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/28.
 */
public class CheckEngineAgumentsNotNull {

    public static void checkEngineAguments(Map<String,Object> nodeConfig) throws EngineAgumentsException{
        String localAddress = (String)nodeConfig.get("localAddress");
        if(StringUtils.isBlank(localAddress)){
            throw new EngineAgumentsException("localAddress");
        }
        String nodeZkAddress = (String)nodeConfig.get("nodeZkAddress");
        if(StringUtils.isBlank(nodeZkAddress)){
            throw new EngineAgumentsException("nodeZkAddress");
        }
        String clientType = (String)nodeConfig.get("clientType");
        if(StringUtils.isBlank(clientType)){
            throw new EngineAgumentsException("clientType");
        }
    }
}
